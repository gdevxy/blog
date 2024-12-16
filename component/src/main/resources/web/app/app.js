import $ from 'jquery';

async function refreshFragment(url, fragmentId, params) {

	if ($.isEmptyObject(params)) {
		return;
	}

	const queryString = Object.entries(params)
		.map(([key, value]) => {

			if (Array.isArray(value)) {
				return value.map(v => `${key}=${v}`).join('&');
			}

			return `${key}=${value}`;
		})
		.join('&');

	$.ajax({
		type: 'GET',
		url: `${url}?${queryString}`,
		success: function(res) {
			$(`#${fragmentId}`).html(res);
		},
		error: function(err) {
			console.log(err);
		}
	});
}

function checkedInputsAsQueryParams(idPrefix) {

	return $(`input[id^=${idPrefix}]:checked`)
		.map(function () {
			return $(this).attr('value');
		})
		.get();
}

const page = $('html');
const progressBar = $('#progress-bar')

$(document).on("scroll", function() {
	const scroll = page.scrollTop();
	const height = page.prop('scrollHeight') - page.prop('clientHeight');
	const percent = (scroll / height) * 100;
	progressBar.attr('aria-valuenow', percent.toString());
	progressBar.children().css('width', `${percent}%`);
});

window.refreshFragment = refreshFragment;
window.checkedInputsAsQueryParams = checkedInputsAsQueryParams;
