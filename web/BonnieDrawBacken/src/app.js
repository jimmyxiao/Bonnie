'use strict';
var rootUrl = '';
var loginUrl ='';
var app = angular.module('app',['ui.router', 'ngCookies', 'ui.bootstrap', 'ngSanitize', 'locationConfigModule']);

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
  	}).state('userManager', {
      	url: '/userManager',
      	views: {
      		"content":{
      			templateUrl:'modules/user/view/userInfoList.html'
      		}
      	},
      	reload: true
  	}).state('adminManager', {
      	url: '/adminManager',
      	views: {
      		"content":{
      			templateUrl:'modules/user/view/backendInfoList.html'
      		}
      	}
  	}).state('mailSet', {
      	url: '/mailSet',
      	views: {
      		"content":{
      			templateUrl:'modules/systemset/view/system_setup.html'
      		}
      	}
  	})
}])

app.run(function($rootScope, $location, $cookieStore, $http, $window, $state, locationIP, serviceName, backendName){
	rootUrl = locationIP + serviceName + '/';
    loginUrl = locationIP + backendName +'/#/login'; 
	$rootScope.nowUrl = $location.path();
	// === develop when close , release when open ===
	// $rootScope.rg_gl = $cookieStore.get('rg_gl') || {};
 //    if ($rootScope.rg_gl.currentUser) {
 //        $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.rg_gl.currentUser.authdata;
 // 	}else{
	// 	$rootScope.logout();
	// }	

   	$rootScope.$on('$locationChangeStart', function (event, next, current){
		// if ((url !== '/login' && url !== '/singup') && !$rootScope.rg_gl.currentUser) {
	 //        $window.location.href = loginUrl;
	 //    }
	});
	// =============================================
	$rootScope.$on('$stateChangeError', function(event) {
  		$state.go('404');
	});

	$rootScope.logout = function() {
		$cookieStore.remove('rg_gl');
		$window.location.replace(loginUrl);
	}
})



