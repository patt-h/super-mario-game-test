package Visuals;

import java.util.ArrayList;

import static Utilities.Constants.VisualsConstants.CLOUD;

public class Cloud extends Visuals {
    public static ArrayList<Cloud> CloudList = new ArrayList<>();

    public Cloud(float x, float y) {
        super(x, y);
        visualType = CLOUD;
        loadFrames(3, 65, "Cloud.png");
    }

    public void update() {
        updateAnimationTick();
    }

    public static ArrayList<Cloud> getCloudList() {
        CloudList.add(new Cloud(120,60));
        CloudList.add(new Cloud(425,70));
        CloudList.add(new Cloud(700,56));
        return CloudList;
    }
}
