package net.sf.openrocket.models.atmosphere;

import static net.sf.openrocket.models.atmosphere.AtmosphericConditions.R;
import net.sf.openrocket.util.MathUtil;


/**
 * Updated from ISA to NRLMSISE Model
 *
 * @author Sampo Niskanen <sampo.niskanen@iki.fi>
 * @author Rayan Sud <rayansud@stanford.edu>
 */
public class NRLMSISE00Model extends InterpolatingAtmosphericModel {

	public static final double STANDARD_TEMPERATURE = 288.15;
	public static final double STANDARD_PRESSURE = 101325;
		public static final double DELTA = 100;
				public static final double MAX_ALT = 150000;
	private static final double G = 9.80665;

	private final double[] layer = { 0,500,1000,1500,2000,2500,3000,3500,4000,4500,5000,5500,6000,6500,7000,7500,8000,8500,9000,9500,10000,10500,11000,11500,12000,12500,13000,13500,14000,14500,15000,15500,16000,16500,17000,17500,18000,18500,19000,19500,20000,20500,21000,21500,22000,22500,23000,23500,24000,24500,25000,25500,26000,26500,27000,27500,28000,28500,29000,29500,30000,30500,31000,31500,32000,32500,33000,33500,34000,34500,35000,35500,36000,36500,37000,37500,38000,38500,39000,39500,40000,40500,41000,41500,42000,42500,43000,43500,44000,44500,45000,45500,46000,46500,47000,47500,48000,48500,49000,49500,50000,50500,51000,51500,52000,52500,53000,53500,54000,54500,55000,55500,56000,56500,57000,57500,58000,58500,59000,59500,60000,60500,61000,61500,62000,62500,63000,63500,64000,64500,65000,65500,66000,66500,67000,67500,68000,68500,69000,69500,70000,70500,71000,71500,72000,72500,73000,73500,74000,74500,75000,75500,76000,76500,77000,77500,78000,78500,79000,79500,80000,80500,81000,81500,82000,82500,83000,83500,84000,84500,85000,85500,86000,86500,87000,87500,88000,88500,89000,89500,90000,90500,91000,91500,92000,92500,93000,93500,94000,94500,95000,95500,96000,96500,97000,97500,98000,98500,99000,99500,100000,100500,101000,101500,102000,102500,103000,103500,104000,104500,105000,105500,106000,106500,107000,107500,108000,108500,109000,109500,110000,110500,111000,111500,112000,112500,113000,113500,114000,114500,115000,115500,116000,116500,117000,117500,118000,118500,119000,119500,120000,120500,121000,121500,122000,122500,123000,123500,124000,124500,125000,125500,126000,126500,127000,127500,128000,128500,129000,129500,130000,130500,131000,131500,132000,132500,133000,133500,134000,134500,135000,135500,136000,136500,137000,137500,138000,138500,139000,139500,140000,140500,141000,141500,142000,142500,143000,143500,144000,144500,145000,145500,146000,146500,147000,147500,148000,148500,149000,149500,150000 };
	private final double[] baseTemperature = {
			292.4,290.3,287.9,285.4,282.6,279.6,276.5,273.3,270,266.6,263.1,259.6,256.1,252.7,249.2,245.8,242.4,239.1,235.8,232.6,229.5,226.5,223.7,221,218.4,216.1,214,212.2,210.6,209.3,208.4,207.7,207.4,207.3,207.5,207.9,208.4,209,209.7,210.5,211.3,212.1,212.9,213.7,214.4,215.2,216,216.7,217.5,218.2,219,219.8,220.6,221.4,222.2,223,223.8,224.7,225.6,226.5,227.5,228.5,229.5,230.6,231.7,232.8,234.1,235.4,236.9,238.4,240.1,241.8,243.5,245.3,247.2,249.1,251.1,253,254.9,256.9,258.7,260.6,262.4,264.1,265.7,267.2,268.5,269.7,270.7,271.6,272.2,272.6,272.8,272.8,272.5,272.1,271.6,270.9,270,269,267.9,266.7,265.5,264.1,262.7,261.2,259.8,258.3,256.7,255.2,253.7,252.3,250.8,249.4,248,246.6,245.2,243.8,242.5,241.2,239.9,238.6,237.3,236.1,234.8,233.6,232.4,231.2,230,228.9,227.7,226.6,225.4,224.3,223.2,222.1,221.1,220,218.9,217.9,216.8,215.8,214.8,213.8,212.8,211.8,210.8,209.9,209.1,208.2,207.4,206.7,205.9,205.2,204.6,203.9,203.2,202.6,202,201.4,200.8,200.2,199.6,199,198.4,197.8,197.2,196.6,195.9,195.3,194.6,194,193.3,192.5,191.8,191,190.2,189.4,188.6,187.7,186.8,185.8,184.9,183.9,183,182.1,181.2,180.3,179.6,178.8,178.2,177.7,177.2,176.9,176.7,176.7,176.8,177,177.4,178.1,178.9,179.9,181.2,182.7,184.4,186.3,188.4,190.7,193.2,196,198.9,202,205.4,208.9,212.7,216.6,220.7,225,229.5,234.2,239,243.9,249,254.2,259.6,265.1,270.7,276.4,282.3,288.3,294.4,300.5,306.8,313.1,319.5,326,332.4,338.9,345.3,351.7,358,364.2,370.2,376.1,381.7,387.1,392.2,396.7,401.2,405.6,410.1,414.4,418.8,423.1,427.3,431.5,435.7,439.8,443.9,448,452,455.9,459.9,463.8,467.6,471.5,475.3,479,482.7,486.4,490.1,493.7,497.2,500.8,504.3,507.8,511.2,514.6,518,521.3,524.7,527.9,531.2,534.4,537.6,540.7,543.9,547,550,553.1,556.1,559.1,562,564.9,567.8,570.7,573.6,576.4,579.2,581.9,584.7
	};
	private final double[] basePressure = new double[layer.length];


