<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="import.jsp" flush="true"/>

<link rel="stylesheet" href="css/jquery.dataTables.min.css">
<link rel="stylesheet" href="css/styles.css">
<script src="js/jquery.dataTables.min.js"></script>
<style>
.tbl_xml{
	text-align:center;
}

</style>

<script>
	$( function() {
		prepare();
	    $(".tbl_xml").each(function( index ){
	    	var this_tab_name = $(this).attr("name"); 
	    	
		    $(this).DataTable({
		    	dom : "lfr<t>ip",
// 		    	"paging": false,
				destroy : true,
				bAutoWidth:false,
				language : {
					"url" : "js/dataTables_zh-tw.txt"
				},
				ajax : {
					url : "js/sbi/SBI_Data_API.xml",
					dataType: "xml",
					type: "GET",
					dataSrc : function ( xml, status ) {
						
				      	var list = [];
				      	$(xml).find('TAB').each(function(){
				      		if($(this).find("tab_name").text()==this_tab_name){
				      			$(this).find('ROW').each(function(){
				      				
						      		var dataType = $(this).find('dataType').text();
						      		var dataTitle = $(this).find('dataTitle').text();
						      		var dataFormat = $(this).find('dataFormat').text();
						      		var url = $(this).find('url').text();
						      		
						      		list.push({
						      			dataType: dataType,
						      			dataTitle: dataTitle,
						      			dataFormat: dataFormat,
						      			url: url
									});
						      		
						      	});
				      		}
				      	});
				      	
				      	return list;
			      	}
				},
				columns : [ 
					{"data": "dataType", "defaultContent": ""},
					{"data": "dataTitle", "defaultContent": ""},
					{"data": null, "defaultContent": ""}
				],
				columnDefs: [{
					targets: 2,
				   	searchable: false,
				   	orderable: false,
				   	render: function ( data, type, row ) {
				   		var options = $("<div/>") //fake tag
					   		.append( $("<div/>", {"class": "table-row-func btn-in-table btn-gray"}) 
								.append( $("<i/>", {"class": "fa fa-ellipsis-h"}) )
								.append( 
									$("<div/>", {"class": "table-function-list"})
										.append( 
											$("<a/>", {
												"class": "btn-in-table btn-alert btn_download",
												"title": "下載資料",
												"dataFormat": row.data_type,
												"href" : row.url
											})
											.append( $("<i/>", {"class": "fa fa-download"}) )
										)
								)
							);
						
						return options.html();
				   	}
				}]
		    });
	    });
	    $(".tbl_xml").on("click", ".btn_download", function(){
	    	var btn_element = $(this)
	    	var tmp_href = $(this).attr("href");
	    	console.log(btn_element);
	    	setTimeout(function(){
	    		btn_element.attr("href","#");
	    	}, 500);
	    	setTimeout(function(){
	    		btn_element.attr("href",tmp_href); 
	    	}, 10*1000);
	    });
	});
</script>
<jsp:include page="header.jsp" flush="true"/>
	<div class="content-wrap">
		<h2 class="page-title">開放API資料下載</h2>
		<div class="search-result-wrap">
			<div id="tabs">
			  <ul>
			    <li><a href="#tabs-1">POI</a></li>
			    <li><a href="#tabs-2">公司登記類別</a></li>
			    <li><a href="#tabs-3">公司登記清冊</a></li>
			    <li><a href="#tabs-4">公司登記其他</a></li>
			    <li><a href="#tabs-5">公司登記統計</a></li>
			  </ul>
			  <div id="tabs-1">
			    <table class="tbl_xml" name='POI'>
					<thead>
						<tr>
							<th>資料項目</th>
							<th>資料內容</th>
							<th>下載資料</th>
						</tr>
					</thead>
				</table>
			  </div>
			  <div id="tabs-2">
			    <table class="tbl_xml" name='公司登記類別'>
					<thead>
						<tr>
							<th>資料項目</th>
							<th>資料內容</th>
							<th>下載資料</th>
						</tr>
					</thead>
				</table>
			  </div>
			  <div id="tabs-3">
			    <table class="tbl_xml" name='公司登記清冊'>
					<thead>
						<tr>
							<th>資料項目</th>
							<th>資料內容</th>
							<th>下載資料</th>
						</tr>
					</thead>
				</table>
			  </div>
			  <div id="tabs-4">
			    <table class="tbl_xml" name='公司登記其他'>
					<thead>
						<tr>
							<th>資料項目</th>
							<th>資料內容</th>
							<th>下載資料</th>
						</tr>
					</thead>
				</table>
			  </div>
			  <div id="tabs-5">
			     <table class="tbl_xml" name='公司登記統計'>
					<thead>
						<tr>
							<th>資料項目</th>
							<th>資料內容</th>
							<th>下載資料</th>
						</tr>
					</thead>
				</table>
			  </div>
			</div>
		</div>
	</div>
	<script>
	function prepare(){
		$( "#tabs" ).tabs();
	}
	</script>
<jsp:include page="footer.jsp" flush="true"/>