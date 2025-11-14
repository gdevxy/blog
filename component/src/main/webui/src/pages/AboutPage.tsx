import React from 'react';
import { Alert, Spinner, Row, Col } from 'react-bootstrap';
import { useProfile } from '@hooks';
import './AboutPage.css';

function AboutPage() {
  const { profile, loading, error } = useProfile();

  return (
    <div className="about-page">
      {loading && (
        <div className="text-center py-5">
          <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading...</span>
          </Spinner>
          <p className="mt-2">Loading profile...</p>
        </div>
      )}

      {error && <Alert variant="danger">Failed to load profile: {error.message}</Alert>}

      {profile && (
        <>
          <Row className="profile-header mb-5">
            {profile.avatarUrl && (
              <Col md={3} className="text-center mb-4 mb-md-0">
                <img
                  src={profile.avatarUrl}
                  alt={profile.displayName}
                  className="avatar rounded-circle img-fluid"
                />
              </Col>
            )}
            <Col md={profile.avatarUrl ? 9 : 12} className="profile-info">
              <h1 className="mb-3">{profile.displayName}</h1>
              {profile.jobTitle && profile.company && (
                <p className="job-title">
                  <strong>{profile.jobTitle}</strong> at <strong>{profile.company}</strong>
                </p>
              )}
              {profile.location && <p className="location">📍 {profile.location}</p>}
              {profile.email && (
                <p className="email">
                  <a href={`mailto:${profile.email}`}>{profile.email}</a>
                </p>
              )}
            </Col>
          </Row>

          {profile.description && (
            <section className="description mb-5">
              <blockquote className="blockquote">
                <p>{profile.description}</p>
              </blockquote>
            </section>
          )}

          {profile.accounts && profile.accounts.length > 0 && (
            <section className="accounts">
              <h2 className="mb-4">Follow Me</h2>
              <div className="social-links">
                {profile.accounts.map((account, index) => (
                  <a
                    key={index}
                    href={account.url}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="social-link"
                    title={account.type}
                  >
                    {account.icon ? (
                      <img src={account.icon} alt={account.type} className="social-icon" />
                    ) : (
                      <span>{account.type}</span>
                    )}
                  </a>
                ))}
              </div>
            </section>
          )}
        </>
      )}

      {!loading && !profile && (
        <Alert variant="info">Profile information not available</Alert>
      )}
    </div>
  );
}

export default AboutPage;
