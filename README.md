# Quantity Measurement App (Backend)

Spring Boot backend for quantity measurement operations with JWT and Google OAuth login.

## Enable Google OAuth

1. Create a Google OAuth 2.0 Web Client in Google Cloud Console.
2. Add this redirect URI in Google Console:
   `http://localhost:8080/login/oauth2/code/google`
3. Create local secrets file from the template:
   - Copy `secrets/oauth-secrets.example.properties`
   - Save it as `secrets/oauth-secrets.properties`
4. Fill your credentials:
   - `spring.security.oauth2.client.registration.google.client-id=...`
   - `spring.security.oauth2.client.registration.google.client-secret=...`
5. Start backend and frontend:
   - Backend: `./mvnw spring-boot:run`
   - Frontend: `npm start` (from `QuantityMeasurementApp-Frontend`)

When credentials are present, frontend Google sign-in uses:
`/oauth2/authorization/google`
