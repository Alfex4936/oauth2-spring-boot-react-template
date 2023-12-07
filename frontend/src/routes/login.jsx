import React, { useEffect, useState, useRef } from 'react';
import { Box, Button, TextField, FormControlLabel, Checkbox, IconButton, Link, Paper, Grid, Typography, Container } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import GoogleLogo from '../assets/google.svg';
import GithubLogo from '../assets/github.svg';
import KakaoLogo from '../assets/kakao.svg';
import NaverLogo from '../assets/naver.svg';
import { useNavigate } from 'react-router-dom';
import useStore from "../store";

import axios from "axios";

export default function Login() {
    const { rememberedEmail, setRememberedEmail, setRememberMe, setAccessToken, setProfileData } = useStore();
    const [remember, setRemember] = useState(false);

    const navigate = useNavigate();

    // State to store email and password
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const passwordInputRef = useRef(null);

    useEffect(() => {
        if (rememberedEmail) {
            setEmail(rememberedEmail);
            setRemember(true);
            passwordInputRef.current?.focus();
        }
    }, [rememberedEmail]);

    const handleRememberChange = (event) => {
        setRemember(event.target.checked);
    };


    const handleLogin = async (event) => {
        event.preventDefault(); // Prevent default form submission

        // Update Zustand store
        setRememberMe(remember);

        const formData = new FormData();
        formData.append('email', email);
        formData.append('password', password);

        try {
            const response = await axios.post(`${process.env.REACT_APP_API_ADDRESS}/auth/login`, formData, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            });
            setAccessToken(response.data.accessToken);
            setProfileData(response.data.user);
            setRememberedEmail(response.data.user.email);
            console.log('Login successful:', response.data.accessToken);
            // Handle success (e.g., navigate to another page or set user context)
            navigate("/profile")
        } catch (error) {
            console.error('Error during login:', error);
            navigate("/")
            // Handle error (e.g., show error message)
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <Paper elevation={6} sx={{ marginTop: 8, display: 'flex', flexDirection: 'column', alignItems: 'center', padding: 2 }}>
                <IconButton onClick={() => navigate('/')} sx={{ position: 'absolute', top: 16, left: 16 }}>
                    <ArrowBackIcon />
                </IconButton>
                <Typography component="h1" variant="h5" sx={{ mt: 1, mb: 2 }}>
                    Sign in
                </Typography>
                <Box component="form" noValidate sx={{ mt: 1 }} onSubmit={handleLogin}>
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="email"
                        label="Email Address"
                        name="email"
                        autoComplete="email"
                        autoFocus
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        name="password"
                        label="Password"
                        type="password"
                        id="password"
                        autoComplete="current-password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        inputRef={passwordInputRef}
                    />
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={remember}
                                onChange={handleRememberChange}
                                color="primary"
                            />
                        }
                        label="Remember me"
                    />
                    <Button
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                        onClick={handleLogin}
                    >
                        Sign In
                    </Button>
                    <Grid container justifyContent="space-between">
                        <Grid item>
                            <Link href="#" variant="body2">
                                Forgot password?
                            </Link>
                        </Grid>

                        <Grid item>
                            <Link
                                component="button"
                                onClick={() => navigate("/signup")}
                                variant="body2"
                                style={{ cursor: 'pointer', textDecoration: 'underline' }}
                            >
                                Sign Up
                            </Link>
                        </Grid>
                    </Grid>

                </Box>
                <Typography variant="body2" align="center" sx={{ my: 2 }}>
                    — OR —
                </Typography>
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <Button
                            fullWidth
                            variant="outlined"
                            startIcon={<img src={GoogleLogo} alt="Google" style={{ height: '24px' }} />}
                            href={`${process.env.REACT_APP_API_ADDRESS}/oauth2/authorization/google`}
                        >
                            Continue with Google
                        </Button>
                    </Grid>
                    <Grid item xs={12}>
                        <Button
                            fullWidth
                            variant="outlined"
                            startIcon={<img src={GithubLogo} alt="GitHub" style={{ height: '24px' }} />}
                            href={`${process.env.REACT_APP_API_ADDRESS}/oauth2/authorization/github`}
                        >
                            Continue with GitHub
                        </Button>
                    </Grid>
                    <Grid item xs={12}>
                        <Button
                            fullWidth
                            variant="outlined"
                            startIcon={<img src={NaverLogo} alt="Naver" style={{ height: '24px' }} />}
                            href={`${process.env.REACT_APP_API_ADDRESS}/oauth2/authorization/naver`}
                        >
                            Continue with Naver
                        </Button>
                    </Grid>
                    <Grid item xs={12}>
                        <Button
                            fullWidth
                            variant="outlined"
                            startIcon={<img src={KakaoLogo} alt="Kakao" style={{ height: '24px' }} />}
                            href={`${process.env.REACT_APP_API_ADDRESS}/oauth2/authorization/kakao`}
                        >
                            Continue with Kakao
                        </Button>
                    </Grid>
                </Grid>
            </Paper>
        </Container>
    );
}
