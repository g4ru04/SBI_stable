<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="import.jsp" flush="true"/>
<!-- <link rel="stylesheet" href="cssPS/styles.css" /> -->
<script src="js/d3.v3.min.js"></script>
<script type="text/javascript" src="js/jquery-migrate-1.4.1.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/additional-methods.min.js"></script>
<script type="text/javascript" src="js/messages_zh_TW.min.js"></script>
<!-- <script type="text/javascript" src="https://raw.githubusercontent.com/CodeYellowBV/html2canvas/master/build/html2canvas.js"></script> -->

<!--  css for d3js -->
<style>
input[type=checkbox] {
    position: static;
}
input[type=radio] {
    position: static;
}
.bentable{
 	margin-left:30px;
	font-size:16px;
}
.bentable tr{
	height:32px;
}
.bentable td{
	padding:8px;
	padding-left:60px;
}
.bentable th{
	word-break: keep-all;
	padding:16px 0px 2px 0px;
	text-align:left;
	font-size:22px;
	font-weight: bold;
	color:#444;
}
.bentable2 {
	margin: 40px auto; 
}
.bentable2 th, .bentable2 td{
	font-size:16px;
	border:1px solid #111;
	padding:8px;
	text-align:center;
}
.bentable2 th{
	background:#3F7FFF;
	color:#F9F9F9;
	font-weight: 900;
	padding:8px 20px;
}

</style>
	
<style>
.text-center{
	text-align: center;
}

.margin-center{
	margin: 0px auto;
}

h2.ui-list-title {
	border-bottom: 1px solid #ccc;
	color: #307CB0;
}
</style>	

