app.controller('loginController', function ($scope, $rootScope, $location, $cookieStore, $window, $state, $http, AuthenticationService) {
    	$scope.user = {
    		email:'xxxxx@xxxxx'
    	}

    	$rootScope.rg_gl = $cookieStore.get('rg_gl') || {};
    	if ($rootScope.rg_gl.currentUser) {
        	$http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.rg_gl.currentUser.authdata;
 		}

   		$rootScope.$on('$locationChangeStart', function (event, next, current) {
	        if ($location.path() !== '/login' && !$rootScope.rg_gl.currentUser) {
	        	// $window.location.href = "google.com.tw";
	        	console.log(': call back');
	        	$state.go('login');
	        }
	    });

    	AuthenticationService.ClearCredentials();
    	$scope.login_error = false ;
        $scope.login = function (valid) {
        	if(valid){
	            $scope.dataLoading = true;
	            AuthenticationService.Login($scope.usercode, $scope.password, function(response,request) {
	            	if(response.status){
	            		$rootScope.sessionId  = response.sessionId;
	            		$rootScope.user = response.userInfo;
	                    AuthenticationService.SetCredentials($scope.usercode, response.userInfo);
	                    $state.go('index');
	                    // $window.location.href = 'index.html';
	            	}else{
	            		$scope.error_msg = response.message;
	            		util.alert(response.message);
	            		$scope.dataLoading = false;
	            		$scope.login_error = true ;
	                }
	            })
        	}else{
        	}
        }
    }
)

