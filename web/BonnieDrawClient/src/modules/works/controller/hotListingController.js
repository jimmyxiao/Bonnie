app.controller('hotListingController', function ($rootScope, $scope, $window ,$location, $http, $filter, $state, $modal, util, worksService) {
		$rootScope.title = '熱門畫作 | BonnieDRAW';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
		$scope.offset = 1;
		$scope.maxPagination = 1;
		$scope.paginList =[];
		$scope.clickPagin = function(pagin){
			$scope.offset = pagin;
			$scope.queryPopularWorks();
		}
		$scope.movePagin = function(offset){
			$scope.offset = $scope.offset + offset;
			$scope.queryPopularWorks();
		}
		$scope.initalPaginList = function(){
			// show number of pages 
			var paginNum = 5;
			if(paginNum<=$scope.maxPagination){
				if(($scope.maxPagination - ($scope.offset-1)) >= paginNum){
					$scope.paginList =[];
					$scope.paginList = $filter('range')($scope.paginList, $scope.offset, $scope.maxPagination);
					if($scope.paginList.length > paginNum){
						$scope.paginList = $filter('limitTo')($scope.paginList, paginNum);
					}
				}
			}else{
				$scope.paginList =[];
				$scope.paginList = $filter('range')($scope.paginList, 1, $scope.maxPagination);
			}
		}

		$scope.queryPopularWorks = function(){
			$scope.mainSectionArr = [];
			var params = util.getInitalScope();
			params.wid = 0;
			params.wt = 2;
			params.stn = $scope.offset;
			params.rc = 4; 
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					$scope.maxPagination = data.maxPagination;
					$scope.initalPaginList();
					$scope.mainSectionArr = data.workList;
				}
			})
		}
		$scope.clickPagin(1);

		// max size 12
		// $scope.mainSectionArr = [
		// 	{
		// 		img:'assets/images/2-column-vid-img_1.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/2-column-vid-img_2.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/2-column-vid-img_4.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/2-column-vid-img_2.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/2-column-vid-img_3.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/2-column-vid-img_2.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/2-column-vid-img_2.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/2-column-vid-img_3.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/2-column-vid-img_1.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/2-column-vid-img_2.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/latest-vid-img-9.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/latest-vid-img-12.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	}
		// ]

	}
)