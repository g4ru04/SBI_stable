//　此JS包含
//　0.google map的pan和zoom　
//　1.搜POI 比 type 的　
//　2.搜POI 比subtype的　
//　3.Menu商圈中畫商圈　
//　4~8,10,11.Menu中[開放資源,國家,城市,熱力圖,整體城市概況,中國省份行政分界 ,大麥克 ]
//　9.原SBI的四個function　
// 12,13[畫區位選擇,畫環域分析]
//　皆為畫googlemap相關功能

//googlemap panto zoom
if(!window.country_polygen){
	var country_polygen=[];
}
if(!window.chinaProvincial){
	var chinaProvincial=[];
}
if(!window.chinaCities){
	var chinaCities={};
}
if(!window.all_markers){
	var all_markers={};
}
if(!window.all_BDs){
	var all_BDs={};
}
if(!window.all_address){
	var all_address=[];
}
if(!window.population_Markers){
	var population_Markers=[];
}
if(!window.heatmap_layer){
	var heatmap_layer={};
}
function smoothZoom (map, max, cnt) {
	map.setZoom(max);
}
function panTo(newLat, newLng) {
	map.panTo( new google.maps.LatLng( newLat, newLng) );
}

//POI[type]
// 20170911 分開 已移至 mapFunctionEnvAnal.js裡
//function select_poi(){
//	已移至 mapFunctionEnvAnal.js裡
//}

//給熱力圖使用 查subtype的東西
function heatmap_poi(poi_name,record,assign_lat,assign_lng,assign_zoom) {
	
	if(heatmap_layer[poi_name]!=null){
		if($("#tree").length>0){
			$("#tree").fancytree("getTree").visit(function(node){
				if(node.title==poi_name){
					node.setSelected(false);
					this_node=node;
					$(this_node.span.childNodes[1]).addClass('loading');
				}
			});
		}
		heatmap_layer[poi_name].setMap(heatmap_layer[poi_name].getMap() ? null : map);
		heatmap_layer[poi_name]=null;
		$(this_node.span.childNodes[1]).removeClass('loading');
		return;
	}
	var this_node;
	
	if($("#tree").length>0){
		if(record!="no_record"){
			var sibling_node = $('#tree').fancytree('getTree').getSelectedNodes();
			sibling_node.forEach(function(sib_node) {
				if(sib_node.title==poi_name){
					this_node=sib_node;
				}
			});
		}else{
			$("#tree").fancytree("getTree").visit(function(node){
				if(node.title==poi_name){
					this_node=node;
					this_node.setActive();
					this_node.setSelected(true);
				}
			});
		}
		
		if(this_node!=null){
			$(this_node.span.childNodes[1]).addClass('loading');
			if(!$(this_node.span.childNodes[1]).hasClass('heat_map')){
				$(this_node.span.childNodes[1]).addClass('heat_map');
				$(this_node.span.childNodes[1]).attr('scenario_lat',new Number((assign_lat!=null?assign_lat:map.getCenter().lat())).toFixed(4));
				$(this_node.span.childNodes[1]).attr('scenario_lng',new Number((assign_lng!=null?assign_lng:map.getCenter().lng())).toFixed(4));
				$(this_node.span.childNodes[1]).attr('scenario_zoom',(assign_zoom!=null?assign_zoom:map.getZoom()));
			}
		}
	}
	$.ajax({
		type : "POST",
		url : "realMap.do",
//		async : false,
		data : {
			action : "select_poi_2",
			name : poi_name,
			lat : (assign_lat!=null?assign_lat:map.getCenter().lat()),
			lng : (assign_lng!=null?assign_lng:map.getCenter().lng()),
			zoom : (assign_zoom!=null?assign_zoom:map.getZoom())
		},
		success : function(result) {
			var json_obj = $.parseJSON(result);
			var point_array=[];
			$.each(json_obj,function(i, item) {
				point_array.push(new google.maps.LatLng(item.center.lat, item.center.lng));
			});

			var heatmap = new google.maps.visualization.HeatmapLayer({
			    data: point_array,
			    map: map
			});
			heatmap.set('radius', 20);
			heatmap_layer[poi_name]=heatmap;
			if(this_node!=null){
				if($("#tree").length>0){
					$(this_node.span.childNodes[1]).removeClass('loading');
				}
			}
		}
	});
}

