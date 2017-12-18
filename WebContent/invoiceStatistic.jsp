<!-- 
	發票資料六種查詢頁面
	table: tb_invoice_county_stat
	資料列範例: (2017-04-01,臺北市,A,土木工程業,無載具,1356,4079637)
-->

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="import.jsp" flush="true"/>

<script src="js/d3.v3.min.js"></script>
<script type="text/javascript" src="js/jquery-migrate-1.4.1.min.js"></script>
<script type="text/javascript" src="js/topojson.min.js"></script>

<style>
/* 整體方向調整 共通css */
	.ui-widget,.ui-widget select,.ui-widget input{
		font-family: "微軟正黑體", "Microsoft JhengHei", 'LiHei Pro', Arial, Helvetica, sans-serif;
	}
	.ui-dialog{
		z-index:10000;
	}
	select, input[type="text"]{
		width:100%;
	}
	
	.axis path, .axis line {
	    fill: none;
	    stroke: #000;
	    shape-rendering: crispEdges;
	}
	.ui-autocomplete {
		max-height: 40%;
		overflow-y: auto;
		overflow-x: hidden;
	}
	.ui-menu .ui-menu-item{
		font-family: "敺株�甇��擃�", "Microsoft JhengHei", 'LiHei Pro', Arial, Helvetica, sans-serif, \5FAE\8EDF\6B63\9ED1\9AD4,\65B0\7D30\660E\9AD4;
	}
</style>
<style> 
/* 	細部 */
	#tabs{
		box-shadow: 2px 2px 6px rgba(0, 0, 0, 0.3);
	}
	#tabs ul li a{
		text-align: center;
		padding: .5em 1.3em;
	}
	.formatted-display{
		margin: 20px auto;
	}
	.formatted-display td{
		padding:6px;
	}
	.formatted-display tr > td:nth-child(1){
		text-align: justify;
	    text-justify: inter-word;
	}
	.ui-widget-content a.btn {
		font-size:24px;
		font-weight:lighter; 
		margin:10px;
		color: #fff;
	}
	
	table.text-align-r td{
		text-align:right;
	}
	
</style>
<style>
/* populationNew.css重複了的 */
	.excel-table th{
		font-weight: 700;
		font-family: Times New Roman, cursive;
		background: #D0CECE;
	}
	.excel-table tr{
		height: 22px;
	}
	.excel-table tr:nth-child(odd){
		background: #D9E1F2;
	}
	.excel-table tr:nth-child(even){
		background: #FFF2CC;
	}
	
	.excel-table td{
		padding: 0 20px;
		word-break: keep-all;
	}
	.excel-table td:nth-child(1){
		text-align:center;
	}
	.excel-table td:nth-child(3),.excel-table td:nth-child(4){
		text-align: right;
	}
	.excel-table tr:nth-child(1) td:nth-child(1){
		background: #fff;
		font-size: 20px;
		padding:10px 0px;
	}
	.excel-table tr:nth-child(1) td:nth-child(1){ 
	 	background: #fff; 
	 	text-align:center; 
	 	font-size: 20px; 
	 } 
	 
 
</style>
<script>

	$(function(){
		setup_jquery_ui();
		setup_select_options();
	});
	
</script>

