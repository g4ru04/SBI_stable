//　
// 20170911 Ben
// 因捷運 交流道等 POI等太多項次 單一function不好maintain 故分開獨自POIjs
//
if(!window.all_markers){
	var all_markers={};
}

//POImethods.turn_off_or_not(choosed_node_key,this_node);
//POImethods.get_node_for_tree(choosed_node_key);

//POI[type]
function select_poi(poi_name,record,assign_border_north,assign_border_east,assign_border_south,assign_border_west){
	//,assign_lat,assign_lng,assign_zoom){
	//還沒有加refresh功能
	if(!window.choosed_node_key){
		choosed_node_key="-1";
	}
	console.log("##### Choosed: "+choosed_node_key+" #####");
	
	var border_north = assign_border_north!=null ? assign_border_north : new Number(map.getBounds().getNorthEast().lat()).toFixed(4);
	var border_south = assign_border_south!=null ? assign_border_south : new Number(map.getBounds().getSouthWest().lat()).toFixed(4);
	var border_east = assign_border_east !=null ? assign_border_east  : new Number(map.getBounds().getNorthEast().lng()).toFixed(4);
	var border_west = assign_border_west !=null ? assign_border_west  : new Number(map.getBounds().getSouthWest().lng()).toFixed(4);
	
	
	var this_node = POImethods.get_node_for_tree(choosed_node_key);
	
	if(POImethods.turn_off_or_not(choosed_node_key,this_node)){
		return ; 
	}else{
		this_node.span.childNodes[1].classList.add("loading");
		if(!this_node.span.childNodes[1].classList.contains("poi")){
			this_node.span.childNodes[1].classList.add("poi");
			this_node.span.childNodes[1].setAttribute("poi_node_key",choosed_node_key);
			this_node.span.childNodes[1].setAttribute("scenario_border_north",border_north);
			this_node.span.childNodes[1].setAttribute("scenario_border_south",border_south);
			this_node.span.childNodes[1].setAttribute("scenario_border_east",border_east);
			this_node.span.childNodes[1].setAttribute("scenario_border_west",border_west);
		}
	}
	
	var req_obj = {
		poi_name:poi_name,
		record:record,
		border_north:border_north,
		border_south:border_south,
		border_east:border_east,
		border_west:border_west
	}
	select_POI_AJAX(req_obj,this_node);
	
}

function select_POI_AJAX(req_obj,this_node){
	$.ajax({
		type : "POST",
		url : "POI.do",
		async : false,
		data : {
			action : "select_POI_by_typelist_bounds",
			typelist : req_obj["poi_name"],
			bound_north : req_obj["border_north"],
			bound_east : req_obj["border_east"],
			bound_south : req_obj["border_south"],
			bound_west : req_obj["border_west"]
		},
		success : function(result) {
			if(result=="fail!!!!!"){
				this_node.span.childNodes[1].classList.remove("loading");
				return;
			}
			var json_obj = $.parseJSON(result);
			$.each(json_obj,function(i, item){json_obj[i]=item["map"];});
			
			var result_table = "";
			all_markers[choosed_node_key]=[];
			if(json_obj.length>1000){
				if(confirm("搜尋資料量達"+json_obj.length+"筆\n是否繼續查詢?","確認繼續","取消")){}else{
					this_node.span.childNodes[1].classList.remove("loading");
					return;
				}
			}
				
			$.each(json_obj,function(i, item) {
				var marker = new google.maps.Marker({
				    position: {lat:+json_obj[i].lat,lng:+json_obj[i].lng},
				    title: json_obj[i].name,
				    map: map
				});
				if(json_obj[i].icon.length>3){
					var poi_icon_url = "./imageIcon.do?action=getPoiIconPath"
						+ "&pic_name="
						+ encodeURI(b64EncodeUnicode(json_obj[i].icon.replace("./refer_data/poi_icon/",""))).replace(/\+/g,'%2b');
					
					marker.setIcon({
						url: poi_icon_url,
						scaledSize: new google.maps.Size(30, 30)
					});
				}
				
				var poi_type ="normal";
				poi_type = (item.type =="捷運" && item.address.indexOf("高雄市") == -1 &&  item.name.indexOf("機場捷運") == -1) ? "metro" : poi_type;
				poi_type =  item.type=="交流道" ?"interchange":poi_type;
				
				var poi_item = new POI_ITEMs(json_obj[i],poi_type);
				var infowindow = poi_item["infowindows"];
				
				google.maps.event.addListener(marker, "click", function(event) {
					poi_item["prepareData"]();
					var infowindow_open = infowindow.getMap();
				    if(infowindow_open !== null && typeof infowindow_open !== "undefined"){
				    	infowindow.close();
				    }else{
				    	poi_item["infowindowsPrepare"](infowindow);
				    	infowindow.open(marker.get('map'), marker);
				    }
		        });
				
				poi_item["googlemapEvent"](marker,infowindow);
				all_markers[choosed_node_key].push(marker);
				
			});
			this_node.span.childNodes[1].classList.remove("loading");
		}
	});
}

