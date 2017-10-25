app.factory('util', function($rootScope, $http, $modal) {
	return {
		alert: function(msg,callback,size) {
			size = size || 'sm';
			$modal.open({
				templateUrl: 'modules/share/view/alert.html',
				backdrop:'static',
				controller: function($scope, $modalInstance) {
					$scope.msg = msg;
					$scope.close = function() {
						$modalInstance.dismiss('cancel');
						if (callback) callback();
					};
				},size: size
			});
		},
		confirm: function(msg, callback) {
			$modal.open({
				templateUrl: 'modules/share/view/confirm.html',
				backdrop: 'static',
				controller: function($scope, $modalInstance) {
					$scope.msg = msg;
					$scope.close = function(r) {
						$modalInstance.dismiss('cancel');
						if(r == true || r == false)
							if (callback) callback(r);
					};
				},size: 'sm'
			});
		},
		isEmpty:function(obj) {
			if (obj == null) return true;
			if (obj.length > 0)    return false;
			if (obj.length === 0)  return true;
			if (typeof obj == "number") return false;
			if (typeof obj !== "object") return true;
			for (var key in obj) {
				if (hasOwnProperty.call(obj, key)) return false;
			}
			return true;
		},
		clone:function(obj) {
			var newObj = JSON.parse(JSON.stringify(obj));
			return newObj;
		},
		refreshDataTable:function(table,list){
			table.clear().draw();
			table.rows.add(list);
			table.columns.adjust().draw();
		},
		getAsiaTimezone:function(){
			var _hourOffset = 8*60*60000; 	//	GMT+h*60*60000 = ms 
			var _userOffset = _date.getTimezoneOffset()*60000;	//min*60000 = ms
			var _nowDate = new Date(_date.getTime()+_helsenkiOffset+_userOffset);
			return _nowDate;
		},
		formatDate:function(date){
			var d = new Date(date),
         	month = '' + (d.getMonth() + 1),
         	day = '' + d.getDate(),
         	year = d.getFullYear();

         	if (month.length < 2) month = '0' + month;
         	if (day.length < 2) day = '0' + day;
         	return [year, month, day].join('-'); 
		},
		compareDate:function(startDate,endDate){
			if(startDate <= endDate && endDate >= startDate) {
        		return true;
    		}else{
    			return false;
    		}
		},
		getNowDate:function(){
			function formatDate(date) {
            	var d = new Date(date),
            	month = '' + (d.getMonth() + 1),
            	day = '' + d.getDate(),
            	year = d.getFullYear();

            	if (month.length < 2) month = '0' + month;
            	if (day.length < 2) day = '0' + day;
            	return [year, month, day].join('-');
            }

			var now= new Date();				 
			return   formatDate(now.toLocaleDateString());
		},
		getTimeNow:function(){
			var now= new Date();
			h= now.getHours();
			m= now.getMinutes(); 
			s= now.getSeconds();
			if(m<10) m= '0'+m;
			if(s<10) s= '0'+s;
				 
			return  h + ':' + m + ':' + s;
		},
		fixTimerLessTen:function(t){
			if(t<10){
				return '0'+t;
			}else{
				return t;
			}
		},
		checkElementCount:function(array,value,limitCount){
			var count =0;
			for (var i = 0; i < array.length; i++) {
				if(array[i]){
					if(array[i]+'' == value+''){
						count++;
						if(count>=limitCount){
							return false;
						}
					}
				}else{
					count++;
					if(count>=limitCount){
						return false;
					}
				}
			}
			return true;
		},
		floatFormat:function( number, n ) {
			var _pow = Math.pow( 10 , n ) ;
			return Math.round(number * _pow ) / _pow ;
		},
		encode4HTML:function(str){
		  	return str
	        .replace(/\r\n?/g,'\n')
	        .replace(/(^((?!\n)\s)+|((?!\n)\s)+$)/gm,'')
	        .replace(/(?!\n)\s+/g,' ')
	        .replace(/^\n+|\n+$/g,'')
	        .replace(/[<>&"']/g,function(a) {
	            switch (a) {
	                case '<'    : return '&lt;';
	                case '>'    : return '&gt;';
	                case '&'    : return '&amp;';
	                case '"'    : return '&quot;';
	                case '\''   : return '&apos;';
	            }
	        })
	        .replace(/\n{2,}/g,'</p><p>')
	        .replace(/\n/g,'<br />')
	        .replace(/^(.+?)$/,'<p>$1</p>');
		},
		getInitalScope:function(){
			var basicBean = {
				ui:$rootScope.rg_gl.currentUser.ui,
				lk:$rootScope.rg_gl.currentUser.lk,
				dt:3
			}
			var newObj = JSON.parse(JSON.stringify(basicBean));
			return newObj;
		},
		splitHashTag:function(words){
			var tagslistarr = words.split(' ');
			var arr=[];
			$.each(tagslistarr,function(i,val){
			    if(tagslistarr[i].indexOf('#') == 0){
			      arr.push(tagslistarr[i]);  
			    }
			});
			return arr;
		}
	}
});

//自動補0
app.filter('numberFixedLen', function () {
  return function (n, len) {
      	var num = parseInt(n, 10);
      	len = parseInt(len, 10);
      	if (isNaN(num) || isNaN(len)) {
          	return n;
      	}
      	num = ''+num;
      	while (num.length < len) {
          	num = '0'+num;
      	}
      	return num;
	}
});

app.filter('range', function() {
	return function(input,start,total) {
		total = parseInt(total);
		for (var i=start; i<=total; i++) {
		    input.push(i);
		}
		return input;
	}
});

app.filter('numberFix', function ($filter) {
	return function (n, len) {
	    var num = $filter('number')(n, len);
	    tmpNum = ''+num;
	      
	    if(tmpNum.substring(tmpNum.indexOf('.'))=='.00'){
	    	num = $filter('number')(n, 0);
	    }
	    return num;
	}
});

app.directive('convertToNumber', function() {
  return {
    require: 'ngModel',
    link: function(scope, element, attrs, ngModel) {
      ngModel.$parsers.push(function(val) {
        return val != null ? parseInt(val, 10) : null;
      });
      ngModel.$formatters.push(function(val) {
        return val != null ? '' + val : null;
      });
    }
  };
});

app.directive('onErrorSrc', function() {
    return {
        link: function(scope, element, attrs) {
          element.bind('error', function() {
            if (attrs.src != attrs.onErrorSrc) {
              attrs.$set('src', attrs.onErrorSrc);
            }
          });
        }
    }
});