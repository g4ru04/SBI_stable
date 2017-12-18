<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="import.jsp" flush="true"/>
	<link rel="stylesheet" href="css/jquery-ui.min.css">
	<link rel="stylesheet" href="css/font-awesome.min.css">
	<link rel="stylesheet" href="css/styles.css">
	<link rel="stylesheet" href="css/styles_news.css">
	<link rel="stylesheet" href="css/jquery.dataTables.min.css" />
	<link rel="stylesheet" href="css/buttons.dataTables.min.css"/>
	
	<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="js/dataTables.buttons.min.js"></script>
	
	<script type="text/javascript" src="js/jquery.validate.min.js"></script>
	<script type="text/javascript" src="js/additional-methods.min.js"></script>
	<script type="text/javascript" src="js/messages_zh_TW.min.js"></script>
<jsp:include page="header.jsp" flush="true"/>

<div class="content-wrap">
	<h2 class="page-title">新聞專區</h2>
	
	<div class="search-result-wrap">
		<div id="tabs" >
			<ul>
				<li><a href="#tabs-1"><span>每日產業重點訊息</span></a></li>
				<li><a href="#tabs-2"><span>商機觀測站</span></a></li>
				<li><a href="#tabs-3"><span>使用及需求服務問卷</span></a></li>
			</ul>
			
			<div id="tabs-1">
				<div class="panel">
					<table id="news-table" width="100%">
						<thead>
							<tr>				
								<th>類別</th> 
								<th>標題</th>
								<th>來源</th>
							</tr>
						<thead>
						<tbody>
						</tbody>
					</table>
				</div>
		  	</div>
			<div id="tabs-2">
				<div class="panel">
					<table id="group-backstage-table" width="100%">
						<thead>
							<tr>
								<th>標題</th>
								<th>上架時間</th>
								<th>功能</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
					<div class="data_source"></div>
				</div>
			</div>
			<div id="tabs-3">
				<div class="panel">
					<div id="QA_finish" style='display:none;'>
						<div style='margin:120px auto;padding:40px 80px;'>
							<h3>您的問卷已經送出，感謝您填寫問卷</h3><br>
							<input type='button' id='QA_recover' value='重新填寫問卷' class='btn btn-wide btn-alert'>
						</div>
					</div>
					<div id="QA">
					 	<table class='bentable2' style='margin:40px auto;'>
							<tr><th colspan='4'>一、基本資料：<span style='position:relative;top:-40px;margin-left:54px; font-size:32px;'>商圈選擇評估問卷</span></th></tr>
							<tr>
								<td>姓名：</td><td><input type="text" name='QA_name'></td>
								<td>職稱：</td><td><input type="text" name='QA_propost'></td>
							</tr>
							<tr>
								<td>公司統編：</td><td><input type="text" name='QA_taxid'></td>
								<td>E-mail：</td><td><input type="text" name='QA_email'></td>
							</tr>
							<tr>
								<td>預計投資國家：</td><td><input type="text" name='QA_investcountry'></td>
							</tr>
							
							<tr><th colspan='3'>二、現在所屬產業：</th></tr>
							<tr>
								<td>現在所屬產業：</td>
								<td colspan='2'>
									<input type="radio" id='QA_industry_retail' name='QA_industry' value='1' onclick='$(".dinning").fadeOut(function(){$(".retail").fadeIn();});'>
									<label for="QA_industry_retail"><span class="form-label">零售業</span></label>
									<input type="radio" id='QA_industry_dining' name='QA_industry' value='2' onclick='$(".retail").fadeOut(function(){$(".dinning").fadeIn();});'>
									<label for="QA_industry_dining"><span class="form-label">餐飲業</span></label>
								</td>
							</tr>
							<tr class='retail' style='display:none;'>
								<td>主要營業項目：<br>(零售業)　</td>
								<td colspan='3'>
									<table><tr><td>
										<input type="checkbox" id='QA_industry_item_1' name='QA_industry_item' value='1'>
										<label for="QA_industry_item_1"><span class="form-label">綜合商品類</span></label>
									</td><td>
										<input type="checkbox" id='QA_industry_item_2' name='QA_industry_item' value='2'>
										<label for="QA_industry_item_2"><span class="form-label">食品、飲料製品類 </span></label>
									</td><td>
										<input type="checkbox" id='QA_industry_item_3' name='QA_industry_item' value='3'>
										<label for="QA_industry_item_3"><span class="form-label">服飾品類 </span></label>
									</td></tr><tr><td>
										<input type="checkbox" id='QA_industry_item_4' name='QA_industry_item' value='4'>
										<label for="QA_industry_item_4"><span class="form-label">家庭器具及用品類</span></label>
									</td><td>
										<input type="checkbox" id='QA_industry_item_5' name='QA_industry_item' value='5'>
										<label for="QA_industry_item_5"><span class="form-label">文教、育樂用品類 </span></label>
									</td><td>
										<input type="checkbox" id='QA_industry_item_6' name='QA_industry_item' value='6'>
										<label for="QA_industry_item_6"><span class="form-label">藥品、醫療用品及化妝品類</span></label>
									</td></tr></table>
								</td>
							</tr>
							<tr class='dinning' style='display:none;'>
								<td>主要營業項目：<br>(餐飲業)　</td>
								<td colspan='3'>
									<input type="radio" id='QA_industry_item2_1' name='QA_industry_item2' value='1'>
									<label for="QA_industry_item2_1"><span class="form-label">餐館類</span></label>
									<input type="radio" id='QA_industry_item2_2' name='QA_industry_item2' value='2'>
									<label for="QA_industry_item2_2"><span class="form-label">飲料類</span></label>
								</td>
							</tr>
							
							<tr><th colspan='3'>三、預計投資產業：</th></tr>
							<tr>
								<td>預計投資產業：</td>
								<td colspan='2'>
									<input type="radio" id='QA_invest_industry_1' name='QA_invest_industry' value='1' onclick='$(".invest_item").animate({opacity: "0"});'>
									<label for="QA_invest_industry_1"><span class="form-label">本業</span></label>
									<input type="radio" id='QA_invest_industry_2' name='QA_invest_industry' value='2' onclick='$(".invest_item").animate({opacity: "1"});'>
									<label for="QA_invest_industry_2"><span class="form-label">非本業</span></label>
								</td>
							</tr>
							<tr class='invest_item dinning' style='display:none;opacity:0;'>
								<td>預計投資項目：<br>(餐飲業-非本業)　</td>
								<td colspan='3'>
									<table><tr><td>
										<input type="checkbox" id='QA_invest_industry_item_1' name='QA_invest_industry_item' value='1'> 
										<label for="QA_invest_industry_item_1"><span class="form-label">綜合商品類</span></label>
									</td><td>
										<input type="checkbox" id='QA_invest_industry_item_2' name='QA_invest_industry_item' value='2'> 
										<label for="QA_invest_industry_item_2"><span class="form-label">食品、飲料製品類</span></label>
									</td><td>
										<input type="checkbox" id='QA_invest_industry_item_3' name='QA_invest_industry_item' value='3'>
										<label for="QA_invest_industry_item_3"><span class="form-label">服飾品類 </span></label>
									</td></tr><tr><td>
										<input type="checkbox" id='QA_invest_industry_item_4' name='QA_invest_industry_item' value='4'>
										<label for="QA_invest_industry_item_4"><span class="form-label">家庭器具及用品類</span></label>
									</td><td>
										<input type="checkbox" id='QA_invest_industry_item_5' name='QA_invest_industry_item' value='5'>
										<label for="QA_invest_industry_item_5"><span class="form-label">文教、育樂用品類 </span></label>
									</td><td>
										<input type="checkbox" id='QA_invest_industry_item_6' name='QA_invest_industry_item' value='6'>
										<label for="QA_invest_industry_item_6"><span class="form-label">藥品、醫療用品及化妝品類</span></label>
									</td></tr></table>
								</td>
							</tr>
							<tr class='invest_item retail' style='display:none;opacity:0;'>
								<td>預計投資項目：<br>(零售業-非本業)　</td>
								<td colspan='3'>
									<input type="radio" id='QA_invest_industry_item2_1' name='QA_invest_industry_item2' value='1'>
									<label for="QA_invest_industry_item2_1"><span class="form-label">餐館類</span></label>
									<input type="radio" id='QA_invest_industry_item2_2' name='QA_invest_industry_item2' value='2'>
									<label for="QA_invest_industry_item2_2"><span class="form-label">飲料類</span></label>
								</td>
							</tr>
							<tr>
								<td>預計投資品牌：</td>
								<td colspan='2'>
									<input type="radio" id='QA_invest_brand_1' name='QA_invest_brand' value='1'>
									<label for="QA_invest_brand_1"><span class="form-label">既有品牌</span></label>
									<input type="radio" id='QA_invest_brand_2' name='QA_invest_brand' value='2'>
									<label for="QA_invest_brand_2"><span class="form-label">新品牌</span></label>
								</td>
							</tr>
							<tr>
								<td>預計投資型態：<br>(可複選)　</td>
								<td colspan='3'>
									<input type="checkbox" id='QA_invest_pattern_1' name='QA_invest_pattern' value='1'>
									<label for="QA_invest_pattern_1"><span class="form-label">獨資</span></label>
									<input type="checkbox" id='QA_invest_pattern_2' name='QA_invest_pattern' value='2'>
									<label for="QA_invest_pattern_2"><span class="form-label">合資</span></label>
									<input type="checkbox" id='QA_invest_pattern_3' name='QA_invest_pattern' value='3'>
									<label for="QA_invest_pattern_3"><span class="form-label">購併</span></label>
									<input type="checkbox" id='QA_invest_pattern_4' name='QA_invest_pattern' value='4'>
									<label for="QA_invest_pattern_4"><span class="form-label">加盟</span></label>
								</td>
							</tr>
							<tr>
								<td>預計投資型式：</td>
								<td colspan='2'>
									<input type="radio" id='QA_invest_type_1' name='QA_invest_type' value='1'>
									<label for="QA_invest_type_1"><span class="form-label">實體店面</span></label>
									<input type="radio" id='QA_invest_type_2' name='QA_invest_type' value='2'>
									<label for="QA_invest_type_2"><span class="form-label">網路店面</span></label>
								</td>
							</tr>
							<tr>
								<td>預計投資金額：</td>
								<td colspan='3'>
									<table><tr><td>
										<input type="radio" id='QA_invest_amount_1' name='QA_invest_amount' value='1'>
										<label for="QA_invest_amount_1"><span class="form-label">＜100萬元</span></label>
									</td><td>
										<input type="radio" id='QA_invest_amount_2' name='QA_invest_amount' value='2'>
										<label for="QA_invest_amount_2"><span class="form-label">100-500萬元</span></label>
									</td><td>
										<input type="radio" id='QA_invest_amount_3' name='QA_invest_amount' value='3'>
										<label for="QA_invest_amount_3"><span class="form-label">500-1,000萬元</span></label>
									</td></tr><tr><td>
										<input type="radio" id='QA_invest_amount_4' name='QA_invest_amount' value='4'>
										<label for="QA_invest_amount_4"><span class="form-label">1,000-1,500萬元</span></label>
									</td><td>
										<input type="radio" id='QA_invest_amount_5' name='QA_invest_amount' value='5'>
										<label for="QA_invest_amount_5"><span class="form-label">1,500-2,000萬元</span></label>
									</td><td>
										<input type="radio" id='QA_invest_amount_6' name='QA_invest_amount' value='6'>
										<label for="QA_invest_amount_6"><span class="form-label">＞2,000萬元</span></label>
									</td></tr></table>
								</td>
							</tr>
							<tr><th colspan='4'>四、建議及需求：</th></tr>
							<tr>
								<td colspan='4'>
									<textarea id='recommendation' style='width:100%;height:120px;'></textarea>
								</td>
							</tr>
							<tr>
								<td colspan='4' style='text-align:center;'>
									<input type='button' id='QA_over' class='btn btn-wide btn-darkblue' value='完成送出'>&nbsp;&nbsp;&nbsp; 
									<input type='button' id='QA_reset' class='btn btn-wide btn-gray' value='重新輸入'>
								</td>
							</tr>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
