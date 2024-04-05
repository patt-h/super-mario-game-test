package Visuals;

import java.util.ArrayList;

import static Utilities.Constants.VisualsConstants.CLOUD;

public class Cloud extends Visuals {
    public static ArrayList<Cloud> CloudList = new ArrayList<>();

    public Cloud(float x, float y) {
        super(x, y);
        width = 97;
        height = 72;
        visualType = CLOUD;
        loadFrames(3, 65, "Cloud.png");
    }

    public void update() {
        updateAnimationTick();
    }

    public static ArrayList<Cloud> getCloudList() {
        return CloudList;
    }
}