<jsp:include page="header.jsp" flush="true"/>
<div class="page-wrapper" >
<div class="content-wrap">
<h2 class="page-title">消費軌跡統計</h2>
	<div class="search-result-wrap">

			<h4>搜尋條件</h4>
			<div id="tabs">
				<ul>
					<li><a href="#tabs-1">消費軌跡年度查詢<br>-依縣市-</a></li>
					<li><a href="#tabs-2">消費軌跡年度查詢<br>-依縣市業種-</a></li>
					
					<li><a href="#tabs-3">消費軌跡業種查詢<br>-依業種-</a></li>
					<li><a href="#tabs-4">消費軌跡業種查詢<br>-依縣市/年度-</a></li>
					
					<li><a href="#tabs-5">消費軌跡單月份查詢<br>-依業種-</a></li>
					<li><a href="#tabs-6">消費軌跡單月份查詢<br>-依縣市-</a></li>
					
					<li><a href="#tabs-7">消費軌跡年度比較查詢<br>-月份/季度-</a></li>
				</ul>
				<div id="tabs-1">
					<table class='formatted-display' id='one'>
						<tr>
							<td colspan='4'></td>
							<td rowspan='4'>
								<table>
									<tr>
										<td>
											<a class='btn btn-darkblue btn-search' >搜尋</a>
										</td>
									</tr>
									<tr>
										<td>
											<a class='btn btn-darkblue btn-reset'>重設</a>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								發票年月：
							</td><td>
								<select name='selectYearMonthStart'><option value="">請選擇</option></select>
							</td><td>
								～
							</td><td>
								<select name='selectYearMonthEnd'><option value="">請選擇</option></select>
							</td>
							
						</tr>
						<tr>
							<td>
								縣市：
							</td>
							<td colspan='3'>
								<select name="selectSearchCity" class="Xunrestricted"><option value="">請選擇</option></select>
							</td>
						</tr>
					</table>
				</div>
				<div id="tabs-2">
					<table class='formatted-display' id='two'>
						<tr>
							<td colspan='4'></td>
							<td rowspan='4'>
								<table>
									<tr>
										<td>
											<a class='btn btn-darkblue btn-search' >搜尋</a>
										</td>
									</tr>
									<tr>
										<td>
											<a class='btn btn-darkblue btn-reset'>重設</a>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								發票年月：
							</td><td>
								<select name='selectYearMonthStart'><option value="">請選擇</option></select>
							</td><td>
								～
							</td><td>
								<select name='selectYearMonthEnd'><option value="">請選擇</option></select>
							</td>
						</tr>
						<tr>
							<td>
								行業別：
							</td><td colspan='3'>
<!-- 								<input type="text" name="selectSearchIndustry" placeholder="&nbsp;請輸入行業別"> -->
								<select name="selectSearchIndustry" class="Xunrestricted"><option value="">請選擇</option></select>
							</td>
						</tr>
						<tr>
							<td>
								縣市：
							</td>
							<td colspan='3'>
								<select name="selectSearchCity"><option value="">請選擇</option></select>
							</td>
						</tr>
					</table>
				</div>
				<div id="tabs-3">
					<table class='formatted-display' id='three'>
						<tr>
							<td colspan='4'><input type="hidden" name="selectSearchCity" value="unrestricted"></td>
							<td rowspan='4'>
								<table>
									<tr>
										<td>
											<a class='btn btn-darkblue btn-search' >搜尋</a>
										</td>
									</tr>
									<tr>
										<td>
											<a class='btn btn-darkblue btn-reset'>重設</a>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								發票年月：
							</td><td>
								<select name='selectYearMonthStart' class="Xunrestricted"><option value="">請選擇</option></select>
							</td><td>
								～
							</td><td>
								<select name='selectYearMonthEnd' class="Xunrestricted"><option value="">請選擇</option></select>
							</td>
						</tr>
						<tr>
							<td>
								行業別：
							</td><td colspan='3'>
<!-- 								<input type="text" name="selectSearchIndustry" placeholder="&nbsp;請輸入行業別"> -->
								<select name="selectSearchIndustry"><option value="">請選擇</option></select>
							</td>
						</tr>
					</table>
				</div>
				<div id="tabs-4">
					<table class='formatted-display' id='four'>
						<tr>
							<td colspan='4'></td>
							<td rowspan='4'>
								<table>
									<tr>
										<td>
											<a class='btn btn-darkblue btn-search' >搜尋</a>
										</td>
									</tr>
									<tr>
										<td>
											<a class='btn btn-darkblue btn-reset'>重設</a>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								發票年月：
							</td><td>
								<select name='selectYearMonthStart'><option value="">請選擇</option></select>
							</td><td>
								～
							</td><td>
								<select name='selectYearMonthEnd'><option value="">請選擇</option></select>
							</td>
						</tr>
						<tr>
							<td>
								行業別：
							</td><td colspan='3'>
<!-- 								<input type="text" name="selectSearchIndustry" placeholder="&nbsp;請輸入行業別"> -->
								<select name="selectSearchIndustry"><option value="">請選擇</option></select>
							</td>
						</tr>
						<tr>
							<td>
								縣市：
							</td>
							<td colspan='3'>
								<select name="selectSearchCity"><option value="">請選擇</option></select>
							</td>
						</tr>
					</table>
				</div>
				
				
<!-- ################# -->
<!-- 2017/07/18新增 5,6 -->
<!-- ################# -->
				<div id="tabs-5">
					<table class='formatted-display' id='five'>
						<tr>
							<td colspan='2'><input type="hidden" name="selectSearchCity" value="unrestricted"></td>
							<td rowspan='3'>
								<table>
									<tr>
										<td>
											<a class='btn btn-darkblue btn-search' >搜尋</a>
										</td>
									</tr>
									<tr>
										<td>
											<a class='btn btn-darkblue btn-reset'>重設</a>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								發票月份：
							</td><td>
								<select name='selectYearMonthStart' style="width: 364px;"><option value="">請選擇</option></select>
							</td>
						</tr>
						<tr>
							<td>
								行業別：
							</td><td>
