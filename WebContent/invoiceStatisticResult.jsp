<!-- 
	這個網頁就是給invoiceStatistic.jsp用POST呼叫的
	傳一個jsonobj的字串變數名稱parameter
	並沒有做任何其他判斷的動作一氣呵成畫到底
		頁面共分三部分 有標題 資料 地圖 圖表
		圖表又分長條圖圓餅圖
-->

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>智能雲端市場定位系統</title>
	<link rel="Shortcut Icon" type="image/x-icon" href="./images/cdri-logo.gif" />
    <link rel="stylesheet" href="css/jquery-ui.min.css">
    
	<script type="text/javascript" src="js/d3.v3.min.js" charset="utf-8"></script>
	<script type="text/javascript" src="js/jquery-1.12.4.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui.min.js"></script>
	<script type="text/javascript" src="js/jquery-migrate-1.4.1.min.js"></script>
	<script type="text/javascript" src="js/topojson.min.js"></script>
	<script type="text/javascript" src="js/common.js"></script>
<style>
	body { 
 		font-family: "微軟正黑體", "Microsoft JhengHei", 'LiHei Pro', Arial, Helvetica, sans-serif; */
 	}
	select{
		width:100%;
	}
	#tooltip{
        position:absolute;
        border:1px solid #333;
        background:#f7f5d1;
        padding:3px;
        color:#333;
        display:none;
        z-index:10010;
        min-width:220px; 
		word-break:keep-all;
		line-height:14px;
		text-align:left;
		border-radius:2px;
		opacity:0.9;
	}
	#tooltip > div > table{
		text-align:left;
		margin:0 auto;
 		width:100%; 
	}
	#loading {
	    position: absolute;
	    background-color: rgba(128,128,128,0.5);
	    width: 100%;
	    height: 100%;
	    top:0px;
	    left:0px;
	    z-index: 9999;
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
 	.axis path, .axis line { 
 	    fill: none; 
 	    stroke: #000; 
 	    shape-rendering: crispEdges; 
 	} 
 	td.numberStr ,.excel-table td.numberStr, th.numberStr{
 		text-align:right; 
	}
</style>
<style> 
/* 	細部 */
	table{
		border-collapse: collapse;
		border-spacing: 0;
	}
	#tabs ul li a{
		text-align: center;
		padding: .5em 1.3em;
	}
	#data_dialog, #data_dialog > table {
		width:100%;
	}
	#data_dialog > table > tbody > tr > td {
		padding: 10px 10px; 
	}
	#data_chart polyline{
		opacity: .3;
		stroke: black;
		stroke-width: 2px;
		fill: none;
	}
</style>
<style>
/* populationNew.css重複了的 但改過td的text-align 改過excel-table */
	.excel-table {
		word-break:keep-all;
		margin: 5px auto;
		width: 95%;
		box-shadow: 1px 1px 6px rgba(0, 0, 0, 0.4);
	}

	.excel-table th{
		font-weight: 700;
		padding: 0 14px;
		background: #D0CECE;
	}
	.excel-table tr{
		height: 26px;
	}
	
	.excel-table tr:nth-child(odd){
		background: #D9E1F2;
	}
	.excel-table tr:nth-child(even){
		background: #FFF2CC;
	}
	.excel-table tr:nth-child(odd):hover{
		background: #c9d1e2;
	}
	.excel-table tr:nth-child(even):hover{
		background: #eFe2bC;
	}
	
	.excel-table td{
		padding: 0 14px;
		word-break: keep-all;
		text-align:center;
	}
</style>
	</head>
	<body>
		<div id='data_dialog' style='float:left;'>
		</div>
	
