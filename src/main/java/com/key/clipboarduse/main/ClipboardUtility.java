package com.key.clipboarduse.main;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingUtilities;
import java.util.logging.Level;

/**
 * Main entry point for the Clipboard Utility application.
 * This application monitors clipboard changes and allows cycling through clipboard history
 * using keyboard shortcuts (Win+Shift).
 */
public class ClipboardUtility {
    private static final Logger logger = LoggerFactory.getLogger(ClipboardUtility.class);

    public static void main(String[] args) {
        // Suppress JNativeHook's verbose logging
        java.util.logging.Logger jnativeLogger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
        jnativeLogger.setLevel(Level.WARNING);
        jnativeLogger.setUseParentHandlers(false);

        logger.info("Starting Clipboard Utility application");

        SwingUtilities.invokeLater(() -> {
            try {
                ClipboardWindow window = new ClipboardWindow();
                window.initialize();
                logger.info("Clipboard Utility initialized successfully");
            } catch (NativeHookException e) {
                logger.error("Failed to initialize native hook. The application requires native keyboard/mouse access.", e);
                System.exit(1);
            }
        });
    }
}
