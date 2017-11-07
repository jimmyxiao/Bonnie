app.controller('tagViewController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, tagManagerService, util) {
    var columns = [
        { title: "數量", data: "count", "bSortable": false, sWidth:"10%" },
        { title: "名稱", data: "tagName", "bSortable": false, sWidth:"20%"}, 
        { title: "相關作品", data:null, "bSortable": false,sWidth:"20%",render: function(data, type, full, meta) {
            var row = meta.row;
            var btnStr=
                '<div class="hidden-sm hidden-xs action-buttons">' +
                '<button name="success" class="btn btn-success btn-sm" value="'+ row +'"><i class="ace-icon fa fa-pencil bigger-130"></i>查看</button>' +
                '</div>'
                return btnStr;
            }
        }
    ]

    var opt = {
        "oLanguage":{"sUrl":"dataTables.zh-tw.txt"},
        "bJQueryUI":true,
        "sPaginationType":'full_numbers',
        "order": [[ 0, "desc" ]],
        "searching": false,
        data: $scope.tagList,
        columns: columns
    }
    var myTable = $('#dynamic-table').DataTable(opt);
    $('#dynamic-table tbody').on( 'click', 'button[name="success"]',function() {              
        var index = $(this).context.value;
        var data = $scope.tagList[index];
        $scope.worksView(data);
    });

    $scope.reloadTable = function(){
        myTable.rows().invalidate();
    }

	$scope.queryTagViewList = function(){
		tagManagerService.queryTagViewList({},function(data, status, headers, config){
			if(data.result){
				$scope.tagList = data.data;
                util.refreshDataTable(myTable,$scope.tagList);
			}
		})
	}
	$scope.queryTagViewList();


    $scope.worksView = function(data){
        var modalInstance = $modal.open({
            templateUrl : 'modules/hashtag/view/dialog/worksViewList.html',
            scope:$scope,
            size:'lg',
            controller : function($scope, $modalInstance, util) {
                $scope.close = function() {
                    $modalInstance.dismiss('cancel');
                }

                tagManagerService.queryTagWorkList(data,function(data, status, headers, config){
                    if(data.result){
                        $scope.viewList = data.data;
                        var columns_sub = [
                            { title: "作品標題", data: "title", sWidth:"10%" },
                            { title: "作品敘述", data: "description",sWidth:"20%"}, 
                            { title: "作者", data: "userName", sWidth:"20%"}
                        ]

                        var opt_sub = {
                            "oLanguage":{"sUrl":"dataTables.zh-tw.txt"},
                            "bJQueryUI":true,
                            "sPaginationType":'full_numbers',
                            "searching": false,
                            data: $scope.viewList,
                            columns: columns_sub
                        }
                        var mySubTable = $('#dynamic-table-sub').DataTable(opt_sub);
                    }
                });
            }
        })
    }

    $scope.searchModel = {
        text:null
    }
    $scope.searching = function(){
        if(!util.isEmpty($scope.searchModel.text)){
            var param = {
                tagName:$scope.searchModel.text
            }
            tagManagerService.searchTagViewList(param,function(data, status, headers, config){
                if(data.result){
                    $scope.tagList = data.data;
                    util.refreshDataTable(myTable,$scope.tagList);
                }
            });
        }
    }


})
