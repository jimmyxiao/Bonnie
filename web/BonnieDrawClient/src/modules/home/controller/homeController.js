app.controller('homeController', function ($rootScope,$scope,$window ,$location, $http,$filter,$state,$modal) {
		$rootScope.title = 'BonnieDRAW';
		$('#loader-container').fadeOut("slow");
		new WOW().init();

		$scope.mainSectionArr_smallgrid = [
			{
				delay:'0.5s',
				like:'57689',
				imgMds:'assets/images/main-vid-image-mds-1.jpg',
				imgSm:'assets/images/main-vid-image-sm-1.jpg',
				author:'admin',
				description:'Awesome Film Performance'
			},
			{
				delay:'0.1s',
				like:'57689',
				imgMds:'assets/images/main-vid-image-mds-2.jpg',
				imgSm:'assets/images/main-vid-image-sm-2.jpg',
				author:'admin',
				description:'Awesome Film Performance'
			},
			{
				delay:'0.2s',
				like:'57689',
				imgMds:'assets/images/main-vid-image-mds-3.jpg',
				imgSm:'assets/images/main-vid-image-sm-3.jpg',
				author:'admin',
				description:'Awesome Film Performance'
			},
			{
				delay:'0.3s',
				like:'57689',
				imgMds:'assets/images/main-vid-image-mds-4.jpg',
				imgSm:'assets/images/main-vid-image-sm-4.jpg',
				author:'admin',
				description:'Awesome Film Performance'
			}
		]

		// max size 9 
		$scope.secondarySectionArr_new = [
			{
				img:'assets/images/latest-vid-img-1.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/latest-vid-img-2.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/latest-vid-img-3.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/latest-vid-img-4.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/latest-vid-img-5.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/latest-vid-img-6.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/latest-vid-img-7.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/latest-vid-img-8.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/latest-vid-img-9.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			}
		]

		// max size 6 
		$scope.secondarySectionArr_popular = [
			{
				img:'assets/images/latest-vid-img-10.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/latest-vid-img-11.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/latest-vid-img-12.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/latest-vid-img-9.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/latest-vid-img-4.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/latest-vid-img-15.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			}
		]

		$scope.secondarySectionArr_userLike = [
			{
				img:'assets/images/most-liked-img-s1.jpg',
				title:'Journey',
				comment:'10',
				view:'534'
			},
			{
				img:'assets/images/most-liked-img-s2.jpg',
				title:'Magic',
				comment:'10',
				view:'534'
			},
			{
				img:'assets/images/most-liked-img-s3.jpg',
				title:'Runner',
				comment:'10',
				view:'534'
			},
			{
				img:'assets/images/most-liked-img-s3.jpg',
				title:'Runner',
				comment:'10',
				view:'534'
			},
			{
				img:'assets/images/most-liked-img-s4.jpg',
				title:'Fantasy',
				comment:'10',
				view:'534'
			}
		]

		$scope.secondarySectionArr_moreCreation = [
			{
				img:'Human Rights Violation',
				title:'Journey',
				comment:'10',
				view:'534'
			},
			{
				img:'assets/images/most-viewd-2.jpg',
				title:'War Video Compilation',
				comment:'10',
				view:'534'
			}
		]

		$scope.secondarySectionArr_keyword =['3D','Animals & Birds','HD','Horror','Art','Self','HD Songs','Comedy'];

	}
)

