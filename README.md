# Spring Boot + React OAuth Login Template

> 스프링 부트 + 리액트 프론트엔드: OAuth2 소셜 로그인 + 홈페이지 회원가입

![app_demo](https://github.com/Alfex4936/oauth2-spring-boot-react-template/assets/2356749/4e28e168-9dcc-428b-b032-d34a666a4563)

## About the Project

This template is a web application built using React v18.

This application leverages the capabilities of React Router v6 for routing and Zustand for JWT token state management.

## JWT token state management

> [!IMPORTANT]
> Zustand for State Management

### OAuth2 Authentication Flow:
1. **Initial Redirect with Short-Lived JWT**: When a user logs in using OAuth2 (e.g., Google, Facebook), they are redirected to the frontend with a short-lived JWT in the query parameter (e.g., `frontend/token?=token=12345`).
2. **Token Exchange for Long-Lived JWT**: The frontend token page makes a call to the backend server, exchanging the short-lived JWT for a long-lived JWT.
3. **Secure Token Storage**: The long-lived JWT is then securely stored in the frontend state using Zustand.
4. **Usage in Subsequent Requests**: For subsequent API calls, the stored JWT is included in the authorization header as a Bearer token (`Authorization: Bearer <token>`).

### Traditional Sign-Up Flow:
1. **Sign-Up and Token Reception**: Users signing up through the traditional method receive a long-lived JWT directly in the response header upon successful registration.
2. **Token Storage and Management**: This token is then stored and managed in the frontend using Zustand, similar to the OAuth2 flow.

### Key Features

> [!IMPORTANT]
> No Refresh Token implementation

- **Social Media Login**: Users can easily sign up and log in using their existing social media accounts.
    - Google
    - Github
    - Naver
    - Kakao
- **Traditional Sign-Up and Login**: A straightforward and secure way for users to create and access their accounts.
- **User Profile Page**: A personalized space for users to view and manage their profile information.

## Getting Started

### Prerequisites

- Node.js
- npm or yarn

### Usage

1. Run Spring boot backend
   ```sh
   cd backend

   ./gradlew bootRun
   ```

2. Run React frontend
   ```sh
   cd frontend

   yarn install
   yarn start
   ```

The frontend will be available at `http://localhost:8123`.

## Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

Distributed under the [MIT]. See `LICENSE` for more information.
