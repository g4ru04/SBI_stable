<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="import.jsp" flush="true"/>
<link rel="stylesheet" href="css/photo/jquery.fileupload.css">
<link rel="stylesheet" href="css/jquery.dataTables.min.css" />
<script type="text/javascript" src="js/jquery-migrate-1.4.1.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="js/additional-methods.min.js"></script>
<script type="text/javascript" src="js/messages_zh_TW.min.js"></script>

<!-- The jQuery UI widget factory, can be omitted if jQuery UI is already included -->
<script src="js/photo/vendor/jquery.ui.widget.js"></script>
<!-- The Load Image plugin is included for the preview images and image resizing functionality -->
<script src="js/photo/load-image.all.min.js"></script>
<!-- The Canvas to Blob plugin is included for image resizing functionality -->
<script src="js/photo/canvas-to-blob.min.js"></script>
<!-- Bootstrap JS is not required, but included for the responsive demo navigation -->
<script src="js/photo/bootstrap.min.js"></script>
<!-- The Iframe Transport is required for browsers without support for XHR file uploads -->
<script src="js/photo/jquery.iframe-transport.js"></script>
<!-- The basic File Upload plugin -->
<script src="js/photo/jquery.fileupload.js"></script>
<!-- The File Upload processing plugin -->
<script src="js/photo/jquery.fileupload-process.js"></script>
<!-- The File Upload image preview & resize plugin -->
<script src="js/photo/jquery.fileupload-image.js"></script>
<!-- The File Upload audio preview plugin -->
<script src="js/photo/jquery.fileupload-audio.js"></script>
<!-- The File Upload video preview plugin -->
<script src="js/photo/jquery.fileupload-video.js"></script>
<!-- The File Upload validation plugin -->
<script src="js/photo/jquery.fileupload-validate.js"></script>
<style>
table.form-table {
 	border-spacing: 10px 8px !important; 
}
</style>

