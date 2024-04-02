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
            if (c.isActive()) {
                c.update();
                g.drawImage(c.frames[c.getAniIndex()], (int)c.x - xLvlOffset, (int)c.y, 97, 72, null);
            }
        }

        for (Grass gr : grasses) {
            if (gr.isActive()) {
                gr.update();
                g.drawImage(gr.frames[gr.getAniIndex()], (int)gr.x - xLvlOffset, (int)gr.y, 96, 48, null);
            }
        }

        for (Fence f : fences) {
            if (f.isActive()) {
                g.drawImage(f.frames[f.getAniIndex()], (int)f.x - xLvlOffset, (int)f.y, 144, 48, null);
            }
        }

        for (FinishLine fl : finishLine) {
            g.drawImage(fl.frames[fl.getAniIndex()], (int)fl.x - xLvlOffset, (int)fl.y, 382, 430, null);
        }
    }

    public void resetObjects() {
        CloudList.clear();
        GrassList.clear();
        FenceList.clear();
        FinishLineList.clear();
    }
}
