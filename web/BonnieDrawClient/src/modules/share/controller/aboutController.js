app.controller('aboutController', function ($rootScope,$scope,$window ,$location, $http,$filter,$state,$modal) {
		switch($state.current.name) {
			case "about":
				$rootScope.title = '關於我們 | BonnieDRW';
				break;
			case "privacy":
				$rootScope.title = '隱私權條款 | BonnieDRW';
				break;
			case "terms":
				$rootScope.title = '使用條款 | BonnieDRW';
				break;
			case "about_app":
				$rootScope.title = '關於我們 | BonnieDRW';
				break;
			case "privacy_app":
				$rootScope.title = '隱私權條款 | BonnieDRW';
				break;
			case "terms_app":
				$rootScope.title = '使用條款 | BonnieDRW';
				break;
		}

		$rootScope.nav = '';
		$('#loader-container').fadeOut("slow");
		new WOW().init();

		 $scope.secondarySectionArr_userLike = [
		 	{
		 		img:'../BonnieDrawService/BDService/loadFile/files/19/png/029c10c3aa424da19b02aeb0f1eedb6c.png',
		 		title:'なおしま',
		 		comment:'10',
		 		view:'534',
		 		worksId:'19'
		 	},
		 	{
		 		img:'../BonnieDrawService/BDService/loadFile/files/20/png/b51441a82b154beaae0eda738d2ba4dc.png',
		 		title:'FiFi',
		 		comment:'10',
		 		view:'534',
		 		worksId:'20'
		 	},
		 	{
		 		img:'../BonnieDrawService/BDService/loadFile/files/23/png/dce333f9fba54dc1a71e10066072b6ce.png',
		 		title:'Bear',
		 		comment:'10',
		 		view:'534',
		 		worksId:'23'
		 	},
		 	{
		 		img:'../BonnieDrawService/BDService/loadFile/files/22/png/1f4f5b64ca95487599b0f934fa755517.png',
		 		title:'test',
		 		comment:'10',
		 		view:'534',
		 		worksId:'22'
		 	},
		 	{
		 		img:'../BonnieDrawService/BDService/loadFile/files/11/png/927334e1e0a34a5eb4b14345e3803376.png',
		 		title:'兔',
		 		comment:'10',
		 		view:'534',
		 		worksId:'11'
		 	}
		 ]

		 $scope.secondarySectionArr_moreCreation = [
		 	{
		 		img:'../BonnieDrawService/BDService/loadFile/files/23/png/dce333f9fba54dc1a71e10066072b6ce.png',
		 		title:'Bear',
		 		comment:'10',
		 		view:'534',
		 		worksId:'23'
		 	}
		 ]

		 $scope.secondarySectionArr_keyword =['3D','Animals & Birds','HD','Horror','Art','Self','HD Songs','Comedy'];
	}
)