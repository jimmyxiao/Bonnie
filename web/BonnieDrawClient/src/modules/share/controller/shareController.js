app.controller('headerMenuController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, generalService) {
		var param = util.getInitalScope();
		param.categoryId = 0;
		$scope.categoryList =[];
		$scope.getCategoryList = function(){
			generalService.getCategoryList(param, function(data, status, headers, config) {
	        	$scope.categoryList = data.categoryList;
			})
		}
		$scope.getCategoryList();

		$scope.clickCategory = function(data){
			
		}


	}
)

app.controller('footerController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, generalService) {
		var param = util.getInitalScope();
		param.dictionaryType = 1;
		param.dictionaryID = 0;
		$scope.dictionaryList =[];
		$scope.getDictionaryList = function(){
			generalService.getDictionaryList(param, function(data, status, headers, config) {
	        	$scope.dictionaryList = data.dictionaryList;
			})
		}
		$scope.getDictionaryList();
	}
)