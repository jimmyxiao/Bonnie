app.controller('columnDetailController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, worksService) {
		$rootScope.title = 'demo畫作 | BonnieDRAW';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
		var wid = $state.params.id;
		$scope.isShow = false;

		$scope.textareaModel ={
			text:'',
			send:false
		}
		$scope.textareaAction = function(){
			if(!$scope.textareaModel.send){
				$scope.textareaModel.send = true;
				var param = util.getInitalScope();
				param.fn = 1;
				param.worksId = $scope.mainSection.worksId;
				param.message = $scope.textareaModel.text;
				worksService.leavemsg(param,function(data, status, headers, config){
					if(data.res != 1){
						alert('發送失敗');
					}else{
						$scope.queryWorks();
						$scope.textareaModel.text = '';
					}
					$scope.textareaModel.send = false;
				})
			}
		}

		$scope.removeMsg = function(data){
			var param = util.getInitalScope();
			param.fn = 0;
			param.worksId = $scope.mainSection.worksId;
			param.msgId = data.worksMsgId;
			worksService.leavemsg(param,function(data, status, headers, config){
				if(data.res != 1){
					alert('刪除失敗');
				}else{
					$scope.queryWorks();
				}
			})
		}

		$scope.queryWorks = function(){
			$scope.mainSection = {};
			var params = util.getInitalScope();
			params.wid = wid;
			// params.wt = 20; 
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					$scope.mainSection = data.work;
					$scope.isShow = true;
				}
			})
		}
		$scope.queryWorks();

		$scope.clickWorksLike = function(data){
			var params = util.getInitalScope();
			if(data.like){
				params.fn = 0;
			}else{
				params.fn = 1;
			}
			params.worksId = data.worksId;
			params.likeType = 1; 
			worksService.setLike(params,function(data, status, headers, config){
				if(data.res == 1){
					$scope.queryWorks();
				}
			})
		}

		$scope.clickWorksMark = function(data){
			var params = util.getInitalScope();
			if(data.collection){
				params.fn = 0;
			}else{
				params.fn = 1;
			}
			params.worksId = data.worksId;
			worksService.setCollection(params,function(data, status, headers, config){
				if(data.res == 1){
					$scope.queryWorks();
				}
			})
		}

		$scope.shareLink = function(tag){
			if(tag=='fb'){
				FB.ui({
    				method: 'share',
    				display: 'popup',
    				href: 'https://developers.facebook.com/docs/',
  				}, function(response){});
			}else if(tag=='google'){

			}else if(tag=='twitter'){
				
			}
		}

		$scope.turnModel = {
			optin:'one'
		}
		
		$scope.clickTurnIn = function(){
			$scope.turnModel.option = 'one';
		}

		$scope.sendTurnIn = function(){

		}

	}
)