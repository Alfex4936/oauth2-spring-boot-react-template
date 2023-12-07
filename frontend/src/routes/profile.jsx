import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Avatar, CircularProgress } from '@mui/material';
import { Container, Typography, Box, Button } from '@mui/material';
import useStore from "../store";
import { useNavigate } from 'react-router-dom';
import "../App.css";

export default function Profile() {
  const { accessToken, profileData, setProfileData, clearAuth } = useStore();
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const logout = () => {
    clearAuth(); // clear auth first
    axios.post(`${process.env.REACT_APP_API_ADDRESS}/logout`, {
      headers: { "Authorization": "Bearer " + accessToken }
    }).then(_response => {
      console.log("Logout successful");
      navigate('/');
    }).catch(error => {
      console.error('Error during logout:', error);
    });
  }

  useEffect(() => {
    if (loading && accessToken) {
      axios.get(`${process.env.REACT_APP_API_ADDRESS}/user/profile`, {
        headers: { "Authorization": "Bearer " + accessToken }
      }).then(response => {
        setProfileData(response.data);

      }).catch(error => {
        console.error('Error fetching profile data:', error);
      }).finally(() => {
        setLoading(false);
      });
    } else {
      setLoading(false);
    }
  }, [loading, accessToken, profileData, setProfileData, setLoading]);

  if (loading) {
    return (
      <Container maxWidth="xs" sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
      </Container>
    );
  }

  if (!profileData) {
    return (
      <Container maxWidth="sm" sx={{ textAlign: 'center', padding: '2rem' }}>
        <Typography variant="h5" gutterBottom>Profile not found. Please log in.</Typography>
        <Button variant="contained" href="/" sx={{ marginTop: '1rem' }}>To home</Button>
      </Container>
    );
  }

  return (
    <Container maxWidth="sm" sx={{ textAlign: 'center', padding: '2rem' }}>
      <Box sx={{ marginBottom: '2rem' }}>
        {profileData.picture && (
          <Avatar alt="Avatar" src={profileData.picture} sx={{ width: 150, height: 150, margin: 'auto' }} />
        )}
      </Box>
      <Typography variant="h6">{profileData.name}</Typography>
      <Typography variant="body1" color="textSecondary">({profileData.provider} | {profileData.email})</Typography>
      <Typography variant="body2" sx={{ margin: '1rem 0' }}>Account Created: {new Date(profileData.createdAt).toLocaleDateString()}</Typography>
      <Button variant="outlined" href="/" sx={{ marginTop: '1rem' }}>To home</Button>
      <Button onClick={logout} variant="contained" color="primary" href="/" sx={{ marginTop: '1rem' }}>Logout</Button>
    </Container>
  );
}
