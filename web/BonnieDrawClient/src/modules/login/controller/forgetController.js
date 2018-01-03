app.controller('forgetController', function ($scope, $rootScope, $location, $cookieStore, $window, $state, $http, forgetService, vcRecaptchaService, $translate) {
        $rootScope.title = 'TITLE.t02_01_forget_pass';
        $scope.register = {
    		phoneNo:null, userName:null, userCode:null, userPw:null, userType:1 
    	}
        $scope.dataLoading = false;
        $scope.response = null;
        $scope.widgetId = null;
        $scope.model = {
            key: '6LczuDYUAAAAAHDLloAHfCwqAqUjpUivI5XaKz_r',
            email:null
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
            if(!$scope.model.email){
                $translate(['MSG.msg02_01_email']).then(function (MSG) {
                    alert(MSG['MSG.msg02_01_email']);
                });
                return;
            }

            if($scope.response == null || $scope.response ==''){
                $translate(['MSG.msg02_01_not_robot']).then(function (MSG) {
                    alert(MSG['MSG.msg02_01_not_robot']);
                });
                return;
            }
            $scope.dataLoading = true;
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
                        forgetService.forgetpwd($scope.model,function(data, status, headers, config){
                            if(data.res == 1){
                                $translate(['MSG.msg02_01_check_email']).then(function (MSG) {
                                    alert(MSG['MSG.msg02_01_check_email']);
                                });
                            }else{
                                alert(data.msg);
                            }
                            $scope.dataLoading = false;
                        })
                    }else{
                        vcRecaptchaService.reload($scope.widgetId);
                    }
                },
                error: function(data){
                    $scope.dataLoading = false;
                    $translate(['MSG.msh02_01_validation_failed']).then(function (MSG) {
                        alert(MSG['MSG.msh02_01_validation_failed']);
                    });
                }
            });
        }

    }
)