//畫BD
function select_BD(BD_name,record){
	
	if(all_BDs[BD_name]!=null){
		for (var i = 0; i < all_BDs[BD_name].length; i++) {
			all_BDs[BD_name][i].setMap(null);   
        }   
		all_BDs[BD_name]=null;
		return;
	}
	if($("#tree").length>0){
		var this_node;
		if(record!="no_record"){
			var sibling_node = $('#tree').fancytree('getTree').getSelectedNodes();
			sibling_node.forEach(function(sib_node) {
				if(sib_node.title==BD_name){
					this_node=sib_node;
				}
			});
		}else{
			$("#tree").fancytree("getTree").visit(function(node){
				if(node.title==BD_name){
					this_node=node;
					this_node.setActive();
					this_node.setSelected(true);
				}
			});
		}
		
		$(this_node.span.childNodes[1]).addClass('loading');
		if(!$(this_node.span.childNodes[1]).hasClass('BD')){
			$(this_node.span.childNodes[1]).addClass('BD');
		}
	}
	$.ajax({
		type : "POST",
		url : "realMap.do",
		data : {
			action : "select_BD",
			name : BD_name
		},
		success : function(result) {
			var json_obj = $.parseJSON(result);
			var result_table = "";
			if($("#tree").length>0){
				all_BDs[BD_name]=[];
			}
			$.each(json_obj,function(i, item) {
				if(record!="no_record"){
					map.panTo(new google.maps.LatLng(json_obj[i].lat,json_obj[i].lng));
					smoothZoom(map, 15, map.getZoom());
				} 
				var bermudaTriangle = new google.maps.Polygon({
					paths: json_obj[i].center,
					strokeColor: '#FF0000',
					strokeOpacity: 0.8,
					strokeWeight: 2,
					fillColor: '#FF0000',
					fillOpacity: 0.1
				});
				bermudaTriangle.setMap(map);
				var isChinaCity = ['天津市','瀋陽市','煙臺市','成都市','重慶市','武漢市','鄭州市','西安市','青島市','唐山市','濟南市'].indexOf(json_obj[i].city)!=-1;
				var tmp_table="";
				if(isChinaCity){
					$.ajax({
						type : "POST",
						url : "realMap.do",
						async : false,
						data : {
							action : "get_ChinaCity_Data",
							name : json_obj[i].city
						},success : function(result2) {
							var json_obj2 = $.parseJSON(result2);
							tmp_table='<table class="info_window keyValueData">'+
								'<tr class="title"><th colspan="2"><br>'+json_obj[i].BD_name+'　</th></tr>'+
								'<tr><td align="right">城市：</td><td>' + json_obj[i].city + '</td></tr>'+
								'<tr><td align="right">商業營業面積：</td><td>' + json_obj[i].area + '</td></tr>'+
	                        	'<tr><td align="right">人流與顧客結構：</td><td>' + json_obj[i].population + '</td></tr>'+
	                        	'<tr><td align="right">市場地位：</td><td>' + json_obj[i].status + '</td></tr>'+
	                        	'<tr><td align="right">商業輻射範圍：</td><td>' + json_obj[i].radiation + '</td></tr>'+
	                        	'<tr><td align="right">商圈通達性：</td><td>' + json_obj[i].traffic + '</td></tr>'+
	                        	
	                        	'<tr class="title"><th colspan="2"><br>消費發展潛力</th></tr>'+
	                        	'<tr><td align="right">常住人口增長率：</td><td>' + (json_obj[i].resident == '' ? '-' : (json_obj[i].resident + ' %')) + '</td></tr>'+
	                        	'<tr><td align="right">人均可支配所得增長率：</td><td>' + (json_obj[i].income == '' ? '-' : (json_obj[i].income + ' %')) + '</td></tr>'+
	                        	'<tr class="title"><th colspan="2"><br>市場消費規模</th></tr>'+
	                        	'<tr><td align="right">人均可支配收入：</td><td>' + (json_obj[i].revenue == '' ? '-' : (json_obj[i].revenue + ' RMB/年')) + '</td></tr>'+
	                        	'<tr><td align="right">家庭人均消費支出：</td><td>' + (json_obj[i].expenditur == '' ? '-' : (json_obj[i].expenditur + ' RMB/年')) + '</td></tr>'+
	                        	'<tr><td colspan="2" >'+
	                        	"<a target='_blank' href=\"http://live.amcharts.com/" + json_obj2.link1 + "/embed/\">交通便利性</a>"+
	                        	"<a target='_blank' href=\"http://live.amcharts.com/" + json_obj2.link2 + "/embed/\">日均客流量(人次)</a>"+
	                        	"<a target='_blank' href=\"http://live.amcharts.com/" + json_obj2.link3 + "/embed/\">設施吸引力(家數)</a>"+
	                        	"<a target='_blank' href=\"http://live.amcharts.com/" + json_obj2.link4 + "/embed/\">設施吸引力(密度)</a>"+
	                        	'</td></tr>'+
	                        	'<tr class="title"><th colspan="2"><br>經營成本</th></tr>'+
	                            '<tr><td align="right">臨街平均租金：</td><td>' + (json_obj[i].nearstreet == '' ? '-' : (json_obj[i].nearstreet + ' RMB/平方米/月')) + '</td></tr>'+
	                            '<tr><td align="right">百貨商場平均租金：</td><td>' + (json_obj[i].dept_store == '' ? '-' : (json_obj[i].dept_store + ' RMB/平方米/月')) + '</td></tr>'+                 
	                            '<tr><td align="right">職工平均工資：</td><td>' + (json_obj[i].working_po == '' ? '-' : (json_obj[i].working_po + ' RMB/年')) + '</td></tr>'+
	                            '<tr><td align="right">五險一金：</td><td>' + (json_obj[i].risk_5 == '' ? '-' : (json_obj[i].risk_5 + ' %')) + '</td></tr>'+
	                            '<tr class="title"><th colspan="2"><br>業種業態組成</th></tr>'+
	                            '<tr><td colspan="2">'+
	                            "<a target='_blank' href=\"http://live.amcharts.com/" + json_obj2.link5 + "/embed/\">業種組成</a>"+
	                            "<a target='_blank' href=\"http://live.amcharts.com/" + json_obj2.link6 + "/embed/\">業態組成</a>"+
	                            '</td></tr>'+
	                            '<tr class="title"><th colspan="2"><br>商圈競爭狀況</th></tr>'+
	                            '<tr><td colspan="2">'+
	                            "<a target='_blank' href=\"http://live.amcharts.com/" + json_obj2.link7 + "/embed/\">競爭優勢-同業種店家數</a>"+
	                            "<a target='_blank' href=\"http://live.amcharts.com/" + json_obj2.link8 + "/embed/\">知名店家數</a>"+
	                            '</td></tr>'+
	                            '</table>';
						}
					});
				}else{
					tmp_table='<table class="info_window keyValueData">'+
						'<tr><th colspan="2">'+json_obj[i].BD_name+'　</th></tr>'+
						(json_obj[i].city.length<2?"":'<tr><td>城市：</td><td>'+json_obj[i].city+'</td></tr>')+
						(json_obj[i].area.length<2?"":'<tr><td>區域：</td><td>'+json_obj[i].area+'</td></tr>')+
						(json_obj[i].population.length<2?"":'<tr><td>人流：</td><td>'+json_obj[i].population+'</td></tr>')+
						(json_obj[i].status.length<2?"":'<tr><td>地位：</td><td>'+json_obj[i].status+'</td></tr>')+
						(json_obj[i].radiation.length<2?"":'<tr><td>輻射：</td><td>'+json_obj[i].radiation+'</td></tr>')+
						(json_obj[i].traffic.length<2?"":'<tr><td>通達：</td><td>'+json_obj[i].traffic+'</td></tr>')+
						(json_obj[i].business_cost.length<2?"":'<tr><td>經營成本：</td><td>'+json_obj[i].business_cost+'</td></tr>')+
						'</table>';
//					console.log(json_obj[i].BD_name+"\t"+(json_obj[i].city.length<2?"X":"O")+"\t"+(json_obj[i].area.length<2?"X":"O")+"\t"+(json_obj[i].population.length<2?"X":"O")+"\t"+(json_obj[i].status.length<2?"X":"O")+"\t"+(json_obj[i].radiation.length<2?"X":"O")+"\t"+(json_obj[i].traffic.length<2?"X":"O")+"\t"+(json_obj[i].business_cost.length<2?"X":"O"));
				}
				var infowindow = new google.maps.InfoWindow({content:tmp_table});
		        var infoMarker = new google.maps.Marker({
		            position: new google.maps.LatLng(json_obj[i].lat,json_obj[i].lng),
		            icon: {
		                path: google.maps.SymbolPath.CIRCLE,
		                scale: 0
		            },
		            map: map
		        });
		        
		        var update_timeout = null;
				google.maps.event.addListener(bermudaTriangle, "click", function(event) { 
					 update_timeout = setTimeout(function(){
						 
						if( isEnvAnalDivOpen()){
							google.maps.event.trigger(map, 'click',event);
						}else{
							infowindow.open(bermudaTriangle.get('map'), infoMarker);
						}
					}, 200);
					
		        });
				
				google.maps.event.addListener(bermudaTriangle, "dblclick", function(event) { 
					clearTimeout(update_timeout);
					infowindow.open(bermudaTriangle.get('map'), infoMarker);
		        });
				google.maps.event.addListener(infowindow, "closeclick", function () {
		            infoMarker.setMap(null);
		        });
				if($("#tree").length>0){
					all_BDs[BD_name].push(bermudaTriangle);
				}
			});
			if($("#tree").length>0){
				$(this_node.span.childNodes[1]).removeClass('loading');
			}
		}
	});
}

// 4.開放資源功能
function country_POLY_for_country_economy (year,type){
	if(!realMapData_have_initial){
		panTo( 8.0, 112.0);
		smoothZoom(map, 4, map.getZoom());
		realMapData_have_initial=1;
	}
	
	var polygen = country_polygen.pop();
	while(polygen != null){
		if (Wkt.isArray(polygen)) {
		       for (i in polygen) {
		           if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
		           	polygen[i].setMap(null);
		       }
		    }
		} else {
		   	polygen.setMap(null);
		}
		polygen = country_polygen.pop();
	}
	$.ajax({
		type : "POST",
		url : "countryEconomy.do",
		data : {
			action : "draw_shpLegend",
			type : type,
			year : year
		},success : function(msg) {
			var arrMsg = msg.split('|');
            TILE = arrMsg[0].split(',');
			$('#span_level1').text(' ~ ' + TILE[0]);
            for (var i = 0; i < 4; i++) {
                $('#span_level' + (i + 2)).text(TILE[i] + ' ~ ' + TILE[i + 1]);
            }
            $('#span_level5').text(TILE[3] + ' ~ ');
		}
	});
	
	$.ajax({
		type : "POST",
		url : "countryEconomy.do",
		async : false,
		data : {
			action : "change_select",
			year : year,
			type : type
		},
		success : function(result) {
			var json_obj = $.parseJSON(result);
			var timer, change_to_click_mode=0;;
			$.each(json_obj,function(i, item) {
				var wkt = new Wkt.Wkt();
				wkt.read(json_obj[i].geom);
	            var config = {
	                fillColor: '#F0F0F0',
	                strokeColor: '#5C5C5C',
	                fillOpacity: 0.7,
	                strokeOpacity: 1,
	                strokeWeight: 1,
	            }
	            var _data = json_obj[i].economy_detail_statistic;
                switch (true) {
                    case +_data <= +TILE[0]:
                        config.fillColor = "#41A85F";
                        break;
                    case +_data > +TILE[0] && +_data <= +TILE[1]:
                        config.fillColor = "#8db444";
                        break;
                    case +_data > +TILE[1] && +_data <= +TILE[2]:
                        config.fillColor = "#FAC51C";
                        break;
                    case +_data > +TILE[2] && +_data <= +TILE[3]:
                        config.fillColor = "#d87926";
                        break;
                    case +_data > +TILE[3]:
                        config.fillColor = "#B8312F";
                        break;
                }
                var msg = "<table>"
                + "<tr><td align='center'>國家：</td><td align='center'>" + json_obj[i].country_name + "</td></tr>"
                + "<tr><td align='center'>" + $('#span_legend').text() + "：</td><td align='center'>" + json_obj[i].economy_detail_statistic + "</td></tr>"
                +"</table>";
                
	            var polygen = wkt.toObject(config);
	            if (Wkt.isArray(polygen)) {
	                for (i in polygen) {
	                    if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
	                    	polygen[i].setMap(map);
	                    	google.maps.event.addListener(polygen[i], 'mouseover', function () {
	                    		if(!change_to_click_mode){
		                    		clearTimeout(timer);
		                    		detail_1.innerHTML = '<div style="width: 380px; height: 230px;">' + msg + '</div>';
	                    		}
	                    	});
	                    	google.maps.event.addListener(polygen[i], 'mouseout', function () {
	                    		if(!change_to_click_mode){
	                    			timer = setTimeout(function(){ detail_1.innerHTML = ''; }, 1500);
	                    		}
	                    	});
	                    	google.maps.event.addListener(polygen[i], 'click', function () {
	                    		change_to_click_mode=1;
	                    		setTimeout(function(){ change_to_click_mode=0; }, 300000);
	                    		detail_1.innerHTML = '<div style="width: 380px; height: 230px;">' + msg + '</div>';
	                    	});
	                    }
	                }
	            } else {
	            	polygen.setMap(map);
	            	google.maps.event.addListener(polygen, 'mouseover', function () {
	            		if(!change_to_click_mode){
	                		clearTimeout(timer);
	                		detail_1.innerHTML = '<div style="width: 380px; height: 230px;">' + msg + '</div>';
	            		}
                	});
                	google.maps.event.addListener(polygen, 'mouseout', function () {
                		if(!change_to_click_mode){
                			timer = setTimeout(function(){ detail_1.innerHTML = ''; }, 1500);
                		}
                	});
                	google.maps.event.addListener(polygen, 'click', function () {
                		change_to_click_mode=1;
                		setTimeout(function(){ change_to_click_mode=0; }, 300000);
                		detail_1.innerHTML = '<div style="width: 380px; height: 230px;">' + msg + '</div>';
                		
                	});
	            }
	            country_polygen.push(polygen);
			});
			
            $("#shpLegend").show();
            var _height = $('#shpLegend div').height();
            if (_height > 0) {
                $('#shpLegend').height(_height);
            }
		}
    });
}

