if(!window.action){
	var action={};
}
if(!window.have_visited){
	var have_visited={};
}
if(!window.choosed_node_key){
	var choosed_node_key=-1;
}

function draw_menu_of_poi(parameter){
	$(".content-wrap").append(
		$("<div/>",{
			"id":"panel",
			"style":"display:none;",
			"onmouseover":"$('#panel').css('left','120px');clearTimeout($('#panel').val());",
			"onmouseout":"$('#panel').val(setTimeout(function () { $('#panel').css('left','0px'); }, 800));"
		}).html([
			$("<div/>",{"id":"tree"}),
			$("<div/>",{
				"id":"pin",
				"style":"position:absolute;top:5px;right:20px;",
				"class":"on_it",
				"onclick":"$('#pin').hide();$('#unpin').show();$('#panel').attr('tmp',$('#panel').attr('onmouseout'));$('#panel').attr('onmouseout','');"
			}).html(
				$("<img/>",{"src":"./images/mapMenuPin.png"})
			),
			$("<div/>",{
				"id":"unpin",
				"style":"position:absolute;top:5px;right:20px;display:none;",
				"class":"on_it",
				"onclick":"$('#unpin').hide();$('#pin').show();$('#panel').attr('onmouseout',$('#panel').attr('tmp'));"
			}).html(
				$("<img/>",{"src":"./images/mapMenuUnpin.png"})
			),
			$("<div/>",{
				"style":"z-index:1;background: url(./importPKG/fancy-tree/skin-xp/bg.png) repeat;position:absolute;width:100%;bottom:0px;border-top:2px solid #aaa;",
			}).html($("<div/>",{
				"style":"margin:10px 0px 10px 0px;",
			}).html("<table><tr><td>&nbsp;&nbsp;透明度：</td><td><div id='opacity' style='width:160px;'></div></td></tr></table></div>"))
		])
	);
	
	if(parameter["type"]=="POI"){
		//好像沒用處 又只是翻過來的副產物 先不使用
//		$("#tree").append(
//			$("<div/>",{
//				"id":"pdf_layer",
//				"onmouseover":"if(!window.pdf_layer_timer){pdf_layer_timer=''}clearTimeout(timer);$('#pdf_layer').show();",
//				"onmouseout":"if(!window.pdf_layer_timer){pdf_layer_timer=''}timer=setTimeout(function(){$('#pdf_layer').hide();},500);",
//				"style":"position:absolute;top:200px;left:10px;z-index:30;width:auto;display:none;"
//			}).html("<li><div>Books</div></li>")
//		);
//		$("#pdf_layer").menu();
	}
	$.ajax({
		type : "POST",
		url : "realMap.do",
		//async : false,
		data : parameter,
		success : function(result) {
			json_obj = $.parseJSON(result);
			hidecheckbox(json_obj);
			$("#tree").fancytree({
				aria: true,
				checkbox: true,
				selectMode: 2,
				quicksearch: true,
				focusOnSelect: true,
				source : json_obj,
				click: function (event, data) {
					var node = data.node;
					if($(node.span.childNodes[1]).hasClass('loading')) {
						return false; 
					}
				    if(!data.node.isFolder()){
				    	event.preventDefault();
				    	node.setSelected( !node.isSelected() );
				    	choosed_node_key=data.node.key;
			    		if(action[data.node.key].length==0){
			    			warningMsg("公告","為了提供您更好的使用品質，該功能維護中。")
			    			node.setSelected(false);
			    		}
			    		console.log(action[data.node.key]);
			    		eval(action[data.node.key]);
				    	
				    }else{
//				    	if(have_visited[node.key]==null && $("#scenario_controller").length==0){
//				    		have_visited[node.key]=true;
//				    		eval(action[node.key]);
//				    	}
				    }
				},
				activate: function (event, data) {
				    var node = data.node;
				    if($(node.span.childNodes[1]).hasClass('loading')) {
				    	return false; 
				    }
				    node.setSelected( !node.isSelected() );
				    
//				    if(data.node.isFolder()&&have_visited[node.key]!=null){
//				    	eval(action[data.node.key]);
//				    }
				},
				
				select: function(event, data) {
					var node = data.node;
					if($(node.span.childNodes[1]).hasClass('loading')) {
						return false; 
					}
				},init: function(event, data, flag) {
					$("#panel").show();
			    }
			}).on("mouseover", ".fancytree-title", function(event){
			    var pdf_layer=["19","20","21","22","23","24","25","26","27","28","29","31","32","33","34","35","42","44","46","48"];
			    var node = $.ui.fancytree.getNode(event);
			    if(pdf_layer.indexOf(node.key)!=-1){
			    	if(!window.ebookpath){
			    		ebookpath = "../cdridoc/";
			    	}
			    	//$('#pdf_layer').children().html('<div onclick=\'window.open(\"http://61.218.8.51/SBI/pdf/'+$("#ftal_"+node.key).text().replace('商圈','')+'.pdf\", \"_blank\");\'> '+$("#ftal_"+node.key).text().replace('商圈','')+"電子書"+'</div>');
			    	$('#pdf_layer').children().html('<div onclick=\'window.open(\"'+ebookpath
			    			+$("#ftal_"+node.key).text().replace('商圈','')+'.pdf\", \"_blank\");\'> '
			    			+$("#ftal_"+node.key).text().replace('商圈','')+"電子書"+'</div>');
			    	$('#pdf_layer').css({
			    		"display": "inline",
			    		"top":($("#ftal_"+node.key).offset().top-120),
			    		"left":($("#ftal_"+node.key).offset().left-130+($("#ftal_"+node.key).text().length*12))
			    	});
			    }
			    node.info(event.type);
			});
			
			if(parameter["type"]=="POI"){
				$("#tree").fancytree("getTree").getNodeByKey("36").setExpanded();
			}
		}
	});
}

function hidecheckbox(json){
	var i=0;
	for(item in json){
		i++;
	}
	if(i==0){
		return;
	}
	for (key in json){
		if(key=="folder" && json[key]=="true"){
			json["hideCheckbox"]=true;
		}
		if(key=="action"){
			action[json["key"]]=json[key];
		}
		
		i=0;
		
		for(item in json[key]){
			i++;
		}
		if(i>0 && (typeof json[key]!="string")){
			hidecheckbox(json[key]);
		}
	}
}
