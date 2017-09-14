app.controller('trackListController', function ($rootScope,$scope,$window ,$location, $http,$filter,$state,$modal) {
		$rootScope.title = '追蹤清單 | BonnieDRAW';
		$('#loader-container').fadeOut("slow");
		new WOW().init();

		// max size 12
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
				img:'assets/images/latest-vid-img-2.jpg',
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
				img:'assets/images/latest-vid-img-2.jpg',
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