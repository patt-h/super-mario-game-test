package Utilities;

public class Constants {

    public static class ObjectConstants {
        public static final int COIN_PRIZE_BLOCK = 0;
        public static final int FLOWER_PRIZE_BLOCK = 1;
        public static final int COIN = 2;
        public static final int MUSHROOM = 3;
        public static final int FLOWER = 4;

        public static int getSpriteAmount(int object_type) {
            return switch (object_type) {
                case MUSHROOM -> 0;
                case COIN_PRIZE_BLOCK, FLOWER_PRIZE_BLOCK, COIN -> 3;
                case FLOWER -> 4;
                default -> 0;
            };
        }
    }

    public static class Directions {
        public static final int LEFT = 0;
        public static final int UP = 1;
        public static final int RIGHT = 2;
        public static final int DOWN = 3;
    }

    public static class PlayerConstants {
        public static final int SMALL = 0;
        public static final int BIG = 1;
        public static final int FIRE = 2;

        public static final int SMALL_MARIO_IDLE = 0;
        public static final int SMALL_MARIO_WALK = 1;
        public static final int SMALL_MARIO_RUN = 2;
        public static final int SMALL_MARIO_JUMP = 4;
        public static final int SMALL_MARIO_TURN = 5;
        public static final int BIG_MARIO_IDLE = 6;
        public static final int BIG_MARIO_WALK = 7;
        public static final int BIG_MARIO_RUN = 8;
        public static final int BIG_MARIO_JUMP = 9;
        public static final int BIG_MARIO_DUCK = 10;
        public static final int BIG_MARIO_TURN = 11;

        public static int getSpriteAmount(int player_action) {
            return switch (player_action) {
                case SMALL_MARIO_IDLE, BIG_MARIO_IDLE -> 0;
                case SMALL_MARIO_WALK, SMALL_MARIO_RUN, SMALL_MARIO_JUMP, BIG_MARIO_WALK, BIG_MARIO_RUN, BIG_MARIO_JUMP, BIG_MARIO_DUCK -> 3;
                case SMALL_MARIO_TURN, BIG_MARIO_TURN -> 4;
                default -> 0;
            };
        }
    }
}
