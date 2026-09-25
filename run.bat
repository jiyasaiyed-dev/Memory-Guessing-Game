@echo off
javac -d out src\com\memorygame\Main.java src\com\memorygame\enums\*.java src\com\memorygame\model\*.java src\com\memorygame\memory\*.java src\com\memorygame\repository\*.java src\com\memorygame\ui\*.java
java -cp out com.memorygame.Main
pause
