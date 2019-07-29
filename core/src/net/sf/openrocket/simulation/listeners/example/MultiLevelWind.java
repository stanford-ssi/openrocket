package net.sf.openrocket.simulation.listeners.example;

import java.util.ArrayList;
import java.util.List;

import net.sf.openrocket.models.wind.PinkNoiseWindModel;
import net.sf.openrocket.simulation.SimulationConditions;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.extension.AbstractSimulationExtension;
import net.sf.openrocket.simulation.extension.SimulationExtension;
import net.sf.openrocket.unit.UnitGroup;

/**
 * The actual simulation extension.  A new instance is created for
 * each simulation it is attached to.
 * 
 * This class contains the configuration and is called before the
 * simulation is run.  It can do changes to the simulation, such
 * as add simulation listeners.
 * 
 * All configuration should be stored in the config variable, so that
 * file storage will work automatically.
 */
public class MultiLevelWind extends AbstractSimulationExtension {
	
	@Override
	public String getName() {
		String label = "Multi Level Wind";
		int lastUsedLevel = NUM_LEVELS - 1;
		for (; lastUsedLevel > 0; lastUsedLevel--) {
			if (getWindDirection(lastUsedLevel) != getWindDirection(lastUsedLevel - 1) ||
					getWind(lastUsedLevel) != getWind(lastUsedLevel - 1)) {
				break;
			}
		}
		for (int i = 0; i <= lastUsedLevel && i < 5; i++) {
			label = label + System.lineSeparator() + getWindLabel(i);
		}
		return label + "...";
	}
	
	private String getWindLabel(int idx) {
		return String.format("%d%d\u00B0%dᵐᵖʰ",
				Math.round(getWindAlt(idx) / 0.3048),
				Math.round(360.0 * getWindDirection(idx) / (Math.PI * 2)),
				Math.round(getWind(idx) / 0.44704));
		//new DegreeUnit().toString(getWindDirection(idx)));
	}
	
	@Override
	public String getDescription() {
		// This description is shown when the user clicks the info-button on the extension
		return "This extension allows setting of different winds at different altitudes.";
	}
	
	public static int NUM_LEVELS = 13;
	
	private double globalWindSpeed = 0.0;
	private double globalWindDirection = 0.0;
	
	@Override
	public void initialize(SimulationConditions conditions) throws SimulationException {
		//conditions.getSimulationListenerList().add(new MultiLevelWindSimulationListener(getMultiplier()));
		List<PinkNoiseWindModel.WindSpecs> windSpecs = new ArrayList<PinkNoiseWindModel.WindSpecs>();
		//windSpecs.add(new MultiLevelWindModel.WindSpecs(getWindZeroAlt(),  getWindZero(),  Math.PI / 2));
		//windSpecs.add(new MultiLevelWindModel.WindSpecs(200,  20,  Math.PI / 2));
		for (int i = 0; i < NUM_LEVELS; i++) {
			windSpecs.add(new PinkNoiseWindModel(getWindAlt(i), getWind(i), getWindDirection(i)));
		}
		
		PinkNoiseWindModel windModel = new PinkNoiseWindModel(windSpecs);
		windModel.setStandardDeviation(conditions.getSimulation().getOptions().getWindSpeedDeviation());
		windModel.setTurbulenceIntensity(conditions.getSimulation().getOptions().getWindTurbulenceIntensity());
		conditions.setWindModel(windModel);
	}
	
	@Override
	public SimulationExtension clone() {
		MultiLevelWind copy = (MultiLevelWind) super.clone();
		copy.setGlobalWindSpeed(this.globalWindSpeed);
		copy.setGlobalWindDirection(this.globalWindDirection);
		return copy;
	}
	
	public void setGlobalWindSpeed(double val) {
		globalWindSpeed = val;
	}
	
	public void setGlobalWindDirection(double val) {
		globalWindDirection = val;
	}
	
	private String getConfigKeyPrefix(int idx) {
		String label = "zero";
		if (idx == 1) {
			label = "one";
		} else if (idx == 2) {
			label = "two";
		} else if (idx == 3) {
			label = "three";
		} else if (idx == 4) {
			label = "four";
		} else if (idx == 5) {
			label = "five";
		} else if (idx == 6) {
			label = "six";
		} else if (idx == 7) {
			label = "seven";
		} else if (idx == 8) {
			label = "eight";
		} else if (idx == 9) {
			label = "nine";
		} else if (idx == 10) {
			label = "ten";
		} else if (idx == 11) {
			label = "eleven";
		} else if (idx == 12) {
			label = "twelve";
		}
		return "wind_" + label;
	}
	
	private double getWind(int idx) {
		return config.getDouble(getConfigKeyPrefix(idx), globalWindSpeed);
	}
	
	private void setWind(int idx, double value) {
		for (int i = idx + 1; i < NUM_LEVELS; i++) {
			if (getWindAlt(i) == getWindAlt(idx) || getWind(i) == getWind(idx)) {
				config.put(getConfigKeyPrefix(i), value);
			} else {
				break;
			}
		}
		config.put(getConfigKeyPrefix(idx), value);
		fireChangeEvent();
	}
	
