import React, { useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faClone } from '@fortawesome/free-regular-svg-icons/faClone';
import { faClipboard } from '@fortawesome/free-regular-svg-icons/faClipboard';
import { faUser } from '@fortawesome/free-regular-svg-icons/faUser';
import { faShareFromSquare } from '@fortawesome/free-regular-svg-icons/faShareFromSquare';
import { faHandshake } from '@fortawesome/free-regular-svg-icons/faHandshake';

function CurrentProfile({ setFullName }) {
  const [username, setUsername] = useState('');
  const [profile, setProfile] = useState(null);
  const [error, setError] = useState('');

  const handleUsernameChange = (e) => {
    setUsername(e.target.value);
  };

  const fetchUserProfile = async () => {
    try {
      const response = await fetch(`http://localhost:5005/userprofile/${username}`);
      if (!response.ok) {
        throw new Error(`Error ${response.status}: Could not fetch profile`);
      }
      const data = await response.json();
      setProfile(data);
      setFullName(data.fullName); // Set fullName in parent state
      setError('');
    } catch (err) {
      setProfile(null);
      setError(err.message);
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>Current Profile</h2>

      <div style={{ marginBottom: '15px' }}>
        <label>
          Enter Username:
          <input
            type="text"
            value={username}
            onChange={handleUsernameChange}
            style={{ marginLeft: '10px', padding: '5px' }}
          />
        </label>
        <button onClick={fetchUserProfile} style={{ marginLeft: '10px' }}>
          Fetch Profile
        </button>
      </div>

      {error && <p style={{ color: 'red' }}>{error}</p>}

      {profile && (
        <div style={{
          backgroundColor: '#f9f9f9',
          padding: '15px',
          border: '1px solid #ddd',
          borderRadius: '8px'
        }}>
          <h3>{profile.fullName}</h3>
          <li style={{ display: 'flex', alignItems: 'center' }}>
          <FontAwesomeIcon icon={faUser} />
          <p><strong> Username:</strong> {profile.uname}</p>
          </li>
          <li style={{ display: 'flex', alignItems: 'center' }}>
          <FontAwesomeIcon icon={faClipboard} />
          <p><strong> Bio:</strong> {profile.bio}</p>
          </li>
          <li style={{ display: 'flex', alignItems: 'center' }}>
          <FontAwesomeIcon icon={faClone} />
          <p><strong> Posts:</strong> {profile.posts}</p>
          </li>
          <li style={{ display: 'flex', alignItems: 'center' }}>
          <FontAwesomeIcon icon={faHandshake} />
          <p><strong> Followers:</strong> {profile.followers}</p>
          </li>
          <li style={{ display: 'flex', alignItems: 'center' }}>
          <FontAwesomeIcon icon={faShareFromSquare} />
          <p><strong> Following:</strong> {profile.following}</p>
          </li>
        </div>
      )}
    </div>
  );
}

export default CurrentProfile;
