package com.key.clipboarduse.main;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Main window for displaying clipboard history.
 * Shows when user navigates through clipboard history with Win+Shift.
 */
public class ClipboardWindow extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(ClipboardWindow.class);
    
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 300;
    private static final String WINDOW_TITLE = "Choose Your Clip to Paste or Press SHIFT for next Clip.";
    
    private final JTextArea textArea;
    private final ClipboardManager clipboardManager;
    private KeyboardEventHandler keyboardEventHandler;
    
    public ClipboardWindow() {
        this.clipboardManager = new ClipboardManager();
        this.textArea = new JTextArea();
        setupWindow();
    }
    
    /**
     * Sets up the window UI components and properties.
     */
    private void setupWindow() {
        setTitle(WINDOW_TITLE);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setAlwaysOnTop(true);
        setResizable(false);
        centerWindow();
        
        // Configure text area
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        // Add text area with scroll pane
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(WINDOW_WIDTH - 50, WINDOW_HEIGHT - 50));
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Centers the window on the screen.
     */
    private void centerWindow() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - WINDOW_WIDTH) / 2;
        int y = (screenSize.height - WINDOW_HEIGHT) / 2;
        setLocation(x, y);
    }
    
    /**
     * Initializes native keyboard/mouse hooks and event handlers.
     * @throws NativeHookException if native hook registration fails
     */
    public void initialize() throws NativeHookException {
        try {
            // Register native hook
            GlobalScreen.registerNativeHook();
            logger.info("Native hook registered successfully");
            
            // Create and register event handler
            keyboardEventHandler = new KeyboardEventHandler(this, clipboardManager);
            GlobalScreen.addNativeKeyListener(keyboardEventHandler);
            
            logger.info("Keyboard event handler registered");
            logger.info("Clipboard Utility is now monitoring clipboard changes");
            logger.info("Press Ctrl+C to capture clipboard, Win+Shift to cycle through history");
            logger.info("Press Win+Shift+E to exit");
            
        } catch (NativeHookException e) {
            logger.error("Failed to register native hook", e);
            throw e;
        }
    }
    
    /**
     * Displays text in the window's text area.
     * @param text the text to display
     */
    public void displayText(String text) {
        textArea.setText(text);
        textArea.setCaretPosition(0); // Scroll to top
    }
    
    /**
     * Cleans up resources before window closes.
     */
    @Override
    public void dispose() {
        try {
            GlobalScreen.unregisterNativeHook();
            logger.info("Native hook unregistered");
        } catch (NativeHookException e) {
            logger.error("Failed to unregister native hook", e);
        }
        super.dispose();
    }
}