	private double getWindAlt(int idx) {
		double defaultVal = 0.0;
		switch (idx) {
		case 0:
			defaultVal = 0.0;
			break;
		case 1:
			defaultVal = UnitGroup.UNITS_DISTANCE.fromString("1000ft");
			break;
		case 2:
			defaultVal = UnitGroup.UNITS_DISTANCE.fromString("2000ft");
			break;
		case 3:
			defaultVal = UnitGroup.UNITS_DISTANCE.fromString("2500ft");
			break;
		case 4:
			defaultVal = UnitGroup.UNITS_DISTANCE.fromString("3000ft");
			break;
		case 5:
			defaultVal = UnitGroup.UNITS_DISTANCE.fromString("5000ft");
			break;
		case 6:
			defaultVal = UnitGroup.UNITS_DISTANCE.fromString("6400ft");
			break;
		case 7:
			defaultVal = UnitGroup.UNITS_DISTANCE.fromString("9842ft");
			break;
		case 8:
			defaultVal = UnitGroup.UNITS_DISTANCE.fromString("4200m");
			break;
		case 9:
			defaultVal = UnitGroup.UNITS_DISTANCE.fromString("5500m");
			break;
		case 10:
			defaultVal = UnitGroup.UNITS_DISTANCE.fromString("7000m");
			break;
		case 11:
			defaultVal = UnitGroup.UNITS_DISTANCE.fromString("9000m");
			break;
		case 12:
			defaultVal = UnitGroup.UNITS_DISTANCE.fromString("10000m");
			break;
		}
		return config.getDouble(getConfigKeyPrefix(idx) + "_alt", defaultVal);
	}
	
	private void setWindAlt(int idx, double value) {
		config.put(getConfigKeyPrefix(idx) + "_alt", value);
		for (int i = 0; i < NUM_LEVELS; i++) {
			if (i < idx) {
				if (getWindAlt(i) > value) {
					config.put(getConfigKeyPrefix(i) + "_alt", value);
				}
			} else if (i > idx) {
				if (getWindAlt(i) < value) {
					config.put(getConfigKeyPrefix(i) + "_alt", value);
				}
			}
		}
		fireChangeEvent();
	}
	
	private double getWindDirection(int idx) {
		//return Math.PI / 2;
		return config.getDouble(getConfigKeyPrefix(idx) + "_direction", globalWindDirection);
	}
	
	private void setWindDirection(int idx, double value) {
		double currValue = getWindDirection(idx);
		for (int i = idx; i < NUM_LEVELS; i++) {
			if (getWindDirection(i) == currValue) {
				config.put(getConfigKeyPrefix(i) + "_direction", value);
			} else {
				break;
			}
		}
		fireChangeEvent();
	}
	
	public double getWindZero() {
		return getWind(0);
	}
	
	public void setWindZero(double wind_zero) {
		setWind(0, wind_zero);
	}
	
	public double getWindZeroAlt() {
		return getWindAlt(0);
	}
	
	public void setWindZeroAlt(double wind_zero_alt) {
		setWindAlt(0, wind_zero_alt);
	}
	
	public double getWindZeroDirection() {
		return getWindDirection(0);
	}
	
	public void setWindZeroDirection(double wind_zero_dir) {
		setWindDirection(0, wind_zero_dir);
	}
	
	public double getWindOne() {
		return getWind(1);
	}
	
	public void setWindOne(double wind_One) {
		setWind(1, wind_One);
	}
	
	public double getWindOneAlt() {
		return getWindAlt(1);
	}
	
	public void setWindOneAlt(double wind_One_alt) {
		setWindAlt(1, wind_One_alt);
	}
	
	public double getWindOneDirection() {
		return getWindDirection(1);
	}
	
	public void setWindOneDirection(double wind_dir) {
		setWindDirection(1, wind_dir);
	}
	
	public double getWindTwo() {
		return getWind(2);
	}
	
	public void setWindTwo(double wind_Two) {
		setWind(2, wind_Two);
	}
	
	public double getWindTwoAlt() {
		return getWindAlt(2);
	}
	
	public void setWindTwoAlt(double wind_Two_alt) {
		setWindAlt(2, wind_Two_alt);
	}
	
	public double getWindTwoDirection() {
		return getWindDirection(2);
	}
	
	public void setWindTwoDirection(double wind_dir) {
		setWindDirection(2, wind_dir);
	}
	
	public double getWindThree() {
		return getWind(3);
	}
	
	public void setWindThree(double wind_Three) {
		setWind(3, wind_Three);
	}
	
	public double getWindThreeAlt() {
		return getWindAlt(3);
	}
	
	public void setWindThreeAlt(double wind_Three_alt) {
		setWindAlt(3, wind_Three_alt);
	}
	
	public double getWindThreeDirection() {
		return getWindDirection(3);
	}
	
	public void setWindThreeDirection(double wind_dir) {
		setWindDirection(3, wind_dir);
	}
	