$(function(){
	
	$( "#tabs" ).tabs();
	
	draw_uploaddocs({"action": "select_all_uploaddoc"});
	
	$("#news-table").DataTable({
		dom: "<t>p",
		destroy: true,
		language: {
			"search" : "搜尋:",
			"paginate": { 
		        "previous": "←",
		        "next":     "→"
		    }
		},
		"order": [],
		ajax: {
			url : "news.do",
			dataSrc: "",
			type : "POST",
			data : {
				action : "onload"
			}
		},
        columnDefs: [{
			targets: 1,
			searchable: false,
			orderable: false,
			render: function ( data, type, row ) {
// 				console.log(row);
				var options = "<a href='" + row.Url + "' target='_blank'>《" + row.Title + "》</a>";
				return options;
			}
		},{
			targets: 2,
			searchable: false,
			orderable: false,
			render: function ( data, type, row ) {
				var options = "【" + row.source + "】";
				return options;
			}
		}],
		columns: [
			{"data": "Type", "width": "15%", "defaultContent": ""},
			{"data": "Title", "width": "70%", "defaultContent": ""},
			{"data": null, "width": "15%", "defaultContent": ""}
		]
	});	

	
	//init data_source
	$('.data_source').empty();
	var obj_a = $("<a></a>")
		.attr("href", "http://www.dataa.com.tw/")
		.attr("target", "_blank")
		.text("Dataa大數據研究中心");
	var obj_h6 = $("<h6></h6>")
		.attr("style", "margin:5px;")
		.append("<span>資料來源: <span>")
		.append(obj_a);

	$('.data_source').append(obj_h6);
	$("#QA_reset").click(function(){
		$('#QA input[name="QA_name"]').val('');
		$('#QA input[name="QA_propost"]').val('');
		$('#QA input[name="QA_taxid"]').val('');
		$('#QA input[name="QA_email"]').val('');
		$('#QA input[name="QA_investcountry"]').val('');
		$('#QA input[name="QA_industry"]:checked').prop('checked',false);
		$('#QA input[name="QA_industry_item"]:checked').prop('checked',false);
		$('#QA input[name="QA_industry_item2"]:checked').prop('checked',false);
		$('#QA input[name="QA_invest_industry"]:checked').prop('checked',false);
		$('#QA input[name="QA_invest_industry_item"]:checked').prop('checked',false);
		$('#QA input[name="QA_invest_industry_item2"]:checked').prop('checked',false);
		$('#QA input[name="QA_invest_brand"]:checked').prop('checked',false);
		$('#QA input[name="QA_invest_pattern"]:checked').prop('checked',false);
		$('#QA input[name="QA_invest_type"]:checked').prop('checked',false);
		$('#QA input[name="QA_invest_amount"]:checked').prop('checked',false);
	});
	$("#QA_over").click(function(){
		var warning="";
		if($('#QA input[name="QA_name"]').val().length>80) {
			warning+="姓名欄長度不可超過80字<br/>";
		}
		if($('#QA input[name="QA_propost"]').val().length>40) {
			warning+="職稱欄長度不可超過40字<br/>";
		}
		if($('#QA input[name="QA_taxid"]').val().length>8) {
			warning+="公司統編欄長度不可超過8字<br/>";
		}
		if($('#QA input[name="QA_email"]').val().length>100) {
			warning+="email欄長度不可超過100字<br/>";
		}
		if($('#QA input[name="QA_investcountry"]').val().length>200) {
			warning+="預計投資國家欄長度不可超過200字<br/>";
		}
		if($('#QA textarea').val().length>400) {
			warning+="建議及需求欄長度不可超過400字<br/>";
		}
		
		if($('#QA input[name="QA_name"]').val().length==0) {
			warning+="請填寫姓名<br/>";
		}
		if($('#QA input[name="QA_propost"]').val().length==0) {
			warning+="請填寫職稱<br/>";
		}
		if($('#QA input[name="QA_taxid"]').val().length==0) {
			warning+="請填寫公司統編<br/>";
		}
		if($('#QA input[name="QA_email"]').val().length==0) {
			warning+="請填寫email<br/>";
		}
		if($('#QA input[name="QA_investcountry"]').val().length==0) {
			warning+="請填寫預計投資國家<br/>";
		}
		if($('#QA input[name="QA_industry"]:checked').length==0) {
			warning+="請填寫現在所屬產業<br/>";
		}
		if(($('#QA input[name="QA_industry"]:checked').val()==1?
				checkboxstr('QA input[name="QA_industry_item"]:checked'):checkboxstr('QA input[name="QA_industry_item2"]:checked')).length==0) {
			warning+="請填寫主要營業項目<br/>";
		}
		if($('#QA input[name="QA_invest_industry"]:checked').length==0) {
			warning+="請填寫預計投資產業<br/>";
		}
		if($('#QA input[name="QA_invest_industry"]:checked').val()==2
				&& ($('#QA input[name="QA_invest_industry"]:checked').val()==1?"":($('#QA input[name="QA_industry"]:checked').val()==1?checkboxstr('QA input[name="QA_invest_industry_item2"]:checked'):checkboxstr('QA input[name="QA_invest_industry_item"]:checked'))).length==0) {
			warning+="請填寫預計投資項目<br/>";
		}
		if($('#QA input[name="QA_invest_brand"]:checked').length==0) {
			warning+="請填寫預計投資品牌<br/>";
		}
		if(checkboxstr('QA input[name="QA_invest_pattern"]:checked').length==0) {
			warning+="請填寫預計投資型態<br/>";
		}
		if($('#QA input[name="QA_invest_type"]:checked').length==0) {
			warning+="請填寫預計投資型式<br/>";
		}
		if($('#QA input[name="QA_invest_amount"]:checked').length==0) {
			warning+="請填寫預計投資金額<br/>";
		}
		
		if(warning.length!=0){
			warningMsg('警告', warning);
			return;
		}
		if(window.scenario_record){scenario_record("區位選擇問卷","['"+$('#QA input[name="QA_name"]').val()+"','"+$('#QA input[name="QA_propost"]').val()+"','"+$('#QA input[name="QA_taxid"]').val()+"','"+$('#QA input[name="QA_email"]').val()+"','"+$('#QA input[name="QA_investcountry"]').val()+"',"+$('#QA input[name="QA_industry"]:checked').val()+",["+($('#QA input[name="QA_industry"]:checked').val()==1?checkboxstr('QA input[name="QA_industry_item"]:checked'):checkboxstr('QA input[name="QA_industry_item2"]:checked'))+"],"+$('#QA input[name="QA_invest_industry"]:checked').val()+",["+($('#QA input[name="QA_invest_industry"]:checked').val()==1?"":($('#QA input[name="QA_industry"]:checked').val()==1?checkboxstr('QA input[name="QA_invest_industry_item2"]:checked'):checkboxstr('QA input[name="QA_invest_industry_item"]:checked')))+"],"+$('#QA input[name="QA_invest_brand"]:checked').val()+",["+checkboxstr('QA input[name="QA_invest_pattern"]:checked')+"],"+$('#QA input[name="QA_invest_type"]:checked').val()+","+$('#QA input[name="QA_invest_amount"]:checked').val()+","+$('#QA textarea').val()+"]");}
		$.ajax({
			type : "POST",
			url : "regionselect.do",
			data : {
				action : "insert_QA",
				QA_name : $('#QA input[name="QA_name"]').val(),
				QA_propost : $('#QA input[name="QA_propost"]').val(),
				QA_taxid : $('#QA input[name="QA_taxid"]').val(),
				QA_email : $('#QA input[name="QA_email"]').val(),
				QA_investcountry : $('#QA input[name="QA_investcountry"]').val(),
				QA_industry : $('#QA input[name="QA_industry"]:checked').val(),
				QA_industry_item : ($('#QA input[name="QA_industry"]:checked').val()==1?checkboxstr('QA input[name="QA_industry_item"]:checked'):checkboxstr('QA input[name="QA_industry_item2"]:checked')),
				QA_invest_industry : $('#QA input[name="QA_invest_industry"]:checked').val(),
				QA_invest_industry_item : ($('#QA input[name="QA_invest_industry"]:checked').val()==1?"":($('#QA input[name="QA_industry"]:checked').val()==1?checkboxstr('QA input[name="QA_invest_industry_item2"]:checked'):checkboxstr('QA input[name="QA_invest_industry_item"]:checked'))),
				QA_invest_brand : $('#QA input[name="QA_invest_brand"]:checked').val(),
				QA_invest_pattern : checkboxstr('QA input[name="QA_invest_pattern"]:checked'),
				QA_invest_type : $('#QA input[name="QA_invest_type"]:checked').val(),
				QA_invest_amount : $('#QA input[name="QA_invest_amount"]:checked').val(),
				QA_recommendation : $('#QA textarea').val()
			},
			success : function(result) {
    			if("success"==result){
    				$("#QA_finish").show();
    				$("#QA").hide();
    			}else{
    				warningMsg('警告', "產生異常問題，請重整");
    			}
			},error: function(){
				warningMsg('警告', "儲存資料異常，請重整");
	        }
    	});
	});
	$("#QA_recover").click(function(){
		$("#QA_reset").trigger("click");
		$("#QA_finish").hide();
		$("#QA").show();
	});
});

