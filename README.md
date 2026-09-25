# Memory Guessing Game (Java Swing GUI)

Pure Java, zero external dependencies (no Maven/Gradle/JavaFX/XML).

## Compile
```
javac -d out src\com\memorygame\Main.java src\com\memorygame\enums\*.java src\com\memorygame\model\*.java src\com\memorygame\memory\*.java src\com\memorygame\repository\*.java src\com\memorygame\ui\*.java
```

## Run
```
java -cp out com.memorygame.Main
```

Or just double-click `run.bat` on Windows.

High scores/statistics/settings are saved under `data/` next to wherever you run it from.
