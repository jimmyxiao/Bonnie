app.controller('categoryListingController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, worksService) {
		$rootScope.title = 'TITLE.t05_01_category';
		$rootScope.nav = 'category';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
		$scope.type = $state.params.type;

		if(util.isEmpty($scope.type)){
			return;
		}

		$rootScope.maxPagination = 0;
		$rootScope.paginList =[];
		$scope.clickPagin = function(pagin){
			$rootScope.offset = pagin;
			$scope.queryRelatedTagWorks();
		}
		$scope.movePagin = function(offset){
			$rootScope.offset = $rootScope.offset + offset;
			$scope.queryRelatedTagWorks();
		}

		$scope.queryRelatedTagWorks = function(type){
			$scope.mainSectionArr = [];
			var params = util.getInitalScope();
			params.wid = 0;
			params.wt = 8;
			params.stn = $scope.offset;
			params.tagName = type;
			params.rc = 12; 
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					$rootScope.maxPagination = data.maxPagination;
					$rootScope.initalPaginList();
					$scope.mainSectionArr = data.workList;
				}
			})
		}
		$scope.queryRelatedTagWorks($scope.type);

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
		
		 $scope.secondarySectionArr_userLike = [
			 	{
			 		img:'../bonniedraw_service/BDService/loadFile/files/19/png/029c10c3aa424da19b02aeb0f1eedb6c.png',
			 		title:'なおしま',
			 		comment:'10',
			 		view:'534',
			 		worksId:'19'
			 	},
			 	{
			 		img:'../bonniedraw_service/BDService/loadFile/files/20/png/b51441a82b154beaae0eda738d2ba4dc.png',
			 		title:'FiFi',
			 		comment:'10',
			 		view:'534',
			 		worksId:'20'
			 	},
			 	{
			 		img:'../bonniedraw_service/BDService/loadFile/files/23/png/dce333f9fba54dc1a71e10066072b6ce.png',
			 		title:'Bear',
			 		comment:'10',
			 		view:'534',
			 		worksId:'23'
			 	},
			 	{
			 		img:'../bonniedraw_service/BDService/loadFile/files/22/png/1f4f5b64ca95487599b0f934fa755517.png',
			 		title:'test',
			 		comment:'10',
			 		view:'534',
			 		worksId:'22'
			 	},
			 	{
			 		img:'../bonniedraw_service/BDService/loadFile/files/11/png/927334e1e0a34a5eb4b14345e3803376.png',
			 		title:'兔',
			 		comment:'10',
			 		view:'534',
			 		worksId:'11'
			 	}
			 ]

			 $scope.secondarySectionArr_moreCreation = [
			 	{
			 		img:'../bonniedraw_service/BDService/loadFile/files/23/png/dce333f9fba54dc1a71e10066072b6ce.png',
			 		title:'Bear',
			 		comment:'10',
			 		view:'534',
			 		worksId:'23'
			 	}
			 ]

			 $scope.secondarySectionArr_keyword =['3D','Animals & Birds','HD','Horror','Art','Self','HD Songs','Comedy'];


	}
)