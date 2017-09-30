app.factory('directoryService', function(baseHttp) {
    return {
        getBreadCrumbs: function(params,callback){
            return baseHttp.service('directoryManager/getBreadCrumbs',params,callback);
        },
    	queryDirectoryList: function(params,callback){
			return baseHttp.service('directoryManager/queryDirectoryList',params,callback);
		},
        createDirectory: function(params,callback){
            return baseHttp.service('directoryManager/createDirectory',params,callback);
        },
        updateDirectory: function(params,callback){
            return baseHttp.service('directoryManager/updateDirectory',params,callback);
        },
        removeDirectory: function(params,callback){
            return baseHttp.service('directoryManager/removeDirectory',params,callback);
        }
    }
})
.controller('addDirController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modalInstance, util, directoryService) {
    $scope.close = function() {
        $modalInstance.dismiss('cancel');
    }

    $scope.insertDir = function(){
        $scope.loading = true;
        console.log($scope.createDir);
        directoryService.createDirectory($scope.createDir,function(data, status, headers, config){
            if(data.result){
                $scope.queryDirectoryList();
                $scope.close();
                util.alert('創建成功');
            }else{
                util.alert('創建失敗');
            }
            $scope.loading = false;
        })
    }
})
.controller('editDirController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modalInstance, util, directoryService) {
    $scope.close = function() {
        $scope.editDir = null;
        $scope.reloadTable();
        $modalInstance.dismiss('cancel');
    }

    $scope.updateDir = function(){
        $scope.loading = true;
        directoryService.updateDirectory($scope.editDir.data,function(data, status, headers, config){
            if(data.result){
                $scope.basicTree[$scope.editDir.index].categoryName = data.data.categoryName;
                $scope.close();
                util.alert('更新成功');
            }else{
                util.alert('更新失敗');
            }
            $scope.loading = false;
        })
    }
})
.controller('directoryController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, directoryService, util) {
    var categoryParentId = $state.params.cp;
    if(util.isEmpty(categoryParentId)){
        categoryParentId = 0;
        $scope.showBack = false;
    }else{
        $scope.showBack = true;
    }

    $scope.getBreadCrumbs = function(){
        if(categoryParentId != 0) {
            directoryService.getBreadCrumbs(categoryParentId,function(data, status, headers, config){
                if(data.result){
                  $scope.breadCrumbs = data.data;
                }
            })
        }
    }
    $scope.getBreadCrumbs();

    var columns = [
        { title: "項次", sWidth:"10%", render: function(data, type, full, meta) {
                return (meta.row + 1);
            }
        },
        { title: "名稱", data: "categoryName", sWidth:"20%"},
        { title: "狀態", sWidth:"10%", data: function(data, type, full) {
                if(data.enable){
                    return '啟用';
                }else{
                    return '不啟用';
                }
            }
        },
        {data:null,"bSortable": false,sWidth:"20%",render: function(data, type, full, meta) {
            var row = meta.row;
            var deleteBtn = '';    
            var editBtn='';
            if(data.categoryId>20){
                deleteBtn = '<button name="remove" class="btn btn-danger btn-sm" value="'+ row +'"><i class="ace-icon fa fa-trash-o bigger-130"></i>刪除</button>';
                editBtn = '<button name="edit" class="btn btn-primary btn-sm" value="'+ row +'"><i class="ace-icon fa fa-trash-o bigger-130"></i>修改</button>';
            }

            var btnStr=
                '<div class="hidden-sm hidden-xs action-buttons">' +
                '<button name="success" class="btn btn-success btn-sm" value="'+ row +'"><i class="ace-icon fa fa-pencil bigger-130"></i>查看</button>' +
                '&nbsp;'+
                editBtn + 
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
        data: $scope.basicTree,
        columns: columns
    }
    var myTable = $('#dynamic-table').DataTable(opt);
    $('#dynamic-table tbody').on( 'click', 'button[name="success"]',function() {              
        var index = $(this).context.value;
        var data = $scope.basicTree[index];
        $window.location.href = $location.absUrl().split("?")[0] + '?cp=' + data.categoryId;
    });

    $('#dynamic-table tbody').on( 'click', 'button[name="edit"]',function() {              
        var index = $(this).context.value;
        $scope.edit(index);
    });

    $('#dynamic-table tbody').on( 'click', 'button[name="remove"]',function() {            
        var index = $(this).context.value;
        $scope.remove($scope.basicTree[index]);
    });

    $scope.reloadTable = function(){
        myTable.rows().invalidate();
    }

    $scope.edit = function(index){
        $scope.loading = false;
        $scope.editDir = {
            index:index,
            data:util.clone($scope.basicTree[index])
        }
        var modalInstance = $modal.open({
            templateUrl : 'modules/directory/view/dialog/editDirectory.html',
            backdrop:'static',
            scope:$scope,
            controller : 'editDirController'
        })
    }

    $scope.remove = function(data){
        util.confirm('包含此內的所有目錄將會一併刪除,<br>確定要刪除 <font color="red">'+ data.categoryName +'</font> ?', function(r) {
            if (r) {
                directoryService.removeDirectory(data,function(data, status, headers, config){
                    if(data.result){
                        $scope.queryDirectoryList();
                    }else{
                        util.alert('刪除失敗');
                    }
                })
            }
        })
    }

    $scope.addDir = function(){
        $scope.createDir = {
            categoryName:null,
            categoryParentId:categoryParentId
        }
        var modalInstance = $modal.open({
            templateUrl : 'modules/directory/view/dialog/createDirectory.html',
            backdrop:'static',
            scope:$scope,
            controller : 'addDirController'
        })
    }

	$scope.queryDirectoryList = function(){
        var params = {
            categoryParentId:categoryParentId
        }
		directoryService.queryDirectoryList(params,function(data, status, headers, config){
			if(data.result){
				$scope.basicTree = data.data;
                util.refreshDataTable(myTable,$scope.basicTree);
			}
		})
	}
	$scope.queryDirectoryList();

    $scope.back = function(){
        history.back();
    }


	// $scope.basicTree=[{
 //        nodeId:0, name: "Node 1", disabled: true, expanded:false,
 //        children: [{
 //            name: "Node 1.1", disabled: true,
 //            children:[{}]
 //        }]
 //    }];

    // $scope.options = {
    // 	showIcon : true,
    //     onSelectNode : function (node) {
    //         $scope.selectedNodes = node;
    //         console.log('Selete Node :');
    //         console.log(node);
    //     },
    //     onExpandNode : function (node) {
    //         $scope.expandedNode = node;
    //         console.log('Expand Node :');
    //         console.log(node);
    //     },
    // 	filter : {}
    // };

})
