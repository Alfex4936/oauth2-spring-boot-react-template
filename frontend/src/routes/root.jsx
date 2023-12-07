import React from 'react';
import { Box, Button, Container, Typography } from '@mui/material';
import OAuth2Logo from '../oauth.png';

import "../App.css";

export default function Root() {
  return (
    <Container maxWidth="xs" sx={{ textAlign: 'center', padding: '2rem', display: 'flex', flexDirection: 'column', justifyContent: 'center', height: '100vh' }}>
      <Box sx={{ marginBottom: '2rem' }}>
        <img src={OAuth2Logo} alt="OAuth2 Logo" style={{ width: '150px', height: 'auto', margin: 'auto' }} />
      </Box>
      <Typography variant="h6" gutterBottom>Welcome</Typography>
      <Button variant="contained" color="primary" href={`/login`} sx={{ marginBottom: '1rem' }}>Login</Button>
      <Button variant="outlined" href={`/profile`}>User Profile</Button>
    </Container>
  );
}
