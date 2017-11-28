app.controller('myfileController', function ($cookieStore, $rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, userService, worksService) {
		$rootScope.title = '我的帳戶 | BonnieDRAW';
		$rootScope.nav = '';
		$('#loader-container').fadeOut("slow");
		$scope.workclick =true;//works,Collection follow,fans div切換
		$scope.usernav='w';
		new WOW().init();
		$rootScope.maxPagination = 0;
		$rootScope.paginList =[];
		$scope.clickPagin = function(pagin){
			$rootScope.offset = pagin;
			callSwitchFunction($scope.usernav);
		}
		$scope.movePagin = function(offset){
			$rootScope.offset = $rootScope.offset + offset;
			callSwitchFunction($scope.usernav);
		}

		function callSwitchFunction(tag){
			if($cookieStore.get('offset') > 1){
				rootScope.offset = $cookieStore.get('offset');
			}else{
				$rootScope.offset = 1;
			}
			switch(tag){
			case 'w':
				$scope.queryMyWorks();
				break;	
			case 'fa':
				$scope.fansWorks();
				break;
			case 'fo':
				$scope.followWorks();
				break;
			case 'c':
				$scope.queryCollectionWorks();
				break;
			}
		}

		$scope.switchTab = function(tag){
			if($scope.usernav != tag){
				$scope.usernav = tag;
				$cookieStore.put('offset',0);
				$rootScope.maxPagination = 0;
				$rootScope.paginList =[];
				callSwitchFunction(tag);
			}
		}

		$scope.queryMyWorks = function(){
			$scope.dataList = [];
			var params = util.getInitalScope();
			params.wid = 0;
			params.wt = 5;
			params.stn = $scope.offset;
			params.rc = 16; 
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					$rootScope.maxPagination = data.maxPagination;
					$rootScope.initalPaginList();
					$scope.dataList = data.workList;
				}
			})
		}

		$scope.queryCollectionWorks = function(){
			$scope.dataList = [];
			var params = util.getInitalScope();
			params.wid = 0;
			params.wt = 7;
			params.stn = $scope.offset;
			params.rc = 16; 
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					$rootScope.maxPagination = data.maxPagination;
					$rootScope.initalPaginList();
					$scope.dataList = data.workList;
				}
			})
		}

		$scope.followWorks = function() {
			$scope.dataList = [];
			var params = util.getInitalScope();
			params.stn = $scope.offset;
			params.rc = 12; 
			params.fn = 1;
			worksService.queryfollowingList(params,function(data, status, headers, config){
				if(data.res == 1){
					$rootScope.maxPagination = data.maxPagination;
					$rootScope.initalPaginList();
					$scope.dataList = data.userList;
				}
			})
		}

		$scope.fansWorks = function(){
			$scope.dataList = [];
			var params = util.getInitalScope();
			params.stn = $scope.offset;
			params.rc = 12; 
			params.fn = 2;
			worksService.queryfollowingList(params,function(data, status, headers, config){
				if(data.res == 1){
					$rootScope.maxPagination = data.maxPagination;
					$rootScope.initalPaginList();
					$scope.dataList = data.userList;
				}
			})
		}
		
		$scope.clickFollow = function(data){
			var params = util.getInitalScope();
			params.followingUserId = data.userId;
			if(data.following){
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

		$scope.queryUser = function(){
			var params = util.getInitalScope();
			params.type = 0;
			userService.userInfoQuery(params,function(data, status, headers, config){
				if(data.res == 1){
					$scope.user = data;
					callSwitchFunction($scope.usernav);
				}
			})
		}
		$scope.queryUser();

	}
)