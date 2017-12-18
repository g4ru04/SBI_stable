/**
 * 存放共用的 function
 * 20170821 Ben
 * 
 * 一覽:
 * drawExcelTable
 * drawBarChart
 * 
 * 使用範例一覽
 * var data = $.parseJSON('[{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"0","flow_sum":"293","title":"<table><tbody><tr><td colspan=\"2\">快官交流道</td></tr><tr><td>0時車流：</td><td>293&nbsp;輛</td></tr></tbody></table>"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"1","flow_sum":"196"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"2","flow_sum":"145"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"3","flow_sum":"148"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"4","flow_sum":"156"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"5","flow_sum":"293"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"6","flow_sum":"834"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"7","flow_sum":"2354"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"8","flow_sum":"2496"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"9","flow_sum":"2377"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"10","flow_sum":"1979"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"11","flow_sum":"2377"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"12","flow_sum":"2588"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"13","flow_sum":"2408"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"14","flow_sum":"2741"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"15","flow_sum":"2924"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"16","flow_sum":"3249"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"17","flow_sum":"3600"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"18","flow_sum":"3514"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"19","flow_sum":"3014"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"20","flow_sum":"2384"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"21","flow_sum":"2031"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"22","flow_sum":"1560"},{"p_interchange_name":"快官交流道","the_day":"2017-08-11","flow_hour":"23","flow_sum":"1000"}]');
 * var excelTable_opt = $.parseJSON('{"elementID":"#data_table","heads":["","時間","車流",""],"datas":["","flow_hour","flow_sum",""],"special_treatment":[null,null,null],"with-class":[null,null,"numberStr"]}');
 * var barChart_opt = $.parseJSON('{"elementID":"#data_chart","svg_width":"400","svg_height":"200","svg_margin":{"top":10,"right":10,"bottom":20,"left":10},"bar_width":"10","title_name":"","sub_title_name":"","yAxis_name":null,"xAxis_name":"時間","yAxis_index":"flow_sum","xAxis_index":"flow_hour","xScale_construct":"d3.scale.linear().domain([-1,25]).range([0, width - margin.left - margin.right]);","yScale_construct":"d3.scale.linear().domain([0,d3.max(data, function(d) { return +d[chartConfig[\"yAxis_index\"]]*1.1;})]).range([height - margin.top - margin.bottom,0])","fScale_construct":"fScale = function (str){color_cache = ['#7BAAF7','#5b9cd6','#ffce54','#4b6ca6','#de7310','#666699','#92d5ea','#5a3b16','#26a4ed','#f45a90'];if(!window.str_cache){window.str_cache=[];}if(str_cache.indexOf(str)==-1){str_cache.push(str);}return color_cache[str_cache.indexOf(str)];}","xAxis_construct":"d3.svg.axis().scale(xScale).orient(\"bottom\").tickValues([0,6,12,18,24]).tickFormat(function(d){return d+\"時\";});","yAxis_construct":"d3.svg.axis().scale(yScale).orient(\"left\").tickFormat(function(d){return d;});"}');
 * drawExcelTable(data,excelTable_opt);
 * drawBarChart(data,barChart_opt);
 * 
 * setting_weAreAllTogether();
 * weAreAllTogether.attach("桃園市",function(){alert('123');});
 * weAreAllTogether.touch("桃園市");
 * 
 * tooltip_sp_locattion因為IE顯示不順暢 延宕
 * 
 */

document.write('<link rel="stylesheet" href="css/styles_commonMethod.css"/>');

