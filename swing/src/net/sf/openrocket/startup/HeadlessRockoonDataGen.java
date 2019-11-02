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
import java.util.*;

public class HeadlessRockoonDataGen {

    private static boolean initialized;
    private static OpenRocketDocument document;

    private static final String sourceFile = "StabilityNew.ork";

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

    private static Map<String, Integer> fieldNames = new HashMap<String, Integer>() {{
        put("Time", 0);
        put("Altitude", 1);
        put("Vertical velocity", 2);
        put("Vertical acceleration", 3);
        put("Total velocity", 4);
        put("Total acceleration", 5);
        put("Position East of launch", 6);
        put("Position North of launch", 7);
        put("Lateral distance", 8);
        put("Lateral direction", 9);
        put("Lateral velocity", 10);
        put("Lateral acceleration", 11);
        put("Latitude", 12);
        put("Longitude", 13);
        put("Gravitational acceleration", 14);
        put("Angle of attack", 15);
        put("Roll rate", 16);
        put("Pitch rate", 17);
        put("Yaw rate", 18);
        put("Mass", 19);
        put("Propellant mass", 20);
        put("Longitudinal moment of inertia", 21);
        put("Rotational moment of inertia", 22);
        put("CP location", 23);
        put("CG location", 24);
        put("Stability margin calibers", 25);
        put("Mach number", 26);
        put("Reynolds number", 27);
        put("Thrust", 28);
        put("Drag force", 29);
        put("Drag coefficient", 30);
        put("Axial drag coefficient", 31);
        put("Friction drag coefficient", 32);
        put("Pressure drag coefficient", 33);
        put("Base drag coefficient", 34);
        put("Normal force coefficient", 35);
        put("Pitch moment coefficient", 36);
        put("Yaw moment coefficient", 37);
        put("Side force coefficient", 38);
        put("Roll moment coefficient", 39);
        put("Roll forcing coefficient", 40);
        put("Roll damping coefficient", 41);
        put("Pitch damping coefficient", 42);
        put("Coriolis acceleration", 43);
        put("Reference length", 44);
        put("Reference area", 45);
        put("Vertical orientation (zenith)", 46);
        put("Lateral orientation (azimuth)", 47);
        put("Wind velocity", 48);
        put("Air temperature", 49);
        put("Air pressure", 50);
        put("Speed of sound", 51);
        put("Simulation time step", 52);
        put("Computation time", 53);
    }};

    public static void main(final String[] args) throws Exception {
        setupApplication();

        OpenRocketDocument document = getDocument();
        Simulation sim = createSimulation(document);
        new HeadlessRockoon().runSimulation(document, sim);

        exportSimulation(sim);
        System.out.println("Done");
    }

    public static String generateServerResponse(int sampleEvery, double spinRate, double launchAltitude, double launchLatitude, double launchLongitude, double rodAngle, double launchRodLength, double windSpeed) throws Exception {
        if (!initialized) {
            setupApplication();
            document = getDocument();
            initialized = true;
        }

        Simulation sim = createSimulation(document);
        new HeadlessRockoon().runSimulation(document, sim, spinRate, launchAltitude, launchLatitude, launchLongitude,rodAngle,launchRodLength,windSpeed);

        System.out.println("sampleEvery="  + sampleEvery + " spinRate=" + spinRate + " launchAltitude=" + launchAltitude + " launchLatitude=" + launchLatitude + " launchLongitude=" + launchLongitude);

        return convertToJSON(sim, sampleEvery);
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
        return convertToJSON(simulation, 1);
    }

    private static String convertToJSON(Simulation simulation, int sampleEvery) {
        FlightData data = simulation.getSimulatedData();
        FlightDataBranch branch = data.getBranch(0);

        FlightDataType[] types = branch.getTypes();

        HashSet<Integer> allowedFields = new HashSet<>();

        allowedFields.add(fieldNames.get("Time"));
        allowedFields.add(fieldNames.get("Lateral orientation (azimuth)"));

        ArrayList<String> fields = new ArrayList<>();

        String initialTabs = "    ";
        for (int i = 0; i < types.length; i++) {
            if (!allowedFields.contains(i)) {
                continue;
            }

            String field = "" +
                    initialTabs + "{\n" +
                    initialTabs + "  \"name\": \"" + types[i].getName() + "\",\n" +
                    initialTabs + "  \"unit\": \"" + types[i].getUnitGroup().getDefaultUnit() + "\"\n" +
                    initialTabs + "}";

            fields.add(field);
        }

        ArrayList<String> values = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            if (!allowedFields.contains(i)) {
                continue;
            }

            List<Double> numericalSeries = branch.get(types[i]);
            ArrayList<String> stringSeries = new ArrayList<>();
            for (int j = 0; j < numericalSeries.size(); j += sampleEvery) {
                double value = numericalSeries.get(j);
                if (Double.isNaN(value)) {
                    stringSeries.add("null");
                    continue;
                }

                stringSeries.add(numericalSeries.get(j).toString());
            }

            String value = "" +
                    initialTabs + "[" +
                    String.join(", ", stringSeries) +
                    "]";

            values.add(value);
        }


        return "" +
                "{\n" +
                "  \"fields\": [\n" +
                    String.join(",\n", fields) + "\n" +
                "  ],\n" +
                "  \"values\": [\n" +
                    String.join(",\n", values) + "\n" +
                "  ]\n" +
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
        runSimulation(doc, sim, 0, 0, 36, -121,3,10,20);
    }

    private void runSimulation(OpenRocketDocument doc, Simulation sim, double spinRate, double launchAltitude, double launchLatitude, double launchLongitude, double rodAngle, double launchRodLength, double windSpeed) {
        System.out.println("Running simulation");

        sim.getOptions().useISA = true;
        sim.getOptions().setLaunchUseNOAA(false);
        sim.getOptions().setLaunchSpinRate(spinRate);
        sim.getOptions().setLaunchAltitude(launchAltitude);
        sim.getOptions().setLaunchLatitude(launchLatitude);
        sim.getOptions().setLaunchLongitude(launchLongitude);
        sim.getOptions().setLaunchRodAngle(rodAngle);
        sim.getOptions().setLaunchRodLength(launchRodLength);
        sim.getOptions().setWindSpeedAverage(windSpeed);

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
