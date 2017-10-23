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

		$scope.covert = function(description){
			var arr = util.splitHashTag(description);
			for(i=0;i<arr.length;i++){
				description = description.replace(arr[i],'<a href="">'+ arr[i] +'</a>');
			}
			return description;
		}

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
			optin:1,
			description:['內容不當', '侵犯著作權利']
		}

		$scope.clickTurnIn = function(){
			$scope.turnModel.option = 1;
		}

		$scope.sendTurnIn = function(){
			var params = util.getInitalScope();
			params.worksId = $scope.mainSection.worksId;
			params.turnInType = $scope.turnModel.option;
			params.description = $scope.turnModel.description[($scope.turnModel.option - 1)];
			worksService.setTurnin(params,function(data, status, headers, config){
				if(data.res == 1){
					alert('檢舉已發送');
				}
			})
		}

	}
)