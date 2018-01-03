app.controller('hotListingController', function ($rootScope, $scope, $window ,$location, $http, $filter, $state, $modal, util, worksService) {
		$rootScope.title = 'TITLE.t05_05_popular';
		$rootScope.nav = 'hot';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
		$rootScope.maxPagination = 0;
		$rootScope.paginList =[];
		$scope.clickPagin = function(pagin){
			$rootScope.offset = pagin;
			$scope.queryPopularWorks();
		}
		$scope.movePagin = function(offset){
			$rootScope.offset = $rootScope.offset + offset;
			$scope.queryPopularWorks();
		}

		$scope.queryPopularWorks = function(){
			$scope.mainSectionArr = [];
			var params = util.getInitalScope();
			params.wid = 0;
			params.wt = 2;
			params.stn = $scope.offset;
			params.rc = 12; 
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					$rootScope.maxPagination = data.maxPagination;
					$rootScope.initalPaginList();
					$scope.mainSectionArr = data.workList;
				}
			})
		}
		$scope.queryPopularWorks();

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