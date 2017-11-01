app.controller('columnDetailController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, worksService) {
		$rootScope.title = 'demo畫作 | BonnieDRAW';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
		var wid = $state.params.id;
		$scope.isShow = false;

		var paused= false;
		$scope.pausebol =true;
		$scope.pasue = function(){
			if (!paused){
				$scope.pausebol =false;
				paused = true;
			} else {
				loop();
				$scope.pausebol =true;
				paused = false;
			}
		}

		var i=0;
		var lines = [];
		var can = null;
		var cxt = null;
		var canvas_width = 1000;
		var canvas_height = 1000;
		function loop(){
			if(i>=lines.length){
				i=0;
				setTimeout(function(){
			    	cxt.clearRect(0, 0, canvas_width, canvas_height);
			    	loop();
				}, 5000);
			}else{
				setTimeout(function(){
			    	drawDot();
				}, lines[i].time);
			}
		}

		function drawDot(){
			cxt.beginPath();
			var data = lines[i];
			var previewData =[];
			if(i>0){
				previewData = lines[i-1];
			}

			if(data.action==1 || data.action==2){
				cxt.arc(data.xPos, data.yPos, data.size , 0, 2 * Math.PI, false);
				cxt.fillStyle = data.color;
				cxt.fill();
			}else if(data.action==3){
				cxt.lineWidth = data.size * 2 ;
				cxt.moveTo(previewData.xPos, previewData.yPos);
				cxt.lineTo(data.xPos, data.yPos);
				cxt.strokeStyle = data.color;
				cxt.stroke();

				cxt.arc(data.xPos, data.yPos, data.size , 0, 2 * Math.PI, false);
				cxt.fillStyle = data.color;
				cxt.fill();
			}
			
			i++;
			if(!paused){
			  	loop();
			}
		}

		function playing() {
			can = document.getElementById("jsonCanvas");
			cxt = can.getContext("2d");
			canvas_width = can.width;
			canvas_height = can.height;
			lines = util.clone($scope.lin);
			var tmpColor = 0;
			var tmpSize = 0;		

			for(i=0;i<lines.length; i++){
				lines[i].xPos = Math.floor(lines[i].xPos / 65536 * canvas_width);
				lines[i].yPos = Math.floor(lines[i].yPos / 65536 * canvas_height);
				lines[i].size = Math.floor(lines[i].size / 65536 * canvas_height);
				if(lines[i].action){
					switch(lines[i].action){
						case 1:
							var bigint = parseInt(lines[i].color, 16);
							var a = (((bigint >> 24) & 255)/255).toFixed(2);
							var r = (bigint >> 16) & 255;
							var g = (bigint >> 8) & 255;
							var b = bigint & 255;
							lines[i].color = 'rgba('+r+','+g+','+b+','+a+')';
							tmpColor = lines[i].color;
							tmpSize = lines[i].size;
						break;
						case 2:
							lines[i].color = util.clone(tmpColor);
							lines[i].size = util.clone(tmpSize);
							tmpColor = 0;
							tmpSize = 0;
						break;
						case 3:
							lines[i].color = util.clone(tmpColor);
							lines[i].size = util.clone(tmpSize);
						break;
					}
				}
			}
			loop();
		}

		$scope.drawingPlay = function(){
			var param = util.getInitalScope();
			param.wid = wid;
			worksService.getDrawingPlay(param,function(data, status, headers, config){		
				$scope.lin = data.pointList;
				if($scope.lin && $scope.lin.length>0){
					angular.element(document.getElementById("jsonCanvas")).ready(function () {
        				playing();
    				});
				}
			})
		}
		$scope.drawingPlay();


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

		$scope.covert = function(description){
			var arr = util.splitHashTag(description);
			for(i=0;i<arr.length;i++){
				description = description.replace(arr[i],'<a href="#/page-not-found">'+ arr[i] +'</a>');
			}
			return description;
		}

		$scope.queryWorks = function(){
			$scope.mainSection = {};
			var params = util.getInitalScope();
			params.wid = wid; 
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					$scope.mainSection = data.work;
					$scope.isShow = true;
					$scope.queryRelatedWorks();
				}
			})
		}
		$scope.queryWorks();

		$scope.queryRelatedWorks = function(){
			$scope.secondarySectionArr_userLike = [];
			var params = util.getInitalScope();
			params.wid = 0;
			params.wt = 6;
			params.stn= 1;
			params.rc = 5;
			params.queryId = $scope.mainSection.userId; 
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					$scope.secondarySectionArr_userLike = data.workList;
				}
			})
		}

		$scope.queryMoreWorks = function(){
			$scope.secondarySectionArr_moreCreation = [];
			var params = util.getInitalScope();
			params.wid = 0;
			params.wt = 4;
			params.stn = 0;
			params.rc = 1; 
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					$scope.secondarySectionArr_moreCreation = data.workList;
				}
			})
		}
		$scope.queryMoreWorks();

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

		 // $scope.secondarySectionArr_keyword =['3D','Animals & Birds','HD','Horror','Art','Self','HD Songs','Comedy'];

	}
)
// .directive("drawing", function(){
// 	  return {
// 		    restrict: "A",
// 		    link: function($scope, element){
// 		       //paused	
// 		       var paused= false;
// 		       $scope.pausebol =true;
// 		       	$scope.pasue = function(){
// 		    		console.log('pasue');
// 		    		if (!paused){
// 						clearInterval(intervalID);
// 						$scope.pausebol =false;
// 						paused = true;
// 				  	} else {
// 				    	intervalID = setInterval(drawLine, 200, cxt);
// 				    	$scope.pausebol =true;
// 				    	paused = false;
// 				  	}
// 		    	};
		    	
