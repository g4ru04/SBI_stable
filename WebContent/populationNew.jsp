<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="import.jsp" flush="true"/>

<link rel="stylesheet" href="css/styles_populationNew.css">

<script src="js/d3.v3.min.js"></script>
<script type="text/javascript" src="js/jquery-migrate-1.4.1.min.js"></script>
<script type="text/javascript" src="js/sbi/populationColNameCorrespond.js"></script>
	
<!-- /**************************************  以下管理者JS區塊    *********************************************/		-->
<script>

	$(function(){
		$(".btn-sp_search").click(function() {
			var search_var_year = $(this).closest("table").find("select[name='select_year']").val();
			
			var have_error = 0;
			var error_msg = "";
			error_msg += search_var_year == ''?((have_error++>0?',':'')+'年份'):'';
			error_msg += $("#selectSearchCity").val() == ''?((have_error++>0?',':'')+'縣市'):'';
			error_msg += $("#selectSearchTown").val() == ''?((have_error++>0?',':'')+'行政區'):'';
			error_msg += $("#selectSearchVillage").val() == ''?((have_error++>0?',':'')+'鄉里'):'';
			if( error_msg.length>0){
				error_msg = "請選擇: " + error_msg;
				warningMsg(error_msg);
 				return ;
			}
			
			$.ajax({
				type : "POST",
				url : "populationNew.do",
				data : {
					action : "search_district_three_levels",
					county_id : $("#selectSearchCity").val(),
					town_id : $("#selectSearchTown").val(),
					village_id : $("#selectSearchVillage").val(),
					year : search_var_year,
				},success : function(result) {
					var total_col = 8;
					var table_cursor = $("<table/>",{'class':'excel-table for-village','style':'float:left;'});
					table_cursor.append($('<tr/>').css("height","2px"));
					table_cursor.append($('<tr/>').append(
						[$('<th/>').text("分析項目"),
						$('<th/>').text("數據"),
						$('<th/>').text("分析項目"),
						$('<th/>').text("數據"),
						$('<th/>').text("分析項目"),
						$('<th/>').text("數據"),
						$('<th/>').text("分析項目"),
						$('<th/>').text("數據")
						]
					));
					table_cursor.append($('<tr/>').css("height","1px"));
					var tr_cursor = $('<tr/>');
					var td_cursor = $('<td/>');
					var amount_cursor = 0 ;
					
					var json_obj = $.parseJSON(result);

					var str ='';
					var space_gone=0;
					$.each(json_obj, function(j, jtem) {
						if(["county_id", "county_name", "town_id", "town_name", "village_id", "village_name"].indexOf(j)==-1
								&& column_name[j]!=null ){
							if(amount_cursor % total_col == 0 && amount_cursor!=0){
								table_cursor.append(tr_cursor);
								if(space_gone==1){
									table_cursor.append($('<tr/>').css("height","2px"));
									table_cursor.append($('<tr/>').css("height","2px"));
									space_gone=0;
								}
								console.log(j+" "+j.indexOf("space"));
								tr_cursor = $('<tr/>');
							}
							tr_cursor.append($('<td/>').text(column_name[j]));
							tr_cursor.append($('<td/>').html(jtem!=""?
								(["M_F_RAT","P_H_CNT","P_DEN","DEPENDENCY_RAT","A0A14_A15A65_RAT",
								  "A65UP_A15A64_RAT","A65_A0A14_RAT"].indexOf(j)!=-1?new Number(jtem).toFixed(2):jtem)
									+" "+unit_name[j]:""));
							td_cursor = $('<td/>');
							amount_cursor += 2;
							if(amount_cursor % total_col == 0 && (j.indexOf("space")!=-1 || j.indexOf("A15UP_M4_F_CNT")!=-1)){
								space_gone=1;
							}
						}
					});
					var title = search_var_year+"年度 <u style='font-size:1.2em;'>"
			    			+($("#selectSearchCity").val()==''?'':$("#selectSearchCity option:selected").text())
			    			+($("#selectSearchTown").val()==''?'':$("#selectSearchTown option:selected").text())
			    			+($("#selectSearchVillage").val()==''?'':$("#selectSearchVillage option:selected").text()) 
			    			+"</u> 人口指標";
			    	$("#data_dialog").html(		
				    	$("<div/>",{
				    		"style":"text-align:center;font-size: 20px;padding:15px 0;"
				    	}).html(title)
			    	);
			    	$("#data_dialog").append(
		    			$("<div/>",{
				    		"style":"position: relative;height:0px;width:100%;z-index:1;"
				    	}).append(
				    		$("<a/>",{
				    			"type": "button",
								"style":"position: absolute;right:-5px;top:-35px;font-size:12px;color:#888;",
								"class": "ui-button ui-corner-all ui-widget ui-dialog-titlebar-close",
								"onclick": "$(\'#data_dialog\').dialog(\'close\');"
				    	}).text("X"))
			    	);
			    	$("#data_dialog").append(
			    		$("<div/>",{
			    			"id":"dialog_tab"
			    		}).append($("<ul/>").append(
			    				$("<li/>").append(
			    					$("<a/>",{"href":"#data-tab"}).text("數據")
			    				),
			    				$("<li/>").append(
			    					$("<a/>",{"href":"#chart-tabs-1"}).text("人口數量統計")
			    				),
			    				$("<li/>").append(
			    					$("<a/>",{"href":"#chart-tabs-2"}).text("婚姻與性別人口統計")
			    				),
			    				$("<li/>").append(
			    					$("<a/>",{"href":"#chart-tabs-3"}).text("男性分齡統計")
			    				),
			    				$("<li/>").append(
			    					$("<a/>",{"href":"#chart-tabs-4"}).text("女性分齡統計")
			    				),
			    				$("<li/>").append(
			    					$("<a/>",{"href":"#chart-tabs-5"}).text("教育程度人數統計")
			    				)
			    			),
		    				$("<div/>",{"id":"data-tab"}),
		    				$("<div/>",{"id":"chart-tabs-1"}),
		    				$("<div/>",{"id":"chart-tabs-2"}),
		    				$("<div/>",{"id":"chart-tabs-3"}),
		    				$("<div/>",{"id":"chart-tabs-4"}),
		    				$("<div/>",{"id":"chart-tabs-5"})
			    		),$("<br/>")
			    	);
					
// 					畫圖表 丟參數 selector dataset datatext title
					draw_barchart_of_one("#chart-tabs-1",
							[json_obj.TOTAL_CNT, json_obj.TOTAL_M_CNT, json_obj.TOTAL_F_CNT],
							[column_name["TOTAL_CNT"],column_name["TOTAL_M_CNT"],column_name["TOTAL_F_CNT"]],
							"人口數量統計圖");
					draw_barchart_of_one("#chart-tabs-2",
							[json_obj.A15UP_M1_M_CNT,json_obj.A15UP_M1_F_CNT,json_obj.A15UP_M2_M_CNT,json_obj.A15UP_M2_F_CNT,json_obj.A15UP_M3_M_CNT,json_obj.A15UP_M3_F_CNT,json_obj.A15UP_M4_M_CNT,json_obj.A15UP_M4_F_CNT],
							[column_name["A15UP_M1_M_CNT"],column_name["A15UP_M1_F_CNT"],column_name["A15UP_M2_M_CNT"],column_name["A15UP_M2_F_CNT"],column_name["A15UP_M3_M_CNT"],column_name["A15UP_M3_F_CNT"],column_name["A15UP_M4_M_CNT"],column_name["A15UP_M4_F_CNT"]],
							"婚姻與性別人口統計圖");
					draw_barchart_of_one("#chart-tabs-3",
							[json_obj.A0A4_M_CNT,json_obj.A5A9_M_CNT,json_obj.A10A14_M_CNT,json_obj.A15A19_M_CNT,json_obj.A20A24_M_CNT,json_obj.A25A29_M_CNT,json_obj.A30A34_M_CNT,json_obj.A35A39_M_CNT,json_obj.A40A44_M_CNT,json_obj.A45A49_M_CNT,json_obj.A50A54_M_CNT,json_obj.A55A59_M_CNT,json_obj.A60A64_M_CNT,json_obj.A65A69_M_CNT,json_obj.A70A74_M_CNT,json_obj.A75A79_M_CNT,json_obj.A80A84_M_CNT,json_obj.A85A89_M_CNT,json_obj.A90A94_M_CNT,json_obj.A95A99_M_CNT,json_obj.A100UP_5_M_CNT],
							[column_name["A0A4_M_CNT"],column_name["A5A9_M_CNT"],column_name["A10A14_M_CNT"],column_name["A15A19_M_CNT"],column_name["A20A24_M_CNT"],column_name["A25A29_M_CNT"],column_name["A30A34_M_CNT"],column_name["A35A39_M_CNT"],column_name["A40A44_M_CNT"],column_name["A45A49_M_CNT"],column_name["A50A54_M_CNT"],column_name["A55A59_M_CNT"],column_name["A60A64_M_CNT"],column_name["A65A69_M_CNT"],column_name["A70A74_M_CNT"],column_name["A75A79_M_CNT"],column_name["A80A84_M_CNT"],column_name["A85A89_M_CNT"],column_name["A90A94_M_CNT"],column_name["A95A99_M_CNT"],column_name["A100UP_5_M_CNT"]],
							"男性分齡統計圖");
					draw_barchart_of_one("#chart-tabs-4",
							[json_obj.A0A4_F_CNT,json_obj.A5A9_F_CNT,json_obj.A10A14_F_CNT,json_obj.A15A19_F_CNT,json_obj.A20A24_F_CNT,json_obj.A25A29_F_CNT,json_obj.A30A34_F_CNT,json_obj.A35A39_F_CNT,json_obj.A40A44_F_CNT,json_obj.A45A49_F_CNT,json_obj.A50A54_F_CNT,json_obj.A55A59_F_CNT,json_obj.A60A64_F_CNT,json_obj.A65A69_F_CNT,json_obj.A70A74_F_CNT,json_obj.A75A79_F_CNT,json_obj.A80A84_F_CNT,json_obj.A85A89_F_CNT,json_obj.A90A94_F_CNT,json_obj.A95A99_F_CNT,json_obj.A100UP_5_F_CNT],
							[column_name["A0A4_F_CNT"],column_name["A5A9_F_CNT"],column_name["A10A14_F_CNT"],column_name["A15A19_F_CNT"],column_name["A20A24_F_CNT"],column_name["A25A29_F_CNT"],column_name["A30A34_F_CNT"],column_name["A35A39_F_CNT"],column_name["A40A44_F_CNT"],column_name["A45A49_F_CNT"],column_name["A50A54_F_CNT"],column_name["A55A59_F_CNT"],column_name["A60A64_F_CNT"],column_name["A65A69_F_CNT"],column_name["A70A74_F_CNT"],column_name["A75A79_F_CNT"],column_name["A80A84_F_CNT"],column_name["A85A89_F_CNT"],column_name["A90A94_F_CNT"],column_name["A95A99_F_CNT"],column_name["A100UP_5_F_CNT"]],
							"女性分齡統計圖");
					draw_barchart_of_one("#chart-tabs-5",
							[json_obj.no_edu,json_obj.self_edu,json_obj.ele_edu,json_obj.jun_edu,json_obj.sen_edu,json_obj.col_edu,json_obj.uni_edu,json_obj.mas_edu,json_obj.phd_edu],
							[column_name["no_edu"],column_name["self_edu"],column_name["ele_edu"],column_name["jun_edu"],column_name["sen_edu"],column_name["col_edu"],column_name["uni_edu"],column_name["mas_edu"],column_name["phd_edu"]],
							"教育程度人數統計圖");
					$("#data-tab").html(table_cursor);
				
					$("#dialog_tab").tabs();
					$('div[aria-describedby="data_dialog"] div.ui-dialog-titlebar').hide();
					$("#data_dialog").dialog("open");
				}
			});
			
		});
		
		$(".tab_container").delegate(".btn-search", "click", function() {
			
			var search_var_table = "search_t_" + $(this).closest("table").attr("dbtable");
			var search_var_year = $(this).closest("table").find("select[name='select_year']").val();
			var search_var_sex = $(this).closest("table").find("select.sex_select").val();
			var search_var_edu = $(this).closest("table").find("select.edu_select").val();
			var search_var_age = $(this).closest("table").find("select.age_select").val();
			var search_var_marriage = $(this).closest("table").find("select.marriage_select").val();
			
			var have_error = 0;
			var error_msg = ""
			error_msg += search_var_year == ''?((have_error++>0?',':'')+'年份'):'';
			error_msg += search_var_sex == ''?((have_error++>0?',':'')+'性別'):'';
			error_msg += search_var_edu == ''?((have_error++>0?',':'')+'學歷'):'';
			error_msg += search_var_age == ''?((have_error++>0?',':'')+'年齡層'):'';
			error_msg += search_var_marriage == ''?((have_error++>0?',':'')+'婚姻狀況'):'';
			if( error_msg.length>0){
				error_msg = "請選擇: " + error_msg;
				warningMsg(error_msg);
 				return ;
			}
			if(search_var_table == "search_t_age_edu_county"){
				if(["0_4","5_9","10_14"].indexOf(search_var_age)!=-1){
					warningMsg("選擇之年齡層尚無學歷資料");
					return;
				}
				if(["65_69","70_74","75_79","80_84","85_89","90_94","95_99","100"].indexOf(search_var_age)!=-1){
					search_var_age = "65";
				}
			}
			var title = search_var_year+"年 全國";
			title += null2str($(this).closest("table").find("select.marriage_select option:selected").text());
			title += search_var_sex != 'all'?null2str($(this).closest("table").find("select.sex_select option:selected").text()):"";
			title += null2str($(this).closest("table").find("select.edu_select option:selected").text());
			title += null2str($(this).closest("table").find("select.age_select option:selected").text());
			title += "人口數";
			$.ajax({
				type : "POST",
				url : "populationNew.do",
				data : {
					action : search_var_table,
					gender : search_var_sex,
					age : search_var_age,
					edu : search_var_edu,
					marriage : search_var_marriage,
					year : search_var_year,
				},success : function(result) {
					$(".ui-dialog-titlebar").show();
					$("#data_dialog").html(
						$("<table/>")
							.append($('<tr/>')
						        .append($('<td/>').attr({
						        	"id":"data",
						        	"rowspan": "2"
						        }))
						        .append($('<td/>').attr({
						        	'id' : 'chart'
						    	}))
						    )
						    .append($('<tr/>')
						        .append($('<td/>')
						        	.append($('<hr/>',{'class':'little_gray_hr'})
						        		,$('<a/>',{
						        			'class':'ui-button ui-corner-all float_right',
						        			'onclick':'$(\"#data_dialog\").dialog(\"close\");'
						        		}).text("確定")
						        	)
						        )
						    )
					);

					var json_obj = $.parseJSON(result);
					var json_obj_chart =[];
					if(json_obj.length==0){
						warningMsg("此查詢條件無資料!!!");
					};
					
					json_obj.sort(function(a,b){
						return ((+a.county_data > +b.county_data)?-1:((+a.county_data < +b.county_data)?1:0));
					});
					var collect=0;
					for(var i=0,j=0;i<json_obj.length;i++){
						if(+json_obj[i].county_data_percent.replace("%","")>2.2){
							json_obj_chart[j]=json_obj[i];
							j++;
						}else{
							if(collect==0){
								j-=1;
								collect=1;
							}
							json_obj_chart[j].county_name+=", ";
							json_obj_chart[j].county_name+=json_obj[i].county_name;
							json_obj_chart[j].county_data=+(json_obj_chart[j].county_data)+(+json_obj[i].county_data);
							json_obj_chart[j].county_data_percent=
								(+json_obj_chart[j].county_data_percent.replace("%",""))
								+(+json_obj[i].county_data_percent.replace("%",""))+"%";
						}
					}
					json_obj_chart[j].county_name+=" 等";
					json_obj_chart[j].county_data_percent=new Number(json_obj_chart[j].county_data_percent.replace("%","")).toFixed(2)+"%";
					draw_piechart_of_four(json_obj_chart);
					
					var json_obj = $.parseJSON(result);
					json_obj.sort(function(a,b){
						return ((+a.county_data > +b.county_data)?-1:((+a.county_data < +b.county_data)?1:0));
					});
					
					var result_html = "";
					result_html+="<table class='excel-table' style='float:left;'>"
					+"<tr><td colspan='4'>"+title+"</td></tr>"
					result_html+="<tr><th>排名</th><th>縣市</th><th>人數</th><th>百分比</th></tr>";
					$.each(json_obj,function(i, item) {
						result_html+="<tr><td>"+(i+1)+"</td><td>"+item.county_name+"</td><td>"+item.county_data+"人</td><td>"+item.county_data_percent+"</td></tr>";
					});
					result_html+="</table><br>　";
					$("#data").html(result_html);
					$("#data_dialog").dialog("open");
				}
			});
		});
		$("#primary-tab .tab_container").delegate(".btn-reset", "click", function() {
			$(this).closest("table").find("select").val('');
		});
		setup_dialog();
		setup_options_of_SEX_EDU_AGE();
		setup_year_of_four_search();
		setup_citytownvillage_search();	
		
		
	    var _showTab = 0;
		var $defaultLi = $('ul.tabs li').eq(_showTab).addClass('active');
		
		$($defaultLi.find('a').attr('href')).siblings().hide();
		$('#primary-tab ul.tabs li').click(function() {
			
			var $this = $(this),
				_clickTab = $this.find('a').attr('href');
			$this.addClass('active').siblings('.active').removeClass('active');
			$(_clickTab).stop(false, true).fadeIn().siblings().hide();

			return false;
		}).find('a').focus(function(){
			this.blur();
		});
		
		$("body").keydown(function (event) {
			if (event.which == 13) {
				$("#send").trigger("click");
			}
		}); 
	});
