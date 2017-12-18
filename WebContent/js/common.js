/**
 * 存放共用的 function
 * 20170815 Ben
 * 
 * 一覽:
 * warningMsg("警告","請輸入密碼");
 * warningMsgFunc("警告","按確定時alert",function(){alert('123');})
 * var tmp_id = UUID();
 * x = null2str(unknown_variable_x);
 * ans = b64EncodeUnicode("Ben");
 * 
 * ans = b64DecodeUnicode("QmVu");
 * 傳遞參數有時會這樣用
 * req = encodeURI(b64EncodeUnicode("Ben").replace(/\+/g,"%2B");
 * res = decodeURI(b64DecodeUnicode("QmVu"))
 * num = money_format(str_or_int_Number);
 * act = getUrlParameter("action")
 * genSelect(inputElementName,[{"key":"somekey","value":"it's label"}])
 * genAutocomplete(inputTextElementName,[{"key":"somekey"},{"key":"somekey2"}])
 * 
 * 		<div class="btn" title='somethingToShow'>
 * 		tooltip("btn");
 * 
 * loading("setting_pic");
 * loading();
 * loading("over");
 * 
 * setting_weAreAllTogether();
 * weAreAllTogether.attach("桃園市",function(){alert('123');});
 * weAreAllTogether.touch("桃園市");
 * 
 * check_name 當初用在全選 太單一可能會從common移除
 * check_includes 檢查是否include 某js檔 或某css檔 採用建言未實用
 * 
 * show_if_significant('Ben',"<tr><td>#P#</td></tr>,<tr><td>#P#</td></tr>")
 */

var a;
/* 關閉自定義 errorhandlerlog 請解開右邊 註解 → */
//if(window.location.href.indexOf("localhost")==-1 || true){
	window.onerror = function(msg, url, line, col, error) {
		
		setTimeout(function(){
			errorHandling = 1;
			console.log(msg);
			var extra = !error ? '' :""+error.stack.replace(/\n/g,"</td></tr>" + "<tr><td>").replace(/@/g,"&nbsp;at&nbsp;</td><td>");
			var log_msg = "<table style='font-family: Microsoft JhengHei;'>"
						+((!window.html2canvas)||(!window.printscreen)?"":"<tr><td>當時截圖:&nbsp;</td><td><a href='"+window.location.href.split("sbi/")[0]+"sbi/record.do?action=get_image&png_name="+printscreen("body")+"'>截圖</a></td></tr>")
						+ "<tr><td>錯誤訊息:&nbsp;</td><td>" + JSON.stringify(msg) + "</td></tr>"
						+ "<tr><td>錯誤頁面:&nbsp;</td><td>" + JSON.stringify(url) + "</td></tr>"
						+ "<tr><td>發生行數:&nbsp;</td><td>" + line + "</td></tr>"
						+ "<tr><td>堆疊資訊:&nbsp;</td><td></td></tr>"
						+ "<tr><td colspan='3'style='padding-left:18px;'>"
							+ "<table style='font-family: Microsoft JhengHei;'>"
							+ "<tr><td>" + extra 
							+ "</td></tr></table>"
						+ "</td><tr>"
						+ "</table>";
//			console.log(error.stack);
//			console.log(extra);
//			console.log(log_msg);
		//	$.ajax({
		//		type : "POST",
		//		url : "https://api.twilio.com/2010-04-01/Accounts/AC4df5ca98148c3665be7914bc79583775/Messages.json",
		//		dataType : 'json',
		//		async : false,
		//		headers : {
		//			"Authorization" : "Basic "+ btoa("AC4df5ca98148c3665be7914bc79583775:1b7154b41b16ba056124b072aa0291bc")
		//		},
		//		data : {
		//			"To" : "+886919863010",
		//			"From" : "+13346001946",
		//			"Body" : "SBI Error Please Check EMail"
		//		},
		//		success : function(result) {
		//			alert('Thanks for your comment!');
		//		}
		//	});	
		//	$.ajax({type : 'POST',url : 'https://api.twilio.com/2010-04-01/Accounts/AC4df5ca98148c3665be7914bc79583775/Messages.json',dataType : 'json',async : false,headers : {'Authorization' : 'Basic '+ btoa('AC4df5ca98148c3665be7914bc79583775:1b7154b41b16ba056124b072aa0291bc')},data : {'To' : '+886919863010','From' : '+13346001946','Body' : 'SBI Error Please Check EMail'},success : function(result) {alert('Thanks for your comment!');}});
		//	beforeSend: function (xhr) {
		//	    xhr.setRequestHeader ("Authorization", "Basic " + btoa(username + ":" + password));
		//	},
			
			
			var xmlhttp;
			if (window.XMLHttpRequest) {
				xmlhttp = new XMLHttpRequest();
			} else {
				xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
			}
		
			xmlhttp.open("GET", window.location.href.split("sbi/")[0]
					+ "sbi/recordMsg.do?level=debug&message=" + b64EncodeUnicode("前端頁面錯誤:<br>"+log_msg).replace(/\+/g,"%2B"), true);
			xmlhttp.send();
		
			var suppressErrorAlert = true;
			return suppressErrorAlert;
		},2000);
		
	};