	/**
	 * Construct the standard NRLMSISE00 model.
	 */
	public NRLMSISE00Model() {
		this(STANDARD_TEMPERATURE, STANDARD_PRESSURE);
	}

	/**
	 * Construct an extended model with the given temperature and pressure at MSL.
	 *
	 * @param temperature	the temperature at MSL.
	 * @param pressure		the pressure at MSL.
	 */
	public NRLMSISE00Model(double temperature, double pressure) {
		this(0, temperature, pressure);
	}


	/**
	 * Construct an extended model with the given temperature and pressure at the
	 * specified altitude.  Conditions below the given altitude cannot be calculated,
	 * and the values at the specified altitude will be returned instead.  The altitude
	 * must be lower than the altitude of the next ISA standard layer (below 11km).
	 *
	 * @param altitude		the altitude of the measurements.
	 * @param temperature	the temperature.
	 * @param pressure		the pressure.
	 * @throws IllegalArgumentException  if the altitude exceeds the second layer boundary
	 * 									 of the ISA model (over 11km).
	 */
	public NRLMSISE00Model(double altitude, double temperature, double pressure) {
		if (altitude >= layer[1]) {
			throw new IllegalArgumentException("Too high first altitude: " + altitude);
		}

		layer[0] = altitude;
		baseTemperature[0] = temperature;
		basePressure[0] = pressure;

		for (int i = 1; i < basePressure.length; i++) {
			basePressure[i] = getExactConditions(layer[i] - 1).getPressure();
		}
	}


	@Override
	protected AtmosphericConditions getExactConditions(double altitude) {
		altitude = MathUtil.clamp(altitude, layer[0], layer[layer.length - 1]);
		int n;
		for (n = 0; n < layer.length - 1; n++) {
			if (layer[n + 1] > altitude)
				break;
		}

		double rate = (baseTemperature[n + 1] - baseTemperature[n]) / (layer[n + 1] - layer[n]);

		double t = baseTemperature[n] + (altitude - layer[n]) * rate;
		double p;
		if (Math.abs(rate) > 0.001) {
			p = basePressure[n] *
					Math.pow(1 + (altitude - layer[n]) * rate / baseTemperature[n], -G / (rate * R));
		} else {
			p = basePressure[n] *
					Math.exp(-(altitude - layer[n]) * G / (R * baseTemperature[n]));
		}

		return new AtmosphericConditions(t, p);
	}

	@Override
	protected double getMaxAltitude() {
		return layer[layer.length - 1];
	}


	public static void main(String foo[]) {
		NRLMSISE00Model model1 = new NRLMSISE00Model();
		NRLMSISE00Model model2 = new NRLMSISE00Model(278.15, 100000);

		for (double alt = 0; alt < MAX_ALT; alt += DELTA) {
			AtmosphericConditions cond1 = model1.getConditions(alt);
			AtmosphericConditions cond2 = model2.getConditions(alt);

			AtmosphericConditions diff = new AtmosphericConditions();
			diff.setPressure((cond2.getPressure() - cond1.getPressure()) / cond1.getPressure() * 100);
			diff.setTemperature((cond2.getTemperature() - cond1.getTemperature()) / cond1.getTemperature() * 100);
			//System.out.println("alt=" + alt +	": std:" + cond1 + " mod:" + cond2 + " diff:" + diff);
		}
	}

	@Override
	public int getModID() {
		return 0;
	}

}
