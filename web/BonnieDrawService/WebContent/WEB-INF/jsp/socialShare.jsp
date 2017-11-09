<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% request.setCharacterEncoding("utf-8"); %>
<html>
<script>
	var url = "${pageContext.request.requestURL}";
	var uri = "${pageContext.request.requestURI}";
	var replace = url.substring(0, (url.length - uri.length)) + "/BonnieDrawClient/#/column-detail?id=" + "${param.id}";
	window.location.replace(replace);
</script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no, width=device-width">
<meta property="og:title" content="${title}" />
<meta property="og:image" content="${image}" />
<meta property="og:description" content="${description}" />
<meta property="og:url" content="${url}" />
<meta property="og:type" content="website" />
<meta property="fb:app_id" content="1376883092359322" />
<title></title>

</head>
<body>

</body>
</html>