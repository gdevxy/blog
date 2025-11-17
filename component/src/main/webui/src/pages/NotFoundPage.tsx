import React from 'react';
import { Container } from 'react-bootstrap';
import './NotFoundPage.css';

function NotFoundPage() {
  return (
    <Container className="not-found-page">
      <div className="not-found-content">
        <h1 className="error-code">404</h1>
        <h2 className="error-message">Oopsi! Page not found.</h2>

        <img
          src="/images/404.jpeg"
          alt="404 - Page not found"
          className="error-image"
        />
      </div>
    </Container>
  );
}

export default NotFoundPage;