</script>
<!-- /**************************************  以上使用者JS區塊    *********************************************/	-->

<jsp:include page="header.jsp" flush="true"/>
<div class="content-wrap">
<h2 class="page-title">台灣人口社會經濟資料</h2>
	<div class="search-result-wrap">
		<div id='primary-tab' style='margin:20px;width:920px;'>
			<h4>搜尋條件</h4>
			
			<ul class="tabs">
				<li><a href="#tab1">縣巿人口社經資料查詢</a></li>
				<li><a href="#tab2">人口結構分析查詢<br>-教育程度-</a></li>
				<li><a href="#tab3">人口結構分析查詢<br>-年齡層-</a></li>
				<li><a href="#tab4">人口結構分析查詢<br>-各年齡及學歷-</a></li>
				<li><a href="#tab5">人口結構分析查詢<br>-婚姻狀況-</a></li>
			</ul>
			<div class="tab_container">
				<div id="tab1" class="tab_content">
					<table class='ben-table' id='village_table'>
						<tr>
							<td>
								年份 (民國)：
							</td><td>
								<select id='selectSearchYear' name='select_year'>
									<option value="">請選擇</option>
									<option value="101">101</option>
									<option value="102">102</option>
									<option value="103">103</option>
									<option value="104">104</option>
								</select>
							</td>
							<td rowspan='4'>
								<a class='btn btn-darkblue btn-sp_search' >搜尋</a>
								<a class='btn btn-darkblue btn-reset'>重設</a>
							</td>
						</tr>
						<tr>
							<td>
								縣市：
							</td>
							<td>
								<select id="selectSearchCity"><option value="">請選擇縣市</option></select>
							</td>
						</tr>
						<tr>
							<td>
								行政區：</td><td><select id="selectSearchTown"><option value="">請先選擇縣市</option></select>
							</td>
						</tr>
						<tr>
							<td>
								鄉里：</td><td><select id="selectSearchVillage"><option value="">請先選擇縣市/行政區</option></select>
							</td>
						</tr>
					</table>
				</div>
				<div id="tab2" class="tab_content">
					<table id='sex_edu_table' class='ben-table' dbtable='edu_county'>
						<tr>
							<td>
								年份 (民國)：
							</td><td>
								<select name='select_year'><option value="">請選擇</option></select>
							</td>
							<td rowspan='3'>
								<a class='btn btn-darkblue btn-search'>搜尋</a>
								<a class='btn btn-darkblue btn-reset'>重設</a>
							</td>
						</tr>
						<tr>
							<td>
								性別：
							</td>
							<td>
								<select class='sex_select'><option value="">請選擇</option></select>
							</td>
						</tr>
						<tr>
							<td>
								學歷：
							</td>
							<td>
								<select class='edu_select'><option value="">請選擇</option></select>
							</td>
						</tr>
					</table>
				</div>
				<div id="tab3" class="tab_content">
					<table id='sex_age_table' class='ben-table' dbtable='age_county'>
						<tr>
							<td>
								年份 (民國)：
							</td><td>
								<select name='select_year'></select>
							</td>
							<td rowspan='3'>
								<a class='btn btn-darkblue btn-search'>搜尋</a>
								<a class='btn btn-darkblue btn-reset'>重設</a>
							</td>
						</tr>
						<tr>
							<td>
								性別：
							</td>
							<td>
								<select class='sex_select'></select>
							</td>
						</tr>
						<tr>
							<td>
								年齡層：
							</td>
							<td>
								<select  class='age_select'></select>
							</td>
						</tr>
					</table>
				</div>
				<div id="tab4" class="tab_content" >
					<table id='sex_age_edu_table' class='ben-table' dbtable='age_edu_county'>
						<tr>
							<td>
								年份 (民國)：
							</td><td>
								<select name='select_year'></select>
							</td>
							<td rowspan='4'>
								<a class='btn btn-darkblue btn-search'>搜尋</a>
								<a class='btn btn-darkblue btn-reset'>重設</a>
							</td>
						</tr>
						<tr>
							<td>
								性別：
							</td>
							<td>
								<select class='sex_select'></select>
							</td>
						</tr>
						<tr>
							<td>
								年齡層：
							</td>
							<td>
								<select class='age_select'></select>
							</td>
						</tr>
						<tr>
							<td>
								學歷：
							</td>
							<td>
								<select class='edu_select'></select>
							</td>
						</tr>
					</table>
				</div>
				<div id="tab5" class="tab_content">
					<table id='sex_marriage_table' class='ben-table' dbtable='marriage_county'>
						<tr>
							<td>
								年份 (民國)：
							</td><td>
								<select name='select_year'></select>
							</td>
							<td rowspan='3'>
								<a class='btn btn-darkblue btn-search'>搜尋</a>
								<a class='btn btn-darkblue btn-reset'>重設</a>
							</td>
						</tr>
						<tr>
							<td>
								性別：
							</td>
							<td>
								<select class='sex_select'></select>
							</td>
						</tr>
						<tr>
							<td>
								婚姻狀況：
							</td>
							<td>
								<select name="searchMarriage" class='marriage_select' ></select>
							</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>

