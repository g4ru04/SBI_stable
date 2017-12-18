<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="import.jsp" flush="true"/>

<link rel="stylesheet" href="css/jquery.dataTables.min.css">
<script src="js/jquery.dataTables.min.js"></script>
<script src="importPKG/oriSBI/js/wicket.js"></script>
<script src="importPKG/oriSBI/js/wicket-gmap3.js"></script>
<style>/* ↓↓↓微調預設格式↓↓↓ */
	
	#search-condition select,#search-condition input[type="text"]  { 
	     width: 98%; 
	} 
	#select_town  select  { 
	     width: 220px; 
	} 
	input[type="radio"] {
		position: static;
	}
	input[type="radio"] + label{
		text-align:center;
	}
	input[type="text"], input[type="password"], input[type="date"], select{
		margin-bottom:0px;
	}
	.content-wrap{ 
	 	margin-bottom: 0px; 
	 	padding-bottom: 30px; 
	     overflow: hidden; 
	     width: 100%; 
	} 
	.search-result-wrap{
		padding: 2px 5px 0px 5px; 
		margin-bottom: 0px;
		height: 100%;
	}
	/* ↑↑↑微調預設格式↑↑↑ */
</style>

<style>/* ↓↓↓左上角框框調整↓↓↓ */
#left-top-box {
	position:fixed;
	top:55px;
	left:120px;
	border:1px solid #dfdfdf;
	background: #fff ;
	border-radius: 2px;
	z-index:5;
}
#left-top-box .btn{ 
	float:right;
	margin-left:10px;
}
#left-top-box .btn.btn-darkblue,#left-top-box .btn.btn-gray{ 
	color: white; 
}
#menu-title-bar{
	font-size:32px;
	height: 46px;
	border-bottom:1px solid #ddd;
	min-width:390px;
}
#getmenu-btn{
	float:right;
	border-left:1px solid #ddd;
	padding:8px 10px 6px 10px;
}
#menu li div{
	padding:8px 12px;
}
/* ↑↑↑左上角框框調整↑↑↑ */
</style>

<style>/* ↓↓↓Ben些許的自訂格式↓↓↓ */
.sbi-logo-line{
	background-image: url(./images/cdri-logo.gif);
	background-repeat: no-repeat;
	background-size:cover;
	position:relative;
	top:6px;
	left:5px;
	width:32px;
	height:32px;
	display: inline-block;
}
.word-75pa{
	font-size:75%; 
}
.word-120pa{
	font-size:120%; 
}
table.with-padding td{
	padding:8px;
}
table.with-full{
	width:98%;
}
table.with-textalign-c{
	text-align:center;
}

.header-line{
	border-top:2px solid #dfdfdf;
}
.footer-line{
	border-bottom:2px solid #dfdfdf;
}

.func:hover{
	box-shadow:0px 0px 0px 0px rgba(80%,80%,80%,0.5),-1px -1px 1px rgba(60%,60%,60%,0.5) inset;
	background-color:#ddd;
}
.func.pressed{
	background-color:#ccc;
}
#loading {
    position: absolute;
    background-color: rgba(128,128,128,0.5);
    width: 100%;
    height: 100%;
    z-index: 9999;
}
#loading img{
    margin: auto;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    position: absolute;
    width:100px;
    border-radius:10px;
    background:#fff;
}
/* ↑↑↑Ben些許的自訂格式↑↑↑ */
</style>
<style>/* ↓↓↓跟css/styles_populationNew.css重複↓↓↓ */
.excel-table th{
	font-weight: 700;
	font-family: Times New Roman, cursive;
	background: #D0CECE;
}
.excel-table tr{
	height: 22px;
}
.excel-table tr:nth-child(odd){
	background: #D9E1F2;
}
.excel-table tr:nth-child(even){
	background: #FFF2CC;
}