function country_economy(node,type){
	if(!node.isSelected()){
	 	var polygen = country_polygen.pop();
	 	while(polygen != null){
	 		if (Wkt.isArray(polygen)) {
	 		       for (i in polygen) {
	 		           if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
	 		           	polygen[i].setMap(null);
	 		       }
	 		    }
	 		} else {
	 		   	polygen.setMap(null);
	 		}
	 		polygen = country_polygen.pop();
	 	}
	 	$("#shpLegend").hide();
	 	return;
	}

	if(!$(node.span.childNodes[1]).hasClass('diagrammap')){
		$(node.span.childNodes[1]).addClass('diagrammap');
	}
	var sibling_node = $('#tree').fancytree('getTree').getSelectedNodes();
	sibling_node.forEach(function(sib_node) {
		if($(sib_node.span).length>0){
			if($(sib_node.span.childNodes[1]).hasClass('diagrammap')){
				sib_node.setSelected(false);
			}
		}
	});
	node.setSelected(true);
	$(node.span.childNodes[1]).addClass('loading');
	$('#span_legend').text(node.title);
    $('#tr_year').show();
    $('#ddl_year').empty();
    var ddl_year = document.getElementById('ddl_year');
    var _select = document.createElement('select');
    ddl_year.appendChild(_select);
    if(window.scenario_record){scenario_record("參閱資料","開放資源圖層　>　CountryEconomy　>　"+type);} 
	$.ajax({
		type : "POST",
		url : "countryEconomy.do",
		data : {
			action : "draw_shpLegend",
			type : type
		},
		success : function(msg) {
			
			if (msg !== undefined) {
                var arrMsg = msg.split('|');

                TILE = arrMsg[0].split(',');
                var MIN = arrMsg[1];
                var MAX = arrMsg[2];
                var UNIT = arrMsg[3];
                var YEAR = arrMsg[4].split(',');

                for (var i = 0; i < YEAR.length; i++) {
                    var option = document.createElement('option');
                    option.value = YEAR[i];
                    option.text = YEAR[i];
                    _select.appendChild(option);
                }
                $('#span_unit').text(UNIT);

                $('#span_level1').text(' ~ ' + TILE[0]);
                for (var i = 0; i < 4; i++) {
                    $('#span_level' + (i + 2)).text(TILE[i] + ' ~ ' + TILE[i + 1]);
                }
                $('#span_level5').text(TILE[3] + ' ~ ');
                country_POLY_for_country_economy(YEAR[0],type);
                _select.addEventListener('change', function () {
                	country_POLY_for_country_economy(_select.value,type);
                });
                $(node.span.childNodes[1]).removeClass('loading');
    			return;
            }
		}
	});
}

//5.國家功能
var realMapData_have_initial=0;
function country_POLY_for_countryData (year,type){
	loading('now');
	if(!realMapData_have_initial){
		panTo( 20.39, 65.57);
		smoothZoom(map, 2, map.getZoom());
		realMapData_have_initial=1;
	}
	var polygen = country_polygen.pop();
	while(polygen != null){
		if (Wkt.isArray(polygen)) {
		       for (i in polygen) {
		           if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
		           	polygen[i].setMap(null);
		       }
		    }
		} else {
		   	polygen.setMap(null);
		}
		polygen = country_polygen.pop();
	}
	
	$.ajax({
		type : "POST",
		url : "countryData.do",
		data : {
			action : "get_country_data",
			type : type,
			year : year
		},success : function(msg) {
			var arrMsg = msg.split('|');
            TILE = arrMsg[0].split(',');
			$('#span_level1').text(' ~ ' + TILE[0]);
            for (var i = 0; i < 4; i++) {
                $('#span_level' + (i + 2)).text('　' + TILE[i] + ' ~ ' + TILE[i + 1]);
            }
            $('#span_level5').text('　' + TILE[3] + ' ~ ');
            
            var result_obj = $.parseJSON(arrMsg[1]);
            var change_to_click_mode=0;
            $.each(result_obj,function(i, item) {
            	var timer;
            	var wkt = new Wkt.Wkt();
            	var map_corr_local= null;
            	
            	if(!window.map_corr){
            		map_corr = {};
            	} 
            	if(map_corr[result_obj[i]["Country"]]==null){
            		var search_country = result_obj[i]["Country"];
            		$.ajax({
                		type : "POST",
                		url : "realMap.do",
                		async : false,
                		data : {
                			action : "prepareGeometryOne",
                			country : search_country
                		},success : function(result){
                			var json_obj = $.parseJSON(result);
                			map_corr[search_country]="";
        					map_corr_local = "";
                			$.each(json_obj, function(j, jtem){
                				if(jtem["CNTRY_NAME"]==search_country){
                					map_corr[search_country]=jtem["geom"];
                					map_corr_local = jtem["geom"];
                				}
            				});
                		}
            		});
            	}else if(map_corr[result_obj[i]["Country"]]==""){
            		console.log("查無此國家地理資料: " + result_obj[i]["Country"]);
            		return ;
            	}else{
            		map_corr_local = map_corr[ result_obj[i]["Country"] ];
            	}
            	
            	if(map_corr_local ==""){
		    		console.log("查無此國家地理資料: " + result_obj[i]["Country"]);
		    		return ;
		    	}
            		
				wkt.read(
					map_corr_local
				);
				var color_list = ["#41A85F","#8db444","#FAC51C","#d87926","#B8312F"];
				var count=0;
				for(count=0;count<4;count++){
					if( +result_obj[i]["Data"] < +TILE[count] ){
						break;
					}
				}
				
				var msg = "<div style='width: 380px; height: 230px;'>" 
					+"<table>"
	                + "<tr><td align='center'>國家：</td><td align='center'>" + result_obj[i]["Country"] + "</td></tr>"
	                + "<tr><td align='center'>"+ $('#span_legend').text() +"：</td><td align='center'>" +
	                (
	                	result_obj[i]["Data"].indexOf(".")!=-1
	                	?(
                			result_obj[i]["Data"]>1000
                			?result_obj[i]["Data"].split(".")[0]
	                		:new Number(result_obj[i]["Data"]).toFixed(2)
	                	)
	                	:result_obj[i]["Data"]
					)
					+ "</td></tr>"
	                +"</table>"
	                +"</div>";
				
				var polygen = wkt.toObject({
					fillColor: color_list[count],
	                strokeColor: '#5C5C5C',
	                fillOpacity: 0.7,
	                strokeOpacity: 1,
	                strokeWeight: 1,
				});
	            
				var this_polygen_setting = function(this_polygen){
					this_polygen.setMap(map);
                	google.maps.event.addListener(this_polygen, 'mouseover', function () {
                		if(!change_to_click_mode){
                    		clearTimeout(timer);
                    		detail_1.innerHTML = msg ;
                		}
                	});
                	google.maps.event.addListener(this_polygen, 'mouseout', function () {
                		if(!change_to_click_mode){
                			timer = setTimeout(function(){ detail_1.innerHTML = ''; }, 1500);
                		}
                	});
                	google.maps.event.addListener(this_polygen, 'click', function () {
                		change_to_click_mode=1;
                		setTimeout(function(){ change_to_click_mode=0; }, 300000);
                		detail_1.innerHTML = msg ;
                	});
                	country_polygen.push(polygen);
				};
				
	            if (Wkt.isArray(polygen)) {
	                for (i in polygen) {
	                    if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
	                    	this_polygen_setting(polygen[i]);
	                    }
	                }
	            } else {
	            	this_polygen_setting(polygen);
	            }
			});
			
            $("#shpLegend").show();
            var _height = $('#shpLegend div').height();
            if (_height > 0) {
                $('#shpLegend').height(_height);
            }
			loading('over');
		}
	});
}


