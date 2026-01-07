# OAuth2 M2M Demo with Auth0

This is a Spring Boot application demonstrating Machine-to-Machine (M2M) OAuth2 authentication using Auth0 as the identity provider.

## Overview

The application provides two endpoints:
- **Public endpoint**: `/api/public/hello` - Accessible without authentication
- **Private endpoint**: `/api/private/hello` - Requires a valid JWT token from Auth0

## Prerequisites

- Java 17 or higher
- Gradle (wrapper included)
- Auth0 account with an M2M application configured

## Configuration

### Auth0 Setup

1. Create an Auth0 account at [auth0.com](https://auth0.com)
2. Create a new M2M application in your Auth0 dashboard
3. Note down the following values:
   - **Domain**: Your Auth0 domain (e.g., `dev-xxxxxx.us.auth0.com`)
   - **Client ID**: From your M2M application
   - **Client Secret**: From your M2M application
   - **API Identifier**: Create an API in Auth0 and note its identifier

### Application Configuration

The application uses `application.yaml` for configuration. Key settings:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${ISSUER_URI}  # Your Auth0 domain
          audience: https://myapi.example.com  # Your API identifier
```

Set the environment variable `ISSUER_URI` to your Auth0 domain:
```bash
export ISSUER_URI=https://your-domain.us.auth0.com/
```

## Running the Application

### Using Gradle
```bash
./gradlew bootRun
```

### Using VS Code
- Open the project in VS Code
- Use the "OauthDemoApplication" launch configuration (environment variable is already configured)

### Using IDE
- Run the main class: `com.example.oauth_demo.OauthDemoApplication`
- Make sure the `ISSUER_URI` environment variable is set

## Testing the Endpoints

### 1. Test Public Endpoint
```bash
curl http://localhost:8080/api/public/hello
```

Expected response:
```json
{
  "message": "Hello from a public endpoint! No authentication required."
}
```

### 2. Obtain Access Token

Get an access token using your Auth0 M2M application credentials:

```bash
TOKEN=$(curl --request POST \
  --url https://your-domain.us.auth0.com/oauth/token \
  --header 'content-type: application/json' \
  --data '{
    "client_id":"YOUR_CLIENT_ID",
    "client_secret":"YOUR_CLIENT_SECRET",
    "audience":"YOUR_API_IDENTIFIER",
    "grant_type":"client_credentials"
  }' | jq -r '.access_token')

echo "Access Token: $TOKEN"
```

### 3. Test Private Endpoint
```bash
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8080/api/private/hello
```

Expected response:
```json
{
  "message": "Hello from a secured endpoint!",
  "user": "your-client-id@clients",
  "claims": {
    "iss": "https://your-domain.us.auth0.com/",
    "sub": "your-client-id@clients",
    "aud": "YOUR_API_IDENTIFIER",
    "iat": 1234567890,
    "exp": 1234567890,
    "azp": "your-client-id",
    "gty": "client-credentials"
  }
}
```

## Security Implementation

The application uses Spring Security with OAuth2 Resource Server configuration:

- **JWT Validation**: Validates issuer and audience claims
- **Custom Audience Validator**: Ensures the token contains the required audience
- **Security Filter Chain**: Configures endpoint access rules

## Project Structure

```
src/
├── main/
│   ├── java/com/example/oauth_demo/
│   │   ├── OauthDemoApplication.java     # Main Spring Boot application
│   │   ├── SecurityConfig.java           # Security configuration
│   │   ├── AudienceValidator.java        # Custom JWT audience validator
│   │   └── controller/
│   │       └── ApiController.java        # REST endpoints
│   └── resources/
│       └── application.yaml              # Application configuration
└── test/
    └── java/com/example/oauth_demo/
        └── OauthDemoApplicationTests.java # Unit tests
```

## Troubleshooting

### Common Issues

1. **Invalid Token Error**
   - Verify the `ISSUER_URI` environment variable is set correctly
   - Ensure the token's audience matches the configured audience
   - Check token expiration

2. **Connection Refused**
   - Make sure the application is running on port 8080
   - Verify no firewall is blocking the port

3. **Auth0 Configuration Issues**
   - Confirm your Auth0 domain is correct
   - Verify client credentials are valid
   - Ensure the API identifier matches between Auth0 and application config

### Debug Logging

Enable debug logging by adding to `application.yaml`:

```yaml
logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.security.oauth2: DEBUG
```

## Development

### Building
```bash
./gradlew build
```

### Testing
```bash
./gradlew test
```

### Code Style
The project follows standard Spring Boot conventions and uses Gradle for dependency management.