# GEO Coding Challenge

## API

GET /points:
- 200: *Returns all points in the system*

GET /points/{lat}:{long}:
- 200: *Returns a boolean if the point exists in the system*

POST /points:

 Request body example:

 ```
 {
    "latitude": 100.0000,
    "longitude": -56.0000
 }
 ```

- 201: *Creates a new point*
- 302: *Point already exists in the system*
- 400: *There's something wrong with the JSON request body*