	public double getWindFour() {
		return getWind(4);
	}
	
	public void setWindFour(double wind_Four) {
		setWind(4, wind_Four);
	}
	
	public double getWindFourAlt() {
		return getWindAlt(4);
	}
	
	public void setWindFourAlt(double wind_Four_alt) {
		setWindAlt(4, wind_Four_alt);
	}
	
	public double getWindFourDirection() {
		return getWindDirection(4);
	}
	
	public void setWindFourDirection(double wind_dir) {
		setWindDirection(4, wind_dir);
	}
	
	public double getWindFive() {
		return getWind(5);
	}
	
	public void setWindFive(double wind_Five) {
		setWind(5, wind_Five);
	}
	
	public double getWindFiveAlt() {
		return getWindAlt(5);
	}
	
	public void setWindFiveAlt(double wind_Five_alt) {
		setWindAlt(5, wind_Five_alt);
	}
	
	public double getWindFiveDirection() {
		return getWindDirection(5);
	}
	
	public void setWindFiveDirection(double wind_dir) {
		setWindDirection(5, wind_dir);
	}
	
	public double getWindSix() {
		return getWind(6);
	}
	
	public void setWindSix(double wind_Six) {
		setWind(6, wind_Six);
	}
	
	public double getWindSixAlt() {
		return getWindAlt(6);
	}
	
	public void setWindSixAlt(double wind_Six_alt) {
		setWindAlt(6, wind_Six_alt);
	}
	
	public double getWindSixDirection() {
		return getWindDirection(6);
	}
	
	public void setWindSixDirection(double wind_dir) {
		setWindDirection(6, wind_dir);
	}
	
	public double getWindSeven() {
		return getWind(7);
	}
	
	public void setWindSeven(double wind_Seven) {
		setWind(7, wind_Seven);
	}
	
	public double getWindSevenAlt() {
		return getWindAlt(7);
	}
	
	public void setWindSevenAlt(double wind_Seven_alt) {
		setWindAlt(7, wind_Seven_alt);
	}
	
	public double getWindSevenDirection() {
		return getWindDirection(7);
	}
	
	public void setWindSevenDirection(double wind_dir) {
		setWindDirection(7, wind_dir);
	}
	
	public double getWindEight() {
		return getWind(8);
	}
	
	public void setWindEight(double wind_Eight) {
		setWind(8, wind_Eight);
	}
	
	public double getWindEightAlt() {
		return getWindAlt(8);
	}
	
	public void setWindEightAlt(double wind_Eight_alt) {
		setWindAlt(8, wind_Eight_alt);
	}
	
	public double getWindEightDirection() {
		return getWindDirection(8);
	}
	
	public void setWindEightDirection(double wind_dir) {
		setWindDirection(8, wind_dir);
	}
	
	public double getWindNine() {
		return getWind(9);
	}
	
	public void setWindNine(double wind_Nine) {
		setWind(9, wind_Nine);
	}
	
	public double getWindNineAlt() {
		return getWindAlt(9);
	}
	
	public void setWindNineAlt(double wind_Nine_alt) {
		setWindAlt(9, wind_Nine_alt);
	}
	
	public double getWindNineDirection() {
		return getWindDirection(9);
	}
	
	public void setWindNineDirection(double wind_dir) {
		setWindDirection(9, wind_dir);
	}
	
	public double getWindTen() {
		return getWind(10);
	}
	
	public void setWindTen(double wind_Ten) {
		setWind(10, wind_Ten);
	}
	
	public double getWindTenAlt() {
		return getWindAlt(10);
	}
	
	public void setWindTenAlt(double wind_Ten_alt) {
		setWindAlt(10, wind_Ten_alt);
	}
	
	public double getWindTenDirection() {
		return getWindDirection(10);
	}
	
	public void setWindTenDirection(double wind_dir) {
		setWindDirection(10, wind_dir);
	}
	
	public double getWindEleven() {
		return getWind(11);
	}
	
	public void setWindEleven(double wind_Eleven) {
		setWind(11, wind_Eleven);
	}
	
	public double getWindElevenAlt() {
		return getWindAlt(11);
	}
	
	public void setWindElevenAlt(double wind_Eleven_alt) {
		setWindAlt(11, wind_Eleven_alt);
	}
	
	public double getWindElevenDirection() {
		return getWindDirection(11);
	}
	
	public void setWindElevenDirection(double wind_dir) {
		setWindDirection(11, wind_dir);
	}
	
	public double getWindTwelve() {
		return getWind(12);
	}
	
	public void setWindTwelve(double wind_Twelve) {
		setWind(12, wind_Twelve);
	}
	
	public double getWindTwelveAlt() {
		return getWindAlt(12);
	}
	
	public void setWindTwelveAlt(double wind_Twelve_alt) {
		setWindAlt(12, wind_Twelve_alt);
	}
	
	public double getWindTwelveDirection() {
		return getWindDirection(12);
	}
	
	public void setWindTwelveDirection(double wind_dir) {
		setWindDirection(12, wind_dir);
	}
}
