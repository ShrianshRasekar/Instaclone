import React from 'react';

function ExtraContent({ fullName }) {
  return (
    <div style={{
      background: "#93C572",  // Pista green color
      width: "250px",  // Fixed width for the sidebar
      padding: "20px",
      color: "white"
    }} className="sidebar">
      <h2>{fullName || 'Current Profile'}</h2> {/* Display fullName or default text */}
      <ul style={{ listStyleType: "none", padding: 0 }}>
        <li><a>Suggestions</a></li>
      </ul>
    </div>
  );
};

export default ExtraContent;
