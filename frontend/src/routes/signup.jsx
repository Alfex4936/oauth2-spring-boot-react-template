import React, { useState } from 'react';
import { Box, Button, TextField, IconButton, Link, Paper, Grid, Typography, Container } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate } from 'react-router-dom';

import Snackbar from '@mui/material/Snackbar';
import MuiAlert from '@mui/material/Alert';
import axios from "axios";

const Alert = React.forwardRef(function Alert(props, ref) {
    return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

export default function SignUp() {
    // State to store email, password, and password confirmation
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [passwordConfirm, setPasswordConfirm] = useState('');

    const [open, setOpen] = React.useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState('');
    const navigate = useNavigate();

    const handleClick = (message) => {
        setSnackbarMessage(message);
        setOpen(true);
    };

    const handleClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }

        setOpen(false);
    };

    const handleSignUp = async (event) => {
        event.preventDefault();

        // empty fields
        if (email === "" || password === "" || passwordConfirm === "") {
            handleClick("Fulfill the information well.");
            return;
        }

        // Email validation
        const emailRegex = /\S+@\S+\.\S+/;
        if (!emailRegex.test(email)) {
            handleClick("Invalid email address");
            return;
        }



        // Password match validation
        if (password !== passwordConfirm) {
            handleClick("Passwords do not match");
            return;
        }


        // Continue with the signup process
        const signUpData = {
            "email": email,
            "password": password
        }

        try {
            const response = await axios.post(`${process.env.REACT_APP_API_ADDRESS}/auth/signup`, signUpData,
                { headers: { 'Content-Type': 'application/json' } }
            );

            // Handle response here
            console.log('Signup successful:', response.data);

            navigate("/login");
        } catch (error) {
            console.error('Signup error:', error);
            handleClick("Signup failed: " + error.message);
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <Snackbar open={open} autoHideDuration={6000} onClose={handleClose}>
                <Alert onClose={handleClose} severity="error" sx={{ width: '100%' }}>
                    {snackbarMessage}
                </Alert>
            </Snackbar>

            <Paper elevation={6} sx={{ marginTop: 8, display: 'flex', flexDirection: 'column', alignItems: 'center', padding: 2 }}>
                <IconButton onClick={() => navigate('/')} sx={{ position: 'absolute', top: 16, left: 16 }}>
                    <ArrowBackIcon />
                </IconButton>
                <Typography component="h1" variant="h5" sx={{ mt: 1, mb: 2 }}>
                    Homepage Sign Up
                </Typography>
                <Box component="form" noValidate sx={{ mt: 1 }} onSubmit={handleSignUp}>
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
                        autoComplete="new-password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        name="passwordConfirm"
                        label="Confirm Password"
                        type="password"
                        id="password-confirm"
                        autoComplete="new-password"
                        value={passwordConfirm}
                        onChange={(e) => setPasswordConfirm(e.target.value)}
                    />
                    <Button
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                        type="submit"
                    >
                        Sign Up
                    </Button>
                    <Grid container justifyContent="space-between">
                        <Grid item>
                            <Link
                                component="button"
                                onClick={() => navigate("/login")}
                                variant="body2"
                                style={{ cursor: 'pointer', textDecoration: 'underline' }}
                            >
                                Already have an account? Sign in
                            </Link>
                        </Grid>
                    </Grid>
                </Box>

                {/* Social Login Options, same as in Login component */}
                {/* ... */}
            </Paper>
        </Container>
    );
}
