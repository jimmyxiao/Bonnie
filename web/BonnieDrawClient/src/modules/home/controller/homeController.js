app.controller('homeController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, worksService) {
		$rootScope.title = 'BonnieDRAW';
		$('#loader-container').fadeOut("slow");
		new WOW().init();

		$scope.popupShare = function(){
			util.alert('223344');
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
			var params = util.getInitalScope();
			params.wid = 0;
			params.wt = 2;
			params.stn = 0;
			params.rc = 3; 
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					$scope.secondarySectionArr_popular = data.workList;
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

		// $scope.secondarySectionArr_userLike = [
		// 	{
		// 		img:'assets/images/most-liked-img-s1.jpg',
		// 		title:'Journey',
		// 		comment:'10',
		// 		view:'534'
		// 	},
		// 	{
		// 		img:'assets/images/most-liked-img-s2.jpg',
		// 		title:'Magic',
		// 		comment:'10',
		// 		view:'534'
		// 	},
		// 	{
		// 		img:'assets/images/most-liked-img-s3.jpg',
		// 		title:'Runner',
		// 		comment:'10',
		// 		view:'534'
		// 	},
		// 	{
		// 		img:'assets/images/most-liked-img-s3.jpg',
		// 		title:'Runner',
		// 		comment:'10',
		// 		view:'534'
		// 	},
		// 	{
		// 		img:'assets/images/most-liked-img-s4.jpg',
		// 		title:'Fantasy',
		// 		comment:'10',
		// 		view:'534'
		// 	}
		// ]

		// $scope.secondarySectionArr_moreCreation = [
		// 	{
		// 		img:'Human Rights Violation',
		// 		title:'Journey',
		// 		comment:'10',
		// 		view:'534'
		// 	},
		// 	{
		// 		img:'assets/images/most-viewd-2.jpg',
		// 		title:'War Video Compilation',
		// 		comment:'10',
		// 		view:'534'
		// 	}
		// ]

		// $scope.secondarySectionArr_keyword =['3D','Animals & Birds','HD','Horror','Art','Self','HD Songs','Comedy'];

	}
)

