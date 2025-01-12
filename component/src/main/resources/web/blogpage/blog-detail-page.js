import $ from 'jquery';
import '../utils/utils.js';

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

function saveComment(e) {

	e.preventDefault();

	const btn = $(e.target);
	const action = $(this).attr("action");
	const params = {};
	$(this).serializeArray().forEach(field => {
		params[field.name] = field.value;
	});

	grecaptcha.ready(function () {
		grecaptcha
			.execute("6Lc7vagqAAAAAKi_E_E275yxYo_B80-RvOVmVaid", { action: "comment" })
			.then(function (token) {
				$.ajax({
					type: "POST",
					url: action,
					dataType: "json",
					contentType: "application/json",
					data: JSON.stringify({
						captcha: token,
						action: "comment",
						... params
					}),
					success: function () {
						return refreshFragment(action.replace('/comment', '/blog-post-comments-fragment'), 'blog_post_comments', {})
							.then(() => renderDataDateDistance())
							.then(() => $("#formComment").on('submit', saveComment));
					},
					error: function (err) {
						btn.addClass("tada");
					}
				});
			});
	});
}

function thumbsUpThumbsDown(e) {

	const btn = $(e.target);

	if(btn.hasClass("thumbs-up-success")) {

		$.ajax({
			type: "POST",
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
				.execute("6Lc7vagqAAAAAKi_E_E275yxYo_B80-RvOVmVaid", { action: "thumbs_up" })
				.then(function (token) {
					$.ajax({
						type: "POST",
						url: `/blog-posts/${btn.attr("name")}/thumbs-up`,
						dataType: "json",
						contentType: "application/json",
						data: JSON.stringify({
							captcha: token,
							action: "thumbs_up"
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
}

$(window).on('scroll', function() {
	checkScrollPosition();
});

$(document).ready(function() {

	$("#btnRate").on('click', thumbsUpThumbsDown);
	$("#formComment").on('submit', saveComment);

	checkScrollPosition();
});
