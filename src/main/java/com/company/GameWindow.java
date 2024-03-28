package com.company;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Objects;

public class GameWindow {
    private JFrame frame;
    private ImageIcon img;

    public GameWindow(GamePanel gamePanel) {
        img = new ImageIcon((Objects.requireNonNull(getClass().getResource("/mario_icon.png"))));

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.setResizable(false);
        frame.pack();
        frame.setTitle("Super Mario Rebrand");
        frame.setIconImage(img.getImage());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {

            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                gamePanel.getGame().windowFocusLost();
            }
        });
    }
}
