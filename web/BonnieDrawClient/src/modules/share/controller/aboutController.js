app.controller('aboutController', function ($rootScope,$scope,$window ,$location, $http,$filter,$state,$modal) {
		$rootScope.title = '關於我們 | BonnieDRW';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
	}
)