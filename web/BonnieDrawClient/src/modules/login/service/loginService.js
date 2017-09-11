app.factory('loginService', function(baseHttp) {
    return {
        login : function(params, callback) {
            return baseHttp.service('loginBackend', params, callback);
        }
    }
}).factory('AuthenticationService',['Base64','$http', '$cookieStore', '$rootScope', '$timeout','loginService',
    function (Base64, $http, $cookieStore, $rootScope, $timeout ,loginService) {
        var service = {};
        $rootScope.queryKey = {
    		userCode : '',
    		userPwd : '',
    		loginPlatform : 1,
    		mask : '',
    		ip:''
		}
        
        $rootScope.wslogin = function(usercode, password){
        	$rootScope.queryKey.userCode = usercode;
        	$rootScope.queryKey.userPwd = password;
    		$rootScope.queryKey_json = JSON.parse(JSON.stringify($rootScope.queryKey));
    		loginService.login($rootScope.queryKey_json, function(data,status, headers,config) {
    			var res_wslogin = {};
    			if(data.result){
    				res_wslogin = data.data;	
    			}
    			return res_wslogin;
    		});
    	}
        
        service.Login = function (usercode, password, callback){
            $timeout(function(){
            	var res_login = {};
            	$rootScope.queryKey.userCode = usercode;
            	$rootScope.queryKey.userPwd = password;
        		$rootScope.queryKey_json = JSON.parse(JSON.stringify($rootScope.queryKey));
        		loginService.login($rootScope.queryKey_json, function(data,status, headers, config) {
        			var res_wslogin = {};
        			if(data.result){
        				res_wslogin = data.data;	
        			}else{
        				res_wslogin.message = data.message;
        			}
        			callback(res_wslogin);
        		})
            }, 1000);
        }
 
        service.SetCredentials = function (usercode,userInfo){
            var authdata = Base64.encode(usercode + ':' + userInfo.userId);
            $rootScope.rg_gl = {
                currentUser: {
                    usercode: usercode,
                    userId: userInfo.userId,
                    securityKey: userInfo.securityKey,
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