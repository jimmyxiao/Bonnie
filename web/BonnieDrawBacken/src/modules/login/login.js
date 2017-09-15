'use strict';
var rootUrl = '';
var loginUrl ='';
angular.module('Authentication', []);
var app = angular.module('app',['ui.router', 'ngCookies', 'ui.bootstrap', 'ngSanitize', 'ngRoute',
     'Authentication', 'locationConfigModule']);

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

app.config(['$routeProvider',function($routeProvider) {   
    $routeProvider.when('/login', {
        template: 'modules/login/views/login.html'
    }).when('/', {
        redirectTo: 'index.html' 
    }).otherwise({ redirectTo: '/login' });
}])

app.run(function($rootScope, $location, $cookieStore, $http, $window, $state, locationIP, serviceName, backendName){
    rootUrl = locationIP + serviceName + '/';
    loginUrl = locationIP + backendName +'/#/login'; 
    // === develop when close , release when open ===
    // $rootScope.rg_gl = $cookieStore.get('rg_gl') || {};
 //    if ($rootScope.rg_gl.currentUser) {
 //        $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.rg_gl.currentUser.authdata;
 //     }

    $rootScope.$on('$locationChangeStart', function (event, next, current){
        // if ((url !== '/login') && !$rootScope.rg_gl.currentUser) {
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

app.controller('loginController', function ($scope, $rootScope, $location, $cookieStore, $window, $state, $http, AuthenticationService) {
        $scope.loginUser = {
    		uc:null, up:null, ut:1, dt:3, fn:1
    	}

    	AuthenticationService.ClearCredentials();
    	$scope.login_error = false ;
        $scope.login = function (valid) {
        	if(valid){
	            $scope.dataLoading = true;
	            AuthenticationService.Login($scope.loginUser, function(response,request) {
	            	if(response.res == 1){
	            		$rootScope.sessionId  = response.sk;
	            		$rootScope.user = response.userInfo;
	                    AuthenticationService.SetCredentials($scope.loginUser.uc, response.userInfo);
	                    $window.location.href = 'index.html';
	            	}else{
	            		$scope.error_msg = response.message;
	            		// util.alert(response.message);
	            		$scope.dataLoading = false;
	            		$scope.login_error = true ;
	            		alert('登入失敗');
	                }
	            })
        	}else{
        	}
        }
    }
)

app.factory('loginService', function(baseHttp) {
    return {
        login : function(params, callback) {
            return baseHttp.service('/BDService/login', params, callback);
        }
    }
}).factory('AuthenticationService',['Base64','$http', '$cookieStore', '$rootScope', '$timeout','loginService',
    function (Base64, $http, $cookieStore, $rootScope, $timeout ,loginService) {
        var service = {};
        service.Login = function (loginUser, callback){
            $timeout(function(){
        		loginService.login(loginUser, function(data, status, headers, config) {
        			callback(data);
        		})
            }, 1000);
        }
 
        service.SetCredentials = function (usercode,userInfo){
            var authdata = Base64.encode(usercode + ':' + userInfo.userId);
            $rootScope.rg_gl = {
                currentUser: {
                    usercode: usercode,
                    userId: userInfo.userId,
                    securityKey: userInfo.sk,
                    userType: userInfo.userType,
                    userName: userInfo.userName,
                    userInfo:userInfo
                }
            }

            $http.defaults.headers.common['Authorization'] = 'Basic' + authdata;
            $cookieStore.put('rg_gl', $rootScope.rg_gl);
        }
 
        service.ClearCredentials = function () {
            $rootScope.rg_gl = {};
            $cookieStore.remove('rg_gl');
            $http.defaults.headers.common.Authorization = 'Basic';
        }
        return service;
    }
]).factory('Base64', function () {
    var keyStr = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
    return {
        encode: function (input) {
            var output = "";
            var chr1, chr2, chr3 = "";
            var enc1, enc2, enc3, enc4 = "";
            var i = 0;
 
            do {
                chr1 = input.charCodeAt(i++);
                chr2 = input.charCodeAt(i++);
                chr3 = input.charCodeAt(i++);
 
                enc1 = chr1 >> 2;
                enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;
 
                if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
                } else if (isNaN(chr3)) {
                    enc4 = 64;
                }
 
                output = output +
                    keyStr.charAt(enc1) +
                    keyStr.charAt(enc2) +
                    keyStr.charAt(enc3) +
                    keyStr.charAt(enc4);
                chr1 = chr2 = chr3 = "";
                enc1 = enc2 = enc3 = enc4 = "";
            } while (i < input.length);
 
            return output;
        },
        decode: function (input) {
            var output = "";
            var chr1, chr2, chr3 = "";
            var enc1, enc2, enc3, enc4 = "";
            var i = 0;
            var base64test = /[^A-Za-z0-9\+\/\=]/g;
            if (base64test.exec(input)){}
            input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
 
            do {
                enc1 = keyStr.indexOf(input.charAt(i++));
                enc2 = keyStr.indexOf(input.charAt(i++));
                enc3 = keyStr.indexOf(input.charAt(i++));
                enc4 = keyStr.indexOf(input.charAt(i++));
 
                chr1 = (enc1 << 2) | (enc2 >> 4);
                chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                chr3 = ((enc3 & 3) << 6) | enc4;
 
                output = output + String.fromCharCode(chr1);
                if (enc3 != 64) {
                    output = output + String.fromCharCode(chr2);
                }
                if (enc4 != 64) {
                    output = output + String.fromCharCode(chr3);
                }
 
                chr1 = chr2 = chr3 = "";
                enc1 = enc2 = enc3 = enc4 = ""; 
            } while (i < input.length);
 
            return output;
        }
    }
})