function drawExcelTable(data,assignChartConfig){
	console.log("call CommonMethod-drawExcelTable");
// 	chartConfig = {
// 		"elementID":"#data_table",
// 		"heads":["","時間","車流",""],
// 		"datas":["","flow_hour","flow_sum",""],
// 		"special_treatment":[,function(d){return d+"時";},function(d){return money_format(d)+"人";},],
// 		"with-class":[,,"numberStr",],
// 	}
	if(assignChartConfig==null){
		assignChartConfig={};
	}
	chartConfig={};
	
	chartConfig["elementID"] = assignChartConfig["elementID"] || "#data_table";
	chartConfig["heads"] = assignChartConfig["heads"] || ["","","","","","","","","","",""];
	chartConfig["datas"] = assignChartConfig["datas"] || ["","","","","","","","","","",""];
	chartConfig["special_treatment"] = assignChartConfig["special_treatment"] || ["","","","","","","","","","",""];
	chartConfig["with_class"] = assignChartConfig["with_class"] || ["","","","","","","","","","",""];
	
	$.each(data, function(i, item) {
		data[i][""]="";
	});
	
	var table_cursor = $("<table/>",{'class':'excel-table'});
	var tr_cursor = $('<tr/>');
	for( var text in chartConfig["heads"]){
		tr_cursor.append(
			$("<th/>",{"text":chartConfig["heads"][text]})
		);
	}
	table_cursor.append(tr_cursor);
	
	var doing_something = function(i,d){
		var ret = d ;
		if(typeof(chartConfig["special_treatment"][i])=="function"){
			ret = chartConfig["special_treatment"][i](d);
		}
		return ret ;
	};

	$.each(data, function(i, item) {
		var tr_cursor = $('<tr/>');
		for(var index=0; index < chartConfig["datas"].length;index++){
			tr_cursor.append(
				$("<td/>",{
					"text":doing_something(index,item[chartConfig["datas"][index]]),
					"class":chartConfig["with_class"][index]
				})
			);
		}
		table_cursor.append(tr_cursor);
	});
	$( chartConfig["elementID"]).html(table_cursor);
}

