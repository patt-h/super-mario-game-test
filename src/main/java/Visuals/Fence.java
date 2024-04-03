package Visuals;

import java.util.ArrayList;

import static Utilities.Constants.VisualsConstants.FENCE;

public class Fence extends Visuals {
    public static ArrayList<Fence> FenceList = new ArrayList<>();

    public Fence(float x, float y) {
        super(x, y);
        width = 144;
        height = 48;
        visualType = FENCE;
        loadFrames(1, 96, "Fence.png");
    }

    public static ArrayList<Fence> getFenceList() {
        FenceList.add(new Fence(662, 576));
        return FenceList;
    }

}
