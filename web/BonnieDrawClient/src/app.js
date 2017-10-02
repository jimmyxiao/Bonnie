'use strict';

// localhost
var locationIP='http://localhost:8080/';
var rootUrl = locationIP + 'BonnieDrawService/';

// release
// var locationIP='https://www.bonniedraw.com/';
// var rootUrl = locationIP + 'bonniedraw_service/';

var rootApi = rootUrl + 'BDService/';
angular.module('Authentication', []);
var app = angular.module('app',['ui.router', 'ngCookies', 'ui.bootstrap', 'ngRoute', 'ngSanitize', 'pascalprecht.translate', 'Authentication']);

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
  	}).state('complete', {
    	url: '/complete?token',
    	views: {
	        "layout": {
    			templateUrl: 'complete.html',
    			controller: 'completeController'
    		}
    	}
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
  	}).state('forget', {
      	url: '/forget',
      	views: {
        	"layout": {
            	templateUrl: 'modules/login/view/forget.html',
            	controller: 'forgetController'
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
      	url: '/myfile?v=',
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
      	url: '/column-detail?id',
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

app.config(['$translateProvider', function($translateProvider){
	$translateProvider.useStaticFilesLoader({
		prefix: 'messages/',
		suffix: '.json'
	});

	$translateProvider.preferredLanguage('zh-tw');
}])

app.run(function($rootScope, $location, $cookieStore, $http, $window, $state, $filter, $translate){
	$rootScope.title = '';
	$rootScope.iTunesStoreUrl = 'https://www.apple.com/tw/itunes/charts/free-apps/';
	$rootScope.googlePlayStoreUrl = 'https://play.google.com/store';
	$rootScope.nowUrl = '';

	$rootScope.rg_gl = $cookieStore.get('rg_gl') || {};
    if ($rootScope.rg_gl.currentUser) {
        $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.rg_gl.currentUser.authdata;
 	}

   	$rootScope.$on('$locationChangeStart', function (event, next, current){
   		var url = $location.path();
   		$rootScope.nowUrl = url;
		if ((url !== '/login' && url !== '/singup' && url !== '/forget' && url !== '/complete') && !$rootScope.rg_gl.currentUser) {
	        $state.go('login');
	    }
	});

	$rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
 		//pagination offset record control
 		if(fromState.url!='^'){
 			$cookieStore.put('offset',0);
 		}
 		if($cookieStore.get('offset') > 1){
			$rootScope.offset = $cookieStore.get('offset');
		}else{
			$rootScope.offset = 1;
		}
 	});

	$rootScope.$on('$stateChangeError', function(event) {
  		$state.go('404');
	});

	$rootScope.logout = function() {
		$cookieStore.remove('rg_gl');
		$state.go('login');
	}

	$rootScope.language = 'zh-tw';
	$rootScope.switchLanguage = function(langKey) {
		$translate.use(langKey);
	}
	$rootScope.switchLanguage($rootScope.language);

	$rootScope.maxPagination = 0;
	$rootScope.paginList =[];
	$rootScope.initalPaginList = function(){
		$cookieStore.put('offset', $rootScope.offset);
		// show number of pages 
		var paginNum = 5;
		if(paginNum<=$rootScope.maxPagination){
			if(($rootScope.maxPagination - ($rootScope.offset-1)) >= paginNum){
				$rootScope.paginList =[];
				$rootScope.paginList = $filter('range')($rootScope.paginList, $rootScope.offset, $rootScope.maxPagination);
				if($rootScope.paginList.length > paginNum){
					$rootScope.paginList = $filter('limitTo')($rootScope.paginList, paginNum);
				}
			}
		}else{
			$rootScope.paginList =[];
			$rootScope.paginList = $filter('range')($rootScope.paginList, 1, $rootScope.maxPagination);
		}
	}

})


//facebook login
window.fbAsyncInit = function() {
  FB.init({
    cookie     : true,  // enable cookies to allow the server to access 
    appId      : '1376883092359322',
    xfbml      : true,
    version    : 'v2.8'
  });
};
(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/zh_TW/sdk.js";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));

//google login
var auth2;
window.googleAsyncInit = function() {
    gapi.load('auth2', function() {
        // Retrieve the singleton for the GoogleAuth library and set up the client.
        auth2 = gapi.auth2.init({
            client_id: "579888826124-32hn4mff2f1gmvo5kk6ep9u40hcoqv46.apps.googleusercontent.com",
            scope: "profile email"
        });
    });
};
(function(d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) {
        return;
    }
    js = d.createElement(s);
    js.id = id;
    js.src = "//apis.google.com/js/api:client.js?onload=googleAsyncInit";
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'google-jssdk'));