<!-- /**************************************  以下管理者JS區塊    *********************************************/		-->
<script>
function radiocheck(name){
	var i,n;
	n=document.getElementsByName(name).length;
	for(i=0;i<n;i++){
		if(document.getElementsByName(name)[i].checked){return (i+1);}
	}
	return false;
}
function checkboxcheck(name){
	var check=0;
	var who="";
	$("input[name='"+name+"']").each(function(n) {
        if($(this).prop("checked")){
        	check++;
        	var tmp=n+1
        	if(who.length==0){
        		who += tmp ;
        	}else{
        		who += "," + tmp;
        	}
        }
    });
	if(check){
		return who;
	}else{
		return false;
	}
}

	$(function(){
		
		$("#view").delegate(".viewAnswer", "click", function(event) {
			event.preventDefault();
			var answer_value = $(this).attr("value").trim();
			var answer_type = answer_value.substring(0,1);
			var parameter = {
				"style":"personaNew",
				"pic_label_1":"目標客群特質",
				"pic_label_2":"商圈性格熱愛矩陣",
				"pic_label_3":"操作策略建議",
				"pic_url_1":"./images/evaluationAnswer/persona/"+answer_value+".png",
				"pic_url_2":"./images/evaluationAnswer/comsumption/"+answer_type+".png",
				"pic_url_3":"./images/evaluationAnswer/operationStrategy/"+answer_type+".png"
			};
			window.open("./answer.jsp?request_str="+encodeURI(b64EncodeUnicode(JSON.stringify(parameter))).replace(/\+/g,"%2B"));
			var host = window.location.href.replace(window.location.href.split("/").pop(),"");
			setTimeout(function(){
				if(window.scenario_record){
					scenario_record("結果查閱","目標客群特質:"+answer_value,"url",
						host+"/images/evaluationAnswer/persona/"+answer_value+".png",function(){});
					scenario_record("結果查閱",answer_type+"型商圈熱愛矩陣","url",
						host+"/images/evaluationAnswer/comsumption/"+answer_type+".png",function(){});
					scenario_record("結果查閱",answer_type+"型策略建議","url",
	 					host+"/images/evaluationAnswer/operationStrategy/"+answer_type+".png",function(){});
				}
			}, 500);
			
		});
		
		$("body").keydown(function (event) {
			if (event.which == 13) {
				$("#send").trigger("click");
			}
		}); 
		
		$("#send").click(function(){
			var wrong=0, i=0;
			var warning_msg="";
			
			if(!radiocheck("px1")) {
				warning_msg+="請選擇品類<br/>";
			}
			if($("#product").val().length==0) {
				warning_msg+="請填寫產品<br/>";
			}
			if(!checkboxcheck("age")) {
				warning_msg+="請選擇性別<br/>";
			}
			if(!checkboxcheck("sex")) {
				warning_msg+="請選擇年齡<br/>";
			}
			if(!radiocheck("px3")) {
				warning_msg+="請選擇產品購物決策思考週期<br/>";
			}
			if((!radiocheck("px4"))||(!radiocheck("px5"))||(!radiocheck("px6"))||(!radiocheck("px7"))) {
				warning_msg+="請完整填寫品牌性格分數<br/>";
			}
			if(!radiocheck("px8")) {
				warning_msg+="請選擇市場價格定位<br/>";
			}
			if(!radiocheck("px9")) {
				warning_msg+="請選擇主力市場分數<br/>";
			}
			
			if(warning_msg.length>3){
				warningMsg('警告', warning_msg);
			} else {
				
				if(window.scenario_record){scenario_record("目標客群定位","["+radiocheck("px1")+",'"+$("#product").val()+"',["+checkboxcheck("sex")+"],["+checkboxcheck("age")+"],"+radiocheck("px3")+","+radiocheck("px4")+","+radiocheck("px5")+","+radiocheck("px6")+","+radiocheck("px7")+","+radiocheck("px8")+","+radiocheck("px9")+"]","selector","#view",function(){/*XXX*/
				
					$.ajax({
						type : "POST",
						url : "persona.do",
						data : {
							action : "persona",
							product_name : $("#product").val(),
							age : checkboxcheck("age"),
							sex : checkboxcheck("sex"),
							px3 : radiocheck("px3"),
							px4 : radiocheck("px4"),
							px5 : radiocheck("px5"),
							px6 : radiocheck("px6"),
							px7 : radiocheck("px7"),
							px8 : radiocheck("px8"),
							px9 : radiocheck("px9")
						},
						success : function(result) {
							var json_obj = null;
							//if(window.scenario_record){scenario_record("目標客群定位","["+checkboxcheck("px1")+","+$("#product").val()+","+checkboxcheck("age")+","+checkboxcheck("sex")+","+radiocheck("px3")+","+radiocheck("px4")+","+radiocheck("px5")+","+radiocheck("px6")+","+radiocheck("px7")+","+radiocheck("px8")+","+radiocheck("px9")+", "+result.replace(/"([^"]*)"/g, "'$1'")+"]");}
							$("#view").fadeOut("slow",function(){
								var result_html="<div style='font-size:20px;'>完成分析！<br><br>";
								var json_obj = $.parseJSON(result);
								
								if(json_obj.Persona.length>0){
									result_html+="　　&nbsp;以下結果是我精心為您分析的「目標客戶特質」及「商圈性格消費熱度矩陣」!"
										+"<div align='center'><table class='bentable2'><tr><th>目標客戶特質</th></tr>";
									$.each(json_obj.Persona,function(i, item) {
<%-- 										result_html+="<tr><td><a href='<%=getServletConfig().getServletContext().getInitParameter("personaimgpath") %>"+item.PersonaCode+".png' target='_blank' onclick='if(window.scenario_record){scenario_record(\"查閱結果\",\""+item.PersonaCode+"\",\"url\",\"<%=getServletConfig().getServletContext().getInitParameter("personaimgpath") %>"+item.PersonaCode+".png\",function(){});}'>"+item.PersonaCode+"</a></td></tr>"; --%>
<%-- 										result_html+="<tr><td><a href='<%=getServletConfig().getServletContext().getInitParameter("personaimgpath") %>"+item.PersonaCode+".png' target='_blank' onclick=''>"+item.PersonaCode+"</a></td></tr>"; --%>
										result_html+="<tr><td><a href='#' class='viewAnswer' value='"+item.PersonaCode+"' target='_blank'>"+item.PersonaCode+"</a></td></tr>";
									});
									
									
// 									var result_2nd_header = '<tr><th colspan="2" rowspan="2">Persona</th><th colspan="3">商圈性格消費喜愛熱度矩陣</th></tr><tr><th>一</th><th>二</th><th>三</th></tr>'
// 									var result_2nd = "<table class='bentable2'><tr>" + result_2nd_header + "</tr>";
// 									$.each(json_obj.Matrix,function(i, item) {
// 										mx1 = item.Martix1.split(',');
// 										mx2 = item.Martix2.split(',');
// 										mx3 = item.Martix3.split(',');
										
// 										var img1='', img2='', img3='';
										
// 										$.each(mx1, function( index, value ) {
// 									  		if (value) {
// 									  			var img = "<img src=images/persona_bd_" + value + ".png alt='" + value + "' height='80px' width='80px'><br/>";
// 										  		img1 += img;
// 											}
// 										});
// 										$.each(mx2, function( index, value ) {
// 									  		if (value) {
// 									  			var img = "<img src=images/persona_bd_" + value + ".png alt='" + value + "' height='80px' width='80px'><br/>";
// 										  		img2 += img;
// 											}
// 										});
// 										$.each(mx3, function( index, value ) {
// 									  		if (value) {
// 									  			var img = "<img src=images/persona_bd_" + value + ".png alt='" + value + "' height='80px' width='80px'><br/>";
// 										  		img3 += img;
// 											}
// 										});
										
// 										var p_name = '';
// 										if (item.Persona == 'A') {
// 											p_name = '精算管家型';
// 										} else if (item.Persona == 'B') {
// 											p_name = '享樂翻糖型';
// 										} else if (item.Persona == 'C') {
// 											p_name = '知性陀飛輪型';
// 										} else if (item.Persona == 'D') {
// 											p_name = '神秘暹羅貓型';
// 										} else if (item.Persona == 'E') {
// 											p_name = '刻苦力爭型';
// 										} else if (item.Persona == 'F') {
// 											p_name = '積極開創者型';
// 										} else if (item.Persona == 'G') {
// 											p_name = '決策苦手型';
// 										} else if (item.Persona == 'H') {
// 											p_name = '生活從眾型';
// 										}  
										
// 										result_2nd += "<tr>" + 
// 										"<td>" + item.Persona + "</td>" + 
// 										"<td>" + p_name + "</td>" + 
// 										"<td>" + img1 + "</td>" + 
// 										"<td>" + img2 + "</td>" + 
// 										"<td>" + img3 + "</td>" + 
// 										"</tr>";
// 									});
// 									result_2nd += "</table>";
									
// 									result_html+="</table>" + result_2nd + "</div>";
									result_html+="</table></div>";
								} else {
									result_html += "很抱歉，經我精心的計算後找不到符合與您客群相符的「目標客戶特質」及「商圈性格消費熱度矩陣」";
								}
								result_html+="</div>"
								$("#view").html(result_html);
								
								var answer = "[";
								$.each(json_obj.Persona,function(i, item) {
									answer += (answer=="["?item.PersonaCode:","+item.PersonaCode);
								});
								answer+= "]";
								setTimeout(function(){
									if(window.scenario_record){
										$.each(json_obj.Persona,function(i, item) {
											scenario_record("結果呈現",item.PersonaCode,"url",
												"<%=getServletConfig().getServletContext().getInitParameter("personaimgpath") %>"
												+item.PersonaCode+".png",function(){});
										});
										scenario_record("目標客群定位結果",answer,"selector","#view");
									}
								}, 500);
								
								
								$("#view").fadeIn("slow");
								
								
							});
						}
					});
				});}
			}
		});
		
		$("#simu").click(function(e){
			e.preventDefault();
			
			var defValue = {
				text: {
					product: '健康養生'
				},
				radio: {
					sex: [1],
					age: [2,3],
					px1: [6],
					px2: [3],
					px3: [3],
					px4: [2],
					px5: [2],
					px6: [3],
					px7: [2],
					px8: [3],
					px9: [3]
				}
			};
			

			$.each(defValue, function(element, object){
				$.each(object, function(key, value){
					if (element == "radio") {
						$.each(value, function(i, attrValue){
							$("#view input[name=" + key + "][value=" + attrValue + "]").attr( "checked", true );
						});
					} else if (element == "text") {
						$("#" + key).val(value);
					}
				});
			});

		});
	});
