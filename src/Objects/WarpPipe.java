package Objects;

import Entities.Player;
import com.company.Game;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import static Utilities.Constants.PlayerConstants.SMALL;


public class WarpPipe extends GameObject {
    public static Map<WarpPipe, WarpPipe> WarpPipesMap = new HashMap<>();
    private boolean teleportingIn;
    private boolean teleportingOut;
    private int pipeCounter;

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
                player.immortality = true;
                teleportingIn = true;
            }
            if (teleportingIn) {
                player.y+=0.5;
                player.duck = true;
                player.isTeleporting = true;
                pipeCounter++;
                if (pipeCounter >= 96) {
                    player.y = outPipe.y;
                    player.x = outPipe.x;
                    teleportingIn = false;
                    teleportingOut = true;
                    pipeCounter = 0;
                }
            }
            else if (teleportingOut) {
                player.y -= 0.5;
                player.duck = true;
                player.isTeleporting = true;
                pipeCounter++;
                if (player.playerStatus == SMALL) {
                    if (pipeCounter >= 60) {
                        teleportingOut = false;
                        pipeCounter = 0;
                        player.duck = false;
                        player.immortality = false;
                    }
                }
                else {
                    if (pipeCounter >= 160) {
                        teleportingOut = false;
                        pipeCounter = 0;
                        player.duck = false;
                        player.immortality = false;
                    }
                }
            }
        }
    }

    public static Map<WarpPipe, WarpPipe> getWarpPipes() {
        return WarpPipesMap;
    }
}
