package net.sf.openrocket.simulation.listeners.example;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.gui.SpinnerEditor;
import net.sf.openrocket.gui.adaptors.DoubleModel;
import net.sf.openrocket.gui.components.BasicSlider;
import net.sf.openrocket.gui.components.UnitSelector;
import net.sf.openrocket.plugin.Plugin;
import net.sf.openrocket.simulation.extension.AbstractSwingSimulationExtensionConfigurator;
import net.sf.openrocket.unit.UnitGroup;

/**
 * The Swing configuration dialog for the extension.
 * 
 * The abstract implementation provides a ready JPanel using MigLayout
 * to which you can build the dialog. 
 */
@Plugin
public class MultiLevelWindConfigurator extends AbstractSwingSimulationExtensionConfigurator<MultiLevelWind> {
	
	public MultiLevelWindConfigurator() {
		super(MultiLevelWind.class);
	}
	
	@Override
	protected JComponent getConfigurationComponent(MultiLevelWind extension, Simulation simulation, JPanel panel) {
		extension.setGlobalWindDirection(simulation.getOptions().getWindDirection());
		extension.setGlobalWindSpeed(simulation.getOptions().getWindSpeedAverage());
		AddWind(extension, simulation, panel, "First Wind", "WindZero");
		AddWind(extension, simulation, panel, "Second Wind", "WindOne");
		AddWind(extension, simulation, panel, "Third Wind", "WindTwo");
		AddWind(extension, simulation, panel, "Fourth Wind", "WindThree");
		AddWind(extension, simulation, panel, "Fifth Wind", "WindFour");
		AddWind(extension, simulation, panel, "Sixth Wind", "WindFive");
		AddWind(extension, simulation, panel, "Seventh Wind", "WindSix");
		AddWind(extension, simulation, panel, "Eighth Wind", "WindSeven");
		AddWind(extension, simulation, panel, "Ninth Wind", "WindEight");
		AddWind(extension, simulation, panel, "Tenth Wind", "WindNine");
		AddWind(extension, simulation, panel, "Eleventh Wind", "WindTen");
		AddWind(extension, simulation, panel, "Twelth Wind", "WindEleven");
		AddWind(extension, simulation, panel, "Thirtheenth Wind", "WindTwelve");
		/*panel.add(new JLabel("First Wind:"));
		
		DoubleModel m = new DoubleModel(extension, "WindZero", UnitGroup.UNITS_WINDSPEED, 0);
		
		JSpinner spin = new JSpinner(m.getSpinnerModel());
		spin.setEditor(new SpinnerEditor(spin));
		panel.add(spin, "w 65lp!");
		
		UnitSelector unit = new UnitSelector(m);
		panel.add(unit, "w 25");
		
		BasicSlider slider = new BasicSlider(m.getSliderModel(0, UnitGroup.UNITS_WINDSPEED.fromString("100mph")));
		panel.add(slider, "w 200lp, wrap");
		
		panel.add(new JLabel("Wind Zero Altitude:"));*/
		
		return panel;
	}
	
	private void AddWind(MultiLevelWind extension, Simulation simulation, JPanel panel, String label, String configKey) {
		//panel.add(new JLabel(label + " altitude:"));
		// Wind altitude
		panel.add(new JLabel("Altitude:"));
		DoubleModel altModel = new DoubleModel(extension, configKey + "Alt", UnitGroup.UNITS_DISTANCE, UnitGroup.UNITS_DISTANCE.fromString("330ft"));
		
		JSpinner altSpin = new JSpinner(altModel.getSpinnerModel());
		altSpin.setEditor(new SpinnerEditor(altSpin));
		panel.add(altSpin, "w 75lp!");
		
		UnitSelector altUnit = new UnitSelector(altModel);
		panel.add(altUnit, "w 25");
		//BasicSlider altSlider = new BasicSlider(altModel.getSliderModel(0, UnitGroup.UNITS_DISTANCE.fromString("20000ft")));
		//panel.add(altSlider, "w 200lp, wrap");
		
		//UnitSelector altUnit = new UnitSelector(altModel);
		//panel.add(altUnit, "w 25");
		
		// Wind speed
		panel.add(new JLabel("       Speed:"));
		DoubleModel speedModel = new DoubleModel(extension, configKey, UnitGroup.UNITS_WINDSPEED, 0);
		
		JSpinner speedSpin = new JSpinner(speedModel.getSpinnerModel());
		speedSpin.setEditor(new SpinnerEditor(speedSpin));
		panel.add(speedSpin, "w 65lp!");
		
		UnitSelector speedUnit = new UnitSelector(speedModel);
		panel.add(speedUnit, "w 25");
		
		// Wind direction
		panel.add(new JLabel("       Direction:"));
		
		DoubleModel dirModel = new DoubleModel(extension, configKey + "Direction", UnitGroup.UNITS_ANGLE, UnitGroup.UNITS_ANGLE.fromString("90"));
		
		JSpinner dirSpin = new JSpinner(dirModel.getSpinnerModel());
		dirSpin.setEditor(new SpinnerEditor(dirSpin));
		panel.add(dirSpin, "w 65lp!");
		
		BasicSlider dirSlider = new BasicSlider(dirModel.getSliderModel(0, UnitGroup.UNITS_ANGLE.fromString("360")));
		panel.add(dirSlider, "w 200lp, wrap");
	}
	
}
