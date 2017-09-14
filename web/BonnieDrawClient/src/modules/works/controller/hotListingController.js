app.controller('hotListingController', function ($rootScope,$scope,$window ,$location, $http,$filter,$state,$modal) {
		$rootScope.title = '熱門畫作 | BonnieDRAW';
		$('#loader-container').fadeOut("slow");
		new WOW().init();

		// max size 12
		$scope.mainSectionArr = [
			{
				img:'assets/images/2-column-vid-img_1.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/2-column-vid-img_2.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/2-column-vid-img_4.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/2-column-vid-img_2.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/2-column-vid-img_3.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/2-column-vid-img_2.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/2-column-vid-img_2.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/2-column-vid-img_3.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/2-column-vid-img_1.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/2-column-vid-img_2.jpg',
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
				img:'assets/images/latest-vid-img-12.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			}
		]

	}
)