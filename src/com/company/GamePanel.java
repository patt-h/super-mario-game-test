package com.company;

import Input.KeyInputs;
import javax.swing.*;
import java.awt.*;

import static com.company.Game.GAME_WIDTH;
import static com.company.Game.GAME_HEIGHT;

public class GamePanel extends JPanel {
    private Game game;

    public GamePanel(Game game) {
        this.game = game;
        addKeyListener(new KeyInputs(this));
        setPanelSize();
    }

    private void setPanelSize() {
        Dimension size = new Dimension(GAME_WIDTH,GAME_HEIGHT);
        setPreferredSize(size);

        System.out.println("Size: " + GAME_WIDTH + "x" + GAME_HEIGHT);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        game.render(g);
    }

    public Game getGame() {
        return game;
    }

    public void updateGame() {

    }
}
