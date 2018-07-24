package net.sf.openrocket.models.wind;

import java.util.Random;

import net.sf.openrocket.util.Coordinate;

public class NOAA_SA_March_WindModel implements WindModel {
	
	@Override
	public int getModID() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public Coordinate getWindVelocity(double time, double altitude) {
		
		Random random = new Random();
		Coordinate wind = new Coordinate(0, 0, 0);
		double x = 0;
		double y = 0;
		if (altitude > 32452.5005978) {
			x = 36.3263165054;
			y = 6.92195614624;
			x = random.nextGaussian() * 9.40202674870916 + x;
			y = random.nextGaussian() * 5.16567738090836 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 32452.5005978 && altitude > 30777.8092016) {
			x = 29.3793911896;
			y = 4.82793886567;
			x = random.nextGaussian() * 8.98350981592996 + x;
			y = random.nextGaussian() * 3.37524892217864 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 30777.8092016 && altitude > 29690.7513985) {
			x = 22.8661673584;
			y = 3.24206293488;
			x = random.nextGaussian() * 9.39827260461067 + x;
			y = random.nextGaussian() * 2.25111555428573 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 29690.7513985 && altitude > 28196.2440108) {
			x = 13.6423899841;
			y = 2.79737683105;
			x = random.nextGaussian() * 9.18410104564979 + x;
			y = random.nextGaussian() * 2.18409165284189 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 28196.2440108 && altitude > 27129.4270855) {
			x = 8.22646098328;
			y = 2.46269087982;
			x = random.nextGaussian() * 7.05298116890889 + x;
			y = random.nextGaussian() * 1.88235953270399 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 27129.4270855 && altitude > 25921.4333433) {
			x = 3.30426757813;
			y = 2.18177420044;
			x = random.nextGaussian() * 4.97043497372214 + x;
			y = random.nextGaussian() * 1.3798094048097 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 25921.4333433 && altitude > 23325.8812703) {
			x = -1.86810777662;
			y = 1.75049559784;
			x = random.nextGaussian() * 4.69338727889435 + x;
			y = random.nextGaussian() * 1.24594311195766 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 23325.8812703 && altitude > 21641.084214) {
			x = -1.33241801453;
			y = 1.49821691895;
			x = random.nextGaussian() * 3.90950687468938 + x;
			y = random.nextGaussian() * 1.0196188536235 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 21641.084214 && altitude > 19324.7937279) {
			x = 3.22674471664;
			y = 2.78367730713;
			x = random.nextGaussian() * 2.1267826055115 + x;
			y = random.nextGaussian() * 1.76888206171594 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 19324.7937279 && altitude > 17671.3673782) {
			x = 12.7885118332;
			y = 5.58127806855;
			x = random.nextGaussian() * 3.44053891841506 + x;
			y = random.nextGaussian() * 2.95901991777127 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 17671.3673782 && altitude > 15799.1354739) {
			x = 25.3903018188;
			y = 8.049134964;
			x = random.nextGaussian() * 5.62373895725015 + x;
			y = random.nextGaussian() * 6.50468042623315 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 15799.1354739 && altitude > 13510.5978217) {
			x = 41.2080617828;
			y = 13.3200896912;
			x = random.nextGaussian() * 5.4949625087604 + x;
			y = random.nextGaussian() * 9.23103377104541 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 13510.5978217 && altitude > 11776.370098) {
			x = 46.7050784645;
			y = 15.8557713928;
			x = random.nextGaussian() * 7.76847193904771 + x;
			y = random.nextGaussian() * 9.70973603683822 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 11776.370098 && altitude > 10364.2734787) {
			x = 42.5318058968;
			y = 13.781663227;
			x = random.nextGaussian() * 7.80338793282181 + x;
			y = random.nextGaussian() * 8.63759545348202 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 10364.2734787 && altitude > 9165.155577) {
			x = 35.317874527;
			y = 10.4327636147;
			x = random.nextGaussian() * 7.29148277518442 + x;
			y = random.nextGaussian() * 6.8733289092929 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 9165.155577 && altitude > 8118.34810593) {
			x = 29.3993032074;
			y = 8.40443960192;
			x = random.nextGaussian() * 7.06179981189336 + x;
			y = random.nextGaussian() * 5.469037476714 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 8118.34810593 && altitude > 7186.40700344) {
			x = 24.6830108281;
			y = 7.45029076004;
			x = random.nextGaussian() * 7.17297724987338 + x;
			y = random.nextGaussian() * 4.54235255985821 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 7186.40700344 && altitude > 6344.48849124) {
			x = 21.0755120545;
			y = 6.6838747368;
			x = random.nextGaussian() * 7.14684988010955 + x;
			y = random.nextGaussian() * 4.07382581671099 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 6344.48849124 && altitude > 5575.20904315) {
			x = 18.3204710388;
			y = 6.12670441056;
			x = random.nextGaussian() * 6.93282287011739 + x;
			y = random.nextGaussian() * 3.96655863543755 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 5575.20904315 && altitude > 4865.89864028) {
			x = 15.8808373261;
			y = 5.74512733459;
			x = random.nextGaussian() * 6.60991520439853 + x;
			y = random.nextGaussian() * 4.0301825172086 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 4865.89864028 && altitude > 4207.01915369) {
			x = 13.5328713951;
			y = 5.20583532715;
			x = random.nextGaussian() * 6.11478321369355 + x;
			y = random.nextGaussian() * 4.03623947198487 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 4207.01915369 && altitude > 3591.1989024) {
			x = 11.1812036171;
			y = 4.52174741364;
			x = random.nextGaussian() * 5.51178927550981 + x;
			y = random.nextGaussian() * 3.77444040982748 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 3591.1989024 && altitude > 3012.61500374) {
			x = 8.77521916581;
			y = 4.41855432129;
			x = random.nextGaussian() * 5.32003601470971 + x;
			y = random.nextGaussian() * 3.14734906304153 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 3012.61500374 && altitude > 2466.58265617) {
			x = 5.52565662002;
			y = 4.9369782486;
			x = random.nextGaussian() * 5.64108699580909 + x;
			y = random.nextGaussian() * 2.77396915792641 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 2466.58265617 && altitude > 1949.27303198) {
			x = 3.06240907287;
			y = 4.85561806107;
			x = random.nextGaussian() * 5.45516251898018 + x;
			y = random.nextGaussian() * 2.63550658441494 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 1949.27303198 && altitude > 1457.51409129) {
			x = 0.836319339755;
			y = 3.48983290863;
			x = random.nextGaussian() * 3.81906859093208 + x;
			y = random.nextGaussian() * 2.26876404091809 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 1457.51409129 && altitude > 988.646562749) {
			x = 0.887814441687;
			y = 3.40720874786;
			x = random.nextGaussian() * 3.78829098382957 + x;
			y = random.nextGaussian() * 2.2378200781135 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 988.646562749 && altitude > 762.07952581) {
			x = 0.886961402885;
			y = 3.4058826027;
			x = random.nextGaussian() * 3.78813762665915 + x;
			y = random.nextGaussian() * 2.23696940243298 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 762.07952581 && altitude > 540.417635196) {
			x = 0.886666290283;
			y = 3.40666239548;
			x = random.nextGaussian() * 3.79084874316545 + x;
			y = random.nextGaussian() * 2.23603081449454 + y;
			wind = new Coordinate(x, y, 0);
		} else if (altitude < 540.417635196 && altitude > 323.429706918) {
			x = 0.887579040522;
			y = 3.4061817131;
			x = random.nextGaussian() * 3.7889655128207 + x;
			y = random.nextGaussian() * 2.23619758460862 + y;
			wind = new Coordinate(x, y, 0);
		} else {
			x = 0.888221012114;
			y = 3.40551837158;
			x = random.nextGaussian() * 3.78962995063943 + x;
			y = random.nextGaussian() * 2.23679618245385 + y;
			wind = new Coordinate(x, y, 0);
		}
		return wind;
	}
	
}
