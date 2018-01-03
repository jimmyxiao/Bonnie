app.controller('trackListController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, worksService) {
		$rootScope.title = 'TITLE.t04_03_follow_sheet';
		$rootScope.nav = 'track';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
		$rootScope.maxPagination = 0;
		$rootScope.paginList =[];
		$scope.clickPagin = function(pagin){
			$rootScope.offset = pagin;
			$scope.queryTrackWorks();
		}
		$scope.movePagin = function(offset){
			$rootScope.offset = $rootScope.offset + offset;
			$scope.queryTrackWorks();
		}

		$scope.queryTrackWorks = function(){
			$scope.mainSectionArr = [];
			var params = util.getInitalScope();
			params.wid = 0;
			params.wt = 1;
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
		$scope.queryTrackWorks();

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
					$scope.queryTrackWorks();
				}
			})
		}


	}
)