# NSW Address Lookup — Spring Boot on AWS Lambda (Option B)

This project uses **Spring Boot 3** + **aws-serverless-java-container** to run a REST controller on AWS Lambda.

## Build
```bash
mvn clean package
```

This produces: `target/nsw-address-lookup-springboot-1.0.0-shaded.jar`

## Local (Spring Boot JVM)
```bash
mvn spring-boot:run
# Then call:
curl "http://localhost:8080/?address=346%20PANORAMA%20AVENUE%20BATHURST"
```

## Local (Lambda emulation with SAM)
Requires Docker + AWS SAM CLI.
```bash
sam local start-api
curl "http://127.0.0.1:3000/?address=346%20PANORAMA%20AVENUE%20BATHURST"
```

## Deploy
Create a Lambda function with:
- **Runtime**: Java 17
- **Handler**: `com.example.lambda.handler.StreamLambdaHandler`
- Upload the shaded JAR.

## Configuration
- Env vars (highest priority): `ADDR_QUERY`, `SUBURB_QUERY`, `SED_QUERY`, `HTTP_TIMEOUT_MS`, `RETRY_MAX_ATTEMPTS`, `RETRY_BASE_DELAY_MS`
- Optional file: `config/app.yaml` (or `/opt/config/app.yaml` via Lambda Layer)

## Endpoint
`GET /?address=<FULL ADDRESS>` →
```json
{
  "location": { "latitude": -33.429..., "longitude": 149.567... },
  "suburb": "BATHURST",
  "stateElectoralDistrict": "BATHURST"
}
```
