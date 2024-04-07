package Objects;

import Audio.AudioPlayer;
import Entities.Player;
import com.company.Game;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;


public class WarpPipe extends GameObject {
    public static Map<WarpPipe, WarpPipe> WarpPipesMap = new HashMap<>();
    public static Map<WarpPipe, String> WorldWarpPipesMap = new HashMap<>();

    public WarpPipe(int x, int y, int objType) {
        super(x, y, objType);
        doAnimation = false;
        hitbox = new Rectangle2D.Float();
        hitbox.width = 16 * Game.SCALE;
        hitbox.height = 2 * Game.SCALE;
        hitbox.x = x;
        hitbox.y = y;
    }

    public void checkPlayerDuck(Player player) {
        for (Map.Entry<WarpPipe, WarpPipe> warpPipes : WarpPipesMap.entrySet()) {
            WarpPipe inPipe = warpPipes.getKey();
            WarpPipe outPipe = warpPipes.getValue();

            if (player.hitbox.intersects(inPipe.hitbox) && player.duck) {
                player.x = inPipe.x;
                player.exitX = outPipe.x;
                player.exitY = outPipe.y;
                player.teleportingIn = true;
                player.getAudioPlayer().playEffect(AudioPlayer.PIPE);
            }
        }

        for (Map.Entry<WarpPipe, String> worldPipes : WorldWarpPipesMap.entrySet()) {
            WarpPipe inPipe = worldPipes.getKey();
            String world = worldPipes.getValue();

            if (player.hitbox.intersects(inPipe.hitbox) && player.duck) {
                player.x = inPipe.x;
                player.worldTeleportingIn = true;
                player.getAudioPlayer().playEffect(AudioPlayer.PIPE);
                Game.world = world;
            }
        }
    }

    public static Map<WarpPipe, WarpPipe> getWarpPipes() {
        return WarpPipesMap;
    }

    public static Map<WarpPipe, String> getWorldPipes() {
        return WorldWarpPipesMap;
    }
}