//當做Object來使用
function POI_ITEMs(poiObj,poi_type){
	var POI_UUID = UUID();
	var weekday_select = '<select id="select_'+POI_UUID+'"><option value="0">星期日</option><option value="1">星期一</option><option value="2">星期二</option><option value="3">星期三</option><option value="4">星期四</option><option value="5">星期五</option><option value="6">星期六</option></select>';
	
	//以下捷運poi_item
	if(poi_type=="metro"){
		return {
			"POI_OBJ" : poiObj,
			"UUID" : POI_UUID,
			"data" : null,
			"infowindows" : 
				new google.maps.InfoWindow({ content:(
					'<table class="info_window keyValueData">'
					+'<tr><th colspan="2">'+poiObj["type"]+'　</th></tr>'
					+'<tr><td>名稱：</td><td style="text-align: left;">'+poiObj["name"]+'</td></tr>'
					+ show_if_significant(poiObj["address"],'<tr><td>地址：</td><td style="text-align: left;">#P#</td></tr>')
					+ show_if_significant(poiObj["subtype"],'<tr><td>類型：</td><td style="text-align: left;">#P#</td></tr>')
					+'<tr><td>人流：</td><td style="text-align: right;">'+weekday_select+'<div id="data_chart_'+POI_UUID+'"style="width:400px;height:200px"></div></td></tr>'
					+'</table>'
				)}
			),
			"prepareData":function(infowindow){
				if(this["data"]==null){
					this["data"] = prepare_metro_data(this["POI_OBJ"]["name"]);
				}
				this["chartConfig"]["yScale_construct"] = "d3.scale.linear().domain([0,"
					+d3.max(this["data"],function(d){
						return +d["flow_sum"]*1.1;
					})
					+"]).range([height - margin.top - margin.bottom,0])";
			},
			"infowindowsPrepare":function(infowindow){
				var this_item = this;
				if( $('#data_chart_'+this_item["UUID"]+' svg').length==0){
		    		google.maps.event.addListener(infowindow, 'domready', function() {
		    			$('#select_'+this_item["UUID"]).val(new Date().getDay());
		    			
			    		drawBarChart(
			    				this_item["data"].filter(function(d){
			    				return d["weekday"]==(new Date().getDay());
			    			}),
			    			this_item["chartConfig"]
			    		);
			    		$('#select_'+this_item["UUID"]).change(function(){
			    			drawBarChart(
			    					this_item["data"].filter(function(d){
			    					return d["weekday"]==$('#select_'+this_item["UUID"]).val();
			    				}),
			    				this_item["chartConfig"]
			    			);
			    		});
					});
		    	}
			},
			"googlemapEvent":function(){},
			"chartConfig":{
 				"elementID":"#data_chart_"+POI_UUID,
 				"svg_width":"380",
 				"svg_height":"180",
 				"svg_margin":{top:10,right:10,bottom:25	,left:10},
 				"bar_width": "10",
 				
 				"title_name":"",
 				"sub_title_name":"",
 				"xAxis_name":"",
 				"yAxis_name":null,
 				
 				"xAxis_index":"flow_hour",
 				"yAxis_index":"flow_sum",
 				
 				"xScale_construct":"d3.scale.linear().domain([-1,24]).range([0, width - margin.left - margin.right]);",
 				"yScale_construct":"d3.scale.linear().domain([0,"+"999"+"]).range([height - margin.top - margin.bottom,0])",
 				"fScale_construct":"fScale = function (str){color_cache = ['#7BAAF7','#5b9cd6','#ffce54','#4b6ca6','#de7310','#666699','#92d5ea','#5a3b16','#26a4ed','#f45a90'];if(!window.str_cache){window.str_cache=[];}if(str_cache.indexOf(str)==-1){str_cache.push(str);}return color_cache[str_cache.indexOf(str)];}",
 				
 				"xAxis_construct":"d3.svg.axis().scale(xScale).orient(\"bottom\").tickValues([3,6,9,12,15,18,21]).tickFormat(function(d){return d+\"時\";});",
 				"yAxis_construct":"d3.svg.axis().scale(yScale).orient(\"left\").tickFormat(function(d){return d;});"
 			}
		};

	//以下交流道poi_item
	}else if(poi_type=="interchange"){
		return {
			"POI_OBJ":poiObj,
			"UUID":POI_UUID,
			"data":null,
			"weekdaySelect":weekday_select,
			"infowindows":
				new google.maps.InfoWindow({content:(
					'<table class="info_window keyValueData">'
					+'<tr><th colspan="2">'+poiObj["type"]+'　</th></tr>'
					+'<tr><td>名稱：</td><td>'+poiObj["name"]+'</td></tr>'
					+ show_if_significant(poiObj["address"],'<tr><td>地址：</td><td>#P#</td></tr>')
					+ show_if_significant(poiObj["subtype"],'<tr><td>類型：</td><td>#P#</td></tr>')
					+'<tr><td>車流：</td><td style="text-align: right;">'+weekday_select+'<div id="data_chart_'+POI_UUID+'"style="width:400px;height:200px"></div></td></tr>'
					+'</table>'
				)})
			,
			"prepareData":function(){
				if(this["data"]==null){
					this["data"] = prepare_interchange_data(this["POI_OBJ"]["name"]);
					
					var weekday=[];
					this["data"].map(function(d){
						return d["the_day"];
					}).filter(function(value,index,self){
						return self.indexOf(value) === index;
					}).sort(function(a,b){
						return a>b;
					}).map(function(d){
						weekday[new Date(d).getDay()]=d;
					});
					this["data"] = this["data"].filter(function(d){
						return weekday.indexOf(d["the_day"])!=-1
					});
					
					this["chartConfig"]["yScale_construct"] = "d3.scale.linear().domain([0,"
						+d3.max(this["data"],function(d){
							return +d["flow_sum"]*1.1;
						})
						+"]).range([height - margin.top - margin.bottom,0])";
				}
			},
			"infowindowsPrepare":function(infowindow){
				
				var this_item = this;
				if( $('#data_chart_'+this_item["UUID"]+' svg').length==0){
		    		google.maps.event.addListener(infowindow, 'domready', function() {
		    			$('#select_'+this_item["UUID"]).val(new Date().getDay());
		    			
			    		drawBarChart(
			    				this_item["data"].filter(function(d){
			    				return d["weekday"]==(new Date().getDay());
			    			}),
			    			this_item["chartConfig"]
			    		);
			    		$('#select_'+this_item["UUID"]).change(function(){
			    			drawBarChart(
			    					this_item["data"].filter(function(d){
			    					return d["weekday"]==$('#select_'+this_item["UUID"]).val();
			    				}),
			    				this_item["chartConfig"]
			    			);
			    		});
					});
		    	}
				
			},
			"googlemapEvent":function(){
				
			},
			"chartConfig" : {
 				"elementID":"#data_chart_"+POI_UUID,
 				"svg_width":"380",
 				"svg_height":"180",
 				"svg_margin":{top:10,right:10,bottom:25	,left:10},
 				"bar_width": "10",
 				
 				"title_name":"",
 				"sub_title_name":"",
 				"xAxis_name":"",
 				"yAxis_name":null,
 				
 				"xAxis_index":"flow_hour",
 				"yAxis_index":"flow_sum",
 				
 				"xScale_construct":"d3.scale.linear().domain([-1,24]).range([0, width - margin.left - margin.right]);",
 				"yScale_construct":"d3.scale.linear().domain([0,"+"999"+"]).range([height - margin.top - margin.bottom,0])",
 				"fScale_construct":"fScale = function (str){color_cache = ['#7BAAF7','#5b9cd6','#ffce54','#4b6ca6','#de7310','#666699','#92d5ea','#5a3b16','#26a4ed','#f45a90'];if(!window.str_cache){window.str_cache=[];}if(str_cache.indexOf(str)==-1){str_cache.push(str);}return color_cache[str_cache.indexOf(str)];}",
 				
 				"xAxis_construct":"d3.svg.axis().scale(xScale).orient(\"bottom\").tickValues([3,6,9,12,15,18,21]).tickFormat(function(d){return d+\"時\";});",
 				"yAxis_construct":"d3.svg.axis().scale(yScale).orient(\"left\").tickFormat(function(d){return d;});"
 			}
		};
	
	//以下普通沒人流的poi_item
	}else if(poi_type=="normal"){
		return {
			"UUID":POI_UUID,
			"POI_OBJ":poiObj,
			"data":null,
			"weekdaySelect":"",
			"infowindows": 
				new google.maps.InfoWindow({content:(
					'<table class="info_window keyValueData">'
					+'<tr><th colspan="2">'+poiObj["type"]+'　</th></tr>'
					+'<tr><td>名稱：</td><td>'+poiObj["name"]+'</td></tr>'
					+ show_if_significant(poiObj["address"],'<tr><td>地址：</td><td>#P#</td></tr>')
					+ show_if_significant(poiObj["subtype"],'<tr><td>類型：</td><td>#P#</td></tr>')
					+'</table>'
				)})
			,
			"prepareData":function(infowindow){},
			"infowindowsPrepare":function(infowindow){},
			"googlemapEvent":function(marker,infowindow){
				google.maps.event.addListener(marker, "mouseout", function(event) {
					setTimeout(function () { infowindow.close(); }, 2500);
		        });
			},
			"chartConfig":{}
		};
	}else{
		console.log("assign_error??");
		return null;
	}
}

