#!/bin/bash
# Test-Suite für 8man Expert Council Simulator
set -e

echo "=== Kompiliere Hauptquellen ==="
mkdir -p build/classes
find src/main/java -name "*.java" > /tmp/8man-sources.txt
javac -d build/classes @/tmp/8man-sources.txt 2>&1

echo "=== Kompiliere Tests ==="
mkdir -p build/test-classes
find src/test/java -name "*.java" > /tmp/8man-test-sources.txt
javac -cp build/classes -d build/test-classes @/tmp/8man-test-sources.txt 2>&1

echo "=== Starte Tests ==="
echo ""
java -cp build/classes:build/test-classes com.eightman.TestRunner
