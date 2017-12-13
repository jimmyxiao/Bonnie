app.controller('columnDetailControllerStreamSimulation', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, worksService) {
		$rootScope.title = 'demo畫作 | BonnieDRAW';
		$rootScope.nav = '';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
		var wid = $state.params.id;
		$scope.share_url ='https://www.bonniedraw.com/bonniedraw_service/BDService/socialShare?id='+wid;
		$scope.isShow = false;

		$scope.playRead = false;
		$scope.loading =false;
		$scope.notfile = true;

		$scope.funcol = false;
		$scope.funcolStop = false;
		$scope.canvasStop = false;

		$scope.fastarr = ['100','70','40','10','0.2','0.5'];//s~q
		$scope.fasti = 0;
		$scope.fastiPow = Math.pow(2,$scope.fasti);
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

		var stocker = document.createElement('canvas');
		var st = stocker.getContext("2d");
		stocker.width = 500;
		stocker.height = 500;

		var background = document.createElement('canvas');
		var bg=background.getContext("2d");
		background.width = 500;
		background.height = 500;

		var bgcolord = '';

		$scope.hoverIn = function(){
			if($scope.pausebol && $scope.playRead){
				$scope.funcol = true;
			}
	    }

	    $scope.hoverOut = function(){
	    	if($scope.pausebol && $scope.playRead){
	    		$scope.funcol = false;
	    	}
	    }

		$scope.pasue = function(){
			$scope.fasti =0;
			$scope.fastnum = 100;
			if(!paused){
				$scope.pausebol = false;
				paused = true;
				$scope.rewinded = false;
				$scope.forwarded = false;
				if(draw_number == 0){
					$scope.funcolStop = true;
					$scope.funcol = false;
				}
				$scope.islast = (predata.length > 0) ? true : false;
				$scope.isnext = (draw_number >= lines.length) ? false : true;
			}else{
				drawingStart();
				$scope.pausebol =true;
				paused = false;
				if(draw_number == 0){
					$scope.funcolStop = false;
				}
				
				$scope.islast = false;
				$scope.isnext = false;
				$scope.rewinded = true;
				$scope.forwarded = true;
			}
		}
		
		$scope.last = function(){
			if(paused){
				if(predata.length > 0){
					var popData = predata.pop();
					draw_number = iarray.pop();
                	cxt.putImageData(popData, 0, 0);
                	$scope.islast = true;
                }else{
                	$scope.islast = false;
                }
			}
		}
		
		$scope.next = function(){
			if(paused && (!nextstatus && draw_number != 0)){
				nextstatus = true;
				auto = false;
				$scope.isnext = true;
				drawingStart();
				$scope.islast = (predata.length>0) ? true : false;
			}
		}

		$scope.fast = function(tag){
			if(!$scope.islast){
				switch(tag) {
				case "forward":
					if(($scope.fastarr.length-1) > $scope.fasti){
						$scope.fasti++;	
						$scope.fastiPow = Math.pow(2,$scope.fasti);						
					}
					break;
				case "rewind":
					if($scope.fasti > 0){
						$scope.fasti--;
						$scope.fastiPow = Math.pow(2,$scope.fasti);							
					}
					break;
				}
				$scope.forwarded = (($scope.fastarr.length-1) == $scope.fasti) ? false : true;
				$scope.rewinded = ($scope.fasti == 0) ? false : true;
				$scope.fastnum = $scope.fastarr[$scope.fasti];
			}
		}

		function changeColor(img, color, colorA){
			r=color.color_r;
			g=color.color_g;
			b=color.color_b;
		    var c = document.createElement('canvas');
		    c.width = img.width;
		    c.height = img.height;

		    var ctx_c = c.getContext('2d');
		    ctx_c.clearRect(0, 0, c.width, c.height);
		    ctx_c.drawImage(img, 0, 0);
		    var imgData=ctx_c.getImageData(0, 0, c.width, c.height);
		    for (var i=0;i<imgData.data.length;i+=4){
		        imgData.data[i]= r ;
		        imgData.data[i+1]= g ;
		        imgData.data[i+2]= b ;
		        imgData.data[i+3]= imgData.data[i+3]*colorA;
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

		function changBgColor(color){
			r=color.color_r;
			g=color.color_g;
			b=color.color_b;
			a=color.color_a;
			var imgData=cxt.createImageData(background.width,background.height);
			for (var i=0;i<imgData.data.length;i+=4){
				imgData.data[i+0]=r;
				imgData.data[i+1]=g;
				imgData.data[i+2]=b;
				imgData.data[i+3]=a;
			}
			bg.clearRect(0, 0, 500, 500);
			bg.putImageData(imgData,0,0);
			return background;
		} 

		function distanceBetween(point1, point2){
			return Math.sqrt(Math.pow(point2.xPos - point1.xPos, 2) + Math.pow(point2.yPos - point1.yPos, 2));
		}

		function angleBetween(point1, point2){
			return Math.atan2( point2.xPos - point1.xPos, point2.yPos - point1.yPos );
		}

		var compos =['source-over','source-atop','source-in','source-out','destination-over','destination-atop','destination-in','destination-out','lighter','copy','xor'];
		var i = 0;
		var draw_number = 0;
		var lines = [];
		var predata = [];
		var iarray = [];
		var can;
		var cxt;
		var canvas_width = 1000;
		var canvas_height = 1000;
		var imgdata;
		var imgarray = [
			 new Image(), new Image(), new Image(), new Image(), new Image(),	new Image() 
		];
		imgarray[0].src = 'assets/images/BrushImage/eraser.png';
		imgarray[1].src = 'assets/images/BrushImage/Creyon_brush_18.png';
		imgarray[2].src = 'assets/images/BrushImage/SoftPencil_brush_04.png';
		imgarray[3].src = 'assets/images/BrushImage/InkPen_brush_01.png';
		imgarray[4].src = 'assets/images/BrushImage/FeltPen_brush_45_c.png';
		imgarray[5].src = 'assets/images/BrushImage/Pastel_brush_05-1.png';
		
		var brush_Alpha = [
			1, 0.4, 0.02, 1, 0.03, 0.4//0, 1, 1, 1, 0.4, 0.034|0, 0.9, 0.02, 1, 0.02, 0.08
		];
		
		var brush_imgAlpha = [
			1, 1, 1, 1, 1, 1
		];
		
		var bursh_array = [
			'橡皮擦', '蠟筆', '鉛筆', '普通筆', '麥克筆', '噴槍', '換底'
		];
		
		var brush_Composite = [
			'source-over', 'source-over', 'source-over', 'source-over', 'source-over', 'source-over'
		];

		var imgBackNew = new Image();
		imgBackNew.src = 'assets/images/BrushImage/crayon-texture1.png';

		var dotInfo	= {
			tmpColor:0,
			tmpSize:0,
			tmpBrush:0
		}
		function setDotInfo(color, size, brush){
			dotInfo.tmpColor = color;
			dotInfo.tmpSize = size;
			dotInfo.tmpBrush = brush;
		}

		function drawingStart(){
			if(draw_number>=lines.length){
				draw_number = 0;
				if(stream.res == 1){
					stream.stn += stream.rc;
					$scope.drawingPlay();
				}else if(stream.res == 4){
					stream.stn = 0;
					setDotInfo(0, 0, 0);
					lines = util.clone(tempLines);

					predata = [];
					var imgendData = cxt.getImageData(0, 0, canvas_width, canvas_height);
					
			    	cxt.clearRect(0, 0, canvas_width, canvas_height);
			    	cxt.putImageData(imgendData, 0, 0);
			    	$scope.isnext = false;
			    	$scope.islast = false;
			    	$scope.funcolStop = true;
			    	$scope.funcol = false;
			    	if(auto){
				    	paused = false;
				    	$scope.$apply(function(){$scope.pasue();})
			    	}
				}
			}else{
				setTimeout(function(){
			    	drawDot();
			    }, $scope.fastnum);
			}
		}

		// 畫點
		function drawDot(){
			cxt.beginPath();
			var data = lines[draw_number];
			var action = data.action;
			var brush = data.brush;
			var previewData =[];
			if(draw_number == 0){
				if(predata.length>21){
					predata.shift();
				}
				if(stream.stn == 0){
					can.style.backgroundColor = "#ffffff";
					cxt.clearRect(0, 0, canvas_width, canvas_height);
				}
				predata.push(cxt.getImageData(0, 0, canvas_width, canvas_height));
				iarray.push(draw_number);
			}else if(draw_number>0){
				previewData = lines[draw_number-1];
			}

			if(action==1 || action==2){
				switch(action){
				case 1:
					if(brush==6){
						//can.style.backgroundColor = data.color.color_rgba;
						bgcolord = data.color;
						var bgColordata = changBgColor(data.color);
						cxt.clearRect(0, 0, canvas_width, canvas_height);
						cxt.globalAlpha = 1;
						cxt.globalCompositeOperation = "source-over";
						cxt.drawImage(bgColordata, 0, 0);
						cxt.drawImage(stocker, 0, 0);
					}
					break;
				case 2:
					if(predata.length>=21){
						predata.shift();
					}
					predata.push(cxt.getImageData(0, 0,canvas_width, canvas_height));
					iarray.push(draw_number);
					break;
				}
			}else if(action == 3){
				// if((data.brush!=0) && (data.brush!=6)){
				if(data.brush!=6){
					imgdata = changeColor(imgarray[data.brush], data.color, brush_Alpha[data.brush]);
				}
				if(data.brush==0){
					imgdata = changeColor(imgarray[0], bgcolord);
				}
				
				if(brush != 6){
					cxt.globalAlpha = brush_Alpha[brush] * data.color.color_A;
					var dist = distanceBetween(previewData, data);
					var angle = angleBetween(previewData, data);
					if(brush == 1){
						var Crayonimg = changeCrayon(imgdata,0,0);
						for (var distnum = 0; distnum < dist; distnum++) {
							x = previewData.xPos + (Math.sin(angle) * distnum) ;
							y = previewData.yPos + (Math.cos(angle) * distnum) ;
							
							//cxt.globalCompositeOperation = 'xor';
							cxt.drawImage(Crayonimg, x, y, data.size, data.size);
							st.drawImage(Crayonimg, x, y, data.size, data.size);

						}
					}else{
						for (var distnum = 0; distnum < dist; distnum++) {
							x = previewData.xPos + (Math.sin(angle) * distnum) ;
							y = previewData.yPos + (Math.cos(angle) * distnum) ;
							if(brush == 0){
								cxt.globalAlpha = 1;
								cxt.clearRect(x, y,data.size,data.size);
								cxt.drawImage(imgdata, x, y, data.size, data.size);
							}else{
								cxt.drawImage(imgdata, x, y, data.size, data.size);
								st.drawImage(imgdata, x, y, data.size, data.size);	
							}
						}
					}
				}else{
					//can.style.backgroundColor = data.color.color_rgba;
				}
			}
			
			draw_number++;
			if(!paused){
			  	drawingStart();
			}else if(!auto){
				if(action !=2 ){
					drawingStart();
					nextstatus = true;
				}else if(lines[draw_number-1].action == 2){
					if(draw_number == lines.length){
						drawingStart();
					}
					auto =true;
					nextstatus = false;
				}
			}
		}

		var tempLines = [];	//片斷串流串接
		// point解析
		function translatePoint(pointList){
			$scope.canvasStop = true;
			canvas_width = can.width;
			canvas_height = can.height;
			lines = util.clone(pointList);

			for(i=0;i<lines.length; i++){
				lines[i].xPos = Math.floor(lines[i].xPos / 65536 * canvas_width);
				lines[i].yPos = Math.floor(lines[i].yPos / 65536 * canvas_width);
				lines[i].size = Math.floor(lines[i].size / 65536 * canvas_width);
				switch(lines[i].action){
				case 1:
					var bigint = parseInt(lines[i].color, 16);
					var A = (((bigint >> 24) & 255)/255).toFixed(2);
					var a = (bigint >> 24) & 255;
					var r = (bigint >> 16) & 255;
					var g = (bigint >> 8) & 255;
					var b = bigint & 255;
					lines[i].color = {
						color_a: a,
						color_r: r,
						color_g: g,
						color_b: b,
						color_rgba: 'rgba('+r+','+g+','+b+','+a+')'
					};
					setDotInfo(lines[i].color, lines[i].size, lines[i].brush);	// tmpBrush = lines[i].brush;
					lines[i].time = 0;
					lines[i].xPos -= lines[i].size/2;
					lines[i].yPos -= lines[i].size/2;
					break;
				
				case 2:
					lines[i].color = util.clone(dotInfo.tmpColor);
					lines[i].size = util.clone(dotInfo.tmpSize);
					lines[i].brush = 4;	// lines[i].brush = util.clone(tmpBrush);
					setDotInfo(0, 0, 0);
					lines[i].time = 0;
					break;
						
				case 3:
					lines[i].color = util.clone(dotInfo.tmpColor);
					lines[i].size = util.clone(dotInfo.tmpSize);
					lines[i].brush = util.clone(dotInfo.tmpBrush);	// lines[i].brush = 4;
					lines[i].xPos -= util.clone(dotInfo.tmpSize)/2;
					lines[i].yPos -= util.clone(dotInfo.tmpSize)/2;
					break;
				}
			}
			tempLines = tempLines.concat(util.clone(lines));

			$scope.playRead = true;
			if(stream.stn == 0){
				$scope.$apply(function(){$scope.loading =true;});
			}
			if($scope.loading){
				drawingStart();
			}			
		}

		var stream = {
			stn:0,
			rc:1000,
			res:2
		}
		$scope.drawingPlay = function(){
			var param = util.getInitalScope();
			param.wid = wid;
			param.stn = stream.stn;
			param.rc = stream.rc;
			worksService.getDrawingPlayStreamSimulation(param,function(data, status, headers, config){		
				var list = data.pointList;
				stream.res = data.res;
				if(list && list.length>0){
					if(stream.stn == 0){
						angular.element(document.getElementById("jsonCanvas")).ready(function () {
							can = document.getElementById("jsonCanvas");
							cxt = can.getContext("2d");
	        				translatePoint(list);
	    				});
					}else{
						translatePoint(list);
					}
				}else{
					$scope.notfile = false;
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
				description = description.replace(arr[i],'<a href="#/category-listing?type='+ util.covertTag(arr[i]) +'">'+ arr[i] +'</a>');
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

	}
)