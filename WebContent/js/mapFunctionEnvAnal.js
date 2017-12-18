//　
// 20170912 Ben EnvironmentAnalysis
// 早在POI和RegionSelect都叫到的時候就該做了
// 暫時只是搬過來沒有美化 setting是丟一個buttonID
//
//window.UUID || document.write('<script type="text/javascript" src="js/common.js"></script>');
var item_marker = function (num, speed, time, marker, circle, lat, lng) {
	this.id = UUID();
	var colors=["","#a0f7db","#a2f7a0","#f7b4a0","#a4a0f7","#f7a0ca"]
	//this.color = hslToHex(Math.floor(Math.random()*360)+"","85%","80%");
	this.color = colors[num];
	this.num = num;
	this.speed = speed;
	this.time = time;
	this.marker = marker;
	this.circle = circle;
	this.lat = lat;
	this.lng = lng;
	
	this.poiMarkers = [];
	this.poiInfoWindows = [];
	this.villageInfoPoly = [];
	
	this.selectPOI_1 = function(){ //type
		var this_ptr = this ;
		var radius = new Number(this_ptr.speed * this_ptr.time *1000*0.016667).toFixed(2);
		
		loading('now'); 
		$("#poi-statistic-table_"+this_ptr.id).remove();
		
		$.ajax({
			type : "POST",
			url : "POI.do",
			data : {
				action : "select_poi_statistic_circle",
				typelist : "",
				center_lat : this_ptr.lat,
				center_lng : this_ptr.lng,
				radius : radius
			},
			success : function(result) {
				var json_obj = $.parseJSON(result);
				var table_container = $("<table/>",{
					"class":"info_window"
				});
				
				table_container.append([
					$("<tr/>").html(
						$("<th/>",{
							"colspan":"3",
							"text":"　各大類型POI統計"
						})
					),$("<tr/>").html([
						$("<td/>",{
							"text":"類型"
						}),$("<td/>",{
							"text":"家數"
						})
					])
				]);
				
				if(json_obj.length==0){
					table_container.append(
						$("<tr/>").html(
							$("<td/>",{
								"colspan":"3",
								"class":"word-120pa",
								"style":"padding:5px;"
							}).html("此區域查無任何類型POI。")
						)
					);
				}
				$.each(json_obj,function(i, item) {
					table_container.append(
						$("<tr/>").html([
							$("<td/>").html(
								$("<a/>",{
									"text" : item["subtype"],
									"href":"#"
								}).click(function(){
										this_ptr.selectPOI_2(item["subtype"]);
								})//('show_poi_statistic_radius_subtype(\''+json_obj[i]["subtype"]+'\',\''+uid+'\',\''+num+'\',\''+center_lat+'\',\''+center_lng+'\',\''+radius+'\',\''+markerObj.color+'\');')
							),$("<td/>").html(
								$("<div/>",{
									"html" : json_obj[i]["count_n"]+"&nbsp;"
								})
							)
						])
					);
				});
				$("body").append(
					$("<div/>",{
		 				"id":"poi-statistic-table_"+this_ptr.id,
		 				"class":"poi-statistic-table",
		 				"style":"z-index:"+getStrongestZindex()+";background-color:"+increase_brightness(this_ptr.color,70)+";"
			 		}).html([
			 			'<div class="ui-corner-all ui-dialog-titlebar ui-widget-header" style="padding: .4em 1em;position: relative;">'
			 			+'<span id="ui-id-1" class="ui-dialog-title" style="font-family: 微軟正黑體;">POI統計-點位 '+ this_ptr.num+'</span>'
			 			+'<button onclick=\'$("#poi-statistic-table_'+this_ptr.id+'").remove();\' type="button" class="ui-button ui-corner-all ui-widget ui-button-icon-only ui-dialog-titlebar-close" title="Close" style="font-family: 微軟正黑體;position: absolute;right: .3em;top: 50%;width: 20px;margin: -10px 0 0 0;padding: 1px;height: 20px;">'
			 			+'<span class="ui-button-icon ui-icon ui-icon-closethick" style="font-family: 微軟正黑體;"></span><span class="ui-button-icon-space" style="font-family: 微軟正黑體;"> </span>Close</button>'
			 			+'</div>',
			 			$("<div/>").html([
			 				$("<div/>",{"id":"selectPOI_1_"+this_ptr.id,"style":"max-height:calc(70vh);overflow-y:auto;padding:12px;"}).html([
				 				table_container
				 			]),
				 			$("<div/>",{"id":"selectPOI_2_"+this_ptr.id,"style":"display:none;max-height:480px;overflow-y:auto;padding:12px;margin:4px;"})
			 			])
			 		])
			 	);
				
				$("#poi-statistic-table_"+this_ptr.id).draggable(
					{ containment: "body",handle:".ui-dialog-titlebar" }
				);
				
				$("#poi-statistic-table_"+this_ptr.id).click(function(){
					$("#poi-statistic-table_"+this_ptr.id).css("z-index",getStrongestZindex());
				});
				loading('over');
			}
		});
	};
	
	this.selectPOI_2 = function( subtype_list ){ //subtype
		var this_ptr = this ;
		var radius = new Number(this_ptr.speed * this_ptr.time *1000*0.016667).toFixed(2);
		
		$("#selectPOI_2_"+this_ptr.id).empty();
		
		loading('now');
		$.ajax({
			type : "POST",
			url : "POI.do",
			data : {
				action : "select_poi_statistic_circle",
				subtypelist : subtype_list,
				center_lat : this_ptr.lat,
				center_lng : this_ptr.lng,
				radius : radius
			},
			success : function(result) {
				if(result == "fail!"){
					alert('查詢資料過久，請稍後再試');
					return ;
				}
				var json_obj = $.parseJSON(result);
				var subtype_result_table = $("<table/>",{
					"style":"width:96%",
					"class":"info_window"
				}).append(
					$("<thead/>").html(
						$("<tr/>").html([
							$("<td/>",{"text":"子類別"}),
							$("<td/>",{"text":"家數","style":"min-width: 60px;"})
						])
					)
				);
				$.each(json_obj,function(i, item) {
					subtype_result_table.append(
						$("<tr/>").html([
							$("<td/>").html(
								$("<a/>",{
									"text" : item["type"],
									"href":"#"
								}).click(function(){
										this_ptr.selectPOI_3(item["type"]);
								})
							),$("<td/>").html(
								item["count_n"]
							)
						])
					);
				});
				
				$("#selectPOI_2_"+this_ptr.id).html([
					$("<div/>",{"style":"text-align: center;padding:10px;"}).html([
						$("<span/>",{"text":"　子類別統計","style":"font-size: 120%; padding: 10px;font-weight: bold;"}),
						$("<button/>",{
							"class":"ui-button go-home-icon",
							"style":"float:right;border-radius:5px;padding: 12px;position:relative;top:-8px;",
							"title":"回類別選單"
						}).html([
							$("<i/>",{"class":"fa fa-home","style":"transform: scale(2, 2)"})
//							$("<span/>",{"text":"回上頁"})
						]).click(function(){
							
							$("#selectPOI_2_"+this_ptr.id).hide();
							$("#selectPOI_1_"+this_ptr.id).show();
						})
					]),
					subtype_result_table
				]);
				tooltip("go-home-icon");
				subtype_result_table.dataTable({
					dom : "r<t>ip",
					"sScrollY":"340px",
					language: {
						"search" : "搜尋:",
						"paginate": { 
					        "previous": "←",
					        "next":     "→"
					    },
					    "info":"　　　_PAGE_ / _PAGES_",
					},
					"order": [[ 1, "desc" ]],
					"pagingType": "simple",
					"fnInitComplete": function( oSettings ) {
						loading('over');
						$(".dataTables_scrollHeadInner").css("width","auto");
						$("#selectPOI_1_"+this_ptr.id).hide();
						$("#selectPOI_2_"+this_ptr.id).show();
					}
				});
			}
		});
	};
	
	this.selectPOI_3 = function(type_name){ //POI detail
//		alert('this is three: '+ type_name);
		var this_ptr = this ;
		var radius = new Number(this_ptr.speed * this_ptr.time *1000*0.016667).toFixed(2);
		loading();
		while(this_ptr.poiMarkers.length>0){
			this.poiMarkers.pop().setMap(null);
		}
		while(this_ptr.poiInfoWindows.length>0){
			this.poiInfoWindows.pop().setMap(null);
		}
		if(type_name==null){
			return ;
		}
		
		$.ajax({
			type : "POST",
			url : "POI.do",
			data : {
				action : "select_poi_statistic_circle_detail",
				typelist : type_name,
				center_lat : this_ptr.lat,
				center_lng : this_ptr.lng,
				radius : radius
			},
			success : function(result) {
				var json_obj = $.parseJSON(result);
				$.each(json_obj,function(i, item){
					var marker = new google.maps.Marker({
					    position: {lat:+item.lat,lng:+item.lng},
					    title: item.name,
					    map: map
					});
					if(item.icon.length>3){
						var poi_icon_url = "./imageIcon.do?action=getPoiIconPath"
							+ "&pic_name="
							+ encodeURI(b64EncodeUnicode(json_obj[i].icon)).replace(/\+/g,'%2b');
						
						marker.setIcon({
							url: poi_icon_url,
							scaledSize: new google.maps.Size(30, 30)
						});
					}
					var infowindow = new google.maps.InfoWindow({ content:(
						'<table class="info_window keyValueData">'
						+'<tr><th colspan="2">'+item["type"]+'　</th></tr>'
						+'<tr><td>名稱：</td><td style="text-align: left;">'+item["name"]+'</td></tr>'
						+ show_if_significant(item["address"],'<tr><td>地址：</td><td style="text-align: left;">#P#</td></tr>')
						+ show_if_significant(item["subtype"],'<tr><td>類型：</td><td style="text-align: left;">#P#</td></tr>')
						+'</table>'
					)});
					this_ptr.poiMarkers.push(marker);
					this_ptr.poiInfoWindows.push(infowindow);
					
					google.maps.event.addListener(marker, "click", function(event) {
						var infowindow_open = infowindow.getMap();
					    if(infowindow_open !== null && typeof infowindow_open !== "undefined"){
					    	infowindow.close();
					    }else{
					    	infowindow.open(marker.get('map'), marker);
					    }
			        });
				});
				loading('over');
			}
		});
	};
	
	this.selectVillageInfo = function(){
		var this_ptr = this ;
		while(this_ptr.villageInfoPoly.length>0){
			this_ptr.villageInfoPoly.pop().setMap(null);
    	}
		
		var radius = new Number(this_ptr.speed * this_ptr.time *1000*0.016667).toFixed(2);
		loading('now');
		$.ajax({
			type : "POST",
			url : "POI.do",
			data : {
				action : "select_poi_statistic_circle_villageinfo",
				center_lat : this_ptr.lat,
				center_lng : this_ptr.lng,
				radius : radius
			},
			success : function(result) {
				
				var config = {
	                fillColor: '#8080f0',
	                strokeColor: '#4C4C8C',
	                fillOpacity: 0.3,
	                strokeOpacity: 0.5,
	                strokeWeight: 2,
	            };
				
				function setVillagePolygenInfo(polygen,item){
	            	polygen.setMap(map);
	            	polygen.setOptions({zIndex:2});
	            	this_ptr.villageInfoPoly.push(polygen);
	            	
	            	var villageInfoTable = "<table class='info_window' style='margin-left:25px;'>"
	            		+"<tr><th colspan='3'>"+item.name+"</th></tr>"
	            		+"<tr><td>"+item.geom_info.replace(/：/g,":</td><td>").replace(/\n/g,"</td></tr><tr><td>")+"</td></tr>"
	            		+"</table>";
	            	var infowindow = new google.maps.InfoWindow({ content : villageInfoTable });
			        var infoMarker = new google.maps.Marker({
			            position: new google.maps.LatLng(item.lat,item.lng),
			            icon: {
			                path: google.maps.SymbolPath.CIRCLE,
			                scale: 0
			            },
			            map: map
			        });
	            	
	            	google.maps.event.addListener(polygen, 'click', function () {
	            		infowindow.open(map, infoMarker);
    	            });
	            }
				
				var json_obj = $.parseJSON(result);
				$.each(json_obj,function(i, item) {
					var wkt = new Wkt.Wkt();
					wkt.read(item.geom);
					var polygen = wkt.toObject(config);
					if (Wkt.isArray(polygen)) {
		                for (i in polygen) {
		                    if (polygen.hasOwnProperty(i) && !Wkt.isArray(polygen[i])) {
		                    	setVillagePolygenInfo(polygen[i],item);
		                    }
		                }
		            } else {
		            	setVillagePolygenInfo(polygen,item);
		            }
				});
				
				$("#poi-villageInfo-table_"+this_ptr.id).remove();
				var table_container = $("<table/>",{
					"class":"info_window"
				}).html(
					$("<tr/>").html(
						$("<th/>",{
							"colspan":"3",
							//"text":"　點位 "+ this_ptr.num+" 鄰近鄰里資訊"
							"text":"　鄰里人口資訊"
						})
					)
				);
				
				if(json_obj.length==0){
					table_container.append(
						$("<tr/>").html(
							$("<td/>",{
								"colspan":"2",
//								"class":"word-120pa",
								"style":"padding:5px;"
							}).html("資料庫無此區鄰里資訊/或區域過小<br>系統判斷無代表性鄰里")
						)
					);
				}else{
					table_container.append(
						$("<tr/>").html([
							$("<td/>",{
								"text":"鄰里全名"
							}),$("<td/>",{
								"text":"常住人口"
							})
						])
					);
				}
				var sumPeople = 0;
				$.each(json_obj,function(i, item) {
					table_container.append(
						$("<tr/>").html([
							$("<td/>").html(
								item["name"]
//								$("<a/>",{
//									"text" : json_obj[i]["subtype"],
//									"href":"#",
//									"onclick" : ('show_poi_statistic_radius_subtype(\''+json_obj[i]["subtype"]+'\',\''+uid+'\',\''+num+'\',\''+center_lat+'\',\''+center_lng+'\',\''+radius+'\',\''+markerObj.color+'\');')
//								})
							),$("<td/>").html(
								$("<div/>",{
									"html" : money_format(item["resident"])+"&nbsp;人"
								})
							)
						])
					);
					sumPeople += +item["resident"];
				});
				if(json_obj.length>0){
					table_container.append(
						$("<tr/>",{"style":"border-top:2px solid #888;"}).html([
							$("<td/>").html(
								"<b style='font-size:1.2em;'>總計人數</b>"
							),$("<td/>").html(
								$("<div/>",{
									"html" : "<b style='font-size:1.2em;'>"+money_format(sumPeople)+"&nbsp;人</b>"
								})
							)
						])
					);
				}
				$("body").append(
					$("<div/>",{
		 				"id":"poi-villageInfo-table_"+this_ptr.id,
		 				"class":"poi-villageInfo-table",
		 				"style":"z-index:"+getStrongestZindex()+";background-color:"+increase_brightness(this_ptr.color,70)+";"
			 		}).html([
			 			'<div class="ui-corner-all ui-dialog-titlebar ui-widget-header" style="padding: .4em 1em;position: relative;">'
			 			+'<span id="ui-id-1" class="ui-dialog-title" style="font-family: 微軟正黑體;">鄰里資料-點位 '+ this_ptr.num+'</span>'
			 			+'<button onclick=\'$("#poi-villageInfo-table_'+this_ptr.id+'").remove();\' type="button" class="ui-button ui-corner-all ui-widget ui-button-icon-only ui-dialog-titlebar-close" title="Close" style="font-family: 微軟正黑體;position: absolute;right: .3em;top: 50%;width: 20px;margin: -10px 0 0 0;padding: 1px;height: 20px;">'
			 			+'<span class="ui-button-icon ui-icon ui-icon-closethick" style="font-family: 微軟正黑體;"></span><span class="ui-button-icon-space" style="font-family: 微軟正黑體;"> </span>Close</button>'
			 			+'</div>',
			 			$("<div/>",{"style":"max-height:calc(70vh);overflow-y:auto;padding:12px;"}).html([
			 				table_container
			 			]),
			 		])
			 	);
				
				$("#poi-villageInfo-table_"+this_ptr.id).draggable(
					{ containment: "body",handle:".ui-dialog-titlebar" }
				);
				
				$("#poi-villageInfo-table_"+this_ptr.id).click(function(){
					$("#poi-villageInfo-table_"+this_ptr.id).css("z-index",getStrongestZindex());
				});
				loading('over');
			}
		});
		this_ptr.villageInfoPoly = [];
	};
	
	this.clear = function(func){
		this.marker.setMap(null);
		this.circle.setMap(null);
		
		$("#poi-statistic-table_"+this.id).remove();
		$("#poi-villageInfo-table_"+this.id).remove();
		
		while(this.poiMarkers.length>0){
			this.poiMarkers.pop().setMap(null);
		}
		
		while(this.poiInfoWindows.length>0){
			this.poiInfoWindows.pop().setMap(null);
		}
		
		while(this.villageInfoPoly.length>0){
			this.villageInfoPoly.pop().setMap(null);
    	}
		
		if(func!=null){
			func();
		}
	};
	
}
//hslToHex(Math.floor(Math.random()*360)+"","85%","50%")
//http://www.googlemapsmarkers.com/v1/LABEL/COLOR/
function envAnalSetup(idStr,map){
	
	if(!window.rs_markers){
		rs_markers=[];
	}
	$("#"+idStr).click(function(){
		$("#region_select"+this_UUID).dialog("open");
	});
	
	$("body").append(envHtml);
	$("#region_select"+this_UUID).dialog({
		draggable : true,
		resizable : false,
		autoOpen : false,
		//minHeight : 125,
        //minWidth : 500 ,
		height : "auto", 
		width : "auto", 
		modal : false,
		
		position: { 
			my: "center", 
			at: "right-180px top+240px ", 
			of: window  
		} ,  
		show : {
			effect : "blind",
			duration : 300
		},
		hide : {
			effect : "fade",
			duration : 300
		},
		open :function(){
			if($("#draw_circle"+this_UUID).is(":hidden")){
				map.setOptions({
					draggableCursor:("url("+location.href.split("sbi/")[0]
							+"sbi/images/mapRegionSelectCursor.png),default")
				});
			}
			$(this).parent().find("*").css("font-family","微軟正黑體");
		},
		close : function() {
			map.setOptions({draggableCursor:null});
			$("#region_select"+this_UUID).dialog("close");
		}
	});
	$("#region_select"+this_UUID).show();
	
	function update_all(){
		//晚點寫
	}
	
	
	$( "#speed"+this_UUID ).spinner({
	    min: 0,
	    max: 3000,
	    spin: function(event, ui) {
	    	if(allow_change($("#rr_pt"+this_UUID).val().id)){
		    	$("#speed"+this_UUID).val(ui.value);
		        $(this).change();
	    	}
	    }
	});
	$( "#time"+this_UUID ).spinner({
	    min: 0,
	    max: 86400,
	    spin: function(event, ui) {
	    	if(allow_change($("#rr_pt"+this_UUID).val().id)){
		    	$("#time"+this_UUID).val(ui.value);
		    	$(this).change();
	    	}
	    }
	});
    $( "#slider"+this_UUID ).slider({
    	min: 0,
        max: 120,
        value: 30,
        orientation: "horizontal",
        range: "min",
        animate: true,
        slide: function (event, ui) {
        	if(allow_change($("#rr_pt"+this_UUID).val().id)){
	        	$("#time"+this_UUID).val(ui.value);
	        	$("#val_time"+this_UUID).html("花費"+ui.value+"分鐘");
	        	$("#rr_pt"+this_UUID).val().speed= $("#speed"+this_UUID).val();
		    	$("#rr_pt"+this_UUID).val().time=$("#time"+this_UUID).val();
		    	var radius = $("#speed"+this_UUID).val()*$("#time"+this_UUID).val()*1000*0.016667;
	        	$("#rr_pt"+this_UUID).val().circle.setRadius(radius);
	        	$("#vague_dist"+this_UUID).html("約為"+format_radius(Math.round(radius*0.1)*10));
        	}else{
//        		event.preventDefault();
        		$( "#slider"+this_UUID ).mouseup();
        		$( "#slider"+this_UUID ).slider('option', 'value', $("#rr_pt"+this_UUID).val().time);
        	}
        }
    });

    $("#region_select_next"+this_UUID).click(function(){
    	if(rs_markers.length==0){
    		warningMsg("提示","<div style='padding:5px 8px;font-size:28px;'>請放置分析點。</div>");
    	}else{
    		map.setOptions({draggableCursor:null});
    		$("#instruction"+this_UUID).hide();
    		$("#draw_circle"+this_UUID).show();
    		$.each(rs_markers,function(i, item) {
    			rs_markers[i].circle.setRadius(rs_markers[i].speed* rs_markers[i].time *1000 * 0.016667);
    		});
    		$("#region_select"+this_UUID).css("width","500px");
    		$("div[aria-describedby='region_select"+this_UUID+"']").animate({"left": "-=180px"});
    	}
    });
    
    $("#region_select_last"+this_UUID).click(function(){
    	map.setOptions({
    		draggableCursor:("url("+location.href.split("sbi/")[0]
			+"sbi/images/mapRegionSelectCursor.png),default")
    	});
		$("#instruction"+this_UUID).show();
		$("#draw_circle"+this_UUID).hide();
		
		$("#region_select"+this_UUID).css("width","300px");
		$("div[aria-describedby='region_select"+this_UUID+"']").animate({"left": "+=180px"});
    });

    $("#speed"+this_UUID).change(function(){
    	if(allow_change($("#rr_pt"+this_UUID).val().id)){
	    	$('#val_speed'+this_UUID).html("時速"+$(this).val()+"公里");
	    	$("#rr_pt"+this_UUID).val().speed=$("#speed"+this_UUID).val();
	    	$("#rr_pt"+this_UUID).val().time=$("#time"+this_UUID).val();
	    	var radius = $("#speed"+this_UUID).val()*$("#time"+this_UUID).val()*1000*0.016667;
	    	$("#rr_pt"+this_UUID).val().circle.setRadius(radius);
	    	$("#vague_dist"+this_UUID).html("約為"+format_radius(Math.round(radius*0.1)*10));
    	}
	});
    $("#time"+this_UUID).change(function(){
    	if(allow_change($("#rr_pt"+this_UUID).val().id)){
	    	$('#val_time'+this_UUID).html("花費"+$(this).val()+"分鐘");
	    	$("#rr_pt"+this_UUID).val().speed=$("#speed"+this_UUID).val();
	    	$("#rr_pt"+this_UUID).val().time=$("#time"+this_UUID).val();
	    	var radius = $("#speed"+this_UUID).val()*$("#time"+this_UUID).val()*1000*0.016667;
	    	$("#rr_pt"+this_UUID).val().circle.setRadius(radius);
	    	$("#vague_dist"+this_UUID).html("約為"+format_radius(Math.round(radius*0.1)*10));
			$('#slider'+this_UUID).slider('option', 'value', $(this).val());
    	}
	});
    
    $("#tooltip_1"+this_UUID).mouseover(function(e){
		 this.newTitle = this.title;
		 this.title = "";
		 var tooltip = "<div id='tooltip'>"+ this.newTitle +"<\/div>";
		 $("body").append(tooltip);
		 $("#tooltip").css({"top": (e.pageY+20) + "px","left": (e.pageX+10)  + "px"}).show();
	 }).mouseout(function(){
	         this.title = this.newTitle;
	         $("#tooltip").remove();
	 }).mousemove(function(e){
	         $("#tooltip").css({"top": (e.pageY+20) + "px","left": (e.pageX+10)  + "px"});
	 });
    
    $("#show_poi_statistic"+this_UUID).click(function(){
    	$("#rr_pt"+this_UUID).val().selectPOI_1();
//    	show_poi_statistic_radius(
//    			$("#rr_pt"+this_UUID).val()
//		);
    });
    $("#show_poi_villageInfo"+this_UUID).click(function(){
    	$("#rr_pt"+this_UUID).val().selectVillageInfo();
//    	show_poi_villageInfo(
//    			$("#rr_pt"+this_UUID).val()
//		);
    });
    
    
    google.maps.event.addListener(map, 'click', function(event) {
		if($("#region_select"+this_UUID).dialog("isOpen")&& $("#draw_circle"+this_UUID).css("display")=="none"){
			
			if(rs_markers.length>=5){warningMsg('警告', "最多五個點");return;}
			var order=(rs_markers.length+1)+"";
			var rs_marker = new google.maps.Marker({
			    position: event.latLng,
			    animation: google.maps.Animation.DROP,
//			    icon: 'http://maps.google.com/mapfiles/kml/paddle/' + order + '.png',
			    icon: (location.href.replace(
			    		location.href.split("/").pop(),
			    		'')+"images/envMarkers/"+order+"-marker.png"), 
			    map: map,
			    draggable:true,
			    title: ("--分析點"+order+"--")
			});
			
			var rs_circle = new google.maps.Circle({
				  strokeColor: '#FF0000',
				  strokeOpacity: 0.5,
				  strokeWeight: 3,
				  fillColor: '#FF8700',
				  fillOpacity: 0.2,
				  map: map,
				  center: event.latLng,
				  radius: 0 
			});
			
			//期望時間為20~40
			// dis ~ 15000 ~ 5000 ~ 1200 ~
			//80 + 60 => 80000
			//60 + 60 => 60000
			//15 + 60 => 15000
			// 5 + 60 =>  5000
			//1 + 60 => 1000
			// >2000 10 dis/10
			
//			var vagueDist = getWindowQuarterDis();
			var vague_speed = 5;
			var vague_time = 12;
//			console.log(vagueDist);
//			if(vagueDist>90000){ 
//				vague_speed = 80; 
//				vague_time = Math.round( vagueDist * 60 * 0.2 / 80000 ) * 5 ;
//			}else if(vagueDist>25000){
//				vague_speed = 60; 
//				vague_time = Math.round( vagueDist * 60 * 0.2 / 60000 ) * 5 ;
//			}else if(vagueDist>5000){
//				vague_speed = 15; 
//				vague_time = Math.round( vagueDist * 60 * 0.2 / 15000 ) * 5 ;
//			}else if(vagueDist>1300){
//				vague_speed = 5; 
//				vague_time = Math.round( vagueDist * 60 * 0.2 / 5000 ) * 5 ;
//			}else if(vagueDist>100){
//				vague_speed = 1; 
//				vague_time = Math.round( vagueDist * 60 * 0.2 / 1000 ) * 5 ;
//			}
			
			var marker_obj = new item_marker( order, vague_speed, vague_time, rs_marker, rs_circle,event.latLng.lat(),event.latLng.lng());
			
			rs_marker.setIcon({
//			    url: "http://www.googlemapsmarkers.com/v1/"+order+"/"+marker_obj.color.replace("#","")+"/",
			    url: (location.href.replace(location.href.split("/").pop(),"")+"images/envMarkers/envMarker-"+order+".png"),
			    scaledSize: new google.maps.Size(31, 50)
			});
			
			$("#rr_pt"+this_UUID).html(order);
			$("#rr_pt"+this_UUID).val(marker_obj);
			$("#speed"+this_UUID).val(marker_obj.speed);
			$("#val_speed"+this_UUID).html("時速"+marker_obj.speed+"公里");
			$("#time"+this_UUID).val(marker_obj.time);
			$("#val_time"+this_UUID).html("花費"+marker_obj.time+"分鐘");
			$("#vague_dist"+this_UUID).html("約為"+format_radius(Math.round(marker_obj.speed*marker_obj.time *1000 * 0.016667*0.1)*10));
			$('#slider'+this_UUID).slider('option', 'value', marker_obj.time);
	        rs_markers.push(marker_obj);
	        google.maps.event.addListener(rs_circle, "click", function(event) { 
				if(( ($("#env_analyse"+this_UUID).length!=0&&$("#env_analyse"+this_UUID).dialog("isOpen")) ||($("#region_select"+this_UUID).length!=0&&$("#region_select"+this_UUID).dialog("isOpen")) )&& $("#draw_circle"+this_UUID).css("display")=="none"){
					google.maps.event.trigger(map, 'click',event);
				}
	        });
	        
			google.maps.event.addListener(rs_marker, "click", function(event) { 
				$.each(rs_markers, function(i, node){
					rs_markers[i].marker.setAnimation(null);
				});
				$("#rr_pt"+this_UUID).css("font-size","38px");
				setTimeout(function(){$("#rr_pt"+this_UUID).css("font-size","16px");},1000);
				$("#rr_pt"+this_UUID).css("color","red");
				setTimeout(function(){$("#rr_pt"+this_UUID).css("color","black");},1000);
				$("#rr_pt"+this_UUID).html(order);
				$("#rr_pt"+this_UUID).val(marker_obj);
				$("#speed"+this_UUID).val(marker_obj.speed);
				$("#time"+this_UUID).val(marker_obj.time);
				$('#val_time'+this_UUID).html("花費"+marker_obj.time+"分鐘");
				$('#val_speed'+this_UUID).html("時速"+marker_obj.speed+"公里");
				$('#slider'+this_UUID).slider('option', 'value', marker_obj.time);
				$("#vague_dist"+this_UUID).html("約為"+format_radius(Math.round(marker_obj.speed*marker_obj.time *1000 * 0.016667*0.1)*10	));
	        }); 
	        
		    google.maps.event.addListener(rs_marker, 'drag', function(event){
		    	if(rs_marker.getDraggable() && allow_change(marker_obj.id)){
			    	marker_obj.lat = event.latLng.lat();
			    	marker_obj.lng = event.latLng.lng();
			    	rs_circle.setCenter(event.latLng);
		    	}
		    });
		    
		    google.maps.event.addListener(rs_marker, 'dragstart', function(event){
		    	if(!allow_change(marker_obj.id)){
		    		google.maps.event.trigger(rs_marker, 'mouseup',event);
		    		rs_marker.setPosition({lat:+marker_obj.lat,lng:+marker_obj.lng});
		    		rs_marker.setOptions({draggable: false});
		    		setTimeout(function(){
		    			rs_marker.setPosition({lat:+marker_obj.lat,lng:+marker_obj.lng});
		    			rs_marker.setOptions({draggable: true});
		    		},100);
		    	}
		    });
		}
	});
    
}