//}
	$(function() {
	    $.ajaxSetup({
	    	beforeSend: function(xhr) {
	    		if(this.data=="action=get_session"){
	    			return ;
	    		}
	    		var xmlhttp;
	        	if (window.XMLHttpRequest) {
	        		xmlhttp = new XMLHttpRequest();
	        	} else {
	        		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	        	}
	        	
	        	var this_ptr = this;
	        	var tmp_ajaxdata = "";
	        	var content = "";
	        	try {
	        		tmp_ajaxdata = this_ptr.data==null?"":JSON.stringify(this_ptr.data).substring(0,300);
		        	content = "'"+this_ptr.type+"'>"
		        				+"'"+ window.location.href.replace(window.location.href.split("/").pop(),this_ptr.url)+"?"+tmp_ajaxdata+"'";
        		}catch (e) {
        			content = "!!Fatal: "+JSON.stringify(this_ptr);
        		}
        		
	        	
		    	xmlhttp.open("GET", window.location.href.split("sbi/")[0]
					+ "sbi/recordMsg.do?action=recordANAL"
					+"&act="+b64EncodeUnicode("ajaxSend").replace(/\+/g,"%2B")
					+"&content="+b64EncodeUnicode(content).replace(/\+/g,"%2B"), true);
		    		xmlhttp.send();
	    	},
	        error: function(jqXHR, exception,errorThrown) {
	        	if(jqXHR.status==0){
	        		return ;//零只是因為還沒丟就跳頁了 
	        	}
	        	var log_msg =  'Ajax.ERROR'
	        				+ 'send:['+JSON.stringify(this.data)
	        				+ ']to:['+this.url+']'
	        				+ 'Get:[' + jqXHR.status + "-" + jqXHR.statusText +']';
	        	
	        	console.log(log_msg);
	        	var xmlhttp;
	        	if (window.XMLHttpRequest) {
	        		xmlhttp = new XMLHttpRequest();
	        	} else {
	        		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	        	}
	        	
	        	var this_ptr = this;
	        	setTimeout(function(){
	        		errorHandling = 1;
		    		xmlhttp.open("GET", window.location.href.split("sbi/")[0]
					+ "sbi/recordMsg.do?level=debug&message="+b64EncodeUnicode(
							"前端頁面錯誤(AJAX):"
							+ "<table style='font-family: Microsoft JhengHei;'>"
							+ ((!window.html2canvas)||(!window.printscreen)?"":"<tr><td>當時截圖:&nbsp;</td><td><a href='"+window.location.href.split("sbi/")[0]+"sbi/record.do?action=get_image&png_name="+printscreen("body")+"'>截圖</a></td></tr>")
							+ "<tr><td>錯誤訊息:&nbsp;</td><td>"+jqXHR.status + "-" + jqXHR.statusText+"</td></tr>" 
							+ "<tr><td>發送頁面:&nbsp;</td><td>"+window.location.href+"</td></tr>"
							+ "<tr><td>發送給誰:&nbsp;</td><td>"+window.location.href.replace(window.location.href.split("/").pop(),this_ptr.url)+"</td></tr>"
							+ "<tr><td>發送參數:&nbsp;</td><td>"+(JSON.stringify(this_ptr.data).match(/.{50}/g)!=null?JSON.stringify(this_ptr.data).match(/.{50}/g).join("&nbsp;<br>"):"")+"</td></tr>"
							+ "<tr><td>發送方式:&nbsp;</td><td>"+this_ptr.type+"</td></tr>"
							+ "<tr><td>是否異步:&nbsp;</td><td>"+this_ptr.async+"</td></tr>"
							+ "<tr><td>回傳頁面:</td></tr>"
							+ "<tr><td colspan='3'><div style='max-width:400px;max-height:400px;border:2px solid #833;overflow-y: scroll;'>"
							+ "<pre>"
								+jqXHR.responseText.replace(/</g, "&lt;")
	//							+jqXHR.responseText.replace(/<\/?[^>]+(>|$)/g, "")
							+"</pre>"
							+"</div></td></tr></table>"
						).replace(/\+/g,"%2B"), true);
		    		xmlhttp.send();
	        	},2000);
	        }
	    });
	});
