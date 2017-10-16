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
    

        //facebook login
        $scope.FBlogin = function () {
            FB.login(function(response) {
                if (response.authResponse) {
                    console.log('Welcome!  Fetching your information.... ');
                    FB.api('/me', {fields: 'name,email'}, function(response) {
                         var data = {
                            id: response.id,
                            name: response.name,
                            img: "http://graph.facebook.com/"+response.id+"/picture?type=large",
                            email: response.email
                         }
                        console.log(data);


                        $scope.loginUser = {
                            uc:response.id, ut:1, dt:3, fn:1
                        }
                        $rootScope.rg_gl = {
                            currentUser: {
                                ut:  $scope.loginUser.ut,
                                ui:  $scope.loginUser.ui,
                                sk:  $scope.loginUser.sk,
                                lk:  $scope.loginUser.lk,
                                userInfo: $scope.loginUser.userInfo
                            }
                        }
                        $cookieStore.put('rg_gl', $rootScope.rg_gl);          
                        console.log($scope.loginUser);
                        $state.go('index');


                    });
                } else {
                    console.log('User cancelled login or did not fully authorize.');
                }
            });
        }

        //google login
        $scope.Googlelogin = function (authResult) {
            console.log(typeof auth2);
            if (typeof auth2 === "undefined") {
                setTimeout(googleLogin, 5000);
                return;
            }
            auth2.signIn().then(function(googleUser) {
                    console.log("google login success");
                    var profile = googleUser.getBasicProfile();
                    var data = {
                        id: profile.getId(),
                        name: profile.getName(),
                        img: profile.getImageUrl(),
                        email: profile.getEmail()
                    };
                    console.log(data);


                    $scope.loginUser = {
                            uc:data.id, ut:1, dt:3, fn:1
                    }
                    $rootScope.rg_gl = {
                        currentUser: {
                            ut:  $scope.loginUser.ut,
                            ui:  $scope.loginUser.ui,
                            sk:  $scope.loginUser.sk,
                            lk:  $scope.loginUser.lk,
                            userInfo: $scope.loginUser.userInfo
                        }
                    }
                    $cookieStore.put('rg_gl', $rootScope.rg_gl);
                    console.log($scope.loginUser);
                    $state.go('index');

                    
                },
                function(error) {
                    console.log("google login error");
                });
        }
        
    }
)

