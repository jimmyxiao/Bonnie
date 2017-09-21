app.factory('worksService', function(baseHttp) {
    return {
    	queryWorksList: function(params,callback){
			return baseHttp.service('BDService/worksList' ,params,callback);
		}
    }
})