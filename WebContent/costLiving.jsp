<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="import.jsp" flush="true"/>
<script src="js/d3.v3.min.js"></script>
<script type="text/javascript" src="js/jquery-migrate-1.4.1.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/additional-methods.min.js"></script>
<script type="text/javascript" src="js/messages_zh_TW.min.js"></script>

<!--  use for Visualize -->
<link type="text/css" rel="stylesheet" href="./css/visualize.jQuery.css"/>
<script type="text/javascript" src="./js/visualize.jQuery_edit.js"></script>
		
<!--  css for d3js -->
<style>
table.form-table{
	border-collapse: separate;
	border-spacing: 0px;
	margin-right: 30px;
	font-family: "微軟正黑體", "Microsoft JhengHei", 'LiHei Pro', Arial, Helvetica, sans-serif, \5FAE\8EDF\6B63\9ED1\9AD4,\65B0\7D30\660E\9AD4;
}
table.form-table tr td:nth-child(2n+1) {
    text-align: right;
    padding-left: 4px;
    padding-bottom: 5px;
}
table.form-table tr td:nth-child(2n) {
    text-align: left;
}
input[type=text].error{
    border: 1px solid #e92500;
    background: rgb(255, 213, 204);
}
table.accessHide {
	position: absolute;
	left: -999999px;
}
select{
	min-width:240px;
}
.visualize-info{
/* 	bottom: -60px !important; */
	bottom: unset !important;
	top: 100%;
	margin-top:30px;
	margin-left: 130px;
}
/*sample alternate styling for info block on Pie Chart */
/* .visualize-pie .visualize-info { top: -150px; border: 0; right: auto; left: 10px; padding: 0; background: none; } */
/* .visualize-pie ul.visualize-title { font-weight: bold; border: 0; } */
/* .visualize-pie ul.visualize-key li { float: none; } */
</style>

<!--<div id="msgAlert"></div>-->

<jsp:include page="header.jsp" flush="true"/>
<div class="content-wrap">
	<h2 class="page-title">生活費用</h2>
	<div class="search-result-wrap">
		<div id='view' style='background:#f8f8f8;padding:20px;border: 3px solid #666;margin:20px auto;width:860px;	border-radius: 8px;box-shadow: 10px 10px 5px #999;'>
			<form id='serviceregister' style='margin:20px;'>
				<table class='form-table'>
					<tr>
						<td><p>國家：</p></td>
						<td>
							<select id="country" name="country"></select>
						</td>
					</tr>
					<tr>
						<td><p>城市：</p></td>
						<td>
							<select id="city" name="city"></select>
						</td>
					</tr>
					<tr>
						<td><p>業種：</p></td>
						<td>
							<select id="business_type" name="business_type"></select>
						</td>
					</tr>
					<tr>
						<td><p>營業類型：</p></td>
						<td>
							<select id="sub_type" name="sub_type"></select>
						</td>
					</tr>
					<tr>
						<td><p>統計值：</p></td>
						<td>
							<input id="rdo-min" name="field_name" type="radio" value="min"> <label for="rdo-min"><span class="form-label">最小值</span></label>
							<input id="rdo-avg" name="field_name" type="radio" value="avg" checked> <label for="rdo-avg"><span class="form-label">平均值</span></label>
							<input id="rdo-max" name="field_name" type="radio" value="max"> <label for="rdo-max"><span class="form-label">最大值</span></label>
						</td>
					</tr>
					<tr>
						<td><p>產品：</p></td>
						<td>
							<div id="product_data">
							</div>
						</td>
					</tr>
					<tr>
						<td><p>產生圖形選擇：</p></td>
						<td>
						</td>
					</tr>
				</table>
				
				<div id="chart" style="padding: 30px 80px 0px 80px;">
					<table id="resultTable" class="result-table">
						<th>產品名稱</th>
						<th></th>
					</table>
				</div>

				<div align='center'>
					<a id='chart_vertical_bar' class='btn btn-primary'>長條圖</a>
					<a id='chart_pie' class='btn btn-primary'>圓餅圖</a>
					<a id='chart_data' class='btn btn-primary'>數據資料</a>
