import React from 'react';
import { Link } from 'react-router-dom';
import './Layout.css';

interface LayoutProps {
  children: React.ReactNode;
}

function Layout({ children }: LayoutProps) {
  return (
    <div className="layout">
      <header className="header">
        <nav className="navbar">
          <Link to="/" className="logo">
            gdevxy
          </Link>
          <ul className="nav-links">
            <li>
              <Link to="/">Home</Link>
            </li>
            <li>
              <Link to="/blog">Blog</Link>
            </li>
            <li>
              <Link to="/about">About</Link>
            </li>
          </ul>
        </nav>
      </header>

      <main className="main-content">{children}</main>

      <footer className="footer">
        <p>&copy; 2024 gdevxy. All rights reserved.</p>
        <p>
          Built with <a href="https://quarkus.io">Quarkus</a> and{' '}
          <a href="https://react.dev">React</a>
        </p>
      </footer>
    </div>
  );
}

export default Layout;
