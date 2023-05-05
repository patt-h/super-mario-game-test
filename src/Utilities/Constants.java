package Utilities;

public class Constants {

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
        public static final int BIG_MARIO_IDLE = 5;
        public static final int BIG_MARIO_WALK = 6;
        public static final int BIG_MARIO_RUN = 7;
        public static final int BIG_MARIO_JUMP = 9;
        public static final int BIG_MARIO_DUCK = 10;
        public static final int BIG_MARIO_TURN = 11;

        public static int getSpriteAmount(int player_action) {
            return switch (player_action) {
                case SMALL_MARIO_IDLE, BIG_MARIO_IDLE -> 0;
                case SMALL_MARIO_WALK, SMALL_MARIO_RUN, BIG_MARIO_WALK, BIG_MARIO_RUN, BIG_MARIO_JUMP, BIG_MARIO_DUCK -> 3;
                case BIG_MARIO_TURN -> 4;
                default -> 0;
            };
        }
    }
}
