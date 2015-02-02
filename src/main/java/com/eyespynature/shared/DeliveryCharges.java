package com.eyespynature.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DeliveryCharges {

	public enum DeliveryMethod implements IsSerializable {
		ECONOMY(true), NEXT_DAY(true), PICK_UP(true), FIRST_CLASS(true), STANDARD(true);

		private boolean tracked;

		DeliveryMethod(boolean tracked) {
			this.tracked = tracked;
		}

		public boolean isTracked() {
			return tracked;
		}
	}

	public static long economyCharge(int weightTotal, long subtotalLong, int countBig) {

		if (weightTotal == 0 || subtotalLong >= 16000) {
			return 0;
		}
		int maxWeight = 10000;
		int count = Math.max(countBig, (weightTotal + maxWeight - 1) / maxWeight);
		int weight = (weightTotal + count - 1) / count;
		long charge;
		if (weight <= 2000) {
			charge = 489;
		} else if (weight <= 5000) {
			charge = 619;
		} else {
			charge = 829;
		}
		long value = subtotalLong / count;
		long insurance;
		if (value <= 5000) {
			insurance = 100;
		} else if (value <= 15000) {
			insurance = 300;
		} else {
			insurance = 500;
		}
		return (charge + insurance) * count;
	}

	public static long standardCharge(int weightTotal, long subtotalLong, int countBig) {

		if (weightTotal == 0) {
			return 0;
		}
		int maxWeight = 10000;
		int count = Math.max(countBig, (weightTotal + maxWeight - 1) / maxWeight);
		int weight = (weightTotal + count - 1) / count;
		long charge;
		if (weight <= 2000) {
			charge = 599;
		} else if (weight <= 5000) {
			charge = 719;
		} else {
			charge = 929;
		}
		long value = subtotalLong / count;
		long insurance;
		if (value <= 5000) {
			insurance = 100;
		} else if (value <= 15000) {
			insurance = 300;
		} else {
			insurance = 500;
		}
		return (charge + insurance) * count;
	}

	public static long nextDayCharge(int weightTotal, int countBig) {
		if (weightTotal == 0) {
			return 0;
		}
		int maxWeight = 10000;
		int count = Math.max(countBig, (weightTotal + maxWeight - 1) / maxWeight);
		int weight = (weightTotal + count - 1) / count;
		long charge = 2335;
		if (weight <= 100) {
			charge = 565;
		} else if (weight <= 500) {
			charge = 600;
		} else if (weight <= 1000) {
			charge = 710;
		} else if (weight <= 2000) {
			charge = 890;
		}
		return charge * count;
	}

	public static long pickupCharge(int weightTotal, long subtotalLong, int countBig) {
		if (weightTotal == 0 || subtotalLong >= 12000) {
			return 0;
		}
		int maxWeight = 10000;
		int count = Math.max(countBig, (weightTotal + maxWeight - 1) / maxWeight);
		long charge = 529;
		long value = subtotalLong / count;
		long insurance;
		if (value <= 5000) {
			insurance = 0;
		} else if (value <= 15000) {
			insurance = 300;
		} else {
			insurance = 500;
		}
		return (charge + insurance) * count;
	}

	public static long firstClassCharge(int weightTotal, int countBig) {
		if (weightTotal == 0) {
			return 0;
		}
		int count = Math.max(countBig, 1);
		int weight = (weightTotal + count - 1) / count;
		long charge = ((weight + 1999 - 4000) / 2000) * 350 + 1125;
		if (weight <= 750) {
			charge = 365;
		} else if (weight <= 1000) {
			charge = 525;
		} else if (weight <= 1250) {
			charge = 655;
		} else if (weight <= 1500) {
			charge = 745;
		} else if (weight <= 1750) {
			charge = 835;
		} else if (weight <= 2000) {
			charge = 925;
		} else if (weight <= 4000) {
			charge = 1125;
		}
		return charge * count;
	}
}