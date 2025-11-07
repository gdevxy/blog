import React from 'react';
import { useProfile } from '@hooks';
import './AboutPage.css';

function AboutPage() {
  const { profile, loading, error } = useProfile();

  return (
    <div className="about-page">
      {loading && <p className="loading">Loading profile...</p>}
      {error && <p className="error">Failed to load profile: {error.message}</p>}

      {profile && (
        <>
          <div className="profile-header">
            {profile.avatarUrl && (
              <img src={profile.avatarUrl} alt={profile.displayName} className="avatar" />
            )}
            <div className="profile-info">
              <h1>{profile.displayName}</h1>
              {profile.jobTitle && profile.company && (
                <p className="job-title">
                  {profile.jobTitle} at {profile.company}
                </p>
              )}
              {profile.location && <p className="location">📍 {profile.location}</p>}
              {profile.email && (
                <p className="email">
                  <a href={`mailto:${profile.email}`}>{profile.email}</a>
                </p>
              )}
            </div>
          </div>

          {profile.description && (
            <section className="description">
              <p>{profile.description}</p>
            </section>
          )}

          {profile.accounts && profile.accounts.length > 0 && (
            <section className="accounts">
              <h2>Follow Me</h2>
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
        <p className="no-profile">Profile information not available</p>
      )}
    </div>
  );
}

export default AboutPage;
