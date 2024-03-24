package Entities;

import Levels.LevelBuilder;
import Levels.LevelManager;
import Levels.MapObjects;
import Objects.Coin;
import Objects.FireFlower;
import Objects.Mushroom;
import com.company.Game;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import static Levels.MapObjects.coinBlocksList;
import static Levels.Playing.lvlLength;
import static Objects.Coin.CoinList;
import static Objects.FireFlower.FireFlowerList;
import static Objects.Mushroom.MushroomList;
import static Utilities.Constants.ObjectConstants.*;
import static Utilities.Constants.ObjectConstants.FIRE_FLOWER;
import static Utilities.Constants.PlayerConstants.*;
import static Utilities.Constants.Directions.*;

public class Player extends Entity {
    public int[][] lvl = LevelBuilder.getLevelData();
    private LevelManager levelManager;
    public int xLvlOffset;
    private int lvlTilesWide = lvl[0].length;
    private int maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
    private int maxLvlOffsetX = maxTilesOffset * Game.TILES_SIZE;

    private int animationTick, animationIndex, animationSpeed = 25;
    private int accurateAnimationRow;
    private float playerSpeed = 1.0f;
    private float minSprint = 1.0f;
    private float maxSprint = 2.0f;
    private float leftPlayerSprint;
    private float rightPlayerSprint;
    public int playerStatus = SMALL;
    private int playerAction = BIG_MARIO_IDLE;
    private boolean moving = false;
    private boolean jumping = false;
    private boolean ducking = false;
    private boolean turning = false;
    public boolean left, up, right, down, jump, duck, sprint;
    public boolean immortality = false;
    public boolean gotHit = false;
    private boolean blockMovement = false;
    public boolean bigUpgrade = false;
    public boolean fireUpgrade = false;
    private int counter = 0;
    private boolean collision;

    public boolean moved = false;
    public boolean movedCoin = false;
    public boolean broken = false;
    public int movedX, movedY;
    public int movedCoinX, movedCoinY;
    public int brokenX, brokenY;

    public int lives = 4;
    public int score;
    public int coins;

    //JUMPING
    public float airSpeed = 0.0f;
    public boolean inAir = false;
    private float gravity = 0.04f * Game.SCALE;
    private float strongerGravity = 0.08f * Game.SCALE;
    private float jumpSpeed = -2.55f * Game.SCALE;

    BufferedImage img;
    BufferedImage smallMario;
    BufferedImage bigMario;
    BufferedImage fireMario;

    public Player(float x, float y) {
        super(x, y);
        direction = RIGHT;
        hitbox = new Rectangle2D.Float();
        levelManager = new LevelManager();
        mirrorReflection();
    }

