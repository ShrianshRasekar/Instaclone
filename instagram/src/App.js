import React, { useState } from 'react';
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate
} from "react-router-dom";
import Sidebar from './components/Sidebar';
import MiddleContent from './components/MiddleContent';
import AddUserProfile from './components/AddUserProfile';
import Notification from './components/Notification';
import ExtraContent from './components/ExtraCotent'; // Ensure correct import name
import CurrentProfile from './components/CurrentProfile';
import Message from './components/Message';
import AuthComponent from './components/AuthComponent';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [fullName, setFullName] = useState('');

  return (
    <Router>
      <div style={{ display: "flex", width: "100vw", height: "100vh" }}>
        
        {/* Show Sidebar only if user is authenticated */}
        {isAuthenticated && (
          <>
            <Sidebar currentProfile={fullName || "Current Profile"} style={{ flex: "1 1 20%", backgroundColor: "#f0f0f0" }} />
            <div style={{ width: '1px', backgroundColor: 'lightgray' }} />
          </>
        )}

        <div style={{ flex: "1 1 50%", display: "flex", flexDirection: "column" }}>
          <Routes>
            {/* Default route shows AuthComponent */}
            <Route path="/" element={!isAuthenticated ? <AuthComponent setIsAuthenticated={setIsAuthenticated} /> : <Navigate to="/middleContent" />} />
            <Route path="/middleContent" element={isAuthenticated ? <MiddleContent /> : <Navigate to="/" />} />
            <Route path="/addUserProfile" element={isAuthenticated ? <AddUserProfile /> : <Navigate to="/" />} />
            <Route path="/currentProfile" element={isAuthenticated ? <CurrentProfile setFullName={setFullName} /> : <Navigate to="/" />} />
            <Route path="/notification" element={isAuthenticated ? <Notification /> : <Navigate to="/" />} />
            <Route path="/message" element={isAuthenticated ? <Message /> : <Navigate to="/" />} />
          </Routes>
        </div>

        {/* Show ExtraContent only if user is authenticated */}
        {isAuthenticated && (
          <>
            <div style={{ width: '1px', backgroundColor: 'lightgray' }} />
            <ExtraContent fullName={fullName} style={{ flex: "1 1 30%", backgroundColor: "#e0e0e0" }} />
          </>
        )}
      </div>
    </Router>
  );
}

export default App;
