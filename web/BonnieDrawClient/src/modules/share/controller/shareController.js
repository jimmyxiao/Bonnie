app.controller('headerMenuController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, generalService) {
		var param = util.getInitalScope();
		param.categoryId = 0;
		$scope.categoryList =[];
		$scope.getCategoryList = function(){
			generalService.getCategoryList(param, function(data, status, headers, config) {
	        	$scope.categoryList = data.categoryList;
	        	console.log($scope.categoryList);
			})
		}
		$scope.getCategoryList();
	}
)

app.controller('footerController', function ($rootScope,$scope,$window ,$location, $http,$filter,$state,$modal) {
		
	}
)