function drawBarChart(data,assignChartConfig){
	console.log(assignChartConfig);
	console.log(data);
	console.log("call CommonMethod-drawBarChart");
//	if(window.test_mode){
//		data = $.parseJSON('[{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"00","flow_sum":"293","weather_data":"","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>00時人流：</td><td>293&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"01","flow_sum":"4","weather_data":"天氣:陰","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>01時天氣：</td><td>陰</td></tr><tr><td>01時人流：</td><td>4&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"05","flow_sum":"0","weather_data":"天氣:陰","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>05時天氣：</td><td>陰</td></tr><tr><td>05時人流：</td><td>0&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"06","flow_sum":"503","weather_data":"天氣:陰","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>06時天氣：</td><td>陰</td></tr><tr><td>06時人流：</td><td>503&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"07","flow_sum":"2455","weather_data":"天氣:晴","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>07時天氣：</td><td>晴</td></tr><tr><td>07時人流：</td><td>2,455&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"08","flow_sum":"6871","weather_data":"天氣:晴","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>08時天氣：</td><td>晴</td></tr><tr><td>08時人流：</td><td>6,871&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"09","flow_sum":"3193","weather_data":"天氣:晴","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>09時天氣：</td><td>晴</td></tr><tr><td>09時人流：</td><td>3,193&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"10","flow_sum":"2710","weather_data":"氣溫:32.3°C","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>10時氣溫：</td><td>32.3°C</td></tr><tr><td>10時人流：</td><td>2,710&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"11","flow_sum":"3019","weather_data":"氣溫:33.1°C,天氣:晴","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>11時氣溫：</td><td>33.1°C</td></tr><tr><td>11時天氣：</td><td>晴</td></tr><tr><td>11時人流：</td><td>3,019&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"12","flow_sum":"3501","weather_data":"氣溫:34.2°C,天氣:晴","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>12時氣溫：</td><td>34.2°C</td></tr><tr><td>12時天氣：</td><td>晴</td></tr><tr><td>12時人流：</td><td>3,501&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"13","flow_sum":"3745","weather_data":"氣溫:35.0°C,天氣:晴","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>13時氣溫：</td><td>35.0°C</td></tr><tr><td>13時天氣：</td><td>晴</td></tr><tr><td>13時人流：</td><td>3,745&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"14","flow_sum":"3508","weather_data":"氣溫:33.6°C,天氣:晴","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>14時氣溫：</td><td>33.6°C</td></tr><tr><td>14時天氣：</td><td>晴</td></tr><tr><td>14時人流：</td><td>3,508&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"15","flow_sum":"3238","weather_data":"氣溫:33.5°C,天氣:陰","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>15時氣溫：</td><td>33.5°C</td></tr><tr><td>15時天氣：</td><td>陰</td></tr><tr><td>15時人流：</td><td>3,238&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"16","flow_sum":"3532","weather_data":"氣溫:33.3°C,天氣:陰","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>16時氣溫：</td><td>33.3°C</td></tr><tr><td>16時天氣：</td><td>陰</td></tr><tr><td>16時人流：</td><td>3,532&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"17","flow_sum":"5810","weather_data":"氣溫:32.4°C,天氣:陰","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>17時氣溫：</td><td>32.4°C</td></tr><tr><td>17時天氣：</td><td>陰</td></tr><tr><td>17時人流：</td><td>5,810&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"18","flow_sum":"6484","weather_data":"氣溫:X°C,天氣:晴","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>18時氣溫：</td><td>X°C</td></tr><tr><td>18時天氣：</td><td>晴</td></tr><tr><td>18時人流：</td><td>6,484&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"19","flow_sum":"3800","weather_data":"氣溫:30.5°C,天氣:陰","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>19時氣溫：</td><td>30.5°C</td></tr><tr><td>19時天氣：</td><td>陰</td></tr><tr><td>19時人流：</td><td>3,800&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"20","flow_sum":"2121","weather_data":"氣溫:30.4°C,天氣:陰","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>20時氣溫：</td><td>30.4°C</td></tr><tr><td>20時天氣：</td><td>陰</td></tr><tr><td>20時人流：</td><td>2,121&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"21","flow_sum":"2315","weather_data":"氣溫:30.3°C,天氣:陰","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>21時氣溫：</td><td>30.3°C</td></tr><tr><td>21時天氣：</td><td>陰</td></tr><tr><td>21時人流：</td><td>2,315&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"22","flow_sum":"1900","weather_data":"氣溫:30.3°C,天氣:陰","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>22時氣溫：</td><td>30.3°C</td></tr><tr><td>22時天氣：</td><td>陰</td></tr><tr><td>22時人流：</td><td>1,900&nbsp;人</td></tr></tbody></table>"},{"modified_name":"中正紀念堂","the_day":"2017-08-25","weekday":"4","flow_hour":"23","flow_sum":"763","weather_data":"氣溫:30.1°C,天氣:陰","title":"<table><tbody><tr><td >中正紀念堂</td></tr><tr><td>資料日期：</td><td>2017-08-25</td></tr><tr><td>23時氣溫：</td><td>30.1°C</td></tr><tr><td>23時天氣：</td><td>陰</td></tr><tr><td>23時人流：</td><td>763&nbsp;人</td></tr></tbody></table>"}]');
//		assignChartConfig = {
//		 		"elementID":"#data_chart_3345678",
//		 		"svg_width":"400",
//		 		"svg_height":"200",
//		 		"svg_margin":{top:10,right:10,bottom:20,left:10},
//		 		"bar_width": "10",
//		 		//"title_name":"交流道車流",
//		 		"title_name":"",
//		 		//"sub_title_name":"星期一",[][[[[[[[]]
//		 		"sub_title_name":"",
//		 		"xAxis_name":"時間",
//		 		"yAxis_name":null,
//		 		"xAxis_index":"flow_hour",
//		 		"yAxis_index":"flow_sum",
//		 		"xScale_construct":"d3.scale.linear().domain([-1,25]).range([0, width - margin.left - margin.right]);",
//		 		"yScale_construct":"d3.scale.linear().domain([0,d3.max(data, function(d) { return +d[chartConfig[\"yAxis_index\"]]*1.1;})]).range([height - margin.top - margin.bottom,0])",
//		 		"fScale_construct":"fScale = function (str){color_cache = ['#7BAAF7','#5b9cd6','#ffce54','#4b6ca6','#de7310','#666699','#92d5ea','#5a3b16','#26a4ed','#f45a90'];if(!window.str_cache){window.str_cache=[];}if(str_cache.indexOf(str)==-1){str_cache.push(str);}return color_cache[str_cache.indexOf(str)];}",
//		 		"xAxis_construct":"d3.svg.axis().scale(xScale).orient(\"bottom\").tickValues([0,6,12,18,24]).tickFormat(function(d){return d+\"時\";});",
//		 		"yAxis_construct":"d3.svg.axis().scale(yScale).orient(\"left\").tickFormat(function(d){return d;});"
//		 	}
//		
//	}
//	console.log(JSON.stringify(data));
//	console.log(JSON.stringify(assignChartConfig));
	
	
// 	chartConfig = {
// 		"elementID":"#data_chart",
// 		"svg_width":"400",
// 		"svg_height":"200",
// 		"svg_margin":{top:10,right:10,bottom:20,left:10},
		
// 		"bar_width": "10",
		
// 		//"title_name":"交流道車流",
// 		"title_name":"",
// 		//"sub_title_name":"星期一",
// 		"sub_title_name":"",
// 		"xAxis_name":"時間",
// 		"yAxis_name":null,
		
// 		"xAxis_index":"flow_hour",
// 		"yAxis_index":"flow_sum",
		
// 		"xScale_construct":"d3.scale.linear().domain([-1,25]).range([0, width - margin.left - margin.right]);",
// 		"yScale_construct":"d3.scale.linear().domain([0,d3.max(data, function(d) { return +d[chartConfig[\"yAxis_index\"]]*1.1;})]).range([height - margin.top - margin.bottom,0])",
// 		"fScale_construct":"fScale = function (str){color_cache = ['#7BAAF7','#5b9cd6','#ffce54','#4b6ca6','#de7310','#666699','#92d5ea','#5a3b16','#26a4ed','#f45a90'];if(!window.str_cache){window.str_cache=[];}if(str_cache.indexOf(str)==-1){str_cache.push(str);}return color_cache[str_cache.indexOf(str)];}",
		
// 		"xAxis_construct":"d3.svg.axis().scale(xScale).orient(\"bottom\").tickValues([0,6,12,18,24]).tickFormat(function(d){return d+\"時\";});",
// 		"yAxis_construct":"d3.svg.axis().scale(yScale).orient(\"left\").tickFormat(function(d){return d;});"
// 	}
	
	if(assignChartConfig==null){
		assignChartConfig={};
	}
	chartConfig={};
	
	if( assignChartConfig["xAxis_index"]==null
		||assignChartConfig["yAxis_index"]==null
		||assignChartConfig["xScale_construct"]==null
		||assignChartConfig["yScale_construct"]==null
		||assignChartConfig["xAxis_construct"]==null
		||assignChartConfig["yAxis_construct"]==null
	){
		console.log(assignChartConfig["xAxis_index"]);
		console.log(assignChartConfig["yAxis_index"]);
		console.log(assignChartConfig["xScale_construct"]);
		console.log(assignChartConfig["yScale_construct"]);
		console.log(assignChartConfig["xAxis_construct"]);
		console.log(assignChartConfig["yAxis_construct"]);
		console.log("使用drawBarChart時必填欄位未給定。不執行。");
		return;
	}
	
	chartConfig["elementID"] = assignChartConfig["elementID"] || "#data_chart";
	chartConfig["svg_width"] = assignChartConfig["svg_width"] || "400";
	chartConfig["svg_height"] = assignChartConfig["svg_height"] || "200";
	chartConfig["svg_margin"] = assignChartConfig["svg_margin"] || {top:10,right:10,bottom:20,left:10};
	chartConfig["bar_width"] = assignChartConfig["bar_width"] || "10";
	chartConfig["title_name"] = assignChartConfig["title_name"] || "";
	chartConfig["sub_title_name"] = assignChartConfig["sub_title_name"] || "";
	chartConfig["xAxis_name"] = assignChartConfig["xAxis_name"] || "400";
	chartConfig["yAxis_name"] = assignChartConfig["yAxis_name"] || null;
	chartConfig["fScale_construct"] = assignChartConfig["fScale_construct"] || "d3.scale.category20()";
	chartConfig["xAxis_index"] = assignChartConfig["xAxis_index"] ;
	chartConfig["yAxis_index"] = assignChartConfig["yAxis_index"] ;
	chartConfig["xScale_construct"] = assignChartConfig["xScale_construct"] ;
	chartConfig["yScale_construct"] = assignChartConfig["yScale_construct"] ;
	chartConfig["xAxis_construct"] = assignChartConfig["xAxis_construct"] ;
	chartConfig["yAxis_construct"] = assignChartConfig["yAxis_construct"] ;
	
	chartConfig["xAxis_index_offset"] = assignChartConfig["xAxis_index_offset"] ;
	
	var width = chartConfig["svg_width"];
	var height = chartConfig["svg_height"];
	var margin = chartConfig["svg_margin"];
	var svg ;
	var build_round;
	
	if($(chartConfig["elementID"]+" > svg").length==0){
		if($(chartConfig["elementID"]).length==0){
			$("body").append($("<div/>",{"id":chartConfig["elementID"].replace("#","")}));
		}
		svg = d3.select(chartConfig["elementID"]).append("svg")
				.attr("width",chartConfig["svg_width"])
				.attr("height",chartConfig["svg_height"]);
		svg.append("g").append("text").text(chartConfig["title_name"])
				.style({
					"text-anchor":"middle",
					"font-size":"30px",
				})
				.attr("x", chartConfig["svg_width"] / 2).attr("y", 30);
		svg.append("g").append("text").text(chartConfig["sub_title_name"])
				.attr("fill", "#666")
				.style({
					"text-anchor":"middle",
					"font-size":"18px",
				})
				.attr("x", chartConfig["svg_width"] / 2).attr("y", 56);
		d3.select(chartConfig["elementID"]+" > svg").append("g").attr("id", "rects");
		d3.select(chartConfig["elementID"]+" > svg").append("g").attr("id", "paths");
		d3.select(chartConfig["elementID"]+" > svg").append("g").attr("id", "circles");
		svg.append("g").attr("id", "axisX").append("text");
		svg.append("g").attr("id", "axisY").append("text");
		build_round=true;
	}else{
		svg = d3.select(chartConfig["elementID"]+" > svg");
		build_round=false;
	}
	
	bind();
	render();
	
	function bind(){
		var selection_1 = d3.select(chartConfig["elementID"]+" > svg > #rects").selectAll("rect").data(data);
	        selection_1.enter().append("rect");
	        selection_1.exit().remove();
	    var selection_2 = d3.select(chartConfig["elementID"]+" > svg > #paths").selectAll("path").data(data);
	        selection_2.enter().append("path");
	        selection_2.exit().remove();
	    var selection_3 = d3.select(chartConfig["elementID"]+" > svg > #circles").selectAll("circle").data(data);
	        selection_3.enter().append("circle");
	        selection_3.exit().remove();
	        
	}
	function render(){
		var xScale = eval(chartConfig["xScale_construct"]);//d3.scale.linear().domain([-1,25]).range([0, width - margin.left - margin.right]); 
		var yScale = eval(chartConfig["yScale_construct"]);
		var fScale = eval(chartConfig["fScale_construct"]);
		
		var xAxis = eval(chartConfig["xAxis_construct"]);
		var yAxis = eval(chartConfig["yAxis_construct"]);
			
		var colorTmp ,titleTmp="";
		d3.select(chartConfig["elementID"]+" > svg").selectAll("rect")
			.attr({	
				"tmp_title" : function(d){
					return show_if_significant(d["title"],"#P#");
				}
			})
			.transition()
		    .duration(build_round?0:500)
			.attr("transform","translate(" + margin.left + "," + ( 0 ) + ")")
			.attr({	
				'x': function(d, i){
					return xScale(d[chartConfig["xAxis_index"]])
						- chartConfig["bar_width"] * 0.5;
				},
				'y': function(d){
					if(+d[chartConfig["yAxis_index"]]>0){
						return margin.top + yScale(d[chartConfig["yAxis_index"]]);
					}else{
						return margin.top + yScale(0);
					}
				},
				'rx':'2',
				'width': chartConfig["bar_width"],
				'height':function(d){
					if(+d[chartConfig["yAxis_index"]]>0){
						return (yScale(0)-yScale(d[chartConfig["yAxis_index"]])+1);
					}else{
						return yScale(d[chartConfig["yAxis_index"]])-yScale(0);
					}
				},
				'fill': function(d){
					return fScale(chartConfig["yAxis_index"]);
				},
				"class" : function(d){ 
					return show_if_significant(d["title"],"tips_on_barchart");
				}
		    });
		
		d3.select(chartConfig["elementID"]+" > svg").selectAll("rect")
		    .on("mouseover",function(d){
		    	 colorTmp = d3.select(this).attr("fill");
	             d3.select(this).attr({fill:"gold"});
				 
				 var tooltip = "<div id='tooltip'>"+  $(this).attr("tmp_title") +"<\/div>";
				 $("body").append(tooltip);
				 $("#tooltip").css({"top": (d3.event.pageY+20) + "px","left": (d3.event.pageX+10)  + "px"}).show();
		    	
		    }).on("mouseout",function(d){
                d3.select(this).attr({fill:colorTmp});
		        $("#tooltip").remove();
		    }).on("mousemove",function(d){
		    	$("#tooltip").css({"top": (d3.event.pageY+20) + "px","left": (d3.event.pageX+10)  + "px"});
		    });
//		console.log(chartConfig["xAxis_index_offset"]);
//		console.log(yScale(-9114) +" ## " +yScale(5790));
		
		if(chartConfig["xAxis_name"]!=null){
			d3.select(chartConfig["elementID"]+" > svg").select("g#axisX")
					.attr("class","axis")
					.attr("transform","translate(" + margin.left + "," + ( margin.top + yScale(0)) + ")")
					.call(xAxis)
				.select('text')
					.text(chartConfig["xAxis_name"])
					.attr("class","bigger-word")
					.attr('transform', 'translate('+ (width-margin.left * 2 +20) +', '+(40)+')')
					.style("text-anchor", "middle");
		}	
		if(chartConfig["yAxis_name"]!=null){
			d3.select(chartConfig["elementID"]+" > svg").select("g#axisY")
					.attr("class","axis")
					.attr("transform","translate(" + (margin.left)+ "," + (margin.top) + ")")
					.call(yAxis)
			  .select('text')
					.text(chartConfig["yAxis_name"])
					.attr("class","bigger-word")
					.attr('transform', 'translate(-30, -8)');
		}
	}
	
	
}

