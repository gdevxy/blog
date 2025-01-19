import $ from 'jquery';
import {format, formatDistanceToNow} from 'date-fns';

async function refreshFragment(url, fragmentId, params) {

	console.log("########### kikoo");
	console.log(url);

	const queryString = Object.entries(params)
		.map(([key, value]) => {

			if (Array.isArray(value)) {
				return value.map(v => `${key}=${v}`).join('&');
			}

			return `${key}=${value}`;
		})
		.join('&');

	return $.ajax({
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

function renderDataDate() {
	$('[data-date]').each(function () {
		const isoDate = $(this).data('date');
		if (isoDate) {
			$(this).text(format(Date.parse(isoDate), "yyyy-MM-dd 'at' h:mma"));
		}
	});
}

function renderDataDateDistance() {
	$('[data-date-distance]').each(function () {
		const isoDate = $(this).data('date-distance');
		if (isoDate) {
			$(this).text(`${formatDistanceToNow(Date.parse(isoDate))} ago`);
		}
	});
}

window.checkedInputsAsQueryParams = checkedInputsAsQueryParams;
window.refreshFragment = refreshFragment;
window.renderDataDate = renderDataDate;
window.renderDataDateDistance = renderDataDateDistance;

$(document).ready(renderDataDate);
$(document).ready(renderDataDateDistance);
