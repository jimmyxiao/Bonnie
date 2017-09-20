app.controller('loginController', function ($scope, $rootScope, $location, $cookieStore, $window, $state, $http, AuthenticationService) {
    	$rootScope.title = '登入 | BonnieDRAW';
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
	            		$rootScope.user = response.userInfo;
	                    AuthenticationService.SetCredentials(response);
	                    $state.go('index');
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