.excel-table td{
	padding: 0 20px;
	word-break: keep-all;
}
.excel-table td:nth-child(1){
	text-align:center;
}
.excel-table td:nth-child(3),.excel-table td:nth-child(4){
	text-align: right;
}
.excel-table tr:nth-child(1) td:nth-child(1){
	background: #fff;
	font-size: 20px;
	padding:10px 0px;
}
/* ↑↑↑跟css/styles_populationNew.css重複↑↑↑ */
</style>
<style>/* ↓↓↓跟css/styles_map.css重複↓↓↓ */
#detail_1{ 
	position:absolute;
	right: 50px; 
	top: 120px; 
	filter: alpha(opacity=85,style=0); 
	-moz-opacity: 0.85; 
	opacity: 0.85; 
	z-index: 3; 
}
#detail_1 td:nth-child(2){
	max-width:40vw;
} 
#detail_1 tr{ 
	background: #FFFFFF;
}
#detail_1 caption{ 
	background: rgb(236,236,236);
}
#detail_1 tr:nth-child(2n+2){ 
	
	background: rgb(236,236,236);
}
#detail_1 * { 
	padding: 10px 20px;
	word-break: keep-all;
}
/* ↑↑↑跟css/styles_map.css重複↑↑↑ */
</style>
<style>/* 頁面隨機應變之判斷 */
.ui-widget, .ui-widget *{
	font-family: "微軟正黑體";
}
.ui-accordion-header.ui-state-active{
	background: #ddd !important;
	color: #000 !important; 
}
.ui-state-active .ui-icon{
    background-image: url("css/images/ui-icons_555555_256x240.png");
}
.ui-state-active {
	color:#000 !important;
	background:#eee !important;
}
#query_answer_table{
	overflow-y: scroll;
	max-height: 200px;
	margin-bottom: 20px;
}
#query_answer_table td{
	padding:0px 18px;
}
#query_answer_detail_table td:nth-child(1),#query_answer_detail_table th:nth-child(1){
	width: 20% !important;
}
#dosearch_statistic_answer{
	width: 95% !important;
}	
#dosearch_statistic_answer td{
	word-break:keep-all;
	min-width:80px;
}
#map{
	height: 100%;
}
.excel-table td{
	text-align:center;
}
</style>
<script>
// 給統計查詢用的一維中英轉換
var english_to_chinese_corr = {
	"ROW":"序號",
	"county_id":"城市代碼",
	"county_name":"城市",
	"industry_type":"產業類別",
	"organ":"申請機關",
	"organ_name":"申請機關別",
		
	"info_time":"資料年月",
	"time_month":"年月",
	"entity":"登記機關",
	"mortgage_amount":"擔保債權額",
	"action":"報表種類",
	"currency":"擔保債權金額幣別",
	"type":"案件類別",
	"quantity":"案件筆數",
	"contract_time":"訂立契約日期",
	"terminate_time":"終止日期",
	
	"last_month_quantity":"上月底家數",
	"last_month_capital":"上月底資本額",
	"existing_quantity":"現有公司家數",
	"existing_capital":"現有公司資本額",
	"established_quantity":"新設立家數",
	"established_capital":"新設立資本額",
	"disbanded_quantity":"解散、撤銷及廢止家數",
	"disbanded_capital":"解散、撤銷及廢止資本額",
	"increase_quantity":"增資家數",
	"increase_capital":"增資資本額",
	"decrease_quantity":"減資家數",
	"decrease_capital":"減資資本額",
	"industry_alter_quantity":"行業變動家數",
	"industry_alter_capital":"行業變動資本額",
	"modification_quantity":"異動調整家數",
	"modification_capital":"異動調整資本額",
	"current_month_quantity":"本月底家數",
	"current_month_capital":"本月底資本額",

	"foreign_company_type":"外商公司類型",
	"total":"總和",
	"AgricultureForestryFishingAnimalHusbandry":"農、林、漁、牧業",
	"MiningAndEarthwork":"礦業及土石採取業",
	"Manufacturing":"製造業",
	"ElectricityAndGasSupplyIndustry":"電力及燃氣供應業",
	"WaterSupplyAndPollutionRemediation":"用水供應及污染整治業",
	"ConstructionIndustry":"營造業",
	"WholesaleAndRetailTrade":"批發及零售業",
	"TransportAndWarehousing":"運輸及倉儲業",
	"AccommodationAndCatering":"住宿及餐飲業",
	"InformationAndCommunication":"資訊及通訊傳播業",
	"FinanceAndInsurance":"金融及保險業",
	"ImmovableIndustry":"不動產業",
	"ProfessionalScientificAndTechnicalServices":"專業、科學及技術服務業",
	"SupportServices":"支援服務業",
	"PublicAdministrationAndDefenseCompulsorySocialSecurity":"公共行政及國防；強制性社會安全",
	"EducationServices":"教育服務業",
	"HealthCareAndSocialWorkServices":"醫療保健及社會工作服務業",
	"ArtsEntertainmentAndLeisureServices":"藝術、娛樂及休閒服務業",
	"OtherServices":"其他服務業",
	"uncategorized":"未分類",

	"_0_1mil":"1百萬元以下",
	"_1_5mil":"1百萬元~",
	"_5_10mil":"5百萬元~",
	"_10_20mil":"10百萬元~",
	"_20_30mil":"20百萬元~",
	"_30_40mil":"30百萬元~",
	"_40_50mil":"40百萬元~",
	"_50_60mil":"50百萬元~",
	"_60_100mil":"60百萬元~",
	"_100_150mil":"100百萬元~",
	"_150_200mil":"150百萬元~",
	"_200mil_up":"200百萬元~",
	

	"total_quantity":"總家數",
	"total_capital":"總資本額",
	"AgricultureForestryFishingAnimalHusbandry_quantity":"農、林、漁、牧業 公司家數", "AgricultureForestryFishingAnimalHusbandry_capital":"農、林、漁、牧業 公司資本額",
	"MiningAndEarthwork_quantity":"礦業及土石採取業 公司家數", "MiningAndEarthwork_capital":"礦業及土石採取業 公司資本額",
	"Manufacturing_quantity":"製造業 公司家數", "Manufacturing_capital":"製造業 公司資本額",
	"ElectricityAndGasSupplyIndustry_quantity":"電力及燃氣供應業 公司家數", "ElectricityAndGasSupplyIndustry_capital":"電力及燃氣供應業 公司資本額",
	"WaterSupplyAndPollutionRemediation_quantity":"用水供應及污染整治業 公司家數", "WaterSupplyAndPollutionRemediation_capital":"用水供應及污染整治業 公司資本額",
	"ConstructionIndustry_quantity":"營造業 公司家數", "ConstructionIndustry_capital":"營造業 公司資本額",
	"WholesaleAndRetailTrade_quantity":"批發及零售業 公司家數", "WholesaleAndRetailTrade_capital":"批發及零售業 公司資本額",
	"TransportAndWarehousing_quantity":"運輸及倉儲業 公司家數", "TransportAndWarehousing_capital":"運輸及倉儲業 公司資本額",
	"AccommodationAndCatering_quantity":"住宿及餐飲業 公司家數", "AccommodationAndCatering_capital":"住宿及餐飲業 公司資本額",
	"InformationAndCommunication_quantity":"資訊及通訊傳播業 公司家數", "InformationAndCommunication_capital":"資訊及通訊傳播業 公司資本額",
	"FinanceAndInsurance_quantity":"金融及保險業 公司家數", "FinanceAndInsurance_capital":"金融及保險業 公司資本額",
	"ImmovableIndustry_quantity":"不動產業 公司家數", "ImmovableIndustry_capital":"不動產業 公司資本額",
	"ProfessionalScientificAndTechnicalServices_quantity":"專業、科學及技術服務業 公司家數", "ProfessionalScientificAndTechnicalServices_capital":"專業、科學及技術服務業 公司資本額",
	"SupportServices_quantity":"支援服務業 公司家數", "SupportServices_capital":"支援服務業 公司資本額",
	"PublicAdministrationAndDefenseCompulsorySocialSecurity_quantity":"公共行政及國防；強制性社會安全 公司家數", "PublicAdministrationAndDefenseCompulsorySocialSecurity_capital":"公共行政及國防；強制性社會安全 公司資本額",
	"EducationServices_quantity":"教育服務業 公司家數", "EducationServices_capital":"教育服務業 公司資本額",
	"HealthCareAndSocialWorkServices_quantity":"醫療保健及社會工作服務業 公司家數", "HealthCareAndSocialWorkServices_capital":"醫療保健及社會工作服務業 公司資本額",
	"ArtsEntertainmentAndLeisureServices_quantity":"藝術、娛樂及休閒服務業 公司家數", "ArtsEntertainmentAndLeisureServices_capital":"藝術、娛樂及休閒服務業 公司資本額",
	"OtherServices_quantity":"其他服務業 公司家數", "OtherServices_capital":"其他服務業 公司資本額",
	"uncategorized_quantity":"未分類 公司家數", "uncategorized_capital":"未分類 公司資本額",

	"UnlimitedCompany_capital":"無限公司",
	"UnlimitedCompany_quantity":"無限公司資本額",
	"LimitedPartnership_capital":"兩合公司",
	"LimitedPartnership_quantity":"兩合公司資本額",
	"LimitedCompany_amount":"有限公司",
	"LimitedCompany_quantity":"有限公司資本額",
	"CompanyLimitedByShares_amount":"股份有限公司",
	"CompanyLimitedByShares_quantity":"股份有限公司資本額",
	"ForeignCompanyWithRecognition_capital":"外國公司在臺認許公司",
	"ForeignCompanyWithRecognition_quantity":"外國公司在臺認許公司在臺營運資金",
	"MainlandRegionWithRecognition_amount":"大陸地區在臺許可公司",
	"MainlandRegionWithRecognition_quantity":"大陸地區在臺許可公司在臺營運資金",
	"ForeignCompanyRepresentativeOffices_amount":"外國公司代表人辦事處",
	"MainlandRegionRepresentativeOffices_quantity":"大陸地區在臺許可辦事處",

	"UnlimitedCompany_quantity":"無限公司家數",
	"UnlimitedCompany_capital":"無限公司資本額",
	"LimitedPartnership_quantity":"兩合公司家數",
	"LimitedPartnership_capital":"兩合公司資本額",
	"LimitedCompany_quantity":"有限公司家數",
	"LimitedCompany_capital":"有限公司資本額",
	"CompanyLimitedByShares_quantity":"股份有限公司家數",
	"CompanyLimitedByShares_capital":"股份有限公司資本額",
	"ForeignCompanyWithRecognition_quantity":"外國公司在臺認許公司家數",
	"ForeignCompanyWithRecognition_capital":"外國公司在臺認許公司資本額",
	"MainlandRegionWithRecognition_quantity":"大陸地區在臺許可公司家數",
	"MainlandRegionWithRecognition_capital":"大陸地區在臺許可公司資本額",
	"ForeignCompanyRepresentativeOffices_quantity":"外國公司代表人辦事處數量",
	"MainlandRegionRepresentativeOffices_quantity ":"大陸地區在臺許可辦事處數量",
	
	"_0_1mil_quantity":"一百萬元以下家數",
	"_0_1mil_capital":"一百萬元以下資本額",
	"_1_5mil_quantity":"一百萬元以上未滿五百萬元家數",
	"_1_5mil_capital":"一百萬元以上未滿五百萬元資本額",
	"_5_10mil_quantity":"五百萬元以上未滿一千萬元家數",
	"_5_10mil_capital":"五百萬元以上未滿一千萬元資本額",
	"_10_20mil_quantity":"一千萬元以上未滿二千萬元家數",
	"_10_20mil_capital":"一千萬元以上未滿二千萬元資本額",
	"_20_30mil_quantity":"二千萬元以上未滿三千萬元家數",
	"_20_30mil_capital":"二千萬元以上未滿三千萬元資本額",
	"_30_40mil_quantity":"三千萬元以上未滿四千萬元家數",
	"_30_40mil_capital":"三千萬元以上未滿四千萬元資本額",
	"_40_50mil_quantity":"四千萬元以上未滿五千萬元家數",
	"_40_50mil_capital":"四千萬元以上未滿五千萬元資本額",
	"_50_100mil_quantity":"五千萬元以上未滿一億元家數",
	"_50_100mil_capital":"五千萬元以上未滿一億元資本額",
	"_100_500mil_quantity":"一億元以上未滿五億元家數",
	"_100_500mil_capital":"一億元以上未滿五億元資本額",
	"_500mil_up_quantity":"五億元以上家數",
	"_500mil_up_capital":"五億元以上資本額",
	
	"total_quantity":"合計家數",
	"M_quantity":"男性負責人_家數",
	"M_quantity_percent":"男性負責人_家數占比",
	"F_quantity":"女性負責人_家數",
	"F_quantity_percent":"女性負責人_家數占比",
	"total_capital":"合計金額(元)",
	"M_capital":"男性負責人_資本額(元)",
	"M_capital_percent":"男性負責人_資本額占比",
	"F_capital":"女性負責人_資本額(元)",
	"F_capital_percent":"女性負責人_資本額占比",
	
	"memo":"備註"
}
//縣市排順序的手動北到南
var city_sort_array = {"基隆市":"1","臺北市":"2","新北市":"3","桃園市":"4","新竹市":"5",
						"新竹縣":"6","苗栗縣":"7","臺中市":"8","彰化縣":"9","南投縣":"10",
						"雲林縣":"11","臺南市":"12","嘉義縣":"13","嘉義市":"14","高雄市":"15",
						"屏東縣":"16","宜蘭縣":"17","花蓮縣":"18","臺東縣":"19","澎湖縣":"20",
						"金門縣":"21","連江縣":"22"}

	$( function() {
		prepare_page();
		prepare_trigger();
		prepare_jquery_ui();
	});
