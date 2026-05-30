@echo off

title Console

:1
cls

echo Compiling Java files...
javac Music\FgbMusicEp61_Fr\FgbMusicEp61.java
javac -cp . FaceGameBat_3DJ_1_5.java
echo Running application...
java -cp . FaceGameBat_3DJ_1_5

pause >nul