<div id='data_dialog'></div>
<div class='tooltip'></div>
<script>

function setup_dialog(){
	$("#data_dialog").dialog({
		title : "查詢結果",
		draggable : true, resizable : false, autoOpen : false,
		height : "auto", width : "auto", modal : true,
	});
}


function setup_options_of_SEX_EDU_AGE(){
	$(".sex_select").html([
		$("<option/>", {"value": ""}).text("請選擇"),
		$("<option/>", {"value": "m"}).text("男性"),
		$("<option/>", {"value": "f"}).text("女性"),
		$("<option/>", {"value": "all"}).text("不限")
	]);
	$(".edu_select").html([
		$("<option/>", {"value": ""}).text("請選擇"),
		$("<option/>", {"value": "phd"}).text("博士"),
		$("<option/>", {"value": "master"}).text("碩士"),
		$("<option/>", {"value": "university"}).text("大學"),
		$("<option/>", {"value": "college"}).text("專科"),
		$("<option/>", {"value": "senior"}).text("高中"),
		$("<option/>", {"value": "junior"}).text("國中"),
		$("<option/>", {"value": "primary"}).text("國小"),
		$("<option/>", {"value": "self"}).text("自學"),
		$("<option/>", {"value": "no"}).text("不識字")
	]);
	$(".age_select").html([
		$("<option/>", {"value": ""}).text("請選擇"),
		$("<option/>", {"value": "0_4"}).text("0~4歲"),
		$("<option/>", {"value": "5_9"}).text("5~9歲"),
		$("<option/>", {"value": "10_14"}).text("10~14歲"),
		$("<option/>", {"value": "15_19"}).text("15~19歲"),
		$("<option/>", {"value": "20_24"}).text("20~24歲"),
		$("<option/>", {"value": "25_29"}).text("25~29歲"),
		$("<option/>", {"value": "30_34"}).text("30~34歲"),
		$("<option/>", {"value": "35_39"}).text("35~39歲"),
		$("<option/>", {"value": "40_44"}).text("40~44歲"),
		$("<option/>", {"value": "45_49"}).text("45~49歲"),
		$("<option/>", {"value": "50_54"}).text("50~54歲"),
		$("<option/>", {"value": "55_59"}).text("55~59歲"),
		$("<option/>", {"value": "60_64"}).text("60~64歲"),
		$("<option/>", {"value": "65_69"}).text("65~69歲"),
		$("<option/>", {"value": "70_74"}).text("70~74歲"),
		$("<option/>", {"value": "75_79"}).text("75~79歲"),
		$("<option/>", {"value": "80_84"}).text("80~84歲"),
		$("<option/>", {"value": "85_89"}).text("85~89歲"),
		$("<option/>", {"value": "90_94"}).text("90~94歲"),
		$("<option/>", {"value": "95_99"}).text("95~99歲"),
		$("<option/>", {"value": "100"}).text("100歲以上")
	]);
	$(".marriage_select").html([
		$("<option/>", {"value": ""}).text("請選擇"),
		$("<option/>", {"value": "unmarried"}).text("未婚"),
		$("<option/>", {"value": "married"}).text("已婚"),
		$("<option/>", {"value": "divorce"}).text("離婚"),
		$("<option/>", {"value": "widowed"}).text("喪偶")
	]);
	
}


