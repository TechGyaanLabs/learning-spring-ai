#!/bin/bash

# Budget Speech API Test Script
# This script demonstrates how to test the Budget Speech API endpoints

BASE_URL="http://localhost:8081/api/v1/budget-speech"

echo "=== Budget Speech API Test Script ==="
echo "Base URL: $BASE_URL"
echo ""

# Function to make HTTP requests and display results
make_request() {
    local method=$1
    local endpoint=$2
    local data=$3
    
    echo "--- $method $endpoint ---"
    if [ -n "$data" ]; then
        curl -s -X $method "$BASE_URL$endpoint" -H "Content-Type: application/json" -d "$data" | jq .
    else
        curl -s -X $method "$BASE_URL$endpoint" | jq .
    fi
    echo ""
}

# Test 1: Health Check
echo "1. Testing Health Check..."
make_request "GET" "/health"

# Test 2: Get Document Count (before loading)
echo "2. Getting Document Count (before loading)..."
make_request "GET" "/count"

# Test 3: Load Documents
echo "3. Loading Budget Speech Documents..."
make_request "POST" "/load"

# Test 4: Get Document Count (after loading)
echo "4. Getting Document Count (after loading)..."
make_request "GET" "/count"

# Test 5: Get Sample Queries
echo "5. Getting Sample Queries..."
make_request "GET" "/sample-queries"

# Test 5.5: Get Chunk Statistics
echo "5.5. Getting Chunk Statistics..."
make_request "GET" "/chunk-stats"

# Test 6: Search Documents (with ChatClient)
echo "6. Searching for 'budget allocation' with ChatClient..."
make_request "GET" "/search?query=budget%20allocation&topK=3&useChatClient=true"

# Test 7: Search Documents (without ChatClient)
echo "7. Searching for 'financial planning' without ChatClient..."
make_request "GET" "/search?query=financial%20planning&topK=3&useChatClient=false"

# Test 8: Chat with Documents
echo "8. Chatting about 'healthcare funding'..."
make_request "POST" "/chat?query=What%20are%20the%20main%20budget%20allocations%20for%20healthcare?&topK=5"

# Test 9: Chat with Documents
echo "9. Chatting about 'infrastructure development'..."
make_request "POST" "/chat?query=How%20much%20is%20allocated%20for%20infrastructure%20development?&topK=3"

echo "=== Test Complete ==="
echo ""
echo "Manual Testing Commands:"
echo "curl -X POST $BASE_URL/load"
echo "curl \"$BASE_URL/search?query=budget%20allocation&topK=5&useChatClient=true\""
echo "curl -X POST \"$BASE_URL/chat?query=What%20are%20the%20main%20budget%20allocations?&topK=5\""
echo "curl $BASE_URL/count"
echo "curl $BASE_URL/health"
echo "curl $BASE_URL/sample-queries"
echo "curl $BASE_URL/chunk-stats"
echo "curl -X DELETE $BASE_URL/clear"
echo "curl -X POST $BASE_URL/reload"
