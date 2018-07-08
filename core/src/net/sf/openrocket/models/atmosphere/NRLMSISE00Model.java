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
	
	private final double[] layer = { 0, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 11000, 12000, 13000, 14000, 15000, 16000, 17000, 18000, 19000, 20000, 21000, 22000, 23000, 24000,
			25000, 26000, 27000, 28000, 29000, 30000, 31000, 32000, 33000, 34000, 35000, 36000, 37000, 38000, 39000, 40000, 41000, 42000, 43000, 44000, 45000, 46000, 47000, 48000, 49000, 50000, 51000,
			52000, 53000, 54000, 55000, 56000, 57000, 58000, 59000, 60000, 61000, 62000, 63000, 64000, 65000, 66000, 67000, 68000, 69000, 70000, 71000, 72000, 73000, 74000, 75000, 76000, 77000, 78000,
			79000, 80000, 81000, 82000, 83000, 84000, 85000, 86000, 87000, 88000, 89000, 90000, 91000, 92000, 93000, 94000, 95000, 96000, 97000, 98000, 99000, 100000, 101000, 102000, 103000, 104000,
			105000, 106000, 107000, 108000, 109000, 110000, 111000, 112000, 113000, 114000, 115000, 116000, 117000, 118000, 119000, 120000, 121000, 122000, 123000, 124000, 125000, 126000, 127000,
			128000, 129000, 130000, 131000, 132000, 133000, 134000, 135000, 136000, 137000, 138000, 139000, 140000, 141000, 142000, 143000, 144000, 145000, 146000, 147000, 148000, 149000, 150000,
			151000, 152000, 153000, 154000, 155000, 156000, 157000, 158000, 159000, 160000, 161000, 162000, 163000, 164000, 165000, 166000, 167000, 168000, 169000, 170000, 171000, 172000, 173000,
			174000, 175000, 176000, 177000, 178000, 179000, 180000, 181000, 182000, 183000, 184000, 185000, 186000, 187000, 188000, 189000, 190000, 191000, 192000, 193000, 194000, 195000, 196000,
			197000, 198000, 199000, 200000, 201000, 202000, 203000, 204000, 205000, 206000, 207000, 208000, 209000, 210000, 211000, 212000, 213000, 214000, 215000, 216000, 217000, 218000, 219000,
			220000, 221000, 222000, 223000, 224000, 225000, 226000, 227000, 228000, 229000, 230000, 231000, 232000, 233000, 234000, 235000, 236000, 237000, 238000, 239000, 240000, 241000, 242000,
			243000, 244000, 245000, 246000, 247000, 248000, 249000, 250000, 251000, 252000, 253000, 254000, 255000, 256000, 257000, 258000, 259000, 260000, 261000, 262000, 263000, 264000, 265000,
			266000, 267000, 268000, 269000, 270000, 271000, 272000, 273000, 274000, 275000, 276000, 277000, 278000, 279000, 280000, 281000, 282000, 283000, 284000, 285000, 286000, 287000, 288000,
			289000, 290000, 291000, 292000, 293000, 294000, 295000, 296000, 297000, 298000, 299000, 300000 };
	private final double[] baseTemperature = {
			290.5, 286.1, 280.9, 274.9, 268.5, 261.8, 255, 248.3, 241.7, 235.3, 229.2, 223.5, 218.5, 214.2, 210.9, 208.8, 207.9, 208, 208.9, 210.3, 211.9, 213.6, 215.2, 216.9, 218.6, 220.3, 222,
			223.8, 225.6, 227.5, 229.4, 231.5, 233.6, 236, 238.7, 241.7, 245.1, 248.6, 252.3, 256, 259.7, 263.1, 266.3, 268.9, 271, 272.3, 272.8, 272.5, 271.4, 269.8, 267.6, 265.1, 262.2, 259.2,
			256.1, 253, 250, 247, 244.1, 241.2, 238.5, 235.8, 233.1, 230.6, 228.1, 225.6, 223.3, 221, 218.7, 216.5, 214.4, 212.4, 210.4, 208.5, 207, 205.8, 204.9, 204.3, 203.8, 203.4, 203.1, 202.8,
			202.5, 202.1, 201.6, 200.9, 200, 198.9, 197.5, 195.7, 193.7, 191.3, 188.7, 186, 183.5, 181.2, 179.2, 177.7, 176.7, 176.4, 176.9, 178.2, 180.4, 183.5, 187.4, 192.1, 197.6, 204.1, 211.4,
			219.7, 228.9, 239, 250.1, 262.3, 275.5, 289.8, 305, 321.1, 337.8, 355, 372.1, 388.7, 404.2, 417.7, 428.4, 438.9, 449.1, 459.1, 468.8, 478.3, 487.6, 496.7, 505.5, 514.1, 522.6, 530.8,
			538.8, 546.7, 554.3, 561.8, 569.1, 576.2, 583.2, 590, 596.6, 603.1, 609.4, 615.6, 621.6, 627.5, 633.2, 638.8, 644.3, 649.6, 654.9, 660, 664.9, 669.8, 674.5, 679.2, 683.7, 688.1, 692.4,
			696.6, 700.7, 704.8, 708.7, 712.5, 716.2, 719.9, 723.5, 726.9, 730.3, 733.7, 736.9, 740.1, 743.2, 746.2, 749.1, 752, 754.8, 757.6, 760.2, 762.8, 765.4, 767.9, 770.3, 772.7, 775, 777.3,
			779.5, 781.7, 783.8, 785.9, 787.9, 789.9, 791.8, 793.7, 795.5, 797.3, 799.1, 800.8, 802.4, 804.1, 805.7, 807.2, 808.8, 810.2, 811.7, 813.1, 814.5, 815.8, 817.2, 818.5, 819.7, 820.9, 822.1,
			823.3, 824.5, 825.6, 826.7, 827.8, 828.8, 829.8, 830.8, 831.8, 832.7, 833.7, 834.6, 835.5, 836.3, 837.2, 838, 838.8, 839.6, 840.4, 841.1, 841.9, 842.6, 843.3, 844, 844.6, 845.3, 845.9,
			846.6, 847.2, 847.8, 848.4, 848.9, 849.5, 850, 850.6, 851.1, 851.6, 852.1, 852.6, 853, 853.5, 854, 854.4, 854.8, 855.3, 855.7, 856.1, 856.5, 856.8, 857.2, 857.6, 857.9, 858.3, 858.6, 859,
			859.3, 859.6, 859.9, 860.2, 860.5, 860.8, 861.1, 861.4, 861.7, 861.9, 862.2, 862.4, 862.7, 862.9, 863.2, 863.4, 863.6, 863.8, 864.1, 864.3, 864.5, 864.7, 864.9, 865.1, 865.3, 865.4, 865.6,
			865.8, 866 };
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