</script>
<jsp:include page="header.jsp" flush="true"/>
	<div class="content-wrap">
		<h2 class="page-title">企業業態資料查詢</h2>
		<div class="search-result-wrap">
			<div id="map"></div>
<!-- 			左上角獨立出來的的小框框分title和右邊按MENU出來的btn -->
			<div id='left-top-box' style='display:none;'>
				<div id='menu-title-bar'>
					<div class="sbi-logo-line"></div>
					<span id='title' class='word-75pa'>企業業態資料</span>
					<span class='word-75pa'><span class='word-75pa'>查詢系統</span></span>
					<span id='getmenu-btn' class='fa fa-reorder func'></span>
				</div>
			
				<ul id="menu" style='display:none;'>
				  <li><div onclick='change_search("type","select_company_by_type","公司名錄查詢-依類別");'>公司名錄查詢-依類別</div></li>
				  <li><div onclick='change_search("dataset","select_company_by_dataset","公司名錄查詢-資料集");'>公司名錄查詢-資料集</div></li>
				  <li><div onclick='change_search("statistic","select_company_by_statistic","公司統計資料查詢");'>公司統計資料查詢</div></li>
				</ul>
				<div id="search-ui" style='display:none;'>
					<div id="search-title" style='padding: 8px 12px;text-align:center;'></div>
