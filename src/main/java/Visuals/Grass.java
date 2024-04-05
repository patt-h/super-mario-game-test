package Visuals;

import java.util.ArrayList;

import static Utilities.Constants.VisualsConstants.GRASS;

public class Grass extends Visuals {
    public static ArrayList<Grass> GrassList = new ArrayList<>();

    public Grass(float x, float y) {
        super(x, y);
        width = 96;
        height = 48;
        visualType = GRASS;
        loadFrames(4, 32, "Grass.png");
    }

    public void update() {
        updateAnimationTick();
    }

    public static ArrayList<Grass> getGrassList() {
        return GrassList;
    }
}
