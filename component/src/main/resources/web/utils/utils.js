import $ from 'jquery';

function isElementInView(element, fullyInView) {

	const pageTop = $(window).scrollTop();
	const pageBottom = pageTop + $(window).height();
	const elementTop = $(element).offset().top;
	const elementBottom = elementTop + $(element).height();

	if (fullyInView === true) {
		return pageTop < elementTop && pageBottom > elementBottom;
	}

	return elementTop <= pageBottom && elementBottom >= pageTop;
}

function isBottomPage() {

	const scrollPosition = $(window).scrollTop() + $(window).height();
	const documentHeight = $(document).height();

	return scrollPosition >= documentHeight - 5;
}

function addOrRemoveClass(element, className) {

	if (element.hasClass(className)) {
		element.removeClass(className);
	} else {
		element.addClass(className);
	}
}

window.addOrRemoveClass = addOrRemoveClass;
window.isElementInView = isElementInView;
window.isBottomPage = isBottomPage;