function countryData(node,type){//country_data
	loading('now');
	type = ( type == "Inflation Rates (Annual);" ? "Inflation Rates (Annual)" : type ) ;
	type = ( type == "Tax and Social Security Contributions per Capita (Current);" ? "Tax and Social Security Contributions per Capita (Current)" : type ) ;
	
	if(!node.isSelected()){
	 	var polygen = country_polygen.pop();
	 	while(polygen != null){
	 		if (Wkt.isArray(polygen)) {
	 		       for (i in polygen) {
	 		           if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
	 		           	polygen[i].setMap(null);
	 		       }
	 		    }
	 		} else {
	 		   	polygen.setMap(null);
	 		}
	 		polygen = country_polygen.pop();
	 	}
	 	$("#shpLegend").hide();
	 	loading('over');
	 	return;
	}

	if(!$(node.span.childNodes[1]).hasClass('diagrammap')){
		$(node.span.childNodes[1]).addClass('diagrammap');
	}
	
	var sibling_node = $('#tree').fancytree('getTree').getSelectedNodes();
	sibling_node.forEach(function(sib_node) {
		if($(sib_node.span).length>0){
			if($(sib_node.span.childNodes[1]).hasClass('diagrammap')){
				sib_node.setSelected(false);
			}
		}
	});
	
	node.setSelected(true);
	$(node.span.childNodes[1]).addClass('loading');
	
    if(window.scenario_record){scenario_record("參閱國家資料","國家　>　"+node.parent.title+"　>　"+type);}
    
	$.ajax({
		type : "POST",
		url : "countryData.do",
		data : {
			action : "get_country_data_disyear",
			type : type
		},
		success : function(msg) {
			$('#span_legend').text(node.title);
		    $('#tr_year').show();
		    $('#ddl_year').empty();
		    
		    var ddl_year = document.getElementById('ddl_year');
		    var _select = document.createElement('select');
		    ddl_year.appendChild(_select);
			if (msg !== undefined) {
				var YEAR = msg.split('|')[0].split(',');
				var UNIT = msg.split('|')[1];
                for (var i = 0; i < YEAR.length; i++) {
                    var option = document.createElement('option');
                    option.value = YEAR[i];
                    option.text = YEAR[i];
                    _select.appendChild(option);
                }
                $('#span_unit').text(UNIT);

                country_POLY_for_countryData(YEAR[0],type);
                _select.addEventListener('change', function () {
                	country_POLY_for_countryData(_select.value , type);
                });
            }
			$(node.span.childNodes[1]).removeClass('loading');
			loading('over');
		}
	});
}

//6.城市功能 
function country_POLY_for_chinaCity (type){//country_polygen
	if(!realMapData_have_initial){
		panTo( 35.0, 100.0);
		smoothZoom(map, 4, map.getZoom());
		realMapData_have_initial=1;
	}
	var polygen = country_polygen.pop();
	while(polygen != null){
		if (Wkt.isArray(polygen)) {
		       for (i in polygen) {
		           if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
		           	polygen[i].setMap(null);
		       }
		    }
		} else {
		   	polygen.setMap(null);
		}
		polygen = country_polygen.pop();
	}
	
	$.ajax({
		type : "POST",
		url : "chinaCity.do",
		async : false,
		data : {
			action : "change_select",
			//year : year,
			type : type
		},
		success : function(result) {
			var json_obj = $.parseJSON(result);
			var timer;
			var change_to_click_mode=0;
			$.each(json_obj,function(i, item) {
				var wkt = new Wkt.Wkt();
				wkt.read(json_obj[i].geom);
	            var config = {
	                fillColor: '#F0F0F0',
	                strokeColor: '#5C5C5C',
	                fillOpacity: 0.7,
	                strokeOpacity: 1,
	                strokeWeight: 1,
	            }
	            var _data = json_obj[i].data;
                switch (true) {
                    case +_data <= +TILE[0]:
                        config.fillColor = "#41A85F";
                        break;
                    case +_data > +TILE[0] && +_data <= +TILE[1]:
                        config.fillColor = "#8db444";
                        break;
                    case +_data > +TILE[1] && +_data <= +TILE[2]:
                        config.fillColor = "#FAC51C";
                        break;
                    case +_data > +TILE[2] && +_data <= +TILE[3]:
                        config.fillColor = "#d87926";
                        break;
                    case +_data > +TILE[3]:
                        config.fillColor = "#B8312F";
                        break;
                }
                var msg = "<table>"
                	+ "<tr><td align='center'>國家：</td><td align='center'>" + json_obj[i].country_name + "</td></tr>"
                	+ "<tr><td align='center'>"+ $('#span_legend').text() +"：</td><td align='center'>" + json_obj[i].data + "</td></tr>"
                	+"</table>";
	            var polygen = wkt.toObject(config);
	            if (Wkt.isArray(polygen)) {
	                for (i in polygen) {
	                    if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
	                    	polygen[i].setMap(map);
	                    	google.maps.event.addListener(polygen[i], 'mouseover', function () {
	    	            		if(!change_to_click_mode){
	    	                		clearTimeout(timer);
	    	                		detail_1.innerHTML = '<div style="width: 380px; height: 230px;">' + msg + '</div>';
	    	            		}
	                    	});
	                    	google.maps.event.addListener(polygen[i], 'mouseout', function () {
	                    		if(!change_to_click_mode){
	                    			timer = setTimeout(function(){ detail_1.innerHTML = ''; }, 1500);
	                    		}
	                    	});
	                    	google.maps.event.addListener(polygen[i], 'click', function () {
	                    		change_to_click_mode=1;
	                    		setTimeout(function(){ change_to_click_mode=0; }, 300000);
	                    		detail_1.innerHTML = '<div style="width: 380px; height: 230px;">' + msg + '</div>';
	                    	});
	                    	
	                    	
	                    }
	                }
	            } else {
	            	polygen.setMap(map);
	            	google.maps.event.addListener(polygen, 'mouseover', function () {
	            		if(!change_to_click_mode){
	                		clearTimeout(timer);
	                		detail_1.innerHTML = '<div style="width: 380px; height: 230px;">' + msg + '</div>';
	            		}
                	});
                	google.maps.event.addListener(polygen, 'mouseout', function () {
                		if(!change_to_click_mode){
                			timer = setTimeout(function(){ detail_1.innerHTML = ''; }, 1500);
                		}
                	});
                	google.maps.event.addListener(polygen, 'click', function () {
                		change_to_click_mode=1;
                		setTimeout(function(){ change_to_click_mode=0; }, 300000);
                		detail_1.innerHTML = '<div style="width: 380px; height: 230px;">' + msg + '</div>';
                	});
	            }
				
	            country_polygen.push(polygen);
			});
			
            $("#shpLegend").show();
            var _height = $('#shpLegend div').height();
            if (_height > 0) {
                $('#shpLegend').height(_height);
            }
		}
    });
}

function chinaCity(node,type){//chinaCity
	if(!node.isSelected()){
	 	var polygen = country_polygen.pop();
	 	while(polygen != null){
	 		if (Wkt.isArray(polygen)) {
	 		       for (i in polygen) {
	 		           if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
	 		           	polygen[i].setMap(null);
	 		       }
	 		    }
	 		} else {
	 		   	polygen.setMap(null);
	 		}
	 		polygen = country_polygen.pop();
	 	}
	 	$("#shpLegend").hide();
	 	return;
	}

	if(!$(node.span.childNodes[1]).hasClass('diagrammap')){
		$(node.span.childNodes[1]).addClass('diagrammap');
	}
	var sibling_node = $('#tree').fancytree('getTree').getSelectedNodes();
	sibling_node.forEach(function(sib_node) {
		if($(sib_node.span).length>0){
			if($(sib_node.span.childNodes[1]).hasClass('diagrammap')){
				sib_node.setSelected(false);
			}
		}
	});
	node.setSelected(true);
	if(window.scenario_record){scenario_record("參閱城市資料","城市　>　"+node.parent.title+"　>　"+type);} 
	$(node.span.childNodes[1]).addClass('loading');
	$('#span_legend').text(node.title);
    $('#tr_year').hide();
	$.ajax({
		type : "POST",
		url : "chinaCity.do",
		data : {
			action : "draw_shpLegend",
			type : type
		},
		success : function(msg) {
			
			if (msg !== undefined) {
                var arrMsg = msg.split('|');
                TILE = arrMsg[0].split(',');
                var MIN = arrMsg[1];
                var MAX = arrMsg[2];
                var UNIT = arrMsg[3];
                $('#span_unit').text(UNIT);

                $('#span_level1').text(' ~ ' + TILE[0]);
                for (var i = 0; i < 4; i++) {
                    $('#span_level' + (i + 2)).text(TILE[i] + ' ~ ' + TILE[i + 1]);
                }
                $('#span_level5').text(TILE[3] + ' ~ ');
                country_POLY_for_chinaCity(type);
            }
			$(node.span.childNodes[1]).removeClass('loading');
		}
	});
	
}
//7.商圈中熱力圖  
function country_POLY_for_heatMap (type){
	var polygen = country_polygen.pop();
	while(polygen != null){
		if (Wkt.isArray(polygen)) {
		       for (i in polygen) {
		           if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
		           	polygen[i].setMap(null);
		       }
		    }
		} else {
		   	polygen.setMap(null);
		}
		polygen = country_polygen.pop();
	}
	
	
	$.ajax({
		type : "POST",
		url : "heatMap.do",
		async : false,
		data : {
			action : "change_select",
			type : type
		},
		success : function(result) {
			var json_obj = $.parseJSON(result);
			$.each(json_obj,function(i, item) {
				var wkt = new Wkt.Wkt();
				wkt.read(json_obj[i].geom);
	            var config = {
	                fillColor: '#F0F0F0',
	                strokeColor: '#5C5C5C',
	                fillOpacity: 0.7,
	                strokeOpacity: 1,
	                strokeWeight: 1,
	            }
	            var _data = json_obj[i].data;
              switch (true) {
                  case +_data <= +TILE[1]:
                      config.fillColor = "#41A85F";
                      break;
                  case +_data > +TILE[1] && +_data <= +TILE[2]:
                      config.fillColor = "#8db444";
                      break;
                  case +_data > +TILE[2] && +_data <= +TILE[3]:
                      config.fillColor = "#FAC51C";
                      break;
                  case +_data > +TILE[3] && +_data <= +TILE[4]:
                      config.fillColor = "#d87926";
                      break;
                  case +_data > +TILE[4]:
                      config.fillColor = "#B8312F";
                      break;
              }
	            var polygen = wkt.toObject(config);
	            if (Wkt.isArray(polygen)) {
	                for (i in polygen) {
	                    if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
	                    	polygen[i].setMap(map);
	                    }
	                }
	            } else {
	            	polygen.setMap(map);
	            }
				
	            country_polygen.push(polygen);
			});
			
          $("#shpLegend").show();
          var _height = $('#shpLegend div').height();
          if (_height > 0) {
              $('#shpLegend').height(_height);
          }
		}
  });
}

