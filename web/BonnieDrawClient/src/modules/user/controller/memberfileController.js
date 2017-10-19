app.controller('myfileController', function ($cookieStore, $rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, userService) {
		$rootScope.title = '帳號設定 | BonnieDRAW';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
		
		var baseBean = util.getInitalScope();
		$scope.status = $state.params.v;
		if(util.isEmpty($scope.status)){
			$scope.tagView = 'profile';
			$scope.user = util.clone($rootScope.rg_gl.currentUser.userInfo);
			$scope.user.ui = baseBean.ui;
			$scope.user.lk = baseBean.lk;
			$scope.user.dt = baseBean.dt;

			$scope.commitProfile = function(valid){
				if(valid){
					userService.updateUser($scope.user, function(data, status, headers, config) {
	        			if(data.res == 1 || data.res == 3){
	        				$rootScope.rg_gl.currentUser.userInfo = util.clone($scope.user);
	        				$cookieStore.put('rg_gl', $rootScope.rg_gl);
	        				alert('更新成功');
	        			}else{
	        				alert(data.msg);
	        			}
	        		})
				}
			}
		}else{
			$scope.tagView = 'modifyPwd';
			$scope.param = {
				oldPwd:null,
				newPwd:null,
				confirmPwd:null
			}
			$scope.commitPwd = function(valid){
				if(valid && $scope.param.newPwd == $scope.param.confirmPwd){
					baseBean.newPwd = $scope.param.newPwd;
					baseBean.oldPwd = $scope.param.oldPwd;
					userService.updatePwd(baseBean, function(data, status, headers, config) {
	        			if(data.res == 1){
	        				$scope.param = {
								oldPwd:null,
								newPwd:null,
								confirmPwd:null
							}
	        				alert('更新成功');
	        			}else{
	        				alert(data.msg);
	        			}
	        		})
				}else{
					alert('密碼不符');
				}
			}
		}

		$scope.switchView = function(tagView){
			$scope.tagView = tagView;
		} 

	}
)