<script>
$(function(){
	
	var validator_insert = $("#insert-dialog-form-post").validate({
		rules : {
		}
	});
	var validator_update = $("#update-dialog-form-post").validate({
		rules : {
		}
	});	
	
	// 查詢商品資料 事件聆聽
	$("#btn_query").click(function(e) {
// 		$.ajax({
// 			type : "POST",
// 			url : "product.do",
// 			data : {
// 				action : "search",
// 				product_spec : $("#search_product_spec").val()
// 			},
// 			success : function(result) {
// 				var json_obj = $.parseJSON(result);
// 				var result_table = "";
				
// 				$.each(json_obj,function(i, item) {
// 					var tmp = "<img src=./image.do?picname="+item.photo+" onerror=\"this.src='images/blank.png'\" style='max-width:100px;max-height:100px'>";
// 					var tmp2 = "<img src=./image.do?action=qrcode&picname="+item.identity_id+".png onerror=\"this.src='images/blank.png'\" style='max-width:100px;max-height:100px'>";
					
// 					result_table 
// 						+= "<tr>"
// 						+ "<td id='product_spec_"+i+"'>" + item.product_spec + "</td>"
// 						+ "<td id='photo_"+i+"' name='"+ item.photo+"'>"+tmp+"</td>"
// 						//+ "<td id='photo_"+i+"'>"+ item.photo + "</td>"
// 						+ "<td id='seed_"+i+"'>"+ item.seed + "</td>"
// 						+ "<td id='identity_id_"+i+"'>"+ item.identity_id + "</td>"
// 						+ "<td>"+ tmp2 + "</td>"
// 						+ "<td><div href='#' class='table-row-func btn-in-table btn-gray'><i class='fa fa-ellipsis-h'></i>"
// 						+ "<div class='table-function-list'>"
// 						+ "<button href='#' name='"+i+"' value='" + item.product_id + "' title='修改' class='btn-update btn-in-table btn-green'><i class='fa fa-pencil'></i></button>"
// 						+ "<button href='#' name='" + item.product_spec + "' value='" + item.product_id + "' title='刪除' class='btn-delete btn-in-table btn-orange'><i class='fa fa-trash'></i></button>"
// 						+ "</div></div></td>"
// 						+ "<td><button value='"+ item.product_id+"' class='btn-iden btn btn-wide btn-primary'>產生</button></td>"
// 						+ "<td><button name='" + i + "' value='"+ item.product_id+"' class='btn-genService btn btn-wide btn-primary'>產生</button></td></tr>";										
// 				});
// 			}
// 		});
		
		$("#table_product").dataTable().fnDestroy();
		$("#table_product").show();
		 
		table = $("#table_product").DataTable({
			dom: 'lfrB<t>ip',
			paging: true,
			ordering: false,
			info: false,
			language: {"url": "js/dataTables_zh-tw.txt"},
			ajax: {
				url : "product.do",
				dataSrc: "",
				type : "POST",
				data : {
					action : "search",
					product_spec : $("#search_product_spec").val()
				}
			},
			columnDefs: [
				{
					targets: 1,
					searchable: false,
					orderable: false,
					render: function ( data, type, row ) {
						var ch = "<img src=./image.do?picname="+row.photo+" onerror=\"this.src='images/blank.png'\" style='max-width:100px;max-height:100px'>";
						return ch;
					} 
				},
				{
					targets: -4,
					searchable: false,
					orderable: false,
					render: function ( data, type, row ) {
						var ch = "<img src=./image.do?action=qrcode&picname="+row.identity_id+".png onerror=\"this.src='images/blank.png'\" style='max-width:100px;max-height:100px'>";
						return ch;
					} 
				},
				{
					targets: -3,
					searchable: false,
					orderable: false,
					render: function ( data, type, row ) {
						var ch = 
							"<div href='#' class='table-row-func btn-in-table btn-gray'>"
								+ "<i class='fa fa-ellipsis-h'></i>"
								+ "<div class='table-function-list'>"
								+ "<button href='#' name='"+ row.product_id +"' value='" + row.product_id + "' title='修改' class='btn-update btn-in-table btn-green'><i class='fa fa-pencil'></i></button>"
								+ "<button href='#' name='" + row.product_spec + "' value='" + row.product_id + "' title='刪除' class='btn-delete btn-in-table btn-orange'><i class='fa fa-trash'></i></button>"
								+ "</div>"
							+ "</div>";
						return ch;
					} 
				},
				{
					targets: -2,
					searchable: false,
					orderable: false,
					render: function ( data, type, row ) {
						var ch = "<button value='"+ row.product_id+"' class='btn-iden btn btn-wide btn-primary'>產生</button>";
						return ch;
					} 
				},
				{
					targets: -1,
					searchable: false,
					orderable: false,
					render: function ( data, type, row ) {
						var ch = "<button value='"+ row.product_id+"' class='btn-genService btn btn-wide btn-primary'>產生</button>";
						return ch;
					} 
				}				        
			],
			columns: [
				{"data": "product_spec" ,"defaultContent":""},
				{"data": "photo" ,"defaultContent":""},
				{"data": "seed" ,"defaultContent":""},
				{"data": "identity_id" ,"defaultContent":""},
				{"data": null ,"defaultContent":""},
				{"data": null ,"defaultContent":""},
				{"data": null ,"defaultContent":""},
				{"data": null ,"defaultContent":""}
			]				      	      
		});

	});
	
	// 新增商品資料 事件聆聽
	$("#btn_insert_product").click(function(e) {
		e.preventDefault();		
		$("#files").html('');
		$("#photo0").val('');
		insert_dialog.dialog("open");
	});
	
	// "新增商品資料" Dialog相關設定
	insert_dialog = $("#dialog-form-insert").dialog({
		draggable : false,//防止拖曳
		resizable : false,//防止縮放
		autoOpen : false,
		show : {
			effect : "clip",
			duration : 500
		},
		hide : {
			effect : "fade",
			duration : 500
		},
		width : 'auto',
		modal : true,
		closeOnEscape: false,
	    open: function(event, ui) {
	        $(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
	    },
		buttons : [{
			id : "insert",
			text : "新增",
			click : function() {
				if ($('#insert-dialog-form-post').valid()) {
					$.ajax({
						type : "POST",
						url : "product.do",
						data : {
							action : "insert",
							product_spec: $("#insert_product_spec").val(),
							photo: $("#photo0").val(),
							seed: $("#insert_seed").val()
						},
						success : function(result) {
							var json_obj = $.parseJSON(result);
							$("#btn_query").trigger('click');
						}
					});
					insert_dialog.dialog("close");
				}
			}
		}, {
			text : "取消",
			click : function() {
				validator_insert.resetForm();
				$("#insert-dialog-form-post").trigger("reset");
				insert_dialog.dialog("close");
			}
		} ],
		close : function() {
			validator_insert.resetForm();
			$("#insert-dialog-form-post").trigger("reset");
			insert_dialog.dialog("close");
		}
	});
	
	// 修改 事件聆聽
	$("#table_product").delegate(".btn-update", "click", function(e) {
		e.preventDefault();
		
		$("#files-update").html('');
		$("#photo0-update").val('');
		
		var row = $(this).closest('tr');
		var data = $("#table_product").dataTable().fnGetData(row);
		
		$("#dialog-form-update input[name='product_spec']").val( data.product_spec );
		$("#dialog-form-update input[name='seed']").val( data.seed );
		
		update_dialog
			.data("product_id", data.product_id)
			.data("photo", data.photo)
			.dialog("open");
	});
	
	// "修改" Dialog相關設定
	update_dialog = $("#dialog-form-update").dialog({
		draggable : false,//防止拖曳
		resizable : false,//防止縮放
		autoOpen : false,
		show : {
			effect : "clip",
			duration : 500
		},
		hide : {
			effect : "fade",
			duration : 500
		},
		width : 'auto',
		modal : true,
		buttons : [{
			id : "update",
			text : "修改",
			click : function() {
				if($("#photo0-update").val()==""){
					$("#photo0-update").val( $(this).data("photo") );					
				}
// 				if ($('#update-dialog-form-post').valid()) {
					$.ajax({
						type : "POST",
						url : "product.do",
						data : {
 							action : "update",
 							product_id: $(this).data("product_id"),
 							product_spec: $("#update_product_spec").val(),
 							photo : $("#photo0-update").val(),
 							seed : $("#update_seed").val()
						},
						success : function(result) {
							var json_obj = $.parseJSON(result);
							$("#btn_query").trigger('click');
						}
					});
					update_dialog.dialog("close");
// 				}
			}
		}, {
			text : "取消",
			click : function() {
				validator_update.resetForm();
				$("#update-dialog-form-post").trigger("reset");
				update_dialog.dialog("close");
			}
		} ],
		close : function() {
			$("#update-dialog-form-post").trigger("reset");
			validator_update.resetForm();
			update_dialog.dialog("close");
		}
	});
	
	//刪除事件聆聽 : 因為聆聽事件動態產生，所以採用delegate來批量處理，節省資源
	$("#table_product").delegate(".btn-delete", "click", function(e) {
		e.preventDefault();
		
		var row = $(this).closest('tr');
		var data = $("#table_product").dataTable().fnGetData(row);
		
		$("#delete_product_spec").html( data.product_spec );
		del_dialog
			.data("product_id", data.product_id)
			.dialog("open");
	});
	
	// "刪除" Dialog相關設定
	del_dialog = $("#dialog-form-delete").dialog({
		draggable : false,//防止拖曳
		resizable : false,//防止縮放
		autoOpen : false,
		show : {
			effect : "clip",
			duration : 500
		},
		hide : {
			effect : "fade",
			duration : 500
		},
		height : 'auto',
		modal : true,
		buttons : [{
			id : "delete",
			text : "確認刪除",
			click : function() {
				$.ajax({
					type : "POST",
					url : "product.do",
					data : {
						action: "delete",
						product_id: $(this).data("product_id")
					},
					success : function(result) {
						var json_obj = $.parseJSON(result);
						$("#btn_query").trigger('click');
					}
				});
				$(this).dialog("close");
			}
		}, {
			text : "取消",
			click : function() {
				$(this).dialog("close");
			}
		} ],
		close : function() {
			$(this).dialog("close");
		}
	});

 	// 取得商品識別碼 事件聆聽 
	$("#table_product").delegate(".btn-iden", "click", function(e) {
		e.preventDefault();
		
		var row = $(this).closest('tr');
		var data = $("#table_product").dataTable().fnGetData(row);
		
		identitiy_dialog
			.data("product_id", data.product_id)
			.dialog("open");
	});
 	
	// "取得商品識別碼" Dialog相關設定
	identitiy_dialog = $("#dialog-form-identity").dialog({
		draggable : false,//防止拖曳
		resizable : false,//防止縮放
		autoOpen : false,
		show : {
			effect : "clip",
			duration : 500
		},
		hide : {
			effect : "fade",
			duration : 500
		},
		height : 'auto',
		modal : true,
		buttons : [{
			id : "gen_identity",
			text : "確認",
			click : function() {
				$.ajax({
					type : "POST",
					url : "product.do",
					data : {
						action: "gen_identity",
						product_id: $(this).data("product_id")
					},
					success : function(result) {
						var json_obj = $.parseJSON(result);
						$("#btn_query").trigger('click');
					}
				});
				$(this).dialog("close");
			}
		}, {
			text : "取消",
			click : function() {
				$(this).dialog("close");
			}
		} ],
		close : function() {
			$(this).dialog("close");
		}
	});	
	
	// 產生服務識別碼 事件聆聽
	$("#table_product").delegate(".btn-genService", "click", function(e) {
		e.preventDefault();
		
		$("#files-update").html('');
		$("#photo0-update").val('');
		
		var row = $(this).closest('tr');
		var data = $("#table_product").dataTable().fnGetData(row);
		
		$("#dialog-form-genService input[name='product_spec']").val( data.product_spec );
		
		genService_dialog
			.data("product_id", data.product_id)
			.data("photo", data.photo)
			.dialog("open");
	});
	
	// "產生服務識別碼" Dialog相關設定
	genService_dialog = $("#dialog-form-genService").dialog({
		draggable : false,//防止拖曳
		resizable : false,//防止縮放
		autoOpen : false,
		show : {
			effect : "clip",
			duration : 500
		},
		hide : {
			effect : "fade",
			duration : 500
		},
		width : 'auto',
		modal : true,
		buttons : [{
			id : "update",
			text : "產生",
			click : function() {
// 				if ($('#genService-dialog-form-post').valid()) {
					$.ajax({
						type : "POST",
						url : "productService.do",
						data : {
 							action : "genService",
 							product_id: $(this).data("product_id"),
 							quantity: $("#genServcie_quantity").val()
						},
						success : function(result) {
							var json_obj = $.parseJSON(result);
							$("#btn_query").trigger('click');
						}
					});
					genService_dialog.dialog("close");
// 				}
			}
		}, {
			text : "取消",
			click : function() {
				//validator_update.resetForm();
				$("#genService-dialog-form-post").trigger("reset");
				genService_dialog.dialog("close");
			}
		} ],
		close : function() {
			$("#genService-dialog-form-post").trigger("reset");
			//validator_update.resetForm();
			genService_dialog.dialog("close");
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
	             	}));
	             },
	             error: function(XMLHttpRequest, textStatus, errorThrown) {
	                 alert_dialog(textStatus);
	             }
	         });
	     },
	     change: function(e, ui) {
	     	 if (!ui.item) {
// 	     		 $(this).val("");
	             $(this).attr("placeholder","請輸入查詢商品規格");
	          }
	     },
	     response: function(e, ui) {
	         if (ui.content.length == 0) {
// 	             $(this).val("");
	             $(this).attr("placeholder","請輸入查詢商品規格");
	         }
	     }           
	});
	
	//<!-- photo section jquery begin by Melvin -->
	'use strict';
    var url = '/sbi/photo.do',
        uploadButton = $('<button/>')
            .addClass('btn btn-primary')
            .prop('disabled', true)
            .text('處理中...')
            .on('click', function (e) {
            	e.preventDefault();
            	
            	var $this = $(this),
                    data = $this.data();
                $this
                    .off('click')
                    .text('Abort')
                    .on('click', function () {
                        $this.remove();
                        data.abort();
                    });
                data.submit().always(function () {
                    $this.remove();
                });
            });
                
    $('#fileupload').fileupload({
        url: url,
        dataType: 'json',
        autoUpload: false,
        acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
        maxFileSize: 600000,
        disableImageResize: /Android(?!.*Chrome)|Opera/
            .test(window.navigator.userAgent),
        previewMaxWidth: 200,
        previewMaxHeight: 200,
        previewCrop: true
    }).on('fileuploadadd', function (e, data) {
        data.context = $('<div/>').appendTo('#files');
        $.each(data.files, function (index, file) {
            var node = $('<p/>')
                    .append($('<span/>').text(file.name));
            if (!index) {
                node
                    .append('<br>')
                    .append(uploadButton.clone(true).data(data));
            }
            node.appendTo(data.context);
            //$("#photo0").val(file.name);
        });
    }).on('fileuploadprocessalways', function (e, data) {
        var index = data.index,
            file = data.files[index],
            node = $(data.context.children()[index]);
        if (file.preview) {
            node
                .prepend('<br>')
                .prepend(file.preview);
        }
        if (file.error) {
            node
                .append('<br>')
                .append($('<span class="text-danger"/>').text(file.error));
        }
        if (index + 1 === data.files.length) {
            data.context.find('button')
                .text('上傳')
                .prop('disabled', !!data.files.error);
        }
    }).on('fileuploadprogressall', function (e, data) {
        var progress = parseInt(data.loaded / data.total * 100, 10);
        $('#progress .progress-bar').css(
            'width',
            progress + '%'
        );
    }).on('fileuploaddone', function (e, data) {
        $.each(data.result.files, function (index, file) {
        	$("#photo0").val(file.name);/////////////////////////////////////
            if (file.url) {
                var link = $('<a>')
                    .attr('target', '_blank')
                    .prop('href', file.url);
                $(data.context.children()[index])
                    .wrap(link);
            } else if (file.error) {
                var error = $('<span class="text-danger"/>').text(file.error);
                $(data.context.children()[index])
                    .append('<br>')
                    .append(error);
            }
        });
    }).on('fileuploadfail', function (e, data) {
    	$.each(data.files, function (index) {
            var error = $('<span class="text-danger"/>').text('File upload failed.');
            $(data.context.children()[index])
                .append('<br>')
                .append(error);
        });
    }).prop('disabled', !$.support.fileInput)
        .parent().addClass($.support.fileInput ? undefined : 'disabled');

	$('#fileupload-update').fileupload({
        url: url,
        dataType: 'json',
        autoUpload: false,
        acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
        maxFileSize: 600000,
        disableImageResize: /Android(?!.*Chrome)|Opera/
            .test(window.navigator.userAgent),
        previewMaxWidth: 200,
        previewMaxHeight: 200,
        previewCrop: true
    }).on('fileuploadadd', function (e, data) {
        data.context = $('<div/>').appendTo('#files-update');
        $.each(data.files, function (index, file) {
            var node = $('<p/>')
                    .append($('<span/>').text(file.name));
            if (!index) {
                node
                    .append('<br>')
                    .append(uploadButton.clone(true).data(data));
            }
            node.appendTo(data.context);
            $("#photo-update").val(file.name);
        });
    }).on('fileuploadprocessalways', function (e, data) {
        var index = data.index,
            file = data.files[index],
            node = $(data.context.children()[index]);
        if (file.preview) {
            node
                .prepend('<br>')
                .prepend(file.preview);
        }
        if (file.error) {
            node
                .append('<br>')
                .append($('<span class="text-danger"/>').text(file.error));
        }
        if (index + 1 === data.files.length) {
            data.context.find('button')
                .text('上傳')
                .prop('disabled', !!data.files.error);
        }
    }).on('fileuploadprogressall', function (e, data) {
        var progress = parseInt(data.loaded / data.total * 100, 10);
        $('#progress .progress-bar').css(
            'width',
            progress + '%'
        );
    }).on('fileuploaddone', function (e, data) {
    	$.each(data.result.files, function (index, file) {
        	$("#photo0-update").val(file.name);
            if (file.url) {
                var link = $('<a>')
                    .attr('target', '_blank')
                    .prop('href', file.url);
                $(data.context.children()[index])
                    .wrap(link);
            } else if (file.error) {
                var error = $('<span class="text-danger"/>').text(file.error);
                $(data.context.children()[index])
                    .append('<br>')
                    .append(error);
            }
        });
    }).on('fileuploadfail', function (e, data) {
    	$.each(data.files, function (index) {
            var error = $('<span class="text-danger"/>').text('File upload failed.');
            $(data.context.children()[index])
                .append('<br>')
                .append(error);
        });
    }).prop('disabled', !$.support.fileInput)
        .parent().addClass($.support.fileInput ? undefined : 'disabled');  

})
</script>
<jsp:include page="header.jsp" flush="true"/>
<div class="content-wrap">
	<h2 class="page-title">商品管理</h2>
	<div class="input-field-wrap">
		<div class="form-wrap">
			<div class="form-row">
				<label for=""> <span class="block-label">商品規格查詢</span> 
				<input type="text" id="search_product_spec" placeholder="請輸入查詢商品規格">
				</label> <a href="#" id="btn_query" class="btn btn-darkblue">查詢</a>
			</div>
			<div class="btn-row">
				<a href="#" id="btn_insert_product" class="btn btn-exec btn-wide">新增商品資料</a>
			</div>
		</div>
		<!-- /.form-wrap -->
	</div>
	<!-- /.input-field-wrap -->

	<div class="search-result-wrap">
		<div class="result-table-wrap">
			<table id="table_product" class="result-table">
				<thead>
					<tr>
						<th>商品規格</th>
						<th>商品圖片名稱</th>
						<th>加密因子</th>
						<th>商品識別碼</th>
						<th>商品識別碼QR code</th>
						<th>功能</th>
						<th>取得商品識別碼</th>
						<th>取得服務識別碼</th>
					</tr>
				</thead>
				<tbody style="text-align: center">
				</tbody>
			</table>
		</div>
	</div>

	<!--對話窗樣式-新增 -->
	<div id="dialog-form-insert" title="新增資料" style="display: none">
		<form name="insert-dialog-form-post" id="insert-dialog-form-post"
			style="display: inline">
			<table style="border-collapse: separate; border-spacing: 10px 20px;">
				<tbody>
					<tr>
						<td><p>商品規格：</p></td>
						<td><input type="text" id="insert_product_spec" name="product_spec"></td>
						<td><p>加密因子：</p></td>
						<td><input type="text" id="insert_seed" name="seed"></td>
					</tr>
				</tbody>
			</table>
			<!-- photo section begin by Melvin -->
			<table class='form-table'>
				<tbody>
					<tr>
						<td>商品圖片：</td>
						<td>
						<span
							class="btn btn-success fileinput-button btn-primary"
							style="padding: 6px 12px; border-radius: 5px;"> 
							<span>
								<font color="white">+&nbsp;</font>瀏覽<font color="red">(最大500K)</font>
							</span>
							<input id="fileupload" type="file" name="files[]"> <br>
						</span>
							<div id="files" class="files"></div></td>
					</tr>
				</tbody>
			</table>
			<!-- photo section end by Melvin -->
		</form>
	</div>

	<!--對話窗樣式-修改 -->
	<div id="dialog-form-update" title="修改資料" style="display: none">
		<form name="update-dialog-form-post" id="update-dialog-form-post">
			<table style="border-collapse: separate; border-spacing: 10px 20px;">
				<tbody>
					<tr>
						<td><p>商品規格：</p></td>
						<td><input type="text" id="update_product_spec" name="product_spec"></td>
						<td><p>加密因子：</p></td>
						<td><input type="text" id="update_seed" name="seed"></td>
					</tr>
				</tbody>
			</table>
			
			<!-- photo section begin by Melvin -->
			<table class="form-table">
				<tbody>
					<tr>
						<td>商品圖片：</td>
						<td><span
							class="btn btn-success fileinput-button btn-primary"
							style="padding: 6px 12px; border-radius: 5px;"> <span><font
									color="white">+&nbsp;</font>瀏覽<font color="red">(最大500K)</font></span>
								<input id="fileupload-update" type="file" name="files-update[]">
								<br>
						</span>
							<div id="files-update" class="files"></div></td>
					</tr>
				</tbody>
			</table>
			<!-- photo section end by Melvin -->
		</form>
	</div>

	<!--對話窗樣式-產生服務識別碼 -->
	<div id="dialog-form-genService" title="產生服務識別碼" style="display: none">
		<form name="genService-dialog-form-post"
			id="genService-dialog-form-post">
			<table style="border-collapse: separate; border-spacing: 10px 20px;">
				<tbody>
					<tr>
						<td><p>商品規格：</p></td>
						<td><input type="text" id="genServcie_product_spec"
							name="product_spec"></td>
						<td><p>數量：</p></td>
						<td><input type="text" id="genServcie_quantity" name="seed"
							value="0"></td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>

	<!--對話窗樣式-刪除 -->
	<div id="dialog-form-delete" title="確認刪除資料嗎?" style="display: none">
		<form name="delete-dialog-form-post" id="delete-dialog-form-post"
			style="display: inline">
			<p>是否確認刪除:</p>
			<div style="text-align: center">
				<label id="delete_product_spec"></label>
			</div>
		</form>
	</div>

	<!--對話窗樣式-產生商品識別碼 -->
	<div id="dialog-form-identity" title="確認產生商品識別碼?" style="display: none">
		<form name="identity-dialog-form-post" id="identity-dialog-form-post"
			style="display: inline">
			<p>確認是否產生?:</p>
		</form>
	</div>
</div>
<input type="text" id="photo0" style="display:none"/>
	<input type="text" id="photo0-update" style="display:none"/>
<jsp:include page="footer.jsp" flush="true"/>