function drawLineChart(){
	console.log("call CommonMethod-drawExcelTable");
	console.log("(Not implemented yet.)");
}

function drawPieChart(){
	console.log("call CommonMethod-drawPieChart");
	console.log("(Not implemented yet.)");
}

function drawCompareBarChart(){
	console.log("call CommonMethod-drawCompareBarChart");
	console.log("(Not implemented yet.)");
}

function setting_weAreAllTogether(){
	console.log("call CommonMethod-setting_weAreAllTogether");
//	weAreAllTogether.attach("桃園市",function(){alert('123');},function(){alert('223');});
//	weAreAllTogether.touch("桃園市");
//	weAreAllTogether.untouch("桃園市");
	if(!window.weAreAllTogether){
		weAreAllTogether={};
		weAreAllTogether.attach = function(key,func,un_func){
			if(weAreAllTogether[key]==null){
				weAreAllTogether[key]=[];
				weAreAllTogether["un_"+key]=[];
			}
			if(func!=null){
				weAreAllTogether[key].push(func);
			}
			if(un_func!=null){
				weAreAllTogether["un_"+key].push(un_func);
			}
		}
		weAreAllTogether.touch = function(key){
			if(weAreAllTogether[key]==null){
				weAreAllTogether[key]=[];
			}
			for(var i=0;i<weAreAllTogether[key].length;i++){
				weAreAllTogether[key][i]();
			}
		}
		weAreAllTogether.untouch = function(key){
			if(weAreAllTogether["un_"+key]==null){
				weAreAllTogether["un_"+key]=[];
			}
			for(var i=0;i<weAreAllTogether["un_"+key].length;i++){
				weAreAllTogether["un_"+key][i]();
				console.log(weAreAllTogether["un_"+key][i]);
				
			}
		}
	}
}

