app.factory('directoryService', function(baseHttp) {
    return {
    	queryDirectoryList: function(params,callback){
			return baseHttp.service('directoryManager/queryDirectoryList',params,callback);
		}
    }
})
.controller('directoryController', function ($rootScope,$scope, $window ,$location, $http, $filter, $state, $modal, directoryService, util) {

	$scope.queryMailSetting = function(){
		directoryService.queryDirectoryList(null,function(data, status, headers, config){
			if(data.result){
				$scope.basicTree = data.data;
			}
		})
	}
	$scope.queryMailSetting();


	// $scope.basicTree=[{
 //        nodeId:0, name: "Node 1", disabled: true, expanded:false,
 //        children: [{
 //            name: "Node 1.1", disabled: true,
 //            children:[{}]
 //        }]
 //    }];

    $scope.options = {
    	showIcon : true,
        onSelectNode : function (node) {
            $scope.selectedNodes = node;
            console.log('Selete Node :');
            console.log(node);
        },
        onExpandNode : function (node) {
            $scope.expandedNode = node;
            console.log('Expand Node :');
            console.log(node);
        },
    	filter : {}
    };

})
