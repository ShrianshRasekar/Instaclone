import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleUser, faComments, faCompass, faHeart, faHome, faRectangleList, faRegistered, faSearch } from '@fortawesome/free-solid-svg-icons';
import '../App.css'; // Import the CSS file for styling
import { Card, CardBody } from 'reactstrap';
import { Link } from 'react-router-dom';
import { faIdBadge } from '@fortawesome/free-solid-svg-icons/faIdBadge';

function Sidebar({ currentProfile }) {
  return (
    <div style={{
      background: "#93C572",  // Pista green color
      width: "250px",  // Fixed width for the sidebar
      padding: "20px",
      color: "white"
    }} className="sidebar">
      <Card>
        <CardBody>
        <h2 style={{ fontFamily: 'cursive', fontWeight: '700' }}>INSTAGRAM</h2>
        <ul style={{ listStyleType: "none", padding: 0 }}>
            <li style={{ display: 'flex', alignItems: 'center' }}>
              <FontAwesomeIcon icon={faHome} />
              <Link to="/middleContent" style={{ marginLeft: '10px', color: 'white' }}>HOME</Link>
            </li>
            <li style={{ display: 'flex', alignItems: 'center' }}>
              <FontAwesomeIcon icon={faSearch} />
              <Link to="/middleContent" style={{ marginLeft: '10px', color: 'white' }}>Search</Link>
            </li>
            <li style={{ display: 'flex', alignItems: 'center' }}>
              <FontAwesomeIcon icon={faCompass} />
              <Link to="/middleContent" style={{ marginLeft: '10px', color: 'white' }}>Explore</Link>
            </li>
            <li style={{ display: 'flex', alignItems: 'center' }}>
              <FontAwesomeIcon icon={faRegistered} />
              <Link to="/middleContent" style={{ marginLeft: '10px', color: 'white' }}>Reels</Link>
            </li>
            <li style={{ display: 'flex', alignItems: 'center' }}>
              <FontAwesomeIcon icon={faComments} />
              <Link to="/message" style={{ marginLeft: '10px', color: 'white' }}>Message</Link>
            </li>
            <li style={{ display: 'flex', alignItems: 'center' }}>
              <FontAwesomeIcon icon={faHeart} />
              <Link to="/notification" style={{ marginLeft: '10px', color: 'white' }}>Notification</Link>
            </li>
            <li style={{ display: 'flex', alignItems: 'center' }}>
              <FontAwesomeIcon icon={faCircleUser} />
              <Link to="/currentProfile" style={{ marginLeft: '10px', color: 'white' }}>{currentProfile}</Link>
            </li>
            <li style={{ display: 'flex', alignItems: 'center' }}>
              <FontAwesomeIcon icon={faIdBadge} />
              <Link to="/addUserProfile" style={{ marginLeft: '10px', color: 'white' }}>Add User Profile</Link>
            </li>
            <li style={{ display: 'flex', alignItems: 'center' }}>
              <FontAwesomeIcon icon={faRectangleList} />
              <Link to="/middleContent" style={{ marginLeft: '10px', color: 'white' }}>More</Link>
            </li>
          </ul>
        </CardBody>
      </Card>
    </div>
  );
};

export default Sidebar;
