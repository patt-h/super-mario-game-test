package Entities;

import Levels.Playing;
import Utilities.LoadSave;
import com.company.Game;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import static Utilities.Constants.PlayerConstants.*;
import static Utilities.Constants.Directions.*;

public class Player extends Entity{
    private BufferedImage[][] animations;
    private int animationTick, animationIndex, animationSpeed = 25;
    private float playerSpeed = 1.0f;
    public static float minSprint = 1.0f;
    private float maxSprint = 2.0f;
    public static float leftPlayerSprint;
    public static float rightPlayerSprint;
    private int playerAction = BIG_MARIO_IDLE;
    private boolean moving = false;
    public static boolean jumping = false;
    private boolean ducking = false;
    public static boolean turning = false;
    private boolean left, up, right, down, jump, duck, sprint;
    private int accurateAnimationRow;

    //JUMPING
    public static float airSpeed = 0.0f;
    public static float gravity = 0.04f * Game.SCALE;
    private float jumpSpeed = -2.25f * Game.SCALE;
    public static boolean inAir = false;


    public Player(int x, int y) {
        super(x, y);
        loadAnimation();
        direction = RIGHT;
        hitbox = new Rectangle2D.Float();
        hitbox.width = 11 * Game.SCALE;
        hitbox.height = 30 * Game.SCALE;
    }

    public void update() {
        updateAnimationTick();
        setAnimation();
        updatePosition();
    }
    public void render(Graphics g) {
        //MIRROR REFLECTION
        BufferedImage img = animations[accurateAnimationRow][animationIndex];
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-img.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        img = op.filter(img,null);

        if (direction == RIGHT) {
            if (animations[accurateAnimationRow][animationIndex] == animations[3][3]) {
                g.drawImage(animations[accurateAnimationRow][animationIndex],(int)x-17,(int)y+12,120,120,null);
            } else if (animations[accurateAnimationRow][animationIndex] == animations[2][4]) {
                g.drawImage(img, (int)x-71, (int)y+3, 120, 120, null);
            }
            else {
                g.drawImage(animations[accurateAnimationRow][animationIndex], (int)x-5, (int)y, 120, 120, null);
            }
        }
        else if (direction == LEFT) {
            if (animations[accurateAnimationRow][animationIndex] == animations[3][3]) {
                g.drawImage(img,(int)x-69,(int)y+12,120,120,null);
            } else if (animations[accurateAnimationRow][animationIndex] == animations[2][4]) {
                g.drawImage(animations[accurateAnimationRow][animationIndex], (int)x, (int)y, 120, 120, null);
            }
            else {
                g.drawImage(img, (int)x-81, (int)y, 120, 120, null);
            }
        }

        g.setColor(Color.RED);
        //g.drawRect((int)x, (int)y, (int)hitbox.width, (int)(hitbox.height));
    }

    private void updateAnimationTick() {
        animationTick++;
        if (playerAction == SMALL_MARIO_IDLE || playerAction == SMALL_MARIO_WALK || playerAction == SMALL_MARIO_RUN || playerAction == SMALL_MARIO_JUMP) {
            accurateAnimationRow = 0;
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

        if (animationTick >= animationSpeed && playerAction != BIG_MARIO_DUCK && playerAction != BIG_MARIO_JUMP && playerAction != BIG_MARIO_TURN) {
            animationTick = 0;
            animationIndex++;
            if (animationIndex >= getSpriteAmount(playerAction))  {
                animationIndex = 0;
            }
        }
    }

    private void setAnimation() {
        if (moving) {
            playerAction = BIG_MARIO_RUN;
        } else if (jumping) {
            playerAction = BIG_MARIO_JUMP;
        } else if (ducking) {
            playerAction = BIG_MARIO_DUCK;
        } else if (turning) {
            playerAction = BIG_MARIO_TURN;
        }
        else playerAction = BIG_MARIO_IDLE;
    }

    private void sprintCounter() {
        if (direction == LEFT || direction == RIGHT) {
            //ADDING UP SPRINT VALUE
            if ((sprint && leftPlayerSprint <= maxSprint) && (left)) {
                if (inAir) {
                    leftPlayerSprint += 0.001f;
                } else {
                    leftPlayerSprint += 0.01f;
                }
            }
            if ((sprint && rightPlayerSprint <= maxSprint) && (right)) {
                if (inAir) {
                    rightPlayerSprint += 0.001f;
                } else {
                    rightPlayerSprint += 0.01f;
                }
            }
            //SLOWING DOWN WHEN NOTHING IS PRESSED AND MARIO SPRINTED
            if (direction == LEFT && (!sprint && leftPlayerSprint > minSprint) || (!left && !right)) {
                leftPlayerSprint -= 0.01f;
            }
            if (direction == RIGHT && (!sprint && rightPlayerSprint > minSprint) || (!left && !right)) {
                rightPlayerSprint -= 0.01f;
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
        moving = false;
        jumping = false;
        ducking = false;
        turning = false;
        animationSpeed = 12;
        sprintCounter();
        Playing.checkCollisions();


        if (!Playing.collision) {
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
        if (jump) {
            jump();
        }
        if (inAir) {
            y += airSpeed;
            airSpeed += gravity;
            jumping = true;
            moving = false;
            if (direction == LEFT) {
                rightPlayerSprint = minSprint;
            } else if (direction == RIGHT) {
                leftPlayerSprint = minSprint;
            }
        }
        //DUCKING
        if (duck && !jump) {
            ducking = true;
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

    private void loadAnimation() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

        animations = new BufferedImage[11][10];
        for (int j = 0; j < animations.length; j++) {
            for (int i = 0; i < animations[j].length; i++) {
                animations[j][i] = img.getSubimage(i * 40, j * 40, 40, 40);
                if (animations[j][i] == animations[3][3]) {
                    animations[j][i] = img.getSubimage((i * 40) - 1, j * 40, 40, 40);
                }
            }
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
}

