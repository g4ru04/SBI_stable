/**
 * 
 */

$(function(){

	setMenu();
	
	function setMenu() {
		$(".sidenav > ul").remove();
		
		var menu = $('#glb_menu').val()==null?"[]":$('#glb_menu').val();
		
		var json_obj = $.parseJSON(menu);
		
		$.each(json_obj, function (i, item) {
			
			var img = $("<img>")
				.attr("src", item.photoPath)
				.attr("alt", "");
			
			if (item.id == 1) {
				var a = $('<a>')
					.attr('href', item.url)
					.attr("target", "_blank")
					.append( img );
				
				img = a;
			}
			
			var ul = $("<ul></ul>");
			var li = $("<li></li>")
				.attr("menu_id", item.id)
				.append( img );
			
			$(".sidenav").append( ul.append( li ) );
			
			//dom
			$(".sidenav > ul:eq(" + i + ") img").after( item.menuName );

			//second level
			var sub_ul = $("<ul></ul>");
			$.each(item.subMenu, function (j, j_item) {
				var a = $('<a>')
					.attr('href', j_item.url)
					.text( j_item.menuName );

				if (j_item.url.match("^http://")) {
					a.attr("target", "_blank");
				}
				
				var li = $("<li></li>")
					.attr("menu_id", j_item.id)
					.append( a );
				
				//third level
				var sub3_ul = $("<ul></ul>");
				$.each(j_item.subMenu, function (k, k_item) {
					var a = $('<a>')
						.attr('href', k_item.url)
						.text( k_item.menuName );
					
					if (k_item.url.match("^http://")) {
						a.attr("target", "_blank");
					}
					
					var li = $("<li></li>")
						.attr("menu_id", k_item.id)
						.append( a );
					
					sub3_ul.append(li);
				});
				
				if (j_item.subMenu.length !== 0) {
					li.append( sub3_ul );
				};
				
				sub_ul.append(li);
			});
			
			$(".sidenav > ul:eq(" + i + ") > li:last").append( sub_ul );
		});
		
		var privilege = $('#glb_privilege').val();
		privilege = privilege == null ? "" : privilege ;
		setPrivilege(privilege);
		adjustMenuCssHeight();
	}

	function setPrivilege(privilege){
		var arrP = [];
		
		arrP = privilege.split(',');
		
		$("li").each(function (i, item) {
			var menuId = $(this).attr('menu_id');
			
			if($.inArray(menuId, arrP) == -1){
			    $( "li[menu_id='" + menuId + "']" ).remove();
			};
		});
		
		$("ul:empty").remove();
	}
	
	function adjustMenuCssHeight(){
		$(".sidenav").children("ul").children("li").children("ul").each(function(){
			var overPx = ((+$(this).offset().top) + ($(this).height()))-(+$(".sidenav").height());
			if(overPx>0){
				$(this).css("top","-"+overPx+"px");
			}else{
				overPx = 0;
			}
			
			$(this).children("li").children("ul").each(function(){
				var overPx2 = ((+$(this).offset().top) + ($(this).height()))-(+$(".sidenav").height());
				if(overPx2-overPx>0){
					$(this).css("top","-"+(overPx2-overPx)+"px");
				}
			});
			
			$(".sidenav > ul > li > ul > li:has( > ul )").addClass("isMenu");
			
		});
	}
});



