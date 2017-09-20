'use strict';

// localhost
var locationIP='http://localhost:8080/';
var rootUrl = locationIP + 'BonnieDrawService/';

// releas
// var locationIP='https://www.bonniedraw.com/';
// var rootUrl = locationIP + 'bonniedraw_service/';

angular.module('Authentication', []);
var app = angular.module('app',['ui.router', 'ngCookies', 'ui.bootstrap', 'ngRoute', 'ngSanitize','Authentication']);

app.factory('baseHttp', function($rootScope, $http){
	function doService(url, params, callback, error){
		if (params == null) { params = {}; }
		$http.post(url, params).success(
			function(data, status, headers, config){
				callback(data, status, headers, config);
			}).error(function(data, status, headers, config){
				alert('Oops ! has error ! try it again !');
			}
		)
	}
	return {
		service : function(url, params, callback, error){
			url = rootUrl + url;
			doService(url, params, callback, error);
		}
	}
})

app.config(['$stateProvider', '$urlRouterProvider',function($stateProvider, $urlRouterProvider) {	
	$urlRouterProvider.otherwise('/login');
	$stateProvider.state('404', {
    	url: '/page-not-found'
    	// templateUrl: 'modules/login/view/login.html'
  	}).state('login', {
      	url: '/login',
	    views: {
	        "layout": {
	            templateUrl: 'modules/login/view/login.html',
	            controller: 'loginController'
	        }
	    }
  	}).state('singup', {
      	url: '/singup',
      	views: {
        	"layout": {
            	templateUrl: 'modules/login/view/singup.html',
            	controller: 'registerController'
        	}
      	}
  	}).state('index', {
      	url: '/',
      	views: {
      		"loader":{
      			templateUrl: 'modules/share/view/loader.html'
      		},
        	"layout": {
            	templateUrl: 'modules/home/view/home.html',
            	controller:'homeController'
        	},
        	"headerMenu@index":{
        		templateUrl: 'modules/share/view/header-menu.html'
        	},
        	"footer@index":{
        		templateUrl: 'modules/share/view/footer.html'
        	}
      	},
      	reload: true
  	}).state('about', {
      	url: '/about',
      	views: {
      		"loader":{
      			templateUrl: 'modules/share/view/loader.html'
      		},
	        "layout": {
	            templateUrl: 'modules/share/view/about.html',
	            controller:'aboutController'
	        },
	        "headerMenu@about":{
	        	templateUrl: 'modules/share/view/header-menu.html'
	        },
	        "footer@about":{
	        	templateUrl: 'modules/share/view/footer.html'
	        }
      	}
  	}).state('myfile', {
      	url: '/myfile',
      	views: {
      		"loader":{
      			templateUrl: 'modules/share/view/loader.html'
      		},
	        "layout": {
	            templateUrl: 'modules/user/view/myfile.html',
	            controller:'myfileController'
	        },
	        "headerMenu@myfile":{
	        	templateUrl: 'modules/share/view/header-menu.html'
	        },
	        "footer@myfile":{
	        	templateUrl: 'modules/share/view/footer.html'
	        }
      	}
  	}).state('trackList', {
      	url: '/track-list',
      	views: {
      		"loader":{
      			templateUrl: 'modules/share/view/loader.html'
      		},
	        "layout": {
	            templateUrl: 'modules/user/view/track-list.html',
	            controller:'trackListController'
	        },
	        "headerMenu@trackList":{
	        	templateUrl: 'modules/share/view/header-menu.html'
	        },
	        "footer@trackList":{
	        	templateUrl: 'modules/share/view/footer.html'
	        }
      	}
  	}).state('categoryListing', {
      	url: '/category-listing',
      	views: {
      		"loader":{
      			templateUrl: 'modules/share/view/loader.html'
      		},
	        "layout": {
	            templateUrl: 'modules/works/view/category-listing.html',
	            controller:'categoryListingController'
	        },
	        "headerMenu@categoryListing":{
	        	templateUrl: 'modules/share/view/header-menu.html'
	        },
	        "footer@categoryListing":{
	        	templateUrl: 'modules/share/view/footer.html'
	        }
      	}
  	}).state('columnDetail', {
      	url: '/column-detail',
      	views: {
      		"loader":{
      			templateUrl: 'modules/share/view/loader.html'
      		},
        	"layout": {
	            templateUrl: 'modules/works/view/column-detail.html',
	            controller:'columnDetailController'
	        },
	        "headerMenu@columnDetail":{
	        	templateUrl: 'modules/share/view/header-menu.html'
	        },
	        "footer@columnDetail":{
	        	templateUrl: 'modules/share/view/footer.html'
	        }
      	}
  	}).state('column', {
      	url: '/column',
      	views: {
      		"loader":{
      			templateUrl: 'modules/share/view/loader.html'
      		},
        	"layout": {
	            templateUrl: 'modules/works/view/column.html',
	            controller:'columnController'
	        },
	        "headerMenu@column":{
	        	templateUrl: 'modules/share/view/header-menu.html'
	        },
	        "footer@column":{
	        	templateUrl: 'modules/share/view/footer.html'
	        }
      	}
  	}).state('hotListing', {
      	url: '/hot-listing',
      	views: {
      		"loader":{
      			templateUrl: 'modules/share/view/loader.html'
      		},
        	"layout": {
	            templateUrl: 'modules/works/view/hot-listing.html',
	            controller:'hotListingController'
	        },
	        "headerMenu@hotListing":{
	        	templateUrl: 'modules/share/view/header-menu.html'
	        },
	        "footer@hotListing":{
	        	templateUrl: 'modules/share/view/footer.html'
	        }
      	}
  	})
}])

app.run(function($rootScope, $location, $cookieStore, $http, $window, $state){
	$rootScope.title = '';
	$rootScope.iTunesStoreUrl = 'https://www.apple.com/tw/itunes/charts/free-apps/';
	$rootScope.googlePlayStoreUrl = 'https://play.google.com/store';
	$rootScope.nowUrl = '';
	// === develop when close , release when open ===
	// $rootScope.rg_gl = $cookieStore.get('rg_gl') || {};
 //    if ($rootScope.rg_gl.currentUser) {
 //        $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.rg_gl.currentUser.authdata;
 // 	}

   	$rootScope.$on('$locationChangeStart', function (event, next, current){
   		var url = $location.path();
   		$rootScope.nowUrl = url;
		// if ((url !== '/login' && url !== '/singup') && !$rootScope.rg_gl.currentUser) {
	 //        $state.go('login');
	 //    }
	});
	// =============================================
	$rootScope.$on('$stateChangeError', function(event) {
  		$state.go('404');
	});

	$rootScope.logout = function() {
		$cookieStore.remove('rg_gl');
		$state.go('login');
	}
})



