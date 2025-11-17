import React, { useEffect, useState } from 'react';
import { ProgressBar } from 'react-bootstrap';
import './ScrollProgressBar.css';

function ScrollProgressBar() {
	const [progress, setProgress] = useState(0);

	useEffect(() => {
		const handleScroll = () => {
			const scrollTop = window.scrollY;
			const docHeight = document.documentElement.scrollHeight - window.innerHeight;
			const scrolled = docHeight > 0 ? (scrollTop / docHeight) * 100 : 0;
			setProgress(scrolled);
		};

		window.addEventListener('scroll', handleScroll);
		return () => window.removeEventListener('scroll', handleScroll);
	}, []);

	return (
		<div className="scroll-progress-container">
			<ProgressBar
				now={progress}
				className="scroll-progress-bar"
				style={{ height: '4px' }}
			/>
		</div>
	);
}

export default ScrollProgressBar;
