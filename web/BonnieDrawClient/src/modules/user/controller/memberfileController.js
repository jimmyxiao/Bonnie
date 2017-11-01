app.controller('memberfileController', function ($cookieStore, $rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, Upload, userService, localStorageService) {
		$rootScope.title = '帳戶設定 | BonnieDRAW';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
		
		$scope.user = util.clone($rootScope.rg_gl.currentUser.userInfo);
		var baseBean = util.getInitalScope();
		$scope.file={
			imagePath:[]
		}
		$scope.upload = function(file) {
		    var parameter = 'ui=' + baseBean.ui 
		    	+ '&lk=' + baseBean.lk 
		    	+ '&dt=' + baseBean.dt 
		    	+ '&fn=2';

			if (file) {
				Upload.upload({
					url: rootApi + 'fileUpload?' + parameter,
					data: {
						file: file
			        }
			    }).then(function(resp) {
			        var data = resp.data;
					if(data.res==1){
						$scope.user.profilePicture = data.profilePicture;
						$rootScope.rg_gl.currentUser.userInfo.profilePicture = data.profilePicture;
						if(localStorageService.isSupported){
                			localStorageService.set('rg_gl', $rootScope.rg_gl);
            			}else{
                			$cookieStore.put('rg_gl', $rootScope.rg_gl);
            			}
						alert('上傳成功');
					}else{
						alert('上傳失敗');
					}
				}, function(resp) {
				}, function(evt) {
					$scope.progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
				});
			}
		}


		$scope.status = $state.params.v;
		if(util.isEmpty($scope.status)){
			$scope.tagView = 'profile';
			$scope.user.ui = baseBean.ui;
			$scope.user.lk = baseBean.lk;
			$scope.user.dt = baseBean.dt;

			$scope.commitProfile = function(valid){
				if(valid){
					userService.updateUser($scope.user, function(data, status, headers, config) {
	        			if(data.res == 1 || data.res == 3){
	        				$rootScope.rg_gl.currentUser.userInfo = util.clone($scope.user);
	        				if(localStorageService.isSupported){
				                localStorageService.set('rg_gl', $rootScope.rg_gl);
				            }else{
				                $cookieStore.put('rg_gl', $rootScope.rg_gl);
				            }
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