# Commit validation update for LEARN-002

In `scripts/validate-commit.sh`, after learning taxonomy seed, add:

```bash
local activities_status
activities_status="$(
  curl -sS -o /tmp/boom-validate-seed-activities-body.txt -w "%{http_code}" \
    -b "$COOKIE_FILE" \
    -X POST "$BACKEND_BASE_URL/api/v1/dev/seed/learning-activities" || true
)"

if [[ "$activities_status" == "200" ]]; then
  echo "Learning activities seed OK."
elif [[ "$activities_status" == "404" ]]; then
  echo "Learning activities seed endpoint not present yet. Skipping."
else
  echo "Learning activities seed returned status $activities_status. Body:"
  cat /tmp/boom-validate-seed-activities-body.txt || true
  exit 1
fi
```

In API smoke checks, add:

```bash
local activities_list_status
activities_list_status="$(
  curl -sS -o /tmp/boom-validate-learning-activities-body.txt -w "%{http_code}" \
    -b "$COOKIE_FILE" \
    "$BACKEND_BASE_URL/api/v1/learning/activities" || true
)"

if [[ "$activities_list_status" == "200" ]]; then
  echo "learning/activities OK."
elif [[ "$activities_list_status" == "404" ]]; then
  echo "learning/activities endpoint not present yet. Skipping."
else
  echo "learning/activities failed with status: $activities_list_status"
  cat /tmp/boom-validate-learning-activities-body.txt || true
  exit 1
fi
```
