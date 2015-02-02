'use strict';

/* Controllers */

(function() {
	var app = angular.module('esnApp.basket', [ 'ngCookies', 'esnApp.delivery' ]);

	app.controller('AddToBasketController', [ '$scope', 'ProductService', '$routeParams', 'BasketService', 
	                                          function($scope, ProductService, $routeParams, BasketService) {

		var addScope = this;
		//TODO need to make better and maybe more dynamic
		addScope.CameraLevel = 'Cameras';
		addScope.NestboxLevel = 'Nestboxes';
		addScope.ToolLevel = 'Tools';

		ProductService.getProduct($routeParams.itemId).then(function(data) {
			$scope.product = data;		
		//	var type = levels[$scope.product.breadcrumbs[0]] || 'other';
			//console.log('JPFTEST AddToBasketController type',type, type.name);
			BasketService.addProduct($scope.product.key, $scope.product.name, $scope.product.price, $scope.product.size,
					$scope.product.imageCart, $scope.product.weight, $scope.product.quantity, $scope.product.breadcrumbs[0]);
			$scope.basket = BasketService.getTotals();

			var types = BasketService.getTypes();
			console.log('JPFTEST AddToBasketController types',types);
			var level = 'Nestboxes';
			console.log('JPFTEST AddToBasketController level ', types.indexOf(addScope.CameraLevel) >= 0,
					types.indexOf(addScope.NestboxLevel) >= 0 ,(types.indexOf(addScope.CameraLevel) >= 0 && types.indexOf(addScope.NestboxLevel) >= 0) );
			if (types.indexOf(addScope.CameraLevel) >= 0 && types.indexOf(addScope.NestboxLevel) >= 0) {
				level = addScope.ToolLevel;
			} else if (types.indexOf(addScope.NestboxLevel) >= 0) {
				// TODO update with Nestbox camera kits;
				level = addScope.CameraLevel;
			} else if (types.indexOf(addScope.CameraLevel) > 0) {
				level = addScope.NestboxLevel;
			} 
			console.log('JPFTEST AddToBasketController level ', level );
			ProductService.getProducts(level).then(function(data) {
		        // TODO Nice to be ordered somehow
				$scope.recommended = data.splice(0,8);
				console.log('JPFTEST AddToBasketController $scope.recommended  ', $scope.recommended  );
			});
		
		});
	} ]);

	app.controller('BasketController', [ '$scope', 'BasketService', 'DeliveryService', function($scope, BasketService, DeliveryService) {

		var updateBasket = function() {
			$scope.basketTotals = BasketService.getTotals();
			$scope.basketItems = BasketService.getProducts();
			var subtotal = $scope.basketTotals.subtotal * 100;
			var bigCount = $scope.basketTotals.bigCount;
			var weightTotal = $scope.basketTotals.weight;
			console.log("JPFTEST BasketController basketTotals", $scope.basketTotals);
			$scope.delivery = {};
			$scope.delivery.firstClass = DeliveryService.getFirstClassCharge(weightTotal, bigCount);
			$scope.delivery.nextDay = DeliveryService.getNextDayCharge(weightTotal, bigCount);
			$scope.delivery.economy = DeliveryService.getEconomyCharge(weightTotal, subtotal, bigCount);
			$scope.delivery.standard = DeliveryService.getStandardCharge(weightTotal, subtotal, bigCount);
			$scope.delivery.pickUp = DeliveryService.getPickupCharge(weightTotal, subtotal, bigCount);
			console.log("JPFTEST BasketController delivery", $scope.delivery);
		};

		updateBasket();

		$scope.addItem = function(itemId) {
			console.log("JPFTEST addItem", itemId);
			BasketService.addToExistingProduct(itemId);
			updateBasket();
		};

		$scope.removeItem = function(itemId) {
			console.log("JPFTEST removeItem", itemId);
			BasketService.removeProduct(itemId);
			updateBasket();
		};

		$scope.deleteItem = function(itemId) {
			console.log("JPFTEST deleteItem", itemId);
			BasketService.deleteProduct(itemId);
			updateBasket();
		};

	} ]);

	var getBasketItems = function() {
		if ($cookieStore.get('cookiesAccepted') === 'Accepted') {
			if (!($cookieStore.get(this.itemsCookie) instanceof Array)) {
				$cookieStore.put(this.itemsCookie, basketItemsNc);
			}
			return basketItems = $cookieStore.get(this.itemsCookie);
		} else {
			return basketItems = basketItemsNc;
		}
	};

	app.factory("BasketService", [ '$cookieStore', function($cookieStore) {
		this.itemsCookie = 'basketCookie';
		var basketItemsNc = [];
		return {
			addToExistingProduct : function(key) {

				var usingCookies = $cookieStore.get('cookiesAccepted') === 'Accepted';
				if (usingCookies) {
					if (!($cookieStore.get(this.itemsCookie) instanceof Array)) {
						$cookieStore.put(this.itemsCookie, basketItemsNc);
					}
					var basketItems = $cookieStore.get(this.itemsCookie);
				} else {
					var basketItems = basketItemsNc;
				}

				for (var i = 0; i < basketItems.length; i++) {
					if (basketItems[i].key == key) {
						basketItems[i].count++;
						break;
					}
				}

				if (usingCookies) {
					$cookieStore.put(this.itemsCookie, basketItems);
					console.log("JPFTEST addToExistingProduct ", $cookieStore.get(this.itemsCookie));
				}
			},
			addProduct : function(key, name, price, size, img, weight, quantity, type) {

				var usingCookies = $cookieStore.get('cookiesAccepted') === 'Accepted';
				if (usingCookies) {
					if (!($cookieStore.get(this.itemsCookie) instanceof Array)) {
						$cookieStore.put(this.itemsCookie, basketItemsNc);
					}
					var basketItems = $cookieStore.get(this.itemsCookie);
				} else {
					var basketItems = basketItemsNc;
				}

				var addedToExistingItem = false;
				for (var i = 0; i < basketItems.length; i++) {
					if (basketItems[i].key == key) {
						basketItems[i].count++;
						addedToExistingItem = true;
						break;
					}
				}
				if (!addedToExistingItem) {
					basketItems.push({
						count : 1,
						key : key,
						price : price,
						name : name,
						size : size,
						image : img,
						weight : weight,
						maxQuantity : quantity,
						type : type
					});
				}

				if (usingCookies) {
					$cookieStore.put(this.itemsCookie, basketItems);
					console.log("JPFTEST addProduct ", $cookieStore.get(this.itemsCookie));
				}
			},
			removeProduct : function(key) {
				var usingCookies = $cookieStore.get('cookiesAccepted') === 'Accepted';
				if (usingCookies) {
					if (!($cookieStore.get(this.itemsCookie) instanceof Array)) {
						$cookieStore.put(this.itemsCookie, basketItemsNc);
					}
					var basketItems = $cookieStore.get(this.itemsCookie);
				} else {
					var basketItems = basketItemsNc;
				}

				var deletedItem = {}
				for (var i = 0; i < basketItems.length; i++) {
					if (basketItems[i].key === key) {
						if (basketItems[i].count === 1) {
							deletedItem = basketItems[i];
							basketItems.splice(i, 1);
						} else {
							basketItems[i].count--;
						}
						break;
					}
				}

				if (usingCookies) {
					$cookieStore.put(this.itemsCookie, basketItems);
					console.log("JPFTEST removeProduct ", $cookieStore.get(this.itemsCookie));
				}

				return deletedItem;
			},
			deleteProduct : function(key) {
				var usingCookies = $cookieStore.get('cookiesAccepted') === 'Accepted';
				if (usingCookies) {
					if (!($cookieStore.get(this.itemsCookie) instanceof Array)) {
						$cookieStore.put(this.itemsCookie, basketItemsNc);
					}
					var basketItems = $cookieStore.get(this.itemsCookie);
				} else {
					var basketItems = basketItemsNc;
				}

				for (var i = 0; i < basketItems.length; i++) {
					if (basketItems[i].key === key) {
						basketItems.splice(i, 1);
						break;
					}
				}

				if (usingCookies) {
					$cookieStore.put(this.itemsCookie, basketItems);
					console.log("JPFTEST deleteProduct ", $cookieStore.get(this.itemsCookie));
				}
			},
			getProducts : function() {
				var usingCookies = $cookieStore.get('cookiesAccepted') === 'Accepted';
				if (usingCookies) {
					if (!($cookieStore.get(this.itemsCookie) instanceof Array)) {
						$cookieStore.put(this.itemsCookie, basketItemsNc);
					}
					var basketItems = $cookieStore.get(this.itemsCookie);
				} else {
					var basketItems = basketItemsNc;
				}
				return basketItems;
			},
			getTotals : function() {

				var usingCookies = $cookieStore.get('cookiesAccepted') === 'Accepted';
				if (usingCookies) {
					if (!($cookieStore.get(this.itemsCookie) instanceof Array)) {
						$cookieStore.put(this.itemsCookie, basketItemsNc);
					}
					var basketItems = $cookieStore.get(this.itemsCookie);
				} else {
					var basketItems = basketItemsNc;
				}

				var basketTotals = {};
				basketTotals.subtotal = 0;
				basketTotals.quantity = 0;
				basketTotals.bigCount = 0;
				basketTotals.weight = 0;

				for (var i = 0; i < basketItems.length; i++) {
					var item = basketItems[i];
					basketTotals.subtotal += item.price * item.count;
					basketTotals.quantity += item.count;
					basketTotals.bigCount += (item.size === "large") ? item.count : 0;
					basketTotals.weight += item.weight * item.count;
				}
				return basketTotals;
			},
			getTypes : function() {
				var usingCookies = $cookieStore.get('cookiesAccepted') === 'Accepted';
				if (usingCookies) {
					if (!($cookieStore.get(this.itemsCookie) instanceof Array)) {
						$cookieStore.put(this.itemsCookie, basketItemsNc);
					}
					var basketItems = $cookieStore.get(this.itemsCookie);
				} else {
					var basketItems = basketItemsNc;
				}

				var types = [];
				for (var i = 0; i < basketItems.length; i++) {
					var type = basketItems[i].type;
					if (types.indexOf(type) < 0) {
						types.push(basketItems[i].type);
					}
				}
				return types;

			}
		}
	} ]);

})();
