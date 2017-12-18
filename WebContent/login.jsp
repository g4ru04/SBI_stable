<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF8">
	<title>SBI 登入</title>
	<link rel="stylesheet" href="css/styles.css">
	<link rel="Shortcut Icon" type="image/x-icon" href="./images/cdri-logo.gif" />
	<script src="js/jquery-1.12.4.min.js"></script>
	<script src="js/common.js"></script>
	<style>
	.info_after{
		padding:15px 20px;
	}
	.register_epaper{
		color:white;
		text-align:center;
		padding:5px 10px;
	}
	#loading {
	    position: fixed;
	    background-color: rgba(128,128,128,0.5);
	    width: 100vw;
	    height: 100vh;
	    z-index: 9999;
	    top:0px;
	    right:0px;
	}
	#loading img{
	    margin: auto;
	    left: 0;
	    right: 0;
	    top: 0;
	    bottom: 0;
	    position: absolute;
	    width:100px;
	    border-radius:10px;
	    background:#fff;
	}
	#loading div{
	    margin: auto;
	    left: 0;
	    right: 0;
	    top: 0;
	    bottom: 0;
	    position: absolute;
	    width:240px;
	    height:24px;
	    border-radius:10px;
	    background:white;
	    font-size:20px;
	    padding: 20px;
	    text-align:center;
	    box-sizing: content-box;
	}
	</style>
	
	<script type="text/javascript">
	<%if(request.getSession().getAttribute("user_name")!=null){%>
		top.location.href="/sbi/news.jsp";
	<%}%>
	    function changeImg(){
	        document.getElementById("validateCodeImg").src="HandleDrawValidateCode.do?t=" + Math.random();
	    }
	
	    function to_login(){
	    	$(".error").removeClass("error");
	    	$(".error-msg").remove();
	    	var wrong = 0;
	
	    	if($("#group_name").val().length<1){
	    		$("#group_name").after("<span class='error-msg'>請選擇公司</span>");
	    		wrong=1;
	    	}

	    	if($("#user_name").val().length<1){
	    		$("#user_name").after("<span class='error-msg'>請輸入帳號</span>");
	    		wrong=1;
	    	}
	    	
	    	if($("#pswd").val().length<1){
	    		$("#pswd").after("<span class='error-msg'>請輸入密碼</span>");
	    		wrong=1;
	    	}
	    	
	    	if($("#validateCode").val().length<1){
	    		$("#validateCode").after("<span class='error-msg'>請輸入驗證碼</span>");
	    		wrong=1;
	    	}

	    	if(wrong == 0){
	    		loading();
	    		$.ajax({
		    		type : "POST",
					url : "login.do",
					cache : false,
					data : {
						action : "login",
						group_name : $("#group_name").val(),
	                	user_name : $("#user_name").val(),
						pswd : $("#pswd").val(),
						validateCode : $("#validateCode").val()
	                },
	                success: function(data) {
	                	var json_obj = $.parseJSON(data);
	                	if (json_obj.message=="success"){
	                		window.location.href = "news.jsp";
	                	}else{
	                		loading('over');
	                	}
	                	
	                	if (json_obj.message=="failure"){
	                		$("#validateCode").val("");
	                		$("#pswd").val("");
	            			$("#pswd").focus();
	    					$("#pswd").after("<span class='error-msg'>密碼錯誤</span>");
	                		changeImg();
	                	}
	                	if (json_obj.message=="code_failure") {
	                		$("#validateCode").val("");
	    					$("#pswd").val("");
	            			$("#validateCode").focus();
	    					$("#validateCode").after("<span class='error-msg'>驗證碼錯誤</span>");
	                	}
	                	if (json_obj.message=="user_failure"){
	                		$("#username").after("<span id='user_err_mes' class='error-msg'>查無此帳號</span>");
	                		changeImg();
	                		wrong=1;
	                	}
	                	
	                },error: function (jqXHR, textStatus, errorThrown) {
	                	loading('get error: '+jqXHR.responseText);
	                	alert("輸入錯誤，請確定輸入大小寫");
	        		}
	            });
	    	}
	    }
	    
		$(function() {
			//aber1,2 區別
			setTimeout(function(){
				$.ajax({
		    		type : "POST",
					url : "viewStatus",
//	 				async : false ,
					data: { "action":"getGlobalIP"},
					success:function(result){
						pSessionSetup();
						if(result=="23.99.114.140"){
	                		$(".is_aber1").html("&nbsp;");
	                	}
	                }
				});
			},3000);
			
			$("#validateCodeImg").click(function() {
				changeImg();
			});
	
			$("#group_name").focus();
			$("#user_name").blur(function(){
		    	$(".error").removeClass("error");
		    	$(".error-msg").remove();
		    	
				$.ajax({
	                url : "login.do",
	                type : "POST",
	                cache : false,
	                delay : 1500,
	                data : {
	                	action : "check_user_exist",
	                	group_name : $("input[name='group_name']").val(),
	                	user_name : $("input[name='user_name']").val()
	                },
	                success: function(data) {
	                	var json_obj = $.parseJSON(data);
	                	if (json_obj.message=="user_failure"){
	                		if($("#user_name").val().length >0){
	        					$("#user_name").after("<span class='error-msg'>查無此帳號</span>");
	                		}
	                		
	                		if($("#user_name").val().length ==0){
	                			if($("#user_err_mes").length){
	                    			$("#user_err_mes").remove();
	                			}
	                		}
	                	} else if (json_obj.message=="success") {
	               			if($("#user_err_mes").length){
	                   			$("#user_err_mes").remove();
	               			}                		
	                	}
	                }
	            });
			});
			
			$('#user_name').keypress(function() {
				if($("#user_err_mes").length){
					$("#user_err_mes").remove();
				}
			});
	
			$("#login_btn").click(function(){
				to_login();
		 	});
	
			$("input").keydown(function (event) {
		        if (event.which == 13) {
		        	to_login();
		        }
		    }); 
	
			$("#reset_btn").click(function(){
				$(".error").removeClass("error");
				$(".error-msg").remove();
				$("#group_name").val("");
				$("#user_name").val("");
				$("#pswd").val("");
				$("#validateCode").val("");
				changeImg();
			});
		});
	</script>