/* 關閉自定義 errorhandlerlog 請解開右上 註解  */

function warningMsg(title, msg) {
	$("<div/>").html(msg).dialog({
		title: title,
		draggable : true,
		resizable : false,
		autoOpen : true,
		height : "auto",
//		width : "auto",
		modal : true,
		open : function(){
			var this_dialog = $(this);
			setTimeout(function(){
				this_dialog.parent().next(".ui-widget-overlay").css("z-index",getStrongestZindex('loading'));
				getStrongestZindex();
				getStrongestZindex();
//				this_dialog.parent().css("width","auto");
				this_dialog.parent().css("z-index",getStrongestZindex('loading'));
			},10);
		},
		buttons : [{
			text: "確認", 
			click: function() { 
				$(this).dialog("close");
			}
		}]
	});
}

function UUID(){
	return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
	    var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
	    return v.toString(16);
	});
}

function null2str(str){
	if(str==null){
		return "";
	}else{
		return str;
	}
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

function money_format(moneyStr){
	if(isNaN(moneyStr)){
		console.log( "input not a number" );
		return "";
	}
	var const_str = moneyStr + "", deal = moneyStr + "";
	var buf="";
	var minus=1;
	while(deal.length-3>0){
		buf = "," + deal.substring(deal.length-3, deal.length) + buf;
		deal = deal.substring(0,const_str.length-minus*3);
		minus++;
	}
	buf = deal.substring(0,deal.length) + buf;
	
	return buf;
}

function tooltip(clas){
	
	$("."+clas).each(function() {
//		$(".tips_on_barchart").each(function(){$(this).addClass("a123");})
		if(!$(this).hasClass("tooltip_over")){
			
			$(this).addClass("tooltip_over");
			$(this).mouseover(function(e){
				 $(this).attr("newTitle",$(this).attr("title"));
				 $(this).attr("title","")
				 var tooltip = "<div id='tooltip'>"+  $(this).attr("newTitle") +"<\/div>";
				 $("body").append(tooltip);
				 $("#tooltip").css({"top": (e.pageY+20) + "px","left": (e.pageX+10)  + "px"}).show();//.show("fast");
			 }).mouseout(function(){
				 	$(this).attr("title",$(this).attr("newTitle"));
			        $("#tooltip").remove();
			 }).mousemove(function(e){
			         $("#tooltip").css({"top": (e.pageY-20) + "px","left": (e.pageX+10)  + "px"});
			 });
			
//			$(this).addClass("tooltip_over");
		}
	});
//	$("."+clas).mouseover(function(e){
//		 this.newTitle = $(this).attr("title");
//		 this.title = "";
//		 var tooltip = "<div id='tooltip'>"+ this.newTitle +"<\/div>";
//		 $("body").append(tooltip);
//		 $("#tooltip").css({"top": (e.pageY+20) + "px","left": (e.pageX+10)  + "px"}).show("fast");
//	 }).mouseout(function(){
//	         this.title = this.newTitle;
//	         $("#tooltip").remove();
//	 }).mousemove(function(e){
//	         $("#tooltip").css({"top": (e.pageY-20) + "px","left": (e.pageX+10)  + "px"});
//	 });
}

function loading(str){
	//就是個普通遮罩
	//但貌似有很多vague issue
	//例如 登入時間時快時慢不知道是0.1秒還是5秒 所以多了settimeout 如果超過0.4秒才顯示
	//又例如 使用之後0.4秒之內 process就被hang住了 忙著畫google地圖 忙完要顯示的時候就解決了 遮罩就沒有用到了 所以肯定會很久的 增加now指令(商圈資訊 > 城市那邊貌似不會用到還沒處理)
	//再例如 process就被hang住了 gif檔案也不會動 所以沒意義 所以增加setting_pic不然預設是文字
	//loading() 開始命令
	//loading('over') 結束命令
	//loading(err_msg) 異常情況
	if(!window.loading_count){
		loading_count=0;
	}
	if(!window.loading_timeout){
		loading_timeout=[];
	}
	
	if(str=="setting_pic"){
		loading_with_pic=1;
		return;
	}
	
	if($("#loading").length==0){
		$("body").append('<div id="loading" style="display: none;padding:15px;">'
 			+(window.loading_with_pic?'<img src="./images/loading.gif">':'<div>資料讀取中，請稍候‧‧‧</div>')
			+'</div>');
	}
//	alert(str);
//	console.log(" ## "+str);
	if(str!=null && str!="now"){
		if(str!="over"){
			console.log("loading_error:"+str);
		}
		loading_count--;
		
		clearTimeout(loading_timeout.shift());
		
		if(loading_count<=0){
			loading_count = 0;
			$("#loading").hide();
		}
	}else{
		loading_count++;
		$("#loading").css("z-index",(getStrongestZindex("loading")+""));
		if(str=="now"){
			$("#loading").show();
			loading_timeout.push (0);
		}else{
			loading_timeout.push ( 
				setTimeout(function(){
					$("#loading").show();
				},400)
			);
		}
	}
}

function getUrlParameter(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function genSelect(element, data) {
/* 以array of javascript object 產生select選單 */
     var target = $("select[name='" + element + "']");

     target.find('option').remove();

     target.append(
         $("<option>", {
              value: "",
              html: "&nbsp;請選擇"
         })
     );

     $.each(data, function(index, object){
         target
              .append(
                  $("<option>", {
                       value: object.key,
                       html: object.value
                  })
              );
     });
}

function genAutocomplete(element, data){
	/* 以array of javascript object 產生autocomplete選單 */
	//data_example: [{"key":"aa","value":"AA"},{"key":"bb","value":"BB"}]
	
	var data_arr = [];
	data.map(function(a){
		data_arr.push(a["key"]);
	});
	
	var target = $("input[name='" + element + "']");
	target.autocomplete({
        source: data_arr,
        minLength: 0
    });
	
//	target.mouseover(function(){
//		$(this).autocomplete("search");
//	});
	
	target.click(function(){
		$(this).autocomplete("search");
	});
	
//	target.keypress(function (e) {
//	    if (e.which == 32) {
//	    	if($(this).val()==""){
//	    		e.preventDefault();
//	    		$(this).autocomplete("search");
//	    	}
//	    }
//	});
	
}

function warningMsgFunc(title, msg, func) {
	
	$("<div/>").html(msg).dialog({
		title: title,
		draggable : true,
		resizable : false,
		autoOpen : true,
		height : "auto",
		modal : true,
		buttons : [{
			text: "確認", 
			click: function() { 
				$(this).dialog("close");
				if(func!=null){
					func();
				}
			}
		}]
	});
}

function check_name(name){
	$("input[name='"+name+"']").prop("checked",true);
	$("input[name='"+name+"']").trigger("change");
}

function check_includes(type,name){
	
	var tag_name = "",tag_attr = "";
	if(type=="css"){
		tag_name="link";
		tag_attr="href";
	}
	if(type=="js"){
		tag_name="script";
		tag_attr="src";
	}
	
	for(var i=0; i<document.getElementsByTagName('head')[0]['children'].length; i++ ){
		var header_item = document.getElementsByTagName('head')[0]['children'][i];
		if(header_item['localName']==tag_name){
			for(var j=0; j < header_item.attributes.length ; j++ ){
				if( header_item.attributes[j]["localName"]==tag_attr && header_item.attributes[j]["nodeValue"].indexOf(name)!=-1){
					return true;
				}
			}
		}
	};
	
	return false;
}

function show_if_significant(pattern,str){
	if(pattern==null || pattern==""){
		return "";
	}else{
		return str.replace(/#P#/g,pattern);
	}
}

function getStrongestZindex(assign){
	if(!window.strongestZindex){
//		strongestZindex=
//			Math.max.apply(null, 
//			    $.map($('body *'), function(e,n) {
//			      if ($(e).css('position') != 'static'){
//			    	  return parseInt($(e).css('z-index')) || 1;
//			      }
//			  }));
		strongestZindex=12000;
	}
	if(assign=="loading"){
		return strongestZindex+10;
	}else{
		strongestZindex = strongestZindex+1;
		return strongestZindex;
	}
} 

function dynamicAppendCss(cssStr){
	var str = cssStr,
	    oStyle = document.createElement('style');
	document.getElementsByTagName('head')[0].appendChild(oStyle);
	if(document.all) { // IE
		oStyle.styleSheet.cssText = str;
	}else{// Firefox, Google Chrome
		oStyle.textContent = str;
	}
}

function tag_it(lat,lng,name){
	if(!window.map){
		console.log("無地圖?")
		return [null,null];
	}
	var marker = new google.maps.Marker({
	    position: {lat:lat,lng:lng},
	    title: name,
	    icon: {
            path: google.maps.SymbolPath.CIRCLE,
            scale: 0
        },
	    map: map
	});
	
	var infowindow = new google.maps.InfoWindow({content:name});
	infowindow.open(map, marker);
	map.setCenter({lat:lat,lng:lng});
	return [marker,infowindow];
}

function pSessionSetup(){
	$.ajax({
		type : "POST",
//		url : "https://l2.io/ip.js?var=userip",//ABER上 寫的
//		url : "http://vl7.net/ip",
		url : "https://helloacm.com/api/what-is-my-ip-address/",
//		async : true,
		success : function(result) {
			$.ajax({
				type : "POST",
				url : "./viewStatus",
//				async : false,
				data : {
					"action" : "setPrivateSession",
					"pSession" : b64EncodeUnicode(result)
				}
			});
		}
	});
}

//function recursiveGetSingleTag(obj,tag){
//	if(Array.isArray(obj)){
//		
//	}else{
//		
//	}
////	var i,
////    currentChild,
////    result;
////
////	if (currentNode.tag!=null) {
////		return currentNode;
////	} else {
////		// Use a for loop instead of forEach to avoid nested functions
////		// Otherwise "return" will not work properly
////		for (i = 0; i < currentNode.children.length; i += 1) {
////			currentChild = currentNode.children[i];
////
////			// Search in the current child
////			result = findNode(id, currentChild);
////
////			// Return the result if the node has been found
////			if (result !== false) {
////				return result;
////			}
////		}
////
////		// The node has not been found and we have no more options
////		return false;
////	}
//}

// sendXMLHttpRequest("http://192.168.112.164/sbi/record.do","POST","action=get_image&png_name=3cc6fe98-11c4-47ab-9cb5-afa5ead2b28e.png")
function sendXMLHttpRequest(url,type,parameter){
	
}
