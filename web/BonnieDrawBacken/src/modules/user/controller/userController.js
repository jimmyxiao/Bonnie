app.factory('userService', function(baseHttp) {
    return {
    	queryUserList: function(params,callback){
			return baseHttp.service('user/queryUserList',params,callback);
		},
		changeStatus: function(params,callback){
			return baseHttp.service('user/changeStatus',params,callback);
		},
		queryUserDetail: function(params,callback){
			return baseHttp.service('user/queryUserDetail',params,callback);
		},
		changeUserGroup: function(params,callback){
            return baseHttp.service('user/changeUserGroup',params,callback);
        }
    }
})
.controller('userController', function ($rootScope, $scope, $window ,$location, $http, $filter, $state, $modal, userService, util) {
	$scope.search = {
		userName:null,
		email:null,
		status: -1,
		userGroup: -1
	}

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
			{ title: "帳號",data: "userCode",sWidth:"30%" },
			{ title: "名稱",data: "userName",sWidth:"30%" },
			{ title: "類型",sWidth:"10%",data: function(data, type, full) {
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
			},
			{data:null,"bSortable": false,sWidth:"20%",render: function(data, type, full, meta) {
				var row = meta.row;
				var str = '';
				if(data.status == 1){
					str = '停用';
				}else{
					str = '啟用';
				}
				var editBtn = '<button name="edit" class="btn btn-primary btn-sm marginbottom4" value="'+ row +'"><i class="ace-icon fa fa-trash-o bigger-130"></i>修改</button>';
				var statusBtn = '<button name="status" class="btn btn-danger btn-sm marginbottom4" value="'+ row +'"><i class="ace-icon fa fa-trash-o bigger-130"></i>' + str + '</button>';
				var btnStr =
					'<div class="hidden-sm hidden-xs action-buttons">' +
					'<button name="detail" class="btn btn-primary btn-sm marginbottom4" value="'+ row +'"><i class="ace-icon fa fa-pencil bigger-130"></i>明細</button>' +
					'&nbsp;'+
					statusBtn +
					'&nbsp;'+
					editBtn+
					'</div>'
					return btnStr;
				}
			}
		]
	}
	var myTable = $('#dynamic-table').DataTable(opt);

	$('#dynamic-table tbody').on( 'click', 'button[name="detail"]',function() {			   
		var index = $(this).context.value;
		$scope.queryUserDetail($scope.userInfoList[index]);
	});

	$('#dynamic-table tbody').on( 'click', 'button[name="status"]',function() {			   
		var index = $(this).context.value;
		$scope.changeStatus(index);
	});

	$('#dynamic-table tbody').on( 'click', 'button[name="edit"]',function() {              
        var index = $(this).context.value;
        $scope.edit(index);
    });

	$scope.changeStatus = function(index){
		userService.changeStatus($scope.userInfoList[index],function(data, status, headers, config){
			if(data.result){
				$scope.userInfoList[index] = data.data;
				myTable.row(index).data(data.data).invalidate();
			}
			 $window.location.reload();
		})
	}

	$scope.edit = function(index){
        $scope.loading = false;
        $scope.editUser = {
            index:index,
            data:util.clone($scope.userInfoList[index])
        }
        console.log($scope.editUser);
        var modalInstance = $modal.open({
            templateUrl : 'modules/user/view/dialog/editUser.html',
            backdrop:'static',
            scope:$scope,
            controller : 'editUserController'
        })
    }

	 $scope.queryUserDetail = function(data){
        var modalInstance = $modal.open({
            templateUrl : 'modules/user/view/dialog/userDetail.html',
            scope:$scope,
            size:'lg',
            controller : function($scope, $modalInstance, util) {
            	$scope.userDetail = null;
                $scope.close = function() {
                    $modalInstance.dismiss('cancel');
                }

                userService.queryUserDetail(data,function(data, status, headers, config){
                    if(data.result){
                        $scope.userDetail = data.data;
                    }
                });
            }
        })
    }

	$scope.queryUserList = function(){
		userService.queryUserList($scope.search,function(data, status, headers, config){
			if(data.result){
				$scope.userInfoList = data.data;
				util.refreshDataTable(myTable,$scope.userInfoList);
			}else{
				$scope.reloadTable();
			}
		})
	}
	$scope.queryUserList();

	$scope.reloadTable = function(){
        myTable.rows().invalidate();
    }
	
})
.controller('editUserController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modalInstance, util, userService) {
    $scope.close = function() {
        $scope.editUser = null;
        $scope.reloadTable();
        $modalInstance.dismiss('cancel');
    }

    $scope.updateCustomTag = function(){
         if($scope.editUser.data.userGroup<0){
            util.alert('順序預設至少為0');
        }else{
            $scope.loading = true;
            userService.changeUserGroup($scope.editUser.data,function(data, status, headers, config){
            	console.log(data);
                if(data.result){
                    $scope.queryUserList();
                    $scope.close();
                    util.alert('更新成功');
                }else{
                    util.alert('更新失敗');
                }
                $scope.loading = false;
            })
        }
    }
   
})
