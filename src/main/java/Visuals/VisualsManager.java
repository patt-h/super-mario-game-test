package Visuals;

import com.company.Game;

import java.awt.*;
import java.util.ArrayList;

import static Visuals.Cloud.CloudList;
import static Visuals.Fence.FenceList;
import static Visuals.FinishLine.FinishLineList;
import static Visuals.Grass.GrassList;

public class VisualsManager {

    public ArrayList<Cloud> clouds = new ArrayList<>();
    public ArrayList<Grass> grasses = new ArrayList<>();
    public ArrayList<Fence> fences = new ArrayList<>();
    public ArrayList<FinishLine> finishLine = new ArrayList<>();

    public VisualsManager() {
        addObjects();
    }

    private void addObjects() {
        clouds = Cloud.getCloudList();
        grasses = Grass.getGrassList();
        fences = Fence.getFenceList();
        finishLine = FinishLine.getFinishLineList();
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawVisuals(g, xLvlOffset);
    }

    private void drawVisuals(Graphics g, int xLvlOffset) {
        for (Cloud c : clouds) {
            if (c.isActive() && c.x - xLvlOffset < Game.GAME_WIDTH && c.x - xLvlOffset + c.getWidth() > 0) {
                c.update();
                g.drawImage(c.frames[c.getAniIndex()], (int)c.x - xLvlOffset, (int)c.y, c.getWidth(), c.getHeight(), null);
            }
        }

        for (Grass gr : grasses) {
            if (gr.isActive() && gr.x - xLvlOffset < Game.GAME_WIDTH && gr.x - xLvlOffset + gr.getWidth() > 0) {
                gr.update();
                g.drawImage(gr.frames[gr.getAniIndex()], (int)gr.x - xLvlOffset, (int)gr.y, gr.getWidth(), gr.getHeight(), null);
            }
        }

        for (Fence f : fences) {
            if (f.isActive() && f.x - xLvlOffset < Game.GAME_WIDTH && f.x - xLvlOffset + f.getWidth() > 0) {
                g.drawImage(f.frames[f.getAniIndex()], (int)f.x - xLvlOffset, (int)f.y, f.getWidth(), f.getHeight(), null);
            }
        }

        for (FinishLine fl : finishLine) {
            if (fl.x - xLvlOffset < Game.GAME_WIDTH && fl.x - xLvlOffset + fl.getWidth() > 0) {
                g.drawImage(fl.frames[fl.getAniIndex()], (int) fl.x - xLvlOffset, (int) fl.y, fl.getWidth(), fl.getHeight(), null);
            }
        }
    }

    public void resetObjects() {
        CloudList.clear();
        GrassList.clear();
        FenceList.clear();
        FinishLineList.clear();
    }
}
