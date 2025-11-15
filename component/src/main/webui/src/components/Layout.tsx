import React from 'react';
import { Link } from 'react-router-dom';
import { Navbar, Nav, Container } from 'react-bootstrap';
import { format } from 'date-fns';
import ThemeToggle from './ThemeToggle';
import './Layout.css';

interface LayoutProps {
  children: React.ReactNode;
}

function Layout({ children }: LayoutProps) {
  return (
    <div className="layout">
      <Navbar sticky="top" className="navbar-custom">
        <Container>
          <Navbar.Brand as={Link} to="/" className="navbar-brand-custom">
            gdevxy
          </Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse id="basic-navbar-nav">
            <Nav className="ms-auto align-items-center gap-3">
              <Nav.Link as={Link} to="/" className="nav-link-custom">
                Home
              </Nav.Link>
              <Nav.Link as={Link} to="/blog" className="nav-link-custom">
                Blog
              </Nav.Link>
              <Nav.Link as={Link} to="/about" className="nav-link-custom">
                About
              </Nav.Link>
              <div className="ms-2">
                <ThemeToggle />
              </div>
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>

      <main className="main-content">
        <Container>{children}</Container>
      </main>

      <footer className="footer">
        <Container>
          <p>&copy; {format(new Date(), 'yyyy')} gdevxy. All rights reserved.</p>
        </Container>
      </footer>
    </div>
  );
}

export default Layout;
