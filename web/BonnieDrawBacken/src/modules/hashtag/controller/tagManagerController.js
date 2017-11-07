app.factory('tagManagerService', function(baseHttp) {
    return {
    	queryTagList: function(params,callback){
			return baseHttp.service('tagManager/queryTagList',params,callback);
		},
        createCustomTag: function(params,callback){
            return baseHttp.service('tagManager/createCustomTag',params,callback);
        },
        updateCustomTag: function(params,callback){
            return baseHttp.service('tagManager/updateCustomTag',params,callback);
        },
        removeCustomTag: function(params,callback){
            return baseHttp.service('tagManager/removeCustomTag',params,callback);
        },
        queryTagViewList: function(params,callback){
            return baseHttp.service('tagManager/queryTagViewList',params,callback);
        },
        queryTagWorkList: function(params,callback){
            return baseHttp.service('tagManager/queryTagWorkList',params,callback);
        },
        searchTagViewList: function(params,callback){
            return baseHttp.service('tagManager/searchTagViewList',params,callback);
        }
    }
})
.controller('addCustomTagController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modalInstance, util, tagManagerService) {
    $scope.close = function() {
        $modalInstance.dismiss('cancel');
    }

    $scope.insertCustomTag = function(){
        if($scope.createCustomTag.tagOrder<0){
            util.alert('順序預設至少為0');
        }else{
            $scope.loading = true;
            tagManagerService.createCustomTag($scope.createCustomTag,function(data, status, headers, config){
                if(data.result){
                    $scope.queryTagList();
                    $scope.close();
                    util.alert('創建成功');
                }else{
                    util.alert(data.message);
                }
                $scope.loading = false;
            })
        }
    }
})
.controller('editCustomTagController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modalInstance, util, tagManagerService) {
    $scope.close = function() {
        $scope.editCustomTag = null;
        $scope.reloadTable();
        $modalInstance.dismiss('cancel');
    }

    $scope.updateCustomTag = function(){
         if($scope.editCustomTag.data.tagOrder<0){
            util.alert('順序預設至少為0');
        }else{
            $scope.loading = true;
            tagManagerService.updateCustomTag($scope.editCustomTag.data,function(data, status, headers, config){
                if(data.result){
                    $scope.queryTagList();
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
.controller('tagManagerController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, tagManagerService, util) {
    var categoryParentId = $state.params.cp;
    if(util.isEmpty(categoryParentId)){
        categoryParentId = 0;
        $scope.showBack = false;
    }else{
        $scope.showBack = true;
    }

    var columns = [
        { title: "順序", data: "tagOrder", sWidth:"10%" },
        { title: "名稱", data: "tagName", sWidth:"20%"},
        { title: "英文名稱", data: "tagEngName", sWidth:"10%"},
        {data:null,"bSortable": false,sWidth:"20%",render: function(data, type, full, meta) {
            var row = meta.row;
            var deleteBtn = '';    
            var editBtn='';
            deleteBtn = '<button name="remove" class="btn btn-danger btn-sm" value="'+ row +'"><i class="ace-icon fa fa-trash-o bigger-130"></i>刪除</button>';
            editBtn = '<button name="edit" class="btn btn-primary btn-sm" value="'+ row +'"><i class="ace-icon fa fa-trash-o bigger-130"></i>修改</button>';

            var btnStr=
                '<div class="hidden-sm hidden-xs action-buttons">' +
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
        data: $scope.tagList,
        columns: columns
    }
    var myTable = $('#dynamic-table').DataTable(opt);

    $('#dynamic-table tbody').on( 'click', 'button[name="edit"]',function() {              
        var index = $(this).context.value;
        $scope.edit(index);
    });

    $('#dynamic-table tbody').on( 'click', 'button[name="remove"]',function() {            
        var index = $(this).context.value;
        $scope.remove($scope.tagList[index]);
    });

    $scope.reloadTable = function(){
        myTable.rows().invalidate();
    }

    $scope.edit = function(index){
        $scope.loading = false;
        $scope.editCustomTag = {
            index:index,
            data:util.clone($scope.tagList[index])
        }
        var modalInstance = $modal.open({
            templateUrl : 'modules/hashtag/view/dialog/editCustomTag.html',
            backdrop:'static',
            scope:$scope,
            controller : 'editCustomTagController'
        })
    }

    $scope.remove = function(data){
        util.confirm('確定要刪除 <font color="red">'+ data.tagName +'</font> ?', function(r) {
            if (r) {
                tagManagerService.removeCustomTag(data,function(data, status, headers, config){
                    if(data.result){
                        $scope.queryTagList();
                    }else{
                        util.alert('刪除失敗');
                    }
                })
            }
        })
    }

    $scope.addCustomTag = function(){
        if($scope.tagList.length>=5){
            util.alert('類別標籤最多5項');
        }else{
            $scope.createCustomTag = {
                tagName:null,
                tagEngName:null,
                tagOrder:0
            }
            var modalInstance = $modal.open({
                templateUrl : 'modules/hashtag/view/dialog/createCustomTag.html',
                backdrop:'static',
                scope:$scope,
                controller : 'addCustomTagController'
            })
        }
    }

	$scope.queryTagList = function(){
		tagManagerService.queryTagList({},function(data, status, headers, config){
			if(data.result){
				$scope.tagList = data.data;
                util.refreshDataTable(myTable,$scope.tagList);
			}
		})
	}
	$scope.queryTagList();

    $scope.back = function(){
        history.back();
    }

})
