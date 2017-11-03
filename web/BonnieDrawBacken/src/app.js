'use strict';
var rootUrl = '';
var loginUrl ='';

var app = angular.module('app',['ui.router', 'ngCookies', 'ui.bootstrap', 'ngSanitize', 'TreeWidget', 'locationConfigModule']);

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
	$urlRouterProvider.otherwise('/userManager');
	$stateProvider.state('404', {
    	url: '/page-not-found',
    	views: {
      		"content":{
      			templateUrl:'modules/share/view/page404.html'
      		}
      	}
  	}).state('adminManager', {
        url: '/adminManager',
        views: {
          "content":{
            templateUrl:'modules/user/view/adminList.html',
            controller:'adminController'
          }
        }
    }).state('userManager', {
      	url: '/userManager',
      	views: {
      		"content":{
      			templateUrl:'modules/user/view/userInfoList.html',
      			controller:'userController'
      		}
      	},
      	reload: true
  	}).state('directoryManager', {
        url: '/directoryManager?cp',
        views: {
          "content":{
            templateUrl:'modules/directory/view/directoryList.html',
            controller:'directoryController'
          }
        }
    }).state('tagManager', {
        url: '/tagManager',
        views: {
          "content":{
            templateUrl:'modules/hashtag/view/tagList.html',
            controller:'tagManagerController'
          }
        }
    }).state('turnInManager', {
        url: '/turnInManager',
        views: {
          "content":{
            templateUrl:'modules/turnIn/view/turnInList.html',
            controller:'turnInController'
          }
        }
    }).state('mailSet', {
      	url: '/mailSet',
      	views: {
      		"content":{
      			templateUrl:'modules/systemset/view/mailSet.html',
      			controller:'mailSetController'
      		}
      	}
  	})
}])

app.run(function($rootScope, $location, $cookieStore, $http, $window, $state, locationIP, serviceName, backendName){
	rootUrl = locationIP + serviceName + '/';
    loginUrl = locationIP + backendName +'/#/login'; 

    $rootScope.logout = function() {
		$cookieStore.remove('rg_gl');
		$window.location.replace(loginUrl);
	}

	$rootScope.rg_gl = $cookieStore.get('rg_gl') || {};
    if ($rootScope.rg_gl.currentUser) {
        $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.rg_gl.currentUser.authdata;
 		$rootScope.rootUser = $rootScope.rg_gl.currentUser.adminInfo;
 	}else{
		$rootScope.logout();
	}	

   	$rootScope.$on('$locationChangeStart', function (event, next, current){
   		var url = $location.path();
		if ((url !== '/login') && !$rootScope.rg_gl.currentUser) {
	        $window.location.href = loginUrl;
	    }
	});

	$rootScope.$on('$stateChangeError', function(event) {
  		$state.go('404');
	});

})



