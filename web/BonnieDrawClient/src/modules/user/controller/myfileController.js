app.controller('myfileController', function ($cookieStore, $rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, userService, worksService) {
		$rootScope.title = '我的帳戶 | BonnieDRAW';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
		
		$scope.queryUser = function(){
			var params = util.getInitalScope();
			params.type = 0;
			userService.userInfoQuery(params,function(data, status, headers, config){
				if(data.res == 1){
					$scope.user = data;
					$scope.queryMyWorks();
				}
			})
		}
		$scope.queryUser();

		$scope.queryMyWorks = function(){
			$scope.worksList = [];
			var params = util.getInitalScope();
			params.wid = 0;
			params.wt = 5;
			params.stn = $scope.offset;
			params.rc = 6; 
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					$scope.worksList = data.workList;
				}
			})
		}

	}
)