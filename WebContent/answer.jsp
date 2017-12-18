<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="import.jsp" flush="true"/>

<script src="js/jquery.elevatezoom.min.js" type="text/javascript"></script>

<style>
.display_table{
	margin:10px;
 	width: calc(100vw - 40px); 
 	height:calc(100vh - 40px); 
	position:relative;
}
.display_table td{
	vertical-align: baseline;
}
.display_table .piclabel{
	font-size:1.4em;
	word-break: keep-all;
	padding:4px;
}
.display_table .piclabel ~ img{
	margin: 10px 16px;
}
</style>	

<script>
	$(function(){
		var request_str = getUrlParameter("request_str");
		
		if(request_str==null){
			window.location.href ="./login.jsp";
			return;
		}
		var para = $.parseJSON(b64DecodeUnicode(decodeURI(request_str)));
		
		if(para["style"]=="marketingStrategy"){
			$("body").append(
				$("<table/>",{"class":"marketingStrategy display_table"}).html([
					$("<tr/>").html(
						$("<td/>").html([
							$("<label/>",{
								"class":"piclabel",
								"text":para["pic_label_1"]
							}),
							$("<br/>"),
							$("<img/>",{
								"zoom-posi":1,
								"src":para["pic_url_1"],
								"style":"max-width:700px;"
							})
						])
					)
				])
			);
		}else if(para["style"]=="personaNew"){
			$("body").append(
				$("<table/>",{"class":"display_table"}).html([
					$("<tr/>").html([
						$("<td/>",{"rowspan":"2"}).html([
							$("<label/>",{
								"class":"piclabel",
								"text":para["pic_label_1"]
							}),
							$("<br/>"),
							$("<img/>",{
								"zoom-posi":1,
								"src":para["pic_url_1"],
								"style":"max-height:calc(100vh - 100px);max-width:calc(100vw - 800px);"
							})
						]),
						$("<td/>").html([
							$("<label/>",{
								"class":"piclabel",
								"text":para["pic_label_2"]
							}),
							$("<br/>"),
							$("<img/>",{
								"zoom-posi":11,
								"src":para["pic_url_2"],
								"style":"width:90%;"
							})
						])
					]),
					$("<tr/>").html(
						$("<td/>").html([
							$("<label/>",{
								"class":"piclabel",
								"text":para["pic_label_3"]
							}),
							$("<br/>"),
							$("<img/>",{
								"zoom-posi":9,
								"src":para["pic_url_3"],
								"zoomWindowHeight":"100",
								"style":"width:90%;"
							}), 
							$("<br/>"),$("<br/>"),$("<br/>")
						])
					)
				])
			);
		}
		$("img").each(function(){
			var zoom_para={};
			if($(this).attr("zoom-posi")!=null){
				zoom_para["zoomWindowPosition"]=+$(this).attr("zoom-posi");
			}
			if($(this).attr("zoomWindowHeight")!=null){
				zoom_para["zoomWindowHeight"]=+$(this).attr("zoomWindowHeight");
			}
			
			$(this).elevateZoom(zoom_para);
		})
		setTimeout(function(){
			$(".display_table,.display_table img").each(function(){
				$(this).width($(this).width());
				$(this).height($(this).height());
			});
		},200);
	});
</script>
