app.factory('registerService', function(baseHttp) {
    return {
        register : function(params, callback) {
            return baseHttp.service('ws/register', params, callback);
        }
    }
})
.controller('registerController', function ($scope, $rootScope, $location, $cookieStore, $window, $state, $http, registerService) {
    	$scope.register = {
    		phoneNo:null, userName:null, userCode:null, userPw:null, userType:1 
    	}
        $scope.callRegister = function (valid) {
        	if(valid){
	            registerService.register($scope.register, function(data, status, headers, config) {
	            	if(data.result){
	            		alert('註冊成功');
	            		$state.go('login');
	            	}else{
	            		alert('註冊失敗');
	            	}
	            })
        	}else{
        	}
        }
    }
)

