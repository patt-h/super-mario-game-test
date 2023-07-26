package Entities;

import Levels.Playing;
import com.company.Game;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import static Utilities.Constants.PlayerConstants.*;
import static Utilities.Constants.Directions.*;

public class Player extends Entity {
    private int animationTick, animationIndex, animationSpeed = 25;
    private int accurateAnimationRow;
    private float playerSpeed = 1.0f;
    public static float minSprint = 1.0f;
    private float maxSprint = 2.0f;
    public static float leftPlayerSprint;
    public static float rightPlayerSprint;
    public static int playerStatus = SMALL;
    public int playerAction = BIG_MARIO_IDLE;
    private boolean moving = false;
    public static boolean jumping = false;
    private boolean ducking = false;
    public static boolean turning = false;
    public static boolean left, up, right, down, jump, duck, sprint;
    public static boolean immortality = false;
    public static boolean gotHit = false;
    public static boolean bigUpgrade = false;
    public static boolean fireUpgrade = false;
    private int counter = 0;

    //JUMPING
    public static float airSpeed = 0.0f;
    public static boolean inAir = false;
    public static float gravity = 0.04f * Game.SCALE;
    private static float strongerGravity = 0.08f * Game.SCALE;
    public static float jumpSpeed = -2.55f * Game.SCALE;

    public static boolean debugMode = false;

    BufferedImage img;
    BufferedImage smallMario;
    BufferedImage bigMario;
    BufferedImage fireMario;

    public Player(float x, float y) {
        super(x, y);
        direction = RIGHT;
        hitbox = new Rectangle2D.Float();
        mirrorReflection();
    }

    public void update() {
        updatePosition();
        updateAnimationTick();
        setAnimation();
    }

