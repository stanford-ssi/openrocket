package net.sf.openrocket.simulation.listeners.example;

import net.sf.openrocket.simulation.SimulationStatus;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.listeners.AbstractSimulationListener;

/**
 * The simulation listener that is attached to the simulation.
 * It is instantiated when the simulation run is started and the
 * methods are called at each step of the simulation.
 */
public class MultiLevelWindSimulationListener extends AbstractSimulationListener {
	
	private double multiplier;
	
	public MultiLevelWindSimulationListener(double multiplier) {
		super();
		this.multiplier = multiplier;
	}
	
	@Override
	public double postSimpleThrustCalculation(SimulationStatus status, double thrust) throws SimulationException {
		return thrust * multiplier;
	}
	
}
