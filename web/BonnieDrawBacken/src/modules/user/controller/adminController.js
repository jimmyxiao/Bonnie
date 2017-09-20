app.factory('adminService', function(baseHttp) {
    return {
    	queryAdminList: function(params,callback){
			return baseHttp.service('admin/queryAdminList',params,callback);
		},
		queryAdminInfo : function(params,callback){
			return baseHttp.service('admin/queryAdminInfo',params,callback);
		},
		insertAdminInfo: function(params,callback){
			return baseHttp.service('admin/insertAdminInfo',params,callback);
		},
		updateAdminInfo: function(params,callback){
			return baseHttp.service('admin/updateAdminInfo',params,callback);
		},
		removeAdminInfo: function(params,callback){
			return baseHttp.service('admin/removeAdminInfo',params,callback);
		}
    }
})
.controller('adminController', function ($rootScope,$scope, $window ,$location, $http, $filter, $state, $modal, adminService, util) {
	$scope.isAdd = false;
	var initAdminInfo = {
		userType:1,
		userCode:null,
		userPw:null,
		userName:null,
		email:null
	}

	var columns = [
		{ title: "帳號",data: "userCode",sWidth:"20%" },
		{ title: "名稱",data: "userName",sWidth:"20%" },
		{ title: "權限",sWidth:"10%",data: function(data, type, full) {
			switch(data.userType){
				case 0:
					return '系統維護';
				case 1 :
					return '管理員';
				default:
					return '未分類';
				} 
			}
		},
		{data:null,"bSortable": false,sWidth:"20%",render: function(data, type, full, meta) {
			var row = meta.row;
			var deleteBtn = (data.adminId !=1 ?
				'<button name="remove" class="btn btn-danger btn-sm" value="'+ row +'"><i class="ace-icon fa fa-trash-o bigger-130"></i>刪除</button>' : '');
			var btnStr=
				'<div class="hidden-sm hidden-xs action-buttons">' +
				'<button name="edit" class="btn btn-primary btn-sm" value="'+ row +'"><i class="ace-icon fa fa-pencil bigger-130"></i>編輯</button>' +
				'&nbsp;'+
				deleteBtn +
				'</div>'
				return btnStr;
			}
		}
	]

	var opt = {
		"oLanguage":{"sUrl":"dataTables.zh-tw.txt"},
		"bJQueryUI":true,
		"sPaginationType":'full_numbers',
		data: $scope.adminList,
		columns: columns
	}
	var myTable = $('#dynamic-table').DataTable(opt);
	$('#dynamic-table tbody').on( 'click', 'button[name="edit"]',function() {			   
		var index = $(this).context.value;
		$scope.edit($scope.adminList[index]);
	});

	$('#dynamic-table tbody').on( 'click', 'button[name="remove"]',function() {			   
		var index = $(this).context.value;
		$scope.remove($scope.adminList[index]);
	});

	$scope.goBack = function(){
		util.confirm('確定要返回?', function(r) {
			if (r) {
				$scope.isAdd = false;
			}
		})
	}

	$scope.queryAdminList = function(){
		adminService.queryAdminList(null,function(data, status, headers, config){
			if(data.result){
				$scope.adminList = data.data;
				util.refreshDataTable(myTable,$scope.adminList);
			}
		})
	}
	$scope.queryAdminList();
	
	$scope.remove = function(data){
		util.confirm('確定要刪除 <font color="red">'+ data.userName +'</font>', function(r) {
			if (r) {
				adminService.removeAdminInfo(data.adminId,function(data, status, headers, config){
					if(data.result){
						$scope.queryAdminList();
					}
					util.alert(data.message);
				})
			}
		})
	}

//	======== add & edit controller start ========
	$scope.queryAdminInfo = function(data){
		if(data.adminId == null){
			util.alert('非法資料');
			return;
		}
		adminService.queryAdminInfo(data.adminId,function(data, status, headers, config){
			if(data.result){
				$scope.adminInfo = data.data;
			}
		})
	}

	$scope.add = function(){
		$scope.isAdd = true;
		$scope.adminInfo = util.clone(initAdminInfo);
	}

	$scope.edit = function(data){
		$scope.isAdd = true;
		$scope.queryAdminInfo(data);
	}

	$scope.save = function(valid){
		if(valid){
			if($scope.adminInfo.adminId==null){
				adminService.insertAdminInfo($scope.adminInfo,function(data, status, headers, config){
					if(data.result){
						$scope.isAdd = false;
						$scope.queryAdminList();
					}
					util.alert(data.message);
				})
			}else{
				adminService.updateAdminInfo($scope.adminInfo,function(data, status, headers, config){
					if(data.result){
						$scope.isAdd = false;
						$scope.queryAdminList();
					}
					util.alert(data.message);
				})
			}	
		}else{
			util.alert('必填欄位未輸入');
		}
	}

//	======== add & edit controller end ======== 

})