var this_UUID = UUID();
var envHtml=
		("<div id='region_select"+this_UUID+"' title='環域分析' style='display:none;'>"
		+"	<div id=\"instruction"+this_UUID+"\">"
		+"		<div style=\"margin:14px 20px;font-size:22px;color:#F00;font-weight:900;word-break: keep-all;\" class='blink'>請點擊地圖新增分析點。</div>"
		+"		<hr style='height:1px;border:none;border-top:1px solid #ddd;'>"
		+"		<div style=\"margin:0px 20px;float:right;\">"
		+"			<button class='ui-button' id='region_select_next"+this_UUID+"'>下一步</button>"
		+"			<button class='ui-button' onclick='while(rs_markers.length>0){var tmp = rs_markers.pop().clear();}'>清除所有點</button>"
		+"		</div>"
		+"	</div>"
		+"	<div id=\"draw_circle"+this_UUID+"\" style='display:none'>"
		+"		<table id=\"region_step_2"+this_UUID+"\" class=\"bentable\">"
		+"			<tr style=''>"
		+"				<td colspan='3' valign=\"bottom\">"
		+"					　&nbsp;<br>調整<span style='font-weight: bold;line-height:40px;'>點位<span id=\"rr_pt"+this_UUID+"\" style=\"transition: all .3s linear;\">1</span></span>的[交通方式]與[通勤時間]"
		+"					<a  style=\"float:right;\" id=\"suggest_time_html"+this_UUID+"\" href=\"./importPKG/oriSBI/Traffic_Time.htm\" target=\"_blank\">建議時數</a>"
		+"				</td>"
		+"			</tr>"
		+"			<tr>"
		+"				<td>"
		+"					<div class=\"col\" id=\"tooltip_1"+this_UUID+"\" title=\"預設時速為車行60公里、步行4公里、單車15公里\">"
		+"						<span style='font-weight: bold;'>交通方式：</span>"
		+"					</div>"
		+"				</td>"
		+"				<td>"
		+"					<img src='./importPKG/oriSBI/car.png' title=\"車行\" val=\"60\" "
		+"						onclick='$(\"#speed"+this_UUID+"\").val(60);$(\"#speed"+this_UUID+"\").change();'>"
		+"					<img src='./importPKG/oriSBI/walk.png' title=\"步行\" val=\"5\" "
		+"						onclick='$(\"#speed"+this_UUID+"\").val(5);$(\"#speed"+this_UUID+"\").change();'>"
		+"					<img src='./importPKG/oriSBI/bike.png' title=\"單車\" val=\"15\""
		+"						 onclick='$(\"#speed"+this_UUID+"\").val(15);$(\"#speed"+this_UUID+"\").change();'>"
		+"				</td>"
		+"				<td>"
		+"					　時速：<input id='speed"+this_UUID+"' style='width:40px;height:20px;padding:0px;' value='10'>　公里"
		+"				</td>"
		+"			</tr>"
		+"			<tr>"
		+"				<td>"
		+"					<span style='font-weight: bold;'>通勤時間：</span>"
		+"				</td>"
		+"				<td>"
		+"					<div id='slider"+this_UUID+"'></div>"
		+"				</td>"
		+"				<td>"
		+"					　需時：<input id='time"+this_UUID+"' style='width:40px;height:20px;padding:0px;' value='30'>　分鐘"
		+"				</td>"
		+"			</tr>"
		+"			<tr style='height:50px;'>"
		+"				<td>"
		+"					環域分析："
		+"				</td>"
		+"				<td colspan='2'>"
		+"					<a id=\"val_speed"+this_UUID+"\" style='color: #c33;text-decoration:underline;font-size:18px;font-weight: bold;'>時速4公里</a>&nbsp;"
		+"					<a id=\"val_time"+this_UUID+"\" style='color: #c33;text-decoration:underline;font-size:18px;font-weight: bold;'>花費15分鐘</a>&nbsp;"
		+"					之可達範圍<br>"
		+"					<a id=\"vague_dist"+this_UUID+"\" style='display:none'>(約為1000公尺)</a>"
		+"				</td>"
		+"			</tr>"
		+"		</table>"
		+"		<hr style='height:1px;border:none;border-top:1px solid #ddd;'>"
		+"		<div style=\"margin:0px 20px;float:right;\">"
		+"			<button class='ui-button' id='show_poi_villageInfo"+this_UUID+"'>人口資料</button>"
		+"			&nbsp;<button class='ui-button' id='show_poi_statistic"+this_UUID+"'>查看POI統計</button>"
		+"			&nbsp;<button class='ui-button' id='region_select_last"+this_UUID+"'>上一步</button>"
		+"			&nbsp;<button class='ui-button' id='end"+this_UUID+"' onclick='$(\"#region_select"+this_UUID+"\").dialog(\"close\");'>結束</button>"
		+"		</div>"
		+"	</div>"
		+"</div>");

