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
	            	if(response.res){
	            		$rootScope.sessionId  = response.sk;
	            		$rootScope.user = response.userInfo;
	                    AuthenticationService.SetCredentials($scope.loginUser.uc, response.userInfo);
	                    $state.go('index');
	            	}else{
	            		$scope.error_msg = response.message;
	            		// util.alert(response.message);
	            		$scope.dataLoading = false;
	            		$scope.login_error = true ;
	                }
	            })
        	}else{
        	}
        }
    }
)

