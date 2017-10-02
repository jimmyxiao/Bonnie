app.factory('userService', function(baseHttp) {
    return {
        updateUser : function(params, callback) {
            return baseHttp.service('/BDService/userInfoUpdate', params, callback);
        }
    }
})
.controller('myfileController', function ($cookieStore, $rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, userService) {
		$rootScope.title = '帳號設定 | BonnieDRAW';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
		
		$scope.status = $state.params.v;
		if(util.isEmpty($scope.status)){
			$scope.tagView = 'profile';
		}else{
			$scope.tagView = 'modifyPwd';
		}

		$scope.user = util.clone($rootScope.rg_gl.currentUser.userInfo);
		var baseBean = util.getInitalScope();
		$scope.user.ui = baseBean.ui;
		$scope.user.lk = baseBean.lk;
		$scope.user.dt = baseBean.dt;

		$scope.switchView = function(tagView){
			$scope.tagView = tagView;
		} 

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

		$scope.commitPwd = function(valid){
			if(valid){
				// not uses api
			}
		}

	}
)