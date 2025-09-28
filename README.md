# NSW Address Lookup 

looks up an NSW street address and returns:

* Geographic coordinates (lat/lon)
* Suburb name
* State Electoral District (SED)


## Architecture

1.Geocode the input address using NSW Geocoded Addressing Theme → get longitude, latitude.

2.Reverse overlay those coordinates on NSW Administrative Boundaries to fetch:

* Suburb (Localities layer)

* State Electoral District (SED layer)

3.Respond with a compact JSON object.

No AWS credentials are required at runtime; all NSW services are public.

## Build
```bash
mvn clean package
```

This produces: `target/nsw-address-lookup-springboot-1.0.0.jar`

## Local
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
Created a Lambda function with:
- **Runtime**: Java 17
- **Handler**: `com.example.lambda.handler.StreamLambdaHandler`
- Uploaded the shaded JAR nsw-address-lookup-springboot-1.0.0-shaded.jar'

##  Function URL:
https://ljodtu5xlxur4izzvxizmys32a0hlxtr.lambda-url.ap-southeast-2.on.aws/

sample test
```bash
curl.exe -v "https://ljodtu5xlxur4izzvxizmys32a0hlxtr.lambda-url.ap-southeast-2.on.aws/?address=206/7-11%20Derowie%20Avenue%20Homebush"
