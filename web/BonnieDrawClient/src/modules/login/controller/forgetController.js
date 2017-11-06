app.controller('forgetController', function ($scope, $rootScope, $location, $cookieStore, $window, $state, $http, forgetService, vcRecaptchaService) {
    	$rootScope.title = '忘記密碼 | BonnieDRAW';
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
                alert('請輸入Email');
                return;
            }

            if($scope.response == null || $scope.response ==''){
                alert('請勾選「我不是機器人」');
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
                                alert('郵件已寄出，請查看郵件');
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
                    alert('驗證失敗');
                }
            });
        }

    }
)

