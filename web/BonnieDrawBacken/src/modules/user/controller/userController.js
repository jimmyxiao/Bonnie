app.factory('userService', function(baseHttp) {
    return {
    	queryUserList: function(params,callback){
			return baseHttp.service('user/queryUserList',params,callback);
		}
    }
})
.controller('userController', function ($rootScope, $scope, $window ,$location, $http, $filter, $state, $modal, userService, util) {
	var opt = {
		"oLanguage":{"sUrl":"dataTables.zh-tw.txt"},
		"bJQueryUI":true,
		"sPaginationType":'full_numbers',
		data: $scope.userInfoList,
		columns: [
			{ title: "項次",sWidth:"20%",render: function(data, type, full, meta) {
					return (meta.row + 1);
				}
			},
			{ title: "帳號",data: "userCode",sWidth:"30%" },
			{ title: "名稱",data: "userName",sWidth:"30%" },
			{ title: "類型",sWidth:"20%",data: function(data, type, full) {
					if(util.isEmpty(data.userType)){
						return '';
					}
					switch(data.userType){
						case 1 :
							return 'email';
						case 2:
							return 'facebook';
						case 3:
							return 'google';
						case 4:
							return 'twitter';
						default:
							return '未分類';
					} 
				}
			}
		]
	}
	var myTable = $('#dynamic-table').DataTable(opt);

	$scope.queryUserList = function(){
		userService.queryUserList(null,function(data, status, headers, config){
			if(data.result){
				$scope.userInfoList = data.data;
				util.refreshDataTable(myTable,$scope.userInfoList);
			}
		})
	}
	$scope.queryUserList();
	
})
