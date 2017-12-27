app.controller('userfileController', function ($cookieStore, $rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, userService, worksService) {
		$rootScope.title = '用戶的個人頁 | BonnieDRAW';
		$rootScope.nav = '';
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
		$scope.status = $state.params.id;

		if($scope.status==$rootScope.rg_gl.currentUser.ui){
			$state.go('myfile');
		}

		if(!util.isEmpty($scope.status) && $scope.status!=$rootScope.rg_gl.currentUser.ui){
			$scope.queryUser = function(){
				var params = util.getInitalScope();
				params.type = 1;
				params.queryId = $scope.status;
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
				params.wt = 6;
				params.stn = $scope.offset;
				params.rc = 6; 
				params.queryId = $scope.status;
				worksService.queryWorksList(params,function(data, status, headers, config){
					if(data.res == 1){
						$rootScope.maxPagination = data.maxPagination;
						$rootScope.initalPaginList();
						$scope.worksList = data.workList;
					}
				})
			}

			$scope.clickFollow = function(){
				var params = util.getInitalScope();
				params.followingUserId = $scope.status;
				if($scope.user.follow){
					params.fn = 0;
				}else{
					params.fn = 1;
				}
				userService.setFollowing(params,function(data, status, headers, config){
					if(data.res == 1){
						$scope.queryUser();
					}
				})
			}
		}

	}
)