app.controller('myfileController', function ($rootScope,$scope,$window ,$location, $http,$filter,$state,$modal) {
		$rootScope.title = '熱門畫作 | BonnieDRAW';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
	}
)