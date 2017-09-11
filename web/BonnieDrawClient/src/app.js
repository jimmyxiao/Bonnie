'use strict';
var locationIP='http://localhost:8080/';
var rootUrl = locationIP + 'BonnieDrawService/';
var loginUrl = locationIP + 'BonnieDrawClient/#/login';

angular.module('Authentication', []);
var app = angular.module('app',['ui.router', 'ngCookies', 'ui.bootstrap', 'ngSanitize','Authentication']);

app.factory('baseHttp', function($rootScope, $http){
	function doService(url, params, callback, error){
		if (params == null) { params = {}; }
		$http.post(url, params).success(
			function(data, status, headers, config){
				callback(data, status, headers, config);
			}).error(function(data, status, headers, config){
				result = data;
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
        }
      }
  	})
  	.state('login', {
      url: '/login',
      views: {
        layout: {
            templateUrl: "modules/login/view/login.html",
            controller: 'loginController'
        }
      }
  	})
  	.state('singup', {
      url: '/singup',
      views: {
        layout: {
            templateUrl: "modules/login/view/singup.html"
        }
      }
  	})
}])


