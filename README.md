# Restful Booker API Automation

A BDD-based and data-driven API automation framework developed for testing the [Restful Booker APIs](https://restful-booker.herokuapp.com/apidoc/index.html).

The framework covers positive and negative scenarios for authentication, health check, booking creation, retrieval, full update, partial update and deletion.

## Technology Stack

- Java 17
- Maven
- REST Assured
- Cucumber BDD
- TestNG
- Apache POI
- Jackson
- PicoContainer
- JSON Schema Validator

## Framework Features

- BDD scenarios using Cucumber
- Endpoint-specific API client classes
- Data-driven testing using Excel
- Separate Excel sheets for different request types
- Positive and negative API test coverage
- Scenario Outline support for multiple datasets
- Runtime data sharing using ScenarioContext
- JSON schema validation
- Soft assertions for response-data validation
- Configurable environment properties
- Smoke and regression suite execution
- Cucumber HTML and JSON reports
- API responses attached to reports
- Sensitive authentication tokens masked in reports

## Project Structure

```text
restful-booker-api-automation
├── pom.xml
├── README.md
├── .gitignore
│
├── src/test/java/com/restfulbooker
│   ├── clients
│   ├── config
│   ├── context
│   ├── hooks
│   ├── models
│   ├── runner
│   ├── stepdefinitions
│   └── utils
│
└── src/test/resources
    ├── features
    ├── schemas
    ├── testdata
    ├── config.properties
    ├── smoke-suite.xml
    └── regression-suite.xml
```

## Package Responsibilities

| Package | Responsibility |
|---|---|
| `clients` | Sends endpoint-specific HTTP requests |
| `config` | Loads environment and authentication configuration |
| `context` | Shares response, booking ID and token within a scenario |
| `hooks` | Attaches API responses to reports and masks sensitive data |
| `models` | Represents API request payloads as Java objects |
| `runner` | Configures and starts Cucumber execution |
| `stepdefinitions` | Implements Given, When and Then steps |
| `utils` | Provides request specifications, Excel reading and data mapping |

## API Coverage

| API | Coverage |
|---|---|
| Health Check | API availability |
| Authentication | Valid and invalid credentials |
| Create Booking | Multiple valid datasets, missing fields and empty body |
| Get Booking | All IDs, booking by ID, filters and invalid IDs |
| Full Update | Valid update, authentication failures, missing fields and invalid ID |
| Partial Update | Different field combinations, authentication failures and invalid ID |
| Delete Booking | Successful deletion, authentication failures, invalid ID and repeated deletion |

## Prerequisites

The following software must be installed:

- Java 17 or later
- Apache Maven
- Git

Verify the installation:

```bash
java -version
javac -version
mvn -version
git --version
```

## Configuration

Environment configuration is maintained in:

```text
src/test/resources/config.properties
```

Default configuration:

```properties
base.url=https://restful-booker.herokuapp.com
username=admin
password=password123
```

The username and password are public test credentials provided for the Restful Booker demo API. Real project credentials must not be committed to source control.

## Test Data

Booking request data is maintained in:

```text
src/test/resources/testdata/BookingTestData.xlsx
```

Workbook sheets:

- `CreateBooking`
- `UpdateBooking`
- `PartialUpdateBooking`

Feature files pass only the required test-case ID. The framework reads the matching Excel row and converts it into the required API payload.

## Running the Tests

### Complete test suite

```bash
mvn clean test
```

### Smoke suite

```bash
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/smoke-suite.xml
```

### Regression suite

```bash
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/regression-suite.xml
```

### Run tests directly using a Cucumber tag

```bash
mvn test '-Dcucumber.filter.tags=@smoke'
```

Other tag examples:

```bash
mvn test '-Dcucumber.filter.tags=@positive'
```

```bash
mvn test '-Dcucumber.filter.tags=@negative'
```

## Reports

After execution, reports are generated inside the `target` directory.

### Cucumber HTML report

```text
target/cucumber-report.html
```

Open it on macOS using:

```bash
open target/cucumber-report.html
```

### Cucumber JSON report

```text
target/cucumber-report.json
```

### Surefire reports

```text
target/surefire-reports
```

API responses and status codes are attached to the Cucumber HTML report. Authentication tokens are masked before being added to the report.

## JSON Schema Validation

Successful JSON responses are validated against schemas maintained in:

```text
src/test/resources/schemas
```

The schema validations verify:

- Required response fields
- Response data types
- Nested object structure
- Booking ID format
- Booking-date format

## Test Design

The suite validates API responses at three levels:

1. HTTP status-code validation
2. Business-data validation
3. JSON contract/schema validation

Fresh bookings are created whenever an existing booking is required. Therefore, update and delete scenarios do not depend on booking IDs from previous executions.

## Notes

Restful Booker is a public test API. Its data resets periodically and it may occasionally respond slowly or return temporary server errors. Re-running a failed test can help determine whether a failure is caused by the public environment or by the automation code.

## Author

Manvi Bhargava