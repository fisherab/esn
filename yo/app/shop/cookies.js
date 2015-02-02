'use strict';

(function() {
	var app = angular.module('esnApp.cookies', [ 'ngCookies' ]);

	app.controller('CookieController', [ '$scope', '$cookies', '$cookieStore', function($scope, $cookies, $cookieStore) {

		this.accept = function() {		
			$cookieStore.put('cookiesAccepted', 'Accepted');
			console.log("accepted =", $cookieStore.get('cookiesAccepted'));
			this.setAcceptedCookies();
		};
		
		this.setAcceptedCookies = function() {
			var acceptedCookie = $cookieStore.get('cookiesAccepted');
			//TODO is scope needed here really as not using apart from showing banner
			$scope.hasAcceptedCookies = (acceptedCookie === 'Accepted') ;
			console.log("hasAcceptedCookies =", $scope.hasAcceptedCookies);
		}
		
	} ]);

})();