'use strict';

// localhost
var locationIP='http://localhost:8080/';
// var locationIP='http://www.bonniedraw.com:8080/';
var rootUrl = locationIP + 'BonnieDrawService/';

// release
 // var locationIP='https://www.bonniedraw.com/';
 // var rootUrl = locationIP + 'bonniedraw_service/';

var rootApi = rootUrl + 'BDService/';
angular.module('Authentication', []);
var app = angular.module('app',
	['ui.router', 'ngCookies', 'ui.bootstrap', 'ngRoute', 'ngSanitize',
	 'pascalprecht.translate', 'ngTextareaEnter', 'ngFileUpload', 'socialLinks', 'LocalStorageModule',
	 'vcRecaptcha', 'Authentication']);

app.factory('baseHttp', function($rootScope, $http){
	function doService(url, params, callback, error){
		if (params == null) { params = {}; }
		$http.post(url, params).success(
			function(data, status, headers, config){
				callback(data, status, headers, config);
			}).error(function(data, status, headers, config){
				// alert('Oops ! has error ! try it again !');
				alert('您的登入帳號遺失,請重新登入!');
				$rootScope.logout();
			}
		)
	}
	return {
		service : function(url, params, callback, error){
			url = rootApi + url;
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
	        "layout": {
	            templateUrl: 'modules/share/view/about.html',
	            controller:'aboutController'
	        }
	    }
  	}).state('privacy', {
      	url: '/privacy',
	    views: {
	        "layout": {
	            templateUrl: 'modules/share/view/privacy.html',
	            controller:'aboutController'
	        }
	    }
  	}).state('terms', {
      	url: '/terms',
	    views: {
	        "layout": {
	            templateUrl: 'modules/share/view/use-terms.html',
	            controller:'aboutController'
	        }
	    }
  	}).state('about_app', {
      	url: '/about_app',
	    views: {
	        "layout": {
	            templateUrl: 'modules/share/view/about_app.html',
	            controller:'aboutController'
	        }
	    }
  	}).state('privacy_app', {
      	url: '/privacy_app',
	    views: {
	        "layout": {
	            templateUrl: 'modules/share/view/privacy_app.html',
	            controller:'aboutController'
	        }
	    }
  	}).state('terms_app', {
      	url: '/terms_app',
	    views: {
	        "layout": {
	            templateUrl: 'modules/share/view/use-terms_app.html',
	            controller:'aboutController'
	        }
	    }
  	})
  	//.state('about', {
    //  	url: '/about',
    // 	views: {
    //  		"loader":{
    //  			templateUrl: 'modules/share/view/loader.html'
    //  		},
	//        "layout": {
	//            templateUrl: 'modules/share/view/about.html',
	//            controller:'aboutController'
	//        },
	//        "headerMenu@about":{
	//        	templateUrl: 'modules/share/view/header-menu.html'
	//        },
	//        "footer@about":{
	//        	templateUrl: 'modules/share/view/footer.html'
	//        }
    //  	}
  	//})
  	// .state('privacy', {
    //    	url: '/privacy',
    //    	views: {
    //    		"loader":{
    //    			templateUrl: 'modules/share/view/loader.html'
    //    		},
	//       "layout": {
	//           templateUrl: 'modules/share/view/privacy.html',
	//           controller:'privacyController'
	//       },
	//       "headerMenu@privacy":{
	//       	templateUrl: 'modules/share/view/header-menu.html'
	//       },
	//       "footer@privacy":{
	//       	templateUrl: 'modules/share/view/footer.html'
	//       }
    //    	}
  	// }).state('terms', {
    //    	url: '/terms',
    //    	views: {
    //    		"loader":{
    //    			templateUrl: 'modules/share/view/loader.html'
    //    		},
	//       "layout": {
	//           templateUrl: 'modules/share/view/terms.html',
	//           controller:'termsController'
	//       },
	//       "headerMenu@terms":{
	//       	templateUrl: 'modules/share/view/header-menu.html'
	//       },
	//       "footer@terms":{
	//       	templateUrl: 'modules/share/view/footer.html'
	//       }
    //    	}
  	// })
  	.state('myfile', {
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
  	}).state('memberfile', {
      	url: '/memberfile?v=',
      	views: {
	        "loader":{
      			templateUrl: 'modules/share/view/loader.html'
      		},
	        "layout": {
	            templateUrl: 'modules/user/view/memberfile.html',
	            controller:'memberfileController'
	        },
	        "headerMenu@memberfile":{
	        	templateUrl: 'modules/share/view/header-menu.html'
	        },
	        "footer@memberfile":{
	        	templateUrl: 'modules/share/view/footer.html'
	        }
      	}
  	}).state('userfile', {
      	url: '/userfile/{id}',
      	views: {
	        "loader":{
      			templateUrl: 'modules/share/view/loader.html'
      		},
	        "layout": {
	            templateUrl: 'modules/user/view/user-files.html',
	            controller:'userfileController'
	        },
	        "headerMenu@userfile":{
	        	templateUrl: 'modules/share/view/header-menu.html'
	        },
	        "footer@userfile":{
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
      	url: '/category-listing?type=',
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
  	}).state('columnDetailStreamSimulation', {
      	url: '/column-detail-StreamSimulation?id',
      	views: {
      		"loader":{
      			templateUrl: 'modules/share/view/loader.html'
      		},
        	"layout": {
	            templateUrl: 'modules/works/view/column-detail.html',
	            controller:'columnDetailControllerStreamSimulation'
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
  	}).state('collectionListing', {
      	url: '/collection-listing',
      	views: {
      		"loader":{
      			templateUrl: 'modules/share/view/loader.html'
      		},
        	"layout": {
	            templateUrl: 'modules/works/view/collection-listing.html',
	            controller:'collectionListingController'
	        },
	        "headerMenu@collectionListing":{
	        	templateUrl: 'modules/share/view/header-menu.html'
	        },
	        "footer@collectionListing":{
	        	templateUrl: 'modules/share/view/footer.html'
	        }
      	}
  	}).state('searchListing', {
      	url: '/searchListing?result=',
      	views: {
      		"loader":{
      			templateUrl: 'modules/share/view/loader.html'
      		},
	        "layout": {
	            templateUrl: 'modules/works/view/searchListing.html',
	            controller:'searchListingController'
	        },
	        "headerMenu@searchListing":{
	        	templateUrl: 'modules/share/view/header-menu.html'
	        },
	        "footer@searchListing":{
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

app.run(function($rootScope, $location, $cookieStore, $http, $window, $state, $filter, $translate, localStorageService){
	$rootScope.title = '';
	$rootScope.iTunesStoreUrl = 'https://www.apple.com/tw/itunes/charts/free-apps/';
	$rootScope.googlePlayStoreUrl = 'https://play.google.com/store';
	$rootScope.nowUrl = '';
	$rootScope.imageLoadUrl = rootApi + 'loadFile';

	if(localStorageService.isSupported){
		$rootScope.rg_gl = localStorageService.get('rg_gl');
	}else{
		$rootScope.rg_gl = $cookieStore.get('rg_gl') || {};
	}
    if ($rootScope.rg_gl && $rootScope.rg_gl.currentUser) {
        $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.rg_gl.currentUser.authdata;
 	}

   	$rootScope.$on('$locationChangeStart', function (event, next, current){
   		var url = $location.path();
   		$rootScope.nowUrl = url;

		if ((url !== '/login' && url !== '/singup' && url !== '/forget' && url !== '/complete' && url !== '/about' && url !== '/privacy' && url !== '/terms') && (!$rootScope.rg_gl || !$rootScope.rg_gl.currentUser)) {
	        event.preventDefault();
	        $state.go('login');
	    }else if( ($rootScope.rg_gl && $rootScope.rg_gl.currentUser) && (url == '/login' && url == '/singup' && url == '/forget' && url == '/complete')){
	    	event.preventDefault();
	    	$state.go('index');
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
		if(localStorageService.isSupported){
        	localStorageService.remove('rg_gl');
        }else{
            $cookieStore.remove('rg_gl');
        }
		$rootScope.rg_gl = null;
		$state.go('login');
	}

	$rootScope.covertTag = function(tagName) {
		var str = tagName.replace('#', '');
		if(str.indexOf('+')!=-1){
			str = str.replace(/\+/gi, '%2B');
		}
		if(str.indexOf('&')!=-1){
			str = str.replace(/\&/gi, '&amp;');
		}
		return str;
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

//twitter
hello.init({
	twitter: "HcXL8RV5wlnL1eM3OWSPcvN1N"
}, {
	//oauth_proxy: 'http://www.bonniedraw.com:8080/proxy'
	scope: "email"
});