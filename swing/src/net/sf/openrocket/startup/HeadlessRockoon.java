package net.sf.openrocket.startup;

import net.sf.openrocket.document.OpenRocketDocument;
import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.gui.simulation.SimulationRunDialog;
import net.sf.openrocket.gui.util.OpenFileWorker;

import java.io.File;

public class HeadlessRockoon {

    private static final String sourceFile = "SpaceshotCurrent.ork";

    public static void main(final String[] args) throws Exception {
        OpenRocketDocument document = open(sourceFile);
        Simulation sim = createSimulation(document);


    }

    private static OpenRocketDocument open(String file) throws Exception {
        OpenFileWorker worker = new OpenFileWorker(new File(file));

        return worker.get();
    }

    private static Simulation createSimulation(OpenRocketDocument document) {
        Simulation sim = new Simulation(document.getRocket());
        sim.setName(document.getNextSimulationName());

        return sim;
    }

    private static void runSimulation(Simulation sim) {
        SimulationRunDialog.InteractiveSimulationWorker
    }
}
