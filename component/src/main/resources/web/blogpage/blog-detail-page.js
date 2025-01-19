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

function evaluateCommentLength(e, commentId) {

	const currentLength = $(e.target).val().length;
	const remainingChars = 2000 - currentLength;
	$(`#commentCharCounter${commentId}`).text(remainingChars);
}

function registerThumbsUpThumbsDownEvent() {

	$("#btnRate").on('click', thumbsUpThumbsDown);
}

function registerCommentEvents() {

	$('[id^="formComment"]').each(function() {

		const el = $(this);
		const commentId = el.data("comment-id");

		el.on('submit', event => saveComment(event, commentId));
		el.find(`#commentComment${commentId}`).on('input', event => evaluateCommentLength(event, commentId));
	});
}

function registerCommentReplyEvents() {

	$('[id^="btnReply"]').each(function() {

		const el = $(this);
		const commentId = el.data("comment-id");

		el.on('click', event => showReplySection(event, commentId));
	});
}

function showReplySection(e, commentId) {

	const form = $(`#formComment${commentId}`);
	addOrRemoveClass(form, "show");
	addOrRemoveClass(form.parent(), "d-none");
}

function saveComment(e, commentId) {

	e.preventDefault();

	const form = $(e.target);
	const btn = form.find(':submit');
	const action = form.attr("action");
	const author = form.find(`#commentAuthor${commentId}`);
	const comment = form.find(`#commentComment${commentId}`);

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
						author: author.val(),
						comment: comment.val()
					}),
					success: function () {

						const url = action.replace(/\/comment.*$/, '/blog-post-comments-fragment');
						console.log(url);

						return refreshFragment(url, 'blog_post_comments', {})
							.then(() => renderDataDateDistance())
							.then(() => registerCommentEvents())
							.then(() => registerCommentReplyEvents());
					},
					error: function (err) {
						console.log(err);
						btn.addClass("tada");
						$('.has-validation > .is-invalid').each(function() { $(this).removeClass("is-invalid"); });
						err.responseJSON.violations.forEach(violation => {
							switch(violation.field) {
								case "saveComment.action.author":
									author.addClass("is-invalid");
									$('#validationCommentAuthorFeedback').text(violation.message);
									break;
								case "saveComment.action.comment":
									comment.addClass("is-invalid");
									$('#validationCommentCommentFeedback').text(violation.message);
									break;
								default:
									console.log(err)
									break;
							}
						})
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
	checkScrollPosition();
	registerCommentEvents();
	registerCommentReplyEvents();
	registerThumbsUpThumbsDownEvent();
});
