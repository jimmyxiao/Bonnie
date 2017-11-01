app.factory('AuthenticationService',['Base64','$http', '$cookieStore', '$rootScope', '$timeout', 'localStorageService', 'loginService',
    function (Base64, $http, $cookieStore, $rootScope, $timeout, localStorageService, loginService) {
        var service = {};
        service.Login = function (loginUser, callback){
            $timeout(function(){
        		loginService.login(loginUser, function(data, status, headers, config) {
        			callback(data);
        		})
            }, 1000);
        }
 
        service.SetCredentials = function (response){
            var authdata = Base64.encode(response.userInfo.userCode + ':' + response.userInfo.userId);
            $rootScope.rg_gl = {
                currentUser: {
                    ut: response.ut,
                    ui: response.ui,
                    sk: response.sk,
                    lk: response.lk,
                    userInfo:response.userInfo
                }
            }

            $http.defaults.headers.common['Authorization'] = 'Basic' + authdata;
            if(localStorageService.isSupported){
                localStorageService.set('rg_gl', $rootScope.rg_gl);
            }else{
                $cookieStore.put('rg_gl', $rootScope.rg_gl);
            }
        }
 
        service.ClearCredentials = function () {
            if(localStorageService.isSupported){
                localStorageService.remove('rg_gl');
            }else{
                $cookieStore.remove('rg_gl');
            }
            $rootScope.rg_gl = null;
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
}).factory('loginService', function(baseHttp) {
    return {
        login : function(params, callback) {
            return baseHttp.service('login', params, callback);
        }
    }
}).factory('generalService', function(baseHttp) {
    return {
        getTagList : function(params, callback) {
            return baseHttp.service('tagList', params, callback);
        },
        getCategoryList : function(params, callback) {
            return baseHttp.service('categoryList', params, callback);
        },
        getDictionaryList : function(params, callback) {
            return baseHttp.service('dictionaryList', params, callback);
        }
    }
}).factory('worksService', function(baseHttp) {
    return {
        getDrawingPlay: function(params,callback){
            return baseHttp.service('drawingPlay' ,params,callback);
        }
        ,queryWorksList: function(params,callback){
            return baseHttp.service('worksList' ,params,callback);
        },
        setLike: function(params,callback){
            return baseHttp.service('setLike' ,params,callback);
        },
        leavemsg: function(params,callback){
            return baseHttp.service('leavemsg' ,params,callback);
        },
        setCollection: function(params,callback){
            return baseHttp.service('setCollection' ,params,callback);
        },
        setTurnin: function(params,callback){
            return baseHttp.service('setTurnin' ,params,callback);
        }
    }
}).factory('userService', function(baseHttp) {
    return {
        userInfoQuery:function(params, callback) {
            return baseHttp.service('userInfoQuery', params, callback);
        },
        updateUser : function(params, callback) {
            return baseHttp.service('userInfoUpdate', params, callback);
        },
        updatePwd : function(params, callback) {
            return baseHttp.service('updatePwd', params, callback);
        },
        setFollowing:function(params, callback) {
            return baseHttp.service('setFollowing', params, callback);
        }
    }
})