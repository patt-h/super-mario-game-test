package Visuals;

import java.util.ArrayList;

import static Utilities.Constants.VisualsConstants.FINISH_LINE;

public class FinishLine extends Visuals {

    public static ArrayList<FinishLine> FinishLineList = new ArrayList<>();

    public FinishLine(float x, float y) {
        super(x, y);
        width = 382;
        height = 430;
        visualType = FINISH_LINE;
        loadFrames(1,255, "FinishLine.png");
    }

    public static ArrayList<FinishLine> getFinishLineList() {
        return FinishLineList;
    }
}
