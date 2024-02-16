> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this base directory.

> **IMPORTANT NOTE**: In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your Server class matches the path specified in the run script. Currently, it is set to execute Server at `edu/brown/cs/student/main/server/Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

# Project Details
This project is a server application that provides a web API for data retrieval and search. It uses two data sources: CSV files under path data/ in the server, and the United State Census Api.</br>
Users can perform load, view, and search with the CSV files, and query data from  the United State Census Api by county, state, and variables in a given set.

# Design Choices
Operations (load, view, and search) on CSV files are implemented through `LoadCSVHandler`, `ViewCSVHandler`, and `SearchCSVHandler`. These handlers route user's endpoint requests(/loadcsv, /viewcsv, and /searchcsv) to the server backend and performs corresponding data retrieving logic.
The class AccessCSV is dedicated to directly managing CSV files. It encapsulates the logic for loading, viewing, and searching within CSV files. This separation ensures that the file handling logic is isolated from the web server's request handling.

User endpoint request to access data from United State Census Api is implemented through  `BroadbandHandler`.  It takes in a `CensusDataSource` interface, which encapsulates the logic for retrieving data from United State Census Api.
The `CensusDataSource` interface is implemented through `ACSCensusDataSource` and `CachedACSDataSource`. `ACSCensusDataSource` retrieves data from United State Census Api with given parameters, and `CachedACSDataSource` wraps  `ACSCensusDataSource` to cache retrieved data. 

# Errors/Bugs

# Tests
We performed unit tests for each of the classes mentioned in Design Choices, except for the handler class. We perform integration testing with mocked data source for all handler classes.

# How to
To start the server,
```angular2html
mvn package
./run
```
To send http request to server,
- Load, view, search:
  - All CSV file can only be viewed/searched after it is loaded. 
```angular2html
http://localhost:3232/loadcsv?path=<path under data/>
http://localhost:3232/viewcsv
http://localhost:3232/searchcsv?query=<query>
```
- Notes on query syntax:
  - Basic Syntax of a query:
    <colIdentifier>,<searchKey(s)>;
    - 'and', 'or', and 'not' can be used in searchKey area. Corresponding symbols are
        - and &, $
        - or |
        - not ^
    - 'and'(&&) can be used for connecting multiple independent queries.
    - 'and'(&)  be used to connect negated search terms(i.e. <searchKey>) within a query
    - 'or' is only used for connecting multiple searchKeys within a query, and it cannot be used to connected negated searchKey given our definition of matching.
    - 'not' is only used for negation of a single searchKey. It cannot be used to negate a query or negate multiple searchKeys connected by 'or'
    - Each search query is separated by '\n'
    - Multiple searchKeys can only be connected by either | or &. | and & cannot be used at the same time within a query.
    - If column identifier is not provided, <colIdentifier> should be replaced with '*'
    - Query examples:
        - The column at index 1 contains the value ‘17’, and the column ‘name1’ contains ‘Nim’<br/>
          `1,17&&name1,Nim`
        - Return all rows that contains 'John'
          `*,John`
        - The column at index 3 contains either the value ‘17’ or the value ‘18’ or '19<br/>
          `3,17|18|19`
        - The column at index 2 does not contain the value ‘5’<br/>
          `2,^5`
        - The column at index 2 does not contain the value ‘5’ and does not contain 7<br/>
          `2,^5&^7`
To get data from United States Census Api,
```angular2html
....
```

