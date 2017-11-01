app.controller('headerMenuController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, generalService) {
		var param = util.getInitalScope();
		param.categoryId = 0;
		$scope.tagList =[];
		$scope.getTagList = function(){
			generalService.getTagList(param, function(data, status, headers, config) {
	        	$scope.tagList = data.tagList;
			})
		}
		$scope.getTagList();

		$scope.clickCategory = function(data){
			
		}

	}
)

app.controller('footerController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, generalService) {
		// var param = util.getInitalScope();
		var param = {};
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