app.controller('columnDetailController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, worksService) {
		$rootScope.title = 'demo畫作 | BonnieDRAW';
		$rootScope.nav = '';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
		var wid = $state.params.id;
		$scope.share_url ='https://www.bonniedraw.com/bonniedraw_service/BDService/socialShare?id='+wid;
		$scope.isShow = false;

		$scope.playRead = false;
		$scope.loading =false;

		$scope.funcol = false;
		$scope.funcolStop = false;
		$scope.canvasStop = false;

		$scope.fastarr = ['100','70','40','10','0.2','0.5'];//s~q
		$scope.fasti =0;
		$scope.fastnum = 100;

		var paused= false;
		$scope.forwarded =true;
		$scope.rewinded =false;
		var auto = true;
		var nextstatus = false;
		var linend =false;
		
		$scope.islast = false;
		$scope.isnext = false;
		
		$scope.pausebol =true;

		var crayon1 = document.createElement('canvas');
		var crayon2 = document.createElement('canvas');
		crayon2.width = 80;
		crayon2.height = 80;
		var ctx2 = crayon2.getContext('2d');

		$scope.hoverIn = function(){
			if($scope.pausebol&&$scope.playRead){
				$scope.funcol = true;
			}
	    };

	    $scope.hoverOut = function(){
	    	if($scope.pausebol&&$scope.playRead){
	    		$scope.funcol = false;
	    	}
	    };

		$scope.pasue = function(){
			$scope.fasti =0;
			$scope.fastnum = 100;
			
			if (!paused){
				$scope.pausebol = false;
				paused = true;
				
				$scope.rewinded = false;
				$scope.forwarded = false;
				if(draw_number==0){
					$scope.funcolStop = true;
					$scope.funcol = false;
				}
				
				if(predata.length>0){
					$scope.islast = true;
				}else{
					$scope.islast = false;
				}
				
				if(draw_number>=lines.length){
					$scope.isnext = false;
				}else{
					$scope.isnext = true;
				}
			} else {
				loop();
				$scope.pausebol =true;
				paused = false;
				if(draw_number==0){
					$scope.funcolStop = false;
				}
				
				$scope.islast = false;
				$scope.isnext = false;
				$scope.rewinded = true;
				$scope.forwarded = true;
				//$scope.funcol = true;
			}
		}
		
		$scope.last = function(){
			//console.log('click last');
			if (paused){
				if(predata.length>0){
					var popData = predata.pop();
					draw_number = iarray.pop();
                	cxt.putImageData(popData, 0, 0);
                	$scope.islast = true;
                }else{
                	console.log('not last');
                	$scope.islast = false;
                	$scope.funcol = false;
					$scope.funcolStop = true;
                }
			}
		}
		
		$scope.next = function(){
			//console.log('click next');
			if (paused){
				if(!nextstatus&&draw_number!=0){
					nextstatus = true;
					auto = false;
					$scope.isnext = true;
					//$scope.funcol = false;
					//$scope.funcolStop = true;
					loop();
					if(predata.length>0){
						$scope.islast = true;
					}else{
						$scope.islast = false;
					}
				}
			}
		}

		$scope.fast = function(tag){
			if(!$scope.islast){
				switch(tag) {
					case "forward":
						if(($scope.fastarr.length-1)>$scope.fasti){
							$scope.fasti++;							
						}
						break;
					case "rewind":
						if($scope.fasti>0){
							$scope.fasti--;							
						}
						break;
				}
				if(($scope.fastarr.length-1)==$scope.fasti){
					$scope.forwarded = false;
				}else{
					$scope.forwarded = true;
				}

				if($scope.fasti==0){
					$scope.rewinded = false;
				}else{
					$scope.rewinded = true;
				}
				$scope.fastnum = $scope.fastarr[$scope.fasti];
				console.log("speed:" + $scope.fastnum);
			}
		}

		function changeColor(img, color) {
			//console.log(color);
			r=color.color_r;
			g=color.color_g;
			b=color.color_b;
			//a=color.color_a;
		    var c = document.createElement('canvas');
		    c.width = img.width;
		    c.height = img.height;

		    var ctx_c = c.getContext('2d');
		    ctx_c.clearRect(0, 0, c.width, c.height);
		    ctx_c.drawImage(img, 0, 0);
		    var imgData=ctx_c.getImageData(0, 0, c.width, c.height);
		    for (var i=0;i<imgData.data.length;i+=4)
		    {
		        //imgData.data[i]= r | imgData.data[i];
		        //imgData.data[i+1]= g | imgData.data[i+1];
		        //imgData.data[i+2]= b | imgData.data[i+2];
		        //imgData.data[i+3]= imgData.data[i+3]*a;

		        imgData.data[i]= r ;
		        imgData.data[i+1]= g ;
		        imgData.data[i+2]= b ;
		        //imgData.data[i+3]= imgData.data[i+3]*a;
		    }
		    ctx_c.putImageData(imgData,0,0);
		    return c;
		} 

		function changeCrayon(img,x,y){
			crayon1.width = canvas_width;
		    crayon1.height = canvas_width;
		    var ctx1 = crayon1.getContext('2d');
		    ctx1.globalCompositeOperation = 'source-over';
		    ctx1.clearRect(0, 0, canvas_width, canvas_height);
		    ctx2.globalCompositeOperation = 'source-over';
		    ctx2.clearRect(0, 0, canvas_width, canvas_height);

		    ctx1.drawImage(imgBackNew,0,0, canvas_width, canvas_width);
		    ctx1.globalCompositeOperation = 'source-in';
		    ctx1.drawImage(img, x,y, img.width, img.height);
		    var imgData=ctx1.getImageData(x,y, img.width,img.height);
			ctx2.putImageData(imgData,0,0);
			return crayon2;
		} 

		function distanceBetween(point1, point2) {
			return Math.sqrt(Math.pow(point2.xPos - point1.xPos, 2) + Math.pow(point2.yPos - point1.yPos, 2));
		}
		function angleBetween(point1, point2) {
			return Math.atan2( point2.xPos - point1.xPos, point2.yPos - point1.yPos );
		}

		var i = 0;
		var draw_number = 0;
		var lines = [];
		var predata = [];
		var iarray = [];
		var can = null;
		var cxt = null;
		var canvas_width = 1000;
		var canvas_height = 1000;

		var brush_Alpha = [];
		var brush_imgAlpha = [];
		var brush_Composite = [];
		var bursh_array = [];
		var imgdata;
		var imgarray = new Image();

		var compos =['source-over','source-atop','source-in','source-out','destination-over','destination-atop','destination-in','destination-out','lighter','copy','xor'];

		imgarray[0] = '';
		imgarray[0].src = '';
		brush_Alpha[0] = 0;
		brush_imgAlpha[0] = 1;
		bursh_array[0] = '橡皮擦'
		brush_Composite[0] ='source-over';

		imgarray[1] = new Image();
		imgarray[1].src = 'assets/images/BrushImage/Creyon_brush_18.png';
		brush_Alpha[1] = 1;
		brush_imgAlpha[1] = 1;
		bursh_array[1] = '蠟筆';
		brush_Composite[1] ='source-over';


		imgarray[2] = new Image();
		imgarray[2].src = 'assets/images/BrushImage/SoftPencil_brush_04.png';
		brush_Alpha[2] = 1;
		brush_imgAlpha[2] = 0.045;
		bursh_array[2] = '鉛筆'
		brush_Composite[2] ='source-over';


		imgarray[3] = new Image();
		imgarray[3].src = 'assets/images/BrushImage/InkPen_brush_01_gb45.png';
		brush_Alpha[3] = 1;
		brush_imgAlpha[3] = 1;
		bursh_array[3] = '普通筆'
		brush_Composite[3] ='source-over';


		imgarray[4] = new Image();
		imgarray[4].src = 'assets/images/BrushImage/FeltPen_brush_45_c.png';
		brush_Alpha[4] = 0.04;
		brush_imgAlpha[4] = 1;
		bursh_array[4] = '麥克筆'
		brush_Composite[4] ='source-over';


		imgarray[5] = new Image();
		imgarray[5].src = 'assets/images/BrushImage/Pastel_brush_05.png';
		brush_Alpha[5] = 0.034;
		brush_imgAlpha[5] = 0.8;
		bursh_array[5] = '噴槍'
		brush_Composite[5] ='source-over';

		bursh_array[6] = '換底'

		var imgBackNew = new Image();
			imgBackNew.src = 'assets/images/BrushImage/crayon-texture1.png';

		

		function loop(){
			if(draw_number>=lines.length){
				predata = [];
				var imgendData=cxt.getImageData(0, 0,canvas_width, canvas_height);
		    	cxt.clearRect(0, 0, canvas_width, canvas_height);
		    	cxt.putImageData(imgendData,0,0);
		    	draw_number=0;
		    	$scope.isnext = false;
		    	$scope.islast = false;
		    	$scope.funcolStop = true;
		    	$scope.funcol = false;
		    	if(auto){
			    	paused = false;
			    	$scope.$apply( function() {$scope.pasue();})
		    	}

				/*
				setTimeout(function(){
					
					predata = [];
			    	cxt.clearRect(0, 0, canvas_width, canvas_height);
			    	loop();
			    	
				}, 5000);*/
			}else{
				/*if($scope.pausebol && $scope.fastnum!=1){
			    		var sett = $scope.fastnum;
			    	}else{
			    		var sett = lines[draw_number].time;
			    }*/
			    var sett = $scope.fastnum;
				setTimeout(function(){
			    	drawDot();
			    }, $scope.fastnum);
				//}, lines[draw_number].time*$scope.fastnum);
			}
		}

		function drawDot(){
			cxt.beginPath();
			var data = lines[draw_number];
			var previewData =[];
			
			if(draw_number==0){
				if(predata.length>21){
					predata.shift();
				}
				cxt.clearRect(0, 0, canvas_width, canvas_height);
				var pindata = cxt.getImageData(0, 0,canvas_width, canvas_height);
				predata.push(pindata);
				iarray.push(draw_number);
			}
			
			if(draw_number>0){
				previewData = lines[draw_number-1];
			}

			if(data.action==1 || data.action==2){
				if(data.action==1){
					console.log('num:'+draw_number);
				}
				if(data.action==2){
					if(predata.length>=21){
						predata.shift();
					}
					var pindata = cxt.getImageData(0, 0,canvas_width, canvas_height);
					predata.push(pindata);
					iarray.push(draw_number);
				}
				/*
				cxt.arc(data.xPos, data.yPos, data.size , 0, 2 * Math.PI, false);
				cxt.fillStyle = data.color;
				cxt.fill();
				*/
			}else if(data.action==3){
				/*cxt.lineWidth = data.size * 2 ;
				cxt.moveTo(previewData.xPos, previewData.yPos);
				cxt.lineTo(data.xPos, data.yPos);
				cxt.strokeStyle = data.color;
				cxt.stroke();

				cxt.arc(data.xPos, data.yPos, data.size , 0, 2 * Math.PI, false);
				cxt.fillStyle = data.color;
				cxt.fill();*/
				if((data.brush!=0) && (data.brush!=6)){
					imgdata = changeColor(imgarray[data.brush], data.color);
				}
				
				if(data.brush!=6){
					cxt.globalAlpha = brush_Alpha[data.brush] * data.color.color_a;
					var dist = distanceBetween(previewData, data);
					var angle = angleBetween(previewData, data);
					if(data.brush==1){
						for (var distnum = 0; distnum < dist; distnum+=4) {

								x = previewData.xPos + (Math.sin(angle) * distnum) - 25;
								y = previewData.yPos + (Math.cos(angle) * distnum) - 25;
								//cxt.globalAlpha=0.04;
								var Crayonimg = changeCrayon(imgdata,x,y);
								cxt.globalCompositeOperation='xor';
								cxt.drawImage(Crayonimg, x, y, data.size, data.size);
							}
					}else{
						for (var distnum = 0; distnum < dist; distnum++) {
							x = previewData.xPos + (Math.sin(angle) * distnum) ;
								y = previewData.yPos + (Math.cos(angle) * distnum) ;
							if(data.brush==0){
								cxt.arc(x,y,data.size/2,0,2*Math.PI);
								cxt.clearRect(x, y,data.size,data.size);
							}else{
								//cxt.globalAlpha=0.04;
								cxt.drawImage(imgdata, x, y, data.size, data.size);
							}
						}
					}
				}else{
					can.style.backgroundColor = data.color.color_rgba;
				}
		}
			
			draw_number++;
			
			if(!paused){
			  	loop();
			}else if(!auto && data.action!=2){
				loop();
				nextstatus = true;
			}else if(!auto && lines[draw_number-1].action==2){
				if(draw_number==lines.length){
					loop();
				}
				auto =true;
				nextstatus = false;
			}
		}

		function playing() {
			$scope.canvasStop = true;
			canvas_width = can.width;
			canvas_height = can.height;
			lines = util.clone($scope.lin);
			var tmpColor = 0;
			var tmpSize = 0;
			var tmpBrush = 0;		

			for(i=0;i<lines.length; i++){
				lines[i].xPos = Math.floor(lines[i].xPos / 65536 * canvas_width);
				lines[i].yPos = Math.floor(lines[i].yPos / 65536 * canvas_height);
				lines[i].size = Math.floor(lines[i].size / 65536 * canvas_height)*2;
				if(lines[i].action){
					switch(lines[i].action){
						case 1:
							var bigint = parseInt(lines[i].color, 16);
							var a = (((bigint >> 24) & 255)/255).toFixed(2);
							var r = (bigint >> 16) & 255;
							var g = (bigint >> 8) & 255;
							var b = bigint & 255;
							var colordata = {
								color_a: a,
								color_r: r,
								color_g: g,
								color_b: b,
								color_rgba: 'rgba('+r+','+g+','+b+','+a+')'
							};
							lines[i].color = colordata;
							tmpColor = lines[i].color;
							tmpSize = lines[i].size;
							tmpBrush = lines[i].brush;
							lines[i].time = 0;
						break;
						case 2:
							lines[i].color = util.clone(tmpColor);
							lines[i].size = util.clone(tmpSize);
							lines[i].brush = util.clone(tmpBrush);
							tmpColor = 0;
							tmpSize = 0;
							lines[i].time = 0;
						break;
						case 3:
							lines[i].color = util.clone(tmpColor);
							lines[i].size = util.clone(tmpSize);
							lines[i].brush = util.clone(tmpBrush);	
						break;
					}
				}
			}
			console.log(lines);

			$scope.playRead = true;
			$scope.$apply( function() {$scope.loading =true;});

			if($scope.loading){
				loop();
			}
		}

		$scope.drawingPlay = function(){
			var param = util.getInitalScope();
			param.wid = wid;
			worksService.getDrawingPlay(param,function(data, status, headers, config){		
				$scope.lin = data.pointList;
				console.log('data');
				console.log(data.pointList);
				if($scope.lin && $scope.lin.length>0){
					angular.element(document.getElementById("jsonCanvas")).ready(function () {
						can = document.getElementById("jsonCanvas");
						cxt = can.getContext("2d");
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
    				href: 'https://www.bonniedraw.com/bonniedraw_service/BDService/socialShare?id='+wid,
    				//href: location.href,
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