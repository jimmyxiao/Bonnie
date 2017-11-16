app.controller('searchListingController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, worksService) {
		$rootScope.title = '搜尋 | BonnieDRAW';
		$rootScope.nav = '';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
		$scope.type = $state.params.result;

		if(util.isEmpty($scope.type)){
			return;
		}

		$rootScope.maxPagination = 0;
		$rootScope.paginList =[];
		$scope.clickPagin = function(pagin){
			$rootScope.offset = pagin;
			$scope.querySearchWorks();
		}
		$scope.movePagin = function(offset){
			$rootScope.offset = $rootScope.offset + offset;
			$scope.querySearchWorks();
		}

		$scope.querySearchWorks = function(type){
			$scope.mainSectionArr = [];
			var params = util.getInitalScope();
			params.wid = 0;
			params.wt = 9;
			params.stn = $scope.offset;
			params.search = type;
			params.rc = 9; 
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					$rootScope.maxPagination = data.maxPagination;
					$rootScope.initalPaginList();
					$scope.mainSectionArr = data.workList;
				}
			})
		}
		$scope.querySearchWorks($scope.type);

		$scope.clickWorksLike = function(data){
			var params = util.getInitalScope();
			if(data.like){
				params.fn = 0;
			}else{
				params.fn = 1;
			}
			params.worksId = data.worksId;
			params.likeType = 1; 
			worksService.setLike(params,function(data, status, headers, config){
				if(data.res == 1){
					$scope.queryPopularWorks();
				}
			})
		}

	}
)