//準備交流道資料
function prepare_interchange_data(interchange_name){
	loading('now');
	var interchange_data;
	$.ajax({
		type : "POST",
		url : "POI.do",
		async : false,
		data : {
			"action":"select_flow_interchange",
			"interchange_name":interchange_name
		},
		success : function(result) {
			
			var json_obj = $.parseJSON(result);
			$.each(json_obj, function(i, item) {
				json_obj[i]["title"] = '<table><tbody>'
						+'<tr><td colspan="2">'+ item["modified_name"] + '</td></tr>'
						+'<tr><td>資料日期：</td><td>'+ item["the_day"]+ '</td></tr>'
						+'<tr><td>'+ item["flow_hour"] + '時車流：</td><td style="text-align:right;">'+ money_format(item["flow_sum"])+ '&nbsp;輛</td></tr>'
						+'</tbody></table>';
			});
			loading('over');
			
			interchange_data =  json_obj ;
			
		}
	});
	return interchange_data;
}

//準備捷運資料
function prepare_metro_data(metro_name){
	loading('now');
	console.log(loading_count);
	var metro_data;
	$.ajax({
		type : "POST",
		url : "POI.do",
		async : false,
		data : {
			"action":"select_flow_metro",
			"metro_name":metro_name
		},
		success : function(result) {
			
			var json_obj = $.parseJSON(result);
			
			$.each(json_obj,function(i, item) {
				json_obj[i]["title"]='<table><tbody><tr><td colspan="2">'+item["modified_name"]+'</td></tr>'
										+'<tr><td>資料日期：</td><td>'+item["the_day"]+'</td></tr>'
										+show_if_significant(item["weather_data"],'<tr><td>'+item["flow_hour"]+'時'+item["weather_data"].replace(":","：</td><td>").replace(":","：</td><td>").replace(",","</td></tr><tr><td>"+item["flow_hour"]+"時")+'</td></tr>')
										+'<tr><td>'+item["flow_hour"]+'時人流：</td><td>'+money_format(item["flow_sum"])+'&nbsp;人</td></tr>'
										+'</tbody></table>';
			});
			loading('over');
			metro_data =  json_obj ;
		}
	});
	return metro_data;
}