<!-- 					JQUERY UI 中的 ACCORDION 分三區 1.查詢 2.縣市數量統計 3.詳細查詢結果 -->
				    <div id="accordion">
					  <h3>設定查詢條件</h3>
					  	<div>
					  		<table id='search-condition' class='with-padding'>
<!-- 					  			每個tr分一區 包著[三種條件查詢的select] [keyword] 和[查詢的btn] -->
								<tr class="type">
									<td>
										行業類別
									</td>
									<td>
										<select name='company_type'><option>不拘</option></select>
									</td>
								</tr>
								<tr class="dataset">
									<td>
										資料集　
									</td>
									<td>
										<select name='dataset_name'>
											<option value=''>請選擇</option>
											<option value='三大科學園區資料集'>三大科學園區資料集</option>
											<option value='全國前100大公司資料集'>全國前100大公司資料集</option>
											<option value='全國前500大公司資料集'>全國前500大公司資料集</option>
											<option value='全國四大超商資料集'>全國四大超商資料集</option>
										</select>
									</td>
								</tr>
								<tr class="statistic">
									<td>
										統計項目<br>
										<select name='statistic_type'></select>
									</td>
								</tr>
								<tr class="header-line type dataset">
									<td>
										關鍵字
									</td>
									<td>
										<input type='text' name='keyword' placeholder='公司統編,名稱,地址,代表人...'>
									</td>
								</tr>
								<tr class="header-line" name='btns'>
									<td colspan='2'>
										<a class='btn btn-darkblue' id='do-search' >查詢</a>
										<a class='btn btn-gray' id='clear-search'>清除</a>
									</td>
								</tr>
							</table>
						</div>
					  <h3 class="query_answer" style="display:none;">查詢結果</h3>
					  	<div class="query_answer" style="display:none;max-height: calc(100vh - 256px);">
					  		<div id="query_answer_title"></div>
					  		<table id='query_answer_table'class='with-full excel-table'>
					  		</table>
						</div>
						<h3 class="query_answer_detail" style="display:none;">查詢結果細項</h3>
					  	<div class="query_answer_detail" style="display:none;max-height: calc(100vh - 300px);min-width: 530px;">
					  		<div id="query_answer_detail_title"></div>
					  		<table id='query_answer_detail_table' class='with-padding with-full with-textalign-c' ></table>
					  	</div>
					</div>
				</div>
			</div>
		</div>
	</div>
<!-- 	選縣市後的選鄉鎮DIALOG -->
	<div id="select_town" title="選擇 鄉,鎮,市,區" style='padding:20px 40px;'>
		<div id='city_name'></div>
		<select>
			<option value="">請選擇</option>
		</select>
	</div>
<!-- 	讀取中的遮罩 -->
	<div id="loading" style="display: none;">
    	<img src="./images/loading.gif">
	</div>

<!-- 以下為操作的script 分三區 1.caller 2.callee 3.common.js -->
	<script>
