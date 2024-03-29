package Visuals;

import com.company.Game;

import java.awt.*;
import java.util.ArrayList;

import static Visuals.Cloud.CloudList;
import static Visuals.Fence.FenceList;
import static Visuals.Grass.GrassList;

public class VisualsManager {

    public ArrayList<Cloud> clouds = new ArrayList<>();
    public ArrayList<Grass> grasses = new ArrayList<>();
    public ArrayList<Fence> fences = new ArrayList<>();

    public VisualsManager() {
        addObjects();
    }

    private void addObjects() {
        clouds = Cloud.getCloudList();
        grasses = Grass.getGrassList();
        fences = Fence.getFenceList();
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawClouds(g, xLvlOffset);
    }

    private void drawClouds(Graphics g, int xLvlOffset) {
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
    }

    public void resetObjects() {
        CloudList.clear();
        GrassList.clear();
        FenceList.clear();
    }

}
