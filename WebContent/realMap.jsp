<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="import.jsp" flush="true"/>

<link rel="stylesheet" href="css/styles_map.css"/>
<link href="importPKG/fancy-tree/skin-xp/ui.fancytree.css" rel="stylesheet">

<script src="js/d3.v3.min.js"></script>
<script type="text/javascript" src="js/jquery-migrate-1.4.1.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/additional-methods.min.js"></script>
<script type="text/javascript" src="js/messages_zh_TW.min.js"></script>

<script src="importPKG/fancy-tree/jquery.fancytree.js"></script>
<script src="importPKG/oriSBI/js/wicket.js"></script>
<script src="importPKG/oriSBI/js/wicket-gmap3.js"></script>
<!-- 以下為自寫的map相關和POIMenu function -->
<script src="js/mapFunction.js"></script>
<script src="js/menu_of_POI.js"></script>

<script>
var map;
	$(function(){
		$.ajax({
    		type : "POST",
    		url : "realMap.do",
    		data : {
    			action : "prepareGeometry",
    		},success : function(result){
    			if(!window.map_corr){
    				map_corr = {};
    			}
    			var json_obj = $.parseJSON(result);
    			$.each(json_obj, function(i, item){
    				map_corr[ item["CNTRY_NAME"] ] = item["geom"];
				});
    		}
		});
		
		$("#shpLegend").draggable({ containment: ".page-wrapper" });
		
		draw_menu_of_poi({
			action : "select_menu", 
			type : "RealMap"
		});
		
		$( "#opacity" ).slider({
	      range: "min",
	      value: 100,
	      min: 40,
	      max: 100,
	      slide: function( event, ui ) {
	        $( "#panel" ).css("opacity", (ui.value*0.01) );
	      }
	    });
	});
</script>

<jsp:include page="header.jsp" flush="true"/>
<div class="content-wrap">
	
<h2 class="page-title">商圈資訊</h2>
	<div class="search-result-wrap">
	<div id="map"></div>
<!-- 左下角的框框 -->
<div id="shpLegend" class="shpLegend" style=" height: 235.5px;display:none;">
    <button onclick='$("#shpLegend").hide();'>x</button>
    <div style="display: block; background-color: #F0F0F0;">
        <table style="width:280px;">
            <tr>
                <th colspan="2" style="font-size: large;">
                    <span id="span_legend">TITLE</span>
                </th>
            </tr>
            <tr id="tr_year">
                <td style="width: 80px;">年份&nbsp;:</td><td><span id="ddl_year">Year</span></td>
            </tr>
            <tr>
                <td style="width: 80px;">單位&nbsp;:</td><td><span id="span_unit">Unit</span></td>
            </tr>
            <tr>
                <td style="vertical-align: top;">圖例&nbsp;:</td>
                <td>
                    <table>
                        <tr>
                            <td style="width: 10px;">
                                <div class="mapLegend" style="background-color: #41A85F;"></div>
                            </td>
                            <td><span id="span_level1">Level 1</span></td>
                        </tr>
                        <tr>
                            <td>
                                <div class="mapLegend" style="background-color: #8db444;"></div>
                            </td>
                            <td><span id="span_level2">Level 2</span></td>
                        </tr>
                        <tr>
                            <td>
                                <div class="mapLegend" style="background-color: #FAC51C;"></div>
                            </td>
                            <td><span id="span_level3">Level 3</span></td>
                        </tr>
                        <tr>
                            <td>
                                <div class="mapLegend" style="background-color: #d87926;"></div>
                            </td>
                            <td><span id="span_level4">Level 4</span></td>
                        </tr>
                        <tr>
                            <td>
                                <div class="mapLegend" style="background-color: #B8312F;"></div>
                            </td>
                            <td><span id="span_level5">Level 5</span></td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </div>
</div>
<!-- 左下角的框框 -->
	<div id="detail_1"></div>
	
    <script>
    var rs_markers=[];
	    function initMap() {
			// Create the map.
			map = new google.maps.Map(document.getElementById('map'), {
				panControl: true,
			    zoomControl: true,
			    mapTypeControl: false,
			    scaleControl: true,
			    streetViewControl: true,
			    overviewMapControl: true,
			    zoom: 7,
				center: {lat: 23.598321171324468, lng: 120.97802734375}
			});
			map.addListener('zoom_changed', function() {
				if(+map.getZoom()<2){
					map.setZoom(2);
				}
			});
   		}
    </script>
    <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyC8QEQE4TX2i6gpGIrGbTsrGrRPF23xvX4&signed_in=true&libraries=places,drawing&callback=initMap"></script> 
	</div>
</div>

<jsp:include page="footer.jsp" flush="true"/>