// 	頁面開始時的三個prepare + googlemap自call的initMap
// 	prepare- 1/3 
//	包含tabs,accordion,menu和一個dialog
	function prepare_jquery_ui(){
		$( "#tabs" ).tabs();
		$( "#accordion" ).accordion({
		    active: 0,
		    heightStyle: "content"
		});
		$( "#menu" ).menu();
		$("#select_town").dialog({
			draggable : true,
			resizable : false,
			autoOpen : false,
			height : "auto", 
			width : "auto", 
			modal : false,
			show : {
				effect : "blind",
				duration : 300
			},
			hide : {
				effect : "fade",
				duration : 300
			},
			buttons : [{
				id : "select_town_btn",
				text : "查詢",
				click : function() {
					if($("#select_town select").val()==''&&$("#select_town select").attr("neglect")=="false"){
						warningMsg('提醒','請選擇鄉鎮市區');
						return;
					}
					while(polygen_array.length>0){
						polygen_array.pop().setMap(null);
					}
					
					var parameter = {};
					parameter["action"] = $("#search-title").attr("action")+"_detail";
					parameter["city"] = $("#select_town div:first-child").attr("city");
					parameter["town"] = $("#select_town select").val()==null?"":$("#select_town select").val();
					$("#query_answer_detail_title").html(
							"<div style='margin:auto;'>以下為 "+parameter["city"]+" "+parameter["town"]+" 之結果。</div><br>"
					);
					
					$("#search-condition").find("select,input").each(function(i) {
						parameter[$(this).attr("name")]=$(this).val();
					});
					console.log(parameter);
					$("#query_answer_detail_table").html(
						$("<thead/>").append(
							$("<tr/>",{"class":"footer-line"}).append([
								$("<th/>",{"html":"定位"}),
								$("<th/>",{"html":"公司名稱"})
							])
						)
					);
					$("#loading").show();
					
					$("#query_answer_detail_table").DataTable({
						dom : "lfr<t>ip",
						destroy : true,
						"pagingType": "simple",
						language : {"url" : "js/dataTables_zh-tw.txt"},
						ajax : {
							type : "POST",
							url : "companyRegister.do",
							data : parameter,
							dataSrc : function ( json_obj, status ) {
								if(json_obj.length==0){
									return [];
								}
								var list = [];
								var avg_lat=0;
								var avg_lng=0;
								var avg_count=0;
								$.each(json_obj,function(i, item) {
									if(item.lat!=""){
										avg_lat+=+item.lat;
										avg_lng+=+item.lng;
										avg_count++;
									}
									var choosed_company_info="";
									$.ajax({
				 						type : "POST",
				 						url : "companyRegister.do",
// 				 						async : false,
				 						data : {
				 							action : "select_the_choosed_company",
				 							id : item.id
				 						},
				 						success : function(result) {
				 							
				 							var json_obj = $.parseJSON(result);
				 							green_marker = json_obj[0].status=="核准設立";
				 							if(json_obj.length>0){
				 								choosed_company_info += "<table class='with-padding'style='margin:8px;'>";
				 								choosed_company_info += "<tr><th colspan='2' class='word-120pa' ><b>公司資料</b></th></tr>";
				 								choosed_company_info += json_obj[0].EIN!=""?"<tr><td>統編:</td><td>"+json_obj[0].EIN+"</td></tr>":"";
				 								choosed_company_info += json_obj[0].company_name!=""?"<tr><td>公司名稱:</td><td>"+json_obj[0].company_name+"</td></tr>":"";
				 								choosed_company_info += json_obj[0].type!=""?"<tr><td>公司類型:</td><td>"+json_obj[0].type+"</td></tr>":"";
				 								choosed_company_info += json_obj[0].source!=""?"<tr><td>類別:</td><td>"+(json_obj[0].source.split("-").length>1?json_obj[0].source.split("-")[0]:"")+"</td></tr>":"";
				 								choosed_company_info += json_obj[0].address!=""?"<tr><td>公司地址:</td><td>"+json_obj[0].address+"</td></tr>":"";
				 								choosed_company_info += json_obj[0].representative!=""?"<tr><td>公司代表人:</td><td>"+json_obj[0].representative+"</td></tr>":"";
				 								choosed_company_info += json_obj[0].capital_amount!=""?"<tr><td>公司資本額:</td><td>"+json_obj[0].capital_amount+"</td></tr>":"";					 								
				 								choosed_company_info += json_obj[0].status!=""?("<tr><td>公司狀態:</td><td>"+(json_obj[0].status=="核准設立"?json_obj[0].status:"<b style='font-size:1.5em;color:red;'>"+json_obj[0].status+"</b>")+"</td></tr>"):"";
				 								choosed_company_info += "<tr><td>設立日期:</td><td>"+(json_obj[0].creation_d!=""?json_obj[0].creation_d:"---")+"</td></tr>";
				 								choosed_company_info += "<tr><td>變更日期:</td><td>"+(json_obj[0].modification_d!=""?json_obj[0].modification_d:"---")+"</td></tr>";
				 								choosed_company_info += "<tr><td>解散日期:</td><td>"+(json_obj[0].disbanded_d!=""?json_obj[0].disbanded_d:"---")+"</td></tr>";
				 								choosed_company_info += "</table>";
// 				 								if(json_obj[0].status!="核准設立"){
// 				 									company_marker.setIcon("http://maps.google.com/mapfiles/ms/icons/green-dot.png");
// 				 								}
				 							}
				 						}
									});

									list.push({
										locate:(item.lat!=""?"<img src='./images/map-marker.png' class='func' style='height:30px;width:30px;' onclick='map.setZoom(19);map.setCenter({lat: "+item.lat+",lng: "+item.lng+"});google.maps.event.trigger(company_marker_list["+item.id+"], \"click\",null);'>":"無"),
										company_name:("<div "+(/*item.lat!=""*/false?"":"onmouseover=\"detail_1.innerHTML =$(this).attr('table');\" onmouseout=\"detail_1.innerHTML ='';\" table=\""+choosed_company_info+"\"")+">"+item.company_name+(item.status!="核准設立"?"(<b style='font-size:1.2em;color:red;'>"+item.status+"</b>)":"")+"</div>")
									})
									
									var company_marker = new google.maps.Marker({
									    position: {lat: +item.lat , lng: +item.lng },
									    map: map,
									    title: (item.company_name),
									    icon: (item.status=="核准設立"?null:"http://maps.google.com/mapfiles/ms/icons/green-dot.png")
									});
// 									if(item.status!="核准設立"){
// 	 									company_marker.setIcon("http://maps.google.com/mapfiles/ms/icons/green-dot.png");
// 	 								}
									if(!window.company_marker_list){
										company_marker_list={};
									}
									if(!window.company_marker_infowindow_list){
										company_marker_infowindow_list={};
									}
									
									company_marker_list[item.id] = company_marker;
									
									google.maps.event.addListener(company_marker, "click", function(event) {
										if(company_marker_infowindow_list[item.id] !=null){
											company_marker_infowindow_list[item.id].open(map, company_marker);
										}else{
											var infowindow = new google.maps.InfoWindow({content:choosed_company_info});
											company_marker_infowindow_list[item.id] = infowindow;
											infowindow.open(map, company_marker);
										}
									});
									
								});
								if(avg_lat!=0){
									map.setCenter({lat:(avg_lat / avg_count),lng:(avg_lng / avg_count)-0.2});
									map.setZoom(11);
								}
								return list;
							}
						},columns : [ 
							{"data": "locate", "defaultContent": ""},
							{"data": "company_name", "defaultContent": ""}
						],"fnInitComplete": function( oSettings ) {
						      $("#loading").hide();
					    }
					});
					
					$(".query_answer_detail").show();
					$("#query_answer_table").parent().css("max-height","calc(100vh - 290px)");
					$( '#accordion' ).accordion( 'option', 'active', 2 );
					$(this).dialog("close"); 
				}
			},{
				text : "取消",
				click : function() {
					$(this).dialog("close");
				}
			}]
		});
	}
// 	prepare- 2/3 
//	包含eventtrigger們
	function prepare_trigger(){
		$("input[name='keyword']").keypress(function(e){
			code = (e.keyCode ? e.keyCode : e.which);
			if (code == 13){
			    $("#do-search").trigger("click");
			}
		});
		$("#getmenu-btn").click(function() {
			$("#search-ui").hide();
			$("#menu").toggle();
			if($("#getmenu-btn").hasClass( "pressed" )){
				$("#getmenu-btn").removeClass("pressed");
			}else{
				$("#getmenu-btn").addClass("pressed");
			}
		});
		$("#do-search").click(function() {
			//地圖資料準備要四秒左右 沒準備好不進
			if(!window.shp_data){
				return;
			}
			//設定搜尋title
			var have_query=0;
			var action_chinese=""
			$("#query_answer_title").html("");
			$(this).closest("table").find("tr").each(function(i) {
				if($(this).css("display")!="none" && $(this).attr("name")!="btns"){
					$("#query_answer_title").append(
						jQuery.trim($(this).find("td:first-child").text())
						+":　"
						+jQuery.trim($(this).find("select,input").val())
						+"<br>");
					if(jQuery.trim($(this).find("select,input").val())!=""){
						have_query++;
						if($(this).find("select,input").prop("tagName")=="INPUT"){
							action_chinese=jQuery.trim($(this).find("input").text());
						}else{
							action_chinese=jQuery.trim($(this).find("select option:selected").text());
						}
					}
					
				}
			});
			if(!have_query){
				warningMsg('提醒','<div class="word-120pa" style="text-align:center"><br>請設定查詢條件<br></div><br>');
				return ;
			}
			$("#query_answer_title").append("<br>");
			
			$("#loading").show();
			//放參數
			var parameter = {};
			parameter["action_chinese"] = action_chinese;
			parameter["action"] = $("#search-title").attr("action");
			$("#search-condition").find("select,input").each(function(i) {
				parameter[$(this).attr("name")]=$(this).val();
			});
			
			if(parameter["action"]=="select_company_by_statistic"){
				dosearch_statistic(parameter);
			}else{
				dosearch_typeANDdataset(parameter);
			}
		});
		$("#clear-search").click(function(){
			$("#search-condition").find("select").each(function(i) {
				$(this).val($(this).find("option").val());
			});
			$("#search-condition").find("input").each(function(i) {
				$(this).val('');
			});
			if(window.polygen_array){
				while(polygen_array.length>0){
					polygen_array.pop().setMap(null);
				}
			}
			if(window.company_marker_list){
				console(company_marker_list);
				$.each(company_marker_list,function(i) {
					if(company_marker_list[i]!=null){
						company_marker_list[i].setMap(null);
					}
				});
				company_marker_list={};
			}
			if(window.company_marker_infowindow_list){
				$.each(company_marker_infowindow_list,function(i) {
					if(company_marker_infowindow_list[i]!=null){
						company_marker_infowindow_list[i].close();
					}
				});
				company_marker_infowindow_list={};
			}
		});
		$("#query_answer_table").delegate( ".search-town", "click" , function(){
			ask_town($(this).html(),$(this).attr("count_n"));
		});
	}
