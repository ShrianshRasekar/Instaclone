import React, { useState } from 'react';
import axios from 'axios';
import './AuthComponent.css'; // Import the new CSS file


const AuthComponent = ({ setIsAuthenticated }) => {
  const [isSignUp, setIsSignUp] = useState(false);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');

  // Handle login request
  const handleLogin = async () => {
    try {
      const response = await axios.post('http://localhost:5003/user/token', {
        username,
        password,
      });

      if (response.data.token) {
        setIsAuthenticated(true);
        localStorage.setItem('token', response.data.token);
      } else {
        alert('Invalid credentials');
      }
    } catch (error) {
      console.error('Login failed:', error);
      alert('An error occurred while logging in');
    }
  };

  // Handle signup request
  const handleSignup = async () => {
    try {
      const response = await axios.post('http://localhost:5003/user/add', {
        uname: username,
        fullName,
        email,
        password,
      });

      if (response.data) {
        alert('Signup successful! Please login.');
        setIsSignUp(false);
      }
    } catch (error) {
      console.error('Signup failed:', error);
      alert('An error occurred while signing up');
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-box">
      <img src="/instalogo1.png" alt="App Logo" className="auth-logo" />
        <h2>{isSignUp ? 'Signup' : 'Login'}</h2>

        {isSignUp ? (
          <>
            <input
              type="text"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              placeholder="Full Name"
              className="auth-input"
            />
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Email"
              className="auth-input"
            />
          </>
        ) : null}

        <input
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          placeholder="Username"
          className="auth-input"
        />
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Password"
          className="auth-input"
        />
        <button onClick={isSignUp ? handleSignup : handleLogin} className="auth-button">
          {isSignUp ? 'Sign Up' : 'Login'}
        </button>

        <p className="auth-toggle">
          {isSignUp ? 'Already have an account? ' : "Don't have an account? "}
          <span onClick={() => setIsSignUp(!isSignUp)} className="auth-link">
            {isSignUp ? 'Login here' : 'Sign up here'}
          </span>
        </p>
      </div>
    </div>
  );
};

export default AuthComponent;
