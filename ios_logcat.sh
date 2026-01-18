#!/bin/bash

# 1. Find a booted simulator
DEVICE_ID=$(xcrun simctl list devices available | grep "(Booted)" | head -n 1 | grep -oE "[A-F0-9-]{36}")

if [ -z "$DEVICE_ID" ]; then
    echo "Error: No booted simulator found. Please start a simulator first."
    exit 1
fi

echo "Using Simulator: $DEVICE_ID"

# 2. Identify the process name
# Based on your previous logs, the process name appears to be 'swiftApp'
PROCESS_NAME="swiftApp"

echo "Streaming logs for process: $PROCESS_NAME ..."
echo "Press Ctrl+C to stop."

# 3. Stream logs
# We use 'log stream' via simctl spawn. 
# Filter by process name to keep it clean.
xcrun simctl spawn "$DEVICE_ID" log stream --level debug --predicate "process == '$PROCESS_NAME'"
