app.controller('columnController', function ($rootScope, $scope, $window , $location, $http, $filter, $state, $modal, util, worksService) {
		$rootScope.title = 'TITLE.t05_03_my_drawings';
		$rootScope.nav ='column';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
		$rootScope.maxPagination = 0;
		$rootScope.paginList =[];
		$scope.clickPagin = function(pagin){
			$rootScope.offset = pagin;	
			$scope.queryMyWorks();
		}
		$scope.movePagin = function(offset){
			$rootScope.offset = $rootScope.offset + offset;
			$scope.queryMyWorks();
		}

		$scope.queryMyWorks = function(){
			$scope.mainSectionArr = [];
			var params = util.getInitalScope();
			params.wid = 0;
			params.wt = 5;
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
		$scope.queryMyWorks();
	}
)