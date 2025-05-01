import React, { useState } from 'react';
import axios from 'axios';
import './AuthComponent.css';
import { useNavigate } from 'react-router-dom';

const AuthComponent = ({ setIsAuthenticated }) => {
  const [isSignUp, setIsSignUp] = useState(false);
const [email, setEmail] = useState('');
const [password, setPassword] = useState('');
const [username, setUsername] = useState(''); 

  const navigate = useNavigate();

  // Handle login request
  const handleLogin = async () => {
    try {
      const response = await axios.post('http://localhost:5003/auth/token', {
        username,
        password,
      }, {
        headers: {
          'Content-Type': 'application/json',
        },
        responseType: 'text', // <--- IMPORTANT: Expect plain text token
      });

      const token = response.data; // Backend sends raw token string

      if (token) {
        // Validate token
        const validationRes = await axios.get(`http://localhost:5003/auth/validate/${token}`);

        if (validationRes.status === 200 && validationRes.data.message === "Token is valid.") {
          localStorage.setItem('token', token);
          setIsAuthenticated(true);
          navigate("/middleContent"); // redirect after login
        } else {
          alert(validationRes.data.message || 'Invalid or expired token');
        }
      } else {
        alert('Invalid credentials');
      }
    } catch (error) {
      console.error('Login failed:', error.response?.data || error.message);
      alert(error.response?.data?.error || 'An error occurred while logging in');
    }
  };

  // Handle signup request
  const handleSignup = async () => {
    try {
      const response = await axios.post(
        'http://localhost:5003/auth/register',
        {
          name: username,    // <<== should send username
          email: email,
          password: password,
        },
        { headers: { 'Content-Type': 'application/json' } }
      );
  
      if (response.status === 201 && response.data.message === "User added to the system.") {
        alert('Signup successful! Please login.');
        setIsSignUp(false); // Switch to Login page
      } else {
        alert(response.data.message || 'Signup failed. Please try again.');
      }
    } catch (error) {
      console.error('Signup failed:', error.response?.data || error.message);
      alert(error.response?.data?.message || 'An error occurred while signing up');
    }
  };
  

  return (
    <div className="auth-container">
      <div className="auth-box">
        <img src="/instalogo1.png" alt="App Logo" className="auth-logo" />
        <h2>{isSignUp ? 'Sign Up' : 'Login'}</h2>

        {isSignUp ? (
          <>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Username"    // not Full Name
              className="auth-input"
            />
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Email"
              className="auth-input"
            />
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Password"
              className="auth-input"
            />

          </>
        ) : (
          <>
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
          </>
        )}

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