function heatMap(node,type){
	if(!node.isSelected()){
	 	var polygen = country_polygen.pop();
	 	while(polygen != null){
	 		if (Wkt.isArray(polygen)) {
	 		       for (i in polygen) {
	 		           if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
	 		           	polygen[i].setMap(null);
	 		       }
	 		    }
	 		} else {
	 		   	polygen.setMap(null);
	 		}
	 		polygen = country_polygen.pop();
	 	}
	 	$("#shpLegend").hide();
	 	return;
	}
	if(!$(node.span.childNodes[1]).hasClass('heatmap')){
		$(node.span.childNodes[1]).addClass('heatmap');
	}
	var sibling_node = $('#tree').fancytree('getTree').getSelectedNodes();
	sibling_node.forEach(function(sib_node) {
		if($(sib_node.span).length>0){
			if($(sib_node.span.childNodes[1]).hasClass('heatmap')){
				sib_node.setSelected(false);
			}
		}
	});
	node.setSelected(true);
	$(node.span.childNodes[1]).addClass('loading');
	$('#span_legend').text(node.title);
	$('#tr_year').hide();
	if(window.scenario_record){scenario_record("參閱熱力圖資料",type);} 
	$.ajax({
		type : "POST",
		url : "heatMap.do",
		data : {
			action : "draw_shpLegend",
			type : type
		},
		success : function(msg) {
			if (msg !== undefined) {
              var arrMsg = msg.split('|');
              TILE = arrMsg[0].split(',');
              var UNIT = arrMsg[3];
              $('#span_unit').text("數量");
              for (var i = 0; i < 5; i++) {
                  $('#span_level' + (i + 1)).text(TILE[i] + ' ~ ' + TILE[i + 1]);
              }
              country_POLY_for_heatMap(type);
          }
			$(node.span.childNodes[1]).removeClass('loading');
		}
	});
	
}

//8.城市整體概況[那11個城市各別用到而已]
function country_POLY_for_city (node,city_name){
	if(!node.isSelected()){
		var polygen = chinaCities[city_name].pop();
		while(polygen != null){
			if (Wkt.isArray(polygen)) {
			       for (i in polygen) {
			           if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
			           	polygen[i].setMap(null);
			       }
			    }
			} else {
			   	polygen.setMap(null);
			}
			polygen = chinaCities[city_name].pop();
		}
	 	return;
	}
	$(node.span.childNodes[1]).addClass('loading');
	loading('now');
	if(window.scenario_record){scenario_record("參閱中國省份資料",city_name);} 
	$.ajax({
		type : "POST",
		url : "chinaprovincial.do",
		data : {
			action : "selectall_SHP_City",
			city :  city_name
		},
		success : function(result) {

			var json_obj = $.parseJSON(result);
			$.each(json_obj,function(i, item) {
				var wkt = new Wkt.Wkt();
				wkt.read(json_obj[i].geom);
				var config = {
                    fillColor: '#7092BE',
                    strokeColor: '#3F48CC',
                    fillOpacity: 0.5,
                    strokeOpacity: 1,
                    strokeWeight: 1,
                }
	            
	            var polygen = wkt.toObject(config);
	            if (Wkt.isArray(polygen)) {
	                for (i in polygen) {
	                    if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
	                    	polygen[i].setMap(map);
	                    }
	                }
	            } else {
	            	polygen.setMap(map);
	            }
	            chinaCities[city_name]=[];
	            chinaCities[city_name].push(polygen);
	            
	            var tmp_table='<table class="info_window">'+
				'<tr><th colspan="2">'+json_obj[i].country_name+'　</th></tr>'+
				(json_obj[i].living==null?"":'<tr><td>常住人口：</td><td>'+json_obj[i].living+'萬人</td></tr>')+
				(json_obj[i].household==null?"":'<tr><td>戶籍人口：</td><td>'+json_obj[i].household+'萬人</td></tr>')+
				(json_obj[i].male==null?"":'<tr><td>男性：</td><td>'+json_obj[i].male+'%</td></tr>')+
				(json_obj[i].female==null?"":'<tr><td>女性：</td><td>'+json_obj[i].female+'%</td></tr>')+
				'</table>';
				
				var infowindow = new google.maps.InfoWindow({content:tmp_table});
		        var infoMarker = new google.maps.Marker({
		            position: new google.maps.LatLng(json_obj[i].cY,json_obj[i].cX),
		            icon: {
		                path: google.maps.SymbolPath.CIRCLE,
		                scale: 0
		            },
		            map: map
		        });
				google.maps.event.addListener(polygen, "click", function(event) { 
		        	infowindow.open(polygen.get('map'), infoMarker);
		        });
				google.maps.event.addListener(infowindow, "closeclick", function () {
		            infoMarker.setMap(null);
		        });
			});
			$(node.span.childNodes[1]).removeClass('loading');
			loading('over');
		}
    });
}

//9. 人口結構資料*3 + 畫圓餅圖
function draw_population_data(node,type){
	if(!node.isSelected()){
		if (population_Markers) {
            for (i in population_Markers) {
            	population_Markers[i].setMap(null);
            }
            population_Markers.length = 0;
        }
		return ;
	}
	if(!$(node.span.childNodes[1]).hasClass('populationData')){
		$(node.span.childNodes[1]).addClass('populationData');
	}
	var sibling_node = $('#tree').fancytree('getTree').getSelectedNodes();
	sibling_node.forEach(function(sib_node) {
		if($(sib_node.span).length>0){
			if($(sib_node.span.childNodes[1]).hasClass('populationData')){
				sib_node.setSelected(false);
			}
		}
	});
	node.setSelected(true);
	$(node.span.childNodes[1]).addClass('loading');
	
	if (population_Markers) {
        for (i in population_Markers) {
        	population_Markers[i].setMap(null);
        }
        population_Markers.length = 0;
    }
	if(window.scenario_record){scenario_record("人口資料","國家　>　人口結構　>　人口數　>　"+type);}
	
	type = (type=="Gender"?"Gender":type);
	type = (type=="Countryage"?"Age":type);
	type = (type=="CountryLaborForce"?"CountryLaborForce":type);
	
	$.ajax({
		type : "POST",
		url : "countryData.do",
		data : {
			action : "get_country_one_data_byTarget",
			type : type
		},success : function(result) {
			var json_obj = $.parseJSON(result);
			var group_by_country={};
			$.each(json_obj,function(i, item) {
				if(group_by_country[item["Country"]]==null){
					group_by_country[item["Country"]]={
						"Aged 0-14":"0",
						"Aged 15-64":"0",
						"Aged 65+":"0",
						"Males":"0",
						"Females":"0"
					};
					group_by_country[item["Country"]]["lat"]=item["latitude"];
					group_by_country[item["Country"]]["lng"]=item["longitude"];
					
				}
				var record_list = ["Aged 0-14","Aged 15-64","Aged 65+","Males","Females"];
				
				if(record_list.indexOf(item["Second_Target"])!=-1){
					group_by_country[item["Country"]][
						item["Second_Target"]
					] = item["Data"].split(".")[0];
				}
			});
			$.each(group_by_country,function(key, item) {
				var count=0;
				$.each(item,function(){count++;});
				if( type == "Age" ){
					SetAgeMarker(key, item.lat, item.lng, item["Aged 0-14"], item["Aged 15-64"],item["Aged 65+"]);
				}else if( type == "Gender" ){
					SetPieTwoMarker(key, item.lat, item.lng, item["Males"], item["Females"], "男性", "女性", "性別人口數");
				}else if( type == "CountryLaborForce" ){
					SetPieTwoMarker(key, item.lat, item.lng, item["Males"], item["Females"], "男性", "女性", "性別人口數");
				}
			});
			$(node.span.childNodes[1]).removeClass('loading');
		}
	});
	
}