function setup_year_of_four_search(){
	
	$.ajax({
		type : "POST",
		url : "populationNew.do",
		data : {
			action : "select_village_years",
		},success : function(result) {
			
			var json_obj = $.parseJSON(result);
			$("#village_table select[name='select_year']").html(
					$("<option/>", {
						"value": ""
					}).text("請選擇")
			);
			$.each(json_obj,function(i, item) {
				$("#village_table select[name='select_year']").append(
						$("<option/>", {
							"value": item.request_data_info_time
						}).text(item.request_data_info_time)
				);
			});
		}
	});
	
	
	$.ajax({
		type : "POST",
		url : "populationNew.do",
		data : {
			action : "select_age_years",
		},success : function(result) {
			
			var json_obj = $.parseJSON(result);
			$("#sex_age_table select[name='select_year']").html(
					$("<option/>", {
						"value": ""
					}).text("請選擇")
			);
			$.each(json_obj,function(i, item) {
				$("#sex_age_table select[name='select_year']").append(
						$("<option/>", {
							"value": item.request_data_info_time
						}).text(item.request_data_info_time)
				);
			});
		}
	});
	$.ajax({
		type : "POST",
		url : "populationNew.do",
		data : {
			action : "select_age_edu_years",
		},success : function(result) {
			var json_obj = $.parseJSON(result);
			$("#sex_age_edu_table select[name='select_year']").html(
					$("<option/>", {
						"value": ""
					}).text("請選擇")
			);
			$.each(json_obj,function(i, item) {
				$("#sex_age_edu_table select[name='select_year']").append(
						$("<option/>", {
							"value": item.request_data_info_time
						}).text(item.request_data_info_time)
				);
			});
		}
	});
	$.ajax({
		type : "POST",
		url : "populationNew.do",
		data : {
			action : "select_edu_years",
		},success : function(result) {
			var json_obj = $.parseJSON(result);
			$("#sex_edu_table select[name='select_year']").html(
					$("<option/>", {
						"value": ""
					}).text("請選擇")
			);
			$.each(json_obj,function(i, item) {
				$("#sex_edu_table select[name='select_year']").append(
						$("<option/>", {
							"value": item.request_data_info_time
						}).text(item.request_data_info_time)
				);
			});
		}
	});
	$.ajax({
		type : "POST",
		url : "populationNew.do",
		data : {
			action : "select_marriage_years",
		},success : function(result) {
			var json_obj = $.parseJSON(result);
			$("#sex_marriage_table select[name='select_year']").html(
					$("<option/>", {
						"value": ""
					}).text("請選擇")
			);
			$.each(json_obj,function(i, item) {
				$("#sex_marriage_table select[name='select_year']").append(
						$("<option/>", {
							"value": item.request_data_info_time
						}).text(item.request_data_info_time)
				);
			});
		}
	});
}

