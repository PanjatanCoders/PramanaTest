# Pramana Test Reporter - API Reference

> Complete REST API documentation for Pramana Test Reporter

---

## 📋 Table of Contents

- [Overview](#overview)
- [Base URL](#base-url)
- [Authentication](#authentication)
- [Response Formats](#response-formats)
- [Error Handling](#error-handling)
- [Suite Management](#suite-management)
- [Test Management](#test-management)
- [Test Steps Management](#test-steps-management)
- [Attachment Management](#attachment-management)
- [Statistics & Analytics](#statistics--analytics)

---

## Overview

Pramana exposes a RESTful API for managing test suites, logging test results, and retrieving reports.

**Key Features:**
- JSON request/response format
- Standard HTTP methods (GET, POST, PUT, DELETE)
- RESTful URL structure
- CORS enabled for cross-origin requests

---

## Base URL

```
http://localhost:9090/api/v1
```

**Custom Port:**
```bash
# Start Pramana on custom port
java -jar pramana-1.0.0.jar --server.port=8080

# Base URL becomes:
http://localhost:8080/api/v1
```

---

## Authentication

**Current Version:** No authentication required

**Future Versions:** May include API keys or token-based auth

---

## Response Formats

### Success Response

**HTTP Status:** `200 OK` or `201 Created`

```json
{
  "id": "suite_abc123",
  "name": "Test Suite",
  "status": "running",
  "startTime": "2025-11-01T10:00:00Z"
}
```

### Error Response

**HTTP Status:** `4xx` or `5xx`

```json
{
  "timestamp": "2025-11-01T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Suite name is required",
  "path": "/api/v1/suites"
}
```

---

## Error Handling

### HTTP Status Codes

| Code | Meaning | Description |
|------|---------|-------------|
| `200` | OK | Request successful |
| `201` | Created | Resource created successfully |
| `400` | Bad Request | Invalid request parameters |
| `404` | Not Found | Resource not found |
| `500` | Internal Server Error | Server error occurred |

### Common Error Scenarios

**Missing Required Field:**
```bash
curl -X POST http://localhost:9090/api/v1/suites -d '{}'
# Response: 400 - "Suite name is required"
```

**Resource Not Found:**
```bash
curl http://localhost:9090/api/v1/suites/invalid_id
# Response: 404 - "Suite not found"
```

---

## Suite Management

### Create Suite

Create a new test suite. Call this **before** running your tests.

**Endpoint:** `POST /api/v1/suites`

**Request Body:**
```json
{
  "name": "Login Test Suite",          // Required
  "environment": "staging",             // Optional
  "tags": ["smoke", "regression"],      // Optional
  "metadata": {                         // Optional
    "browser": "chrome",
    "os": "windows"
  }
}
```

**Response:** `201 Created`
```json
{
  "id": "suite_abc123xyz",
  "name": "Login Test Suite",
  "environment": "staging",
  "tags": ["smoke", "regression"],
  "status": "running",
  "startTime": "2025-11-01T10:00:00Z",
  "endTime": null,
  "totalTests": 0,
  "passedCount": 0,
  "failedCount": 0,
  "skippedCount": 0,
  "passRate": 0.0,
  "createdAt": "2025-11-01T10:00:00Z",
  "updatedAt": "2025-11-01T10:00:00Z"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:9090/api/v1/suites \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Login Test Suite",
    "environment": "staging",
    "tags": ["smoke", "regression"]
  }'
```

---

### List All Suites

Retrieve all test suites with optional filtering and pagination.

**Endpoint:** `GET /api/v1/suites`

**Query Parameters:**

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `status` | string | - | Filter by status: `running`, `completed`, `failed` |
| `limit` | integer | 50 | Number of results per page |
| `offset` | integer | 0 | Number of results to skip |

**Response:** `200 OK`
```json
{
  "suites": [
    {
      "id": "suite_123",
      "name": "Login Tests",
      "environment": "staging",
      "status": "completed",
      "totalTests": 10,
      "passedCount": 8,
      "failedCount": 2,
      "startTime": "2025-11-01T10:00:00Z",
      "endTime": "2025-11-01T10:05:00Z"
    }
  ],
  "total": 1,
  "limit": 50,
  "offset": 0
}
```

**cURL Examples:**
```bash
# Get all suites
curl http://localhost:9090/api/v1/suites

# Get only running suites
curl http://localhost:9090/api/v1/suites?status=running

# Get with pagination
curl http://localhost:9090/api/v1/suites?limit=10&offset=0
```

---

### Get Suite by ID

Retrieve detailed information about a specific suite.

**Endpoint:** `GET /api/v1/suites/{id}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string | Suite ID |

**Response:** `200 OK`
```json
{
  "id": "suite_abc123",
  "name": "Login Test Suite",
  "environment": "staging",
  "tags": ["smoke", "regression"],
  "status": "completed",
  "startTime": "2025-11-01T10:00:00Z",
  "endTime": "2025-11-01T10:05:00Z",
  "totalTests": 10,
  "passedCount": 8,
  "failedCount": 1,
  "skippedCount": 1,
  "passRate": 80.0,
  "durationMs": 300000,
  "createdAt": "2025-11-01T10:00:00Z",
  "updatedAt": "2025-11-01T10:05:00Z"
}
```

**cURL Example:**
```bash
curl http://localhost:9090/api/v1/suites/suite_abc123
```

---

### Update Suite

Update suite metadata (name, environment, tags).

**Endpoint:** `PUT /api/v1/suites/{id}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string | Suite ID |

**Request Body:**
```json
{
  "name": "Updated Suite Name",      // Optional
  "environment": "production",        // Optional
  "tags": ["smoke", "critical"]       // Optional
}
```

**Response:** `200 OK`
```json
{
  "id": "suite_abc123",
  "name": "Updated Suite Name",
  "environment": "production",
  "tags": ["smoke", "critical"],
  "status": "running",
  "updatedAt": "2025-11-01T10:10:00Z"
}
```

**cURL Example:**
```bash
curl -X PUT http://localhost:9090/api/v1/suites/suite_abc123 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Suite Name",
    "environment": "production"
  }'
```

---

### Mark Suite as Complete

Mark suite as completed. This sets `status = completed`, `endTime = now`, and calculates final statistics.

**Endpoint:** `PUT /api/v1/suites/{id}/complete`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string | Suite ID |

**Response:** `200 OK`
```json
{
  "id": "suite_abc123",
  "status": "completed",
  "endTime": "2025-11-01T10:05:00Z",
  "totalTests": 10,
  "passedCount": 8,
  "failedCount": 2,
  "passRate": 80.0,
  "durationMs": 300000
}
```

**cURL Example:**
```bash
curl -X PUT http://localhost:9090/api/v1/suites/suite_abc123/complete
```

---

### Mark Suite as Failed

Mark suite as failed with optional reason.

**Endpoint:** `PUT /api/v1/suites/{id}/fail`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string | Suite ID |

**Request Body:** (Optional)
```json
{
  "reason": "Test execution interrupted"
}
```

**Response:** `200 OK`
```json
{
  "id": "suite_abc123",
  "status": "failed",
  "endTime": "2025-11-01T10:03:00Z"
}
```

**cURL Example:**
```bash
curl -X PUT http://localhost:9090/api/v1/suites/suite_abc123/fail \
  -H "Content-Type: application/json" \
  -d '{"reason": "Test execution interrupted"}'
```

---

### Delete Suite

Delete suite and **all associated tests, steps, and attachments** (cascade delete).

**Endpoint:** `DELETE /api/v1/suites/{id}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string | Suite ID |

**Response:** `200 OK`
```json
{
  "message": "Suite deleted successfully",
  "id": "suite_abc123"
}
```

**cURL Example:**
```bash
curl -X DELETE http://localhost:9090/api/v1/suites/suite_abc123
```

**⚠️ Warning:** This operation is **irreversible** and deletes all test data!

---

### Get Suite Summary

Get aggregated statistics for a suite.

**Endpoint:** `GET /api/v1/suites/{id}/summary`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string | Suite ID |

**Response:** `200 OK`
```json
{
  "suiteId": "suite_abc123",
  "name": "Login Test Suite",
  "totalTests": 10,
  "passedCount": 8,
  "failedCount": 1,
  "skippedCount": 1,
  "flakyCount": 2,
  "passRate": 80.0,
  "averageDuration": 2500,
  "totalDuration": 25000
}
```

**cURL Example:**
```bash
curl http://localhost:9090/api/v1/suites/suite_abc123/summary
```

---

## Test Management

### Log Test Result

Log a test result. Supports **smart rerun logic** - if a test with the same `testCaseId` already exists in the suite, it creates a new run.

**Endpoint:** `POST /api/v1/suites/{suiteId}/tests`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `suiteId` | string | Suite ID |

**Request Body:**
```json
{
  "testCaseId": "TC001",                          // Required - Unique test identifier
  "testName": "Login with valid credentials",     // Required
  "status": "passed",                             // Required: "passed", "failed", "skipped"
  "duration": 2500,                               // Optional - Duration in milliseconds
  "startTime": "2025-11-01T10:01:00Z",           // Required - ISO 8601 format
  "endTime": "2025-11-01T10:01:02Z",             // Optional - ISO 8601 format
  "errorMessage": "Login failed",                 // Optional - Only for failed tests
  "stackTrace": "at LoginPage.login(...)"         // Optional - Stack trace for failures
}
```

**Response:** `201 Created`
```json
{
  "id": "test_xyz789",
  "suiteId": "suite_abc123",
  "testCaseId": "TC001",
  "testName": "Login with valid credentials",
  "status": "passed",
  "duration": 2500,
  "startTime": "2025-11-01T10:01:00Z",
  "endTime": "2025-11-01T10:01:02Z",
  "errorMessage": null,
  "stackTrace": null,
  "runNumber": 1,
  "isLatest": true,
  "isFlaky": false,
  "createdAt": "2025-11-01T10:01:02Z"
}
```

**cURL Example:**
```bash
# Passed test
curl -X POST http://localhost:9090/api/v1/suites/suite_abc123/tests \
  -H "Content-Type: application/json" \
  -d '{
    "testCaseId": "TC001",
    "testName": "Login with valid credentials",
    "status": "passed",
    "duration": 2500,
    "startTime": "2025-11-01T10:01:00Z",
    "endTime": "2025-11-01T10:01:02Z"
  }'

# Failed test with error details
curl -X POST http://localhost:9090/api/v1/suites/suite_abc123/tests \
  -H "Content-Type: application/json" \
  -d '{
    "testCaseId": "TC002",
    "testName": "Login with invalid password",
    "status": "failed",
    "duration": 1200,
    "startTime": "2025-11-01T10:02:00Z",
    "endTime": "2025-11-01T10:02:01Z",
    "errorMessage": "Expected login success but got error",
    "stackTrace": "AssertionError at line 45..."
  }'
```

**Smart Rerun Logic:**

When you log the same `testCaseId` multiple times:
- `runNumber` increments (1, 2, 3...)
- Previous runs marked as `isLatest = false`
- New run marked as `isLatest = true`
- If previous status ≠ current status → `isFlaky = true`

**Example - Flaky Test Detection:**
```bash
# Run 1: Failed
curl -X POST .../tests -d '{"testCaseId": "TC003", "status": "failed", ...}'
# Response: runNumber=1, isLatest=true, isFlaky=false

# Run 2: Passed (rerun)
curl -X POST .../tests -d '{"testCaseId": "TC003", "status": "passed", ...}'
# Response: runNumber=2, isLatest=true, isFlaky=true (detected flakiness!)
```

---

### Get Tests for Suite

Retrieve all tests for a specific suite.

**Endpoint:** `GET /api/v1/suites/{suiteId}/tests`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `suiteId` | string | Suite ID |

**Query Parameters:**

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `onlyLatest` | boolean | true | If true, return only latest run of each test |

**Response:** `200 OK`
```json
{
  "tests": [
    {
      "id": "test_xyz789",
      "suiteId": "suite_abc123",
      "testCaseId": "TC001",
      "testName": "Login with valid credentials",
      "status": "passed",
      "duration": 2500,
      "runNumber": 1,
      "isLatest": true,
      "isFlaky": false,
      "createdAt": "2025-11-01T10:01:02Z"
    }
  ],
  "total": 1,
  "suiteId": "suite_abc123"
}
```

**cURL Examples:**
```bash
# Get only latest test runs (default)
curl http://localhost:9090/api/v1/suites/suite_abc123/tests

# Get all test runs (including reruns)
curl http://localhost:9090/api/v1/suites/suite_abc123/tests?onlyLatest=false
```

---

### Get Test by ID

Retrieve detailed information about a specific test.

**Endpoint:** `GET /api/v1/tests/{id}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string | Test ID |

**Response:** `200 OK`
```json
{
  "id": "test_xyz789",
  "suiteId": "suite_abc123",
  "testCaseId": "TC001",
  "testName": "Login with valid credentials",
  "status": "passed",
  "startTime": "2025-11-01T10:01:00Z",
  "endTime": "2025-11-01T10:01:02Z",
  "duration": 2500,
  "errorMessage": null,
  "stackTrace": null,
  "runNumber": 1,
  "isLatest": true,
  "isFlaky": false,
  "createdAt": "2025-11-01T10:01:02Z"
}
```

**cURL Example:**
```bash
curl http://localhost:9090/api/v1/tests/test_xyz789
```

---

### Get Test History (Flakiness Analysis)

Get aggregated history for a test case across **all suites and runs**.

**Endpoint:** `GET /api/v1/tests/case/{testCaseId}/history`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `testCaseId` | string | Test case ID (e.g., TC001) |

**Response:** `200 OK`
```json
{
  "testCaseId": "TC001",
  "testName": "Login with valid credentials",
  "totalRuns": 10,
  "passedCount": 9,
  "failedCount": 1,
  "skippedCount": 0,
  "passRate": 90.0,
  "flakinessLevel": "LOW",
  "isFlaky": true,
  "lastRunTime": "2025-11-01T10:01:02Z"
}
```

**Flakiness Levels:**

| Pass Rate | Flakiness Level |
|-----------|----------------|
| 100% | `STABLE` |
| 80-99% | `LOW` |
| 50-79% | `MEDIUM` |
| < 50% | `HIGH` |

**cURL Example:**
```bash
curl http://localhost:9090/api/v1/tests/case/TC001/history
```

---

### Get All Runs of a Test Case

Get all individual runs of a specific test case (across all suites).

**Endpoint:** `GET /api/v1/tests/case/{testCaseId}/runs`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `testCaseId` | string | Test case ID |

**Response:** `200 OK`
```json
{
  "tests": [
    {
      "id": "test_123",
      "suiteId": "suite_abc",
      "testCaseId": "TC001",
      "status": "failed",
      "runNumber": 1,
      "createdAt": "2025-11-01T09:00:00Z"
    },
    {
      "id": "test_124",
      "suiteId": "suite_abc",
      "testCaseId": "TC001",
      "status": "passed",
      "runNumber": 2,
      "createdAt": "2025-11-01T09:01:00Z"
    }
  ],
  "total": 2,
  "testCaseId": "TC001"
}
```

**cURL Example:**
```bash
curl http://localhost:9090/api/v1/tests/case/TC001/runs
```

---

### Update Test

Update test error message or stack trace.

**Endpoint:** `PUT /api/v1/tests/{id}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string | Test ID |

**Request Body:**
```json
{
  "errorMessage": "Updated error message",
  "stackTrace": "Updated stack trace"
}
```

**Response:** `200 OK`
```json
{
  "id": "test_xyz789",
  "errorMessage": "Updated error message",
  "stackTrace": "Updated stack trace",
  "updatedAt": "2025-11-01T10:10:00Z"
}
```

**cURL Example:**
```bash
curl -X PUT http://localhost:9090/api/v1/tests/test_xyz789 \
  -H "Content-Type: application/json" \
  -d '{
    "errorMessage": "Updated error message",
    "stackTrace": "Updated stack trace"
  }'
```

---

### Delete Test

Delete a specific test.

**Endpoint:** `DELETE /api/v1/tests/{id}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string | Test ID |

**Response:** `200 OK`
```json
{
  "message": "Test deleted successfully",
  "id": "test_xyz789"
}
```

**cURL Example:**
```bash
curl -X DELETE http://localhost:9090/api/v1/tests/test_xyz789
```

---

## Test Steps Management

> **New in v1.1.6** - Add granular step-by-step execution tracking with screenshots for enhanced debugging

### Add Test Step

Add a step to a test for granular reporting. Steps appear in the test details modal in chronological order.

**Endpoint:** `POST /api/v1/steps`

**Request Body:**
```json
{
  "testId": "test_xyz789",                    // Required - Parent test ID
  "stepNumber": 1,                            // Required - Step sequence number
  "description": "Navigate to login page",    // Required - Step description
  "status": "passed",                         // Required: "passed", "failed", "skipped"
  "duration": 1500,                           // Optional - Duration in milliseconds
  "errorMessage": "Element not found"         // Optional - Error details for failed steps
}
```

**Response:** `201 Created`
```json
{
  "id": "step_abc123",
  "testId": "test_xyz789",
  "stepNumber": 1,
  "description": "Navigate to login page",
  "status": "passed",
  "timestamp": "2025-11-01T10:01:00Z",
  "duration": 1500,
  "errorMessage": null,
  "createdAt": "2025-11-01T10:01:01Z"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:9090/api/v1/steps \
  -H "Content-Type: application/json" \
  -d '{
    "testId": "test_xyz789",
    "stepNumber": 1,
    "description": "Navigate to login page",
    "status": "passed",
    "duration": 1500
  }'
```

**Complete Test Workflow with Steps:**
```bash
# 1. Log the test
TEST_RESPONSE=$(curl -s -X POST http://localhost:9090/api/v1/suites/suite_123/tests \
  -H "Content-Type": application/json" \
  -d '{
    "testCaseId": "TC001",
    "testName": "User Login Test",
    "status": "passed",
    "duration": 5000
  }')

TEST_ID=$(echo $TEST_RESPONSE | jq -r '.id')

# 2. Add step 1
STEP1=$(curl -s -X POST http://localhost:9090/api/v1/steps \
  -H "Content-Type: application/json" \
  -d '{
    "testId": "'$TEST_ID'",
    "stepNumber": 1,
    "description": "Navigate to login page",
    "status": "passed",
    "duration": 1200
  }')

STEP1_ID=$(echo $STEP1 | jq -r '.id')

# 3. Add screenshot to step 1
curl -s -X POST http://localhost:9090/api/v1/tests/$TEST_ID/attachments \
  -H "Content-Type: application/json" \
  -d '{
    "stepId": "'$STEP1_ID'",
    "type": "screenshot",
    "name": "step1-login-page.png",
    "content": "'"$(base64 -w 0 screenshot1.png)"'",
    "timestamp": "'$(date -u +%Y-%m-%dT%H:%M:%SZ)'"
  }'

# 4. Add step 2
STEP2=$(curl -s -X POST http://localhost:9090/api/v1/steps \
  -H "Content-Type: application/json" \
  -d '{
    "testId": "'$TEST_ID'",
    "stepNumber": 2,
    "description": "Enter credentials",
    "status": "passed",
    "duration": 800
  }')

# 5. Add step 3 (failed)
STEP3=$(curl -s -X POST http://localhost:9090/api/v1/steps \
  -H "Content-Type: application/json" \
  -d '{
    "testId": "'$TEST_ID'",
    "stepNumber": 3,
    "description": "Click login button",
    "status": "failed",
    "duration": 500,
    "errorMessage": "Login button not clickable"
  }')

STEP3_ID=$(echo $STEP3 | jq -r '.id')

# 6. Add screenshot to failed step
curl -s -X POST http://localhost:9090/api/v1/tests/$TEST_ID/attachments \
  -H "Content-Type: application/json" \
  -d '{
    "stepId": "'$STEP3_ID'",
    "type": "screenshot",
    "name": "step3-login-error.png",
    "content": "'"$(base64 -w 0 screenshot_error.png)"'",
    "timestamp": "'$(date -u +%Y-%m-%dT%H:%M:%SZ)'"
  }'
```

---

### Get Steps for Test

Retrieve all steps for a specific test, ordered by step number.

**Endpoint:** `GET /api/v1/steps/test/{testId}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `testId` | string | Test ID |

**Response:** `200 OK`
```json
[
  {
    "id": "step_abc123",
    "testId": "test_xyz789",
    "stepNumber": 1,
    "description": "Navigate to login page",
    "status": "passed",
    "timestamp": "2025-11-01T10:01:00Z",
    "duration": 1500,
    "errorMessage": null,
    "createdAt": "2025-11-01T10:01:01Z"
  },
  {
    "id": "step_def456",
    "testId": "test_xyz789",
    "stepNumber": 2,
    "description": "Enter credentials",
    "status": "passed",
    "timestamp": "2025-11-01T10:01:02Z",
    "duration": 800,
    "errorMessage": null,
    "createdAt": "2025-11-01T10:01:03Z"
  },
  {
    "id": "step_ghi789",
    "testId": "test_xyz789",
    "stepNumber": 3,
    "description": "Click login button",
    "status": "failed",
    "timestamp": "2025-11-01T10:01:03Z",
    "duration": 500,
    "errorMessage": "Login button not clickable",
    "createdAt": "2025-11-01T10:01:04Z"
  }
]
```

**cURL Example:**
```bash
curl http://localhost:9090/api/v1/steps/test/test_xyz789
```

---

### Get Step by ID

Retrieve detailed information about a specific step.

**Endpoint:** `GET /api/v1/steps/{id}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string | Step ID |

**Response:** `200 OK`
```json
{
  "id": "step_abc123",
  "testId": "test_xyz789",
  "stepNumber": 1,
  "description": "Navigate to login page",
  "status": "passed",
  "timestamp": "2025-11-01T10:01:00Z",
  "duration": 1500,
  "errorMessage": null,
  "createdAt": "2025-11-01T10:01:01Z"
}
```

**cURL Example:**
```bash
curl http://localhost:9090/api/v1/steps/step_abc123
```

---

### Get Attachments for Step

Retrieve all attachments (screenshots) for a specific step.

**Endpoint:** `GET /api/v1/tests/{testId}/steps/{stepId}/attachments`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `testId` | string | Test ID |
| `stepId` | string | Step ID |

**Response:** `200 OK`
```json
[
  {
    "id": "attachment_abc123",
    "testId": "test_xyz789",
    "stepId": "step_abc123",
    "type": "screenshot",
    "name": "step1-login-page.png",
    "content": "iVBORw0KGgoAAAANSUhEUg...",
    "description": null,
    "timestamp": "2025-11-01T10:01:00Z",
    "contentSize": 45678,
    "createdAt": "2025-11-01T10:01:01Z"
  }
]
```

**cURL Example:**
```bash
curl http://localhost:9090/api/v1/tests/test_xyz789/steps/step_abc123/attachments
```

---

### UI Display

Steps with screenshots are automatically displayed in the **test details modal** on the dashboard:

1. Click "Details" on any test in the suite report
2. Scroll to the "Test Steps" section
3. Each step shows:
   - Step number and status badge (color-coded)
   - Description
   - Duration
   - Error message (if failed)
   - "📷 Screenshots" button
4. Click "📷 Screenshots" to view/hide step screenshots
5. Click any screenshot for fullscreen lightbox view

**Features:**
- ✅ Color-coded step cards (green/red/gray)
- ✅ Expandable screenshots per step
- ✅ Fullscreen image viewer with ESC to close
- ✅ Chronological step order
- ✅ Visual debugging with screenshots

---

## Attachment Management

### Add Attachment

Add screenshot, log, or video to a test.

**Endpoint:** `POST /api/v1/tests/{testId}/attachments`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `testId` | string | Test ID |

**Request Body:**
```json
{
  "type": "screenshot",                          // Required: "screenshot", "video", "log", "other"
  "name": "login-failure.png",                   // Required
  "content": "iVBORw0KGgoAAAANSUhEUg...",          // Required - Base64 encoded
  "description": "Screenshot of login failure",   // Optional
  "timestamp": "2025-11-01T10:01:02Z"            // Required - ISO 8601 format
}
```

**Response:** `201 Created`
```json
{
  "id": "attachment_abc123",
  "testId": "test_xyz789",
  "type": "screenshot",
  "name": "login-failure.png",
  "description": "Screenshot of login failure",
  "timestamp": "2025-11-01T10:01:02Z",
  "contentSize": 45678,
  "createdAt": "2025-11-01T10:01:03Z"
}
```

**cURL Example:**
```bash
# Screenshot attachment
curl -X POST http://localhost:9090/api/v1/tests/test_xyz789/attachments \
  -H "Content-Type: application/json" \
  -d '{
    "type": "screenshot",
    "name": "login-failure.png",
    "content": "iVBORw0KGgoAAAANSUhEUgAA...",
    "description": "Screenshot of login failure",
    "timestamp": "2025-11-01T10:01:02Z"
  }'

# Log attachment
curl -X POST http://localhost:9090/api/v1/tests/test_xyz789/attachments \
  -H "Content-Type: application/json" \
  -d '{
    "type": "log",
    "name": "browser-console.log",
    "content": "W0VSUk9SXSBGYWlsZWQgdG8gbG9hZA==",
    "description": "Browser console log",
    "timestamp": "2025-11-01T10:01:02Z"
  }'
```

**Base64 Encoding:**

```bash
# Linux/Mac - Encode file to Base64
base64 -w 0 screenshot.png

# Python
import base64
with open("screenshot.png", "rb") as f:
    encoded = base64.b64encode(f.read()).decode()

# JavaScript
const fs = require('fs');
const buffer = fs.readFileSync('screenshot.png');
const base64 = buffer.toString('base64');

# Java
byte[] fileContent = Files.readAllBytes(Paths.get("screenshot.png"));
String encoded = Base64.getEncoder().encodeToString(fileContent);
```

---

### Get Attachments for Test

Retrieve all attachments for a specific test.

**Endpoint:** `GET /api/v1/tests/{testId}/attachments`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `testId` | string | Test ID |

**Response:** `200 OK`
```json
{
  "attachments": [
    {
      "id": "attachment_abc123",
      "testId": "test_xyz789",
      "type": "screenshot",
      "name": "login-failure.png",
      "description": "Screenshot of login failure",
      "timestamp": "2025-11-01T10:01:02Z",
      "contentSize": 45678,
      "content": "iVBORw0KGgoAAAANSUhEUg..."
    }
  ],
  "total": 1,
  "testId": "test_xyz789"
}
```

**cURL Example:**
```bash
curl http://localhost:9090/api/v1/tests/test_xyz789/attachments
```

---

### Delete Attachment

Delete a specific attachment.

**Endpoint:** `DELETE /api/v1/attachments/{id}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string | Attachment ID |

**Response:** `200 OK`
```json
{
  "message": "Attachment deleted successfully",
  "id": "attachment_abc123"
}
```

**cURL Example:**
```bash
curl -X DELETE http://localhost:9090/api/v1/attachments/attachment_abc123
```

---

## Statistics & Analytics

### Get Test Statistics

Get aggregated statistics for all tests in a suite.

**Endpoint:** `GET /api/v1/suites/{suiteId}/tests/statistics`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `suiteId` | string | Suite ID |

**Response:** `200 OK`
```json
{
  "suiteId": "suite_abc123",
  "totalTests": 10,
  "passedCount": 8,
  "failedCount": 1,
  "skippedCount": 1,
  "flakyCount": 2,
  "passRate": 80.0,
  "averageDuration": 2500,
  "totalDuration": 25000,
  "fastestTest": {
    "id": "test_123",
    "testName": "Quick test",
    "duration": 500
  },
  "slowestTest": {
    "id": "test_456",
    "testName": "Slow test",
    "duration": 8000
  }
}
```

**cURL Example:**
```bash
curl http://localhost:9090/api/v1/suites/suite_abc123/tests/statistics
```

---

## Complete Workflow Example

Here's a complete test execution workflow using the API:

```bash
#!/bin/bash

# 1. Create suite
SUITE_RESPONSE=$(curl -s -X POST http://localhost:9090/api/v1/suites \
  -H "Content-Type: application/json" \
  -d '{
    "name": "E2E Test Suite",
    "environment": "staging",
    "tags": ["e2e", "nightly"]
  }')

SUITE_ID=$(echo $SUITE_RESPONSE | jq -r '.id')
echo "Suite created: $SUITE_ID"

# 2. Run tests and log results

# Test 1: Passed
curl -s -X POST http://localhost:9090/api/v1/suites/$SUITE_ID/tests \
  -H "Content-Type: application/json" \
  -d '{
    "testCaseId": "TC001",
    "testName": "User can login",
    "status": "passed",
    "duration": 2500,
    "startTime": "'$(date -u +%Y-%m-%dT%H:%M:%SZ)'"
  }'

# Test 2: Failed
TEST_RESPONSE=$(curl -s -X POST http://localhost:9090/api/v1/suites/$SUITE_ID/tests \
  -H "Content-Type: application/json" \
  -d '{
    "testCaseId": "TC002",
    "testName": "User can checkout",
    "status": "failed",
    "duration": 3200,
    "startTime": "'$(date -u +%Y-%m-%dT%H:%M:%SZ)'",
    "errorMessage": "Checkout button not clickable"
  }')

TEST_ID=$(echo $TEST_RESPONSE | jq -r '.id')

# 3. Add screenshot to failed test
curl -s -X POST http://localhost:9090/api/v1/tests/$TEST_ID/attachments \
  -H "Content-Type: application/json" \
  -d '{
    "type": "screenshot",
    "name": "checkout-error.png",
    "content": "'"$(base64 -w 0 screenshot.png)"'",
    "description": "Screenshot of checkout error",
    "timestamp": "'$(date -u +%Y-%m-%dT%H:%M:%SZ)'"
  }'

# 4. Complete suite
curl -s -X PUT http://localhost:9090/api/v1/suites/$SUITE_ID/complete

# 5. Get results
echo "View report: http://localhost:9090/report.html?suiteId=$SUITE_ID"
```

---

## Best Practices

### 1. Suite Lifecycle

```
Create Suite → Log Tests → Complete/Fail Suite
     ↓              ↓              ↓
  status:      status:         status:
  "running"    "running"     "completed"
```

### 2. Test Case IDs

Use **consistent, unique identifiers**:
- ✅ Good: `TC_LOGIN_001`, `TEST_CHECKOUT_05`
- ❌ Bad: `test1`, `myTest` (not unique across runs)

### 3. Error Handling

Always wrap API calls in try-catch:
```javascript
try {
  await logTestResult(...);
} catch (error) {
  console.error('Failed to log to Pramana:', error);
  // Don't fail the test if reporting fails
}
```

### 4. Attachments

- Only attach for **failed tests** to save space
- Compress images before Base64 encoding
- Keep attachments **< 5MB**

### 5. Timestamps

Always use **ISO 8601 format**:
```
2025-11-01T10:00:00Z
```

**Generate in your language:**
```bash
# Bash
date -u +%Y-%m-%dT%H:%M:%SZ

# Python
from datetime import datetime
datetime.utcnow().isoformat() + 'Z'

# JavaScript
new Date().toISOString()

# Java
Instant.now().toString()
```

---

## Rate Limits

**Current Version:** No rate limits

**Recommended:** Don't exceed 100 requests/second to avoid performance issues

---

## Versioning

**Current Version:** v1

API is versioned via URL path: `/api/v1/`

Future versions will be `/api/v2/`, etc.

---

## Support

- **Documentation:** [README.md](README.md)
- **Quick Start:** [QUICKSTART.md](QUICKSTART.md)
- **Integration Guide:** [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)
- **GitHub Issues:** [Report a bug](https://github.com/yourusername/pramana/issues)

---

**Happy Testing! 🚀**