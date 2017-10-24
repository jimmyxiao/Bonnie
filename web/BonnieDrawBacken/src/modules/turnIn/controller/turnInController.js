app.factory('turnInService', function(baseHttp) {
    return {
    	queryTurnInList: function(params,callback){
			return baseHttp.service('turnInManager/queryTurnInList',params,callback);
		}
    }
})
.controller('turnInController', function ($rootScope, $scope, $window ,$location, $http, $filter, $state, $modal, turnInService, util) {
	var opt = {
		"oLanguage":{"sUrl":"dataTables.zh-tw.txt"},
		"bJQueryUI":true,
		"sPaginationType":'full_numbers',
		data: $scope.userInfoList,
		columns: [
			{ title: "項次",sWidth:"10%",render: function(data, type, full, meta) {
					return (meta.row + 1);
				}
			},
			{ title: "檢舉人ID",data: "userId",sWidth:"15%" },
			{ title: "作品ID",data: "worksId",sWidth:"10%" },
			{ title: "類型",sWidth:"10%",data: function(data, type, full) {
					if(util.isEmpty(data.turnInType)){
						return '';
					}
					switch(data.turnInType){
						case 1 :
							return '色情';
						case 2:
							return '暴力';
						default:
							return '未分類';
					} 
				}
			},
			{ title: "內容",data: "description",sWidth:"30%" },
			{ title: "狀態",sWidth:"10%",data: function(data, type, full) {
					if(util.isEmpty(data.status)){
						return '';
					}
					switch(data.status){
						case 1 :
							return '已檢舉';
						case 2:
							return '已處理';
						default:
							return '未分類';
					} 
				}
			},
			{ title: "日期",sWidth:"15%",data: function(data, type, full) {
					if(util.isEmpty(data.creationDate)){
						return '';
					}
					return util.formatDate(data.creationDate);
				}
			}
		]
	}
	var myTable = $('#dynamic-table').DataTable(opt);

	$scope.queryTurnInList = function(){
		turnInService.queryTurnInList(null,function(data, status, headers, config){
			if(data.result){
				$scope.turnInList = data.data;
				util.refreshDataTable(myTable, $scope.turnInList);
			}
		})
	}
	$scope.queryTurnInList();
	
})
