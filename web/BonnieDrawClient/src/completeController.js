app.controller('completeController', function ($scope, $rootScope, $location, $cookieStore, $window, $state, $http, AuthenticationService) {
    	$rootScope.title = ' | BonnieDRAW';
    	console.log('call completeController !!!');
    	var params = $state.params;
    	var url = rootUrl + 'ws/complete?token='+ params.token;

		$http.post(url, params).success(
			function(data, status, headers, config) {
				if(data.result){
					alert("完成驗證");
					$state.go('login');
				}else{
					alert(data.message);
				}
			}
		).error(function(data, status, headers, config) {
			alert('發送失敗');
		});

    }
)

