app.factory('registerService', function(baseHttp) {
    return {
        register : function(params, callback) {
            return baseHttp.service('BDService/login', params, callback);
        }
    }
})
.controller('registerController', function ($scope, $rootScope, $location, $cookieStore, $window, $state, $http, registerService, AuthenticationService) {
    	$rootScope.title = '註冊 | BonnieDRAW';
        $scope.register = {
    		phoneNo:null, userName:null, userCode:null, userPw:null, userType:1 
    	}
        $scope.callRegister = function (valid) {
        	if(valid){
                var params = {
                    phoneNo:$scope.register.phoneNo,
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
        
      //facebook login
        $scope.Facebooklogin = function () {
        	console.log("facebook clik");
            FB.login(function(response) {
                if (response.authResponse) {
                    console.log('Welcome!  Fetching your information.... ');
                    FB.api('/me','GET', {fields : 'id,name,picture,email,gender'}, function(response) {
                         var data = {
                        	uc: response.id,
                        	un: response.name,
                        	thirdPictureUrl: response.picture.data.url,
                        	thirdEmail: response.email,
                        	ut:2,
    					    dt:3,
    					    fn:1
                         }
                         console.log(data);
                         console.log('You are signed in to facebook');
                         $scope.dataLoading = true;
            			 $scope.loginUser = data;
            			 
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

                    });
                } else {
                    console.log('User cancelled login or did not fully authorize.');
                }
            },{scope: 'email'});
        }

        //google login
        $scope.Googlelogin = function (authResult) {
        	console.log("google clik");
            //console.log(typeof auth2);
            if (typeof auth2 === "undefined") {
                setTimeout(googleLogin, 5000);
                return;
            }
            auth2.signIn().then(function(googleUser) {
                    //console.log("google login success");
                    var profile = googleUser.getBasicProfile();
                    var data = {
                    	uc: profile.getId(),//id
                    	un: profile.getName(),//name
                    	thirdPictureUrl: profile.getImageUrl(),
                        thirdEmail: profile.getEmail(),//email
                        ut:3,
                        dt:3,
                        fn:1
                    };
                    console.log(data);
                    console.log('You are signed in to google');
                    $scope.dataLoading = true;
       			    $scope.loginUser = data;
       			 
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
     	            
                },
                function(error) {
                    console.log("google login error");
                });
        }
        
        //twitter login
        $scope.Twitterlogin = function () {
        	console.log("twitter clik");
        	hello('twitter').login().then(function() {
        		hello('twitter').api('me','get', {include_email : true}).then(function(json) {
        			 var data = {
						uc: json.id,//id
						un: json.name,//name
						thirdPictureUrl: json.profile_image_url_https,
						thirdEmail: json.email,//email
					    ut:4,
					    dt:3,
					    fn:1
        			 };
        			 console.log(data);
        			 console.log('You are signed in to twitter');
        			 $scope.dataLoading = true;
        			 $scope.loginUser = data;
        			 
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
        			 
            	}, function(e) {
            		console.log('Whoops! ' + e.error.message);
            	});
        	}, function(e) {
        		console.log('Signin error: ' + e.error.message);
        	});
        	
        }
        
    }
)