//function tooltip_sp_locattion(clas){
//	//如果debug要記得 有時候是因為alert才會錯
//	var this_width = '';
//	var this_height = '';
//	$("."+clas).css("z-index","2").mouseover(function(e){
//		
//		$(this).attr("newTitle",$(this).attr("title"));
//		$(this).attr("title","");
//		var tooltip = "<div id='tooltip' style='padding:5px;'><div style='margin:0 auto;'>"+ $(this).attr("newTitle") +"</div><\/div>";
//		$("body").append(tooltip);
//		$("#tooltip").css({"top": (e.pageY+20) + "px","left": (e.pageX+10)  + "px"}).show();
//		this_width = document.getElementById('tooltip').offsetWidth;
//		this_height = document.getElementById('tooltip').offsetHeight;
//	
//	 }).mouseout(function(){
//		 
//		 $(this).attr("title",$(this).attr("newTitle"));
//	     $("#tooltip").remove();
//	     
//	 }).mousemove(function(e){
//		 var final_x = e.pageX, final_y = e.pageY ;
//		 while( final_y + this_height > +$("html").height()-10){
//			 final_y-=1;
//		 }
//		 
//		 if( (final_x + this_width + 30) > +$("html").width()){
//			 $("#tooltip").css({"top": (final_y) + "px","left": (final_x-10-(this_width))  + "px"});
//		 }else{
//	     	$("#tooltip").css({"top": (final_y) + "px","left": (final_x+10)  + "px"});
//		 }
//		 
//	 });
//}
