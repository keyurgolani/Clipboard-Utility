package com.key.clipboarduse.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Stack;

/**
 * Manages clipboard history and operations.
 * Stores clipboard items in a stack for easy navigation through history.
 */
public class ClipboardManager {
    private static final Logger logger = LoggerFactory.getLogger(ClipboardManager.class);
    
    private final Stack<String> clipboardHistory;
    private final Clipboard systemClipboard;
    
    public ClipboardManager() {
        this.clipboardHistory = new Stack<>();
        this.systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        
        // Initialize with empty clipboard
        clearSystemClipboard();
    }
    
    /**
     * Adds the current clipboard content to history.
     */
    public void captureClipboard() {
        try {
            Object data = systemClipboard.getData(DataFlavor.stringFlavor);
            if (data instanceof String) {
                String content = (String) data;
                if (!content.isEmpty()) {
                    clipboardHistory.push(content);
                    logger.debug("Captured clipboard content: {} characters", content.length());
                }
            }
        } catch (UnsupportedFlavorException e) {
            logger.warn("Clipboard does not contain text data");
        } catch (IOException e) {
            logger.error("Failed to read clipboard content", e);
        }
    }
    
    /**
     * Retrieves a clipboard item from history at the specified offset from the end.
     * @param offsetFromEnd 1 for most recent, 2 for second most recent, etc.
     * @return the clipboard content, or empty string if not found
     */
    public String getHistoryItem(int offsetFromEnd) {
        int size = clipboardHistory.size();
        if (size == 0 || offsetFromEnd <= 0 || offsetFromEnd > size) {
            return "";
        }
        return clipboardHistory.get(size - offsetFromEnd);
    }
    
    /**
     * Sets the system clipboard to the specified content.
     * @param content the content to set
     */
    public void setSystemClipboard(String content) {
        try {
            StringSelection selection = new StringSelection(content);
            systemClipboard.setContents(selection, null);
            logger.debug("Set system clipboard: {} characters", content.length());
        } catch (Exception e) {
            logger.error("Failed to set system clipboard", e);
        }
    }
    
    /**
     * Returns the number of items in clipboard history.
     * @return the history size
     */
    public int getHistorySize() {
        return clipboardHistory.size();
    }
    
    /**
     * Clears the system clipboard.
     */
    private void clearSystemClipboard() {
        setSystemClipboard("");
    }
}
