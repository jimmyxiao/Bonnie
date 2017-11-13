app.factory('notiMsgSetService', function(baseHttp) {
    return {
		queryNotiMsgSetting: function(params,callback){
			return baseHttp.service('systemSetup/notification/queryNotiMsgSetting',params,callback);
		},
		saveNotiMsgSetting: function(params,callback){
			return baseHttp.service('systemSetup/notification/saveNotiMsgSetting',params,callback);
		},
		getDictionaryList : function(params, callback) {
            return baseHttp.service('BDService/dictionaryList', params, callback);
        }
    }
})
.controller('notiMsgSetController', function ($rootScope, $scope, $window ,$location, $http, $filter, $state, notiMsgSetService, util) {
	$scope.selectModel = {
		notiMsgType:1,
		languageCode:null,
		message1:null
	}

	var param = {};
	param.dictionaryType = 1;
	param.dictionaryID = 0;
	$scope.dictionaryList =[];
	notiMsgSetService.getDictionaryList(param, function(data, status, headers, config) {
	    $scope.dictionaryList = data.dictionaryList;
	    if($scope.dictionaryList.length>0){
	        $scope.selectModel.languageCode = $scope.dictionaryList[0].dictionaryCode;
	        $scope.queryNotiMsgSetting();
	    }
	})
	

	$scope.queryNotiMsgSetting = function(){
		if(!util.isEmpty($scope.selectModel.notiMsgType) && !util.isEmpty($scope.selectModel.languageCode)){
			notiMsgSetService.queryNotiMsgSetting($scope.selectModel, function(data, status, headers, config){
				if(data.result){
					$scope.selectModel.message1 = data.data;
				}
			})
		}
	}

	$scope.save = function(valid){
		if(valid){
			notiMsgSetService.saveNotiMsgSetting($scope.selectModel, function(data, status, headers, config){
				if(data.result){
					$scope.queryNotiMsgSetting();
				}
				util.alert(data.message);
			})
		}else{
			// util.alert('必填欄位未輸入');
		}
	}

})
