package com.key.clipboarduse.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages clipboard history and operations.
 * Stores clipboard items in a list for easy navigation through history.
 */
public class ClipboardManager {
    private static final Logger logger = LoggerFactory.getLogger(ClipboardManager.class);
    private static final int MAX_HISTORY_SIZE = 100;
    
    private final List<String> clipboardHistory;
    private final Clipboard systemClipboard;
    
    public ClipboardManager() {
        this.clipboardHistory = new ArrayList<>();
        this.systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        
        // Initialize with empty clipboard
        clearSystemClipboard();
    }
    
    /**
     * Adds the current clipboard content to history.
     * Prevents duplicate consecutive entries.
     */
    public void captureClipboard() {
        try {
            if (!systemClipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                logger.debug("Clipboard does not contain text data");
                return;
            }
            
            Object data = systemClipboard.getData(DataFlavor.stringFlavor);
            if (data instanceof String) {
                String content = ((String) data).trim();
                if (!content.isEmpty() && !isDuplicate(content)) {
                    clipboardHistory.add(content);
                    trimHistoryIfNeeded();
                    logger.debug("Captured clipboard content: {} characters", content.length());
                } else if (content.isEmpty()) {
                    logger.debug("Skipped empty clipboard content");
                } else {
                    logger.debug("Skipped duplicate clipboard content");
                }
            }
        } catch (UnsupportedFlavorException e) {
            logger.debug("Clipboard does not contain text data");
        } catch (IOException e) {
            logger.error("Failed to read clipboard content", e);
        } catch (IllegalStateException e) {
            logger.warn("Clipboard is currently unavailable", e);
        }
    }
    
    /**
     * Checks if the content is a duplicate of the most recent history item.
     * @param content the content to check
     * @return true if it's a duplicate, false otherwise
     */
    private boolean isDuplicate(String content) {
        return !clipboardHistory.isEmpty() 
            && clipboardHistory.get(clipboardHistory.size() - 1).equals(content);
    }
    
    /**
     * Trims history if it exceeds the maximum size.
     */
    private void trimHistoryIfNeeded() {
        if (clipboardHistory.size() > MAX_HISTORY_SIZE) {
            clipboardHistory.remove(0);
            logger.debug("Trimmed clipboard history to {} items", MAX_HISTORY_SIZE);
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
        if (content == null || content.isEmpty()) {
            logger.debug("Skipping empty clipboard set operation");
            return;
        }
        
        try {
            StringSelection selection = new StringSelection(content);
            systemClipboard.setContents(selection, null);
            logger.debug("Set system clipboard: {} characters", content.length());
        } catch (IllegalStateException e) {
            logger.warn("Clipboard is currently unavailable", e);
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
     * Returns an unmodifiable view of the clipboard history.
     * @return the clipboard history
     */
    public List<String> getHistory() {
        return Collections.unmodifiableList(clipboardHistory);
    }
    
    /**
     * Clears all clipboard history.
     */
    public void clearHistory() {
        clipboardHistory.clear();
        logger.info("Clipboard history cleared");
    }
    
    /**
     * Clears the system clipboard.
     */
    private void clearSystemClipboard() {
        try {
            StringSelection selection = new StringSelection("");
            systemClipboard.setContents(selection, null);
        } catch (Exception e) {
            logger.debug("Could not clear system clipboard", e);
        }
    }
}
