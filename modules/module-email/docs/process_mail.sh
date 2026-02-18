#!/bin/bash

# Configuration
# =============

# The folder to watch for files
WATCH_FOLDER="<folder to watch>"
# The full URL to the linux-mail endpoint
ENDPOINT_URL="https://<url>/rest/email/linux-mail"
# The authentication token (Bearer or Key)
# e.g. "Bearer your_token_here" or "Key your_api_key_here"
AUTH_TOKEN="Key <key>"

# Script Logic
# ============

# Check if folder exists
if [ ! -d "$WATCH_FOLDER" ]; then
    echo "Error: Watch folder '$WATCH_FOLDER' does not exist."
    exit 1
fi

# Iterate over all files in the folder
# We use a glob that ignores directories
for file in "$WATCH_FOLDER"/*; do
    # Check if there are no files matching the glob
    [ -e "$file" ] || continue
    # Skip directories
    [ -f "$file" ] || continue

    echo "Processing file: $file"

    # Call the REST endpoint
    # -X POST: Use POST method
    # -H: Add headers
    # --data-binary: Upload file content as-is
    # -w "%{http_code}": Output only the HTTP status code
    # -o /dev/null: Discard the response body
    # -s: Silent mode
    response_code=$(curl -s -o /dev/null -w "%{http_code}" \
        -X POST \
        -H "Authorization: $AUTH_TOKEN" \
        -H "Content-Type: application/octet-stream" \
        --data-binary @"$file" \
        "$ENDPOINT_URL")

    if [ "$response_code" -eq 202 ]; then
        echo "Successfully processed. Deleting file."
        rm "$file"
    else
        echo "Failed to process file. HTTP Response Code: $response_code"
        echo "Script terminating."
        exit 2
    fi
done

echo "Done."
