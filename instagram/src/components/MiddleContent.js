import React from 'react';
import '../App.css'; // Import the CSS file for styling
function MiddleContent() {
  return (
    <div
      style={{
        background: "#93C572",   // Pista green color
        width: "100%",           // Full width of the container
        height: "100%",          // Full height of the container
        padding: "20px",
        color: "white",
        display: "flex",
        flexDirection: "column",
        alignItems: "center"
      }}
      className="middle-content"
    >
      {/* Top section */}
      <div style={{ width: "100%", textAlign: "center", marginBottom: "20px" }}>
        <h2>Stories</h2>
        <ul style={{ listStyleType: "none", padding: 0 }}>
          <li><a>HOME FEED</a></li>
        </ul>
      </div>
      
      {/* Additional centered content (if needed) */}
      <div style={{ flexGrow: 1, display: "flex", alignItems: "center", justifyContent: "center" }}>
        {/* Any other centered content goes here */}
      </div>
    </div>
  );
}

export default MiddleContent;