<script> 
	$(function(){
		
		var request_str = '<%= request.getParameter("request_str")%>';
		if(request_str=='null'){
			window.location.href ="./login.jsp";
			return;
		}
		
		request_str = b64DecodeUnicode(decodeURI(request_str));
		
		console.log(request_str);
		loading();
		request_json_obj = $.parseJSON(request_str=="null"?"[]":request_str);
// 		console.table(request_json_obj);
		//解析資料做應對
		if(request_json_obj["by-month"]!="false"){
			$("#data_dialog").addClass( "by-month" );
		}
		
		if(request_json_obj["compare_type"]!=null){
			$("#data_dialog").addClass( "two-lines" );
		}
		
		if(request_json_obj["with-map"]=="true"){
			//判斷式名存實亡 現在所有搜尋都withmap 沒有不同的html了
			$("#data_dialog").addClass( "with-map" );
			$("#data_dialog").html(
				$("<table/>").html([
					$("<tr/>").html([
						$("<td/>",{"colspan":"2"}).html(
							$("<div/>",{"id":"data_title","style":"text-align:center;font-size:28px;"})
						)
					]),
					
					$("<tr/>").html([
						$("<td/>",{"style":"","valign":"top"}).html(
							$("<div/>",{"id":"data_table","style":"padding:20px 10px 0 0;overflow-y: auto;"})
						),
						$("<td/>",{"rowspan":"2","valign":"top"}).html(
							$("<div/>",{"id":"data_map","text":""})
						)
					]),
					$("<tr/>").html([
						$("<td/>",{"valign":"top"}).html(
							$("<div/>",{"id":"data_chart","style":"text-align:center;"})
						)
					])
				])
			);
		}
		
		$("#data_title").html(request_json_obj["title"]);
		
		//解析資料做應對後查資料
		$.ajax({
			type : "POST",
			url : "invoiceStatistic.do",
			data : request_json_obj,
			success : function(result) {
				//查出資料後做資料處理
				//處理後主要進的function有三個(piechart barchart twoline_barchart擇一)
				//draw_topo_map() draw_data_table() draw_pie_chart() draw_bar_chart()
				//piechart項因為小於4%的資料顯示很醜 所以重組json_obj
				var json_obj = $.parseJSON(result);
				console.log(json_obj);
				if(json_obj.length==0){
					warningMsg("系統","此查詢條件查無資料。");
					loading('over');
					return ;
				}
				
				$.each(json_obj,function(i,item) {
					json_obj[i]=item["map"];
				});
				
				$.each(json_obj,function(i,item) {
					json_obj[i]["title"]= "<table>"
						+(item["year"]==null
							?(item["yyyymm"]!=null?("<tr><td>發票開立月份：</td><td>" + item["yyyymm"]+"</td></tr>"):"")
							:"<tr><td>發票開立時間：</td><td >" + item["year"]+($("#data_dialog").hasClass( "by-month" )?"":" 年度")+"</td></tr>")
						+(item["county_name"]==null?"":"<tr><td>發票開立縣市：</td><td>" + item["county_name"]+"</td></tr>")
						+(item["industry_category"]==null?"":"<tr><td>發票開立行業：</td><td>" + item["industry_category"]+"</td></tr>")
						+(item["quantity"]==null?"":"<tr><td>發票開立數量：</td><td>" + item["quantity"]+"&nbsp;張</td></tr>")
						+(item["quantity"]==null?"":"<tr><td>發票數量占比：</td><td>" + new Number((+item["quantity"] * 100)  / (+d3.sum(json_obj, function(d){return +d["quantity"];}))).toFixed(2)+" %</td></tr>")
						+(item["amount"]==null?"":"<tr><td>發票開立總額：</td><td>" + item["amount"]+"&nbsp;元</td></tr>")
						+(item["amount"]==null?"":"<tr><td>發票總額占比：</td><td>" + new Number((+item["amount"] * 100)  / (+d3.sum(json_obj, function(d){return +d["amount"];}))).toFixed(2)+" %</td></tr>")
						+"</table>";
				});
				
				var chart_config= {
					title : "消費軌跡資料統計",
					xAxis_name: "year",
					yAxis_name: "quantity",
					xAxis_label: "年份",
					yAxis_label01: "發票張數(張)",
					yAxis_label02: "占比(%)",
				};
			 	
				if($("#data_dialog").hasClass( "with-map" )){
					loading();
					if(request_json_obj["compare_type"]=="quarter"){
						var obj = quarter_data_deal(json_obj);
						if(obj.length==0){
// 							warningMsg("系統","此查詢條件無法年度比較。");
							loading('over');
							loading('over');
							return ;
						}
						draw_topo_map(obj);
					}else{
	 					draw_topo_map(json_obj);
					}
	 			}
				loading();
				if(request_json_obj["compare_type"]=="quarter"){
					var obj = quarter_data_deal(json_obj);
					if(obj.length==0){
// 						warningMsg("系統","此查詢條件無法年度比較。");
						loading('over');
						loading('over');
						return ;
					}
					draw_data_table(obj);
				}else{
					draw_data_table(json_obj);
				}
	 			
				
	 			if($("#data_dialog").hasClass( "by-month" )){
	 				var pie_chart_obj = prepare_pie_chart_data(json_obj);
	 				loading();
	 				draw_pie_chart(pie_chart_obj);
				}else if($("#data_dialog").hasClass( "two-lines" )){
					var bar_chart_obj = prepare_two_line_bar_chart_data(json_obj,request_json_obj["compare_type"]);
					
					if(bar_chart_obj.length==0){
						warningMsg("系統","此查詢條件無法年度比較。");
						loading('over');
						loading('over');
						return ;
					}
					
					draw_two_line_bar_chart(bar_chart_obj);
				}else{
					loading();
					draw_bar_chart(json_obj,chart_config);
				}
	 			loading('over');
	 			setTimeout(function(){
		 			$("#data_dialog").css("zoom",
		 				new Number(+document.body.clientWidth * 100 / +document.body.scrollWidth ).toFixed(0)+"%"
		 			);
	 			},100);
			},error : function(e) {
				warningMsg("警告","網路連線異常，請洽系統管理員。");
			}
		});
		
	});