</script>
<!-- /**************************************  以上使用者JS區塊    *********************************************/	-->

<jsp:include page="header.jsp" flush="true"/>

<div class="content-wrap">
<h2 class="page-title">目標客群定位</h2>
	<div class="search-result-wrap">
		<h1 style="margin-bottom:8px;color:#f44" align="center">客群.&nbsp;您了解多少?</h1>
		<div style="font-size:18px;margin-bottom:20px;color:#888;" align="center">--或許 我可以幫您清晰出客群的輪廓--</div>
<!-- 		<div style='width:790px;margin-left:140px;margin-bottom:8px;'> -->
<!-- 			<h1 style='color:#f44' align='center'>客群.&nbsp;您了解多少?</h1> -->
<!-- 			<div style='font-size:18px;margin-bottom:20px;color:#888;' align='center'>--或許 我可以幫您清晰出客群的輪廓--</div> -->
<!-- 		</div> -->
							  
		<div id='view' style='background:#f8f8f8;padding:20px 60px;border: 3px solid #666;margin:0 auto;width:740px;	border-radius: 8px;box-shadow: 10px 10px 5px #999;'>
			<table class='bentable'>
				<tr><th>一、品類：</th></tr>
				<tr>
					<td>
						<input type="radio"name='px1' value='1'>生活必需品&nbsp;<input type="radio" name='px1' value='2'>生活質感&nbsp;<input type="radio" name='px1' value='3'>飲食&nbsp;<input type="radio" name='px1' value='4'>休閒&nbsp;<input type="radio"name='px1' value='5'>學習&nbsp;<input type="radio" name='px1' value='6'>健康
					</td>
				</tr>
				<tr><th>二、產品：</th></tr>
				<tr>
					<td>
						<input type='text' id='product'>
					</td>
				</tr>
				<tr><th>三、目標族群：</th></tr>
				<tr>
					<td>
						姓別：<input type="checkbox" name='sex' value='1'>男性&nbsp;<input type="checkbox" name='sex' value='2'>女性
					</td>
				</tr>
				<tr>
					<td>
						年齡：<input type="checkbox" name='age' value='1'>青年(15-29歲)&nbsp;<input type="checkbox" name='age' value='2'>中年(30-55歲)&nbsp;<input type="checkbox" name='age' value='3'>老年(56歲以上)
					</td>
				</tr>
				<tr><th>四、產品購物決策思考週期：</th></tr>
				<tr>
					<td style="padding-left: 280px;">
						<input type="radio" name='px3' value='1'>長&nbsp;<input type="radio" name='px3' value='2'>中&nbsp;<input type="radio" name='px3' value='3'>短
					</td>
				</tr>
				<tr><th>五、品牌性格：</th></tr>
				<tr>
					<td>
						<span style='display: inline-block;width:220px'>追隨者→先驅者(1至5分)：</span><input type="radio" name='px4' value='1'>1&nbsp;<input type="radio" name='px4' value='2'>2&nbsp;<input type="radio" name='px4' value='3'>3&nbsp;<input type="radio" name='px4' value='4'>4&nbsp;<input type="radio" name='px4' value='5'>5
					</td>
				</tr>
				<tr>
					<td>
						<span style='display: inline-block;width:220px'>感性→理性(1至5分)： </span><input type="radio" name='px5' value='1'>1&nbsp;<input type="radio" name='px5' value='2'>2&nbsp;<input type="radio" name='px5' value='3'>3&nbsp;<input type="radio" name='px5' value='4'>4&nbsp;<input type="radio" name='px5' value='5'>5
					</td>
				</tr>
				<tr>
					<td>
						<span style='display: inline-block;width:220px'>基本款vs流行款(1至5分)：</span><input type="radio" name='px6' value='1'>1&nbsp;<input type="radio" name='px6' value='2'>2&nbsp;<input type="radio" name='px6' value='3'>3&nbsp;<input type="radio" name='px6' value='4'>4&nbsp;<input type="radio" name='px6' value='5'>5
					</td>
				</tr>
				<tr>
					<td>
						<span style='display: inline-block;width:220px'>實惠→精品(1至5分)： </span><input type="radio" name='px7' value='1'>1&nbsp;<input type="radio" name='px7' value='2'>2&nbsp;<input type="radio" name='px7' value='3'>3&nbsp;<input type="radio" name='px7' value='4'>4&nbsp;<input type="radio" name='px7' value='5'>5
					</td>
				</tr>
				<tr><th>六、市場價格定位：</th></tr>
				<tr>
					<td style="padding-left: 280px;">
						<input type="radio" name='px8' value='1'>低&nbsp;<input type="radio" name='px8' value='2'>中&nbsp;<input type="radio" name='px8' value='3'>高
					</td>
				</tr>
				<tr><th>七、主力市場：</th></tr>
				<tr>
					<td>
						<span style='display: inline-block;width:220px'>線下→線上(1至5分)：</span><input type="radio" name='px9' value='1'>1&nbsp;<input type="radio" name='px9' value='2'>2&nbsp;<input type="radio" name='px9' value='3'>3&nbsp;<input type="radio" name='px9' value='4'>4&nbsp;<input type="radio" name='px9' value='5'>5
					</td>
				</tr>
				<tr>
					<td align='center'>
						　<br>
						<a id='send' class='btn btn-darkblue'>送出分析</a>
						<a id='simu' class='btn btn-darkblue'>模擬資料</a>
					</td>
				</tr>
			</table>
		</div>
	</div>
</div>

<div id="msgAlert"></div>

<jsp:include page="footer.jsp" flush="true"/>