function getWindowQuarterDis(){
	var dist= GetDistance(
		new Number(map.getBounds().getNorthEast().lat()).toFixed(4),
		new Number(map.getBounds().getNorthEast().lng()).toFixed(4),
		new Number(map.getBounds().getSouthWest().lat()).toFixed(4),
		new Number(map.getBounds().getSouthWest().lng()).toFixed(4)
	) * 0.125;
	Math.round(dist*0.1)*10;
	return dist > 1000? 
		Math.round(dist*0.01)*100 
		:(dist > 100
			?Math.round(dist*0.1)*10
			:dist);
}

function allow_change(uid){
	if($("#poi-statistic-table_"+uid).length==0 && $("#poi_statistic_subtype_answer_container_"+uid).length==0){
		return true;
	}else{
		warningMsg("提示","關閉該點位POI統計表方可更動。");
		setTimeout(function(){
			$("#speed"+this_UUID).val($("#rr_pt"+this_UUID).val().speed);
			$("#time"+this_UUID).val($("#rr_pt"+this_UUID).val().time);
			$("#slider"+this_UUID).slider('option', 'value', $("#rr_pt"+this_UUID).val().time);
		},300);
		
		return false;
	}
}

function format_radius(radius){
	if(radius>=2000){
		return new Number(radius/1000).toFixed(1) +" 公里";
	}else{
		return radius+" 公尺";
	}
}


