app.factory('workService', function(baseHttp) {
    return {
    	queryWorkList: function(params,callback){
			return baseHttp.service('work/queryWorkList',params,callback);
		},
		changeStatus: function(params,callback){
			return baseHttp.service('work/changeStatus',params,callback);
		},
		queryWorkDetail: function(params,callback){
			return baseHttp.service('work/queryWorkDetail',params,callback);
		}
    }
})
.controller('workController', function ($rootScope, $scope, $window ,$location, $http, $filter, $state, $modal, workService, util) {
	$scope.search = {
		userName:null,
		email:null,
		title:null,
		description:null,
		tagName:null
	}

	var opt = {
		"oLanguage":{"sUrl":"dataTables.zh-tw.txt"},
		"bJQueryUI":true,
		"sPaginationType":'full_numbers',
		data: $scope.workInfoList,
		columns: [
			{ title: "項次",sWidth:"10%",render: function(data, type, full, meta) {
					return (meta.row + 1);
				}
			},
			{ title: "作者",data: "userName",sWidth:"20%" },
			{ title: "標題",data: "title",sWidth:"20%" },
			{ title: "說明",sWidth:"30%", data:"description" },
			{data:null,"bSortable": false,sWidth:"20%",render: function(data, type, full, meta) {
				var row = meta.row;
				var str = '';
				if(data.status == 1){
					str = '下架';
				}else{
					str = '上架';
				}
				var statusBtn = '<button name="status" class="btn btn-danger btn-sm" value="'+ row +'"><i class="ace-icon fa fa-trash-o bigger-130"></i>' + str + '</button>';
				var btnStr =
					'<div class="hidden-sm hidden-xs action-buttons">' +
					'<button name="detail" class="btn btn-primary btn-sm" value="'+ row +'"><i class="ace-icon fa fa-pencil bigger-130"></i>明細</button>' +
					'&nbsp;'+
					statusBtn +
					'</div>'
					return btnStr;
				}
			}
		]
	}
	var myTable = $('#dynamic-table').DataTable(opt);

	$('#dynamic-table tbody').on( 'click', 'button[name="detail"]',function() {			   
		var index = $(this).context.value;
		$scope.queryWorkDetail($scope.workInfoList[index]);
	});

	$('#dynamic-table tbody').on( 'click', 'button[name="status"]',function() {			   
		var index = $(this).context.value;
		$scope.changeStatus(index);
	});

	$scope.changeStatus = function(index){
		workService.changeStatus($scope.workInfoList[index],function(data, status, headers, config){
			if(data.result){
				$scope.workInfoList[index] = data.data;
				myTable.row(index).data(data.data).draw();
			}
		})
	}

	 $scope.queryWorkDetail = function(data){
        var modalInstance = $modal.open({
            templateUrl : 'modules/works/view/dialog/workDetail.html',
            scope:$scope,
            size:'lg',
            controller : function($scope, $modalInstance, util) {
            	$scope.workDetail = null;
                $scope.close = function() {
                    $modalInstance.dismiss('cancel');
                }

                workService.queryWorkDetail(data,function(data, status, headers, config){
                    if(data.result){
                        $scope.workDetail = data.data;
                    }
                });
            }
        })
    }

	$scope.queryWorkList = function(){
		workService.queryWorkList($scope.search,function(data, status, headers, config){
			if(data.result){
				$scope.workInfoList = data.data;
				util.refreshDataTable(myTable,$scope.workInfoList);
			}
		})
	}
	// $scope.queryWorkList();
	
})
