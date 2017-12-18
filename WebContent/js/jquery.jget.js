/**
 * jget plugin
 * @author Alexandre Magno
 * @desc get a query string to be accessible for javascript
 * @version 1.0
 * @example
 *
 * http://www.foo.com/test.php?var1=test1
 * var outputQuery = $.jget['var1'];
 * $.debug(outputQuery);
 * //Will show 'test1'
 *
 *
 * @license free
 * @param bool vertical, bool horizontal
 * @site http://blog.alexandremagno.net
 *
 */

jQuery.extend({

	//starting the jget object
	jget: {},
	//get the url
	url: window.location.href.replace(/^[^\?]+\??/,''),
	//get the queryString
	parseQuery: function ( query ) {
	   var Params = {};
	   if ( ! query ) {return Params;}// return empty object
	   var Pairs = query.split(/[;&]/);
	   for ( var i = 0; i < Pairs.length; i++ ) {
	      var KeyVal = Pairs[i].split('=');
	      if ( ! KeyVal || KeyVal.length != 2 ) {continue;}
	      var key = unescape( KeyVal[0] );
	      var val = unescape( KeyVal[1] );
	      val = val.replace(/\+/g, ' ');
	      Params[key] = val;
	   }
	   return Params;
	},
	//Make the jget object available to jQuery.extend
	getQueryString: function() {
		this.jget = this.parseQuery(this.url);
	},
	//debugging
	debug: function(message) {

		if(!$.browser.msie) {

			console.info(message);

		} else if($.browser.safari) {

			window.console.log(message);

		} else {

			alert(message);
		}

	}

});

//start the plugin
$.getQueryString();

$(function(){
	
	// 先取得網址上的參數 ?tab=1
	var _q = $.jget['tab'];
	// 若有參數 tab 則顯示指定的頁籤, 否則預設顯示第一個頁籤
	// 並先把 .tabs, .tabs li 及 .tab_content, .tab_content li 等元素取出
	// 同時也要取得 .tab_content 的寬
	var _default = !!!_q ? 0 : parseInt(_q), 
		$block = $('#abgne-block-20120327'), 
		$tabs = $block.find('.tabs'), 
		$tabsLi = $tabs.find('li'), 
		$tab_content = $block.find('.tab_content'), 
		$tab_contentLi = $tab_content.find('li'), 
		_width = $tab_content.width();

	$tabsLi.bind('cssClassChanged', function(){ 
		var title = $(this).find("span").text();
		
		$('.data_source').empty();
		if (title === "商機觀測站") {
			var obj_a = $("<a></a>")
				.attr("href", "http://www.dataa.com.tw/")
				.attr("target", "_blank")
				.text("Dataa大數據研究中心");
			var obj_h6 = $("<h6></h6>")
				.attr("style", "margin:5px;")
				.append("<span>資料來源: <span>")
				.append(obj_a);

			$('.data_source').append(obj_h6);
		}
	});
	
	$tabsLi.trigger("cssClassChanged");
	// 當滑鼠移到 .tabs li 上時要套用 .hover 樣式
	// 移出時要移除 .hover 樣式
	$tabsLi.hover(function(){
		var $this = $(this);

		// 若被滑鼠移上去的 li 是目前顯示的頁籤就不做任何動作
		if($this.hasClass('active')) return;

		$this.toggleClass('hover');
	}).click(function(){	// 當滑鼠點擊 .tabs li 時
		// 先取出被點擊及目前顯示的頁籤與索引
		$('.tab_content').scrollTop(0);
		var $active = $tabsLi.filter('.active').removeClass('active'), 
			_activeIndex = $active.index(),  
			$this = $(this).addClass('active').removeClass('hover'), 
			_index = $this.index(), 
			isNext = _index > _activeIndex;
		
		$this.trigger("cssClassChanged");
		
		// 如果被點擊的頁籤就是目前已顯示的頁籤, 則不做任何動作
		if(_activeIndex == _index) return;
		
		// 依索引大或小來決定移動的位置
		$tab_contentLi.eq(_activeIndex).stop().animate({
			left: isNext ? -_width : _width
		}).end().eq(_index).css('left', isNext ? _width : -_width).stop().animate({
			left: 0
		});
	});
	
	// 先把預設要顯示的頁籤加上 .active 樣式及顯示相對應的內容
	$tabsLi.eq(_default).addClass('active');
	$tab_contentLi.eq(_default).siblings().css({
		left: _width
	});
});