    public void update() {
        updateHitbox();
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
                            if (counter >= 128) {
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
                            if (counter >= 128) {
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
                            if (counter >= 128) {
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
                            if (counter >= 128) {
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
                            if (counter >= 128) {
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
                            if (counter >= 128) {
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
                            if (counter >= 128) {
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
                            if (counter >= 128) {
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
        if (Game.debugMode) {
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
            if (animationIndex >= getPlayerSpriteAmount(playerAction))  {
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

    private void updateHitbox() {
        if (playerStatus == SMALL || (duck && !inAir)) {
            hitbox.width = 11 * Game.SCALE;
            hitbox.height = 14 * Game.SCALE;
        }
        else if (playerStatus == BIG || playerStatus == FIRE && !duck) {
            hitbox.width = 11 * Game.SCALE;
            hitbox.height = 30 * Game.SCALE;
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
        checkCloseToBorder();
        checkCollisions();
        fallenOutsideMap();
        if (coins == 100) {
            lives++;
            coins = 0;
        }

        if (!collision && playerStatus != DEAD) {
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

        if (playerStatus == SMALL) {
            duck = false;
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

    public void checkCloseToBorder() {
        int playerX = (int)x + 17;
        int diff = playerX - xLvlOffset;
        int leftBorder = (int) (0.5 * Game.GAME_WIDTH);
        int rightBorder = (int) (0.5 * Game.GAME_WIDTH);
        if (diff > rightBorder) {
            xLvlOffset += diff - rightBorder;
        } else if (diff < leftBorder) {
            xLvlOffset += diff - leftBorder;
        }

        if (xLvlOffset > maxLvlOffsetX) {
            xLvlOffset = maxLvlOffsetX;
        } else if (xLvlOffset < 0) {
            xLvlOffset = 0;
        }
    }

    private void checkCollisions() {
        collision = false;
        blockMovement = false;

        int entityLeftX = (int) x;
        int entityRightX = (int) x + (int) hitbox.width;
        int entityTopY = (int) y;
        int entityBottomY = (int)y + (int) hitbox.height - 3;
        if (duck) {
            entityTopY = (int) y + Game.TILES_SIZE - 3;
            entityBottomY = (int) y + Game.TILES_SIZE + (int) hitbox.height - 3;
        }
        int entityMiddleY = (int) y + (int) hitbox.height / 2;
        int entityMiddleRow = entityMiddleY / Game.TILES_SIZE;

        int entityLeftCol = entityLeftX / Game.TILES_SIZE;
        int entityRightCol = entityRightX / Game.TILES_SIZE;
        int entityTopRow = entityTopY / Game.TILES_SIZE;
        int entityBottomRow = entityBottomY / Game.TILES_SIZE;

        int tileNum1, tileNum2, tileNum3, tileNum4;

        //BLOCKING JUMPING ABOVE HIGHEST BLOCK
        float distanceX;
        if (y < 0) {
            //COLLISION WHILE WALKING
            if (levelManager.sprites.get(lvl[0][entityRightCol + 1]) != levelManager.sprites.get(90) && direction == RIGHT) {
                distanceX = (entityRightCol + 1) * Game.TILES_SIZE - entityRightX;
                if (distanceX <= 5) {
                    rightPlayerSprint = 1;
                    leftPlayerSprint = 1;
                    collision = true;
                }
            }
            if (levelManager.sprites.get(lvl[0][entityLeftCol - 1]) != levelManager.sprites.get(90) && direction == LEFT) {
                distanceX = entityLeftX - (entityLeftCol) * Game.TILES_SIZE;
                if (distanceX <= 5) {
                    rightPlayerSprint = 1;
                    leftPlayerSprint = 1;
                    collision = true;
                }
            }
        }

        //COLLISIONS WHILE PLAYER IS TWO BLOCKS HIGH AND HALF OF PLAYER IS OUTSIDE OF MAP
        float distanceY;
        if (y + 2*Game.TILES_SIZE > 0 && y < 0 && playerStatus != SMALL) {
            tileNum2 = lvl[entityBottomRow][entityRightCol];
            tileNum3 = lvl[entityBottomRow][entityLeftCol];

            //COLLISION WHILE WALKING
            if (levelManager.sprites.get(lvl[0][entityRightCol + 1]) != levelManager.sprites.get(90) && direction == RIGHT) {
                distanceX = (entityRightCol + 1) * Game.TILES_SIZE - entityRightX;
                if (distanceX <= 5) {
                    rightPlayerSprint = 1;
                    leftPlayerSprint = 1;
                    collision = true;
                }
            }
            else if (levelManager.sprites.get(lvl[0][entityLeftCol - 1]) != levelManager.sprites.get(90) && direction == LEFT) {
                distanceX = entityLeftX - (entityLeftCol) * Game.TILES_SIZE;
                if (distanceX <= 5) {
                    rightPlayerSprint = 1;
                    leftPlayerSprint = 1;
                    collision = true;
                }
            }
            //COLLISION WHEN FALLING
            if (inAir
                    && (levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90))) {
                distanceY = (entityBottomRow) * Game.TILES_SIZE - entityBottomY;
                if (distanceY < 0) {
                    y = (entityBottomRow - 2) * Game.TILES_SIZE + (Game.TILES_SIZE * 2 - hitbox.height);
                    jump = false;
                    inAir = false;
                    airSpeed = 0;
                    collision = true;
                }
            }
            //FALLING
            if (levelManager.sprites.get(lvl[entityBottomRow + 1][entityRightCol]) == levelManager.sprites.get(90)
                    && levelManager.sprites.get(lvl[entityBottomRow + 1][entityLeftCol]) == levelManager.sprites.get(90)
                    && levelManager.sprites.get(lvl[entityBottomRow][entityRightCol]) == levelManager.sprites.get(90)
                    && levelManager.sprites.get(lvl[entityBottomRow][entityLeftCol]) == levelManager.sprites.get(90)) {
                inAir = true;
            }
        }

        //NORMAL COLLISIONS
        if (y > 0 && y < Game.GAME_HEIGHT - 2*Game.TILES_SIZE) {
            switch (direction) {
                case RIGHT -> {
                    tileNum1 = lvl[entityTopRow][entityRightCol];
                    tileNum2 = lvl[entityBottomRow][entityRightCol];
                    tileNum3 = lvl[entityBottomRow][entityLeftCol];
                    tileNum4 = lvl[entityTopRow][entityLeftCol];

                    //HITTING INTERACTIVE BLOCKS

                    //HITTING BRICKS
                    if (inAir && airSpeed < 0 &&
                            (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(190)) || (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(190))) {
                        //WHEN PLAYER IS SMALL MARIO, BRICKS MOVE INSTEAD OF BREAKING
                        if (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(190)) {
                            if (playerStatus == SMALL) {
                                moved = true;
                                movedX = entityRightCol * 48;
                                movedY = entityTopRow * 48;
                            } else {
                                lvl[entityTopRow][entityRightCol] = 90;
                                broken = true;
                            }
                        }
                        if (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(190)) {
                            if (playerStatus == SMALL) {
                                moved = true;
                                movedX = entityLeftCol * 48;
                                movedY = entityTopRow * 48;
                            } else {
                                lvl[entityTopRow][entityLeftCol] = 90;
                                broken = true;
                            }
                        }
                        collision = true;
                    }
                    //HITTING COIN BRICKS
                    if (inAir && airSpeed < 0 &&
                            (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(191)) || (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(191))) {
                        for (MapObjects cb : coinBlocksList) {
                            if (cb.isActive()) {
                                if (Player.hitbox.intersects(cb.hitbox)) {
                                    cb.coinsInside--;
                                    if (cb.coinsInside > 0) {
                                        movedCoin = true;
                                        coins++;
                                        score += 200;
                                    }
                                    if (cb.coinsInside == 0) {
                                        coins++;
                                        cb.setActive(false);
                                        if (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(191)) {
                                            lvl[entityTopRow][entityRightCol] = 152;
                                        }
                                        if (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(191)) {
                                            lvl[entityTopRow][entityLeftCol] = 152;
                                        }
                                    }
                                }
                                if (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(191)) {
                                    movedCoinX = entityRightCol * 48;
                                    movedCoinY = entityTopRow * 48;
                                    CoinList.add(new Coin(entityRightCol * Game.TILES_SIZE, (entityTopRow) * Game.TILES_SIZE, COIN_BRICK));
                                }
                                if (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(191)) {
                                    movedCoinX = entityLeftCol * 48;
                                    movedCoinY = entityTopRow * 48;
                                    CoinList.add(new Coin(entityLeftCol * Game.TILES_SIZE, (entityTopRow) * Game.TILES_SIZE, COIN_BRICK));
                                }
                            }
                        }
                        collision = true;
                    }
                    //HITTING POWERUP BLOCK
                    if (inAir && airSpeed < 0 &&
                            (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(115) || levelManager.sprites.get(tileNum4) == levelManager.sprites.get(115))) {
                        if (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(115)) {
                            lvl[entityTopRow][entityRightCol] = 152;
                            if (playerStatus == SMALL) {
                                MushroomList.add(new Mushroom(entityRightCol * Game.TILES_SIZE + 3, (entityTopRow) * Game.TILES_SIZE - 5, MUSHROOM));
                            } else {
                                FireFlowerList.add(new FireFlower(entityRightCol * Game.TILES_SIZE, (entityTopRow) * Game.TILES_SIZE - 5, FIRE_FLOWER));
                            }
                        } else if (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(115)) {
                            lvl[entityTopRow][entityLeftCol] = 152;
                            if (playerStatus == SMALL) {
                                MushroomList.add(new Mushroom(entityLeftCol * Game.TILES_SIZE + 3, (entityTopRow) * Game.TILES_SIZE - 5, MUSHROOM));
                            } else {
                                FireFlowerList.add(new FireFlower(entityLeftCol * Game.TILES_SIZE, (entityTopRow) * Game.TILES_SIZE - 5, FIRE_FLOWER));
                            }
                        }
                        collision = true;
                    }

                    //WALKING COLLISIONS

                    //CHECKING IF NEXT BLOCK EXISTS
                    if (x + 2 * Game.TILES_SIZE < lvlLength * Game.TILES_SIZE) {
                        //COLLISION WHILE MOVING
                        if (levelManager.sprites.get(lvl[entityTopRow][entityRightCol + 1]) != levelManager.sprites.get(90)
                                || levelManager.sprites.get(lvl[entityBottomRow][entityRightCol + 1]) != levelManager.sprites.get(90)) {
                            distanceX = (entityRightCol + 1) * Game.TILES_SIZE - entityRightX;
                            if (distanceX <= 5) {
                                rightPlayerSprint = 1;
                                leftPlayerSprint = 1;
                                collision = true;
                            }
                        }
                        //COLLISION WHEN JUMPING PLAYER HITS BLOCK IN HALF
                        if (inAir && levelManager.sprites.get(lvl[entityMiddleRow][entityRightCol + 1]) != levelManager.sprites.get(90)) {
                            distanceX = (entityRightCol + 1) * Game.TILES_SIZE - entityRightX;
                            if (distanceX <= 5) {
                                rightPlayerSprint = 0;
                            }
                        }
                        //COLLISION WHILE SLIDING
                        if (leftPlayerSprint > minSprint) {
                            if (levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90)) {
                                leftPlayerSprint = minSprint;
                                collision = true;
                            }
                        }
                    } else if (x + Game.TILES_SIZE >= lvlLength * Game.TILES_SIZE) {
                        collision = true;
                        leftPlayerSprint = 0;
                        rightPlayerSprint = 0;
                    }
                    //MOVING PLAYER FURTHER IF HE HAS BLOCK ABOVE WHILE DUCKING
                    if (!inAir && playerStatus != SMALL) {
                        if (levelManager.sprites.get(tileNum4) != levelManager.sprites.get(90)) {
                            x++;
                            blockMovement = true;
                        }
                        if (levelManager.sprites.get(tileNum1) != levelManager.sprites.get(90) && levelManager.sprites.get(tileNum4) == levelManager.sprites.get(90)) {
                            x--;
                            blockMovement = true;
                        }
                    }

                    //PLAYER IN AIR COLLISIONS

                    //COLLISION WHEN FALLING
                    if (inAir && (y < Game.GAME_HEIGHT - 3*Game.TILES_SIZE || playerStatus == SMALL)
                            && (levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90))) {
                        distanceY = (entityBottomRow) * Game.TILES_SIZE - entityBottomY;
                        if (distanceY < 0) {
                            y = entityTopRow * Game.TILES_SIZE + 8;
                            jump = false;
                            inAir = false;
                            airSpeed = 0;
                            collision = true;
                        }
                    }
                    //COLLISION WHEN JUMPING
                    if (inAir
                            && (levelManager.sprites.get(tileNum1) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum4) != levelManager.sprites.get(90))) {
                        distanceY = (entityTopRow) * Game.TILES_SIZE - entityTopY;
                        if (distanceY < 0) {
                            y = (entityTopRow + 1) * Game.TILES_SIZE;
                            airSpeed = 0;
                            collision = true;
                        }
                    }
                    //FALLING
                    if (y < Game.GAME_HEIGHT - 3 * Game.TILES_SIZE || playerStatus == SMALL) {
                        if (levelManager.sprites.get(lvl[entityBottomRow + 1][entityRightCol]) == levelManager.sprites.get(90)
                                && levelManager.sprites.get(lvl[entityBottomRow + 1][entityLeftCol]) == levelManager.sprites.get(90)
                                && levelManager.sprites.get(lvl[entityBottomRow][entityRightCol]) == levelManager.sprites.get(90)
                                && levelManager.sprites.get(lvl[entityBottomRow][entityLeftCol]) == levelManager.sprites.get(90)) {
                            inAir = true;
                        }
                    }
                }

                case LEFT -> {
                    tileNum1 = lvl[entityTopRow][entityLeftCol];
                    tileNum2 = lvl[entityBottomRow][entityLeftCol];
                    tileNum3 = lvl[entityBottomRow][entityRightCol];
                    tileNum4 = lvl[entityTopRow][entityRightCol];

                    //HITTING INTERACTIVE BLOCKS

                    //HITTING BRICKS
                    if (inAir && airSpeed < 0 &&
                            (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(190)) || (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(190))) {
                        //WHEN PLAYER IS SMALL MARIO, BRICKS MOVE INSTEAD OF BREAKING
                        if (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(190)) {
                            if (playerStatus == SMALL) {
                                moved = true;
                                movedX = entityLeftCol * 48;
                                movedY = entityTopRow * 48;
                            } else {
                                lvl[entityTopRow][entityLeftCol] = 90;
                                broken = true;
                            }
                        }
                        if (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(190)) {
                            if (playerStatus == SMALL) {
                                moved = true;
                                movedX = entityRightCol * 48;
                                movedY = entityTopRow * 48;
                            } else {
                                lvl[entityTopRow][entityRightCol] = 90;
                                broken = true;
                            }
                        }
                        collision = true;
                    }
                    //HITTING COIN BRICKS
                    if (inAir && airSpeed < 0 &&
                            (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(191)) || (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(191))) {
                        for (MapObjects cb : coinBlocksList) {
                            if (cb.isActive()) {
                                if (Player.hitbox.intersects(cb.hitbox)) {
                                    cb.coinsInside--;
                                    if (cb.coinsInside > 0) {
                                        movedCoin = true;
                                        coins++;
                                        score+=200;
                                    }
                                    if (cb.coinsInside == 0) {
                                        coins++;
                                        cb.setActive(false);
                                        if (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(191)) {
                                            lvl[entityTopRow][entityLeftCol] = 152;
                                        }
                                        if (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(191)) {
                                            lvl[entityTopRow][entityRightCol] = 152;
                                        }
                                    }
                                }
                                if (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(191)) {
                                    movedCoinX = entityLeftCol * 48;
                                    movedCoinY = entityTopRow * 48;
                                    CoinList.add(new Coin(entityLeftCol * Game.TILES_SIZE, (entityTopRow) * Game.TILES_SIZE, COIN_BRICK));
                                }
                                if (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(191)) {
                                    movedCoinX = entityRightCol * 48;
                                    movedCoinY = entityTopRow * 48;
                                    CoinList.add(new Coin(entityRightCol * Game.TILES_SIZE, (entityTopRow) * Game.TILES_SIZE, COIN_BRICK));
                                }
                            }
                        }
                        collision = true;
                    }
                    //HITTING POWERUP BLOCK
                    if (inAir && airSpeed < 0 &&
                            (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(115) || levelManager.sprites.get(tileNum4) == levelManager.sprites.get(115))) {
                        if (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(115)) {
                            lvl[entityTopRow][entityLeftCol] = 152;
                            if (playerStatus == SMALL) {
                                MushroomList.add(new Mushroom(entityLeftCol * Game.TILES_SIZE + 3, (entityTopRow) * Game.TILES_SIZE - 5, MUSHROOM));
                            } else {
                                FireFlowerList.add(new FireFlower(entityLeftCol * Game.TILES_SIZE, (entityTopRow) * Game.TILES_SIZE - 5, FIRE_FLOWER));
                            }
                        } else if (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(115)) {
                            lvl[entityTopRow][entityRightCol] = 152;
                            if (playerStatus == SMALL) {
                                MushroomList.add(new Mushroom(entityRightCol * Game.TILES_SIZE + 3, (entityTopRow) * Game.TILES_SIZE - 5, MUSHROOM));
                            } else {
                                FireFlowerList.add(new FireFlower(entityRightCol * Game.TILES_SIZE, (entityTopRow) * Game.TILES_SIZE - 5, FIRE_FLOWER));
                            }
                        }
                        collision = true;
                    }

                    //WALKING COLLISIONS

                    //CHECKING IF NEXT BLOCK EXISTS
                    if (x - Game.TILES_SIZE > 0) {
                        //COLLISION WHILE MOVING
                        if (levelManager.sprites.get(lvl[entityTopRow][entityLeftCol - 1]) != levelManager.sprites.get(90)
                                || levelManager.sprites.get(lvl[entityBottomRow][entityLeftCol - 1]) != levelManager.sprites.get(90)) {
                            distanceX = entityLeftX - (entityLeftCol) * Game.TILES_SIZE;
                            if (distanceX <= 5) {
                                rightPlayerSprint = 1;
                                leftPlayerSprint = 1;
                                collision = true;
                            }
                        }
                        //COLLISION WHEN JUMPING PLAYER HITS BLOCK IN HALF
                        if (inAir && levelManager.sprites.get(lvl[entityMiddleRow][entityLeftCol - 1]) != levelManager.sprites.get(90)) {
                            distanceX = entityLeftX - (entityLeftCol) * Game.TILES_SIZE;
                            if (distanceX <= 5) {
                                leftPlayerSprint = 0;
                            }
                        }
                        //COLLISION WHILE SLIDING
                        if (rightPlayerSprint > minSprint) {
                            if (levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90)) {
                                rightPlayerSprint = minSprint;
                                collision = true;
                            }
                        }
                    } else if (x - Game.TILES_SIZE / 6.0f < 0) {
                        rightPlayerSprint = 0;
                        leftPlayerSprint = 0;
                        collision = true;
                    }
                    //MOVING PLAYER FURTHER IF HE HAS BLOCK ABOVE WHILE DUCKING
                    if (!inAir && playerStatus != SMALL) {
                        if (levelManager.sprites.get(tileNum4) != levelManager.sprites.get(90)) {
                            x--;
                            blockMovement = true;
                        }
                        if (levelManager.sprites.get(tileNum1) != levelManager.sprites.get(90) && levelManager.sprites.get(tileNum4) == levelManager.sprites.get(90)) {
                            x++;
                            blockMovement = true;
                        }
                    }

                    //PLAYER IN AIR COLLISIONS

                    //COLLISION WHEN FALLING
                    if (inAir && (y < Game.GAME_HEIGHT - 3*Game.TILES_SIZE || playerStatus == SMALL)
                            && (levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90))) {
                        distanceY = (entityBottomRow) * Game.TILES_SIZE - entityBottomY;
                        if (distanceY < 0) {
                            y = entityTopRow * Game.TILES_SIZE + 8;
                            jump = false;
                            inAir = false;
                            airSpeed = 0;
                        }
                    }
                    //COLLISION WHEN JUMPING
                    if (inAir
                            && (levelManager.sprites.get(tileNum1) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum4) != levelManager.sprites.get(90))) {
                        distanceY = (entityTopRow) * Game.TILES_SIZE - entityTopY;
                        if (distanceY < 0) {
                            y = (entityTopRow + 1) * Game.TILES_SIZE;
                            airSpeed = 0;
                        }
                    }
                    //FALLING
                    if (y < Game.GAME_HEIGHT - 3 * Game.TILES_SIZE || playerStatus == SMALL) {
                        if (levelManager.sprites.get(lvl[entityBottomRow + 1][entityRightCol]) == levelManager.sprites.get(90)
                                && levelManager.sprites.get(lvl[entityBottomRow + 1][entityLeftCol]) == levelManager.sprites.get(90)
                                && levelManager.sprites.get(lvl[entityBottomRow][entityRightCol]) == levelManager.sprites.get(90)
                                && levelManager.sprites.get(lvl[entityBottomRow][entityLeftCol]) == levelManager.sprites.get(90)) {
                            inAir = true;
                        }
                    }
                }
            }
        }
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

    public boolean isRight() {
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

    public boolean isBlockMovement() {
        return blockMovement;
    }
}