#!/bin/bash

# Configuration
WORKSPACE="swiftApp/swiftApp.xcworkspace"
SCHEME="swiftApp"
SIM_NAME="iPhone 17" # Default fallback

# 1. Find a booted simulator or boot one
DEVICE_ID=$(xcrun simctl list devices available | grep "(Booted)" | head -n 1 | grep -oE "[A-F0-9-]{36}")

if [ -z "$DEVICE_ID" ]; then
    echo "No booted simulator found. Attempting to boot $SIM_NAME..."
    # Find UDID for SIM_NAME (checking available devices)
    DEVICE_ID=$(xcrun simctl list devices available | grep "$SIM_NAME" | head -n 1 | grep -oE "[A-F0-9-]{36}")
    
    if [ -z "$DEVICE_ID" ]; then
        echo "Error: Simulator '$SIM_NAME' not found."
        echo "Available devices:"
        xcrun simctl list devices available
        exit 1
    fi
    
    echo "Booting $SIM_NAME ($DEVICE_ID)..."
    xcrun simctl boot "$DEVICE_ID"
    
    # Wait a moment for boot to stabilize (optional but helpful)
    sleep 5
fi

echo "Using Simulator: $DEVICE_ID"

# 2. Build the app
echo "Building $SCHEME..."
# We use a derived data path to easily locate the built .app later
DERIVED_DATA_PATH="build/ios_derived_data"

xcodebuild -workspace "$WORKSPACE" \
    -scheme "$SCHEME" \
    -configuration Debug \
    -destination "platform=iOS Simulator,id=$DEVICE_ID" \
    -derivedDataPath "$DERIVED_DATA_PATH" \
    build

if [ $? -ne 0 ]; then
    echo "Build failed."
    exit 1
fi

# 3. Locate the .app
# The path depends on the scheme and configuration. 
# Typically: Build/Products/Debug-iphonesimulator/AppName.app
APP_PATH=$(find "$DERIVED_DATA_PATH/Build/Products/Debug-iphonesimulator" -name "*.app" | head -n 1)

if [ -z "$APP_PATH" ]; then
    echo "Error: App bundle not found in $DERIVED_DATA_PATH"
    exit 1
fi

echo "Found app at: $APP_PATH"

# 4. Extract Bundle Identifier
# Using 'PlistBuddy' to read Info.plist inside the app bundle
BUNDLE_ID=$(/usr/libexec/PlistBuddy -c "Print CFBundleIdentifier" "$APP_PATH/Info.plist")

if [ -z "$BUNDLE_ID" ]; then
    echo "Error: Could not extract Bundle Identifier from $APP_PATH/Info.plist"
    exit 1
fi

echo "Bundle ID: $BUNDLE_ID"

# 5. Install and Launch
echo "Installing..."
xcrun simctl install "$DEVICE_ID" "$APP_PATH"

echo "Launching..."
xcrun simctl launch "$DEVICE_ID" "$BUNDLE_ID"

echo "Done."