// 	prepare- 3/3 
//	單純準備shp和select
	function prepare_page(){
		$.ajax({
			type : "POST",
			url : "companyRegister.do",
			data : {
				"action" : "page_load_shp"
			},
			success : function(result) {
				shp_data={};
				var json_obj = $.parseJSON(result);
				$.each(json_obj,function(i, item) {
					var shp_item = {};
					var shp_name="";
					$.each(item,function(key, value) {
						if(key=="district_name"){
							shp_name=value;
						}
						shp_item[key]=value;
					});
					shp_data[shp_name]=shp_item;
				});
			}
		});
		
		genSelect("company_type",[
			{"key":"一般廣告服務業","value":"一般廣告服務業"},
			{"key":"三温暖浴室服務業","value":"三温暖浴室服務業"},
			{"key":"不動產代銷經紀業","value":"不動產代銷經紀業"},
			{"key":"不動產仲介經紀業","value":"不動產仲介經紀業"},
			{"key":"中藥零售業","value":"中藥零售業"},
			{"key":"人力派遣","value":"人力派遣"},
			{"key":"仲介服務業","value":"仲介服務業"},
			{"key":"便利商店","value":"便利商店"},
			{"key":"保全業","value":"保全業"},
			{"key":"倉儲業","value":"倉儲業"},
			{"key":"公寓大廈管理服務","value":"公寓大廈管理服務"},
			{"key":"公益彩券服務","value":"公益彩券服務"},
			{"key":"公證業","value":"公證業"},
			{"key":"公路汽車客運業","value":"公路汽車客運業"},
			{"key":"其他工商服務業","value":"其他工商服務業"},
			{"key":"其他服務","value":"其他服務"},
			{"key":"其他綜合零售","value":"其他綜合零售"},
			{"key":"其他餐飲業","value":"其他餐飲業"},
			{"key":"剪報業","value":"剪報業"},
			{"key":"加氣站業","value":"加氣站業"},
			{"key":"加油站業","value":"加油站業"},
			{"key":"厨具.衛浴設備安裝工程業","value":"厨具.衛浴設備安裝工程業"},
			{"key":"喜慶綜合服務業","value":"喜慶綜合服務業"},
			{"key":"室內裝修","value":"室內裝修"},
			{"key":"室內裝潢","value":"室內裝潢"},
			{"key":"室內輕鋼架工程業","value":"室內輕鋼架工程業"},
			{"key":"小客車租賃業","value":"小客車租賃業"},
			{"key":"就業服務業","value":"就業服務業"},
			{"key":"市區汽車客運業","value":"市區汽車客運業"},
			{"key":"市場研究及民意調查業","value":"市場研究及民意調查業"},
			{"key":"廣告傳單分送業","value":"廣告傳單分送業"},
			{"key":"影印業","value":"影印業"},
			{"key":"應該帳款管理服務業","value":"應該帳款管理服務業"},
			{"key":"打字業","value":"打字業"},
			{"key":"排版業","value":"排版業"},
			{"key":"攝影業","value":"攝影業"},
			{"key":"汽車貨運業","value":"汽車貨運業"},
			{"key":"漁船加油站業","value":"漁船加油站業"},
			{"key":"無店面零售業","value":"無店面零售業"},
			{"key":"特定寵物批發業","value":"特定寵物批發業"},
			{"key":"特定寵物服務業","value":"特定寵物服務業"},
			{"key":"特定寵物零售業","value":"特定寵物零售業"},
			{"key":"玻璃安裝工程","value":"玻璃安裝工程"},
			{"key":"理貨包裝業","value":"理貨包裝業"},
			{"key":"環保服務業","value":"環保服務業"},
			{"key":"瘦身美容業","value":"瘦身美容業"},
			{"key":"短期補習班業","value":"短期補習班業"},
			{"key":"移民服務業","value":"移民服務業"},
			{"key":"第三方支付服務業","value":"第三方支付服務業"},
			{"key":"管理系統驗證業","value":"管理系統驗證業"},
			{"key":"網路認證服務業","value":"網路認證服務業"},
			{"key":"美容美髮業","value":"美容美髮業"},
			{"key":"翻譯業","value":"翻譯業"},
			{"key":"西藥零售業","value":"西藥零售業"},
			{"key":"計程車客運業","value":"計程車客運業"},
			{"key":"資料處理服務業","value":"資料處理服務業"},
			{"key":"資訊軟體服務","value":"資訊軟體服務"},
			{"key":"遊覽車客運業","value":"遊覽車客運業"},
			{"key":"遊說業","value":"遊說業"},
			{"key":"門穸安裝工程業","value":"門穸安裝工程業"},
			{"key":"電子資訊供應服務業","value":"電子資訊供應服務業"},
			{"key":"飲料店業","value":"飲料店業"},
			{"key":"飲酒行業","value":"飲酒行業"},
			{"key":"餐具清洗業","value":"餐具清洗業"},
			{"key":"餐廰餐館業","value":"餐廰餐館業"}
		]);
		
		genSelect("statistic_type",[
			{"key":"company_chattels_mortgage_transaction","value":"動產擔保交易登記統計"},
			{"key":"company_chattels_mortgage_transaction_stat","value":"動產担保交易公示變更清冊"},
			{"key":"company_register_stat_change","value":"公司登記家數及實收資本額異動"},
			{"key":"company_register_stat_change_industrytype","value":"公司登記家數及實收資本額異動-按行業別分"},
			{"key":"company_register_stat_change_organ","value":"公司登記家數及實收資本額異動-按申請機關別分"},
			{"key":"company_register_stat_foreign_industry","value":"外國公司認許與代表人辦事處現有家數-按行業別分"},
			{"key":"company_register_stat_foreign_workingcapital","value":"外國公司認許與代表人辦事處現有家數-按營運資金分"},
			{"key":"company_register_stat_industry_disbanded","value":"公司解散,撤銷及廢止登記家數及資本額-按行業別分"},
			{"key":"company_register_stat_industry_established","value":"新設立公司登記家數及資本額-按行業別分"},
			{"key":"company_register_stat_industry_existing","value":"現有公司登記家數及資本額-按行業別分"},
			{"key":"company_register_stat_industry_organ","value":"公司登記現有家數及實收資本額-按行業別及申登機關別分"},
			{"key":"company_register_stat_organization","value":"現有公司登記家數-按組織別分"},
			{"key":"company_register_stat_organization_industrytype","value":"現有公司登記家數及實收資本額-按組織別及行業別分"},
			{"key":"company_register_stat_paidup_industry","value":"公司登記現有家數及實收資本額-按行業別分"},
			{"key":"company_register_stat_sex","value":"公司登記現有家數及資本額-按負責人性別及縣市別分"},
			{"key":"company_register_stat_status","value":"公司登記家數及資本額-按縣市分"}
		]);
	}
	
	function initMap() {
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
		google.maps.event.addListener(map, 'idle', function(event) {
			$("#left-top-box").show();
		});
	}
	</script>
	<script>
