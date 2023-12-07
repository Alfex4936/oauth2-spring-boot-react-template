import { useEffect, useState } from 'react';
import useStore from "../store";
import { useNavigate, useSearchParams } from 'react-router-dom';
import { CircularProgress } from '@mui/material';
import { Container } from '@mui/material';
import axios from "axios";

export default function Token() {
  const { setAccessToken } = useStore();
  const [loading, setLoading] = useState(true);
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const currentToken = searchParams.get('token');
    console.log("Short-lived Token: " + currentToken);
    if (currentToken) {
      axios.get(`${process.env.REACT_APP_API_ADDRESS}/user/exchange-token?token=${currentToken}`, {
        headers: { "Authorization": "Bearer " + currentToken }
      })
        .then(response => {
          console.log("Long-lived Token: " + response.data);
          // Clean up the URL and navigate to profile after setting the token
          setAccessToken(response.data);
          navigate('/profile', { replace: true });
        })
        .catch(error => {
          console.error('Error fetching profile data:', error);
          // Handle error, e.g., navigate to login or show an error message
          navigate('/login', { replace: true });
        })
        .finally(() => {
          setLoading(false);
        });
    } else {
      navigate('/', { replace: true });
    }
  }, []);

  if (loading) {
    return (
      <Container maxWidth="xs" sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
      </Container>
    );
  }

  return (
    <div>
      Token Page (Never see you)
    </div>
  );
}