</head>
<body class="login-body">
	<div class="login-wrapper">

		<div class="login-panel-wrap">
		<div class="login-panel">
			<h1 class="sys-logo">SBI</h1>
			<h2>使用者登入</h2>
			<form id="login-form-post">
				<label for="uninumber">
					<span class="block-label">公司</span>
					<input type="text" id="group_name" name="group_name"></select>
				</label>
				<label for="username">
					<span class="block-label">帳號</span>
					<input type="text" id="user_name" name="user_name">
				</label>
				<label for="pswd">
					<span class="block-label">密碼</span>
					<input type="password" id="pswd" name="pswd">
				</label>
				<div class="verify-wrap">
					<label for="validateCode">
						<span class="block-label">認證碼</span>
						<input type="text" id="validateCode" name="validateCode">
					</label>
					<div class="captcha-wrap">
						<img title="點選圖片可重新產生驗證碼" src="HandleDrawValidateCode.do" id="validateCodeImg">
					</div>
				</div><!-- /.verify-wrap -->
				<div class="login-btn-wrap">
					<a href="#" class="login-button" id="login_btn">登入</a>
					<a href="#" class="login-reset-button" id="reset_btn">清除重填</a>					
				</div><!-- /.login-btn-wrap -->
				<% if("61.218.8.55".equals(java.net.InetAddress.getLocalHost().getHostAddress())){ %>
				<div class="login-btn-wrap">
					<div align="center" class='info_after'>
						<a href="./registerEpaper.jsp" class="register_epaper" id="register_epaper">訂閱電子報</a>		
					</div>			
				</div>
				<% } %>
			</form>
		</div><!-- /.login-panel -->
		</div><!-- /.login-panel-wrap -->

		<div class="login-footer">
			<p>委辦單位：<a href="http://www.moea.gov.tw/Mns/doit/home/Home1.aspx" target="_blank"><img src="images/LOGO_MOEA.jpg" style="width: 8.5%"><a class='is_aber1'></a>經濟部<a class='is_aber1'></a>技術處</a>&nbsp;&nbsp;執行單位：<a href="http://www.cdri.org.tw/MainPage.aspx?func=884C5749-91C8-4730-9941-794" target="_blank"><img src="images/LOGO_CDRI.jpg" style="width: 7%">財團法人商業發展研究院</a></p>
            <p>Copyright © Commerce Development Research Institute <strong>財團法人商業發展研究院</strong>登載的內容係屬本院版權所有</p>
            <p>10665臺北市復興南路一段303號4樓&nbsp;&nbsp;&nbsp;電話：02-7713-1010&nbsp;&nbsp;&nbsp;傳真：02-7713-3366</p>
            <p>請使用IE8以上版本&nbsp;&nbsp;&nbsp;最佳瀏覽解析度<!-- start: 2013 correcting -->1280×1024<!-- end: 2013 correcting --></p>

<!-- 			北祥股份有限公司 <span>服務電話：+886-2-2658-1910 | 傳真：+886-2-2658-1920</span> -->
		</div><!-- /.login-footer -->
	<script>//### common method ###
		function loading(str){
			if(!window.loading_count){
				loading_count=0;
			}
			if(!window.loading_timeout){
				loading_timeout=[];
			}
			if($("#loading").length==0){
				$("body").append('<div id="loading" style="display: none;">'
			 			+'<img src="./images/loading.gif">'
		// 					+'<div>資料讀取中，請稍候‧‧‧</div>'
						+'</div>');
			}
			
			if(str!=null){
				if(str!="over"){
					console.log("loading_error:"+str);
				}
				loading_count--;
				
				clearTimeout(loading_timeout.shift());
				
				if(loading_count==0){
					$("#loading").hide();
				}
			}else{
				loading_count++;
				loading_timeout.push ( 
					setTimeout(function(){
						$("#loading").show();
					},400)
				);
			}
		}
	</script>
	</div><!-- /.login-wrapper -->
</body>
</html>
