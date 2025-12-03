package com.key.clipboarduse.main;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles native keyboard events for clipboard navigation.
 * Monitors key combinations:
 * - Ctrl+C: Captures clipboard content
 * - Win+Shift: Cycles through clipboard history
 * - Win+Shift+E: Exits application
 */
public class KeyboardEventHandler implements NativeKeyListener {
    private static final Logger logger = LoggerFactory.getLogger(KeyboardEventHandler.class);
    
    private final ClipboardWindow window;
    private final ClipboardManager clipboardManager;
    
    private boolean isWindowKeyHeld = false;
    private boolean isCtrlKeyHeld = false;
    private boolean isWinShiftHeld = false;
    private int historyIndex = 0;
    
    public KeyboardEventHandler(ClipboardWindow window, ClipboardManager clipboardManager) {
        this.window = window;
        this.clipboardManager = clipboardManager;
    }
    
    @Override
    public void nativeKeyPressed(NativeKeyEvent event) {
        int keyCode = event.getKeyCode();
        
        switch (keyCode) {
            case NativeKeyEvent.VC_META:
            case NativeKeyEvent.VC_META_L:
            case NativeKeyEvent.VC_META_R:
                isWindowKeyHeld = true;
                break;
                
            case NativeKeyEvent.VC_CONTROL:
            case NativeKeyEvent.VC_CONTROL_L:
            case NativeKeyEvent.VC_CONTROL_R:
                isCtrlKeyHeld = true;
                break;
                
            case NativeKeyEvent.VC_SHIFT:
            case NativeKeyEvent.VC_SHIFT_L:
            case NativeKeyEvent.VC_SHIFT_R:
                if (isWindowKeyHeld) {
                    handleShiftWithWin();
                }
                break;
                
            case NativeKeyEvent.VC_E:
                if (isWinShiftHeld) {
                    logger.info("Exit command received (Win+Shift+E)");
                    window.setVisible(false);
                    System.exit(0);
                }
                break;
        }
    }
    
    @Override
    public void nativeKeyReleased(NativeKeyEvent event) {
        int keyCode = event.getKeyCode();
        
        switch (keyCode) {
            case NativeKeyEvent.VC_META:
            case NativeKeyEvent.VC_META_L:
            case NativeKeyEvent.VC_META_R:
                if (isWindowKeyHeld) {
                    handleWindowKeyRelease();
                }
                isWindowKeyHeld = false;
                break;
                
            case NativeKeyEvent.VC_CONTROL:
            case NativeKeyEvent.VC_CONTROL_L:
            case NativeKeyEvent.VC_CONTROL_R:
                isCtrlKeyHeld = false;
                break;
                
            case NativeKeyEvent.VC_SHIFT:
            case NativeKeyEvent.VC_SHIFT_L:
            case NativeKeyEvent.VC_SHIFT_R:
                if (isWindowKeyHeld) {
                    isWinShiftHeld = true;
                }
                break;
                
            case NativeKeyEvent.VC_C:
                if (isCtrlKeyHeld) {
                    handleCtrlC();
                }
                break;
        }
    }
    
    @Override
    public void nativeKeyTyped(NativeKeyEvent event) {
        // Not used
    }
    
    /**
     * Handles the Shift key press while Windows key is held.
     * Cycles through clipboard history.
     */
    private void handleShiftWithWin() {
        int historySize = clipboardManager.getHistorySize();
        
        if (historySize == 0) {
            logger.debug("No clipboard history available");
            return;
        }
        
        window.setVisible(false);
        
        // Increment index, wrapping around if necessary
        if (historySize - historyIndex <= 0) {
            historyIndex = 1;
        } else {
            historyIndex++;
        }
        
        // Display the selected history item
        String content = clipboardManager.getHistoryItem(historyIndex);
        window.displayText(content);
        window.setVisible(true);
        
        logger.debug("Showing clipboard history item {} of {}", historyIndex, historySize);
    }
    
    /**
     * Handles the Windows key release.
     * Sets the selected history item to the system clipboard.
     */
    private void handleWindowKeyRelease() {
        isWindowKeyHeld = false;
        window.setVisible(false);
        
        if (historyIndex > 0) {
            String content = clipboardManager.getHistoryItem(historyIndex);
            clipboardManager.setSystemClipboard(content);
            logger.debug("Set clipboard to history item {}", historyIndex);
        }
        
        historyIndex = 0;
    }
    
    /**
     * Handles Ctrl+C key combination.
     * Captures current clipboard content to history.
     */
    private void handleCtrlC() {
        // Small delay to allow clipboard to be populated by the system
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted while waiting for clipboard", e);
        }
        clipboardManager.captureClipboard();
    }
}
