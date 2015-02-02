'use strict';

(function() {
	var app = angular.module('esnApp', [ 'ngRoute', 'esnApp.shop',
			'esnApp.basket', 'esnApp.products', 'esnApp.cookies' ]);

	app.config([ '$routeProvider', '$locationProvider',
			function($routeProvider) {

				$routeProvider.when('/Home', {
					templateUrl : 'views/home.html',
					controller : 'StoreController'
				}).when('/Build', {
					templateUrl : 'views/build.html',
					controller : 'BuildController'
				}).when('/About', {
					templateUrl : 'views/about.html',
					controller : 'StoreController'
				}).when('/Contact', {
					templateUrl : 'views/contact.html',
					controller : 'StoreController'
				}).when('/Privacy', {
					templateUrl : 'views/privacy.html',
					controller : 'StoreController'
				}).when('/Delivery', {
					templateUrl : 'views/delivery.html',
					controller : 'StoreController'
				}).when('/Shop', {
					templateUrl : 'views/shop.html',
					controller : 'StoreFrontController'
				}).when('/Shop/:productLevel', {
					templateUrl : 'views/products.html',
					controller : 'ProductsController'
				}).when('/Item/:itemId', {
					templateUrl : 'views/product.html',
					controller : 'ProductController'
				}).when('/Add/:itemId', {
					templateUrl : 'views/add.html',
					controller : 'AddToBasketController'
				}).when('/Basket', {
					templateUrl : 'views/basket.html',
					controller : 'BasketController'
				}).otherwise({
					redirectTo : '/Home'
				});
			} ]);

})();
