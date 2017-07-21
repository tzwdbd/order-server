$(function() {

	$(".menu-item").click(function() {
		var that = $(this);
		var url = that.attr("href");
		url = url.replace("#", "");
		if (url) {
			$.get(url, function(data) {
				$("#data").html(data);
			});
		}
		
		$.each($(".menu-item"),function(){
			$(this).removeClass("cur-menu-item");
		});
		
		$(this).addClass("cur-menu-item");
	});

	$(".menu-item").first().click();

	

});