function SetPieTwoMarker(country, lat, lng, data1, data2, data1_desc, data2_desc, type) {
    var LatLng = new google.maps.LatLng(lat, lng);

    var data1_percent = (data1 - 0) / ((data1 - 0) + (data2 - 0));
    var data2_percent = (data2 - 0) / ((data1 - 0) + (data2 - 0));
    var image = {
        url: "http://chart.apis.google.com/chart?cht=p&chbh=a&chco=0000ff&chd=t:" + data1_percent + "," + data2_percent + "&chs=50x50&chf=bg,s,ffffff00",
        size: new google.maps.Size(150, 70),
        origin: new google.maps.Point(0, 0),
        anchor: new google.maps.Point(0, 0)
    };
    var marker = new google.maps.Marker({
        position: LatLng,
        map: map,
        icon: image,
        title: country
    });

    if (data1 == null)
        data1 = "";
    else
        data1 = data1 - 0;
    if (data2 == null)
        data2 = "";
    else
        data2 = data2 - 0;

    var msg = "<table><caption>" + type + " (" + country + ")</caption>"
            + "<tr><td align='center'>" + data1_desc + "</td><td align='center'>"+data2_desc +"</td></tr>"
            + "<tr><td align='center'>" + data1 + "&nbsp;千人</td><td align='center'>" + data2 + "&nbsp;千人</td></tr></table>";

    SetMarkerAttribute(marker, country, LatLng, msg);

    population_Markers.push(marker);
}
function SetMarkerAttribute(marker, city, LatLng, msg) {
    var infowindow = new google.maps.InfoWindow({
        content: '<div style="width: 320px; height: 230px;">' + city + "</br></br>" + msg + '</div>',
        position: LatLng,
        disableAutoPan: true
    });
    
    google.maps.event.addListener(marker, 'mouseover', function () {
        detail_1.innerHTML = '<div style="width: 320px; height: 230px;">' + msg + '</div>';
    });
    google.maps.event.addListener(marker, 'mouseout', function () {
        detail_1.innerHTML = '';
    });
}

function SetAgeMarker(country, lat, lng, under14, between1564, up65) {
    var LatLng = new google.maps.LatLng(lat, lng);

    var total = (under14 - 0) + (between1564 - 0) + (up65 - 0);
    var percent_14 = (under14 - 0) / total;
    var percent_1564 = (between1564 - 0) / total;
    var percent_65 = (up65 - 0) / total;

    var image = {
        url: "http://chart.apis.google.com/chart?cht=p&chbh=a&chco=0000ff&chd=t:" + percent_14 + "," + percent_1564 + "," + percent_65 + "&chs=150x65&chl=14↓|15-64|65↑&chf=bg,s,ffffff00",
        size: new google.maps.Size(150, 70),
        origin: new google.maps.Point(0, 0),
        anchor: new google.maps.Point(0, 0)
    };
    var marker = new google.maps.Marker({
        position: LatLng,
        map: map,
        icon: image,
        title: country
    });

    var msg = "<table><caption>年齡人口數 (" + country + ")</caption>"
            + "<tr><td align='center'>未滿14歲</td><td align='center'>15-64歲</td><td align='center'>高於65歲</td></tr>"
            + "<tr><td align='center'>" + under14 + "&nbsp;千人</td><td align='center'>" + between1564 + "&nbsp;千人</td><td align='center'>" + up65 + "&nbsp;千人</td></tr></table>";
    SetMarkerAttribute(marker, country, LatLng, msg);
    population_Markers.push(marker);
}

//10.中國省份行政分界 
function country_POLY_for_chinaProvincial(node){
	if(!node.isSelected()){
		var polygen = chinaProvincial.pop();
		while(polygen != null){
			if (Wkt.isArray(polygen)) {
			       for (i in polygen) {
			           if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
			           	polygen[i].setMap(null);
			       }
			    }
			} else {
			   	polygen.setMap(null);
			}
			polygen = chinaProvincial.pop();
		}
		return ;
	}
	if(window.scenario_record){scenario_record("中國行政區劃分","城市　>　中國行政區劃分");} 
	$(node.span.childNodes[1]).addClass('loading');
	$.ajax({
		type : "POST",
		url : "chinaprovincial.do",
		data : {
			action : "selectall_SHP_ChinaProvincial",
		},
		success : function(result) {
			panTo( 35.99498458547868,97.060791015625 );smoothZoom(map, 4, map.getZoom());
			var json_obj = $.parseJSON(result);
			$.each(json_obj,function(i, item) {
				var wkt = new Wkt.Wkt();
				wkt.read(json_obj[i].geom);
				var config = {
                    fillColor: '#7092BE',
                    strokeColor: '#3F48CC',
                    fillOpacity: 0.5,
                    strokeOpacity: 1,
                    strokeWeight: 1,
                }
	            var polygen = wkt.toObject(config);
				if (Wkt.isArray(polygen)) {
	                for (i in polygen) {
	                    if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
	                    	polygen[i].setMap(map);
	                    }
	                }
	            } else {
	            	polygen.setMap(map);
	            }
				chinaProvincial.push(polygen);
			});
			$(node.span.childNodes[1]).removeClass('loading');
		}
	});
}
//11大麥克 
function bigmac(node){
	loading('now');
	if(!node.isSelected()){
	 	var polygen = country_polygen.pop();
	 	while(polygen != null){
	 		if (Wkt.isArray(polygen)) {
	 		       for (i in polygen) {
	 		           if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
	 		           	polygen[i].setMap(null);
	 		       }
	 		    }
	 		} else {
	 		   	polygen.setMap(null);
	 		}
	 		polygen = country_polygen.pop();
	 	}
	 	$("#shpLegend").hide();
	 	loading('over');
	 	return;
	}
	if(!realMapData_have_initial){
		panTo( 28.0, 130.0);
		smoothZoom(map, 2, map.getZoom());
		realMapData_have_initial=1;
	}
	
 	var polygen = country_polygen.pop();
 	while(polygen != null){
 		if (Wkt.isArray(polygen)) {
 		       for (i in polygen) {
 		           if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
 		           	polygen[i].setMap(null);
 		       }
 		    }
 		} else {
 		   	polygen.setMap(null);
 		}
 		polygen = country_polygen.pop();
 	}
 	$("#shpLegend").hide();
	
	if(!$(node.span.childNodes[1]).hasClass('diagrammap')){
		$(node.span.childNodes[1]).addClass('diagrammap');
	}
	var sibling_node = $('#tree').fancytree('getTree').getSelectedNodes();
	sibling_node.forEach(function(sib_node) {
		if($(sib_node.span).length>0){
			if($(sib_node.span.childNodes[1]).hasClass('diagrammap')){
				sib_node.setSelected(false);
			}
		}
	});
	node.setSelected(true);
	$(node.span.childNodes[1]).addClass('loading');
	if(window.scenario_record){scenario_record("大麥克指數","國家　>　經濟結構　>　世界主要國家_大麥克指數");} 
	$.ajax({
		type : "POST",
		url : "countryData.do",
		async : false,
		data : {
			action : "bigmac_select",
		},
		success : function(result) {
			var json_obj = $.parseJSON(result);
			var TILE = [];
			var mac_price_array=[];
			var timer, change_to_click_mode=0;
			$.each(json_obj,function(i, item) {
				mac_price_array.push(+json_obj[i].price);
			});
			mac_price_array.sort();
			for(var i=0;i<4;i++){
				TILE[i]=+mac_price_array[Math.round(mac_price_array.length/5)*(i+1)];
			}
			 $('#span_level1').text(' ~ ' + TILE[0]);
             for (var i = 0; i < 4; i++) {
                 $('#span_level' + (i + 2)).text(TILE[i] + ' ~ ' + TILE[i + 1]);
             }
             $('#span_level5').text(TILE[3] + ' ~ ');
             $('#span_legend').text("大麥克指數");
             $('#span_unit').text("大麥克售價比");
             $('#tr_year').hide();
             
			$.each(json_obj,function(i, item) {
				var wkt = new Wkt.Wkt();
				wkt.read(json_obj[i].geom);
	            var config = {
	            	fillColor: ("hsla(" + Math.floor(Math.random()*360) + ", 85%, 50%, 0.8)"),
	                strokeColor: '#5C5C5C',
	                fillOpacity: 0.7,
	                strokeOpacity: 1,
	                strokeWeight: 1,
	            }
	            var _data = json_obj[i].price;
	            switch (true) {
	                case +_data <= +TILE[0]:
	                    config.fillColor = "#41A85F";
	                    break;
	                case +_data > +TILE[0] && +_data <= +TILE[1]:
	                    config.fillColor = "#8db444";
	                    break;
	                case +_data > +TILE[1] && +_data <= +TILE[2]:
	                    config.fillColor = "#FAC51C";
	                    break;
	                case +_data > +TILE[2] && +_data <= +TILE[3]:
	                    config.fillColor = "#d87926";
	                    break;
	                case +_data > +TILE[3]:
	                    config.fillColor = "#B8312F";
	                    break;
	            }
	            
	            var msg = "<table><caption>大麥克指數 (" + json_obj[i].country_name + ")</caption>"
	            + "<tr><td align='center'>大麥克售價：</td><td align='center'>" + json_obj[i].price + "</td></tr>"
	            + "<tr><td align='center'>高/低估比率：</td><td align='center'>" + json_obj[i].rawIndex + "</td></tr>"
	            + "<tr><td align='center'>實際匯率：</td><td align='center'>" + json_obj[i].actualExchangeRate + "</td></tr>"
	            + "<tr><td align='center'>隱含匯率：</td><td align='center'>" + json_obj[i].impliedExchangeRate + "</td></tr>"
	            +"</table>";
	            var polygen = wkt.toObject(config);
	            if (Wkt.isArray(polygen)) {
	                for (i in polygen) {
	                    if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
	                    	polygen[i].setMap(map);
	                    	
	                    	google.maps.event.addListener(polygen[i], 'mouseover', function () {
	                    		if(!change_to_click_mode){
		                    		clearTimeout(timer);
		        	                detail_1.innerHTML = '<div style="width: 320px; height: 230px;">' + msg + '</div>';
	                    		}
	        	            });
	        	            google.maps.event.addListener(polygen[i], 'mouseout', function () {
	        	            	if(!change_to_click_mode){
	        	            		timer = setTimeout(function(){ detail_1.innerHTML = ''; }, 1500);
	        	            	}
	        	            });
	        	            google.maps.event.addListener(polygen[i], 'click', function () {
	        	            	change_to_click_mode=1;
	        	            	setTimeout(function(){ change_to_click_mode=0; }, 300000);
	        	            	detail_1.innerHTML = '<div style="width: 320px; height: 230px;">' + msg + '</div>';
	        	            });
	                    }
	                }
	            } else {
	            	polygen.setMap(map);
	            	google.maps.event.addListener(polygen, 'mouseover', function () {
                		if(!change_to_click_mode){
                    		clearTimeout(timer);
        	                detail_1.innerHTML = '<div style="width: 320px; height: 230px;">' + msg + '</div>';
                		}
    	            });
    	            google.maps.event.addListener(polygen, 'mouseout', function () {
    	            	if(!change_to_click_mode){
    	            		timer = setTimeout(function(){ detail_1.innerHTML = ''; }, 1500);
    	            	}
    	            });
    	            google.maps.event.addListener(polygen, 'click', function () {
    	            	change_to_click_mode=1;
    	            	setTimeout(function(){ change_to_click_mode=0; }, 300000);
    	            	detail_1.innerHTML = '<div style="width: 320px; height: 230px;">' + msg + '</div>';
    	            });
	            }
	            country_polygen.push(polygen);
			});
			$("#shpLegend").show();
	        $(node.span.childNodes[1]).removeClass('loading');
	        loading('over');
		}
	});
}

