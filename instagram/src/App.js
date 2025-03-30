import React, { useState } from 'react';
import logo from './logo.svg';
import './App.css';
import Sidebar from './components/Sidebar';
import MiddleContent from './components/MiddleContent';
import AddUserProfile from './components/AddUserProfile';
import Notification from './components/Notification'; // Ensure you import the Notification component
import {
  BrowserRouter as Router,
  Routes,
  Route
} from "react-router-dom";
import ExtraContent from './components/ExtraCotent'; // Correct the import name
import CurrentProfile from './components/CurrentProfile';
import Message from './components/Message';

function App() {
  const [fullName, setFullName] = useState(''); // State to store the full name

  return (
    <Router>
      <div style={{ display: "flex", width: "100vw", height: "100vh" }}>
        <Sidebar currentProfile={fullName || "Current Profile"} style={{ flex: "1 1 20%", backgroundColor: "#f0f0f0" }} />

        {/* Vertical Divider */}
        <div style={{ width: '1px', backgroundColor: 'lightgray' }} />

        <div style={{ flex: "1 1 50%", display: "flex", flexDirection: "column" }}>
          <Routes>
            <Route path="/middleContent" element={<MiddleContent />} />
            <Route path="/addUserProfile" element={<AddUserProfile />} />
            <Route path="/currentProfile" element={<CurrentProfile setFullName={setFullName} />} />
            <Route path="/notification" element={<Notification />} />
            <Route path="/message" element={<Message />} />
          </Routes>
        </div>

        {/* Vertical Divider */}
        <div style={{ width: '1px', backgroundColor: 'lightgray' }} />

        <ExtraContent fullName={fullName} style={{ flex: "1 1 30%", backgroundColor: "#e0e0e0" }} />
      </div>
    </Router>
  );
}

export default App;