function setup_citytownvillage_search(){
	$.ajax({
		type : "POST",
		url : "populationNew.do",
		data : {
			action : "search_county",
		},
		success : function(result) {
			var result_html="<option value=''>請選擇縣市</option>";
			var json_obj = $.parseJSON(result);
			if(json_obj.result.length>0){
				$.each(json_obj.result,function(i, item) {
					result_html+="<option value='"+json_obj.result_id[i]+"'>"+json_obj.result[i]+"</option>";
				});
			}
			$("#selectSearchCity").html(result_html);
			
		}
	});
	
	$("#selectSearchCity").change(function(){
		var county_id = $(this).val();
		$.ajax({
			type : "POST",
			url : "populationNew.do",
			data : {
				action : "search_town",
				county :county_id
			},
			success : function(result) {
				var result_html="<option value=''>請選擇行政區</option>";
				var json_obj = $.parseJSON(result);
				if(json_obj.result.length>0){
					$.each(json_obj.result,function(i, item) {
						result_html+="<option value='"+json_obj.result_id[i]+"'>"+json_obj.result[i]+"</option>";
					});
				} else {
					result_html="<option value=''>請先選擇縣市</option>";
				}
				$("#selectSearchTown").html(result_html);
				$("#selectSearchVillage").html("<option value=''>請先選擇行政區</option>");
			}
		});
	});
	
	$("#selectSearchTown").change(function(){
		var town_id = $(this).val();
		$.ajax({
			type : "POST",
			url : "populationNew.do",
			data : {
				action : "search_village",
				town : town_id
			},
			success : function(result) {
				var result_html="<option value=''>請選擇鄉里</option>";
				var json_obj = $.parseJSON(result);
				if(json_obj.result.length>0){
					$.each(json_obj.result,function(i, item) {
						result_html+="<option value='"+json_obj.result_id[i]+"'>"+json_obj.result[i]+"</option>";
					});
				} else {
					result_html="<option value=''>請先選擇行政區</option>";
				}
				$("#selectSearchVillage").html(result_html);
			}
		});
	});
	
}
function draw_piechart_of_four(json_obj_chart){

	var width = 540,
	    height = 400,
		radius = Math.min(width, height) / 2;
	var pie = d3.layout.pie()
		.sort(null)
		.value(function(d) {
			return d.county_data;
		});
	var fScale = d3.scale.category20c();
	var arc = d3.svg.arc()
		.outerRadius(radius * 0.8)
		.innerRadius(radius * 0);

	var outerArc = d3.svg.arc()
		.innerRadius(radius * 0.9)
		.outerRadius(radius * 0.9);
	
	var key = function(d){return d.data.county_name; };
	var tooltipdiv = d3.select(".tooltip");
	var svg = d3.select("#chart")
		.append("svg").attr({
			id : "pie_chart",
            style : "float:right;",
            width: width,
            height: height,
		})
		.append("g");
	svg.attr("transform", "translate(" + width * 0.5 + "," + height * 0.5 + ")");
	svg.append("g")
		.attr("class", "slices");
	svg.append("g")
		.attr("class", "labels");
	svg.append("g")
		.attr("class", "lines");

	var colorTmp ;
	var slice = svg.select(".slices").selectAll("path.slice")
	.data(pie(json_obj_chart), key);
	
	slice.enter()
		.insert("path")
		.style("fill", function(d) { return fScale(d.data.county_name); })
		.attr({
			"class":"slice"
		})
		.on("mouseover", function(d) {	
			colorTmp = d3.select(this).attr("style");
            d3.select(this).attr({"style":"fill:gold;"});
			tooltipdiv.transition()		
                .duration(200)		
                .style("opacity", .9);		
            tooltipdiv.html("<div class='little-title'>"+d.data.county_name+"</div>"+(d.data.county_name.length>7?"合計 "+d.data.county_data+" 人　共 ":"")+d.data.county_data_percent+"<br/>")	
                .style("left", (d3.event.pageX) + "px")		
                .style("top", (d3.event.pageY - 28) + "px");	
            })					
        .on("mouseout", function(d) {
        	d3.select(this).attr({"style":colorTmp});
        	tooltipdiv.transition()		
                .duration(500)		
                .style("opacity", 0);	
        });
	slice		
		.transition().duration(1000)
		.attrTween("d", function(d) {
			this._current = this._current || d;
			var interpolate = d3.interpolate(this._current, d);
			this._current = interpolate(0);
			return function(t) {
				return arc(interpolate(t));
			};
		})

	slice.exit()
		.remove();

	/* ------- TEXT LABELS -------*/
	var text = svg.select(".labels").selectAll("text")
		.data(pie(json_obj_chart), key);

	text.enter()
		.append("text")
		.attr("dy", ".35em")
		.text(function(d) {
			if(d.data.county_name.length>7){
				return "其他";
			}
			return d.data.county_name;
		});
	
	function midAngle(d){
		return d.startAngle + (d.endAngle - d.startAngle)/2;
	}

	text.transition().duration(1000)
		.attrTween("transform", function(d) {
			this._current = this._current || d;
			var interpolate = d3.interpolate(this._current, d);
			this._current = interpolate(0);
			return function(t) {
				var d2 = interpolate(t);
				var pos = outerArc.centroid(d2);
				pos[0] = radius * (midAngle(d2) < Math.PI ? 1 : -1);
				return "translate("+ pos +")";
			};
		})
		.styleTween("text-anchor", function(d){
			this._current = this._current || d;
			var interpolate = d3.interpolate(this._current, d);
			this._current = interpolate(0);
			return function(t) {
				var d2 = interpolate(t);
				return midAngle(d2) < Math.PI ? "start":"end";
			};
		});

	text.exit()
		.remove();

	/* ------- SLICE TO TEXT POLYLINES -------*/
	var polyline = svg.select(".lines").selectAll("polyline")
		.data(pie(json_obj_chart), key);
	
	polyline.enter()
		.append("polyline");

	polyline.transition().duration(1000)
		.attrTween("points", function(d){
			this._current = this._current || d;
			var interpolate = d3.interpolate(this._current, d);
			this._current = interpolate(0);
			return function(t) {
				var d2 = interpolate(t);
				var pos = outerArc.centroid(d2);
				pos[0] = radius * 0.95 * (midAngle(d2) < Math.PI ? 1 : -1);
				return [arc.centroid(d2), outerArc.centroid(d2), pos];
			};			
		});
	
	polyline.exit()
		.remove();
	
}

