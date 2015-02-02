'use strict';

/* Directives */

(function() {
	var app = angular.module('esnApp.products', []);

	app.controller('BuildController', function() {
		//NOOP
	});

	app.controller('StoreFrontController', [ '$scope', function($scope) {
		$scope.types = types;
	} ]);

	app.controller('ProductsController', [ '$scope', 'ProductService', '$routeParams', function($scope, ProductService, $routeParams) {
		$scope.productLevel = $routeParams.productLevel;
		ProductService.getProducts($scope.productLevel).then(function(data) {
			$scope.products = data;
			console.log("JPFTEST JPFTEST ProductsController $scope.products ", $scope.products);
			$scope.gridView = true;
			$scope.toggleView = function() {
				if ($scope.gridView) {
					$scope.gridView = false;
				} else {
					$scope.gridView = true;
				}
			};
		});

	} ]);

	app.controller('ProductController', [ '$scope', 'ProductService', '$routeParams', function($scope, ProductService, $routeParams) {
		ProductService.getProduct($routeParams.itemId).then(function(data) {
			$scope.product = data;
		});
	} ]);

	app.directive('quantityWarning', function() {
		return {
			restrict : 'E',
			templateUrl : 'views/quantity_warning.html'
		};
	});

	app.factory('ProductService', [ '$http', function($http) {
		return {
			getProducts : function(productLevel) {

				//						http://localhost:8080/esn/rest/products?menuComponents=[]
				//						[{
				//							"key":1,
				//							"name":"Classic Nestbox (25mm Entrance Hole)",
				//							"shortDescription":"Designed to house Blue, Coal and Marsh tits.",
				//							"price":5500,
				//							"image":"/products/nestboxes/classic_nestboxes/Classic _Nestbox_(25mm_Entrance_Hole)/Classic _Nestbox_25a.JPG",
				//							"breadcrumbs":["Nestboxes","Classic Nestboxes"]
				//						}...
				//						]
				//						
				//						{
				//							key : 1,
				//							name : 'Classic Nestbox (25mm Entrance Hole)',
				//							shortDescription : "Designed to house Blue, Coal and Marsh tits",
				//							price : 55.00,
				//							image : "images/photo_placeholder_200.jpg",
				//							imageList : "images/photo_placeholder_100.jpg",
				//							quantity : 5,
				//							breadcrumb : [ "Home" ]
				//						},
				//TODO 
				// Quantity / numberInStock - thought we needed this to see if in stock (even though changing the messages)
				// imageList - size 100 according to Pete's new way of doing the pics, so they appear better in a list (eg show more then 2 rows)
				this.url = '/esn/rest/products?menuComponents=["' + productLevel + '"]';
				return $http.get(this.url).then(function(result) {
					console.log("JPFTEST ProductService under ", productLevel, '=', result.data);
					return result.data;
				});
			},
			getProduct : function(productId) {
         		//						http://localhost:8080/esn/rest/product/3
				//						{
				//							"name":"Classic Nestbox (28mm Entrance Hole)",
				//							"shortDescription":"Designed to house Great tits, Tree sparrows and Pied flycatchers.",
				//							"longDescription":"This nest box has been designed to house and only to house these birds by RSPB guidelines. " +
				//									"It's design has been improved by giving it an apex roof so rainwater bounces to the side instead of in front of the nestbox, " +
				//									"it has a textured floor so birds do not slide around when trying to stand in it making nest building faster, there are ladder " +
				//									"grooves beneath the entrance to make it easier for birds to leave the nestbox and the nestbox has been made to accept a camera " +
				//									"so that you can watch the birds.",
				//							"notes":"This nestbox does not come with a camera. If you would like one please look under " +
				//									"the camera category. Applying and reapplying a waterproof low VOC woodstain will prolong the life of this nest box. " +
				//									"This nestbox comes with stainless steel screws to mount it to a brick/stone wall or wooden post that is in good condition. " +
				//									"Also comes with wire and padding to mount on a tree. Other mounting options are available in the nestbox accessories category.",
				//							"spec":"Manufacturer: Eye Spy Nature\nDimensions: W230 x D170 x H357mm\nMaterials: Finnish spruce plywood, Stainless steel, Zinc plated steel, " +
				//									"Electro brass plated steel\nContents: Nestbox, Two washers, three screws, two wall plugs, Galvernised steel wire, 4 Clear plastic tubes",
				//							"popularity":0,
				//							"large":true,
				//							"weight":1600,
				//							"price":5500,
				//							"numberInStock":1,
				//							"numberReserved":0,
				//							"videos":[],
				//							"images":["/products/nestboxes/classic_nestboxes/Classic _Nestbox_(28mm_Entrance_Hole)/Classic _Nestbox_28a.JPG"],
				//							"breadcrumbs":["Nestboxes","Classic Nestboxes"]
				//						}
				//
				//						{
				//							key : 17,
				//							name : 'Pretreated Classic Nestbox (32mm Entrance Hole)',
				//							summary : "Designed to house House sparrows, Nuthatches and Lesser spotted woodpeckers",
				//							description : [ "This nest box has been designed to house House sparrows, Nuthatches and Lesser spotted woodpeckers and only to house these birds by RSPB guidelines. It's design has been improved by giving it an apex roof so rainwater bounces to the side instead of in front of the nestbox, it has a textured floor so birds do not slide around when trying to stand in it making nest building faster, there are ladder grooves beneath the entrance to make it easier for birds to leave the nestbox and the nestbox has been made to accept a camera so that you can watch the birds. This box has been pretreated with a antique pine finish to make it look good and last longer in the british weather." ],
				//							information : [ "This nestbox does not come with a camera. If you would like one please look nder the camera category. Reapplying the waterproof low VOC woodstain will prolong the life of this nest box. This nestbox comes with stainless steel screws to mount it to a brick/stone wall or wooden post that is in good condition. Also comes with wire and padding to mount on a tree. Other mounting options are available in the nestbox accessories category." ],
				//							specification : [
				//									"Manufacturer: Eye Spy Nature",
				//									"Dimensions: W230 x D170 x H357mm",
				//									"Materials: Finnish spruce plywood, Stainless steel, Zinc plated steel, Electro brass plated steel, Low Vox water based woodstain",
				//									"Contents: Nestbox, Two washers, three screws, two wall plugs, Galvernised steel wire, 4 Clear plastic tubes" ],
				//							price : 75.00,
				//							image : "images/Pretreated_Classic_Nestbox_32a_320.jpg",
				//							imageCart : "images/Pretreated_Classic_Nestbox_32a_100.jpg",
				//							quantity : 3,
				//							size : "large",
				//							weight : 1600,
				//							breadcrumb : [ "Home", "Nestboxes", "Classic Nestboxes",
				//									"Classic Nestbox (32mm Entrance Hole)" ]
				//						},
				//TODO 
				//
				//longDescription needs to be an array for formatting
				//notes needs to be an array for formatting
				//spec needs to be an array for formatting - currently split using \n, maybe do this in above
				//needs key or I can add
				//
				this.url = '/esn/rest/product/' + productId;
				return  $http.get(this.url).then(function(result) {
					console.log("JPFTEST ProductService", productId, '=', result.data);
					result.data.longDescription = [result.data.longDescription];
					result.data.notes = [result.data.notes];
					result.data.spec = result.data.spec.split('\n');
					result.data.key = productId;
					return result.data;
				});

			}
		};
	} ]);

})();
