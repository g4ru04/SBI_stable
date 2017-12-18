<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="import.jsp" flush="true"/>
<style>
	h2.ui-list-title {
		border-bottom: 1px solid #ccc;
		color: #307CB0;
	}
	section {
		margin-bottom: 60px;
	}
	#tablesInTable td{
		border:0px solid #aaa;
	}
</style>
	
<script>
	function setV(){
		
		if($("#file").val()<1){
			alert("請選擇檔案");
			return false;
		}
		var i=0;
		
		while(!document.getElementsByName("db_name")[i].checked){
			i++;
			if(document.getElementsByName("db_name")[i]==null){
				alert("請選擇資料表");
				return false;
			}
		}
		document.getElementById("form1").action+="?db_name="+document.getElementsByName("db_name")[i].value;
		if(window.scenario_record){scenario_record("上傳資料表-產業分析資料庫","上傳 '"+$("#file").val()+"' 至"+$(document.getElementsByName("db_name")[i]).parent().text());} 
		return true;
	};
	$(function(){
		prepare();
	});
</script>
<%
	String str=(String)request.getAttribute("action");
	if(str!=null){
		if("success".equals(str)||str.indexOf("\"success\": true")!=-1||str.indexOf("\"success\":true")!=-1){
			out.println("<script>alert('傳輸成功');$('body').css('cursor', 'default');window.location.href = './upload.jsp';</script>");
		}else{
			out.println("<script>alert('傳輸失敗 ');$('body').css('cursor', 'default');window.location.href = './upload.jsp';</script>");
		}
	}
%>
<jsp:include page="header.jsp" flush="true"/>
	<div class="content-wrap">
		<h2 class="page-title">產業分析基礎資料庫</h2>
		<div class="search-result-wrap">
			<h2 class="ui-list-title">選擇欲上傳資料表</h2>
			<form action="upload.do" id="form1" method="post" enctype="multipart/form-data">
				<table id='tablesInTable' class="result-table"></table>
				
				<div style="text-align:center; margin:20px auto;">   
					<input type="file" id="file" name="file" size="40" class="ifile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" style="display:none;" onchange="this.form.upload_name.value=this.value;"	/>
					<input type="text" id="upload_name" name="upload_name" size="40" readonly/>
					<input type="button" class="btn btn-exec btn-wide" value="開啟檔案" style="color: #fff" onclick="this.form.file.click();"> 
					<input type="submit" class="btn btn-exec btn-wide" onclick="$('body').css('cursor', 'wait');return setV();" value="檔案匯入" style="color: #fff;margin-left:20px"/>
				</div> 
			</form>
		</div>
	</div>
<script>
function prepare(){
	var uploadDB_json='[{"table":"City","text":"城市資料表"},'
		+'{"table":"CityCivilCar","text":"城市民用汽車擁有量資料表"},'
		+'{"table":"CityConsumptionExpenditure","text":"城市消費支出資料表"},'
		+'{"table":"CityEstateAvgSale","text":"城市房地產平均銷售價格資料表"},'
		+'{"table":"CityEstateCompletion","text":"城市房地產竣工面積資料表"},'
		+'{"table":"CityEstateSaleAmount","text":"城市房地產銷售額資料表"},'
		+'{"table":"CityEstateSaleArea","text":"城市房地產銷售面積資料表"},'
		+'{"table":"CityGender","text":"城市性別人口數資料表"},'
		+'{"table":"CityGoodsTraffic","text":"城市貨運量資料表"},'
		+'{"table":"CityIncome","text":"城市所得資料表"},'
		+'{"table":"CityPassengerTraffic","text":"城市客運量資料表"},'
		+'{"table":"CityPractitioners","text":"城市從業人員數資料表"},'
		+'{"table":"CityRetailExponent","text":"城市商品零售價格指數資料表"},'
		+'{"table":"CitySocialConsume","text":"城市社會消費品零售總額資料表"},'
		+'{"table":"CityStaff","text":"城市國有單位職工人數資料表"},'
		+'{"table":"CityTertiaryIncrease","text":"城市第三產業增加趨勢資料表"},'
		+'{"table":"CityTertiaryIndustry","text":"城市第三產業GDP比重資料表"},'
		+'{"table":"CityWholesaleRetail","text":"城市批發零售社會消費品零售總額資料表"},'
		+'{"table":"consumer_intelligence","text":"消費力情報資料表"},'
		+'{"table":"Country","text":"國家資料表"},'
		+'{"table":"CountryAge","text":"國家年齡人口數資料表"},'
		+'{"table":"CountryLaborForce","text":"國家勞動力結構資料表"},'
		+'{"table":"Gender","text":"國家性別人口數資料表"},'
		+'{"table":"MarketSize","text":"國家產業數據資料表"},'
		+'{"table":"Variables","text":"變數資料表"},'
		+'{"table":"Countrystatistic","text":"動態統計-國家資料表"},'
		+'{"table":"Countrycitystatistic","text":"動態統計-城市資料表"},'
		+'{"table":"POI","text":"POI資料表"},'
		+'{"table":"BD","text":"商圈資料表"}]';
	
	var json_obj = $.parseJSON(uploadDB_json);
	$("#tablesInTable").html('');
	var tr_container= $("<tr/>");
	
	$.each(json_obj,function(i,item){
		if( i%3==0 && i!=0 ){
			$("#tablesInTable").append(tr_container);
			tr_container = $("<tr/>")
		}
		
		var download_example_url="<%=getServletConfig().getServletContext().getInitParameter("updateDBExample") %>";
		tr_container.append(
			$("<td/>").append(
				[$("<input/>",{
					type: "radio",
					name: "db_name",
					id: item.table,
					value: item.table,
				}),
				$("<label/>",{
					"for": item.table,
					html: $("<span/>",{
						"class": "form-label",
						html:[(item.withOutEx=="true"?item.text:$("<a/>",{
							href: (download_example_url+item.table+".xlsx"),
							html: item.text
						}))]
					})
				})]
			)
		)
	});
	
	var counter=json_obj.length;
	while(counter%3!=0){
		console.log(counter);
		counter++;
		tr_container.append($("<td/>"));
	}
	$("#tablesInTable").append(tr_container)
}
</script>
<jsp:include page="footer.jsp" flush="true"/>