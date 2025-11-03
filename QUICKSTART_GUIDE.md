# Pramana Test Reporter - Quick Start Guide

> Get up and running with Pramana in 5 minutes

---

## ⏱️ 5-Minute Setup

### Step 1: Prerequisites (30 seconds)

Check if you have Java 17 or higher:

```bash
java -version
```

✅ If you see version 17 or higher, you're good to go!
❌ If not, [download Java here](https://www.oracle.com/java/technologies/downloads/)

---

### Step 2: Get Pramana (1 minute)

**Option A: Download JAR** (recommended)
```bash
# Download from google drive
https://drive.google.com/drive/folders/15tdsQqCfk60VDtSuLfiEWS0LNCdum8XG?usp=sharing
```

---

### Step 3: Start Pramana (30 seconds)

```bash
java -jar pramana-1.0.0.jar
```

**Wait for this message:**
```
✅ Pramana Test Reporter started successfully!
   Access Dashboard: http://localhost:9090
   API Base URL: http://localhost:9090/api/v1
```

---

### Step 4: Verify Dashboard (30 seconds)

Open your browser:
```
http://localhost:9090
```

You should see the Pramana dashboard with:
- Empty suite list
- "Create Suite" button
- Statistics showing 0/0/0/0

✅ **Pramana is running!**

---

### Step 5: Create Your First Suite (1 minute)

**Option A: Using Dashboard**

1. Click **"Create Suite"** button
2. Enter suite name: `My First Test Suite`
3. Enter environment: `staging`
4. Click **"Create Suite"**

**Option B: Using API (curl)**

```bash
curl -X POST http://localhost:9090/api/v1/suites \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My First Test Suite",
    "environment": "staging",
    "tags": ["demo", "quickstart"]
  }'
```

**Response:**
```json
{
  "id": "suite_abc123xyz",
  "name": "My First Test Suite",
  "environment": "staging",
  "status": "running",
  "startTime": "2025-11-01T10:00:00Z",
  "totalTests": 0,
  "passedCount": 0,
  "failedCount": 0
}
```

**💾 Save the `id` value** - you'll need it for the next steps!

---

### Step 6: Log Your First Test (1 minute)

Replace `{SUITE_ID}` with the ID from the previous response:

```bash
curl -X POST http://localhost:9090/api/v1/suites/{SUITE_ID}/tests \
  -H "Content-Type: application/json" \
  -d '{
    "testCaseId": "TC001",
    "testName": "My first test - Login with valid credentials",
    "status": "passed",
    "duration": 2500,
    "startTime": "2025-11-01T10:01:00Z",
    "endTime": "2025-11-01T10:01:02Z"
  }'
```

**Log a few more tests:**

```bash
# Failed test
curl -X POST http://localhost:9090/api/v1/suites/{SUITE_ID}/tests \
  -H "Content-Type: application/json" \
  -d '{
    "testCaseId": "TC002",
    "testName": "Login with invalid password",
    "status": "failed",
    "duration": 1200,
    "startTime": "2025-11-01T10:02:00Z",
    "endTime": "2025-11-01T10:02:01Z",
    "errorMessage": "Login failed: Invalid credentials",
    "stackTrace": "AssertionError: Expected login success but got error page"
  }'

# Skipped test
curl -X POST http://localhost:9090/api/v1/suites/{SUITE_ID}/tests \
  -H "Content-Type: application/json" \
  -d '{
    "testCaseId": "TC003",
    "testName": "Login with special characters",
    "status": "skipped",
    "startTime": "2025-11-01T10:03:00Z"
  }'
```

---

### Step 7: View Your Report (30 seconds)

**Option A: Dashboard**

1. Refresh http://localhost:9090
2. Click **"View Report"** on your suite
3. See your test results with pass/fail status

**Option B: API**

```bash
# Get suite summary
curl http://localhost:9090/api/v1/suites/{SUITE_ID}

# Get all tests
curl http://localhost:9090/api/v1/suites/{SUITE_ID}/tests?onlyLatest=true
```

---

### Step 8: Mark Suite Complete (30 seconds)

```bash
curl -X PUT http://localhost:9090/api/v1/suites/{SUITE_ID}/complete
```

Check the dashboard - suite status should now be **"COMPLETED"**!

---

## 🎉 Congratulations!

You've successfully:
✅ Started Pramana
✅ Created a test suite
✅ Logged test results (passed, failed, skipped)
✅ Viewed the report
✅ Completed the suite

---

## 🚀 What's Next?

### 1. Integrate with Your Test Framework

See **[INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)** for step-by-step integration with:
- Java (TestNG, JUnit 5)
- Python (pytest)
- JavaScript/TypeScript (Mocha, Jest, Playwright, Cypress)
- And more...

### 2. Explore Advanced Features

```bash
# Test flakiness detection - run the same test twice with different results
curl -X POST http://localhost:9090/api/v1/suites/{SUITE_ID}/tests \
  -d '{"testCaseId": "TC004", "testName": "Flaky test", "status": "failed", ...}'

curl -X POST http://localhost:9090/api/v1/suites/{SUITE_ID}/tests \
  -d '{"testCaseId": "TC004", "testName": "Flaky test", "status": "passed", ...}'

# Get test history (flakiness analysis)
curl http://localhost:9090/api/v1/tests/case/TC004/history
```

### 3. Add Screenshots/Logs

```bash
# First, get the test ID from the response when you logged the test
curl -X POST http://localhost:9090/api/v1/tests/{TEST_ID}/attachments \
  -H "Content-Type: application/json" \
  -d '{
    "type": "screenshot",
    "name": "login-failure.png",
    "content": "iVBORw0KGgoAAAANSUhEUg...(base64 encoded image)",
    "description": "Screenshot of login failure"
  }'
```

### 4. Learn the Complete API

See **[API_REFERENCE.md](API_REFERENCE.md)** for full API documentation.

---

## 📋 Common Commands Reference

```bash
# Start Pramana (default port 9090)
java -jar pramana-1.0.0.jar

# Start on custom port
java -jar pramana-1.0.0.jar --server.port=8080

# Custom database location
java -jar pramana-1.0.0.jar --pramana.db.path=/custom/path/pramana.db

# View all suites
curl http://localhost:9090/api/v1/suites

# View specific suite
curl http://localhost:9090/api/v1/suites/{SUITE_ID}

# Delete suite (and all its tests)
curl -X DELETE http://localhost:9090/api/v1/suites/{SUITE_ID}
```

---

## 🐛 Troubleshooting

### "Address already in use" error

Port 9090 is taken. Use a different port:
```bash
java -jar pramana-1.0.0.jar --server.port=8080
```

### Can't access dashboard

Check if Pramana is running:
```bash
curl http://localhost:9090/api/v1/suites
```

If you get "Connection refused", Pramana is not running.

### Database errors

Delete the database and restart:
```bash
rm pramana.db
java -jar pramana-1.0.0.jar
```

### Need help?

- 📘 [Full Documentation](README.md)
- 🔧 [Integration Guide](INTEGRATION_GUIDE.md)
- 📚 [API Reference](API_REFERENCE.md)
- 🐛 [Report Issues](https://github.com/yourusername/pramana/issues)

---

## 💡 Pro Tips

1. **Keep Pramana running** - Start it once and use it for all test executions
2. **Create one suite per test run** - Don't reuse suite IDs
3. **Use meaningful test case IDs** - Enables flakiness detection across runs
4. **Add screenshots only for failures** - Saves database space
5. **Complete suites when done** - Calculates final statistics

---

## 🎯 Example: Full Test Execution Flow

```bash
# 1. Start Pramana
java -jar pramana-1.0.0.jar

# 2. Create suite before tests run
RESPONSE=$(curl -s -X POST http://localhost:9090/api/v1/suites \
  -H "Content-Type: application/json" \
  -d '{"name": "Nightly Tests", "environment": "production"}')

SUITE_ID=$(echo $RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo "Suite ID: $SUITE_ID"

# 3. Run your tests (your framework calls Pramana API)
# ... tests execute ...

# 4. Complete suite after all tests finish
curl -X PUT http://localhost:9090/api/v1/suites/$SUITE_ID/complete

# 5. View report
echo "View report: http://localhost:9090/report.html?suiteId=$SUITE_ID"
```

---

## 🔄 Quick Reset

Want to start fresh?

```bash
# Stop Pramana (Ctrl+C)
# Delete database
rm pramana.db

# Restart
java -jar pramana-1.0.0.jar
```

All suites and tests will be cleared.

---

**Ready to integrate with your real tests?**

👉 **Next: [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)**

---

**Happy Testing! 🚀**