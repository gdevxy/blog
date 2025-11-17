import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from '@components/Layout';
import HomePage from '@pages/HomePage';
import BlogListPage from '@pages/BlogListPage';
import BlogDetailPage from '@pages/BlogDetailPage';
import AboutPage from '@pages/AboutPage';
import NotFoundPage from '@pages/NotFoundPage';

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/blog" element={<BlogListPage />} />
          <Route path="/blog/:slug" element={<BlogDetailPage />} />
          <Route path="/about" element={<AboutPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </Layout>
    </Router>
  );
}

export default App;