// 		    	var can = document.getElementById("jsonCanvas");
// 				var cxt = can.getContext('2d');

// 				//console.log('canvas_width:'+can.width+'canvas_height:'+can.height);
// 				var canvas_width = can.width;
// 				var canvas_height = can.height;

// 				//var lineCount = -1;
// 				var pointCount = 0;
// 				var point = 0;
// 				var intervalID;

// 				var lines =	[
// 					{"xPos":20992,"yPos":12792,"color":"80c0392b","action":1,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":20992,"yPos":12792,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":20992,"yPos":13940,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":20336,"yPos":15088,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":19844,"yPos":15908,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":19680,"yPos":17056,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":19024,"yPos":17876,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":18040,"yPos":19680,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":17712,"yPos":20500,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":17056,"yPos":21648,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":16564,"yPos":22468,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":16072,"yPos":23124,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":15744,"yPos":23780,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":15416,"yPos":24600,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":15252,"yPos":25092,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":15088,"yPos":25584,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":14760,"yPos":26404,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":14432,"yPos":26896,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":14432,"yPos":27224,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":14104,"yPos":27880,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":13776,"yPos":28372,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":13776,"yPos":28864,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":13776,"yPos":28864,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":13776,"yPos":29028,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":13776,"yPos":29028,"color":"80c0392b","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":10496,"yPos":41984,"color":"80c0392b","action":2,"size":2184,"brush":0,"time":0,"reserve":0},
					
// 					{"xPos":10496,"yPos":41984,"color":"FF090aed","action":1,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":10496,"yPos":41984,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":10660,"yPos":41984,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":11972,"yPos":41328,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":13284,"yPos":40836,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":15088,"yPos":40180,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":17056,"yPos":39360,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":18696,"yPos":38376,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":20336,"yPos":37556,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":22304,"yPos":36736,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":24436,"yPos":35588,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":26240,"yPos":34932,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":28208,"yPos":34112,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":29684,"yPos":33456,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":31488,"yPos":32800,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":33456,"yPos":31652,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":35588,"yPos":30832,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":36736,"yPos":30340,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":38048,"yPos":29684,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":38376,"yPos":29520,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":38868,"yPos":29028,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":39360,"yPos":29028,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":39360,"yPos":29028,"color":"FF090aed","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":47888,"yPos":34932,"color":"FF090aed","action":2,"size":2184,"brush":0,"time":0,"reserve":0},
					
