#!/usr/bin/env bash
set -euo pipefail
BACKEND_BASE_URL="${BACKEND_BASE_URL:-http://localhost:8080}"
COOKIE_FILE="${COOKIE_FILE:-/tmp/boom-cookies.txt}"
curl -sS -i -c "$COOKIE_FILE" -X POST "$BACKEND_BASE_URL/api/v1/auth/login" -H "Content-Type: application/json" -d '{"identifier":"daniel.test","password":"BoomTest123!"}' >/tmp/boom-login-response.txt
curl -sS -i -b "$COOKIE_FILE" -X POST "$BACKEND_BASE_URL/api/v1/dev/seed/learning-taxonomy"
echo; curl -sS -b "$COOKIE_FILE" "$BACKEND_BASE_URL/api/v1/learning/subjects"
echo; curl -sS -b "$COOKIE_FILE" "$BACKEND_BASE_URL/api/v1/learning/curriculum/frameworks"
echo