    private void mirrorReflection() {
        img = animations[accurateAnimationRow][animationIndex];
        smallMario = animations[0][animationIndex];
        bigMario = animations[2][animationIndex];
        fireMario = animations[4][animationIndex];

        //MIRROR REFLECTION OF EVERY SPRITES
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-img.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        img = op.filter(img,null);

        //MIRROR REFLECTION OF SMALL MARIO SPRITES TO GETTING HIT ANIMATION
        AffineTransform tx2 = AffineTransform.getScaleInstance(-1, 1);
        tx2.translate(-smallMario.getWidth(null), 0);
        AffineTransformOp op2 = new AffineTransformOp(tx2, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        smallMario = op2.filter(smallMario,null);

        //MIRROR REFLECTION OF BIG MARIO SPRITES TO GETTING HIT ANIMATION
        AffineTransform tx3 = AffineTransform.getScaleInstance(-1, 1);
        tx3.translate(-bigMario.getWidth(null), 0);
        AffineTransformOp op3 = new AffineTransformOp(tx3, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        bigMario = op3.filter(bigMario,null);

        //MIRROR REFLECTION OF FIRE MARIO SPRITES TO GETTING HIT ANIMATION
        AffineTransform tx4 = AffineTransform.getScaleInstance(-1, 1);
        tx4.translate(-fireMario.getWidth(null), 0);
        AffineTransformOp op4 = new AffineTransformOp(tx4, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        fireMario = op4.filter(fireMario,null);
    }

    public void render(Graphics g, int lvlOffset) {
        mirrorReflection();

        if (playerStatus != DEAD) {
            if (direction == RIGHT) {
                if (playerStatus == SMALL) {
                    //TURNING
                    if (animations[accurateAnimationRow][animationIndex] == animations[0][4]) {
                        g.drawImage(img, (int) (x - 81) - lvlOffset, (int) y - 6, 120, 120, null);
                    }
                    //RUNNING FIXED
                    else if (animations[accurateAnimationRow][animationIndex] == animations[0][2]) {
                        g.drawImage(animations[accurateAnimationRow][animationIndex], (int) (x - 5) - lvlOffset, (int) y - 6, 120, 120, null);
                    }
                    //EVERYTHING ELSE
                    else {
                        if (!gotHit) {
                            g.drawImage(animations[accurateAnimationRow][animationIndex], (int) (x - 5) - lvlOffset, (int) y - 4, 120, 120, null);
                        }
                        if (gotHit) {
                            counter++;
                            if (counter % 16 == 0 || counter % 16 == 1 || counter % 16 == 2 || counter % 16 == 3 || counter % 16 == 4 || counter % 16 == 5 || counter % 16 == 6 || counter % 16 == 7) {
                                g.drawImage(animations[2][animationIndex], (int) (x - 5) - lvlOffset, (int) y - 48, 120, 120, null);
                            } else {
                                g.drawImage(animations[accurateAnimationRow][animationIndex], (int) (x - 5) - lvlOffset, (int) y - 4, 120, 120, null);
                            }
                            if (counter >= 256) {
                                counter = 0;
                                gotHit = false;
                                immortality = false;
                            }
                        }
                    }
                }
                if (playerStatus == BIG) {
                    //TURNING
                    if (animations[accurateAnimationRow][animationIndex] == animations[2][4]) {
                        g.drawImage(img, (int) (x - 81) - lvlOffset, (int) y - 2, 120, 120, null);
                    }
                    //DUCKING
                    else if (animations[accurateAnimationRow][animationIndex] == animations[3][3]) {
                        g.drawImage(animations[accurateAnimationRow][animationIndex], (int) (x - 17) - lvlOffset, (int) y + 12, 120, 120, null);
                    }
                    //EVERYTHING ELSE
                    else {
                        if (!gotHit && !bigUpgrade) {
                            g.drawImage(animations[accurateAnimationRow][animationIndex], (int) (x - 5) - lvlOffset, (int) y, 120, 120, null);
                        }
                        if (bigUpgrade) {
                            counter++;
                            if (counter % 16 == 0 || counter % 16 == 1 || counter % 16 == 2 || counter % 16 == 3 || counter % 16 == 4 || counter % 16 == 5 || counter % 16 == 6 || counter % 16 == 7) {
                                g.drawImage(animations[0][animationIndex], (int) (x - 5) - lvlOffset, (int) y + 44, 120, 120, null);
                            } else {
                                g.drawImage(animations[accurateAnimationRow][animationIndex], (int) (x - 5) - lvlOffset, (int) y, 120, 120, null);
                            }
                            if (counter >= 256) {
                                counter = 0;
                                bigUpgrade = false;
                            }
                        }
                        if (gotHit) {
                            counter++;
                            if (counter % 16 == 0 || counter % 16 == 1 || counter % 16 == 2 || counter % 16 == 3 || counter % 16 == 4 || counter % 16 == 5 || counter % 16 == 6 || counter % 16 == 7) {
                                g.drawImage(null, (int) (x - 5) - lvlOffset, (int) y, 120, 120, null);
                            } else {
                                g.drawImage(animations[accurateAnimationRow][animationIndex], (int) (x - 5) - lvlOffset, (int) y, 120, 120, null);
                            }
                            if (counter >= 256) {
                                counter = 0;
                                gotHit = false;
                                immortality = false;
                            }
                        }
                    }
                }
                if (playerStatus == FIRE) {
                    //TURNING
                    if (animations[accurateAnimationRow][animationIndex] == animations[4][4]) {
                        g.drawImage(img, (int) (x - 81) - lvlOffset, (int) y - 2, 120, 120, null);
                    }
                    //DUCKING
                    else if (animations[accurateAnimationRow][animationIndex] == animations[5][3]) {
                        g.drawImage(animations[accurateAnimationRow][animationIndex], (int) (x - 17) - lvlOffset, (int) y + 12, 120, 120, null);
                    }
                    //EVERYTHING ELSE
                    else {
                        if (!fireUpgrade) {
                            g.drawImage(animations[accurateAnimationRow][animationIndex], (int) (x - 5) - lvlOffset, (int) y, 120, 120, null);
                        }
                        else {
                            counter++;
                            if (counter % 16 == 0 || counter % 16 == 1 || counter % 16 == 2 || counter % 16 == 3 || counter % 16 == 4 || counter % 16 == 5 || counter % 16 == 6 || counter % 16 == 7) {
                                g.drawImage(animations[2][animationIndex], (int) (x - 5) - lvlOffset, (int) y, 120, 120, null);
                            } else {
                                g.drawImage(animations[accurateAnimationRow][animationIndex], (int) (x - 5) - lvlOffset, (int) y, 120, 120, null);
                            }
                            if (counter >= 256) {
                                counter = 0;
                                fireUpgrade = false;
                            }
                        }
                    }
                }
            } else if (direction == LEFT) {
                if (playerStatus == SMALL) {
                    //TURNING
                    if (animations[accurateAnimationRow][animationIndex] == animations[0][4]) {
                        g.drawImage(animations[accurateAnimationRow][animationIndex], (int) (x - 5) - lvlOffset, (int) y - 6, 120, 120, null);
                    }
                    //RUNNING FIXED
                    else if (animations[accurateAnimationRow][animationIndex] == animations[0][2]) {
                        g.drawImage(img, (int) (x - 81) - lvlOffset, (int) y - 6, 120, 120, null);
                    }
                    //EVERYTHING ELSE
                    else {
                        if (!gotHit) {
                            g.drawImage(img, (int) (x - 81) - lvlOffset, (int) y - 4, 120, 120, null);
                        }
                        if (gotHit) {
                            counter++;
                            if (counter % 16 == 0 || counter % 16 == 1 || counter % 16 == 2 || counter % 16 == 3 || counter % 16 == 4 || counter % 16 == 5 || counter % 16 == 6 || counter % 16 == 7) {
                                g.drawImage(bigMario, (int) (x - 81) - lvlOffset, (int) y - 48, 120, 120, null);
                            } else {
                                g.drawImage(img, (int) (x - 81) - lvlOffset, (int) y - 4, 120, 120, null);
                            }
                            if (counter >= 256) {
                                counter = 0;
                                gotHit = false;
                                immortality = false;
                            }
                        }
                    }
                }
                if (playerStatus == BIG) {
                    //TURNING
                    if (animations[accurateAnimationRow][animationIndex] == animations[2][4]) {
                        g.drawImage(animations[accurateAnimationRow][animationIndex], (int) (x - 5) - lvlOffset, (int) y - 2, 120, 120, null);
                    }
                    //DUCKING
                    else if (animations[accurateAnimationRow][animationIndex] == animations[3][3]) {
                        g.drawImage(img, (int) (x - 69) - lvlOffset, (int) y + 12, 120, 120, null);
                    }
                    //EVERYTHING ELSE
                    else {
                        if (!gotHit && !bigUpgrade) {
                            g.drawImage(img, (int) (x - 81) - lvlOffset, (int) y, 120, 120, null);
                        }
                        if (bigUpgrade) {
                            counter++;
                            if (counter % 16 == 0 || counter % 16 == 1 || counter % 16 == 2 || counter % 16 == 3 || counter % 16 == 4 || counter % 16 == 5 || counter % 16 == 6 || counter % 16 == 7) {
                                g.drawImage(smallMario, (int) (x - 81) - lvlOffset, (int) y + 44, 120, 120, null);
                            } else {
                                g.drawImage(img, (int) (x - 81) - lvlOffset, (int) y, 120, 120, null);
                            }
                            if (counter >= 256) {
                                counter = 0;
                                bigUpgrade = false;
                            }
                        }
                        if (gotHit) {
                            counter++;
                            if (counter % 16 == 0 || counter % 16 == 1 || counter % 16 == 2 || counter % 16 == 3 || counter % 16 == 4 || counter % 16 == 5 || counter % 16 == 6 || counter % 16 == 7) {
                                g.drawImage(null, (int) (x - 81) - lvlOffset, (int) y, 120, 120, null);
                            } else {
                                g.drawImage(img, (int) (x - 81) - lvlOffset, (int) y, 120, 120, null);
                            }
                            if (counter >= 256) {
                                counter = 0;
                                gotHit = false;
                                immortality = false;
                            }
                        }
                    }
                }
                if (playerStatus == FIRE) {
                    //TURNING
                    if (animations[accurateAnimationRow][animationIndex] == animations[4][4]) {
                        g.drawImage(animations[accurateAnimationRow][animationIndex], (int) (x - 5) - lvlOffset, (int) y - 2, 120, 120, null);
                    }
                    //DUCKING
                    else if (animations[accurateAnimationRow][animationIndex] == animations[5][3]) {
                        g.drawImage(img, (int) (x - 69) - lvlOffset, (int) y + 12, 120, 120, null);
                    }
                    //EVERYTHING ELSE
                    else {
                        if (!fireUpgrade) {
                            g.drawImage(img, (int) (x - 81) - lvlOffset, (int) y, 120, 120, null);
                        }
                        else {
                            counter++;
                            if (counter % 16 == 0 || counter % 16 == 1 || counter % 16 == 2 || counter % 16 == 3 || counter % 16 == 4 || counter % 16 == 5 || counter % 16 == 6 || counter % 16 == 7) {
                                g.drawImage(bigMario, (int) (x - 81) - lvlOffset, (int) y, 120, 120, null);
                            } else {
                                g.drawImage(img, (int) (x - 81) - lvlOffset, (int) y, 120, 120, null);
                            }
                            if (counter >= 256) {
                                counter = 0;
                                fireUpgrade = false;
                            }
                        }
                    }
                }
            }
        }
        //DEAD ANIMATION
        else {
            g.drawImage(animations[1][2], (int)(x - 5) - lvlOffset, (int) y, 120, 120, null);
            jump();
            inAir = true;
        }
        if (debugMode) {
            if (duck && !inAir) {
                g.setColor(Color.BLACK);
                g.drawRect((int) x - lvlOffset, (int) y + Game.TILES_SIZE - 3, (int) hitbox.width, (int) (hitbox.height));
            } else {
                g.setColor(Color.RED);
                g.drawRect((int) x - lvlOffset, (int) y, (int) hitbox.width, (int) (hitbox.height));
            }
        }
    }

    private void updateAnimationTick() {
        animationTick++;
        if (playerAction == SMALL_MARIO_IDLE || playerAction == SMALL_MARIO_WALK || playerAction == SMALL_MARIO_RUN) {
            accurateAnimationRow = 0;
        }
        if (playerAction == SMALL_MARIO_JUMP) {
            accurateAnimationRow = 0;
            animationIndex = 3;
        }
        if (playerAction == SMALL_MARIO_TURN) {
            accurateAnimationRow = 0;
            animationIndex = 4;
        }
        if (playerAction == BIG_MARIO_IDLE || playerAction == BIG_MARIO_WALK || playerAction == BIG_MARIO_RUN) {
            accurateAnimationRow = 2;
        }
        if (playerAction == BIG_MARIO_JUMP) {
            accurateAnimationRow = 2;
            animationIndex = 3;
        }
        if (playerAction == BIG_MARIO_TURN) {
            accurateAnimationRow = 2;
            animationIndex = 4;
        }
        if (playerAction == BIG_MARIO_DUCK) {
            accurateAnimationRow = 3;
            animationIndex = 3;
        }
        if (playerAction == FIRE_MARIO_IDLE || playerAction == FIRE_MARIO_WALK || playerAction == FIRE_MARIO_RUN) {
            accurateAnimationRow = 4;
        }
        if (playerAction == FIRE_MARIO_JUMP) {
            accurateAnimationRow = 4;
            animationIndex = 3;
        }
        if (playerAction == FIRE_MARIO_TURN) {
            accurateAnimationRow = 4;
            animationIndex = 4;
        }
        if (playerAction == FIRE_MARIO_DUCK) {
            accurateAnimationRow = 5;
            animationIndex = 3;
        }

        if (animationTick >= animationSpeed && playerStatus != DEAD
                && playerAction != SMALL_MARIO_JUMP && playerAction != SMALL_MARIO_TURN
                && playerAction != BIG_MARIO_DUCK && playerAction != BIG_MARIO_JUMP && playerAction != BIG_MARIO_TURN
                && playerAction != FIRE_MARIO_DUCK && playerAction != FIRE_MARIO_JUMP && playerAction != FIRE_MARIO_TURN) {
            animationTick = 0;
            animationIndex++;
            if (animationIndex >= getSpriteAmount(playerAction))  {
                animationIndex = 0;
            }
        }
    }

    private void setAnimation() {
        if (playerStatus == SMALL) {
            if (moving) {
                playerAction = SMALL_MARIO_RUN;
            } else if (jumping) {
                playerAction = SMALL_MARIO_JUMP;
            } else if (turning) {
                playerAction = SMALL_MARIO_TURN;
            } else playerAction = SMALL_MARIO_IDLE;
        }
        if (playerStatus == BIG) {
            if (moving) {
                playerAction = BIG_MARIO_RUN;
            } else if (jumping) {
                playerAction = BIG_MARIO_JUMP;
            } else if (ducking) {
                playerAction = BIG_MARIO_DUCK;
            } else if (turning) {
                playerAction = BIG_MARIO_TURN;
            } else playerAction = BIG_MARIO_IDLE;
        }
        if (playerStatus == FIRE) {
            if (moving) {
                playerAction = FIRE_MARIO_RUN;
            } else if (jumping) {
                playerAction = FIRE_MARIO_JUMP;
            } else if (ducking) {
                playerAction = FIRE_MARIO_DUCK;
            } else if (turning) {
                playerAction = FIRE_MARIO_TURN;
            } else playerAction = FIRE_MARIO_IDLE;
        }
    }

    private void sprintCounter() {
        if (direction == LEFT || direction == RIGHT) {
            //ADDING UP SPRINT VALUE
            if ((sprint && leftPlayerSprint <= maxSprint) && (left) && (!right)) {
                if (inAir) {
                    leftPlayerSprint += 0.001f;
                } else {
                    leftPlayerSprint += 0.01f;
                }
            }
            if ((sprint && rightPlayerSprint <= maxSprint) && (right) && (!left)) {
                if (inAir) {
                    rightPlayerSprint += 0.001f;
                } else {
                    rightPlayerSprint += 0.01f;
                }
            }
            //SLOWING DOWN WHEN NOTHING IS PRESSED AND MARIO SPRINTED
            if (direction == LEFT && (!sprint && leftPlayerSprint > minSprint) || (!left && !right)) {
                leftPlayerSprint -= 0.02f;
            }
            if (direction == RIGHT && (!sprint && rightPlayerSprint > minSprint) || (!left && !right)) {
                rightPlayerSprint -= 0.02f;
            }
            //PREVIOUS VALUE WHEN PLAYER SPRINT IS BELOW ZERO
            if (leftPlayerSprint < minSprint) {
                leftPlayerSprint = minSprint;
            }
            if (rightPlayerSprint < minSprint) {
                rightPlayerSprint = minSprint;
            }
        }
    }

    private void updatePosition() {
        hitbox.x = x;
        hitbox.y = y;

        moving = false;
        jumping = false;
        ducking = false;
        turning = false;
        animationSpeed = 12;
        sprintCounter();
        fallenOutsideMap();
        Playing.checkCloseToBorder();
        Playing.checkCollisions();

        if (playerStatus == SMALL) {
            duck = false;
        }
        if (playerStatus == SMALL || (duck && !inAir)) {
            hitbox.width = 11 * Game.SCALE;
            hitbox.height = 14 * Game.SCALE;
        }
        else if (playerStatus == BIG || playerStatus == FIRE) {
            hitbox.width = 11 * Game.SCALE;
            hitbox.height = 30 * Game.SCALE;
        }

        if (!Playing.collision && playerStatus != DEAD) {
            //MOVING LEFT
            if (left && !right && !duck) {
                if (rightPlayerSprint > minSprint) {
                    turning = true;
                    x += rightPlayerSprint;
                    rightPlayerSprint -= 0.02f;
                    leftPlayerSprint = 0;
                } else if (sprint && rightPlayerSprint <= minSprint) {
                    moving = true;
                    x -= playerSpeed * leftPlayerSprint;
                    animationSpeed = 8;
                } else {
                    moving = true;
                    x -= playerSpeed * leftPlayerSprint;
                }
                direction = LEFT;
            }
            //MOVING RIGHT
            else if (right && !left && !duck) {
                if (leftPlayerSprint > minSprint) {
                    turning = true;
                    x -= leftPlayerSprint;
                    leftPlayerSprint -= 0.02f;
                    rightPlayerSprint = 0;
                } else if (sprint && leftPlayerSprint <= minSprint) {
                    moving = true;
                    x += playerSpeed * rightPlayerSprint;
                    animationSpeed = 8;
                } else {
                    moving = true;
                    x += playerSpeed * rightPlayerSprint;
                }
                direction = RIGHT;
            }
            //SLOWING DOWN WHEN SPRINT WAS PRESSED
            if (direction == LEFT && leftPlayerSprint > minSprint) {
                x -= leftPlayerSprint;
                if (duck) {
                    ducking = true;
                } else {
                    moving = true;
                }
            } else if (direction == RIGHT && rightPlayerSprint > minSprint) {
                x += rightPlayerSprint;
                if (duck) {
                    ducking = true;
                } else {
                    moving = true;
                }
            }
            //MOVING UP ON LADDER OR PIPE
            if (up && !duck) {
                moving = true;
            }
        }
        //JUMPING
        if (jump && !duck) {
            jump();
        }
        if (inAir) {
            y += airSpeed;
            airSpeed += gravity;
            if (!jump && airSpeed < 0 && playerStatus != DEAD) {
                airSpeed += strongerGravity;
            }
            jumping = true;
            moving = false;
            ducking = false;
            if (direction == LEFT) {
                rightPlayerSprint = minSprint;
            } else if (direction == RIGHT) {
                leftPlayerSprint = minSprint;
            }
        }
        //DUCKING
        if (duck && !inAir) {
            ducking = true;
            jump = false;
            left = false;
            right = false;
        }
    }

    private void jump() {
        if (inAir) {
            return;
        }
        inAir = true;
        airSpeed = jumpSpeed;
    }

    private void fallenOutsideMap() {
        if (y > Game.GAME_HEIGHT && playerStatus != DEAD) {
            playerStatus = DEAD;
            y = Game.GAME_HEIGHT;
            inAir = false;
        }
    }

    public void resetDirBooleans() {
        left = false;
        right = false;
        up = false;
        down = false;
        jump = false;
        duck = false;
        sprint = false;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        direction = LEFT;
        this.left = left;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public static boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        direction = RIGHT;
        this.right = right;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isJump() {
        return jump;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public boolean isDuck() {
        return duck;
    }

    public void setDuck(boolean duck) {
        this.duck = duck;
    }

    public boolean isSprint() {
        return sprint;
    }

    public void setSprint(boolean sprint) {
        this.sprint = sprint;
    }
}