//以下為import來的function

function hslToHex( hue, saturation, lightness){
	hsl = [hue,saturation,lightness];
	h = parseFloat( hsl[0] ) / 360;
	s = parseFloat( hsl[1] ) / 100;
	l = parseFloat( hsl[2] ) / 100;
	
	if ( s == 0 ) {
		r = g = b = l;
	}else {
		q = l < 0.5 ? l * ( 1.0 + s ) : l + s - ( l * s );
		p = 2.0 * l - q;
	    r = rgbToInt( rgb( _rgb( h + 1.0 / 3.0 ) ) );
		g = rgbToInt( rgb( _rgb( h ) ) );
		b = rgbToInt( rgb( _rgb( h - 1.0 / 3.0 ) ) );
	}
	
	return "#"+r.toString(16)+g.toString(16)+b.toString(16);
}

function rgbToInt( t ){
	t = Math.floor( t * 255 );
	return t;
}

function rgb( t ){
	if ( t < 1.0 / 6.0 ) {
		color = p + ( ( q - p ) * 6.0 * t );
	}else if ( t >= 1.0 / 6.0 && t < 0.5 ) {
		color = q;
	}else if ( t >= 0.5 && t < 2.0 / 3.0 ) {
		color = p + ( ( q - p ) * 6.0 * ( 2.0 / 3.0 - t ) );
	}else {
		color = p;
	}
	return color;
}