// 					{"xPos":47888,"yPos":34932,"color":"0D014f14","action":1,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":48052,"yPos":36080,"color":"0D014f14","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":48052,"yPos":37064,"color":"0D014f14","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":48052,"yPos":38376,"color":"0D014f14","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":48052,"yPos":40016,"color":"0D014f14","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":48052,"yPos":41492,"color":"0D014f14","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":48052,"yPos":43296,"color":"0D014f14","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":48052,"yPos":45264,"color":"0D014f14","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":48052,"yPos":46576,"color":"0D014f14","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":48052,"yPos":47396,"color":"0D014f14","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":48052,"yPos":48052,"color":"0D014f14","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":48052,"yPos":48216,"color":"0D014f14","action":3,"size":2184,"brush":0,"time":0,"reserve":0},
// 					{"xPos":4592,"yPos":72488,"color":"0D014f14","action":2,"size":2184,"brush":0,"time":0,"reserve":0}
// 					];
// 				//console.log(JSON.stringify($scope.lin));
// 				for(var i=0;i<lines.length; i++){

// 					//畫點位置
// 					//lines[i].xPos = Math.floor(lines[i].xPos/400*canvas_width);
// 					//lines[i].yPos = Math.floor(lines[i].yPos/400*canvas_height);

// 					lines[i].xPos = Math.floor(lines[i].xPos/65536*canvas_width);
// 					lines[i].yPos = Math.floor(lines[i].yPos/65536*canvas_height);


// 					//畫點顏色
// 					var bigint = parseInt(lines[i].color, 16);
// 					var a = (bigint >> 24)& 255;
// 					var r = (bigint >> 16) & 255;
// 					var g = (bigint >> 8) & 255;
// 					var b = bigint & 255;

// 					a /= 255;
// 					a = a.toFixed(2);
// 					lines[i].color = 'rgba('+r+','+g+','+b+','+a+')';


// 					//畫點動作
// 					lines[i].action = (lines[i].action!=2) ? false : true;


// 					//畫點大小
// 					lines[i].size = Math.floor(lines[i].size/65536*canvas_height);


// 					//畫筆樣式
// 					//lines[i].brush = lines[i].brush ;


// 					//畫點停頓時間
// 					//lines[i].time = lines[i].time ;
// 				}

// 				//console.log(lines);
				
// 				nextLine(cxt);
				
// 				function nextLine(cxt) {
// 					/*lineCount++;
// 					if(lineCount > 0){
// 						return;
// 					}*/
// 					pointCount = 0;
// 					//cxt.moveTo(lines[0].xPos,lines[0].yPos);
// 					intervalID = setInterval(drawLine, 200, cxt);
// 				}

// 				function drawLine(context) {
// 					context.globalCompositeOperation="source-over";
					
// 					context.beginPath();
// 					if(pointCount==0){
// 						pointord = lines[pointCount];
// 					}else{
// 						pointord = lines[pointCount-1];
// 					}
// 					point = lines[pointCount];

					
					
// 					switch(point.brush) {
// 						case 0:
// 							//context.lineWidth = 10;
// 							context.lineWidth = point.size;
// 							context.lineCap = 'round';//線末端樣式
// 							context.strokeStyle = point.color;
// 							break;
						
// 					}
					
					


// 					if(!point.action){
// 						context.moveTo(pointord.xPos,pointord.yPos);
// 					}
					
// 					context.lineTo(point.xPos, point.yPos);
// 					context.stroke();
// 					pointCount++;

// 					if (pointCount == lines.length) {
// 						clearInterval(intervalID);
// 						cxt.clearRect(0, 0, can.width, can.height);
// 						nextLine(context,true);
// 					}
// 				}
		    	
// 		    }
// 		  };
// 		});