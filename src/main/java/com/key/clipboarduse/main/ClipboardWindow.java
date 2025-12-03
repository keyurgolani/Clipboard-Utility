package com.key.clipboarduse.main;

import com.formdev.flatlaf.FlatLightLaf;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main window for displaying clipboard history.
 * Shows when user navigates through clipboard history with Win+Shift.
 * Features a modern, clean UI design with improved usability.
 */
public class ClipboardWindow extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(ClipboardWindow.class);
    
    // UI Constants
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 400;
    private static final String WINDOW_TITLE = "Clipboard History";
    private static final int BORDER_PADDING = 20;
    private static final int TEXT_AREA_FONT_SIZE = 14;
    private static final int HEADER_FONT_SIZE = 13;
    
    // Colors for modern UI
    private static final Color HEADER_BG_COLOR = new Color(250, 250, 250);
    private static final Color HEADER_TEXT_COLOR = new Color(60, 60, 60);
    private static final Color TEXT_AREA_BG_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    
    private final JTextArea textArea;
    private final JLabel instructionLabel;
    private final ClipboardManager clipboardManager;
    private KeyboardEventHandler keyboardEventHandler;
    
    public ClipboardWindow() {
        this.clipboardManager = new ClipboardManager();
        this.textArea = new JTextArea();
        this.instructionLabel = new JLabel();
        
        // Set modern Look and Feel
        try {
            FlatLightLaf.setup();
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("Button.arc", 8);
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("ScrollBar.width", 12);
        } catch (Exception e) {
            logger.warn("Failed to set FlatLaf Look and Feel, using system default", e);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                logger.warn("Failed to set system Look and Feel", ex);
            }
        }
        
        setupWindow();
    }
    
    /**
     * Sets up the window UI components and properties with modern design.
     */
    private void setupWindow() {
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setAlwaysOnTop(true);
        setResizable(true);
        setMinimumSize(new Dimension(500, 300));
        centerWindow();
        
        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBorder(new EmptyBorder(BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));
        mainPanel.setBackground(Color.WHITE);
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create content panel with text area
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
    }
    
    /**
     * Creates the header panel with instructions.
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(HEADER_BG_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        
        // Icon label (using a simple emoji/symbol for clipboard)
        JLabel iconLabel = new JLabel("📋");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        headerPanel.add(iconLabel, BorderLayout.WEST);
        
        // Instruction text
        instructionLabel.setText("<html><b>Clipboard History Preview</b><br/>"
            + "<span style='font-size:11px; color:#666;'>Press <b>SHIFT</b> for next item • "
            + "Release <b>Win</b> key to paste • <b>Win+Shift+E</b> to exit</span></html>");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, HEADER_FONT_SIZE));
        instructionLabel.setForeground(HEADER_TEXT_COLOR);
        headerPanel.add(instructionLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Creates the content panel with the text area.
     */
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        
        // Configure text area with modern styling
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, TEXT_AREA_FONT_SIZE));
        textArea.setBackground(TEXT_AREA_BG_COLOR);
        textArea.setForeground(new Color(40, 40, 40));
        textArea.setMargin(new Insets(15, 15, 15, 15));
        textArea.setCaretColor(new Color(0, 120, 215));
        
        // Create scroll pane with modern styling
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        return contentPanel;
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
        SwingUtilities.invokeLater(() -> {
            textArea.setText(text);
            textArea.setCaretPosition(0); // Scroll to top
        });
    }
    
    /**
     * Makes the window visible with a smooth appearance.
     * Overridden to ensure proper UI thread execution.
     */
    @Override
    public void setVisible(boolean visible) {
        if (SwingUtilities.isEventDispatchThread()) {
            super.setVisible(visible);
        } else {
            SwingUtilities.invokeLater(() -> super.setVisible(visible));
        }
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