function _rgb( t ){
	if ( t < 0 )
		t = t + 1.0
	if ( t > 1 )
		t = t - 1.0
	return t;
}

function increase_brightness(hex, percent){
    hex = hex.replace(/^\s*#|\s*$/g, '');

    if(hex.length == 3){
        hex = hex.replace(/(.)/g, '$1$1');
    }

    var r = parseInt(hex.substr(0, 2), 16),
        g = parseInt(hex.substr(2, 2), 16),
        b = parseInt(hex.substr(4, 2), 16);

    return '#' +
       ((0|(1<<8) + r + (256 - r) * percent / 100).toString(16)).substr(1) +
       ((0|(1<<8) + g + (256 - g) * percent / 100).toString(16)).substr(1) +
       ((0|(1<<8) + b + (256 - b) * percent / 100).toString(16)).substr(1);
}


function rad(d) {
	return d * Math.PI / 180.0;
}

function GetDistance(lat1, lng1, lat2, lng2) {
	
	var radLat1 = rad(lat1);
	var radLat2 = rad(lat2);
	var a = radLat1 - radLat2;
	var b = rad(lng1) - rad(lng2);
	var s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
			+ Math.cos(radLat1) * Math.cos(radLat2)
			* Math.pow(Math.sin(b / 2), 2)));
	s = s * 6378.137;// EARTH_RADIUS;
	s = Math.round(s * 10000) / 10;
	return s;
}
