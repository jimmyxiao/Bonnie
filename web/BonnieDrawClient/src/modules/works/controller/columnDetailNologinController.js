app.controller('columnDetailNologinController', function ($rootScope, $scope, $window, $location, $http, $filter, $state, $modal, util, worksService, $translate) {
		$rootScope.title = 'TITLE.t05_04_word_demo';
		$rootScope.nav = '';
		$('#loader-container').fadeOut("slow");
		new WOW().init();
		var wid = $state.params.id;
		$scope.isShow = false;

		$scope.playRead = false;
		$scope.loading =false;
		$scope.notfile = true;
		$scope.image = true;


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
			$scope.fastiPow = Math.pow(2,$scope.fasti);
			
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
                	//$scope.funcol = false;
					//$scope.funcolStop = true;
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
							$scope.fastiPow = Math.pow(2,$scope.fasti);						
						}
						break;
					case "rewind":
						if($scope.fasti>0){
							$scope.fasti--;
							$scope.fastiPow = Math.pow(2,$scope.fasti);							
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

		// function changeColor(img, color) {
		function changeColor(img, color, colorA) {
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
		        //imgData.data[i]= b | imgData.data[i];
		        //imgData.data[i+1]= g | imgData.data[i+1];
		        //imgData.data[i+2]= r | imgData.data[i+2];
		        //imgData.data[i+3]= imgData.data[i+3];

		        imgData.data[i]= r ;
		        imgData.data[i+1]= g ;
		        imgData.data[i+2]= b ;
		        // imgData.data[i+3]= imgData.data[i+3];//*a;
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
		var can ;
		var cxt ;
		var canvas_width = 500;//1000;
		var canvas_height = 500;//1000;

		var brush_Alpha = [];
		var brush_imgAlpha = [];
		var brush_Composite = [];
		var bursh_array = [];
		var imgdata;
		var imgarray = new Image();

		var stocker = document.createElement('canvas');
		var st = stocker.getContext("2d");
		stocker.width = 500;
		stocker.height = 500;

		var background = document.createElement('canvas');
		var bg=background.getContext("2d");
		background.width = 500;
		background.height = 500;

		var compos =['source-over','source-atop','source-in','source-out','destination-over','destination-atop','destination-in','destination-out','lighter','copy','xor'];

		// imgarray[0] = '';
		// imgarray[0].src = '';
		// brush_Alpha[0] = 0;
		imgarray[0]  = new Image();
		imgarray[0].src = 'assets/images/BrushImage/eraser.png';
		brush_Alpha[0] = 1;
		brush_imgAlpha[0] = 1;
		bursh_array[0] = '橡皮擦'
		brush_Composite[0] ='source-over';

		imgarray[1] = new Image();
		imgarray[1].src = 'assets/images/BrushImage/Creyon_brush_18.png';
		brush_Alpha[1] = 0.4;
		brush_imgAlpha[1] = 1;
		bursh_array[1] = '蠟筆';
		brush_Composite[1] ='source-over';


		imgarray[2] = new Image();
		imgarray[2].src = 'assets/images/BrushImage/SoftPencil_brush_04.png';
		brush_Alpha[2] = 0.2//0.05;
		brush_imgAlpha[2] = 1;
		bursh_array[2] = '鉛筆'
		brush_Composite[2] ='source-over';


		imgarray[3] = new Image();
		imgarray[3].src = 'assets/images/BrushImage/InkPen_brush_01.png';
		brush_Alpha[3] = 1;
		brush_imgAlpha[3] = 1;
		bursh_array[3] = '普通筆'
		brush_Composite[3] ='source-over';


		imgarray[4] = new Image();
		imgarray[4].src = 'assets/images/BrushImage/FeltPen_brush_45_c.png';
		brush_Alpha[4] = 0.03;//0.4
		brush_imgAlpha[4] = 1;
		bursh_array[4] = '麥克筆'
		brush_Composite[4] ='source-over';


		imgarray[5] = new Image();
		imgarray[5].src = 'assets/images/BrushImage/Pastel_brush_05-1.png';
		brush_Alpha[5] = 0.4//0.034;
		brush_imgAlpha[5] = 1;
		bursh_array[5] = '噴槍'
		brush_Composite[5] ='source-over';

		bursh_array[6] = '換底'

		var imgBackNew = new Image();
			imgBackNew.src = 'assets/images/BrushImage/crayon-texture1.png';

		var firstnum=0;
		var bgcolord = [];

		function loop(){
			if(draw_number>=lines.length){
				predata = [];
				var imgendData=cxt.getImageData(0, 0,canvas_width, canvas_height);
				//can.style.backgroundColor = "#ffffff";
		    	cxt.clearRect(0, 0, canvas_width, canvas_height);
		    	$scope.image = false;
		    	// cxt.putImageData(imgendData,0,0);
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
			    $scope.image = true;
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
				can.style.backgroundColor = "#ffffff";
				cxt.clearRect(0, 0, canvas_width, canvas_height);
				st.clearRect(0, 0, canvas_width, canvas_height);
				var pindata = cxt.getImageData(0, 0,canvas_width, canvas_height);
				predata.push(pindata);
				iarray.push(draw_number);
			}
			
			if(draw_number>0){
				previewData = lines[draw_number-1];
			}

			if(data.action==1 || data.action==2){
				if(data.action==1){
					if(data.brush==6){
						// can.style.backgroundColor = data.color.color_rgba;
						bgcolord = data.color;
						// var st_pindata = st.getImageData(0, 0,canvas_width, canvas_height);
						// cxt.clearRect(0, 0, canvas_width, canvas_height);
						// cxt.globalCompositeOperation = "source-over";
						// var bgColordata = cahngBgCoior(data.color);
						// var cxt_gco = cxt.globalCompositeOperation;
						// var cxt_ga = cxt.globalAlpha;
						// cxt.putImageData(st_pindata, 0, 0);
						// cxt.globalAlpha = 1;
						// cxt.globalCompositeOperation = "destination-atop";
						// cxt.drawImage(bgColordata, 0, 0);
						// cxt.globalAlpha = cxt_ga;
						// cxt.globalCompositeOperation = cxt_gco;	

						var bgColordata = changBgColor(data.color);
						cxt.clearRect(0, 0, canvas_width, canvas_height);
						cxt.globalAlpha = 1;
						cxt.globalCompositeOperation = "source-over";
						cxt.drawImage(bgColordata, 0, 0);
						cxt.drawImage(stocker, 0, 0);

						
					}
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
				// if((data.brush!=0) && (data.brush!=6)){
				if(data.brush!=6){
					//imgdata = changeColor(imgarray[data.brush], data.color);
					 imgdata = changeColor(imgarray[data.brush], data.color, brush_Alpha[data.brush]);
				}
				if(data.brush==0 && bgcolord.length!=0){
					imgdata = changeColor(imgarray[0], bgcolord, brush_Alpha[data.brush]);
				}
			
				if(data.brush!=6){
					cxt.globalAlpha = brush_Alpha[data.brush] * data.color.color_A;
					var dist = distanceBetween(previewData, data);
					var angle = angleBetween(previewData, data);
					if(data.brush==1){
						var Crayonimg = changeCrayon(imgdata,0,0);
						for (var distnum = 0; distnum < dist; distnum++) {

							x = previewData.xPos + (Math.sin(angle) * distnum) ;
							y = previewData.yPos + (Math.cos(angle) * distnum) ;
							//cxt.globalAlpha=0.04;
							//var Crayonimg = changeCrayon(imgdata,x,y);
							//cxt.globalCompositeOperation='xor';
							cxt.drawImage(Crayonimg, x, y, data.size, data.size);
							st.drawImage(Crayonimg, x, y, data.size, data.size);	
						}
					}else{
						for (var distnum = 0; distnum < dist; distnum++) {
							x = previewData.xPos + (Math.sin(angle) * distnum) ;
							y = previewData.yPos + (Math.cos(angle) * distnum) ;
							if(data.brush==0){
								// cxt.arc(x,y,data.size/2,0,2*Math.PI);
								// cxt.clearRect(x, y,data.size/2,data.size/2);
								// cxt.globalAlpha = 1;
								// cxt.clearRect(x, y,data.size,data.size);
								if(bgcolord.length == 0){
									cxt.clearRect(x, y,data.size,data.size);
								}else{	
									cxt.globalCompositeOperation="xor";
									cxt.drawImage(imgdata, x, y, data.size, data.size);
									cxt.globalCompositeOperation="source-over";
									cxt.drawImage(imgdata, x, y, data.size, data.size);
								}
							}else{
								//cxt.globalAlpha=0.04;
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
				lines[i].yPos = Math.floor(lines[i].yPos / 65536 * canvas_width);
				// lines[i].size = Math.floor(lines[i].size / 65536 * canvas_width);
				// lines[i].size = Math.round(lines[i].size / 65536 * canvas_width);
				// lines[i].size = lines[i].size / 65536 * canvas_width;
				lines[i].size = Math.ceil(lines[i].size / 65536 * canvas_width);
				if(lines[i].action){
					switch(lines[i].action){
						case 1:
							var bigint = parseInt(lines[i].color, 16);
							var A = (((bigint >> 24) & 255)/255).toFixed(2);
							var a = (bigint >> 24) & 255;
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
							lines[i].xPos -= tmpSize/2;
							lines[i].yPos -= tmpSize/2;
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
							lines[i].xPos -= util.clone(tmpSize)/2;
							lines[i].yPos -= util.clone(tmpSize)/2;
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
			var param ={
				"dt":3,
				"wid": wid
			}
			worksService.getDrawingPlay(param,function(data, status, headers, config){		
				$scope.lin = data.pointList;
				console.log($scope.lin);
				
				if($scope.lin && $scope.lin.length>0){
					angular.element(document.getElementById("jsonCanvas")).ready(function () {
						can = document.getElementById("jsonCanvas");
						cxt = can.getContext("2d");
        				playing();
    				});
    				console.log('file');
				}else{
					console.log('notfile');
					$scope.notfile = false;
				}
			})
		}
		$scope.drawingPlay();

		$scope.queryWorks = function(){
			$scope.mainSection = {};
			var params ={
				"dt":3,
				"wid": wid
			}
			worksService.queryWorksList(params,function(data, status, headers, config){
				if(data.res == 1){
					$scope.mainSection = data.work;
					$scope.isShow = true;
				}
			})
		}
		$scope.queryWorks();

	}
)