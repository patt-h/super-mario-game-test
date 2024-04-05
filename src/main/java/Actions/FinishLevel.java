package Actions;

import Entities.Player;
import Levels.Playing;
import Objects.FinishBar;
import Utilities.ChangeWorld;
import com.company.Game;

import static Utilities.Constants.Directions.RIGHT;

public class FinishLevel {

    public void finishLevelActions(Player player) {
        player.setBlockMovement(true);
        if (!player.inAir) {
            if (player.x < player.getLvlTilesWide() * Game.TILES_SIZE + Game.TILES_SIZE) {
                player.setAnimatedAction(true);
                player.setMoving(true);
                player.immortality = true;
                player.direction = RIGHT;
                player.x++;
            }
            else {
                if (Playing.worldTime > 0) {
                    Playing.worldTime--;
                    player.score += 10;
                }
                else {
                    FinishBar.GotTaken = false;
                    player.immortality = false;
                    player.setAnimatedAction(false);
                    player.setMoving(false);
                    player.setBlockMovement(false);
                    ChangeWorld.incrementWorld();
                    Game.changeWorld = true;
                }
            }
        }
    }


}