<!-- 								<input type="text" name="selectSearchIndustry" placeholder="&nbsp;請輸入行業別"> -->
								<select name="selectSearchIndustry" class="Xunrestricted"><option value="">請選擇</option></select>
							</td>
						</tr>
					</table>
				</div>
				<div id="tabs-6">
					<table class='formatted-display' id='six'>
						<tr>
							<td colspan='2'><input type="hidden" name="selectSearchIndustry" value="unrestricted"></td>
							<td rowspan='3'>
								<table>
									<tr>
										<td>
											<a class='btn btn-darkblue btn-search' >搜尋</a>
										</td>
									</tr>
									<tr>
										<td>
											<a class='btn btn-darkblue btn-reset'>重設</a>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								發票月份：
							</td><td style="width: 364px;">
								<select name='selectYearMonthStart'><option value="">請選擇</option></select>
							</td>
						</tr>
						<tr>
							<td>
								縣市：
							</td>
							<td>
								<select name="selectSearchCity"><option value="">請選擇</option></select>
							</td>
						</tr>
					</table>
				</div>
<!-- ################# -->
<!-- 2017/08/08新增 7,8 -->
<!-- ################# -->
				
				<div id="tabs-7">
					<table class='formatted-display' id='seven'>
						<tr>
							<td colspan='4'></td>
							<td rowspan='4'>
								<table>
									<tr>
										<td>
											<a class='btn btn-darkblue btn-search' >搜尋</a>
										</td>
									</tr>
									<tr>
										<td>
											<a class='btn btn-darkblue btn-reset'>重設</a>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								行業別：
							</td><td colspan='3'>
<!-- 								<input type="text" name="selectSearchIndustry" placeholder="&nbsp;請輸入行業別"> -->
								<select name="selectSearchIndustry"><option value="">請選擇</option></select>
							</td>
						</tr>
						<tr>
							<td>
								縣市：
							</td>
							<td colspan='3'>
								<select name="selectSearchCity"><option value="">請選擇</option></select>
							</td>
						</tr>
						<tr>
							<td>
								比較年份：
							</td><td style="width: 364px;">
								<select name='selectYearStart' ><option value="">請選擇</option></select>
							</td>
						</tr>
						<tr>
							<td>
								比較類型：
							</td>
							<td colspan='3'>
								<input name="compare_type" id="by_month" value="month" type="radio" checked><label for="by_month"><span class="form-label">月份比較</span></label>
								<input name="compare_type" id="by_quarter" value="quarter" type="radio"><label for="by_quarter"><span class="form-label">季度比較</span></label>
							</td>
						</tr>
					</table>
				</div>
			</div>

		</div>
	</div>
</div>

<script>

