#!/bin/zsh
#
# Console.sh — macOS/Linux starter for FaceGameBat 3DJ 1.5
# (zsh equivalent of "Run Console.bat")
#
# Compiles the Java sources and launches the app. Always runs from the
# repository root so the relative image / icon / MP3 paths resolve correctly.

set -e

# Move to the directory this script lives in (= repository root).
cd "${0:A:h}"

echo "Compiling Java files..."
javac Music/FgbMusicEp61_Fr/FgbMusicEp61.java
javac -cp . FaceGameBat_3DJ_1_5.java

echo "Running application..."
java -cp . FaceGameBat_3DJ_1_5
