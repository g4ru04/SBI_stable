<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% 
String menu = (String) request.getSession().getAttribute("menu"); 
String privilege = (String) request.getSession().getAttribute("privilege"); 
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>東南亞商機定位工具</title>
<link rel="Shortcut Icon" type="image/x-icon" href="./images/cdri-logo.gif" />
<link rel="stylesheet" href="css/styles.css" />
<link href="css/jquery-ui-1.12.0/jquery-ui.css" rel="stylesheet">
<link rel="stylesheet" href="css/font-awesome.min.css">

<script type="text/javascript" src="js/jquery-1.12.4.min.js"></script>
<script type="text/javascript" src="css/jquery-ui-1.12.0/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/additional-methods.min.js"></script>
<script type="text/javascript" src="js/messages_zh_TW.min.js"></script>
</head>
<body>
<input type="hidden" id="glb_menu" value='<%= menu %>' />
<input type="hidden" id="glb_privilege" value="<%= privilege %>" />

	<div class="page-wrapper" >
	
		<div class="header">
			<div class="userinfo">
				<p>使用者<span><%= (request.getSession().getAttribute("user_name")==null)?"":request.getSession().getAttribute("user_name").toString() %></span></p>
				<a href="#" id="logout" class="btn-logout">登出</a>
			</div>
		</div>
		
		<jsp:include page="menu.jsp"></jsp:include>
				
	 	<h2 id="title" class="page-title">東南亞商機定位工具</h2>
	 	
		<div class="content-wrap" style="display:none">
		</div>
		
		<script src="js/sbi/menu.js"></script>
		<footer class="footer">
			財團法人商業發展研究院  <span>電話(02)7707-4800 | 傳真(02)7713-3366</span> 
		</footer>
	</div>
	
	<div class="content-wrap">
		<div class="search-result-wrap" style="height:100%">
			<iframe id="ifm1" width="100%" height="100%" src="/persona/Persona_ProductAdminCtrl"></iframe>
		</div>
	</div>

<script>
	$(function() {
		$("#logout").click(function(e) {
			$.ajax({
				type : "POST",
				url : "login.do",
				data : {
					action : "logout"
				},
				success : function(result) {
					top.location.href = "login.jsp";
				}
			});
		});

		$("#ifm1").load(function() {
			$(this).height( $(this).contents().find("body").height() );
			$(this).width( $(this).contents().find("body").width() );
		}); 
		
		$.ajax({
			type : "POST",
			url : "/persona/AdminLoginCtrl",
			data : {
				Action : "Api_Login",
				Key : "A7BBMK3",
				UserId : "admin1",
				GroupId  : "group1",
				Password : "caf1a3dfb505ffed0d024130f58c5cfa"
			},
			success : function(result) {
				var $iframe = $('#ifm1');
			    if ( $iframe.length > 0 ) {
			        $iframe.attr('src', "/persona/Persona_OpportunityUser1Ctrl?Action=Page1BF");   
			    }
			},
			error : function(result) {

			}
		});
	});
	
	
</script>

</body>
</html>