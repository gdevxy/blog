import $ from 'jquery';
import '../utils/utils.js';

$(document).ready(function() {

	const headings = $("p.h1, p.h2, p.h3");
	const indexLinks = $("#index a");
	let activeIndex = undefined;

	function checkScrollPosition() {

		indexLinks.removeClass('active-index');

		if (isBottomPage()) {
			activeIndex = indexLinks.length - 1;
		} else {
			headings.each(function(index)  {
				if (isElementInView(this)) {
					activeIndex = index;
					return false;
				}
			});
		}

		if (activeIndex !== undefined) {
			$(indexLinks[activeIndex]).addClass('active-index');
		}
	}

	$(window).on('scroll', function() {
		checkScrollPosition();
	});

	checkScrollPosition();

	$("#btnRate").on('click', function (e) {

		const btn = $(e.target);

		if(btn.hasClass("thumbs-up-success")) {

			$.ajax({
				type: 'POST',
				url: `/blog-posts/${btn.attr("name")}/thumbs-down`,
				success: function () {
					btn.removeClass("thumbs-up-success").removeClass("pulse");
				},
				error: function (err) {
					btn.addClass("tada");
				}
			});

		} else {

			grecaptcha.ready(function () {
				grecaptcha
					.execute("6Lc7vagqAAAAAKi_E_E275yxYo_B80-RvOVmVaid", { action: "submit" })
					.then(function (token) {
						$.ajax({
							type: 'POST',
							url: `/blog-posts/${btn.attr("name")}/thumbs-up`,
							dataType: 'json',
							contentType: "application/json",
							data: JSON.stringify({
								captcha: token
							}),
							success: function () {
								btn.addClass("thumbs-up-success").addClass("pulse");
							},
							error: function (err) {
								btn.addClass("tada");
							}
						});
					});
			});
		}
	});

});