// 偽package 建POImethod //
var POImethods = {
	turn_off_or_not: function(choosed_node_key,this_node){
		return turn_off_or_not_implement(choosed_node_key,this_node);
	},
	get_node_for_tree : function(choosed_node_key){
		return get_node_for_tree_implement(choosed_node_key);
	}
}

function turn_off_or_not_implement(choosed_node_key,this_node){
	if(all_markers[choosed_node_key]!=null){
		for (var i = 0; i < all_markers[choosed_node_key].length; i++) {   
			all_markers[choosed_node_key][i].setMap(null);   
        }
		
		all_markers[choosed_node_key]=null;
		this_node.span.childNodes[1].classList.remove("loading");
		this_node.setActive(false);
		this_node.setSelected(false);
		return true;
	}
	return false;
}



function get_node_for_tree_implement(choosed_node_key){
	var this_node = null;
	if($("#tree").length>0 && choosed_node_key!=null && choosed_node_key!=-1){
		this_node = $('#tree').fancytree('getTree').getNodeByKey(choosed_node_key);
		this_node.setActive();
		this_node.setSelected(true);
	}else{
		this_node = {
			"setActive":function(){
				console.log("Node_not_found_setactive: "+choosed_node_key);
			},	
			"setSelected":function(){
				console.log("Node_not_found_setselect: "+choosed_node_key);
			},
			"span":{
				"childNodes":[{},{
					"setAttribute":function(){
						console.log("Node_not_found_attr: "+choosed_node_key);
					},
					"classList":{
						"add":function(){
							console.log("Node_not_found_add: "+choosed_node_key);
						},
						"remove":function(){
							console.log("Node_not_found_remove: "+choosed_node_key);
						},
						"contains":function(){
							console.log("Node_not_found_contains: "+choosed_node_key);
							return true;
						}
					}
				},{},{}]
			}
		}
	}
	return this_node;
}