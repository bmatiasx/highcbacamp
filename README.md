# High Cba Camping

This REST API is designed to manage the reservations for the campsite in order to accomodate
the incoming and potential guests.

### The constraints are:
* The campsite can be reserved for a maximum period of 3 days.
* The campsite can be reserved minumum 1 day ahead and up to 1 month in advance.
* Reservations can be cancelled anytime.
* Default date range is 1 month.
* For reserving the campsite the user should provide first name, last name and an email
in addition to the arrival and departre dates. Unique booking identifier
will be returned back to the user.
* Provide appropriate endpoints to modify/cancel an existing reservation.
* The API should handle concurrent requests so that the overlapping scenarios are avoided.
* The API should provide appropriate error messages for failure cases.
* The API should be able to handle large volume of requests for checking the
campsite availability.

## Running the tests
To run unit and integration tests
````
mvn clean test
````

IDE output for coverage:

![test coverage](https://github.com/bmatiasx/highcbacamp/blob/master/src/test/resources/img/test__coverage.png)

### Concurrent test case
The concurrency scenario can be run from CLI navigating to the project directory
 `src/test/resources/json/concurrency` and then with _curl_ command as follows:
````
curl -H "Content-Type: application/json" -d 
@create-reservation-concurrency-test-1.json 
http://localhost:8080/api/reservation/create 
& curl -H "Content-Type: application/json" -d 
@create-reservation-concurrency-test-2.json 
http://localhost:8080/api/reservation/create 
& curl -H "Content-Type: application/json" -d 
@create-reservation-concurrency-test-3.json 
http://localhost:8080/api/reservation/create
```` 
Then the response looks like this:
````
Reservation created successfully with bookingId: 1
{"timestamp":"2019-12-08 06:16:45 PM","status":400,"error":"Bad 
Request","message":"The chosen dates are not available. Please select others"}
{"timestamp":"2019-12-08 06:16:45 PM","status":400,"error":"Bad 
Request","message":"The chosen dates are not available. Please select others"}
````

### Load test
The handling of big amounts of requests was done with Apache Benchmark tool.
The input was as follows:
`ab -n 10000 -c 300 -k http://localhost:8080/api/availability`

Where the parameters are:
 + **-n** Number of requests to perform
 + **-c** Number multiple requests to make at a time
 + **-k** Use HTTP KeepAlive feature which allows a connection to stay open 
 for several HTTP requests 

The result is:
````
This is ApacheBench, Version 2.3 <$Revision: 1843412 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 1000 requests
Completed 2000 requests
Completed 3000 requests
Completed 4000 requests
Completed 5000 requests
Completed 6000 requests
Completed 7000 requests
Completed 8000 requests
Completed 9000 requests
Completed 10000 requests
Finished 10000 requests


Server Software:
Server Hostname:        localhost
Server Port:            8080

Document Path:          /api/availability
Document Length:        124 bytes

Concurrency Level:      300
Time taken for tests:   5.838 seconds
Complete requests:      10000
Failed requests:        0
Keep-Alive requests:    0
Total transferred:      2290000 bytes
HTML transferred:       1240000 bytes
Requests per second:    1712.89 [#/sec] (mean)
Time per request:       175.143 [ms] (mean)
Time per request:       0.584 [ms] (mean, across all concurrent requests)
Transfer rate:          383.06 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0  10.0      0     501
Processing:     3  172 246.8     92    2248
Waiting:        2  141 222.2     69    1913
Total:          3  172 247.0     92    2248

Percentage of the requests served within a certain time (ms)
  50%     92
  66%    101
  75%    108
  80%    120
  90%    584
  95%    608
  98%   1028
  99%   1085
 100%   2248 (longest request)
````
## Persistence layer
The persistence was made using H2 in-memory data base. The url to access 
the db console is `http://localhost:8080/h2`.
With Driver class `org.h2.Driver`, JDBC url `jdbc:h2:mem:campdb`,
 username `sa` and password empty.
 
 ## Endpoints
 The API endpoints and specifications can be found at `http://localhost:8080/swagger-ui.html` page.
 Also the _javadoc_ maven plugin incorporated allows to get an API doc by typing in the CLI the command:
 `mvn javadoc:javadoc`
 Such _javadoc_ is located at `/target/site/apidocs/` directory.
