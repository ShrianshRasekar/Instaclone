import React, { useState } from 'react';
import { Button, Form, FormGroup, Label, Input, Container, Row, Col, Toast, ToastBody, ToastHeader } from 'reactstrap';

function AddUserProfile() {
  const [formData, setFormData] = useState({
    uname: '',
    fullName: '',
    bio: '',
    posts: 0,
    followers: 0,
    following: 0
  });

  const [responseMessage, setResponseMessage] = useState('');
  const [showToast, setShowToast] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch('http://localhost:5005/userprofile/addProfileInfo', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          uname: formData.uname,
          fullName: formData.fullName,
          bio: formData.bio,
          posts: formData.posts || 0,
          followers: formData.followers || 0,
          following: formData.following || 0
        }),
      });

      if (response.ok) {
        setResponseMessage('User account created successfully!');
      } else {
        setResponseMessage('Failed to create user account.');
      }

      setShowToast(true);
    } catch (error) {
      setResponseMessage('Error occurred: ' + error.message);
      setShowToast(true);
    }
  };

  return (
    <Container className="mt-5" style={{ height: '100%' }}>
      <Row className="justify-content-center">
        <Col md={{ size: 6 }} style={{ display: 'flex', justifyContent: 'center' }}>
          <div style={{
            backgroundColor: '#f0f8ff',  // Light blue background color
            padding: '20px',
            border: '2px solid #007bff',  // Blue border
            borderRadius: '10px',         // Rounded corners
            width: '100%',                 // Full width for the card
          }}>
            <h2 className="text-center mb-4">Create User Profile</h2>

            <Form onSubmit={handleSubmit}>
              <FormGroup>
                <Label for="uname">Username</Label>
                <Input
                  type="text"
                  name="uname"
                  id="uname"
                  placeholder="Enter username"
                  value={formData.uname}
                  onChange={handleChange}
                  required
                />
              </FormGroup>

              <FormGroup>
                <Label for="fullName">Full Name</Label>
                <Input
                  type="text"
                  name="fullName"
                  id="fullName"
                  placeholder="Enter full name"
                  value={formData.fullName}
                  onChange={handleChange}
                  required
                />
              </FormGroup>

              <FormGroup>
                <Label for="bio">Bio</Label>
                <Input
                  type="textarea"
                  name="bio"
                  id="bio"
                  placeholder="Short bio"
                  value={formData.bio}
                  onChange={handleChange}
                  required
                />
              </FormGroup>

              <FormGroup>
                <Label for="posts">Posts</Label>
                <Input
                  type="number"
                  name="posts"
                  id="posts"
                  placeholder="Number of posts"
                  value={formData.posts}
                  onChange={handleChange}
                  min="0"
                />
              </FormGroup>

              <FormGroup>
                <Label for="followers">Followers</Label>
                <Input
                  type="number"
                  name="followers"
                  id="followers"
                  placeholder="Number of followers"
                  value={formData.followers}
                  onChange={handleChange}
                  min="0"
                />
              </FormGroup>

              <FormGroup>
                <Label for="following">Following</Label>
                <Input
                  type="number"
                  name="following"
                  id="following"
                  placeholder="Number of following"
                  value={formData.following}
                  onChange={handleChange}
                  min="0"
                />
              </FormGroup>

              <Button color="primary" block>
                Create User Profile
              </Button>
            </Form>

            {/* Toast Notification */}
            <div style={{ position: 'fixed', bottom: '20px', right: '20px' }}>
              <Toast isOpen={showToast}>
                <ToastHeader icon={responseMessage.includes('successfully') ? 'success' : 'danger'}>
                  {responseMessage.includes('successfully') ? 'Success' : 'Error'}
                </ToastHeader>
                <ToastBody>{responseMessage}</ToastBody>
              </Toast>
            </div>
          </div>
        </Col>
      </Row>
    </Container>
  );
}

export default AddUserProfile;
