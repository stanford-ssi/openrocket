package net.sf.openrocket.startup;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import net.sf.openrocket.database.Databases;
import net.sf.openrocket.document.OpenRocketDocument;
import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.gui.simulation.SimulationWorker;
import net.sf.openrocket.gui.util.OpenFileWorker;
import net.sf.openrocket.gui.util.SwingPreferences;
import net.sf.openrocket.plugin.PluginModule;
import net.sf.openrocket.rocketcomponent.Configuration;
import net.sf.openrocket.rocketcomponent.IgnitionConfiguration;
import net.sf.openrocket.rocketcomponent.MotorMount;
import net.sf.openrocket.simulation.*;
import net.sf.openrocket.simulation.customexpression.CustomExpression;
import net.sf.openrocket.simulation.customexpression.CustomExpressionSimulationListener;
import net.sf.openrocket.simulation.exception.SimulationCancelledException;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.exception.SimulationLaunchException;
import net.sf.openrocket.simulation.listeners.AbstractSimulationListener;
import net.sf.openrocket.simulation.listeners.SimulationListener;
import net.sf.openrocket.util.ArrayList;
import net.sf.openrocket.util.MathUtil;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class HeadlessRockoon {

    private static boolean initialized;
    private static OpenRocketDocument document;

    private static final String sourceFile = "SpaceshotCurrent.ork";

    private static double simulationMaxAltitude;
    private static double simulationMaxVelocity;
    private static boolean simulationDone;
    private static SimulationStatus simulationStatus;

    /** Update the dialog status every this many ms */
    private static final long UPDATE_MS = 200;

    /** Flight progress at motor burnout */
    private static final double BURNOUT_PROGRESS = 0.4;

    /** Flight progress at apogee */
    private static final double APOGEE_PROGRESS = 0.7;

    public static void main(final String[] args) throws Exception {
        setupApplication();

        OpenRocketDocument document = getDocument();
        Simulation sim = createSimulation(document);
        new HeadlessRockoon().runSimulation(document, sim);

        exportSimulation(sim);
        System.out.println("Done");
    }

    public static String generateServerResponse() throws Exception {
        if (!initialized) {
            setupApplication();
            document = getDocument();
            initialized = true;
        }

        Simulation sim = createSimulation(document);
        new HeadlessRockoon().runSimulation(document, sim);

        return convertToJSON(sim);
    }

    private static void setupApplication() {
        GuiModule guiModule = new GuiModule();
        Module pluginModule = new PluginModule();
        Injector injector = Guice.createInjector(guiModule, pluginModule);
        Application.setInjector(injector);
        guiModule.startLoader();

        ((SwingPreferences) Application.getPreferences()).loadDefaultUnits();

        Databases.fakeMethod();
    }

    public static OpenRocketDocument getDocument() throws Exception {
        return open(sourceFile);
    }

    private static OpenRocketDocument open(String file) throws Exception {
        System.out.println("Opening " + file);
        OpenFileWorker worker = new OpenFileWorker(new File(file));
        worker.execute();

        return worker.get();
    }

    private static Simulation createSimulation(OpenRocketDocument document) {
        System.out.println("Creating simulation");
        Simulation sim = new Simulation(document.getRocket());
        sim.setName(document.getNextSimulationName());

        return sim;
    }

    private static void exportSimulation(Simulation simulation) {
        System.out.println("Exporting simulation");
        String result = convertToJSON(simulation);
        writeResult("result.json", result);
    }

    private static String convertToJSON(Simulation simulation) {
        FlightData data = simulation.getSimulatedData();
        FlightDataBranch branch = data.getBranch(0);

        FlightDataType[] types = branch.getTypes();

        HashSet<Integer> allowedFields = new HashSet<>();
        allowedFields.add(0);

        ArrayList<String> fields = new ArrayList<>();

        String initialTabs = "\t\t";
        for (int i = 0; i < types.length; i++) {
            if (!allowedFields.contains(i)) {
                continue;
            }

            String field = "" +
                    initialTabs + "{\n" +
                    initialTabs + "\t\"name\": \"" + types[i].getName() + "\",\n" +
                    initialTabs + "\t\"unit\": \"" + types[i].getUnitGroup().getDefaultUnit() + "\"\n" +
                    initialTabs + "}";

            fields.add(field);
        }

        ArrayList<String> values = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            if (!allowedFields.contains(i)) {
                continue;
            }

            List<Double> numericalSeries = branch.get(types[i]);
            String[] stringSeries = new String[numericalSeries.size()];
            for (int j = 0; j < stringSeries.length; j++) {
                double value = numericalSeries.get(j);
                if (Double.isNaN(value)) {
                    stringSeries[j] = "null";
                    continue;
                }

                stringSeries[j] = numericalSeries.get(j).toString();
            }

            String value = "" +
                    initialTabs + "[" +
                    String.join(", ", stringSeries) +
                    "]";

            values.add(value);
        }


        return "" +
                "{\n" +
                "\t\"fields\": [\n" +
                    String.join(",\n", fields) + "\n" +
                "\t],\n" +
                "\t\"values\": [\n" +
                    String.join(",\n", values) + "\n" +
                "\t]\n" +
                "}";
    }

    private static void writeResult(String destination, String data) {
        File file = new File(destination);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void runSimulation(OpenRocketDocument doc, Simulation sim) {
        System.out.println("Running simulation");
        sim.getOptions().useISA = true;
        InteractiveSimulationWorker worker = new InteractiveSimulationWorker(doc, sim);
        worker.execute();

        try {
            worker.get();
        } catch (Exception e) { }
    }

    /**
     * A SwingWorker that performs a flight simulation.  It periodically updates the
     * simulation statuses of the parent class and calls updateProgress().
     * The progress of the simulation is stored in the progress property of the
     * SwingWorker.
     *
     * @author Sampo Niskanen <sampo.niskanen@iki.fi>
     */
    private class InteractiveSimulationWorker extends SimulationWorker {

        private double burnoutTimeEstimate;
        private volatile double burnoutVelocity;
        private volatile double apogeeAltitude;

        private CustomExpressionSimulationListener exprListener;

        /*
         * -2 = time from 0 ... burnoutTimeEstimate
         * -1 = velocity from v(burnoutTimeEstimate) ... 0
         *  0 ... n = stages from alt(max) ... 0
         */
        private volatile int simulationStage = -2;

        private int progress = 0;


        public InteractiveSimulationWorker(OpenRocketDocument doc, Simulation sim) {
            super(sim);
            List<CustomExpression> exprs = doc.getCustomExpressions();
            exprListener = new CustomExpressionSimulationListener(exprs);

            // Calculate estimate of motor burn time
            double launchBurn = 0;
            double otherBurn = 0;
            Configuration config = simulation.getConfiguration();
            String id = simulation.getOptions().getMotorConfigurationID();
            Iterator<MotorMount> iterator = config.motorIterator();
            while (iterator.hasNext()) {
                MotorMount m = iterator.next();
                if (m.getIgnitionConfiguration().getDefault().getIgnitionEvent() == IgnitionConfiguration.IgnitionEvent.LAUNCH)
                    launchBurn = MathUtil.max(launchBurn, m.getMotor(id).getBurnTimeEstimate());
                else
                    otherBurn = otherBurn + m.getMotor(id).getBurnTimeEstimate();
            }
            burnoutTimeEstimate = Math.max(launchBurn + otherBurn, 0.1);
        }


        /**
         * Return the extra listeners to use, a progress listener and cancel listener.
         */
        @Override
        protected SimulationListener[] getExtraListeners() {
            return new SimulationListener[] { new SimulationProgressListener(), exprListener };
        }


        /**
         * Processes simulation statuses published by the simulation listener.
         * The statuses of the parent class and the progress property are updated.
         */
        @Override
        protected void process(List<SimulationStatus> chunks) {
//
            // Update max. altitude and velocity
            for (SimulationStatus s : chunks) {
                simulationMaxAltitude = Math.max(simulationMaxAltitude,
                        s.getRocketPosition().z);
                simulationMaxVelocity = Math.max(simulationMaxVelocity,
                        s.getRocketVelocity().length());
            }

            // Calculate the progress
            SimulationStatus status = chunks.get(chunks.size() - 1);
            simulationStatus = status;

            // 1. time = 0 ... burnoutTimeEstimate
            if (simulationStage == -2 && status.getSimulationTime() < burnoutTimeEstimate) {
                System.out.println("Method 1:  t=" + status.getSimulationTime() + "  est=" + burnoutTimeEstimate);
                setSimulationProgress(MathUtil.map(status.getSimulationTime(), 0, burnoutTimeEstimate,
                        0.0, BURNOUT_PROGRESS));
                return;
            }

            if (simulationStage == -2) {
                simulationStage++;
                burnoutVelocity = MathUtil.max(status.getRocketVelocity().z, 0.1);
                System.out.println("CHANGING to Method 2, vel=" + burnoutVelocity);
            }

            // 2. z-velocity from burnout velocity to zero
            if (simulationStage == -1 && status.getRocketVelocity().z >= 0) {
                System.out.println("Method 2:  vel=" + status.getRocketVelocity().z + " burnout=" + burnoutVelocity);
                setSimulationProgress(MathUtil.map(status.getRocketVelocity().z, burnoutVelocity, 0,
                        BURNOUT_PROGRESS, APOGEE_PROGRESS));
                return;
            }

            if (simulationStage == -1 && status.getRocketVelocity().z < 0) {
                simulationStage++;
                apogeeAltitude = MathUtil.max(status.getRocketPosition().z, 1);
                System.out.println("CHANGING to Method 3, apogee=" + apogeeAltitude);
            }

            // 3. z-position from apogee to zero
            // TODO: MEDIUM: several stages
//            System.out.println("Method 3:  alt=" + status.getRocketPosition().z + "  apogee=" + apogeeAltitude);
            setSimulationProgress(MathUtil.map(status.getRocketPosition().z,
                    apogeeAltitude, 0, APOGEE_PROGRESS, 1.0));
        }

        /**
         * Marks this simulation as done and calls the progress update.
         */
        @Override
        protected void simulationDone() {
            simulationDone = true;
//            System.out.println("Simulation done");
            setSimulationProgress(1.0);
        }


        /**
         * Marks the simulation as done and shows a dialog presenting
         * the error, unless the simulation was cancelled.
         */
        @Override
        protected void simulationInterrupted(Throwable t) {
            if (t instanceof SimulationCancelledException) {
                System.out.println("Simulation canceled");
                return;
            }

            if (t instanceof SimulationLaunchException) {
                System.out.println("Simulation launch exception: " + t.getMessage());
                return;
            }

            if (t instanceof SimulationException) {
                System.out.println("Simulation exception: " + t.getMessage());
                return;
            }

            System.out.println("Unknown exception");
            System.out.println(t);
            t.printStackTrace();
        }


        private void setSimulationProgress(double p) {
            int exact = Math.max(progress, (int) (100 * p + 0.5));
            progress = MathUtil.clamp(exact, 0, 100);
//            System.out.println("Setting progress to " + progress + " (real " + exact + ")");
            super.setProgress(progress);
        }


        /**
         * A simulation listener that regularly updates the progress property of the
         * SimulationWorker and publishes the simulation status for the run dialog to process.
         *
         * @author Sampo Niskanen <sampo.niskanen@iki.fi>
         */
        private class SimulationProgressListener extends AbstractSimulationListener {
            private long time = 0;

            @Override
            public boolean handleFlightEvent(SimulationStatus status, FlightEvent event) {
                switch (event.getType()) {
                    case APOGEE:
                        simulationStage = 0;
                        apogeeAltitude = status.getRocketPosition().z;
                        System.out.println("APOGEE, setting progress");
                        setSimulationProgress(APOGEE_PROGRESS);
                        publish(status);
                        break;

                    case LAUNCH:
                        publish(status);
                        break;

                    case SIMULATION_END:
                        System.out.println("END, setting progress");
                        setSimulationProgress(1.0);
                        break;
                }
                return true;
            }

            @Override
            public void postStep(SimulationStatus status) {
                if (System.currentTimeMillis() >= time + UPDATE_MS) {
                    time = System.currentTimeMillis();
                    publish(status);
                }
            }
        }
    }
}