function draw_uploaddocs(parameter){
	var count=1;
	$("#group-backstage-table").DataTable({
		dom: "<t>p",
		destroy: true,
		language: {
			"search" : "搜尋:",
			"paginate": { 
		        "previous": "←",
		        "next":     "→",
		    }
		},
		"order": [],
		ajax: {
			url : "uploaddoc.do",
			dataSrc: "",
			type : "POST",
			data : parameter
		},
        columnDefs: [{
			targets: 0,
			searchable: false,
			orderable: false,
			render: function ( data, type, row ) {
				var options = "<a href=\"./uploaddocs.jsp?id="+(count++)+"\">"+row.title+"</a>";
				return options;
			}
		},{
			targets: -1,
			searchable: false,
			orderable: false,
			render: function ( data, type, row ) {
				var options =
					"<a class='btn btn-darkblue'"
					+" onclick=\'window.open(\"./uploaddoc.do?action=download_doc&file_name="
					+ row.store_name
					+ "&ori_name=" + row.show_name+"\", \"_blank\");\'"
					+ " value='"
					+ row.id
					+ "'><font color='white'>下載</font></a>";
				return options;
			}
		}],
		columns: [
			{"data": "title", "width": "80%", "defaultContent":""},
			{"data": "upload_time", "width": "10%", "defaultContent":""},
			{"data": null, "width": "10%", "defaultContent":""}
		]
	});	
}
function checkboxstr(selector) {
	var str = '';
	$('#' + selector).each(function(i) {
		if (i != 0)
			str += ",";
		str += $(this).val();
	});
	return str;
}
</script>
<jsp:include page="footer.jsp" flush="true"/>