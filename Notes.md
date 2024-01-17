# Notes & Documentation for ***Mindex Java Challenge***
## Requirements/Setup
Reading through the requirements is always the first step in my process when developing an application. In this case, I have two tasks. To build upon an existing project, in return adding functionality consisting of new RESTful endpoints. Normally, our team would have a discussion with our Project Manager to make sure the team understands the requirements fully, addressing any concerns before starting the project. In this situation, it’s a take-home assignment, so I will construct it to the best of my ability according to the given requirements, taking notes along the way.

When delving into an existing project, my initial step involves navigating through its project structure so I can understand its composition. I found a few inconsistencies with the debug logger messages that must have been copy and pasted between methods in the Employee Controller. I fixed them to reflect the correct method type. After making sure all unit test cases passed, I continued with running a local instance of the application. During this process, I find it handy to have Postman running nearby which helps verify the API's are working as intended.

## Task 1 - Create a new type, Reporting Structure
This task seems pretty straightforward. Since the data will be on the fly and not persisted, there is no need to create a ReportingStructure repository. The numberOfReports property needs to track all distinct reports in the employee hierarchy, so there will need to be some business logic around this task. There are a few ways to go about this:

First, I could recursively check each employee’s direct reports and keep count of all distinct reports. This however, leads to multiple database calls directly and can cause latency when data reaches deeper hierarchies.

Secondly, I could populate all employee’s into a collection using a findAll() method in the EmployeeRepository, then iterate through each distinct report and keep a count. All Employees may not have direct reports, and this may impact performance by unnecessarily loading all Employee’s at once.

Third, I could use an aggregation framework within MongoDb, which would be more efficient than calling the database directly for each direct report, utilizing the graphLookup aggregation which is used to handle hierarchical data efficiently. However when testing, the version 1.25.0 we are using is outdated and does not support the graphLookup aggregation stage until version 3.4. I also wanted to avoid upgrading any dependencies since we could run into compatibility issues with major update releases
### Solution:
Since I am currently using a small amount of data, I chose to create a method in the ReportingStructureServiceImpl class that iterates through each employee’s direct report’s, using a for-each loop which recursively checks the direct reports count until no direct reports are left. This method can also scale into deeper hierarchies.

I also enabled lazy loading for the directReports list in the Employee bean for performance benefits since the directReports will be loaded on-demand. If I were to disable lazy loading, the method would be slightly altered by calling the EmployeeRepository for each direct report which resulted in longer response times. I tested these implementations in JUnit using ‘System.currentTimeMillis()’ starting before and after calling the endpoint and subtracting the difference. More in depth testing could also be used, JMeter as an example.
## Task 2 - Create a new type, Compensation.

Create/Read endpoints will need to be implemented and the data will need to be persisted this time so I will need to create a Compensation Repository. The payload will contain an Employee object, alongside a salary and effectiveDate.
### Solution:
For the Compensation bean I chose to use a String for salary, although a float or BigDecimal could also be used if calculations are needed for the future. I chose a LocalDate for the effectiveDate with an annotated “yyyy-MM-dd'' format. Could have also used “Instant” for a more precise date if time and time zones are needed, which are usually handled when gathering the requirements.

For the CompensationServiceImpl create() logic, I made sure to include a null check for an existing compensation to prevent duplicates and RunTimeExceptions. Normally, I recommend implementing an update() method to update an existing compensation for a given employee, however this is not in the requirements and could be discussed in the requirements process.

In the CompensationRepository, I had to use findByEmployee_EmployeeId() instead of findByEmployeeId() since the compensation entity does not have an EmployeeID.

Another note: Perhaps the compensation is better suited in the employee object instead. However, I understand salary information is sensitive data that may not need to be present when querying employee information for general services. Just a thought.
## Unit Testing:

When it comes to unit tests, my main goal is to ensure that I’m testing the functionality within the scope of the endpoint without adding unnecessary complexity. There are two ways to go about this. First, I could use ‘@Autowire’ to inject repositories and use them for testing the endpoints. However, this approach leans towards integration testing, involving real data and direct database calls. Secondly, I could mock the dependencies so that the unit test focuses on the specific behavior of the endpoint without using production data. I chose the latter in this case.

I also noticed that the EmployeeServiceImplTest has an autowired EmployeeService that was not being used, so I removed it from that specific test class.

## Security

Authentication - I would recommend adding authentication to our end points, possibly using tokens or a role based system, especially if our API’s are exposed to external applications.

Validation - Other ways to secure the application is to validate and sanitize user input to prevent injection attacks.

CORS - The use of Cross Origin Resource-Sharing (CORS) headers to prevent cross-origin security issues is always a great idea.

HTTPS - Configuring the server to enforce HTTPS and ensuring certificates are valid and up to date.

Although these are not in scope for this assignment, they are great ways to prevent security issues for the application.