//12.帶出區位選擇結果
function draw_region_select(polydiagram){
	var json_obj = eval(polydiagram);
	$.each(json_obj[12],function(i, item) {
		var BD_name=item['City'].replace("商圈","")+"商圈";
    	if(item['City']=="新板"){BD_name="新板特區商圈";}		
    	$.ajax({
    		type : "POST",
    		url : "realMap.do",
    		async : false,
    		data : {
    			action : "select_BD",
    			name : BD_name
    		},
    		success : function(result) {
    			var json_obj = $.parseJSON(result);
    			var timer;
    			$.each(json_obj,function(j, item) {
    				var tmp_table='<table class="info_window">'+
					'<tr><th colspan="2">'+json_obj[j].BD_name+'　</th></tr>'+
					(json_obj[j].city.length<2?"":'<tr><td>城市：</td><td>'+json_obj[j].city+'</td></tr>')+
					(json_obj[j].area.length<2?"":'<tr><td>區域：</td><td>'+json_obj[j].area+'</td></tr>')+
					(json_obj[j].population.length<2?"":'<tr><td>人流：</td><td>'+json_obj[j].population+'</td></tr>')+
					(json_obj[j].status.length<2?"":'<tr><td>地位：</td><td>'+json_obj[j].status+'</td></tr>')+
					(json_obj[j].radiation.length<2?"":'<tr><td>輻射：</td><td>'+json_obj[j].radiation+'</td></tr>')+
					(json_obj[j].traffic.length<2?"":'<tr><td>通達：</td><td>'+json_obj[j].traffic+'</td></tr>')+
					(json_obj[j].business_cost.length<2?"":'<tr><td>經營成本：</td><td>'+json_obj[j].business_cost+'</td></tr>')+
					'</table>';
					var infowindow2 = new google.maps.InfoWindow({content:tmp_table});
    				var infowindow = new google.maps.InfoWindow({content: ("<div style='padding:6px;'>區位選擇 - 第"+(i+1)+"名<br><a style='font-size:16px;'>"+BD_name+"</a></div>")});
	    			var bermudaTriangle = new google.maps.Polygon({
						paths: json_obj[j].center,
						strokeColor: '#FF0000',
						strokeOpacity: 0.8,
						strokeWeight: 2,
						fillColor: '#FF0000',
						fillOpacity: 0.1
					});
					bermudaTriangle.setMap(map);
					var marker = new google.maps.Marker({
					    position: new google.maps.LatLng( json_obj[j].lat, json_obj[j].lng),
						icon: (location.href.replace(location.href.split("/").pop(),"")+"images/envMarkers/rsMarker-"+(i+1+"")+".png"),
					    title: json_obj[j].BD_name,
					    map: map
					});
					var mouseover_listener = google.maps.event.addListener(marker, "mouseover", function(event) { 
				    	infowindow.open(map, marker);
				    	clearTimeout(timer);
				    });
					
					google.maps.event.addListener(marker, "click", function(event) { 
						google.maps.event.removeListener(mouseover_listener);
						infowindow2.open(map, marker);
				    });
					
					google.maps.event.addListener(marker, "mouseout", function(event) { 
				    	timer = setTimeout(function () { infowindow.close(); }, 1500);
				    });
					google.maps.event.addListener(infowindow, "closeclick", function(event) { 
						marker.setMap(null);
						bermudaTriangle.setMap(null);
						infowindow.setMap(null);
				    });
					
					google.maps.event.addListener(bermudaTriangle, "click", function(event) {
						if(isEnvAnalDivOpen()){
							google.maps.event.trigger(map, 'click',event);
						}else{
							infowindow2.open(marker.get('map'), marker);
						}
					});
    			});
    		}
		});
	});
}
//13.帶出環域分析結果
function draw_env_analyse(points){
	var json_obj = eval(points);
	$.each(json_obj,function(i, item) {
		if(i!=0){
			var order = item[0].replace("點","");
			var google_latlng = new google.maps.LatLng( item[1], item[2]);
			var infowindow,rs_marker,rs_circle,timer;
	//		infowindow = new google.maps.InfoWindow({content: ("<div style='padding:10px;'><table><tr><td>環域分析 - 點"+order+"<br>時速："+item[4]+"<br>時間："+item[5]+"</td><td><img src='./images/POIdelete.png' class='func' style='height:22px;' onclick='rs_marker.setMap(null);rs_circle.setMap(null);infowindow.setMap(null);alert();'></td></tr></div>")});
			infowindow = new google.maps.InfoWindow({content: ("<div style='padding:6px;'>環域分析 - 點"+order+"<br>時速："+item[4]+"<br>時間："+item[5]+"</div>")});
			rs_marker = new google.maps.Marker({
			    position: google_latlng,
			    animation: google.maps.Animation.DROP,
//			    icon: 'http://maps.google.com/mapfiles/kml/paddle/' + order + '.png',
			    icon: (location.href.replace(location.href.split("/").pop(),"")+"images/envMarkers/envMarker-"+order+".png"),
			    map: map,
			    draggable:false,
			    title: ("--分析點"+order+"--")
			});
			rs_circle = new google.maps.Circle({
				  strokeColor: '#FF0000',
				  strokeOpacity: 0.5,
				  strokeWeight: 2,
				  fillColor: '#FF8700',
				  fillOpacity: 0.2,
				  map: map,
				  center: google_latlng,
				  radius: parseInt(item[3].replace("m",""),10)
			});
			if(window.rs_markers){
				var marker_obj = new item_marker( order,parseInt(item[4].replace("km/hr","")), parseInt(item[5].replace("mins","")), rs_marker, rs_circle,item[1],item[2]);
//		        rs_markers.push(marker_obj);
			
//				google.maps.event.addListener(rs_marker, "click", function(event) { 
//					$.each(rs_markers, function(i, node){
//						rs_markers[i].marker.setAnimation(null);
//					});
//					$("#rr_pt").css("font-size","38px");
//					setTimeout(function(){$("#rr_pt").css("font-size","16px");},1000);
//					$("#rr_pt").css("color","red");
//					setTimeout(function(){$("#rr_pt").css("color","black");},1000);
//					$("#rr_pt").html(order);
//					$("#rr_pt").val(marker_obj);
//					$("#speed").val(marker_obj.speed);
//					$("#time").val(marker_obj.time);
//					$('#val_time').html("花費"+marker_obj.time+"分鐘");
//					$('#val_speed').html("時速"+marker_obj.speed+"公里");
//					$('#slider').slider('option', 'value', marker_obj.time);
//		        }); 
		        
//			    google.maps.event.addListener(rs_marker, 'drag', function(marker){
//			    	rs_circle.setCenter(marker.latLng);
//			    });
			    
//			    google.maps.event.addListener(rs_marker, 'dragstart', function(marker){
//			    	rs_marker.setAnimation(null);
//			    	$.each(rs_markers, function(i, node){
//						rs_markers[i].marker.setAnimation(null);
//					});
//					$("#rr_pt").css("font-size","38px");
//					setTimeout(function(){$("#rr_pt").css("font-size","16px");},1000);
//					$("#rr_pt").css("color","red");
//					setTimeout(function(){$("#rr_pt").css("color","black");},1000);
//					$("#rr_pt").html(order);
//					$("#rr_pt").val(marker_obj);
//					$("#speed").val(marker_obj.speed);
//					$("#time").val(marker_obj.time);
//					$('#slider').slider('option', 'value', marker_obj.time);
//			    });
			}
			
			google.maps.event.addListener(rs_marker, "mouseover", function(event) { 
		    	infowindow.open(map, rs_marker);
		    	clearTimeout(timer);
		    });
			google.maps.event.addListener(rs_marker, "mouseout", function(event) { 
		    	timer = setTimeout(function () { infowindow.close(); }, 1500);
		    });
//			google.maps.event.addListener(infowindow, "closeclick", function(event) { 
//				rs_marker.setMap(null);
//				rs_circle.setMap(null);
//				infowindow.setMap(null);
//		    });
			google.maps.event.addListener(rs_circle, "click", function(event) {
				if(isEnvAnalDivOpen()){
					google.maps.event.trigger(map, 'click',event);
				}
//				$("div").each(function(){
//					if($(this).attr("id")!=null && $(this).attr("id").indexOf("region_select")!=-1){
//						if($(this).dialog("isOpen")){
//							google.maps.event.trigger(map, 'click',event);
//						}
//					}
//				});
			});
		}
	});
	return ;
}

