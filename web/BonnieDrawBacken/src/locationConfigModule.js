(function () {
	angular.module('locationConfigModule', [])
		// dev
		.value("locationIP","http://localhost:8080/")
		.value("serviceName","BonnieDrawService")
		.value("backendName","BonnieDrawBacken");

		// release
		// .value("locationIP","https://www.bonniedraw.com/")
		// .value("serviceName","bonniedraw_service")
		// .value("backendName","BDBackend");
})();