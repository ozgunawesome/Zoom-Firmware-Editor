package services.ozzy.zoomeditor.gui;

import services.ozzy.ZoomFirmwareEditor;

import javax.swing.*;
import java.awt.*;

public final class AppWindow extends JFrame {

    private static final long serialVersionUID = -8769364485266832709L;
    private final CardLayout cl;
    private final JPanel cards;

    public AppWindow(MainPanel mainPanel) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(ZoomFirmwareEditor.getProperty("title") + " " + ZoomFirmwareEditor.getProperty("version"));
        Dimension dimension = new Dimension(550, 443);
        setSize(dimension);
        setMinimumSize(dimension);
        setResizable(true);
        setLocationRelativeTo(null);

        cl = new CardLayout();
        cards = new JPanel(cl);
        cards.add(new DisclaimerPanel());
        cards.add(mainPanel);
        add(cards);
        cl.first(cards);
    }

    public void setMainCard() {
        cl.last(cards);
    }

}
