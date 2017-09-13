'use strict';
var locationIP='http://localhost:8080/';
var rootUrl = locationIP + 'BonnieDrawService/';

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
	$stateProvider.state('index', {
      url: '/',
      views: {
        layout: {
            templateUrl: 'modules/home/view/home.html',
            controller:'homeController'
        },
        headerMenu:{
        	templateUrl: 'modules/share/view/header-menu.html'
        }
      },
      reload: true
  	}).state('login', {
      url: '/login',
      views: {
        layout: {
            templateUrl: 'modules/login/view/login.html',
            controller: 'loginController'
        }
      }
  	}).state('singup', {
      url: '/singup',
      views: {
        layout: {
            templateUrl: 'modules/login/view/singup.html',
            controller: 'registerController'
        }
      }
  	})
}])

app.run(function($rootScope, $location, $cookieStore, $http, $window, $state){
	$rootScope.title = '';
	
	// === develop when close , release when open ===
	$rootScope.rg_gl = $cookieStore.get('rg_gl') || {};
    if ($rootScope.rg_gl.currentUser) {
        $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.rg_gl.currentUser.authdata;
 	}

   	$rootScope.$on('$locationChangeStart', function (event, next, current){
   		var url = $location.path();
		if ((url !== '/login' && url !== '/singup') && !$rootScope.rg_gl.currentUser) {
	        $state.go('login');
	    }
	});
	// =============================================

	$rootScope.logout = function() {
		$cookieStore.remove('rg_gl');
		$state.go('login');
	}
})



