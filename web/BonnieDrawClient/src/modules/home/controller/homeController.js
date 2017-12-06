app.controller('homeController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, worksService) {
		$rootScope.title = 'BonnieDRAW';
		$rootScope.nav = '';
		$('#loader-container').fadeOut("slow");
		new WOW().init();

		$scope.popupShare = function(){
			
		}

		$scope.queryNewUploadWorks = function(){
			$scope.secondarySectionArr_new = [];
			var params = util.getInitalScope();
			params.wid = 0;
			params.wt = 4;
			params.stn = 0;
			params.rc = 6; 
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					$scope.secondarySectionArr_new = data.workList;
				}
			})
		}
		$scope.queryNewUploadWorks();

		$scope.queryPopularWorks = function(){
			$scope.secondarySectionArr_popular = [];
			$scope.secondarySectionArr_popular_first = [];
			var params = util.getInitalScope();
			params.wid = 0;
			params.wt = 2;
			params.stn = 0;
			params.rc = 5; 
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					for (var i = 0; i < data.workList.length; i++) {
						if(data.workList[i].likeCount==false ||data.workList[i].likeCount==""||data.workList[i].likeCount==null){
							data.workList[i].likeCount=0;
						}
					}
					$scope.secondarySectionArr_popular = data.workList;
					$scope.secondarySectionArr_popular_first = $scope.secondarySectionArr_popular.shift();
				}
			})
		}
		$scope.queryPopularWorks();

		$scope.clickWorksLike = function(index,tag){
			if(tag == 'p'){
				var data = $scope.secondarySectionArr_popular[index];
			}else if(tag == 'n'){
				var data = $scope.secondarySectionArr_new[index];
			}else{
				return;
			}
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
					$scope.queryNewUploadWorks();
					$scope.queryPopularWorks();
				}
			})
		}
		$scope.clickWorksLikeOne = function(tag){
			if(tag == 'p'){
				var data = $scope.secondarySectionArr_popular_first;
			}else{
				return;
			}
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
					$scope.queryNewUploadWorks();
					$scope.queryPopularWorks();
				}
			})
		}

		// $scope.mainSectionArr_mdgrid=[{}];
		// $scope.mainSectionArr_mdgrid ={
		// 	delay:'1s',
		// 	title:'favorite_border',
		// 	like:'57689',
		// 	imgXs:'assets/images/main-vid-image-md-1.jpg',
		// 	imgLg:'assets/images/main-vid-image-smmd-1.jpg',
		// 	author:'Admin',
		// 	description:'Gladiators Fighting'
		// }

		// $scope.mainSectionArr_smallgrid = [
		// 	{
		// 		delay:'0.5s',
		// 		like:'57689',
		// 		imgMds:'assets/images/main-vid-image-mds-1.jpg',
		// 		imgSm:'assets/images/main-vid-image-sm-1.jpg',
		// 		author:'admin',
		// 		description:'Awesome Film Performance'
		// 	},
		// 	{
		// 		delay:'0.1s',
		// 		like:'57689',
		// 		imgMds:'assets/images/main-vid-image-mds-2.jpg',
		// 		imgSm:'assets/images/main-vid-image-sm-2.jpg',
		// 		author:'admin',
		// 		description:'Awesome Film Performance'
		// 	},
		// 	{
		// 		delay:'0.2s',
		// 		like:'57689',
		// 		imgMds:'assets/images/main-vid-image-mds-3.jpg',
		// 		imgSm:'assets/images/main-vid-image-sm-3.jpg',
		// 		author:'admin',
		// 		description:'Awesome Film Performance'
		// 	},
		// 	{
		// 		delay:'0.3s',
		// 		like:'57689',
		// 		imgMds:'assets/images/main-vid-image-mds-4.jpg',
		// 		imgSm:'assets/images/main-vid-image-sm-4.jpg',
		// 		author:'admin',
		// 		description:'Awesome Film Performance'
		// 	}
		// ]

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

		 $scope.secondarySectionArr_keyword =['hello', 'foobar', 'Face'];

	}
)

