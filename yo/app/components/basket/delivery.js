'use strict';

/* Controllers */

(function() {
	var app = angular.module('esnApp.delivery', []);

	app.factory('DeliveryService', [
		'$http',
		function($http) {
			return {
				getFirstClassCharge : function(weightTotal, countBig) {
					if (weightTotal === 0) {
						return 0;
					}
					var count = Math.max(countBig, 1);
					var weight = count===1 ? weightTotal : Math.ceil(weightTotal / count);
					var charge = ((weight + 1999 - 4000) / 2000) * 350 + 1125;
					//console.log("getFirstClassCharge weight", weight, 1, (weight <= 750), 2, (weight <= 1000), 3, (weight <= 1250) ,4,(weight <= 1500) , 5, (weight <= 1750)) ;
					if (weight <= 750) {
						//console.log("getFirstClassCharge weight 1", weight);
						charge = 365;
					} else if (weight <= 1000) {
						//console.log("getFirstClassCharge weight 2", weight);
						charge = 525;
					} else if (weight <= 1250) {
						//console.log("getFirstClassCharge weight 3", weight);
						charge = 655;
					} else if (weight <= 1500) {
						//console.log("getFirstClassCharge weight 4", weight);
						charge = 745;
					} else if (weight <= 1750) {
						//console.log("getFirstClassCharge weight 5*", weight);
						charge = 835;
					} else if (weight <= 2000) {
						charge = 925;
					} else if (weight <= 4000) {
						charge = 1125;
					}
					var total = (charge * count)/100;
					//console.log("getFirstClassCharge charge", charge, "count", count, "total", total, "weight", weight);
					return total;
				},
				getNextDayCharge : function(weightTotal, countBig) {
					if (weightTotal === 0) {
						return 0;
					}
					var maxWeight = 10000;
					var count = Math.max(countBig, Math.round((weightTotal + maxWeight - 1) / maxWeight));
					var weight = Math.ceil(weightTotal / count);
					var charge = 2335;
					//console.log("getNextDayCharge weight", weight, 1, (weight <= 100), 2, (weight <= 500), 3, (weight <= 1000),4,(weight <= 2000) ) ;
					if (weight <= 100) {
						//console.log("getNextDayCharge weight 1", weight);
						charge = 565;
					} else if (weight <= 500) {
						//console.log("getNextDayCharge weight 2", weight);
						charge = 600;
					} else if (weight <= 1000) {
						//console.log("getNextDayCharge weight 3", weight);
						charge = 710;
					} else if (weight <= 2000) {
						//console.log("getNextDayCharge weight 4*", weight);
						charge = 890;
					}
					var total =  (charge * count)/100;
					//console.log("getNextDayCharge charge", charge, "count", count, "total", total, "weight", weight);
					return total;
				},
				getEconomyCharge : function(weightTotal, subtotalLong, countBig) {

					if (weightTotal === 0 || subtotalLong >= 16000) {
						return 0;
					}
					var maxWeight = 10000;
					var count = Math.max(countBig, Math.round((weightTotal + maxWeight - 1) / maxWeight));
					var weight = Math.ceil(weightTotal / count);
					var charge;
					if (weight <= 2000) {
						charge = 489;
					} else if (weight <= 5000) {
						charge = 619;
					} else {
						charge = 829;
					}
					var value = subtotalLong / count;
					var insurance;
					if (value <= 5000) {
						insurance = 100;
					} else if (value <= 15000) {
						insurance = 300;
					} else {
						insurance = 500;
					}
					var total = ((charge + insurance) * count ) / 100;
					//console.log("getEconomyCharge charge", charge, "insurance", insurance, "count", count, "total", total, "weight", weight);
					return total;
				},
				getStandardCharge : function(weightTotal, subtotalLong, countBig) {

					if (weightTotal === 0) {
						return 0;
					}
					var maxWeight = 10000;
					var count = Math.max(countBig, Math.round((weightTotal + maxWeight - 1) / maxWeight));
					var weight = Math.ceil(weightTotal / count);
					var charge;
					if (weight <= 2000) {
						charge = 599;
					} else if (weight <= 5000) {
						charge = 719;
					} else {
						charge = 929;
					}
					var value = subtotalLong / count;
					var insurance;
					if (value <= 5000) {
						insurance = 100;
					} else if (value <= 15000) {
						insurance = 300;
					} else {
						insurance = 500;
					}
					var total = ((charge + insurance) * count) / 100;
					//console.log("getStandardCharge charge", charge, "insurance", insurance, "count", count, "total", total, "weight", weight);
					return total;
				},
				getPickupCharge : function(weightTotal, subtotalLong, countBig) {
					if (weightTotal === 0 || subtotalLong >= 12000) {
						return 0;
					}
					var maxWeight = 10000;
					var count = Math.max(countBig, Math.round((weightTotal + maxWeight - 1) / maxWeight));
					var charge = 529;
					var value = subtotalLong / count;
					var insurance;
					if (value <= 5000) {
						insurance = 0;
					} else if (value <= 15000) {
						insurance = 300;
					} else {
						insurance = 500;
					}
					var total =  ((charge + insurance) * count) / 100;
					//console.log("getPickupCharge charge", charge, "insurance", insurance, "count", count, "total", total);
					return total;
				}
			};
		} ]);

})();