function draw_address(search_str){
	var poi_amount=0;
	console.log(map.getCenter().lat);
	$.ajax({
		type : "POST",
		url : "realMap.do",
		async : false,
		data : {
			action : "select_poi",
			name : search_str,
			lat : map.getCenter().lat,
			lng : map.getCenter().lng,
			zoom : map.getZoom()
		},success : function(result) {
			var json_obj = $.parseJSON(result);
			if(json_obj.length>0){
				poi_amount=json_obj.length;
				select_poi(search_str,"no_record");
			}
		}
	});
	if(poi_amount!=0){
		return;
	}
	
	new google.maps.places.AutocompleteService().getPlacePredictions({
	    input: search_str,
	    offset: search_str.length
	}, function listentoresult(list, status) {
		var place_to_latlng_service = new google.maps.places.PlacesService(map);
		place_to_latlng_service.getDetails({ 
			placeId: list[0].place_id
		}, function(place, status) {
			if (status == google.maps.places.PlacesServiceStatus.OK) {
				address = [
				    (place.address_components[0] && place.address_components[0].short_name || ''),
				    (place.address_components[1] && place.address_components[1].short_name || ''),
				    (place.address_components[2] && place.address_components[2].short_name || '')
				  ].join(' ');
				all_address.push(list[0].description);
				var infowindow = new google.maps.InfoWindow({
					content: ("<div style='padding:6px;'><strong>" + place.name + "</strong><br>" + address+"</div>")
				});
				var marker = new google.maps.Marker({
				    position: place.geometry.location,
				    animation: google.maps.Animation.DROP,
				    icon: null,
				    map: map,
				    draggable:true,
				});
				map.setCenter(place.geometry.location);
				
				if(!isEnvAnalDivOpen()){
					map.fitBounds(place.geometry.viewport);
				}
				marker.setVisible(false);
				infowindow.open(map, marker);
				google.maps.event.addListener(infowindow, "closeclick", function () {
					marker.setMap(null);
					infowindow.setMap(null);
					if (all_address.indexOf(list[0].description)>-1){
						all_address.splice(all_address.indexOf(list[0].description), 1);
					}
		        });
			}
		});
	});
}

function b64EncodeUnicode(str) {
    return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, function(match, p1) {
        return String.fromCharCode('0x' + p1);
    }));
}

function b64DecodeUnicode(str) {
    return decodeURIComponent(Array.prototype.map.call(atob(str), function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
}
//這個function還沒用到 可以用到的地方是 realmap.jsp POI.jsp scenario_draw_env
//function a_new_env_marker(env_lat,env_lng,order,info_msg,env_v,env_t){
//	
//	var google_latlng = new google.maps.LatLng( env_lat, env_lng);
//	var rs_marker = new google.maps.Marker({
//	    position: google_latlng,
//	    animation: google.maps.Animation.DROP,
//	    icon: 'http://maps.google.com/mapfiles/kml/paddle/' + order + '.png',
//	    map: map,
//	    draggable:true,
//	    title: ("--分析點"+order+"--")
//	});
//	var rs_circle = new google.maps.Circle({
//		  strokeColor: '#FF0000',
//		  strokeOpacity: 0.5,
//		  strokeWeight: 2,
//		  fillColor: '#FF8700',
//		  fillOpacity: 0.2,
//		  map: map,
//		  center: google_latlng,
//		  radius: parseInt(item[3].replace("m",""),10)
//	});
//}


//20170905 Ben 
//使用方式  1.map_focus() 2.map_focus().clear()
//可能過幾天一併要把map整理整理 像common那樣
function map_focus(border_north,border_east,border_south,border_west){
	
	border_north = border_north!=null ? border_north : new Number(map.getBounds().getNorthEast().lat()).toFixed(4);
	border_south = border_south!=null ? border_south : new Number(map.getBounds().getSouthWest().lat()).toFixed(4);
	border_east = border_east !=null ? border_east  : new Number(map.getBounds().getNorthEast().lng()).toFixed(4);
	border_west = border_west !=null ? border_west  : new Number(map.getBounds().getSouthWest().lng()).toFixed(4);
	
	if(!window.map_focus_arr){
		map_focus_arr=[];
	}
	while(map_focus_arr.length>0){
		map_focus_arr.pop().setMap(null);
	}
	
	var points_x =[+border_east,+border_west];
	var points_y =[+border_north,+border_south];
	var x_corner_length = (points_x[0]-points_x[1])*0.1;
	var y_corner_length = (points_y[0]-points_y[1])*0.15;
	var rectPoints=[];
	for(var i=0;i<4;i++){
		var vector = [-1,1];
		var polyline = new google.maps.Polyline({
			path: [
				new google.maps.LatLng((points_y[Math.floor(i/2)]+y_corner_length*vector[Math.floor(i/2)]),points_x[Math.floor(i%2)]),
				new google.maps.LatLng( points_y[Math.floor(i/2)]                                         ,points_x[Math.floor(i%2)]),
				new google.maps.LatLng( points_y[Math.floor(i/2)]                                         ,(points_x[Math.floor(i%2)]+x_corner_length*vector[Math.floor(i%2)]))
	        ],
			geodesic: true,
			strokeColor: '#338833',
			strokeOpacity: 0.7,
			strokeWeight: 6,
			map: map
		});
		map_focus_arr.push(polyline);
		rectPoints.push({lat: points_y[Math.floor(i/2)], lng: points_x[Math.floor((i-Math.floor(i/2))%2)]});
	}
	
	rectPoints.push(new google.maps.LatLng(points_y[0],points_x[0]));
	var bermudaTriangle = new google.maps.Polygon({
		paths: rectPoints,
		strokeColor: '#FF0000',
		strokeOpacity: 0.2,
		strokeWeight: 2,
		fillColor: '#ffc',
		fillOpacity: 0.2,
		map: map
	});
	map_focus_arr.push(bermudaTriangle);
	
	return { clear :function(){
		while(map_focus_arr.length>0){
			map_focus_arr.pop().setMap(null);
		}
	}};
}

function isEnvAnalDivOpen(){
	var open = false ;
	$("div").each(function(){
		if($(this).attr("id")!=null && $(this).attr("id").indexOf("region_select")!=-1){
			if($(this).dialog("isOpen")){
				open = true;
			}
		}
	});
	return open ;
}

//一些可共用但目前只有少數頁面使用因此未合併的func

//function hslToHex(){
//	hsl(360,100%,80%) = > #000 //參見mapFunctionEnvAnal.js
//}
//function increase_brightness(hex, percent){
//	增加#888亮度 // 參見mapFunctionEnvAnal.js
//}
//function GetDistance(lat1, lng1, lat2, lng2) {
//	兩經緯度間距離公尺 //參見mapFunctionEnvAnal.js
//}

