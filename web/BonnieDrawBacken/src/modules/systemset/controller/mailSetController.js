app.factory('mailSetService', function(baseHttp) {
    return {
		queryMailSetting: function(params,callback){
			return baseHttp.service('systemSetup/mail/queryMailSetting',params,callback);
		},
		saveMailSetting: function(params,callback){
			return baseHttp.service('systemSetup/mail/saveMailSetting',params,callback);
		}
    }
})
.controller('mailSetController', function ($rootScope, $scope, $window ,$location, $http, $filter, $state, mailSetService, util) {
	var initSys = {
		mailHost:null,
		mailPort:25,
		mailUsername:null,
		mailPassword:null,
		mailProtocol:null
	}
	$scope.sys = util.clone(initSys);

	$scope.queryMailSetting = function(){
		mailSetService.queryMailSetting(null,function(data, status, headers, config){
			if(data.result){
				$scope.sys = data.data;
			}
		})
	}
	$scope.queryMailSetting();

	$scope.save = function(valid){
		if(valid){
			mailSetService.saveMailSetting($scope.sys,function(data, status, headers, config){
				if(data.result){
					$scope.queryMailSetting();
				}
				util.alert(data.message);
			})
		}else{
			util.alert('必填欄位未輸入');
		}
	}

})
