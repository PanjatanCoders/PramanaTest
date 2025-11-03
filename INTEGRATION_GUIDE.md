# Pramana Test Reporter - Integration Guide

> Step-by-step guide to integrate Pramana with your test automation framework

---

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Setup](#quick-setup)
- [Integration Steps](#integration-steps)
- [Framework-Specific Examples](#framework-specific-examples)
  - [Java (TestNG)](#java-testng)
  - [Java (JUnit 5)](#java-junit-5)
  - [Python (pytest)](#python-pytest)
  - [JavaScript/TypeScript (Mocha)](#javascripttypescript-mocha)
  - [JavaScript/TypeScript (Jest)](#javascripttypescript-jest)
  - [Selenium (any language)](#selenium-any-language)
  - [Playwright](#playwright)
  - [Cypress](#cypress)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

---

## Prerequisites

Before integrating Pramana with your test framework:

✅ **Java 17+** installed on your machine
✅ **Pramana JAR** downloaded or built from source
✅ **HTTP client library** for your programming language
✅ **Test automation framework** already set up

---

## Quick Setup

### Step 1: Start Pramana Server

```bash
# Download Pramana JAR (or build from source)
java -jar pramana-1.0.0.jar

# Custom port (optional)
java -jar pramana-1.0.0.jar --server.port=8080

# Custom database location (optional)
java -jar pramana-1.0.0.jar --pramana.db.path=/custom/path/pramana.db
```

**Expected Output:**
```
Pramana Test Reporter started successfully!
Access Dashboard: http://localhost:9090
API Base URL: http://localhost:9090/api/v1
```

### Step 2: Verify Server is Running

Open your browser and navigate to:
```
http://localhost:9090
```

You should see the Pramana dashboard.

---

## Integration Steps

### Step 1: Create a Suite Before Test Execution

**API Endpoint:** `POST /api/v1/suites`

```bash
curl -X POST http://localhost:9090/api/v1/suites \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Login Test Suite",
    "environment": "staging",
    "tags": ["smoke", "regression"]
  }'
```

**Response:**
```json
{
  "id": "suite_abc123",
  "name": "Login Test Suite",
  "environment": "staging",
  "status": "running",
  "startTime": "2025-11-01T10:00:00Z"
}
```

**Save the `id` field** - you'll need it to log test results.

---

### Step 2: Log Test Results After Each Test

**API Endpoint:** `POST /api/v1/suites/{suiteId}/tests`

```bash
curl -X POST http://localhost:9090/api/v1/suites/suite_abc123/tests \
  -H "Content-Type: application/json" \
  -d '{
    "testCaseId": "TC001",
    "testName": "Login with valid credentials",
    "status": "passed",
    "duration": 2500,
    "startTime": "2025-11-01T10:05:00Z",
    "endTime": "2025-11-01T10:05:02Z"
  }'
```

**Request Fields:**
- `testCaseId` (required): Unique identifier for the test case
- `testName` (required): Human-readable test name
- `status` (required): `passed`, `failed`, or `skipped`
- `duration` (optional): Test duration in milliseconds
- `startTime` (required): ISO 8601 timestamp
- `endTime` (optional): ISO 8601 timestamp
- `errorMessage` (optional): Error message if test failed
- `stackTrace` (optional): Stack trace if test failed

---

### Step 3: Add Screenshots/Logs (Optional)

**API Endpoint:** `POST /api/v1/tests/{testId}/attachments`

```bash
curl -X POST http://localhost:9090/api/v1/tests/{testId}/attachments \
  -H "Content-Type: application/json" \
  -d '{
    "type": "screenshot",
    "name": "login-failure.png",
    "content": "iVBORw0KGgoAAAANSUhEUgAA...(base64)",
    "description": "Screenshot of login failure",
    "timestamp": "2025-11-01T10:05:02Z"
  }'
```

**Attachment Types:**
- `screenshot` - PNG/JPG images
- `video` - MP4/WebM videos
- `log` - Text logs
- `other` - Any other file type

---

### Step 4: Mark Suite as Complete

**API Endpoint:** `PUT /api/v1/suites/{suiteId}/complete`

```bash
curl -X PUT http://localhost:9090/api/v1/suites/suite_abc123/complete
```

This will:
- Set suite status to `completed`
- Calculate final test counts
- Set end time

---

## Framework-Specific Examples

### Java (TestNG)

#### Step 1: Add HTTP Client Dependency

**Maven (`pom.xml`):**
```xml
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
    <version>5.2.1</version>
</dependency>
```

#### Step 2: Create Pramana Helper Class

```java
package com.yourproject.reporting;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PramanaReporter {

    private static final String BASE_URL = "http://localhost:9090/api/v1";
    private static String currentSuiteId = null;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String createSuite(String name, String environment, List<String> tags) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/suites");
            request.setHeader("Content-Type", "application/json");

            Map<String, Object> body = Map.of(
                "name", name,
                "environment", environment,
                "tags", tags
            );

            request.setEntity(new StringEntity(mapper.writeValueAsString(body)));

            String response = client.execute(request, r -> {
                return new String(r.getEntity().getContent().readAllBytes());
            });

            Map<String, Object> result = mapper.readValue(response, Map.class);
            currentSuiteId = (String) result.get("id");

            System.out.println("✅ Pramana Suite Created: " + currentSuiteId);
            return currentSuiteId;

        } catch (Exception e) {
            System.err.println("❌ Failed to create Pramana suite: " + e.getMessage());
            return null;
        }
    }

    public static void logTestResult(String testCaseId, String testName,
                                      String status, long duration,
                                      String errorMessage, String stackTrace) {
        if (currentSuiteId == null) {
            System.err.println("⚠️ No active suite. Call createSuite() first.");
            return;
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(
                BASE_URL + "/suites/" + currentSuiteId + "/tests"
            );
            request.setHeader("Content-Type", "application/json");

            Map<String, Object> body = new HashMap<>();
            body.put("testCaseId", testCaseId);
            body.put("testName", testName);
            body.put("status", status);
            body.put("duration", duration);
            body.put("startTime", java.time.Instant.now().toString());

            if (errorMessage != null) {
                body.put("errorMessage", errorMessage);
            }
            if (stackTrace != null) {
                body.put("stackTrace", stackTrace);
            }

            request.setEntity(new StringEntity(mapper.writeValueAsString(body)));

            client.execute(request, r -> {
                System.out.println("✅ Test logged: " + testName + " [" + status + "]");
                return null;
            });

        } catch (Exception e) {
            System.err.println("❌ Failed to log test: " + e.getMessage());
        }
    }

    public static void completeSuite() {
        if (currentSuiteId == null) return;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(
                BASE_URL + "/suites/" + currentSuiteId + "/complete"
            );

            client.execute(request, r -> {
                System.out.println("✅ Suite marked as complete");
                return null;
            });

        } catch (Exception e) {
            System.err.println("❌ Failed to complete suite: " + e.getMessage());
        }
    }
}
```

#### Step 3: Integrate with TestNG Listeners

```java
package com.yourproject.listeners;

import com.yourproject.reporting.PramanaReporter;
import org.testng.*;
import java.util.Arrays;

public class PramanaListener implements ITestListener, ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        String suiteName = suite.getName();
        String environment = System.getProperty("env", "staging");

        PramanaReporter.createSuite(
            suiteName,
            environment,
            Arrays.asList("automated", "testng")
        );
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logTest(result, "passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logTest(result, "failed");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logTest(result, "skipped");
    }

    @Override
    public void onFinish(ISuite suite) {
        PramanaReporter.completeSuite();
    }

    private void logTest(ITestResult result, String status) {
        String testCaseId = result.getMethod().getMethodName();
        String testName = result.getMethod().getDescription() != null
            ? result.getMethod().getDescription()
            : result.getMethod().getMethodName();

        long duration = result.getEndMillis() - result.getStartMillis();

        String errorMessage = null;
        String stackTrace = null;

        if (result.getThrowable() != null) {
            errorMessage = result.getThrowable().getMessage();
            stackTrace = Arrays.toString(result.getThrowable().getStackTrace());
        }

        PramanaReporter.logTestResult(
            testCaseId, testName, status, duration, errorMessage, stackTrace
        );
    }
}
```

#### Step 4: Configure TestNG to Use the Listener

**testng.xml:**
```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Test Suite">
    <listeners>
        <listener class-name="com.yourproject.listeners.PramanaListener"/>
    </listeners>

    <test name="Login Tests">
        <classes>
            <class name="com.yourproject.tests.LoginTest"/>
        </classes>
    </test>
</suite>
```

**OR via annotation:**
```java
@Listeners(PramanaListener.class)
public class LoginTest {

    @Test(description = "Login with valid credentials")
    public void testValidLogin() {
        // Your test code
    }
}
```

---

### Java (JUnit 5)

#### Step 1: Add HTTP Client Dependency

Same as TestNG example above.

#### Step 2: Create JUnit 5 Extension

```java
package com.yourproject.extensions;

import com.yourproject.reporting.PramanaReporter;
import org.junit.jupiter.api.extension.*;

public class PramanaExtension implements
        BeforeAllCallback, AfterAllCallback, TestWatcher {

    @Override
    public void beforeAll(ExtensionContext context) {
        String suiteName = context.getDisplayName();
        String environment = System.getProperty("env", "staging");

        PramanaReporter.createSuite(
            suiteName,
            environment,
            Arrays.asList("automated", "junit5")
        );
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        logTest(context, "passed", null);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        logTest(context, "failed", cause);
    }

    @Override
    public void testDisabled(ExtensionContext context,
                            Optional<String> reason) {
        logTest(context, "skipped", null);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        PramanaReporter.completeSuite();
    }

    private void logTest(ExtensionContext context, String status,
                        Throwable throwable) {
        String testCaseId = context.getTestMethod()
            .map(m -> m.getName()).orElse("unknown");
        String testName = context.getDisplayName();

        long duration = 0; // JUnit 5 doesn't provide duration easily

        String errorMessage = throwable != null ? throwable.getMessage() : null;
        String stackTrace = throwable != null
            ? Arrays.toString(throwable.getStackTrace()) : null;

        PramanaReporter.logTestResult(
            testCaseId, testName, status, duration, errorMessage, stackTrace
        );
    }
}
```

#### Step 3: Use the Extension

```java
package com.yourproject.tests;

import com.yourproject.extensions.PramanaExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PramanaExtension.class)
public class LoginTest {

    @Test
    public void testValidLogin() {
        // Your test code
    }

    @Test
    public void testInvalidLogin() {
        // Your test code
    }
}
```

---

### Python (pytest)

#### Step 1: Install Required Libraries

```bash
pip install requests pytest
```

#### Step 2: Create Pramana Plugin (`conftest.py`)

```python
# conftest.py

import pytest
import requests
import datetime
import traceback

BASE_URL = "http://localhost:9090/api/v1"
suite_id = None

def pytest_configure(config):
    """Called before test execution starts"""
    global suite_id

    suite_name = config.option.verbose and "Test Suite" or "pytest Suite"
    environment = config.getoption("--env", default="staging")

    response = requests.post(
        f"{BASE_URL}/suites",
        json={
            "name": suite_name,
            "environment": environment,
            "tags": ["automated", "pytest"]
        }
    )

    if response.status_code == 201:
        suite_id = response.json().get("id")
        print(f"\n✅ Pramana Suite Created: {suite_id}")
    else:
        print(f"\n❌ Failed to create suite: {response.text}")

def pytest_runtest_logreport(report):
    """Called after each test phase"""
    global suite_id

    if report.when == "call":  # Only log during test execution phase
        test_case_id = report.nodeid
        test_name = report.location[2]

        status = "passed" if report.passed else "failed" if report.failed else "skipped"
        duration = int(report.duration * 1000)  # Convert to milliseconds

        error_message = None
        stack_trace = None

        if report.failed:
            error_message = str(report.longrepr)
            stack_trace = "\n".join(traceback.format_tb(report.longrepr))

        try:
            response = requests.post(
                f"{BASE_URL}/suites/{suite_id}/tests",
                json={
                    "testCaseId": test_case_id,
                    "testName": test_name,
                    "status": status,
                    "duration": duration,
                    "startTime": datetime.datetime.now().isoformat(),
                    "errorMessage": error_message,
                    "stackTrace": stack_trace
                }
            )

            if response.status_code == 201:
                print(f"✅ Test logged: {test_name} [{status}]")
            else:
                print(f"❌ Failed to log test: {response.text}")

        except Exception as e:
            print(f"❌ Error logging to Pramana: {str(e)}")

def pytest_sessionfinish(session, exitstatus):
    """Called after all tests finish"""
    global suite_id

    if suite_id:
        try:
            response = requests.put(f"{BASE_URL}/suites/{suite_id}/complete")
            if response.status_code == 200:
                print("\n✅ Suite marked as complete")
        except Exception as e:
            print(f"\n❌ Error completing suite: {str(e)}")

def pytest_addoption(parser):
    """Add custom command line options"""
    parser.addoption("--env", action="store", default="staging",
                    help="Environment to run tests against")
```

#### Step 3: Run Your Tests

```bash
# Run tests with Pramana reporting
pytest tests/ --env=staging

# Run with verbose output
pytest tests/ -v --env=production
```

---

### JavaScript/TypeScript (Mocha)

#### Step 1: Install Dependencies

```bash
npm install mocha axios
```

#### Step 2: Create Pramana Reporter

```javascript
// pramana-reporter.js

const axios = require('axios');

const BASE_URL = 'http://localhost:9090/api/v1';
let suiteId = null;

class PramanaReporter {

    static async createSuite(name, environment, tags) {
        try {
            const response = await axios.post(`${BASE_URL}/suites`, {
                name: name,
                environment: environment,
                tags: tags
            });

            suiteId = response.data.id;
            console.log(`✅ Pramana Suite Created: ${suiteId}`);
            return suiteId;

        } catch (error) {
            console.error(`❌ Failed to create suite: ${error.message}`);
            return null;
        }
    }

    static async logTestResult(testCaseId, testName, status, duration, errorMessage) {
        if (!suiteId) {
            console.error('⚠️ No active suite. Call createSuite() first.');
            return;
        }

        try {
            const body = {
                testCaseId: testCaseId,
                testName: testName,
                status: status,
                duration: duration,
                startTime: new Date().toISOString()
            };

            if (errorMessage) {
                body.errorMessage = errorMessage;
            }

            await axios.post(`${BASE_URL}/suites/${suiteId}/tests`, body);
            console.log(`✅ Test logged: ${testName} [${status}]`);

        } catch (error) {
            console.error(`❌ Failed to log test: ${error.message}`);
        }
    }

    static async completeSuite() {
        if (!suiteId) return;

        try {
            await axios.put(`${BASE_URL}/suites/${suiteId}/complete`);
            console.log('✅ Suite marked as complete');
        } catch (error) {
            console.error(`❌ Failed to complete suite: ${error.message}`);
        }
    }
}

module.exports = PramanaReporter;
```

#### Step 3: Integrate with Mocha Hooks

```javascript
// test/hooks.js

const PramanaReporter = require('../pramana-reporter');

before(async function() {
    await PramanaReporter.createSuite(
        'Mocha Test Suite',
        process.env.ENV || 'staging',
        ['automated', 'mocha']
    );
});

afterEach(async function() {
    const test = this.currentTest;
    const status = test.state === 'passed' ? 'passed' : 'failed';
    const duration = test.duration;
    const errorMessage = test.err ? test.err.message : null;

    await PramanaReporter.logTestResult(
        test.title,
        test.fullTitle(),
        status,
        duration,
        errorMessage
    );
});

after(async function() {
    await PramanaReporter.completeSuite();
});
```

#### Step 4: Run Tests

```bash
# Run tests
mocha --require test/hooks.js test/*.spec.js

# With environment variable
ENV=production mocha --require test/hooks.js test/*.spec.js
```

---

### JavaScript/TypeScript (Jest)

#### Step 1: Install Dependencies

```bash
npm install jest axios
```

#### Step 2: Create Jest Reporter

```javascript
// pramana-jest-reporter.js

const axios = require('axios');

const BASE_URL = 'http://localhost:9090/api/v1';
let suiteId = null;

class PramanaJestReporter {

    async onRunStart() {
        try {
            const response = await axios.post(`${BASE_URL}/suites`, {
                name: 'Jest Test Suite',
                environment: process.env.ENV || 'staging',
                tags: ['automated', 'jest']
            });

            suiteId = response.data.id;
            console.log(`\n✅ Pramana Suite Created: ${suiteId}`);

        } catch (error) {
            console.error(`❌ Failed to create suite: ${error.message}`);
        }
    }

    async onTestResult(test, testResult) {
        if (!suiteId) return;

        for (const result of testResult.testResults) {
            const status = result.status === 'passed' ? 'passed' :
                          result.status === 'failed' ? 'failed' : 'skipped';

            const errorMessage = result.failureMessages.length > 0
                ? result.failureMessages.join('\n')
                : null;

            try {
                await axios.post(`${BASE_URL}/suites/${suiteId}/tests`, {
                    testCaseId: result.ancestorTitles.join(' > '),
                    testName: result.title,
                    status: status,
                    duration: result.duration,
                    startTime: new Date().toISOString(),
                    errorMessage: errorMessage
                });

            } catch (error) {
                console.error(`❌ Failed to log test: ${error.message}`);
            }
        }
    }

    async onRunComplete() {
        if (!suiteId) return;

        try {
            await axios.put(`${BASE_URL}/suites/${suiteId}/complete`);
            console.log('\n✅ Suite marked as complete');
        } catch (error) {
            console.error(`❌ Failed to complete suite: ${error.message}`);
        }
    }
}

module.exports = PramanaJestReporter;
```

#### Step 3: Configure Jest

**jest.config.js:**
```javascript
module.exports = {
    reporters: [
        'default',
        './pramana-jest-reporter.js'
    ]
};
```

#### Step 4: Run Tests

```bash
npm test
```

---

### Selenium (any language)

For Selenium, integrate Pramana in your existing test framework hooks (TestNG, JUnit, pytest, etc.) and add screenshot capture on failure:

**Java Example:**
```java
@AfterMethod
public void captureScreenshotOnFailure(ITestResult result) {
    if (result.getStatus() == ITestResult.FAILURE) {
        // Capture screenshot
        TakesScreenshot ts = (TakesScreenshot) driver;
        String screenshot = ts.getScreenshotAs(OutputType.BASE64);

        // Get test ID from Pramana response
        String testId = getLastTestId(); // You need to store this from logTestResult

        // Upload to Pramana
        uploadAttachment(testId, "screenshot", "failure.png", screenshot);
    }
}

private void uploadAttachment(String testId, String type, String name, String content) {
    // Use HTTP client to POST to /api/v1/tests/{testId}/attachments
}
```

---

### Playwright

**TypeScript Example:**

```typescript
// playwright.config.ts

import { PlaywrightTestConfig } from '@playwright/test';
import PramanaReporter from './pramana-reporter';

const config: PlaywrightTestConfig = {
    reporter: [
        ['list'],
        ['./pramana-reporter.ts']
    ]
};

export default config;
```

**Custom Reporter:**
```typescript
// pramana-reporter.ts

import { Reporter, TestCase, TestResult } from '@playwright/test/reporter';
import axios from 'axios';

class PramanaReporter implements Reporter {
    private suiteId: string | null = null;

    async onBegin() {
        const response = await axios.post('http://localhost:9090/api/v1/suites', {
            name: 'Playwright Tests',
            environment: process.env.ENV || 'staging',
            tags: ['automated', 'playwright']
        });

        this.suiteId = response.data.id;
        console.log(`✅ Suite Created: ${this.suiteId}`);
    }

    async onTestEnd(test: TestCase, result: TestResult) {
        if (!this.suiteId) return;

        const status = result.status === 'passed' ? 'passed' :
                      result.status === 'failed' ? 'failed' : 'skipped';

        await axios.post(`http://localhost:9090/api/v1/suites/${this.suiteId}/tests`, {
            testCaseId: test.id,
            testName: test.title,
            status: status,
            duration: result.duration,
            startTime: new Date().toISOString(),
            errorMessage: result.error?.message
        });
    }

    async onEnd() {
        if (this.suiteId) {
            await axios.put(`http://localhost:9090/api/v1/suites/${this.suiteId}/complete`);
            console.log('✅ Suite Complete');
        }
    }
}

export default PramanaReporter;
```

---

### Cypress

**cypress.config.js:**
```javascript
const axios = require('axios');

let suiteId = null;

module.exports = {
    e2e: {
        setupNodeEvents(on, config) {

            on('before:run', async () => {
                const response = await axios.post('http://localhost:9090/api/v1/suites', {
                    name: 'Cypress Tests',
                    environment: config.env.environment || 'staging',
                    tags: ['automated', 'cypress']
                });

                suiteId = response.data.id;
                console.log(`✅ Suite Created: ${suiteId}`);
            });

            on('after:spec', async (spec, results) => {
                if (!suiteId) return;

                for (const test of results.tests) {
                    const status = test.state === 'passed' ? 'passed' : 'failed';

                    await axios.post(`http://localhost:9090/api/v1/suites/${suiteId}/tests`, {
                        testCaseId: test.title[0],
                        testName: test.title.join(' > '),
                        status: status,
                        duration: test.duration,
                        startTime: new Date().toISOString()
                    });
                }
            });

            on('after:run', async () => {
                if (suiteId) {
                    await axios.put(`http://localhost:9090/api/v1/suites/${suiteId}/complete`);
                    console.log('✅ Suite Complete');
                }
            });
        }
    }
};
```

---

## Best Practices

### 1. Error Handling

Always wrap Pramana API calls in try-catch blocks to prevent test failures if reporting fails:

```java
try {
    PramanaReporter.logTestResult(...);
} catch (Exception e) {
    // Log error but don't fail the test
    System.err.println("Failed to report to Pramana: " + e.getMessage());
}
```

### 2. Suite Management

- Create **one suite per test execution run**
- Use meaningful suite names (e.g., "Login Tests - Chrome - Staging")
- Include environment and browser info in suite metadata

### 3. Test Case IDs

- Use **consistent, unique test case IDs** across runs
- This enables flakiness detection and test history
- Example: `TC_LOGIN_001`, `TC_CHECKOUT_005`

### 4. Attachments

- Only attach screenshots for **failed tests** to save space
- Compress images before Base64 encoding
- Consider attachment size limits (< 5MB recommended)

### 5. Async Reporting (Optional)

For faster test execution, log results asynchronously:

**Java Example:**
```java
CompletableFuture.runAsync(() -> {
    PramanaReporter.logTestResult(...);
});
```

### 6. Configuration

Store Pramana URL and settings in environment variables:

```bash
export PRAMANA_URL=http://localhost:9090
export PRAMANA_ENVIRONMENT=staging
```

---

## Troubleshooting

### Issue: "Connection Refused" Error

**Cause:** Pramana server is not running

**Solution:**
```bash
# Check if Pramana is running
curl http://localhost:9090/api/v1/suites

# Start Pramana if not running
java -jar pramana-1.0.0.jar
```

### Issue: "Suite not found" Error

**Cause:** Suite ID is incorrect or suite was deleted

**Solution:**
- Verify you're using the correct suite ID from the create response
- Check suite exists: `curl http://localhost:9090/api/v1/suites/{suiteId}`

### Issue: Tests not appearing in dashboard

**Cause:** Tests logged before suite was created

**Solution:**
- Ensure `createSuite()` is called **before** any tests run
- Use framework hooks: `@BeforeSuite`, `pytest_configure`, `before()`, etc.

### Issue: Duplicate test entries

**Cause:** Multiple listeners/reporters enabled

**Solution:**
- Ensure Pramana listener is only registered once
- Remove duplicate listener configurations

### Issue: Screenshots too large

**Cause:** Uncompressed images

**Solution:**
```java
// Compress image before Base64 encoding
BufferedImage img = ImageIO.read(new File("screenshot.png"));
ImageIO.write(img, "jpg", outputStream); // Use JPEG with compression
```

---

## Need Help?

- **Documentation:** [README.md](README.md)
- **API Reference:** http://localhost:9090/swagger-ui (if enabled)
- **GitHub Issues:** [Report a bug](https://github.com/yourusername/pramana/issues)
- **Email:** support@pramana.io

---

**Happy Testing! 🚀**