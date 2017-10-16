app.controller('columnDetailController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, worksService) {
		$rootScope.title = 'demo畫作 | BonnieDRAW';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
		var wid = $state.params.id;
		$scope.isShow = false;

		$scope.queryWorks = function(){
			$scope.mainSection = {};
			var params = util.getInitalScope();
			params.wid = wid;
			params.wt = 20; 
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					$scope.mainSection = data.work;
					$scope.isShow = true;
				}
			})
		}
		$scope.queryWorks();

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
					$scope.queryWorks();
				}
			})
		}

	}
)