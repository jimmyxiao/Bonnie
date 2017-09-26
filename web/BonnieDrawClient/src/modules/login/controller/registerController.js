app.factory('registerService', function(baseHttp) {
    return {
        register : function(params, callback) {
            return baseHttp.service('BDService/login', params, callback);
        }
    }
})
.controller('registerController', function ($scope, $rootScope, $location, $cookieStore, $window, $state, $http, registerService) {
    	$rootScope.title = '註冊 | BonnieDRAW';
        $scope.register = {
    		phoneNo:null, userName:null, userCode:null, userPw:null, userType:1 
    	}
        $scope.callRegister = function (valid) {
        	if(valid){
                var params = {
                    uc:$scope.register.userCode,
                    up:$scope.register.userPw,
                    un:$scope.register.userName,
                    ut:$scope.register.userType,
                    dt:3
                }
                params.fn = 3;
	            registerService.register(params, function(data, status, headers, config) {
	            	if(data.res == 1){
                        params.fn = 2;
	            		registerService.register(params, function(data, status, headers, config) {
                            if(data.res == 1){
                                alert('註冊成功,信件已發送!');
                                $state.go('login');
                            }else{
                                alert('註冊失敗');
                            }
                        })
	            	}else{
	            		alert('註冊失敗');
	            	}
	            })
        	}else{
        	}
        }
    }
)

