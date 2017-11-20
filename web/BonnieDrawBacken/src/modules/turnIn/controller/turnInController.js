app.factory('turnInService', function(baseHttp) {
    return {
    	queryTurnInWorkList: function(params,callback){
			return baseHttp.service('turnInManager/queryTurnInWorkList',params,callback);
		},
    	queryTurnInList: function(params,callback){
			return baseHttp.service('turnInManager/queryTurnInList',params,callback);
		},
		changeStatus:function(params,callback){
			return baseHttp.service('turnInManager/changeStatus',params,callback);
		}
    }
})
.controller('turnInController', function ($rootScope, $scope, $window ,$location, $http, $filter, $state, $modal, turnInService, util) {
	var opt = {
		"oLanguage":{"sUrl":"dataTables.zh-tw.txt"},
		"bJQueryUI":true,
		"sPaginationType":'full_numbers',
		data: $scope.turnInWorkList,
		columns: [
			{ title: "項次",sWidth:"10%",render: function(data, type, full, meta) {
					return (meta.row + 1);
				}
			},
			{ title: "作者",data: "userName",sWidth:"20%" },
			{ title: "標題",data: "title",sWidth:"20%" },
			{ title: "圖檔位置", data: "imagePath", sWidth:"20%" },
			{data:null,"bSortable": false,sWidth:"30%",render: function(data, type, full, meta) {
				var row = meta.row;
				var btnStr =
					'<div class="hidden-sm hidden-xs action-buttons">' +
					'<button name="detail" class="btn btn-primary btn-sm" value="'+ row +'"><i class="ace-icon fa fa-pencil bigger-130"></i>明細</button>' +
					'&nbsp;'+
					'</div>'
					return btnStr;
				}
			}
		]
	}
	var myTable = $('#dynamic-table').DataTable(opt);

	$('#dynamic-table tbody').on( 'click', 'button[name="detail"]',function() {			   
		var index = $(this).context.value;
		$scope.queryTurnInList($scope.turnInWorkList[index]);
	});

	$scope.queryTurnInWorkList = function(){
		turnInService.queryTurnInWorkList(null,function(data, status, headers, config){
			if(data.result){
				$scope.turnInWorkList = data.data;
				util.refreshDataTable(myTable, $scope.turnInWorkList);
			}
		})
	}
	$scope.queryTurnInWorkList();

	
	$scope.queryTurnInList = function(data){
        var modalInstance = $modal.open({
            templateUrl : 'modules/turnIn/view/dialog/turnInList.html',
            scope:$scope,
            size:'lg',
            controller : function($scope, $modalInstance, util) {
                $scope.close = function() {
                    $modalInstance.dismiss('cancel');
                }

                $scope.changeStatus = function(index){
					turnInService.changeStatus($scope.turnInList[index],function(data, status, headers, config){
						if(data.result){
							$scope.turnInList[index] = data.data;
							mySubTable.row(index).data(data.data).invalidate();
						}
					})
				}

				var mySubTable;
                turnInService.queryTurnInList(data,function(data, status, headers, config){
					if(data.result){
						$scope.turnInList = data.data;
						var opt_sub = {
							"oLanguage":{"sUrl":"dataTables.zh-tw.txt"},
							"bJQueryUI":true,
							"sPaginationType":'full_numbers',
							data: $scope.turnInList,
							columns: [
								{ title: "項次",sWidth:"10%",render: function(data, type, full, meta) {
										return (meta.row + 1);
									}
								},
								{ title: "檢舉人ID",data: "userId",sWidth:"10%" },
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
								{ title: "內容",data: "description",sWidth:"35%" },
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
								{ title: "日期",sWidth:"10%",data: function(data, type, full) {
										if(util.isEmpty(data.creationDate)){
											return '';
										}
										return util.formatDate(data.creationDate);
									}
								},
								{data:null,"bSortable": false,sWidth:"15%",render: function(data, type, full, meta) {
									var row = meta.row;
									var str = '';
									if(data.status == 1){
										str = '處理完畢';
									}else{
										str = '進行檢舉';
									}
									var statusBtn = '<button name="status" class="btn btn-danger btn-sm" value="'+ row +'"><i class="ace-icon fa fa-trash-o bigger-130"></i>' + str + '</button>';
									var btnStr =
										'<div class="hidden-sm hidden-xs action-buttons">' +
										statusBtn +
										'</div>'
										return btnStr;
									}
								}
							]
						}
						mySubTable = $('#dynamic-table-sub').DataTable(opt_sub);

						$('#dynamic-table-sub tbody').on( 'click', 'button[name="status"]',function() {			   
							var index = $(this).context.value;
							$scope.changeStatus(index);
						});
					}
				})
            }
        })
    }

})
