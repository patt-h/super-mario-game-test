package Utilities;

import com.company.Game;

public class ChangeWorld {

    public static void incrementWorld() {
        String[] parts = Game.world.split("-");
        int part1 = Integer.parseInt(parts[0]);
        int part2 = Integer.parseInt(parts[1]);

        part2++;
        Game.world = part1 + "-" + part2;
    }

}
