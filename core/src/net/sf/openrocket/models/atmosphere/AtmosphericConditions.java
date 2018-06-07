package net.sf.openrocket.models.atmosphere;

import net.sf.openrocket.util.BugException;
import net.sf.openrocket.util.MathUtil;
import net.sf.openrocket.util.Monitorable;
import net.sf.openrocket.util.UniqueID;

public class AtmosphericConditions implements Cloneable, Monitorable {
	
	/** Specific gas constant of dry air. */
	public static final double R = 287.053;
	
	/** Specific heat ratio of air. */
	public static final double GAMMA = 1.4;
	
	/** The standard air pressure (1.01325 bar). */
	public static final double STANDARD_PRESSURE = 101325.0;
	
	/** The standard air temperature (20 degrees Celcius). */
	public static final double STANDARD_TEMPERATURE = 293.15;
	
	
	
	/** Air pressure, in Pascals. */
	private double pressure;
	
	/** Air temperature, in Kelvins. */
	private double temperature;
	
	private int modID;
	
	
	/**
	 * Construct standard atmospheric conditions.
	 */
	public AtmosphericConditions() {
		this(STANDARD_TEMPERATURE, STANDARD_PRESSURE);
	}
	
	/**
	 * Construct specified atmospheric conditions.
	 *
	 * @param temperature	the temperature in Kelvins.
	 * @param pressure		the pressure in Pascals.
	 */
	public AtmosphericConditions(double temperature, double pressure) {
		this.setTemperature(temperature);
		this.setPressure(pressure);
		this.modID = UniqueID.next();
	}
	
	
	
	public double getPressure() {
		return pressure;
	}
	
	public void setPressure(double pressure) {
		this.pressure = pressure;
		this.modID = UniqueID.next();
	}
	
	public double getTemperature() {
		return temperature;
	}
	
	public void setTemperature(double temperature) {
		this.temperature = temperature;
		this.modID = UniqueID.next();
	}
	
	/**
	 * Return the current density of air for dry air.
	 *
	 * @return   the current density of air.
	 */
	public double getDensity() {
		return getPressure() / (R * getTemperature());
	}
	
	
	/**
	 * Return the current speed of sound for dry air.
	 * <p>
	 * T in K
	 *
	 * @return   the current speed of sound.
	 */
	public double getMachSpeed() {
		double v = Math.sqrt(GAMMA * R * (getTemperature()));
		return v;
	}
	
	
	
	/**
	 * Return the current kinematic viscosity of the air.
	 * <p>
	 * The effect of temperature on the viscosity of a gas can be computed using
	 * Sutherland's formula.  In the region of -40 ... 40 degrees Celcius the effect
	 * is highly linear, and thus a linear approximation is used in its stead.
	 * This is divided by the result of {@link #getDensity()} to achieve the
	 * kinematic viscosity.
	 *
	 * @return	the current kinematic viscosity.
	 */
	public double getKinematicViscosity() {
		double v = (1.512041288 * Math.pow(getTemperature(), 1.5) / (getTemperature() + 120));
		return v / getDensity();
	}
	
	
	/**
	 * Return a copy of the atmospheric conditions.
	 */
	@Override
	public AtmosphericConditions clone() {
		try {
			return (AtmosphericConditions) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new BugException("CloneNotSupportedException encountered!");
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof AtmosphericConditions))
			return false;
		AtmosphericConditions o = (AtmosphericConditions) other;
		return MathUtil.equals(this.pressure, o.pressure) && MathUtil.equals(this.temperature, o.temperature);
	}
	
	@Override
	public int hashCode() {
		return (int) (this.pressure + this.temperature * 1000);
	}
	
	@Override
	public int getModID() {
		return modID;
	}
	
	@Override
	public String toString() {
		return String.format("AtmosphericConditions[T=%.2f,P=%.2f]", getTemperature(), getPressure());
	}
	
}
