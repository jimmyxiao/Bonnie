app.controller('myfileController', function ($cookieStore, $rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, userService, worksService) {
		$rootScope.title = '我的帳戶 | BonnieDRAW';
		$rootScope.nav = '';
		$('#loader-container').fadeOut("slow");
		$scope.workclick =true;//works,Collection follow,fans div切換
		$scope.usernav='';
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
		
		$scope.queryUser = function(){
			var params = util.getInitalScope();
			params.type = 0;
			userService.userInfoQuery(params,function(data, status, headers, config){
				if(data.res == 1){
					$scope.user = data;
					$scope.queryMyWorks();
					console.log(data);
				}
			})
		}
		$scope.queryUser();

		$scope.queryMyWorks = function(tag){
			if($scope.usernav!='w'){
				$scope.usernav = tag;
				$cookieStore.put('offset',0);
		 		if($cookieStore.get('offset') > 1){
					$rootScope.offset = $cookieStore.get('offset');
				}else{
					$rootScope.offset = 1;
				}
				$rootScope.maxPagination = 0;
				$rootScope.paginList =[];

				$scope.worksList = [];
				var params = util.getInitalScope();
				params.wid = 0;
				params.wt = 5;
				params.stn = $scope.offset;
				params.rc = 16; 
				worksService.queryWorksList(params,function(data, status, headers, config){
					if(data.res == 1){
						$rootScope.maxPagination = data.maxPagination;
						$rootScope.initalPaginList();
						$scope.worksList = data.workList;
						$scope.dataList = $scope.worksList;
						$scope.workclick =true;
					}
				})
			}
		}

		$scope.queryCollectionWorks = function(tag){
			if($scope.usernav!='fa'){
				$scope.usernav = tag;
				$cookieStore.put('offset',0);
		 		if($cookieStore.get('offset') > 1){
					$rootScope.offset = $cookieStore.get('offset');
				}else{
					$rootScope.offset = 1;
				}
				$rootScope.maxPagination = 0;
				$rootScope.paginList =[];

				$scope.mainSectionArr = [];
				var params = util.getInitalScope();
				params.wid = 0;
				params.wt = 7;
				params.stn = $scope.offset;
				params.rc = 16; 
				worksService.queryWorksList(params,function(data, status, headers, config){
					if(data.res == 1){
						$rootScope.maxPagination = data.maxPagination;
						$rootScope.initalPaginList();
						$scope.mainSectionArr = data.workList;
						$scope.dataList = $scope.mainSectionArr;
						$scope.workclick =true;
					}
				})
			}
		}
		$scope.followWorks = function(tag) {
			if($scope.usernav!='fo'){
				$scope.usernav = tag;
				$cookieStore.put('offset',0);
		 		if($cookieStore.get('offset') > 1){
					$rootScope.offset = $cookieStore.get('offset');
				}else{
					$rootScope.offset = 1;
				}
				$rootScope.maxPagination = 0;
				$rootScope.paginList =[];

				$scope.followList = [];
				var params = util.getInitalScope();
				params.stn = $scope.offset;
				params.rc = 12; 
				params.fn = 1;
				worksService.queryfollowingList(params,function(data, status, headers, config){
					if(data.res == 1){
						$rootScope.maxPagination = data.maxPagination;
						$rootScope.initalPaginList();
						$scope.followList = data.workList;
						$scope.dataList = $scope.followList;
						$scope.workclick =false;
					}
				})
			}
		}
		$scope.fansWorks = function(tag) {
			if($scope.usernav!='c'){
				$scope.usernav = tag;
				$cookieStore.put('offset',0);
		 		if($cookieStore.get('offset') > 1){
					$rootScope.offset = $cookieStore.get('offset');
				}else{
					$rootScope.offset = 1;
				}
				$rootScope.maxPagination = 0;
				$rootScope.paginList =[];

				$scope.fansList = [];
				var params = util.getInitalScope();
				params.stn = $scope.offset;
				params.rc = 12; 
				params.fn = 2;
				worksService.queryfollowingList(params,function(data, status, headers, config){
					if(data.res == 1){
						$rootScope.maxPagination = data.maxPagination;
						$rootScope.initalPaginList();
						$scope.fansList = data.workList;
						$scope.dataList = $scope.fansList;
						$scope.workclick =false;
					}
				})
			}
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
)