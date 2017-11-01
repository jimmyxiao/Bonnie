app.factory('forgetService', function(baseHttp) {
    return {
        register : function(params, callback) {
            return baseHttp.service('BDService/login', params, callback);
        }
    }
})
.controller('forgetController', function ($scope, $rootScope, $location, $cookieStore, $window, $state, $http, forgetService, vcRecaptchaService) {
    	$rootScope.title = '忘記密碼 | BonnieDRAW';
        $scope.register = {
    		phoneNo:null, userName:null, userCode:null, userPw:null, userType:1 
    	}

        $scope.response = null;
        $scope.widgetId = null;
        $scope.model = {
            key: '6LczuDYUAAAAAHDLloAHfCwqAqUjpUivI5XaKz_r'
        }

        $scope.setResponse = function (response) {
            $scope.response = response;
        }
                
        $scope.setWidgetId = function (widgetId) {
            $scope.widgetId = widgetId;
        }
                
        $scope.cbExpiration = function() {
            vcRecaptchaService.reload($scope.widgetId);
            $scope.response = null;
        }

        $scope.submit = function () {
            if($scope.response == null || $scope.response ==''){
                alert('請勾選「我不是機器人」');
                return;
            }
            var valid=false;
            var url = rootUrl + 'ws/recaptcha-valid';
            $.ajax({
                url: url,
                type: 'post',
                dataType: 'json',
                data: $scope.response,
                success: function (data) {
                    valid = data.result;
                    if(valid){
                        console.log('call send forget email');
                    }else{
                        vcRecaptchaService.reload($scope.widgetId);
                    }
                },
                error: function(data){

                }
            });
        }


        // $scope.callRegister = function (valid) {
        // 	if(valid){
        //         var params = {
        //             phoneNo:$scope.register.phoneNo,
        //             uc:$scope.register.userCode,
        //             up:$scope.register.userPw,
        //             un:$scope.register.userName,
        //             ut:$scope.register.userType,
        //             dt:3    
        //         }
        //         params.fn = 3;
	       //      forgetService.register(params, function(data, status, headers, config) {
	       //      	if(data.res == 1){
        //                 params.fn = 2;
	       //      		forgetService.register(params, function(data, status, headers, config) {
        //                     if(data.res == 1){
        //                         alert('註冊成功,信件已發送!');
        //                         $state.go('login');
        //                     }else{
        //                         alert('註冊失敗');
        //                     }
        //                 })
	       //      	}else{
	       //      		alert('註冊失敗');
	       //      	}
	       //      })
        // 	}else{
        // 	}
        // }
    }
)

