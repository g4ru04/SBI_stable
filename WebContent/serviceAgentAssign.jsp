<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="import.jsp" flush="true"/>
<script type="text/javascript" src="js/jquery-migrate-1.4.1.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/additional-methods.min.js"></script>
<script type="text/javascript" src="js/messages_zh_TW.min.js"></script>
<style>
.demo {
	height: 350px;
}
.demo ul {
	height: 300px;
    border: 3px solid #194A6B;
    background: #3F7FAA;
	margin-right: 20px;
	overflow-y: auto;
	color: #fff; 
}
.demo-left {
 	float:left; 
	width: 450px;
}
</style>
<script>
$(function(){
	
	// 查詢商品 事件聆聽
	$("#btn_query_product").click(function(e) {
	
		$.ajax({
			type : "POST",
			url : "serviceAgentAssign.do",
			data : {
				action : "selectServiceAgentNull",
				product_spec : $("#search_product_spec").val()
			},
			success : function(result) {
				var result_table = "";
				var json_obj = $.parseJSON(result);
				
				$("#draggable-cnt").text("共 " + json_obj.length + " 個");
				if(json_obj.length>0){
					$.each(json_obj,function(i, item) {
						result_table+="<li>"+item.service_id+"</li>";
					});
	    			$("#draggable").html(result_table);
	    			
	    			$("#draggable li")
	    		    // Script to deferentiate a click from a mousedown for drag event
	    		    .bind('mousedown mouseup', function(e){
	    		        if (e.type=="mousedown") {
	    		        	lastClick = e.timeStamp; // get mousedown time
	    		        } else {
	    		            diffClick = e.timeStamp - lastClick;
	    		            if ( diffClick < clickDelay ) {
	    		            	// add selected class to group draggable objects
	    		                $(this).toggleClass(selectedClass);
	    		            }
	    		        }
	    		    });
				}else{
					$("#draggable").html("");
				}
			}
		});
		
	});
	
	// 查詢通路商 事件聆聽
	$("#btn_query_agent").click(function(e) {
		
		$.ajax({
			type : "POST",
			url : "serviceAgentAssign.do",
			data : {
				action : "selectServiceAgentName",
				product_spec : $("#search_product_spec").val(),
				agent_name : $("#search_agent_name").val()
			},
			success : function(result) {
				var result_table = "";
				var json_obj = $.parseJSON(result);
				
				$("#droppable-cnt").text("共 " + json_obj.length + " 個");
				if(json_obj.length>0){
						$.each(json_obj,function(i, item) {
							result_table+="<li>"+item.service_id+"</li>";
						});
		    			$("#droppable").html(result_table);
		    			
		    			$("#droppable li")
			    		    // Script to deferentiate a click from a mousedown for drag event
			    		    .bind('mousedown mouseup', function(e){
			    		        if (e.type=="mousedown") {
			    		        	lastClick = e.timeStamp; // get mousedown time
			    		        } else {
			    		            diffClick = e.timeStamp - lastClick;
			    		            if ( diffClick < clickDelay ) {
			    		            	// add selected class to group draggable objects
			    		                $(this).toggleClass(selectedClass);
			    		            }
			    		        }
			    		    });
				}else{
					$("#droppable").html("");
				}
			}
		});
	});
	
	// 指定 事件聆聽
	$("#btn_assign").click(function(e) {
		var q = $("#quantity").val();
		var limit = $( "#draggable > li" ).size();
		
		if (q > limit) {
			warningMsg("警告",'服務識別碼數量不足，請確認。');
			return;
		}
		
		for (var i = 0; i < q; i++) {
			
			$.ajax({
				type : "POST",
				url : "serviceAgentAssign.do",
				data : {
					action : "update",
					service_id : $('#draggable > li')[i].innerHTML,
					agent_name : $("#search_agent_name").val(),
					product_spec : $("#search_product_spec").val()
				},
				success : function(result) {
					var result_table = "", result_tableAgent = "";
					var json_obj = $.parseJSON(result);

					if(json_obj.listNull.length>0){
							$("#draggable-cnt").text("共 " + json_obj.listNull.length + " 個");
							
							$.each(json_obj.listNull,function(i, item) {
								result_table+="<li>"+item.service_id+"</li>";
							});
							
			    			$("#draggable").html(result_table);
					}else{
						$("#draggable").html("");
					}
					
					if(json_obj.listAgent.length>0){
						$("#droppable-cnt").text("共 " + json_obj.listAgent.length + " 個");
						
						$.each(json_obj.listAgent,function(i, item) {
		    				result_tableAgent+="<li>"+item.service_id+"</li>";
						});
		    			
		    			$("#droppable").html(result_tableAgent);
		    			
					}else{
						$("#droppable").html("");
					}
					
	    			$("#draggable li, droppable li")
		    		    // Script to deferentiate a click from a mousedown for drag event
		    		    .bind('mousedown mouseup', function(e){
		    		        if (e.type=="mousedown") {
		    		        	lastClick = e.timeStamp; // get mousedown time
		    		        } else {
		    		            diffClick = e.timeStamp - lastClick;
		    		            if ( diffClick < clickDelay ) {
		    		            	// add selected class to group draggable objects
		    		                $(this).toggleClass(selectedClass);
		    		            }
		    		        }
		    		    });
				}
			});
		}
	});
	
	//處理 product spec 的autocomplete查詢
	$("#search_product_spec").autocomplete({
		minLength: 1,
		source: function (request, response) {
			$.ajax({
	             url : "product.do",
	             type : "POST",
	             cache : false,
	             delay : 1500,
	             data : {
	             	action : "autocomplete_spec",
	             	term : request.term
	             },
	             success: function(data) {
	             	var json_obj = $.parseJSON(data);
	             	response($.map(json_obj, function (item) {
							return {
								label: item.product_spec,
			                    value: item.product_spec
			               	}
		             	})
	             	);
	             },
	             error: function(XMLHttpRequest, textStatus, errorThrown) {
	                 alert_dialog(textStatus);
	             }
     		});
		},
		change: function(e, ui) {
			if (!ui.item) {
				$(this).attr("placeholder","請輸入查詢商品規格");
			}
		},
		response: function(e, ui) {
			if (ui.content.length == 0) {
		    	$(this).attr("placeholder","請輸入查詢商品規格");
			}
		}           
	});
	
	//處理 agent name 的autocomplete查詢
	$("#search_agent_name").autocomplete({
	     minLength: 1,
	     source: function (request, response) {
	         $.ajax({
	             url : "serviceAgentAssign.do",
	             type : "POST",
	             cache : false,
	             delay : 1500,
	             data : {
	             	action : "autocomplete_name",
	             	product_spec : $("#search_product_spec").val(),
	             	term : request.term
	             },
	             success: function(data) {
	             	var json_obj = $.parseJSON(data);
	             	response($.map(json_obj, function (item) {
	             		return {
							label: item.agent_name,
		                    value: item.agent_name
		               	}
	             	}));
	             },
	             error: function(XMLHttpRequest, textStatus, errorThrown) {
	                 alert_dialog(textStatus);
	             }
	         });
	     },
	     change: function(e, ui) {
	     	 if (!ui.item) {
	             $(this).attr("placeholder","請輸入查詢通路商名稱");
	          }
	     },
	     response: function(e, ui) {
	         if (ui.content.length == 0) {
	             $(this).attr("placeholder","請輸入查詢通路商名稱");
	         }
	     }           
	 });

		
	var selectedClass = 'ui-state-highlight',
    clickDelay = 600,     // click time (milliseconds)
    lastClick, diffClick; // timestamps

	$("#draggable li, #droppable li")
	    .draggable({
	        revertDuration: 10, // grouped items animate separately, so leave this number low
	        containment: '.demo',
	        start: function(e, ui) {
	            ui.helper.addClass(selectedClass);
	        },
	        stop: function(e, ui) {
	            // reset group positions
	            $('.' + selectedClass).css({ top:0, left:0 });
	        },
	        drag: function(e, ui) {
	            // set selected group position to main dragged object
	            // this works because the position is relative to the starting position
	            $('.' + selectedClass).css({
	                top : ui.position.top,
	                left: ui.position.left
	            });
	        }
	    });
	
	$("#droppable, #draggable")
	    .sortable()
	    .droppable({
	    	drop: function(e, ui) {
	            $('.' + selectedClass)
	             .appendTo($(this))
	             .add(ui.draggable) // ui.draggable is appended by the script, so add it after
	             .removeClass(selectedClass)
	             .css({ top:0, left:0 });
	        }
	    });
	
})
</script>
<jsp:include page="header.jsp" flush="true"/>
	<div class="content-wrap">
		<h2 class="page-title">服務識別碼指定通路商作業</h2>
		<!--<div id="msgAlert"></div>-->	
		
		<div class="input-field-wrap">
			<div class="form-wrap">
				<div class="form-row">
					<label for="">
						<span class="block-label">商品規格查詢</span>
						<input type="text" id="search_product_spec" placeholder="請輸入查詢商品規格">
					</label>
					<a href="#" id="btn_query_product" class="btn btn-darkblue">查詢</a>
				</div>
				<div class="form-row">
					<label for="">
						<span class="block-label">通路商名稱</span>
						<input type="text" id="search_agent_name" placeholder="請輸入查詢通路商名稱">
					</label>
					<a href="#" id="btn_query_agent" class="btn btn-darkblue">查詢</a>
				</div>
				<div class="form-row">
					<label for="">
						<span class="block-label">數量</span>
						<input type="text" id="quantity" placeholder="請輸入數量">
					</label>
					<a href="#" id="btn_assign" class="btn btn-darkblue">指定</a>
				</div>
			</div>
		</div>
		
		<div class="search-result-wrap">
			<div class="result-table-wrap">
				<div class="demo">
					<div class="demo-left">
						<p>尚未指定代理商列表</p>    
					    <p id="draggable-cnt">共 0 個</p>
					    <ul id="draggable">
					    </ul>
				    </div>
					<div class="demo-left">
					    <p>已指定代理商列表</p>
					    <p id="droppable-cnt">共 0 個</p>  
					    <ul id="droppable">
					    </ul>
				    </div>
				</div>
			</div>
		</div>
	</div>
<jsp:include page="footer.jsp" flush="true"/>