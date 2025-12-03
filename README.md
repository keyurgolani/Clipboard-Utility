# Clipboard Utility

A clipboard monitoring utility with keyboard shortcut navigation for clipboard history. This application runs in the background and allows you to cycle through previously copied text items.

## Features

- **Clipboard History**: Automatically captures text copied to the clipboard (Ctrl+C)
- **Quick Navigation**: Use Win+Shift keyboard shortcut to cycle through clipboard history
- **Native Event Monitoring**: Uses JNativeHook for system-wide keyboard event monitoring
- **Modern Architecture**: Built with Java 11+ and modern logging framework (SLF4J + Logback)
- **Lightweight**: Runs in the background with minimal resource usage

## How It Works

The application monitors your clipboard activity and maintains a history of copied text items:

1. **Copy text** using Ctrl+C - the text is automatically captured to history
2. **Navigate history** by holding Win key and pressing Shift repeatedly
3. **Select an item** by releasing the Win key - the selected item is copied to your clipboard
4. **Exit** the application using Win+Shift+E

## Requirements

- **Java**: JDK 11 or higher
- **Maven**: 3.6+ (for building from source)
- **Operating System**: Windows, macOS, or Linux with native hook support

## Installation

### Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/keyurgolani/Clipboard-Utility.git
   cd Clipboard-Utility
   ```

2. Build the project using Maven:
   ```bash
   mvn clean package
   ```

3. The executable JAR will be created at `target/clipboard-utility.jar`

## Running the Application

### Run with Maven

```bash
mvn exec:java -Dexec.mainClass="com.key.clipboarduse.main.ClipboardUtility"
```

### Run the JAR directly

```bash
java -jar target/clipboard-utility.jar
```

### Run in Background (Linux/macOS)

```bash
nohup java -jar target/clipboard-utility.jar &
```

### Run in Background (Windows)

```batch
start javaw -jar target/clipboard-utility.jar
```

## Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl+C` | Capture current clipboard content to history |
| `Win+Shift` (hold and press Shift) | Cycle through clipboard history |
| `Win` (release) | Select current history item and copy to clipboard |
| `Win+Shift+E` | Exit the application |

## Dependencies

The application uses the following libraries:

- **JNativeHook 2.2.2**: For native keyboard and mouse event monitoring
- **SLF4J 2.0.9**: Logging facade API
- **Logback 1.4.11**: Logging implementation

All dependencies are managed through Maven and will be automatically downloaded during the build process.

## Project Structure

```
Clipboard-Utility/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/key/clipboarduse/main/
│       │       ├── ClipboardUtility.java      # Main application entry point
│       │       ├── ClipboardWindow.java       # UI window component
│       │       ├── ClipboardManager.java      # Clipboard history manager
│       │       └── KeyboardEventHandler.java  # Keyboard event handling
│       └── resources/
│           └── logback.xml                    # Logging configuration
├── pom.xml                                    # Maven build configuration
├── README.md                                  # This file
└── .gitignore                                 # Git ignore rules
```

## Configuration

### Logging

The application uses Logback for logging. Configuration can be modified in `src/main/resources/logback.xml`.

By default:
- Console output: INFO level and above
- File output: All logs written to `clipboard-utility.log`
- JNativeHook logs: WARN level only (to reduce verbosity)

To enable debug logging, change the root logger level in `logback.xml`:
```xml
<root level="DEBUG">
```

## Troubleshooting

### Application doesn't capture keyboard events

**Solution**: The application requires system-level permissions to monitor keyboard events. On some systems, you may need to:

- **macOS**: Grant accessibility permissions in System Preferences > Security & Privacy > Accessibility
- **Linux**: May require running with appropriate permissions or adding user to input group
- **Windows**: Usually works without additional configuration

### "Failed to register native hook" error

**Solution**: 
1. Ensure no other instance of the application is running
2. Check that your system supports native hooks
3. Try running with administrator/sudo privileges (not recommended for regular use)

### Clipboard history not working

**Solution**:
1. Ensure you're using Ctrl+C to copy (not right-click copy in some applications)
2. Check the log file (`clipboard-utility.log`) for errors
3. Verify that clipboard contains text (not images or other data types)

## Development

### Building

```bash
# Clean and compile
mvn clean compile

# Run tests (if available)
mvn test

# Package into JAR
mvn package

# Clean all build artifacts
mvn clean
```

### Code Style

The project follows modern Java best practices:
- Java 11+ features and syntax
- Proper exception handling with logging
- Separation of concerns (UI, business logic, event handling)
- Comprehensive documentation

## Performance Notes

- The application maintains clipboard history in memory
- Memory usage grows with the number of clipboard items captured
- Consider restarting the application periodically for very long-running sessions
- Only text clipboard content is captured (images and other formats are ignored)

## Known Limitations

- Only captures text content from clipboard (no images, files, etc.)
- Clipboard history is not persisted between application restarts
- The preview window shows text only in plain format

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

## Author

Keyur Golani

## Version History

- **2.0.0** (2024): Major modernization
  - Migrated to Maven build system
  - Updated to Java 11+
  - Added SLF4J + Logback logging
  - Refactored code for better organization
  - Updated JNativeHook to latest version (2.2.2)
  
- **1.0.0**: Initial release
