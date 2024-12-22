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
});
