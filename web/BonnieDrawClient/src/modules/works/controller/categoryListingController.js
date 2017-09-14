app.controller('categoryListingController', function ($rootScope,$scope,$window ,$location, $http,$filter,$state,$modal) {
		$rootScope.title = '畫作類別 | BonnieDRAW';
		$('#loader-container').fadeOut("slow");
		new WOW().init();

		// max size 9
		$scope.mainSectionArr = [
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
				img:'assets/images/latest-vid-img-3.jpg',
				text:{
					title:'favorite_border',
					like:'57689',
					author:'Jhon Doe',
					description:'Super Hero of Kids'
				}	
			},
			{
				img:'assets/images/latest-vid-img-16.jpg',
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
				img:'assets/images/latest-vid-img-15.jpg',
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