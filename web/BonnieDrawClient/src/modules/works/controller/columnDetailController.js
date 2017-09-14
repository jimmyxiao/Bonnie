app.controller('columnDetailController', function ($rootScope,$scope,$window ,$location, $http,$filter,$state,$modal) {
		$rootScope.title = 'demo畫作 | BonnieDRAW';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
	}
)