// 	給大家叫的function
// 	左上角menu中右邊部分的btn(嚴格來說比較像trigger)
	function change_search(type,action,title){
		$("#search-title").html(
			"<b class='word-120pa'>"+title+"　</b>"
			+"<a class='func word-75pa' style='float:right;padding:6px;' onclick='$(\"#search-ui\").hide();'>X</a>"
		).attr("action",action);
		$( '#accordion' ).accordion( 'option', 'active', 0 );
		$("#menu").hide();
		$("#getmenu-btn").removeClass("pressed");
		$('.query_answer').hide();
		
		$('.query_answer_detail').hide();
		$("#query_answer_table").parent().css("max-height","calc(100vh - 256px)");
		$("#search-ui").show();
		
		$(".type").hide();
		$(".dataset").hide();
		$(".statistic").hide();
		$("."+type).show();
		if(window.polygen_array){
			while(polygen_array.length>0){
				polygen_array.pop().setMap(null);
			}
		}
		if(window.company_marker_list){
			$.each(company_marker_list,function(i) {
				if(company_marker_list[i]!=null){
					company_marker_list[i].setMap(null);
				}
			});
		}
		if(window.company_marker_infowindow_list){
			$.each(company_marker_infowindow_list,function(i) {
				if(company_marker_infowindow_list[i]!=null){
					company_marker_infowindow_list[i].close();
				}
			});
		}
	}
	function change_search(type,action,title){
		$("#search-title").html(
			"<b class='word-120pa'>"+title+"　</b>"
			+"<a class='func word-75pa' style='float:right;padding:6px;' onclick='$(\"#search-ui\").hide();'>X</a>"
		).attr("action",action);
		$( '#accordion' ).accordion( 'option', 'active', 0 );
		$("#menu").hide();
		$("#getmenu-btn").removeClass("pressed");
		$('.query_answer').hide();
		
		$('.query_answer_detail').hide();
		$("#query_answer_table").parent().css("max-height","calc(100vh - 256px)");
		$("#search-ui").show();
		
		$(".type").hide();
		$(".dataset").hide();
		$(".statistic").hide();
		$("."+type).show();
		if(window.polygen_array){
			while(polygen_array.length>0){
				polygen_array.pop().setMap(null);
			}
		}
		if(window.company_marker_list){
			$.each(company_marker_list,function(i) {
				if(company_marker_list[i]!=null){
					company_marker_list[i].setMap(null);
				}
			});
		}
		if(window.company_marker_infowindow_list){
			$.each(company_marker_infowindow_list,function(i) {
				if(company_marker_infowindow_list[i]!=null){
					company_marker_infowindow_list[i].close();
				}
			});
		}
	}

	
	function ask_town(city,n){
		$("#select_town div:first-child").html(city+"之<br><br>");
		$("#select_town div:first-child").attr("city",city);
		if(n<30){
			$("#select_town select").attr('neglect','true');
			$("#select_town select").val('');
			$("#select_town_btn").trigger("click");
			return;
		}
		$("#select_town select").attr('neglect','false');
		$("#select_town div:first-child").html(city+"之<br><br>");
		$("#select_town div:first-child").attr("city",city);
		$("#loading").show();
		$.ajax({
			type : "POST",
			url : "companyRegister.do",
			async : false,
			data : {
				"action" : "select_town",
				"city" : city,
				"company_type" : ($("#search-condition select[name='company_type']").closest("tr").css("display")!="none"?$("#search-condition select[name='company_type']").val():""),
				"dataset_name" : ($("#search-condition select[name='dataset_name']").closest("tr").css("display")!="none"?$("#search-condition select[name='dataset_name']").val():""),
				"keyword" : ($("#search-condition input[name='keyword']").closest("tr").css("display")!="none"?$("#search-condition input[name='keyword']").val():"")
			},
			success : function(result) {
				var json_obj = $.parseJSON(result);
				$("#select_town select").html($("<option/>").html("請選擇").val(''));
				$.each(json_obj,function(i, item) {
					$("#select_town select").append(
						$("<option/>").html(item.district_name.replace(city,"")).val(item.district_name.replace(city,""))
					);
				});
				
				$('#select_town').dialog('open');
				$("#loading").hide();
			}
		});
	}
	
	function dosearch_statistic(parameter){
		var dosearch_dialog = $("<div/>").html([
				$("<div/>",{"class":"word-120pa"}).html($("<div/>",{"class":"word-120pa"}).html($("<div/>",{"class":"word-120pa","style":"text-align:center;","html":"<br>"+parameter["action_chinese"]}))),
				$("<table/>",{"id":"dosearch_statistic_answer"}).append("<thead/>")
			]).dialog({
			title : "公司統計資料查詢",
			draggable : true,
			resizable : false,
			autoOpen : false,
			height : "auto", 
			width : "auto", 
			modal : false,
			create : function(){
				$(this).css("max-width", "calc(80vw)");
			},
			open : function(){
				$(this).css("max-height", "calc(85vh)");
			},
			show : {
				effect : "blind",
				duration : 300
			},
			hide : {
				effect : "fade",
				duration : 300
			},
			close: function( event, ui ) {
				$(this).remove();
			}
		});
		
		$.ajax({
			type : "POST",
			url : "companyRegister.do",
			async : false,
			data : parameter,
			success : function(result) {
				var json_obj = $.parseJSON(result);
				$.each(json_obj,function(i,item) {
					json_obj[i]=item["map"];
				});
				
				var col_array=[];
				var head_array="";
				if(json_obj.length>1){
					var copy_json = JSON.parse(JSON.stringify(json_obj[0]));
					console.log(copy_json);
					$.each(copy_json,function(key,value) {
						if(key!="id"){
							col_array.push({
								"data" : key,
								"defaultContent": ""
							});
						}
					});
					var tmp_count=0;
					var sort_seq={};
					$.each(english_to_chinese_corr,function(key,value) {
						sort_seq[key]=tmp_count++;
					});
					col_array.sort(function(a, b){
						return sort_seq[a.data]-sort_seq[b.data];
					});
					$.each(col_array,function(i,item) {
						head_array+="<th>"+word_correspond(item.data)+"</th>";
					});
				}
				
				$("#dosearch_statistic_answer").find("thead").html("<tr>"+head_array+"</tr>");
				$("#dosearch_statistic_answer").DataTable({
					"aaData":json_obj,
					dom : "lfr<t>ip",
					destroy : true,
					language : {"url" : "js/dataTables_zh-tw.txt"},
					columns : col_array ,
					"fnInitComplete": function( oSettings ) {
						  $("#loading").hide();
						  dosearch_dialog.dialog("open");
					}
				});
			}
		});
	}
	
	function dosearch_typeANDdataset(parameter){
		$.ajax({
			type : "POST",
			url : "companyRegister.do",
			async : false,
			data : parameter,
			success : function(result) {
				map.setZoom(7);
				map.setCenter({lat: 23.598321171324468, lng: 120.97802734375});
				var json_obj = $.parseJSON(result);
				json_obj.sort(function(a, b){
					return city_sort_array[a.city_name]-city_sort_array[b.city_name];
				});
				$("#query_answer_table").html(
					$("<tr/>").attr("class","footer-line").append([
						$("<th/>").html("序號"),
						$("<th/>").html("縣市"),
						$("<th/>").html("公司家數")
					])
				);
				
				$.each(json_obj,function(i, item) {
					$("#query_answer_table").append(
						$("<tr/>").append([
							$("<td/>").html(i+1),
							$("<td/>").append(
								$("<a/>").attr({
									"href":"#",
									"class": "search-town",
									"count_n" : item.count_n
								}).html(item.city_name)
							),
							$("<td/>").html(item.count_n)
						])
					);
					
					var msg = "<table><caption>查詢結果</caption>"
		            + "<tr><td align='center'>城市:</td><td align='center' width='120px'>"+item.city_name+"</td></tr>"
		            + "<tr><td align='center'>公司家數:</td><td align='center'>" + item.count_n + " 家</td></tr></table>";
		            
		            if(!window.detail_1){
		            	$("body").append($("<div/>",{"id":"detail_1"}));
		            }
		            if(!window.polygen_array){
						polygen_array=[];
					}
		            
					var wkt = new Wkt.Wkt();
					wkt.read(shp_data[item.city_name].geom);
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
		                    	polygen_array.push(polygen[i]);
		                    	google.maps.event.addListener(polygen[i], "click", function(event) { 
					            	ask_town(item.city_name,item.count_n);
						        });
		                    	google.maps.event.addListener(polygen[i], "mouseover", function(e) { 
				            		detail_1.innerHTML = msg ;
				            		$.each(polygen,function(j, item) {
					            		item.setOptions({fillColor:'#0f0'}); 
					            	});
				            	});
				            	google.maps.event.addListener(polygen[i], "mouseout", function(e) { 
				            		detail_1.innerHTML = "" ;
				            		$.each(polygen,function(j, item) {
					            		item.setOptions({fillColor:'#7092BE'}); 
					            	});
				            	});
		                    }
		                }
		            } else {
		            	polygen.setMap(map);
		            	polygen_array.push(polygen);
		            	google.maps.event.addListener(polygen, "click", function(event) { 
			            	ask_town(item.city_name,item.count_n);
				        });
		            	google.maps.event.addListener(polygen, "mouseover", function(e) { 
		            		detail_1.innerHTML = msg ;
		            		polygen.setOptions({fillColor:'#0f0'}); 
		            	});
		            	google.maps.event.addListener(polygen, "mouseout", function(e) { 
		            		detail_1.innerHTML = "" ;
		            		polygen.setOptions({fillColor:'#7092BE'});
		            	});
		            }
				});
				$("#loading").hide();
			}
		});
		$('.query_answer').show();
		$( '#accordion' ).accordion( 'option', 'active', 1 );
	}
	
	
	function word_correspond(str){
		if(english_to_chinese_corr[str]==null)console.log(str);
		return english_to_chinese_corr[str]==null?str:english_to_chinese_corr[str];
	}
	</script>
	<script>
// 	可能共用的function
	function warningMsg(title, msg) {
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
				}
			}]
		});
	}
	function genSelect(element, data) {
		/* 以array of javascript object 產生select選單 */
	     var target = $("select[name='" + element + "']");

	     target.find('option').remove();

	     target.append(
	         $("<option>", {
	              value: "",
	              html: "請選擇"
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
	</script>
<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyC8QEQE4TX2i6gpGIrGbTsrGrRPF23xvX4&signed_in=true&libraries=places&callback=initMap"></script>
<jsp:include page="footer.jsp" flush="true"/>