</script>
	<script> 
	//含4個畫頁面function
	//draw_pie_chart() 畫圓餅圖
	//draw_topo_map() 畫地圖  
	//draw_bar_chart() 畫長條圖
	//draw_data_table() 畫資料表
	//draw_two_line_bar_chart() 月份季度比較圖
	
	function draw_pie_chart(json_obj_chart){
	// 	預期接受的jsonobj包含四個參數
	// 	name,data,percent,title
		var width = 900,
		    height = 600,
			radius = 250;
		var pie = d3.layout.pie()
			.sort(null)
			.value(function(d) {
				return d["data"];
			});
		var fScale = d3.scale.category20();
		var arc = d3.svg.arc()
			.outerRadius(radius * 0.8)
			.innerRadius(radius * 0);
	
		var outerArc = d3.svg.arc()
			.innerRadius(radius * 0.9)
			.outerRadius(radius * 0.9);
		
		var key = function(d){return d.data["name"]; };
		var tooltipdiv = d3.select(".tooltip");
		var svg = d3.select("#data_chart")
			.append("svg").attr({
				id : "pie_chart",
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
			.style("fill", function(d) { return fScale(d.data["name"]); })
			.attr({
				"class":function(d){
					return "slice tips_on_piechart "+"name_"+d.data["name"];
				},"title":function(d){
					return d.data["title"];
				}
			}).on("mouseover",function(d){
				var pos = outerArc.centroid(d);
				pos[0]=pos[0]*0.02;
				pos[1]=pos[1]*0.02;
				
				var str =fScale(d.data["name"]);
				str = str.replace("#", "");
                var hxs = str.match(/../g);
                for (var i = 0; i < 3; i++) hxs[i] = parseInt(hxs[i], 16);
                var rgbc = hxs;
                for (var i = 0; i < 3; i++) rgbc[i] = Math.floor((255 - rgbc[i]) * 0.2 + rgbc[i]);
                var darker = "rgb("+ rgbc.join(",")+")";
				d3.select(this).transition().duration(300)
						.attr("stroke", "#f00")
						.attr("stroke-width", 1)
						.attr("transform","translate("+ pos +")")
						.style("fill",darker);
				
			}).on("mouseout",function(d){
				d3.select(this).transition().duration(300)
				.attr("stroke", "#f00")
				.attr("stroke-width", 0)
				.attr("transform","translate(0,0)")
				.style("fill",fScale(d.data["name"]));
			})
			
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
				return d.data["name"];
			}).attr({
				"class":function(d){
					return "tips_on_piechart";
				},"title":function(d){
					return d.data["title"];
				}
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
			.append("polyline")
			.attr({
				"class":function(d){
					return "tips_on_piechart";
				},"title":function(d){
					return d.data["title"];
				}
			});

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
		tooltip("tips_on_piechart");
		loading('over');
	}
	
	function draw_topo_map(data){
		//畫地圖 讀取 topo_Taiwan_edit.json共22縣市的資料 edit是修改過連江縣金門縣位置的檔案
		//讀取資料 把同縣市的title累加上去並在滑鼠移上去時用tooltip顯示出來 (但上限五個)
		//有資料的縣市綠色 剩下淡藍色
		county_list = [
			{"county":"基隆市","district_title":"","count":0},{"county":"臺北市","district_title":"","count":0},{"county":"新北市","district_title":"","count":0},{"county":"桃園市","district_title":"","count":0},
			{"county":"新竹縣","district_title":"","count":0},{"county":"新竹市","district_title":"","count":0},{"county":"苗栗縣","district_title":"","count":0},{"county":"臺中市","district_title":"","count":0},
			{"county":"南投縣","district_title":"","count":0},{"county":"彰化縣","district_title":"","count":0},{"county":"雲林縣","district_title":"","count":0},{"county":"嘉義縣","district_title":"","count":0},
			{"county":"嘉義市","district_title":"","count":0},{"county":"臺南市","district_title":"","count":0},{"county":"高雄市","district_title":"","count":0},{"county":"屏東縣","district_title":"","count":0},
			{"county":"宜蘭縣","district_title":"","count":0},{"county":"花蓮縣","district_title":"","count":0},{"county":"臺東縣","district_title":"","count":0},{"county":"澎湖縣","district_title":"","count":0},
			{"county":"金門縣","district_title":"","count":0},{"county":"連江縣","district_title":"","count":0}
		];
		$.each(data,function(i,item){
			$.each(county_list,function(j,jtem){
				if(county_list[j]["county"] == data[i]["county_name"]){
					if(county_list[j]["count"]==null){
						county_list[j]["count"]=0;	
					}
					
					if(county_list[j]["count"]<5){
						county_list[j]["district_title"] += (county_list[j]["district_title"]!=""?"<br>":"")+ data[i]["title"] ;
					}else if(county_list[j]["count"]==5){
						county_list[j]["district_title"] = county_list[j]["district_title"]
													+ "<div>.</div><div>.</div><div>.</div><div>&nbsp;</div>" ;
					}
					
					county_list[j]["count"]++;
				}
			});
		});
		
		county_count = 0;
		$.each(county_list,function(i,item){
			if(item["district_title"]!=""){
				county_list[i]["district_title"] = "<div style=\"font-size:1.2em;margin:10px auto;\">&nbsp;&nbsp;"+county_list[i]["county"]+"</div>"
													+county_list[i]["district_title"]
													+(county_list[i]["count"]<5?"":"<div>等"+county_list[i]["count"]+"筆資料</div>");
				county_list[i]["No"] = county_count++;
			}
		});
		
		d3.json("./js/json/topo_Taiwan_edit.json", function(topoData) {
            var projection = d3.geo.mercator().center([121.6,25.1]).scale(10000);    
            var path = d3.geo.path().projection(projection);
            var topo = topojson.feature(topoData, topoData.objects["county"]);
            $("#data_map > svg").remove();
            var selection = d3.select("#data_map").append("svg")
	            .attr("width","700")
				.attr("height","1000")
	            .selectAll("path").data(topo.features);
            
            selection.enter().append("path");
            selection.exit().remove();
            selection.classed("map-boundary", true).attr("d", path);
            
            var fScale = d3.scale.category20c();
            var colorTmp ;
            
            d3.selectAll("#data_map path")
            .attr({
                title : function(d,i){
                	var ret_str="";
					$.each(county_list,function(i,item){
						if(county_list[i]["county"] == d["properties"]["C_Name"]){
							ret_str =  county_list[i]["district_title"];
							return "";
						}
					});
					return ret_str;
                },
                "stroke": "#000",
                "stroke-width": "0.3",
				fill: function(d,i){
                	if($(this).attr("title")!=""){
                		return "#282";
                	}
                    return "rgba(180,180,256,0.3)";
                },
                class : function(d,i){
                	if($(this).attr("title")!=""){
                		return "tip_on_map";
					}else{
						return "";
					}
                }
           }).on("mouseover",function(d){
                colorTmp = d3.select(this).attr("fill");
                d3.select(this).attr({fill:(d3.select(this).attr("title")==""?colorTmp:"gold")});
           }).on("mouseout",function(d){
               d3.select(this).attr({fill:colorTmp});
           });
            tooltip("tip_on_map");
            loading('over');
		});
	}
	
	
	function draw_bar_chart(data,chart_config){
		//畫長條圖 
		//分年度的有兩種狀況
		//search124 一種是最多就四年2014~2017的寬寬長條圖 會有path連接長條圖上的點點
		//search3 一種是不同年度不同縣市皆要顯示 很多小草貌 縣市依顏色排序 沒有path連接
		
//		本來是要把參數都放進chartconfig做成通用的畫圖表method
//		可是因為比較匆忙 所以放寬標準後可能寫到一些當前頁面依賴的參數
//		還不能實用的幾個原因
// 		1.縱軸橫軸Scale沒有因應 "不連續項目","連續項目","時間" 做不同的整理
// 			(目前X軸是時間前後各多半個小時 顯示單位為"年")
// 			(目前Y軸是連續項目參照yAxis_name)
//		2.目前指定畫在綁id的element上
//		3.後面比較匆忙 沒有檢查有沒有依賴到其他資料

		var county_arr=[];
		var year_arr=[];
		
		if(!window.county_list){
			$.each(data,function(i,item){
				if(county_arr.indexOf(item["county_name"])==-1){
					county_arr.push(item["county_name"]);
				}
			});
			county_list = [];
			$.each(county_arr,function(i,item){
				county_list.push({
					county : item,
					district_title : "",
					No : i
				});
			});
		}
		
		$.each(data,function(i,item){
			if(county_arr.indexOf(item["county_name"])==-1){
				county_arr.push(item["county_name"]);
			}
			if(year_arr.indexOf(item["year"])==-1){
				year_arr.push(item["year"]);
			}
	
			$.each(county_list,function(j,jtem){
				if(jtem["county"]==item["county_name"]){
					data[i]["No"] = county_list[j]["No"];
				}
			});
			
		});
		
		d3.select("#data_chart svg").remove();
		var margin = {top: 70, right: 160, bottom: 100, left: 120};
		
		var svg = d3.select("#data_chart").append("svg")
					.attr("width","760")
					.attr("height","600")
		
		var width = $("#data_chart svg").width();
		var height = $("#data_chart svg").height();
		var barMargin = 10 ;
		
		var barWidth = ( width-margin.left-margin.right ) / ((county_arr.length+1) * (year_arr.length+1));
		
		if(barWidth < barMargin*3){
			barMargin = barWidth * 0.3;
			barWidth = barWidth * 0.7;
		} else {
			barWidth = barWidth - barMargin ; 
		}
		barWidth = barWidth > 80 ? 80 : barWidth;
		
		//左邊右邊下面的三個比例尺
		 var xScale = d3.time.scale()
			.domain([
				new Date(d3.min(data, function(data) { 
						return new Date(data[chart_config.xAxis_name]+($("#data_dialog").hasClass( "by-month" )?"/01":""));
					}).valueOf()
					- 1000 * 60 * 60 * 24 * 100),
				new Date(d3.max(data, function(data) { 
						return new Date(data[chart_config.xAxis_name]+($("#data_dialog").hasClass( "by-month" )?"/01":""));
					}).valueOf()
					+ 1000 * 60 * 60 * 24 * 300)
			])
			.range([0, width - margin.left - margin.right]);
		var yScale = d3.scale.linear()
			    .domain([
			    	0,
			    	(
			    		data.length>10
			    		?d3.max(data, function(data) { return +data[chart_config.yAxis_name]; })*1.1
			    		:d3.sum(data, function(data) { return +data[chart_config.yAxis_name]; })
			    	)
			    ])
			    .range([height - margin.top - margin.bottom,0]);
		var yScale2 = d3.scale.linear()
			    .domain([0,
			    	(
			    		data.length>10
		    			?d3.max(data, function(data) { return +data[chart_config.yAxis_name]; })*1.1*100
			    		/
			    		d3.sum(data, function(data) { return +data[chart_config.yAxis_name]; })
			    		:100
			    	)
			    	])
			    .range([height - margin.top - margin.bottom, 0]);
		var fScale = d3.scale.category20();
		
		//一個X軸 兩個Y軸的背景設定
		var xAxis = d3.svg.axis()
			    .scale(xScale)
			    .orient("bottom")
			    .ticks(d3.time.year, 1)
			    .tickFormat(d3.time.format('%Y年度'));
		
		var yAxis = d3.svg.axis()
			    .scale(yScale)
			    .tickFormat(function(d){
			    	return money_format(d);
			    })
			    .orient("left");
		
		var yAxis2 = d3.svg.axis()
			    .scale(yScale2)
			    .tickFormat(function(d){
			    	return d+"%";
			    })
			    .orient("right");
		
		//產生linechart的用
		var lineGen = d3.svg.line()
				.x(function(data) {return xScale(new Date(data[chart_config.xAxis_name]))+((barWidth+barMargin)*data["No"])+barWidth*0.5;})
				.y(function(data) {return yScale(data[chart_config.yAxis_name]);})
				.interpolate("line");
		
		//長條圖的方框
		svg.selectAll('rect').data(data).enter()
			.append('rect') 
			.attr("transform","translate(" + margin.left + "," + ( 0 ) + ")")
			.attr({	
				'x': function(d, i){
					return xScale(new Date(d[chart_config.xAxis_name])) + ((barWidth+barMargin)*d["No"]);
				},
				'y': function(d){
					return height-margin.bottom;
				},
				'width': barWidth,
				'height':function(d){
					return 0;
				},
				'fill': function(d){
					return (d["No"]!=null?fScale(d["No"]):"");
				},
				"title" : function(data){
					return data.title!=null?data.title:"";
				},
				"class" : function(data){ 
					return (data.title!=null?"tips_on_barchart":"");
				}
			})
		    .attr({
		    	'y': function(d){
		    		return margin.top + yScale(d[chart_config.yAxis_name])},
		    	'height':function(d){return height - margin.bottom - margin.top - yScale(d[chart_config.yAxis_name])-0.3},
		    });
		if( data.length == year_arr.length ){
		//折線圖
			svg.append('path')
			    .attr({
				  'stroke': '#e60',
			      'fill': 'none',
			      'stroke-width':'3',
			    }).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
			    .attr({
			    	'd': lineGen(data),
			    });
		}
		//移上去會有內容的圓點
		svg.append("g").selectAll("circle")
			.data(data)
			.enter()
			.append("circle")
			.attr({
				"cx":function(data) {
					return xScale(new Date(data[chart_config.xAxis_name]))+((barWidth+barMargin)*data["No"])+barWidth*0.5;},
				"cy":function(data) {return height-margin.top-margin.bottom;},
				"r" : 0,
				"stroke" : "rgba(0,0,0,0)",
				"stroke-width" : 12,
				"fill": function(d){
					if(data.length == year_arr.length){
						return "#e60";
					}
					//把顏色加量一點 不然很像猥褻物
					var str = fScale(d["No"]);
					str = str.replace("#", "");
	                var hxs = str.match(/../g);
	                for (var i = 0; i < 3; i++) hxs[i] = parseInt(hxs[i], 16);
	                var rgbc = hxs;
	                for (var i = 0; i < 3; i++) rgbc[i] = Math.floor(rgbc[i] * (1 - 0.2));
	                
	                var hexs = [rgbc[0].toString(16), rgbc[1].toString(16), rgbc[2].toString(16)];
	                for (var i = 0; i < 3; i++) if (hexs[i].length == 1) hexs[i] = "0" + hexs[i];
	                return "#" + hexs.join("");
	                
				},
				"title" : function(data){
					return data.title!=null?data.title:"";
				},
				"class" : function(data){ 
					return (data.title!=null?"tips_on_barchart":"");
				}
			}).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
	    	.attr({
	    		"r" : Math.abs(barWidth*0.5-5)>5?5:barWidth*0.5,
	    		"cy":function(data) {return yScale(data[chart_config.yAxis_name]);}
	    	});
		//d3js chrome ie 不吃html 只吃text 所以info分兩個-1
		svg.append("g").selectAll("text")
			.data(county_list)
			.enter()
			.append("text")
			.attr("fill",function(d,i){
				return fScale(d["No"]);
			})
		    .text(function(d,i){
		    	return (d["No"]==null?"":"▬");
		    })
		    .attr('transform',function(d,i){
		    	return 'translate('+(width-margin.right+54)+', '+ (margin.top+(d["No"]==null?20:(d["No"]+1)*16)) +')';
		     }) 
		     .attr('font-size','16px')
		    .style("text-anchor", "start");
		
		//d3js chrome ie 不吃html 只吃text 所以info分兩個-2
		svg.append("g").selectAll("text")
			.data(county_list)
			.enter()
			.append("text")
			.attr("fill",function(d,i){
				return "#000";
			})
		    .text(function(d,i){
		    	return (d["No"]==null?"":d["county"]);
		    })
		    .attr('transform',function(d,i){
		    	return 'translate('+(width-margin.right+76)+', '+ (margin.top+(d["No"]==null?20:(d["No"]+1)*16)) +')';
		     }) 
		     .attr('font-size','16px')
		    .style("text-anchor", "start");
		
		//畫一個X軸 兩個Y軸 和X軸的標籤
		svg.append("g")
			   .attr("class","axis")
			   .attr("transform","translate(" + margin.left + "," + (height - margin.bottom) + ")")
			   .call(xAxis)
			   .selectAll("text")
			   .attr("transform", "rotate(25)")
			   .style("text-anchor", "start");
		
		svg.append("g")
			  .attr("class","axis")
			  .attr("transform","translate(" + (width-margin.right)+ "," + margin.top + ")")
			  .call(yAxis2)
			  .append('text')
			  .text(chart_config.yAxis_label02)
			  .attr('transform', 'translate(-50, -20)');
		
		svg.append("g")
			  .attr("class","axis")
			  .attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
			  .call(yAxis)
			  .append('text')
			  .text(chart_config.yAxis_label01)
			  .attr('transform', 'translate(-50, -10)');
		
		svg.append("g")
			.append('text')
			.text(chart_config.xAxis_label)
			.attr("transform","translate(" + (margin.left-40)+ "," + (height-margin.bottom+40) + ")");
		
		tooltip("tips_on_barchart");
		loading('over');
	}
	
	function draw_data_table(data){
		//因為時間不太夠 直接用html code
		$("#data_table > table").remove();
		var title = "";
		var table_cursor = $("<table/>",{'class':'excel-table','style':'width:840px;'});
		
		table_cursor.append($('<tr/>').append(
			"<th>序號</th>"
			+(data[0]["year"]==null
				?( data[0]["yyyymm"]!=null?"<th>月份</th>":"" )
				:($("#data_dialog").hasClass( "by-month" )?"<th>月份</th>":"<th>年度</th>"))
			+(data[0]["county_name"]==null?"":"<th>縣市</th>")
			+(data[0]["industry_category"]==null?"":"<th>行業別</th>")
			+(data[0]["quantity"]==null?"":"<th>開立發票數量</th>")
			+(data[0]["quantity"]==null?"":"<th>占比</th>")
			+(data[0]["amount"]==null?"":"<th>開立發票總額</th>")
			+(data[0]["amount"]==null?"":"<th>占比</th>")
		));
		
		var tr_cursor = $('<tr/>');
		var td_cursor = $('<td/>');
		var amount_cursor = 0 ;
		$.each(data, function(j, jtem) {
			table_cursor.append("<tr><td>"+(j+1)+"</td>"
				+(data[j]["year"]==null
					?( data[j]["yyyymm"]!=null?("<td>"+data[j]["yyyymm"]+"</td>"):"" )
					:"<td>"+null2str(($("#data_dialog").hasClass( "by-month" )?jtem["year"].replace("/","&nbsp;年&nbsp;")+"&nbsp;月":jtem["year"]))+"</td>")
				+(data[j]["county_name"]==null?"":"<td>"+null2str(jtem["county_name"])+"</td>")
				+(data[j]["industry_category"]==null?"":"<td>"+null2str(jtem["industry_category"])+"</td>")
				+(data[j]["quantity"]==null?"":"<td class='numberStr'>"+money_format(null2str(jtem["quantity"]))+"&nbsp;張</td>")
				+(data[j]["quantity"]==null?"":"<td class='numberStr'>"+new Number((+jtem["quantity"] * 100)  / (+d3.sum(data, function(d){return +d["quantity"];}))).toFixed(2)+"&nbsp;%</td>")
				+(data[j]["amount"]==null?"":"<td class='numberStr'>"+money_format(null2str(jtem["amount"]))+"&nbsp;元</td>")
				+(data[j]["amount"]==null?"":"<td class='numberStr'>"+new Number((+jtem["amount"] * 100)  / (+d3.sum(data, function(d){return +d["amount"];}))).toFixed(2)+"&nbsp;%</td>")
				+"</tr>"
				);
		});
		
		table_cursor.append(
				"<tr style='border-top:1px solid #000;'><th colspan='"+(table_cursor.html().split("</th>").length-5)+"'>總　　計</th>"
				+"<th class='numberStr'>"+money_format(+d3.sum(data, function(d){return +d["quantity"];}))+"&nbsp;張</th>"
				+"<th class='numberStr'>100&nbsp;%</th>"
				+"<th class='numberStr'>"+money_format(+d3.sum(data, function(d){return +d["amount"];}))+"&nbsp;元</th>"
				+"<th class='numberStr'>100&nbsp;%</th>"
				+"</tr>"
				);
		$("#data_table").append(table_cursor);
		$(".excel-table tr").css("height",
			(data.length <5 ?"30px":
				(data.length <25 ?"26px":
					(data.length <40 ?"20px":"16px")
				)
			)
		);
		tooltip('tips_on_data');
		loading('over');
	}
	function draw_two_line_bar_chart(data,chart_config){
		chart_config = $.parseJSON('{"xAxis_name":"interval_name","yAxis_name_left":"fst_quantity","yAxis_name_right":"snd_quantity","xAxis_label":"時間","yAxis_label01":"發票開立張數"}');
		
		var fst_name = data.length>0?data[0]["fst_name"]:"";
		var snd_name = data.length>0?data[0]["snd_name"]:"";

		var yAxis_name=[chart_config.yAxis_name_left,chart_config.yAxis_name_right];
		d3.select("#data_chart svg").remove();
		var margin = {top: 70, right: 80, bottom: 100, left: 160};
		
		var svg = d3.select("#data_chart").append("svg")
					.attr("width","1100")
					.attr("height","600")
		var yAxis_name = [chart_config.yAxis_name_left,chart_config.yAxis_name_right]
		var width = $("#data_chart svg").width();
		var height = $("#data_chart svg").height();
		var barWidth = request_json_obj["compare_type"]=="quarter"?70:25 ;
		console.log(barWidth+"###");
		var deviation_padding = barWidth * 0.5+0.1;
		
		 var xScale = d3.scale.linear()
		    .domain([0,(request_json_obj["compare_type"]=="quarter"?5:13)])
			.range([0, width - margin.left - margin.right]);
		var yScale = d3.scale.linear()
			    .domain([
			    	0,
			    	d3.max(data, function(data) { return Math.max(+data[chart_config.yAxis_name_left],+data[chart_config.yAxis_name_right]); })*1.1
			    ])
			    .range([height - margin.top - margin.bottom,0]);
		var fScale = d3.scale.category20();
		
		var fScale = function(str){
			
			color_cache = ['#5b9cd6','#ffce54','#4b6ca6','#de7310','#666699','#92d5ea','#5a3b16','#26a4ed','#f45a90'];
			if(!window.str_cache){
				window.str_cache=[];
			}
			if(str_cache.indexOf(str)==-1){
				str_cache.push(str);
			}
			return color_cache[str_cache.indexOf(str)];
		}
		
		//一個X軸 兩個Y軸的背景設定
		var xAxis = d3.svg.axis()
			    .scale(xScale)
			    .orient("bottom")
			    .tickValues(request_json_obj["compare_type"]=="quarter"?[1,2,3,4]:[1,2,3,4,5,6,7,8,9,10,11,12])
				.tickFormat(function(d){
			    	return (request_json_obj["compare_type"]=="quarter"?"第"+d+"季":d+"月");
			    })
		
		var yAxis = d3.svg.axis()
			    .scale(yScale)
			    .tickFormat(function(d){
			    	return money_format(d);
			    })
			    .orient("left");
		//旁邊的info
		//d3js chrome ie 不吃html 只吃text 所以info分兩個-1
		svg.append("g").selectAll("text")
			.data([yAxis_name[0],yAxis_name[1]])
			.enter()
			.append("text")
			.attr("fill",function(d,i){
				return fScale(d);
			})
		    .text(function(d,i){
		    	return "▬";
		    })
		    .attr('transform',function(d,i){
		    	return 'translate('+(width-margin.right-30)+', '+ (margin.top+(i*22)) +')';
		     }) 
		     .attr('font-size','22px')
		    .style("text-anchor", "end");
		//d3js chrome ie 不吃html 只吃text 所以info分兩個-2
		svg.append("g").selectAll("text")
			.data([yAxis_name[0],yAxis_name[1]])
			.enter()
			.append("text")
			.attr("fill",function(d,i){
				return "#000";
			})
		    .text(function(d,i){
		    	return (i==0?fst_name:snd_name);
		    })
		    .attr('transform',function(d,i){
		    	return 'translate('+(width-margin.right-18)+', '+ (margin.top+(i*22)) +')';
		     }) 
		     .attr('font-size','16px')
		    .style("text-anchor", "start");
		
		//畫一個X軸 兩個Y軸 和X軸的label
		svg.append("g")
			   .attr("class","axis")
			   .attr("transform","translate(" + margin.left + "," + (height - margin.bottom) + ")")
			   .call(xAxis)
			   .selectAll("text")
			   .style("text-anchor", "middle");
		
		svg.append("g")
			  .attr("class","axis")
			  .attr("transform","translate(" + (margin.left)+ "," + (margin.top) + ")")
			  .call(yAxis)
			  .append('text')
			  .html("<a style='font-size:1.2em'>"+chart_config.yAxis_label01+"</a>")
			  .attr('transform', 'translate(-50, -30)');
		
		svg.append("g")
			.append('text')
			.html("<a style='font-size:1.2em'>"+chart_config.xAxis_label+"</a>")
			.attr("transform","translate(" + (width-margin.right-40)+ "," + (height-margin.bottom+60) + ")");
		
		//畫長條圖的方框
		for(var deviation=0;deviation<2;deviation++){
			
			svg.selectAll('rect.'+yAxis_name[deviation]).data(data).enter()
				.append('rect') 
				.attr("transform","translate(" + margin.left + "," + ( 0 ) + ")")
				.attr({	
					'x': function(d, i){
						return xScale(d[chart_config.xAxis_name])
							- barWidth * 0.5
							- Math.pow(-1,deviation) * deviation_padding;
					},
					'y': function(d){
						return margin.top + yScale(d[yAxis_name[deviation]])
					},
					'width': barWidth,
					'height':function(d){
						return height - margin.bottom - margin.top 
							- yScale(d[yAxis_name[deviation]])-0.3;
					},
					'stroke-width': 0 ,//0.3,
					'stroke':function(d){
						return "#000";
					},
					'fill': function(d){
						return fScale(yAxis_name[deviation]);
					},
					"title" : function(data){
						return data[yAxis_name[deviation].substr(0,3)+"_title"]!=null?data[yAxis_name[deviation].substr(0,3)+"_title"]:"";
					},
					"class" : function(data){ 
						return (data[yAxis_name[deviation].substr(0,3)+"_title"]!=null?"tips_on_barchart":"");
					}
			    });
		}
		
		for(var deviation=0;deviation<2;deviation++){
			var lineGen = d3.svg.line()
			.x(function(data) {
				return xScale(data[chart_config.xAxis_name])
					- Math.pow(-1,deviation) * deviation_padding;
			})
			.y(function(data) {return yScale(data[yAxis_name[deviation]]);})
			.interpolate("line");
			
			//折線圖
			svg.append('path')
			    .attr({
				  'fill': 'none',
				  'stroke':function(d){
					  	return fScale(yAxis_name[deviation]+"_2");
				  },
			      'stroke-width':3,
			    }).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
			    .attr({
			    	'd': lineGen(data),
			    });
			
			//移上去會有內容的圓點
			svg.selectAll('circle.'+yAxis_name[deviation]).data(data).enter()//.data(data)
			.append('circle') 
				.attr({
					"cx":function(data) {
						return xScale(data[chart_config.xAxis_name])
							- Math.pow(-1,deviation) * deviation_padding;
					},
					"cy": function(data) {return yScale(data[yAxis_name[deviation]]);},
					"r" : 3+1,
					"stroke" : "rgba(0,0,0,0)",
					"stroke-width" : 12,
					'fill': function(d){
						return fScale(yAxis_name[deviation]+"_2");
					},
					"title" : function(data){
						return data[yAxis_name[deviation].substr(0,3)+"_title"]!=null?data[yAxis_name[deviation].substr(0,3)+"_title"]:"";
					},
					"class" : function(data){ 
						return (data[yAxis_name[deviation].substr(0,3)+"_title"]!=null?"tips_on_barchart":"");
					}
				}).attr("transform","translate(" + (margin.left)+ "," + margin.top + ")")
		    ;
		}
		tooltip("tips_on_barchart");
	}
	
	
	</script>

	<script>//### data deal ###
	//含四個function
	//getQuarter()
	//quarter_data_deal()
	//prepare_two_line_bar_chart_data()
	//prepare_pie_chart_data()
	function getQuarter(d) {
		var q = ["1","2","3","4"];
		return q[Math.floor((+d.substr(4,2)-1)/ 3)];
	}
	
	function quarter_data_deal(data){
		var one_dimension_answer={};
		$.each(data,function(i,item) {
			var q = ["1","2","3","4"];
			var keyStr = item["yyyymm"].substr(0,4)+"-Q"+ q[Math.floor((+item["yyyymm"].substr(4,2)-1)/ 3)];
			
			if(one_dimension_answer[keyStr]==null){
				one_dimension_answer[keyStr]={};
			}
			var ptr=one_dimension_answer[keyStr];
			
			ptr["county_name"]=item["county_name"];
			ptr["industry_category"]=item["industry_category"];
			ptr["count"]=(ptr["count"]==null?1:ptr["count"]+1);
			ptr["yyyymm"]=keyStr;
			$.each(item,function(key,value) {
				if(key=="amount"||key=="quantity"){
					ptr[key]=ptr[key]==null?+value:ptr[key]+(+value);
				}
			});
		});
		
		
		var answer_arr=[];
		$.each(one_dimension_answer,function(key,item) {
			if(item["count"]==3){
				item["title"]="<table>"
					+(item["yyyymm"]!=null?("<tr><td>發票開立季度：</td><td>" + item["yyyymm"]+"</td></tr>"):"")
					+(item["county_name"]==null?"":"<tr><td>發票開立縣市：</td><td>" + item["county_name"]+"</td></tr>")
					+(item["industry_category"]==null?"":"<tr><td>發票開立行業：</td><td>" + item["industry_category"]+"</td></tr>")
					+(item["quantity"]==null?"":"<tr><td>發票開立數量：</td><td>" + item["quantity"]+"&nbsp;張</td></tr>")
					+(item["quantity"]==null?"":"<tr><td>發票數量占比：</td><td>" + new Number((+item["quantity"] * 100)  / (+d3.sum(data, function(d){return +d["quantity"];}))).toFixed(2)+" %</td></tr>")
					+(item["amount"]==null?"":"<tr><td>發票開立總額：</td><td>" + item["amount"]+"&nbsp;元</td></tr>")
					+(item["amount"]==null?"":"<tr><td>發票總額占比：</td><td>" + new Number((+item["amount"] * 100)  / (+d3.sum(data, function(d){return +d["amount"];}))).toFixed(2)+" %</td></tr>")
					+"</table>";
				answer_arr.push(item);
			}
		});
		
		answer_arr.sort(function(a, b) {return a["yyyymm"] - b["yyyymm"];});
		
		return answer_arr;
	}
	
	function prepare_two_line_bar_chart_data(data,month_or_quarter){
		//之後如果有機會要改到java或SQL_sp處理的部份
		
		var one_dimension_answer={};
		var fst_year = request_json_obj["selectYearStart"],snd_year = (+request_json_obj["selectYearStart"]+1)+"";
		var data_group=""
		$.each(data,function(i,item) {
			data_group = item["yyyymm"].substr(0,4)==fst_year?"fst_":data_group;
			data_group = item["yyyymm"].substr(0,4)==snd_year?"snd_":data_group;
		
			
			$.each(item,function(key,value) {
				if(month_or_quarter=="quarter"){
					if(one_dimension_answer[getQuarter(item["yyyymm"])]==null){
						one_dimension_answer[getQuarter(item["yyyymm"])]={};
					}
					if(key=="amount"||key=="quantity"){
						
						if(one_dimension_answer[getQuarter(item["yyyymm"])][ data_group + key ] ==null){
							one_dimension_answer[getQuarter(item["yyyymm"])][ data_group + key ] = 0;
						}
						one_dimension_answer[getQuarter(item["yyyymm"])][ data_group + key ] += (+value);
					}else if(key=="yyyymm"){
					}else if(key=="title"){
					}else{
						one_dimension_answer[getQuarter(item["yyyymm"])][ data_group + key ] = value
					}
				}else{
					if(one_dimension_answer[item["yyyymm"].substr(4,2)]==null){
						one_dimension_answer[item["yyyymm"].substr(4,2)]={};
					}
					one_dimension_answer[item["yyyymm"].substr(4,2)][ data_group + key ] = value;
				}
			});
			if(month_or_quarter=="quarter"){
				if(one_dimension_answer[getQuarter(item["yyyymm"])]["count"]==null){
					one_dimension_answer[getQuarter(item["yyyymm"])]["count"]=0;
				}
				one_dimension_answer[getQuarter(item["yyyymm"])]["count"]++;
			}else{
				if(one_dimension_answer[item["yyyymm"].substr(4,2)]["count"]==null){
					one_dimension_answer[item["yyyymm"].substr(4,2)]["count"]=0;
				}
				one_dimension_answer[item["yyyymm"].substr(4,2)]["count"]++;
			}
		});
		
		console.log(one_dimension_answer);
		var answer_array=[];
		$.each(one_dimension_answer,function(key,obj) {
			obj["fst_name"] = fst_year+"年";
			obj["snd_name"] = snd_year+"年";
			obj["interval_name"] = key;
			answer_array.push(obj);
		});
		console.log(answer_array);
		console.log("=======");
		$.each(answer_array,function(i,item) {
			answer_array[i]["fst_title"] = "<table>"
				+(item["fst_name"]!=null?("<tr><td>發票開立時間：</td><td>" + item["fst_name"]+(month_or_quarter=="quarter"?"第"+item["interval_name"]+"季":item["interval_name"]+"月")+"</td></tr>"):"")
				+(item["fst_county_name"]==null?"":"<tr><td>發票開立縣市：</td><td>" + item["fst_county_name"]+"</td></tr>")
				+(item["fst_industry_category"]==null?"":"<tr><td>發票開立行業：</td><td>" + item["fst_industry_category"]+"</td></tr>")
				+(item["fst_quantity"]==null?"":"<tr><td>發票開立數量：</td><td>" + item["fst_quantity"]+"&nbsp;張</td></tr>")
				+(item["fst_quantity"]==null?"":"<tr><td>發票數量占比：</td><td>" + new Number((+item["fst_quantity"] * 100)  / (+d3.sum(data, function(d){return +d["quantity"];}))).toFixed(2)+" %</td></tr>")
				+(item["fst_amount"]==null?"":"<tr><td>發票開立總額：</td><td>" + item["fst_amount"]+"&nbsp;元</td></tr>")
				+(item["fst_amount"]==null?"":"<tr><td>發票總額占比：</td><td>" + new Number((+item["fst_amount"] * 100)  / (+d3.sum(data, function(d){return +d["amount"];}))).toFixed(2)+" %</td></tr>")
				+"</table>";
			answer_array[i]["snd_title"] = "<table>"
				+(item["snd_name"]!=null?("<tr><td>發票開立時間：</td><td>" + item["snd_name"]+(month_or_quarter=="quarter"?"第"+item["interval_name"]+"季":item["interval_name"]+"月")+"</td></tr>"):"")
				+(item["snd_county_name"]==null?"":"<tr><td>發票開立縣市：</td><td>" + item["snd_county_name"]+"</td></tr>")
				+(item["snd_industry_category"]==null?"":"<tr><td>發票開立行業：</td><td>" + item["snd_industry_category"]+"</td></tr>")
				+(item["snd_quantity"]==null?"":"<tr><td>發票開立數量：</td><td>" + item["snd_quantity"]+"&nbsp;張</td></tr>")
				+(item["snd_quantity"]==null?"":"<tr><td>發票數量占比：</td><td>" + new Number((+item["snd_quantity"] * 100)  / (+d3.sum(data, function(d){return +d["quantity"];}))).toFixed(2)+" %</td></tr>")
				+(item["snd_amount"]==null?"":"<tr><td>發票開立總額：</td><td>" + item["snd_amount"]+"&nbsp;元</td></tr>")
				+(item["snd_amount"]==null?"":"<tr><td>發票總額占比：</td><td>" + new Number((+item["snd_amount"] * 100)  / (+d3.sum(data, function(d){return +d["amount"];}))).toFixed(2)+" %</td></tr>")
				+"</table>";
		});
		
		var answer_array_reduce = [];
		answer_array.map( function (a) {
			var full_count = request_json_obj["compare_type"]=="quarter"?6:2;
			
			if(a["count"]==full_count){
				answer_array_reduce.push(a);
			}
		});
		answer_array_reduce.sort(function(a, b) {return a["interval_name"] - b["interval_name"];});
		return answer_array_reduce;
	}
	function prepare_pie_chart_data(json_obj){
		//之後如果有機會要改到java或SQL_sp處理的部份
		$.each(json_obj,function(i,item) {
			json_obj[i]["year"]=item["year"].replace("/","&nbsp;年&nbsp;")+"&nbsp;月";
		});
			
		var tmp_obj = $.parseJSON(JSON.stringify(json_obj));
		
		var tmp_item = null;
		
		var pie_chart_obj = [];
		$.each(tmp_obj,function(i,item) {
			
			tmp_obj[i]["name"]=item[
				request_json_obj["key-name"]
			];
			tmp_obj[i]["data"]=item["quantity"];
			tmp_obj[i]["percent"] = new Number((+item["quantity"] * 100)  /
					(+d3.sum(tmp_obj, function(d){return +d["quantity"];}))).toFixed(2)+" %";
			
			if(+tmp_obj[i]["percent"].replace("%","")>2.4){
				pie_chart_obj.push({
					"name":tmp_obj[i]["name"],
					"data":tmp_obj[i]["data"],
					"percent":tmp_obj[i]["percent"],
					"title":tmp_obj[i]["title"],
				});
			}else{
				if(tmp_item == null){
					tmp_item={
 						"name" : "" ,
 						"data" : "0" ,
 						"percent" : "" ,
 						"title" : "",
 						"count" : 0
 					}
				}
				tmp_item["name"] = "其他";
				tmp_item["data"] = (+tmp_item["data"])+(+tmp_obj[i]["data"]);
				tmp_item["percent"] = new Number((+tmp_item["data"] * 100)  /
						(+d3.sum(tmp_obj, function(d){return +d["quantity"];}))).toFixed(2)+" %";;
				if(tmp_item["count"]<5){
					tmp_item["title"] += "<br>" + tmp_obj[i]["title"];
				}else if(tmp_item["count"]==5){
					tmp_item["title"] += "<div>.</div><div>.</div><div>.</div><div>&nbsp;</div>";
				}
				tmp_item["count"]++;
			}
			
		});
		
		if(tmp_item != null){
			tmp_item["title"] = "<div>共含下列&nbsp;" + tmp_item["count"]+ "&nbsp;筆資料</div>" + tmp_item["title"];
			pie_chart_obj.push(tmp_item);
		}
		return pie_chart_obj;
	}
	</script>
</body>