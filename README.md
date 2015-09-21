# GEO Coding Challenge

## Quick start

1. Install Java
2. ./gradlew run

The application will: 

- Start an embedded H2 database instance, create a table based on /src/main/resources/schema.sql and pre-insert 10,000 random latitude and longitude points

- Start an embedded Tomcat instance on port 8080 running the API detailed below.

- Query the REST API for all the random points and create a CSV file with the following columns:
 - Latitude of the point
 - Longitude of the point
 - Boolean whether the point is in the contiguous US
 - Boolean whether the point is within 500 miles of Tokyo
 - The distance in miles from Tokyo
 - Boolean whether the point is within 500 miles of Sydney
 - The distance in miles from Sydney
 - Boolean whether the point is within 500 miles of Riyadh
 - The distance in miles from Riyadh
 - Boolean whether the point is within 500 miles of Zurich
 - The distance in miles from Zurich
 - Boolean whether the point is within 500 miles of Reykjavik
 - The distance in miles from Reykjavik
 - Boolean whether the point is within 500 miles of Mexico City
 - The distance in miles from Mexico City
 - Boolean whether the point is within 500 miles of Lima
 - The distance in miles from Lima


## API

GET /points:

- 200: *Returns all points in the system*

GET /points/{lat}:{long}:

- 200: *Returns a boolean if the point exists in the system*

POST /points:

 JSON Request body example:

 ```
 {
    "latitude": 100.0000,
    "longitude": -56.0000
 }
 ```

- 201: *Creates a new point*
- 302: *Point already exists in the system*
- 400: *There's something wrong with the JSON request body*