package Levels;

import Entities.Goomba;
import Entities.Piranha;
import Entities.Troopa;
import Objects.Coin;
import Objects.CoinBlock;
import Objects.WarpPipe;
import com.company.Game;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

import static Entities.Goomba.GoombaList;
import static Entities.Piranha.PiranhaList;
import static Entities.Troopa.TroopaList;
import static Objects.Coin.CoinList;
import static Objects.CoinBlock.coinBlocksList;
import static Objects.WarpPipe.WarpPipesMap;
import static Objects.WarpPipe.WorldWarpPipesMap;
import static Utilities.Constants.ObjectConstants.COIN;
import static Utilities.Constants.ObjectConstants.WARPPIPE;

public class LevelBuilder {

    public static int[][] getLevelData() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File("worlds/" + Game.world + ".json"));

            if (rootNode.has("map")) {
                JsonNode mapNode = rootNode.get("map");
                int rowCount = mapNode.size();
                int[][] lvl = new int[rowCount][];
                for (int i = 0; i < rowCount; i++) {
                    JsonNode rowNode = mapNode.get(i);
                    int colCount = rowNode.size();
                    lvl[i] = new int[colCount];
                    for (int j = 0; j < colCount; j++) {
                        lvl[i][j] = rowNode.get(j).asInt();
                        if (lvl[i][j] == 191) {
                            coinBlocksList.add(new CoinBlock(j*Game.TILES_SIZE, i*Game.TILES_SIZE));
                        }
                    }
                }
                getWarps(rootNode);
                getCoins(rootNode);
                getEnemy(rootNode);


                return lvl;
            } else {
                System.out.println("There's no file with that name!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[0][0];
    }

    private static void getEnemy(JsonNode rootNode) {
        JsonNode goombaNode = rootNode.path("mobs").path("goomba");
        for (JsonNode mob : goombaNode) {
            int x = mob.get(0).asInt();
            int y = mob.get(1).asInt();
            GoombaList.add(new Goomba(x, y));
        }

        JsonNode troopaNode = rootNode.path("mobs").path("troopa");
        for (JsonNode mob : troopaNode) {
            int x = mob.get(0).asInt();
            int y = mob.get(1).asInt();
            TroopaList.add(new Troopa(x, y));
        }

        JsonNode piranhaNode = rootNode.path("mobs").path("piranha");
        for (JsonNode mob : piranhaNode) {
            int x = mob.get(0).asInt();
            int y = mob.get(1).asInt();
            PiranhaList.add(new Piranha(x, y));
        }
    }

    private static void getWarps(JsonNode rootNode) {
        JsonNode warpNode = rootNode.path("warps");
        for (JsonNode warp : warpNode) {
            int entranceX = warp.get(0).asInt();
            int entranceY = warp.get(1).asInt();
            int exitX = warp.get(2).asInt();
            int exitY = warp.get(3).asInt();
            WarpPipesMap.put(new WarpPipe(entranceX, entranceY, WARPPIPE), new WarpPipe(exitX, exitY, WARPPIPE));
        }

        JsonNode worldNode = rootNode.path("worldPipes");
        for (JsonNode warp : worldNode) {
            int x = warp.get(0).asInt();
            int y = warp.get(1).asInt();
            String world = warp.get(2).asText();
            WorldWarpPipesMap.put(new WarpPipe(x, y, WARPPIPE), world);
        }
    }

    private static void getCoins(JsonNode rootNode) {
        JsonNode coinsNode = rootNode.path("coins");
        for (JsonNode coin : coinsNode) {
            int x = coin.get(0).asInt();
            int y = coin.get(1).asInt();
            CoinList.add(new Coin(x, y, COIN));
        }
    }
}
