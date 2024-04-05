package Actions;

import Entities.EnemyManager;
import Entities.Player;
import Levels.LevelBuilder;
import Levels.Playing;
import Objects.ObjectManager;

import static Utilities.Constants.PlayerConstants.SMALL;

public class PlayerDeath {

    public void reloadWorld(Player player, Playing playing, EnemyManager enemyManager, ObjectManager objectManager) {
        player.resetDirBooleans();
        enemyManager.resetEnemy();
        objectManager.resetObjects();
        player.lvl = LevelBuilder.getLevelData();
        playing.initLevel();
        player.lives--;
        player.playerStatus = SMALL;
        player.x = LevelBuilder.startingX;
        player.y = LevelBuilder.startingY;
        player.setAnimatedAction(false);
    }
}
