# Pramana Demo Project

> **Demonstrating crash-resistant, real-time test reporting with Pramana - The truth-seeking test reporter**

This project demonstrates how to integrate [Pramana](https://github.com/yourusername/pramana) with **any test automation framework** through its simple REST API. Pramana provides persistent, real-time reporting that survives crashes and works with any programming language.

**This Example Uses:** TestNG + Selenium (Java)

**But Pramana Works With:** TestNG, JUnit, pytest, Mocha, Jest, Playwright, Cypress, PHPUnit, RSpec, Robot Framework, Cucumber, or **any framework in any language** that can make HTTP requests.

📖 **See [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md) for integration examples in other frameworks and languages.**

---

## What is Pramana?

**Pramana** is an AI-augmented test reporting platform designed for the modern QA ecosystem. It transforms raw test execution data into real-time, intelligent insights — helping teams identify issues faster, detect flaky tests, and make data-driven quality decisions.

### The Problem with Traditional Reporters

Traditional test reporters like **Extent Report** and **Allure** have critical limitations:

- **Crash = Data Loss**: If test execution is interrupted, the entire report is lost
- **No Resumability**: Users must rerun the complete suite to get a report
- **No Historical Analysis**: Limited insights into test flakiness and trends
- **Framework Lock-in**: Tightly coupled with specific testing frameworks

### How Pramana Solves This

Pramana provides **persistent, real-time test reporting** through a simple REST API:

- **Crash-Resistant**: Test results stored in real-time to SQLite database
- **Resume Capability**: Continue test execution from where it stopped
- **Smart Rerun Logic**: If a test passes on rerun, final status is "passed" (with flakiness flag)
- **Framework Agnostic**: REST API works with **ANY testing framework in ANY language**
- **Zero Configuration**: Just run the JAR - works out of the box
- **Lightweight**: < 20MB JAR file, minimal resource usage

**Supported Frameworks & Languages:**
- Java: TestNG, JUnit 5, Cucumber
- Python: pytest, unittest, Robot Framework
- JavaScript/TypeScript: Mocha, Jest, Playwright, Cypress
- PHP: PHPUnit
- Ruby: RSpec
- Go: Go testing
- Any language with HTTP client capabilities

---

## Core Features Demonstrated

### 1. Crash-Resistant Reporting
Unlike traditional reporters, Pramana saves test results **as they execute**. Even if your test run crashes:
- All completed test results are preserved
- You can view partial reports immediately
- No need to rerun everything

### 2. Flakiness Detection
Pramana automatically tracks:
- Tests that fail sometimes but pass on retry
- Historical pass/fail trends
- Unstable test identification

### 3. Real-Time Dashboard
View test execution progress in real-time at `http://localhost:9090`

### 4. Attachments Support
- Screenshots on failure (Base64 embedded)
- Log files
- Videos
- Any binary data

### 5. Test History & Trends
- Track test performance over time
- Identify regression patterns
- Compare suite executions

---

## Why This Demo Uses TestNG?

This demo project uses **TestNG + Selenium** simply as an example. The same approach works with:

- **JUnit 5** - See [INTEGRATION_GUIDE.md#java-junit-5](INTEGRATION_GUIDE.md#java-junit-5)
- **pytest** - See [INTEGRATION_GUIDE.md#python-pytest](INTEGRATION_GUIDE.md#python-pytest)
- **Mocha** - See [INTEGRATION_GUIDE.md#javascripttypescript-mocha](INTEGRATION_GUIDE.md#javascripttypescript-mocha)
- **Jest** - See [INTEGRATION_GUIDE.md#javascripttypescript-jest](INTEGRATION_GUIDE.md#javascripttypescript-jest)
- **Playwright** - See [INTEGRATION_GUIDE.md#playwright](INTEGRATION_GUIDE.md#playwright)
- **Cypress** - See [INTEGRATION_GUIDE.md#cypress](INTEGRATION_GUIDE.md#cypress)
- **Any other framework** - Just send HTTP requests to Pramana's API!

**The key concept**: Pramana doesn't care about your test framework. It only cares about receiving test results via its REST API.

---

## Project Structure

```
PramanaTest/
├── src/
│   └── test/
│       ├── java/
│       │   └── com/razatech/
│       │       ├── base/
│       │       │   └── BaseTest.java          # Base class for Selenium tests
│       │       ├── listeners/
│       │       │   └── PramanaListener.java   # TestNG listener for Pramana
│       │       ├── reporting/
│       │       │   └── PramanaReporter.java   # Pramana API client
│       │       └── tests/
│       │           ├── LoginTests.java        # Sample login tests
│       │           ├── HomePageTest.java      # Sample homepage tests
│       │           └── TablePageTest.java     # Sample table tests
│       └── resources/
│           └── config.properties              # Pramana configuration
├── testng.xml                                 # TestNG suite configuration
├── pom.xml                                    # Maven dependencies
├── INTEGRATION_GUIDE.md                       # Detailed integration guide
└── README.md                                  # This file
```

---

## Prerequisites

Before running this project, ensure you have:

- **Java 17+** installed
- **Maven 3.6+** installed
- **Chrome browser** (or update `BaseTest.java` for your browser)
- **Access to Pramana Server** - Choose one:
- **Option A (Hosted):** Use a deployed Pramana instance (recommended for teams)
- **Option B (Local):** Download Pramana JAR from [pramana-1.0.0.jar](https://drive.google.com/file/d/12Z9z48WYM56J51tlEBUlPNC8ZJ8sldJM/view?usp=sharing)

---

## Quick Start

### Step 1: Access Pramana Server

You have two options:

#### Option A: Use Hosted Pramana (Recommended)

If your team has deployed Pramana as a service, simply update `config.properties` with the hosted URL:

```properties
api.base.url=https://pramana.yourcompany.com
```

**Benefits:**
- No local setup required
- Centralized reporting for the entire team
- Accessible from CI/CD pipelines
- No need to start/stop server manually

#### Option B: Run Pramana Locally

Download the Pramana JAR and start the server locally:

```bash
java -jar pramana-1.0.0.jar
```

**Expected Output:**
```
Pramana Test Reporter started successfully!
Access Dashboard: http://localhost:9090
API Base URL: http://localhost:9090/api/v1
```

**Optional Configuration:**
```bash
# Custom port
java -jar pramana-1.0.0.jar --server.port=8080

# Custom database location
java -jar pramana-1.0.0.jar --pramana.db.path=/custom/path/pramana.db
```

### Step 2: Create a Test Suite in Pramana

Before running tests, create a suite in Pramana:

```bash
curl -X POST http://localhost:9090/api/v1/suites \
  -H "Content-Type: application/json" \
  -d '{
    "name": "PramanaTest Demo Suite",
    "environment": "staging",
    "tags": ["demo", "testng", "selenium"]
  }'
```

**Response:**
```json
{
  "id": "suite_abc123",
  "name": "PramanaTest Demo Suite",
  "environment": "staging",
  "status": "running",
  "startTime": "2025-11-03T10:00:00Z"
}
```

**Important**: Copy the `id` field (e.g., `suite_abc123`)

### Step 3: Configure Suite ID

Update `src/test/resources/config.properties`:

```properties
# Pramana API Configuration
api.base.url=http://localhost:9090
suite.id=suite_abc123  # Replace with your suite ID

# Note: You can override suite.id at runtime:
# mvn test -Dsuite.id=your-actual-suite-id
```

### Step 4: Run Tests

```bash
# Clean install and run tests
mvn clean test

# Run with custom environment
mvn test -Denv=production

# Override suite ID at runtime
mvn test -Dsuite.id=suite_xyz456
```

### Step 5: View Results

Open your browser and navigate to:
```
http://localhost:9090
```

You'll see:
- ✅ Real-time test execution progress
- ✅ Pass/fail statistics
- ✅ Test duration and timestamps
- ✅ Error messages and stack traces for failures
- ✅ Flakiness indicators

---

## How It Works

### Architecture

```
┌─────────────────────┐         ┌──────────────────┐         ┌────────────────┐
│   ANY Test          │ ──────> │  Your Framework  │ ──────> │  HTTP Client   │
│   Framework         │         │  Listener/Hook   │         │  (REST API)    │
│ (TestNG, pytest,    │         │ (Lifecycle mgmt) │         └────────┬───────┘
│  Mocha, Jest, etc.) │         └──────────────────┘                  │
└─────────────────────┘                                                │
                                                            HTTP POST/PUT
                                                                       │
                                                                       ▼
                                                         ┌────────────────────┐
                                                         │  Pramana Server    │
                                                         │  - SQLite DB       │
                                                         │  - REST API        │
                                                         │  - Dashboard       │
                                                         └────────────────────┘
```

**This example (TestNG):**
```
┌─────────────────┐         ┌──────────────────┐         ┌────────────────┐
│  TestNG Tests   │ ──────> │ PramanaListener  │ ──────> │ PramanaReporter│
│  (LoginTests,   │         │ (Test Lifecycle) │         │  (HTTP Client) │
│   HomePage...)  │         └──────────────────┘         └────────┬───────┘
└─────────────────┘                                                │
                                                                   │ REST API
                                                                   ▼
                                                         ┌────────────────────┐
                                                         │  Pramana Server    │
                                                         └────────────────────┘
```

### Test Execution Flow

1. **Suite Start**: `PramanaListener.onStart()` → Uses suite ID from config.properties
2. **Test Execution**: Each test runs normally
3. **Test Complete**: `PramanaListener` → Sends result to Pramana API
4. **Suite Complete**: `PramanaListener.onFinish()` → Marks suite as complete

### Key Components

#### 1. PramanaReporter (src/test/java/com/razatech/reporting/PramanaReporter.java)
- Loads configuration from `config.properties`
- Provides API methods: `createSuite()`, `logTestResult()`, `completeSuite()`
- Uses Apache HttpClient 5 for REST calls
- Uses Jackson for JSON serialization

#### 2. PramanaListener (src/test/java/com/razatech/listeners/PramanaListener.java)
- Implements TestNG's `ITestListener` and `ISuiteListener`
- Hooks into test lifecycle events
- Automatically reports test results to Pramana

#### 3. config.properties (src/test/resources/config.properties)
- Stores Pramana server URL
- Stores suite ID (can be overridden via `-Dsuite.id`)

---

## Configuration Options

### Environment Variables

```bash
# Override suite ID
mvn test -Dsuite.id=suite_custom123

# Set environment
mvn test -Denv=production

# Both
mvn test -Dsuite.id=suite_abc -Denv=staging
```

### config.properties

```properties
# Pramana Server URL
api.base.url=http://localhost:9090

# Suite ID (required)
suite.id=suite_8454f04b
```

---

## API Endpoints Used

This project demonstrates the following Pramana API endpoints:

### 1. Create Suite
```bash
POST /api/v1/suites
Content-Type: application/json

{
  "name": "Suite Name",
  "environment": "staging",
  "tags": ["tag1", "tag2"]
}
```

### 2. Log Test Result
```bash
POST /api/v1/suites/{suiteId}/tests
Content-Type: application/json

{
  "testCaseId": "LoginTests_testValidLogin",
  "testName": "Login with valid credentials",
  "status": "passed",
  "duration": 2500,
  "startTime": "2025-11-03T10:05:00Z",
  "endTime": "2025-11-03T10:05:02Z"
}
```

### 3. Complete Suite
```bash
PUT /api/v1/suites/{suiteId}/complete
```

### 4. Add Attachments (Optional)
```bash
POST /api/v1/tests/{testId}/attachments
Content-Type: application/json

{
  "type": "screenshot",
  "name": "login-failure.png",
  "content": "iVBORw0KGgo...",
  "description": "Screenshot on failure"
}
```

---

## Advantages Over Traditional Reporters

### vs. Extent Report

| Feature | Extent Report | Pramana |
|---------|--------------|---------|
| **Crash Resistance** | ❌ Report lost on crash | ✅ Real-time persistence |
| **Resume Tests** | ❌ Not supported | ✅ Continue from last point |
| **Framework Agnostic** | ❌ Tightly coupled | ✅ Works with any framework |
| **Flakiness Detection** | ❌ No | ✅ Automatic tracking |
| **Historical Trends** | ❌ Limited | ✅ Full history |
| **Real-time Viewing** | ❌ Only after completion | ✅ Live updates |

### vs. Allure

| Feature | Allure | Pramana |
|---------|--------|---------|
| **Setup Complexity** | ⚠️ Requires Allure CLI + plugins | ✅ Single JAR file |
| **Data Persistence** | ❌ File-based (fragile) | ✅ SQLite database |
| **API-First Design** | ❌ No | ✅ REST API |
| **Multi-Framework** | ⚠️ Requires adapters | ✅ Universal API |
| **Resource Usage** | ⚠️ Heavy | ✅ Lightweight |

---

## Best Practices

### 1. Create One Suite Per Test Execution
```bash
# Good: Create a new suite for each run
curl -X POST http://localhost:9090/api/v1/suites -d '{"name": "Run #123"}'

# Then use that suite ID in config.properties
```

### 2. Use Meaningful Test Case IDs
The project automatically generates IDs like:
- `LoginTests_testValidLogin`
- `HomePageTest_testPageTitle`

This ensures test history tracking works correctly.

### 3. Don't Commit .db Files
The `.gitignore` is already configured to exclude:
- `*.db`
- `*.db-journal`
- `pramana.db`

### 4. Take Screenshots on Failure
You can extend `BaseTest.java` to capture screenshots:

```java
@AfterMethod
public void tearDown(ITestResult result) {
    if (result.getStatus() == ITestResult.FAILURE) {
        // Capture screenshot
        TakesScreenshot ts = (TakesScreenshot) driver;
        String screenshot = ts.getScreenshotAs(OutputType.BASE64);

        // Upload to Pramana (implement this)
        uploadScreenshot(screenshot);
    }

    if (driver != null) {
        driver.quit();
    }
}
```

---

## Troubleshooting

### Issue: "Connection Refused"

**Cause**: Pramana server is not running

**Solution**:
```bash
# Check if Pramana is running
curl http://localhost:9090/api/v1/suites

# Start Pramana if not running
java -jar pramana-1.0.0.jar
```

### Issue: "Suite not found"

**Cause**: Suite ID in config.properties doesn't exist

**Solution**:
1. Create a new suite via API
2. Update `suite.id` in config.properties

### Issue: "Tests not appearing in dashboard"

**Cause**: Wrong suite ID or API URL

**Solution**:
1. Verify `api.base.url` in config.properties
2. Verify `suite.id` exists: `curl http://localhost:9090/api/v1/suites/{suiteId}`

### Issue: Maven build fails

**Cause**: Missing dependencies

**Solution**:
```bash
mvn clean install -U
```

---

## Running Tests in CI/CD

### GitHub Actions Example (Using Hosted Pramana)

**Recommended:** Use a centrally deployed Pramana instance for CI/CD:

```yaml
name: Run Tests with Pramana

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Create Test Suite
      id: create_suite
      run: |
        SUITE_ID=$(curl -X POST ${{ secrets.PRAMANA_URL }}/api/v1/suites \
          -H "Content-Type: application/json" \
          -d '{"name":"CI Run '$GITHUB_RUN_NUMBER'","environment":"ci","tags":["ci","github-actions"]}' \
          | jq -r '.id')
        echo "suite_id=$SUITE_ID" >> $GITHUB_OUTPUT

    - name: Run Tests
      run: mvn test -Dsuite.id=${{ steps.create_suite.outputs.suite_id }}
      env:
        PRAMANA_API_URL: ${{ secrets.PRAMANA_URL }}

    - name: View Report URL
      run: echo "Report available at ${{ secrets.PRAMANA_URL }}/suite/${{ steps.create_suite.outputs.suite_id }}"
```

**Note:** Set `PRAMANA_URL` as a GitHub secret (e.g., `https://pramana.yourcompany.com`)

### Alternative: Local Pramana in CI

If you prefer to run Pramana locally in CI:

```yaml
    - name: Start Pramana Server
      run: |
        java -jar pramana-1.0.0.jar &
        sleep 5

    - name: Create Test Suite
      id: create_suite
      run: |
        SUITE_ID=$(curl -X POST http://localhost:9090/api/v1/suites \
          -H "Content-Type: application/json" \
          -d '{"name":"CI Run '$GITHUB_RUN_NUMBER'","environment":"ci"}' \
          | jq -r '.id')
        echo "suite_id=$SUITE_ID" >> $GITHUB_OUTPUT

    - name: Run Tests
      run: mvn test -Dsuite.id=${{ steps.create_suite.outputs.suite_id }}
```

---

## Sample Test Output

When you run `mvn test`, you'll see:

```
Pramana Configuration Loaded:
  API Base URL: http://localhost:9090
  Using existing Suite ID: suite_8454f04b

========================================
Suite started: MySuite
========================================

✅ Test logged: Login with valid credentials [passed]
✅ Test logged: Login with invalid credentials [failed]
✅ Test logged: Check homepage title [passed]
✅ Test logged: Verify table data [passed]

========================================
Suite finished: MySuite
========================================
✅ Suite marked as complete

Tests run: 4, Failures: 1, Errors: 0, Skipped: 0
```

---

## Integrating with Other Frameworks

This project demonstrates TestNG integration, but Pramana works with **any test framework**.

### Available Integration Examples

Check [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md) for complete integration examples for:

| Framework/Language | Guide Link | Difficulty |
|-------------------|------------|------------|
| **Java (TestNG)** | [Guide](INTEGRATION_GUIDE.md#java-testng) | ⭐ Easy |
| **Java (JUnit 5)** | [Guide](INTEGRATION_GUIDE.md#java-junit-5) | ⭐ Easy |
| **Python (pytest)** | [Guide](INTEGRATION_GUIDE.md#python-pytest) | ⭐ Easy |
| **JavaScript (Mocha)** | [Guide](INTEGRATION_GUIDE.md#javascripttypescript-mocha) | ⭐ Easy |
| **JavaScript (Jest)** | [Guide](INTEGRATION_GUIDE.md#javascripttypescript-jest) | ⭐ Easy |
| **Playwright** | [Guide](INTEGRATION_GUIDE.md#playwright) | ⭐⭐ Medium |
| **Cypress** | [Guide](INTEGRATION_GUIDE.md#cypress) | ⭐⭐ Medium |
| **Selenium (any language)** | [Guide](INTEGRATION_GUIDE.md#selenium-any-language) | ⭐ Easy |

### Integration Pattern (Universal)

Regardless of your framework, the integration pattern is always:

1. **Before Suite**: Call `POST /api/v1/suites` to create a suite
2. **After Each Test**: Call `POST /api/v1/suites/{suiteId}/tests` to log result
3. **After Suite**: Call `PUT /api/v1/suites/{suiteId}/complete` to mark complete

That's it! No complex setup, no framework-specific adapters needed.

---

## Further Reading

- **Integration Guide**: See [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md) for detailed integration steps for all frameworks
- **Pramana Documentation**: [Link to Pramana docs]
- **API Reference**: http://localhost:9090/swagger-ui (if enabled)

---

## Contributing

This is a demo project. For contributing to Pramana itself, visit the main repository.

---

## License

This demo project is licensed under MIT License.

---

## Contact & Support

- **GitHub Issues**: [Report a bug](https://github.com/yourusername/pramana/issues)
- **Email**: support@pramana.io
- **Documentation**: [Pramana Docs](https://docs.pramana.io)

---

**Happy Testing! 🚀**

*"In Pramana, truth is not subjective — it's measurable."*