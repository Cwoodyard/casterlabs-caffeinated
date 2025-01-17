package co.casterlabs.caffeinated.bootstrap;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import co.casterlabs.caffeinated.app.CaffeinatedApp;
import co.casterlabs.caffeinated.app.preferences.PreferenceFile;
import co.casterlabs.caffeinated.app.ui.UIPreferences;
import co.casterlabs.caffeinated.webview.Webview;
import lombok.NonNull;

public class TrayHandler {
    private static CheckboxMenuItem showCheckbox;
    private static SystemTray tray;

    private static Image lastImage;
    private static TrayIcon icon;

    public static void tryCreateTray(Webview webview) {
        if (tray == null) {
            SwingUtilities.invokeLater(() -> {
                // Check the SystemTray support
                if (!SystemTray.isSupported()) {
                    return;
                }

                tray = SystemTray.getSystemTray();
                PopupMenu popup = new PopupMenu();

                // Create the popup menu components
                showCheckbox = new CheckboxMenuItem("Show");
                MenuItem itemExit = new MenuItem("Exit");

                showCheckbox.setState(webview.isOpen());

                // Add components to popup menu
                popup.add(showCheckbox);
                popup.addSeparator();
                popup.add(itemExit);

                showCheckbox.addItemListener((ItemEvent e) -> {
                    if (webview.isOpen()) {
                        webview.close();
                    } else {
                        webview.open(Bootstrap.getAppUrl());
                    }
                });

                itemExit.addActionListener((ActionEvent e) -> {
                    Bootstrap.shutdown();
                });

                // Setup the tray icon.
                icon = new TrayIcon(createImage("assets/logo/casterlabs.png", "Casterlabs Logo"));

                changeTrayIcon(CaffeinatedApp.getInstance().getUiPreferences().get().getIcon());
                CaffeinatedApp.getInstance().getUiPreferences().addSaveListener((PreferenceFile<UIPreferences> uiPreferences) -> {
                    changeTrayIcon(uiPreferences.get().getIcon());
                });

                icon.setImageAutoSize(true);
                icon.setPopupMenu(popup);

                icon.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {}

                    @Override
                    public void mousePressed(MouseEvent e) {}

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (!e.isPopupTrigger()) {
                            webview.open(Bootstrap.getAppUrl());
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {}

                    @Override
                    public void mouseExited(MouseEvent e) {}
                });

                try {
                    tray.add(icon);
                } catch (AWTException e) {}
            });
        } else {
            throw new IllegalStateException("Tray handler is already initialized.");
        }
    }

    public static void updateShowCheckbox(boolean newState) {
        if (showCheckbox != null) {
            showCheckbox.setState(newState);
        }
    }

    private static void changeTrayIcon(String logo) {
        if (lastImage != null) {
            lastImage.flush();
        }

        Image image = createImage(String.format("assets/logo/%s.png", logo), "Casterlabs Logo");
        lastImage = image;

        icon.setImage(image);
    }

    public static void notify(@NonNull String message, @NonNull MessageType type) {
        icon.displayMessage("Casterlabs Caffeinated", message, type);
    }

    public static void destroy() {
        if (tray != null) {
            SwingUtilities.invokeLater(() -> {
                tray.remove(icon);
            });
        }
    }

    private static Image createImage(String path, String description) {
        try {
            return new ImageIcon(FileUtil.loadResourceAsUrl(path), description).getImage();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
