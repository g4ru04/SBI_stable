<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="import.jsp" flush="true"/>
	<script type="text/javascript" src="js/jquery-1.12.4.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<style>
.search-result-wrap {
    margin-bottom: 20px;
}
</style>
<script>
	$(function(){
		if(location.search.indexOf("id")!=-1){
			var essay_id = location.search.split("?id=")[1]-1;
			$.ajax({
				type : "POST",
				url : "uploaddoc.do",
				data : { action : "select_all_uploaddoc" },
				success : function(result) {
					var jsonObj = $.parseJSON(result);
					
					$("#title").html(jsonObj[essay_id].title);
					$("#info").html("上架時間： "+jsonObj[essay_id].upload_time);
					$("#summary").html("摘要:<br>&nbsp;&nbsp;&nbsp;&nbsp;"+jsonObj[essay_id].summary);
					$("#download").attr("onclick","window.open(\"./uploaddoc.do?action=download_doc"
							+"&file_name=" + jsonObj[essay_id].store_name
							+ "&ori_name=" + jsonObj[essay_id].show_name+"\", \"_blank\");");
					$("#f_name").html("檔名: " + jsonObj[essay_id].show_name);
					$("#essay").fadeIn();
				}
			});
		}else{
			window.location.href ="./news.jsp?tab=1";
		}
	});
</script>
<jsp:include page="header.jsp" flush="true"/>
<div class="content-wrap">
	<h2 class="page-title">商機觀測站</h2>
	<div class="search-result-wrap">
		<div id='essay' style='display:none;padding:20px;'>
			<div id='title' style='font-size: 18px;color: #000066;line-height: 1.4em;font-size: 20px;pdpadding-bottom:5px;border-bottom:1px dashed #888;'></div>
			<div id='info' style='color: #6D6D6D;padding-top: 16px;padding-bottom: 10px;'></div>
			<div id='summary' style='color: #262626;line-height: 1.8em;padding: 16px;margin-bottom: 10px;clear: both;max-width:600px;'></div>
			
			<div id='f_name'></div><br>
			<input type='button' id='download' class='btn btn-exec' onclick='' value='下載' style='margin-left:380px'>
			&nbsp;&nbsp;<input type='button' id='return' class='btn btn-primary' onclick='window.location.href ="./news.jsp#tabs-2"' value='返回'>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" flush="true"/>