function draw_barchart_of_one(selector,dataset,datatext,chart_title){
		if($(selector).length==0){
			return;
		}
		$(selector).html('');
		var width = 1000;
		var height = 400;
		var svg = d3.select(selector)
					.append("svg")
					.attr("width", width+40)
					.attr("height", height);
		var padding = {left:170, right:10, top:80, bottom:50};
// 		資料範例
// 		var dataset = [65, 20, 30, 40, 33, 24, 12, 5,8,60];
// 		var datatext = ["六五", "二十", "三十", "四十", "三十三", "阿十四", "100歲&nbsp;", "100歲以上男性人口數","100歲以上男性人口數　","陸食"];
// 		var chart_title = "圖表標題";
		var rectPadding = dataset.length<5?28:(dataset.length>10?3:8);
		//比例尺*2
		var xScale = d3.scale.linear()
					.domain([0,d3.max(dataset,function(d) { return +d; })*1.05])
					.range([0, width - padding.left - padding.right ]);
		var yScale = d3.scale.ordinal()
					.domain(d3.range(dataset.length))
					.rangeRoundBands([ height  - padding.top - padding.bottom ,0]);
		var fScale = d3.scale.category20c();
		var fScale2 = d3.scale.category10();
		//定義軸
		var xAxis = d3.svg.axis()
					.scale(xScale)
					.orient("bottom");
		var yAxis = d3.svg.axis()
					.scale(yScale)
					.orient("left")
					.tickFormat(function(d,i){return datatext[i];});
		//網格
		var axisXGrid = d3.svg.axis()
				      .scale(xScale)
				      .orient("bottom")
				      .ticks(10)
				      .tickFormat("")
				      .tickSize(-height+padding.top+40,0);

		var XGrid = svg.append('g')
				     .call(axisXGrid)
				     .attr({
				      'fill':'none',
				      'stroke':'rgba(0,0,0,.1)',
				      'transform':'translate('+padding.left +','+(height-padding.bottom)+')' 
				     });
		
		//畫框框
		var colorTmp;
		var tooltipdiv = d3.select(".tooltip");
		var rects = svg.selectAll(".MyRect")
					.data(dataset)
					.enter()
					.append("rect")
					.attr("class","MyRect")
					.attr("transform","translate(" + padding.left + "," + padding.top + ")")
					.attr("x", function(d,i){
						return 0;
					} )
					.attr("y",function(d,i){
						return yScale(i) + rectPadding/2;
					})
					.attr("width", function(d){
						return xScale(d) ;
					})
					.attr("height",yScale.rangeBand() - rectPadding)
					.style("fill", function(d,i) { 
						if(selector == '#chart-tabs-1'){
							
							return fScale2(datatext[i]);
						}
						if(selector == '#chart-tabs-3'){
							return '#4d79ff';
						}
						if(selector == '#chart-tabs-4'){
							return '#ff471a';
						}
						return fScale(datatext[i]); 
					})
					.on("mouseover", function(d,i) {	
						colorTmp = d3.select(this).attr("style");
			            d3.select(this).attr({"style":"fill:gold;"});
						tooltipdiv.transition()		
			                .duration(200)		
			                .style("opacity", .9);		
			            tooltipdiv.html("<div class='little-title'>"+datatext[i]+"："+d+"人</div>")	
			                .style("left", (d3.event.pageX) + "px")		
			                .style("top", (d3.event.pageY - 28) + "px");	
			            })					
			        .on("mouseout", function(d) {
			        	d3.select(this).attr({"style":colorTmp});
			        	tooltipdiv.transition()		
			                .duration(500)		
			                .style("opacity", 0);	
			        });
		//x,y軸
		svg.append("g")
					.attr("class","axis")
					.attr("transform","translate(" + padding.left + "," + (height - padding.bottom) + ")")
					.call(xAxis); 
		svg.append("g")
					.attr("class","axis")
					.attr("transform","translate(" + padding.left + "," + padding.top + ")")
					.call(yAxis);
		svg.append("text")
				    .attr("x", width * 0.5 + (padding.left-padding.right) * 0.3 )
				    .attr("y", 40 )
				    .attr('fill','#555')
				    .style({
				    	"text-anchor" : "middle",
				    	"font-size" : "28px",
				    	"font-weight" : "700"
				    })
				    .text(chart_title);
		svg.append("text")
		    .attr("x", width  )
		    .attr("y",  height-10 )
		    .attr('fill','#333')
		    .style({
		    	"text-anchor" : "end",
		    	"color" : "28px"
		    })
		    .text("數量(單位：人)");
}
</script>
<jsp:include page="footer.jsp" flush="true"/>