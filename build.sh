#!/bin/bash
# Build script for 8man Expert Council Simulator
# No external dependencies required - uses only Java standard library

set -e

echo "=== Building 8man Expert Council Simulator ==="

# Clean
rm -rf build/

# Compile
mkdir -p build/classes
find src/main/java -name "*.java" > /tmp/8man-sources.txt
javac -d build/classes @/tmp/8man-sources.txt
echo "Compiled Java sources."

# Copy static resources
cp -r src/main/resources/static build/classes/
echo "Copied static resources."

# Create JAR
mkdir -p build/jar
cd build/classes
jar cfe ../jar/8man.jar com.eightman.App .
cd ../..
echo "Created build/jar/8man.jar"

echo ""
echo "=== Build complete ==="
echo "Run with: java -jar build/jar/8man.jar"
echo ""
echo "Environment variables:"
echo "  EIGHTMAN_API_KEY       - Your OpenAI-compatible API key (required)"
echo "  EIGHTMAN_API_BASE_URL  - API base URL (default: https://api.openai.com/v1)"
echo "  EIGHTMAN_DEFAULT_MODEL - Default model (default: gpt-4o)"
echo "  EIGHTMAN_PORT          - Server port (default: 8080)"
