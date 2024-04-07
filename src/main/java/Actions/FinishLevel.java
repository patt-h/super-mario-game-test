package Actions;

import Audio.AudioPlayer;
import Entities.Player;
import Levels.Playing;
import Objects.FinishBar;
import Utilities.ChangeWorld;
import com.company.Game;

import static Utilities.Constants.Directions.RIGHT;

public class FinishLevel {
    public static long startTime;

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
                if (System.nanoTime() - startTime >= 8 * 1000000000L) {
                    if (Playing.worldTime > 0) {
                        // RIP MY EARS, DIDN'T WORK WHEN I WAS TESTING THIS FOR THE FIRST TIME
                        if (!player.getAudioPlayer().isPlaying(AudioPlayer.TIME_DECREASING)) {
                            player.getAudioPlayer().playEffect(AudioPlayer.TIME_DECREASING);
                        }
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

    public static void playFinishSound(Player player) {
        player.getAudioPlayer().playEffect(AudioPlayer.FINISH_LEVEL);
    }
}