<!-- 					<a id='register2' class='btn btn-primary'>長條圖</a> -->

					<a id='btn_reset' class='btn btn-darkblue'>重設</a>
				</div>
			</form>
		</div>
	</div>
</div>
<script>
	$(function(){
		$("#country").focus();
		var defaultValue = "請選擇";
		var color_array = ['#e9e744','#666699','#92d5ea','#ee8310','#8d10ee',
			'#5a3b16','#26a4ed','#f45a90','#be1e2d','#FA5858',
			'#F4FA58','#58FA82','#DA81F5','#00FF40','#FAAC58',
			'#013ADF','#DF3A01','#8181F7','#F5A9BC','#04B45F',];
		
		$.ajax({
			type : "POST",
			url : "costliving.do",
			data : {
				action : "getCountry"
			},
			success : function(result) {
				var json_obj = $.parseJSON(result);
				console.log(json_obj);
				$("#country").find('option').remove();
				$("#country").append($('<option></option>').val('').html(defaultValue));
				$.each(json_obj, function(i, item) {
					$("#country").append($('<option></option>').val(item.country).html(item.country));	
				});	
			}
		});
		
		$("#btn_reset").click(function(){
			$("#country").val('');
			$("#city").find('option').remove();
			$("#business_type").find('option').remove();
			$("#sub_type").find('option').remove();
			$("#product_data").find('input').remove();
			$("#product_data").find('label').remove();
			$("#product_data").find('br').remove();
		});
		
		$("#country").change(function(){
			
			$("#business_type").find('option').remove();
			$("#sub_type").find('option').remove();
			$("#product_data").find('input').remove();
			$("#product_data").find('label').remove();
			$("#product_data").find('br').remove();
			
			$.ajax({
				type : "POST",
				url : "costliving.do",
				data : {
					action : "getCity",
					country : $("#country").val()
				},
				success : function(result) {
					var json_obj = $.parseJSON(result);
					console.log(json_obj);
					$("#city").find('option').remove();
					$("#city").append($('<option></option>').val('').html(defaultValue));
					$.each(json_obj, function(i, item) {
						$("#city").append($('<option></option>').val(item.city).html(item.city));	
					});	
				}
			});
		});		

		$("#city").change(function(){
			
			$("#sub_type").find('option').remove();
			$("#product_data").find('input').remove();
			$("#product_data").find('label').remove();
			$("#product_data").find('br').remove();

			$.ajax({
				type : "POST",
				url : "costliving.do",
				data : {
					action : "getBusinessType",
					country : $("#country").val(),
					city : $("#city").val()
				},
				success : function(result) {
					var json_obj = $.parseJSON(result);
					console.log(json_obj);
					$("#business_type").find('option').remove();
					$("#business_type").append($('<option></option>').val('').html(defaultValue));
					$.each(json_obj, function(i, item) {
						$("#business_type").append($('<option></option>').val(item.businessType).html(item.businessType));
					});	
				}
			});
		});		

		$("#business_type").change(function(){

			$("#product_data").find('input').remove();
			$("#product_data").find('label').remove();
			$("#product_data").find('br').remove();

			$.ajax({
				type : "POST",
				url : "costliving.do",
				data : {
					action : "getSubType",
					country : $("#country").val(),
					city : $("#city").val(),
					business_type : $("#business_type").val()
				},
				success : function(result) {
					var json_obj = $.parseJSON(result);
					console.log(json_obj);
					$("#sub_type").find('option').remove();
					$("#sub_type").append($('<option></option>').val('').html(defaultValue));
					$.each(json_obj, function(i, item) {
						$("#sub_type").append($('<option></option>').val(item.subType).html(item.subType));	
					});	
				}
			});
		});		

		$("#sub_type").change(function(){

			$.ajax({
				type : "POST",
				url : "costliving.do",
				data : {
					action : "getProduct",
					country : $("#country").val(),
					city : $("#city").val(),
					business_type : $("#business_type").val(),
					sub_type : $("#sub_type").val()
				},
				success : function(result) {
					var json_obj = $.parseJSON(result);
					console.log(json_obj);
					
					
					$("#product_data").find('input').remove();
					$("#product_data").find('label').remove();
					$("#product_data").find('br').remove();
					$.each(json_obj, function(i, item) {
						$("#product_data").append('<input type="checkbox" id=' + item.productId + ' name="product" value="' + item.productId + '">' +
						'<label for="' + item.productId + '"><span class="form-label">' + item.productName + '</span></label><br/><br/>');
					});	
				}
			});
		});		

		$("#chart_vertical_bar").click(function(){
			var chartOptions = "";
				
			chartOptions = {
				'width':'700',
				'height':'300',
				'type':'bar',
				'barMargin':'1',
				'barGroupMargin':'20',
				'appendTitle':'true',
				'appendKey':'true',
				'textColors':'',
				'parseDirection':'x',
				'colors': color_array
			};
         	
			if (get_productlist() == '') {
				warningMsg("警告", "尚有資料未填寫完畢");
				return;
			}
			
			show_chart(get_productlist(), chartOptions, true);
		});
//		不確定是什麼的register1 只知道好像沒用到?
		$("#register1").click(function(){
			var chartOptions = "";
				
			chartOptions = {
				'width':'700',
				'height':'300',
				'type':'line',
				'barMargin':'1',
				'barGroupMargin':'20',
				'appendTitle':'true',
				'appendKey':'true',
				'textColors':'',
				'parseDirection':'x',
				'colors': color_array
			};
			
			if (get_productlist() == '') {
				warningMsg("警告", "尚有資料未填寫完畢");
				return;
			}
			
			show_chart(get_productlist(), chartOptions);
		});

		$("#chart_data").click(function(){
			var chartOptions = "";
				
			chartOptions = {
				'width':'700',
				'height':'300',
				'type':'area',
				'lineWeight':'4',
				'appendTitle':'true',
				'appendKey':'true',
				'textColors':'',
				'parseDirection':'x',
				'colors': color_array
			};
			
			if (get_productlist() == '') {
				warningMsg("警告", "尚有資料未填寫完畢");
				return;
			}
			
			show_chart(get_productlist(), chartOptions, false);
		});

		$("#chart_pie").click(function(){
			var chartOptions = "";
				
			chartOptions = {
				'width':'700',
				'height':'300',
				'type':'pie',
				'pieMargin':'20',
				'pieLabelPos':'outside',
				'lineWeight':'4',
				'appendTitle':'true',
				'appendKey':'true',
				'textColors':'',
				'parseDirection':'x',
				'colors': color_array
			};
			
			if (get_productlist() == '') {
				warningMsg("警告", "尚有資料未填寫完畢");
				return;
			}
			
			show_chart(get_productlist(), chartOptions, true);
		});
		
		chart = $("#chart").dialog({
			title: '結果圖表',
			draggable : false,//防止拖曳
			resizable : false,//防止縮放
			autoOpen : false,
			width : 860,
			height : 470,
			modal : true,
			open : function(){
				$("#chart").css("height", "calc(80vh)");
				$("#chart").parent().css("top", "10px");
			},
			buttons : [{
				text : "關閉",
				className: 'save',
				click : function() {
					chart.dialog("close");
				}
			}]
		});
	});
	
	function get_productlist() {
		var product_list = "";
		$('input[name="product"]').each(function() {
	         if($(this).is(":checked")) {
	        	 product_list += "'" + $(this).val() + "',";
	         }
    	});
		
		product_list = product_list.substring(0, product_list.length - 1);
		
		return product_list;
	}
	
	function show_chart(product_list, chartOptions, isChart) {
		
		if(window.scenario_record){scenario_record("生活費用","[ "+$("#country").val()+", "+$("#city").val()+", "+$("#business_type").val()+", "+$("#sub_type").val()+", "+$('input[name="field_name"]:checked').val()+", "+product_list+"]");}
		$.ajax({
			type : "POST",
			url : "costliving.do",
			data : {
				action : "getChart",
				country : $("#country").val(),
				city : $("#city").val(),
				business_type : $("#business_type").val(),
				sub_type : $("#sub_type").val(),
				field_name : $('input[name="field_name"]:checked').val(),
				product_id : product_list
			},
			success : function(result) {
				var json_obj = $.parseJSON(result);
				console.log(json_obj);
				var row_unit = "";
				chart.dialog("open");
				
				var column_name = $('label[for="rdo-' + $('input[name="field_name"]:checked').val() + '"] > span').html();
				
				$("#resultTable").html('<thead><td>產品名稱</td><th>' + column_name + '</th></thead>');
				
				$("#resultTable").append('<tbody>');
				$.each(json_obj, function(i, item) {
					row_unit=item.unit;
					$("#resultTable").append('<tr><th>' + item.productName + '</th><td style="text-align:right;">' + item.result + '</td></tr>');
				});
				$("#resultTable").append('</tbody>');
				
				if (isChart) {
					$('#resultTable').visualize();
					
					//hide table
					$('#resultTable').addClass('accessHide');
					
					$('.visualize').remove();
					$('#resultTable').visualize(chartOptions);					
				} else {
					//show table
					$('#resultTable').removeClass('accessHide');
					
					$('.visualize').remove();
				}
				
				//==============↓↓↓20170726 Ben加標題單位 置中↓↓↓===============
				//原本的頁面調整
				$("#chart")
					.css("padding-top", (
						chartOptions["barMargin"]==null && chartOptions["pieMargin"]==null
							? (($("#chart").height() - $("#resultTable").height()) / 2)
							:"80"
						) + "px")
					.css("padding-left", "80px");
				//標題
				$(".visualize-info").after(
					$("<ul/>",{
						"style":"width: 700px; height: 300px;position: absolute;left: 0;top: 0;list-style: none;"
					}).html(
						$("<li/>",{
							"class":"visualize-label-pos",
							"style":"width:100%;text-align:center;margin-top:-40px;position: absolute;"
						}).html(
							$("<span/>",{
								"class":"label",
								"style":" font-size: 28px;"
							}).html(
								(chartOptions["barMargin"]!=null?"各物價指標 比較圖":"各物價指標占比 比例圖")
							)
						)
					)
				);
				//圓餅圖單位
				$(".visualize-labels").append(
					$("<li/>",{
						"class":"visualize-label-pos",
						"style":"top: 100%;margin-top: 30px;"
					}).html(
						$("<span/>",{
							"class":"label",
							"style":"margin-left:40px; font-size: 16.25px;"
						}).html(
							"<b>單位: " + row_unit+"</b>"
						)
					)
				);
				//長條圖單位X2
				$(".visualize-labels-y").append(
					$("<li/>",{
						"class":"visualize-label-pos",
						"style":"bottom: 100%;margin-bottom: 15px;"
					}).html(
						$("<span/>",{
							"class":"label",
							"style":"bottom:0px;margin-left:-30px;font-size: 16.25px; text-align: left; left:0px; width: 240px; word-break: keep-all;"
						}).html(
							"<b><i>"+row_unit+"</i></b>"
						)
					)
				);
				$(".visualize-labels-x").append(
					$("<li/>",{
						"class":"visualize-label-pos",
						"style":"left: 100%;"
					}).html(
						$("<span/>",{
							"class":"label",
							"style":"margin-top:20px; font-size: 16.25px;width:auto;word-break: keep-all;"
						}).html(
							"<b><i>統計類型</i></b>"
						)
					)
				);
				//表格單位
				$("#resultTable").append("<caption style='text-align:left;padding-left:20px;'><b style='font-size:1em;'>單位: "+row_unit+"</b><br>　</caption>");
				//==============↑↑↑20170726 Ben加標題單位 置中↑↑↑===============
				
				
			}
		});
	}

</script>
<jsp:include page="footer.jsp" flush="true"/>
