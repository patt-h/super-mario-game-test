package Visuals;

import java.util.ArrayList;

import static Utilities.Constants.VisualsConstants.HILL;

public class Hill extends Visuals {
    public static ArrayList<Hill> HillList = new ArrayList<>();

    public Hill(float x, float y) {
        super(x, y);
        width = 488;
        height = 207;
        visualType = HILL;
        loadFrames(1, 325, "Hill.png");
    }

    public static ArrayList<Hill> getHillList() {
        return HillList;
    }
}