</script>
	<script> //### caller & prepare_step ###
	function setup_jquery_ui(){
		//含一個dialog 一個tab 兩種button類別的trigger
		
		$( "#tabs" ).tabs();
		$(".btn-search").click(function() {
			var corr = {
				"selectSearchCity":"縣市",
				"selectSearchIndustry":"行業別",
				"selectYearMonthStart":"發票年月起月",
				"selectYearMonthEnd":"發票年月迄月",
				"selectYearStart":"比較年份",
				"compare_type":"比較類型"
			};
			var parameter = { action : "select_invoice_county_stat" };
			var warning_msg = "";
			$(this).closest("table.formatted-display").find("select,input").each(function(i,item){
				if($(this).val()==""){
					warning_msg += (warning_msg.length!=0?",":"")+ corr[$(this).attr("name")];
				}
				if($(this).attr("type")=="radio"){
					parameter[$(this).attr("name")] = $('input:radio[name="'+$(this).attr("name")+'"]:checked').val();
				}else{
					parameter[$(this).attr("name")] = $(this).val();
				}
			});
			
			parameter["title"] = $("#tabs ul li.ui-state-active a").html();
			parameter["with-map"] = "true";
			if($(this).closest("table.formatted-display").attr("id")=="five"){
				parameter["selectYearMonthEnd"] = parameter["selectYearMonthStart"];
				parameter["by-month"] = $(this).closest("table.formatted-display").find("select[name='selectYearMonthStart'] option:selected").text();
				parameter["key-name"] = "county_name";
			}else if($(this).closest("table.formatted-display").attr("id")=="six"){
				parameter["selectYearMonthEnd"] = parameter["selectYearMonthStart"];
				parameter["by-month"] = $(this).closest("table.formatted-display").find("select[name='selectYearMonthStart'] option:selected").text();
				parameter["key-name"] = "industry_category";
			}else if($(this).closest("table.formatted-display").attr("id")=="seven"){
				parameter["selectYearMonthStart"] = parameter["selectYearStart"] + "/01/01";
				parameter["selectYearMonthEnd"] = (+parameter["selectYearStart"]+1) + "/12/31"
				parameter["by-month"] = "false";
			}else{
				parameter["by-month"] = "false";
			}
			
			if(warning_msg.length!=0){
				warning_msg = "請選擇：<br> " + warning_msg+"。";
				warningMsg("警告",warning_msg);
				return ;
			}
			
			if($(this).closest("table.formatted-display").find("select[name='selectYearMonthStart']").val()
				> $(this).closest("table.formatted-display").find("select[name='selectYearMonthEnd']").val()
				){
				warning_msg+="起月不可大於迄月";
				warningMsg("警告",warning_msg);
				return ;
			}
			var str_tmp="";
			$.each(parameter,function(key,value) {
				str_tmp += key + " : "+value+ "\n"; 
			});
			
			console.log(JSON.stringify(parameter));
			window.open("./invoiceStatisticResult.jsp?request_str="+encodeURI(b64EncodeUnicode(JSON.stringify(parameter))).replace(/\+/g,"%2B"));
			
		});
		
		$("#tabs").delegate(".btn-reset", "click", function() {
			$(this).closest("table.formatted-display").find("select").val('');
		});
		
	}
	function setup_select_options(){
		setTimeout(function(){
			genSelect("selectYearMonthStart",
				get_invoice_dimension('start_time')
			);
			genSelect("selectYearMonthEnd",
				get_invoice_dimension('end_time')
			);
			genSelect("selectSearchCity",
				get_invoice_dimension('county_name')
			);
// 			genAutocomplete("selectSearchIndustry",
			genSelect("selectSearchIndustry",
				get_invoice_dimension('industry_category')
			);
			genSelect("selectYearStart",
				get_invoice_dimension('years')
			);
			$(".unrestricted").each(function(){
				$(this).html(
					$(this).html().replace('<option value="">請選擇</option>',
					'<option value="">請選擇</option><option value="unrestricted">不限</option>')
				);
			});
		}, 100);
	}
	</script>

	<script>
	function get_invoice_dimension(dimension_name){
// 		取Invoice的Select選項
		var ret_obj=[];
		$.ajax({
			type : "POST",
			url : "invoiceStatistic.do",
			async : false,
			data : {
				action : "select_invoice_dimension",
				dimension_name : dimension_name,
			},success : function(result) {
				var json_obj = $.parseJSON(result);
				$.each(json_obj,function(i,item) {
					var ret_item={};
					if(dimension_name=='start_time'){
						ret_item["key"]=item["map"]["askDimension"]+"/01";
					}else if(dimension_name=='end_time'){
						ret_item["key"]=item["map"]["askDimension"]+"/02";
					}else{
						ret_item["key"]=item["map"]["askDimension"];
					}
					
					if(dimension_name=='start_time' || dimension_name=='end_time'){
						ret_item["value"]="&nbsp;"+item["map"]["askDimension"].replace("/","&nbsp;年&nbsp;")+"&nbsp;月";
					}else if(dimension_name=='industry_category'){
// 						ret_item["value"]=((i+1)>=10?"":"&nbsp;")+(i+1)+".&nbsp;"+item["map"]["askDimension"];
						ret_item["value"] = "&nbsp;"+item["map"]["askDimension"];
					}else if(dimension_name=='years'){
						ret_item["value"]="&nbsp;"+item["map"]["askDimension"]+"&nbsp;vs&nbsp;"+(+item["map"]["askDimension"]+1);
					}else{
						ret_item["value"]="&nbsp;"+item["map"]["askDimension"];
					}
					
					ret_obj.push(ret_item);
					
					if( dimension_name=='years' // 今年不顯示
						&& item["map"]["askDimension"]==new Date().getFullYear() ){
						ret_obj.pop();
					}
					
				});
			}
		});
		return ret_obj ; 
	}
	
	</script>

<jsp:include page="footer.jsp" flush="true"/>
