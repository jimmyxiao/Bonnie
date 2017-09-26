app.controller('columnController', function ($rootScope, $scope, $window , $location, $http, $filter, $state, $modal, util, worksService) {
		$rootScope.title = '我的畫作 | BonnieDRAW';
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
			params.rc = 4; 
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					$rootScope.maxPagination = data.maxPagination;
					$rootScope.initalPaginList();
					$scope.mainSectionArr = data.workList;
				}
			})
		}
		$scope.queryMyWorks();


		// max size 12
		// $scope.mainSectionArr = [
		// 	{
		// 		img:'assets/images/latest-vid-img-1.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/latest-vid-img-2.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/latest-vid-img-4.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/latest-vid-img-2.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/latest-vid-img-3.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/latest-vid-img-2.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/latest-vid-img-2.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/latest-vid-img-3.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/latest-vid-img-1.jpg',
		// 		text:{
		// 			title:'favorite_border',
		// 			like:'57689',
		// 			author:'Jhon Doe',
		// 			description:'Super Hero of Kids'
		// 		}	
		// 	},
		// 	{
		// 		img:'assets/images/latest-vid-img-2.jpg',
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