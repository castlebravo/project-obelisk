package slick2dunixgame;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Transform;

/**
 *
 * @author ben
 */
public class Weapon
{
    public static final int N  = 0; 
    public static final int W  = 1;
    public static final int E  = 2;
    public static final int S  = 3;
    public static final int NE = 4; 
    public static final int SE = 5;
    public static final int SW = 6;
    public static final int NW = 7;
    
    private final float     MS_PER_DEGREE = (float)2.3;
    private final double    COLLISION_SCALE = (float)5/(float)8;
    private final int       SWING_ANGLE = 80;
    private final int       BLADE_LENGTH = 40;
    private final int       BLADE_WIDTH = 5;
    private final int       BASE_DAMAGE = 10;
    private final int       MAX_CRIT_DMG = 2;
    private final int       MS_COOLDOWN = 250;
    
    public static int   ANGLE_START = 0;
    public static int   ANGLE_END = 0;
    public static int   SWING_DIRECTION = -1;
    
    private Rectangle   HITBOX;
    private Rectangle   COLLISIONBOX;
    private Shape       ROTATED_HITBOX;
    private Shape       ROTATED_COLLISIONBOX;
    
    boolean isSwinging = false;
    boolean isOnCooldown = false;
    
    int tx = 0;
    int ty = 0;
    int tw = 0;
    int th = 0;
    int dmgDone = 0;
    int targetx = 0;
    int targety = 0;
    
    int msSinceReset = 0;
    
    public Weapon()
    { 
        HITBOX = new Rectangle(Player.HITBOX.getCenterX(), 
                Player.HITBOX.getCenterY() - BLADE_WIDTH,
                BLADE_LENGTH, BLADE_WIDTH);
        COLLISIONBOX = new Rectangle(Player.HITBOX.getCenterX(), 
                Player.HITBOX.getCenterY() - BLADE_WIDTH,
                (float)(BLADE_LENGTH*COLLISION_SCALE), BLADE_WIDTH);
    }
    
    public void draw(Graphics g)
    {                
        if(isSwinging)
        {
            g.setColor(Color.gray);
            g.fill(HITBOX.transform(Transform.createRotateTransform(
                (float)Math.toRadians(ANGLE_START),Player.HITBOX.getCenterX(),
                Player.HITBOX.getCenterY())));
            
         //   g.setColor(Color.blue);
         //   g.fill(COLLISIONBOX.transform(Transform.createRotateTransform(
           //     (float)Math.toRadians(ANGLE_START),Player.HITBOX.getCenterX(),
             //   Player.HITBOX.getCenterY())));
            
            
            //World.sSword.s
            //World.sSword.setCenterOfRotation(1.5f, -22.5f);
            //World.sSword.rotate(ANGLE_START);
            //World.sSword.rotate(-10);
            //World.sSword.draw(Player.HITBOX.getCenterX(), Player.HITBOX.getCenterY());
            //World.sSword.rotate(-ANGLE_START);
            
        }
    }
    
    public void update(boolean start, int delta)
    {        
        HITBOX.setBounds(Player.HITBOX.getCenterX(), Player.HITBOX.getCenterY(),
                BLADE_LENGTH, BLADE_WIDTH);
        COLLISIONBOX.setBounds(Player.HITBOX.getCenterX(), Player.HITBOX.getCenterY(),
                (float)(BLADE_LENGTH*COLLISION_SCALE), BLADE_WIDTH);

        if(isOnCooldown)
        {
            msSinceReset += delta;
            if(msSinceReset >= MS_COOLDOWN)
            {
                msSinceReset = 0;
                isOnCooldown = false;
            }
        }
        
        if(start && !isSwinging && !isOnCooldown)
        {
            isSwinging = true;
            isOnCooldown = true;
            SWING_DIRECTION = Player.FACING;
            
            if(Player.FACING == N)
                ANGLE_START = -(180-SWING_ANGLE)/2;
            else if(Player.FACING == NE)
                ANGLE_START = -(180-SWING_ANGLE)/2 + 45;
            else if(Player.FACING == NW)
                ANGLE_START = -(180-SWING_ANGLE)/2 - 45;
            else if(Player.FACING == E)
                ANGLE_START = -(180-SWING_ANGLE)/2 + 90;
            else if(Player.FACING == W)
                ANGLE_START = -(180-SWING_ANGLE)/2 - 90;
            else if(Player.FACING == SW)
                ANGLE_START = -(180-SWING_ANGLE)/2 - 135;
            else if(Player.FACING == SE)
                ANGLE_START = -(180-SWING_ANGLE)/2 + 135;
            else if(Player.FACING == S)
                ANGLE_START = -(180-SWING_ANGLE)/2 - 180;
            
            ANGLE_END = ANGLE_START - SWING_ANGLE;
        }
        
        if(isSwinging)
        {
            //disabled, not functioning as intended
          //  if(SWING_DIRECTION != Player.FACING)
            //    resetSwing();
            
            if(ANGLE_START > ANGLE_END)
            {
                ANGLE_START -= (delta / MS_PER_DEGREE);
                
                ROTATED_HITBOX = HITBOX.transform(Transform.createRotateTransform(
                   (float)Math.toRadians(ANGLE_START),Player.HITBOX.getCenterX(),
                   Player.HITBOX.getCenterY()));
                
                ROTATED_COLLISIONBOX = COLLISIONBOX.transform(Transform.createRotateTransform(
                   (float)Math.toRadians(ANGLE_START),Player.HITBOX.getCenterX(),
                   Player.HITBOX.getCenterY()));
                
                checkHit();
                if(isCollision())
                    resetSwing();
            }
            else
                resetSwing();
        }
    } 
    
    private void checkHit()
    {
        tx = (int)Math.floor((ROTATED_HITBOX.getMinX()) /
                Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getTileWidth());
        tw = ((int)Math.ceil((ROTATED_HITBOX.getMaxX()) /
                Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getTileWidth()) - tx);
        
        ty = (int)Math.floor((ROTATED_HITBOX.getMinY()) /
                Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getTileHeight());
        th = ((int)Math.ceil((ROTATED_HITBOX.getMaxY()) /
                Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getTileHeight()) - ty);   
        for(int i = tx; i < tx + tw; ++i)
        {
            for(int j = ty; j < ty + th; ++j)
            {
                if(Slick2DUnixGame.WORLD.overlay.get(
                        World.CURRENT_WORLD_INDEX)[i][j].DESTRUCTABLE &&
                        !Slick2DUnixGame.WORLD.overlay.get(
                        World.CURRENT_WORLD_INDEX)[i][j].DESTROYED)
                {
                    int dmg = BASE_DAMAGE + (int)(Math.random()*MAX_CRIT_DMG);
                    
                    Slick2DUnixGame.WORLD.overlay.get(
                            World.CURRENT_WORLD_INDEX)[i][j].HEALTH -= dmg;
                    if(Slick2DUnixGame.WORLD.overlay.get(
                            World.CURRENT_WORLD_INDEX)[i][j].HEALTH < 0)
                    {
                        Slick2DUnixGame.WORLD.overlay.get(
                                World.CURRENT_WORLD_INDEX)[i][j].DESTROYED = true;
                        Slick2DUnixGame.WORLD.overlay.get(
                                World.CURRENT_WORLD_INDEX)[i][j].HAS_HEALTH = true;
                        dmgDone += (dmg + Slick2DUnixGame.WORLD.overlay.get(
                                World.CURRENT_WORLD_INDEX)[i][j].HEALTH);
                    }
                    else
                        dmgDone += dmg;
                    targetx = 32*i;
                    targety = 32*j;
                }
            }
        }
        for(int i = 0; i < World.AGGRESSOR_LIST.get(
                World.CURRENT_WORLD_INDEX).size(); ++i)
        {
            if(ROTATED_HITBOX.intersects(World.AGGRESSOR_LIST.get(
                    World.CURRENT_WORLD_INDEX).get(i).HITBOX))
            {
                int dmg = BASE_DAMAGE + (int)(Math.random()*MAX_CRIT_DMG);
                
                World.AGGRESSOR_LIST.get(World.CURRENT_WORLD_INDEX).get(i).HEALTH -= dmg;
                if(World.AGGRESSOR_LIST.get(World.CURRENT_WORLD_INDEX).get(i).HEALTH < 0)
                {
                    //World.AGGRESSOR_LIST.get(i).dropItem();
                    World.AGGRESSOR_LIST.get(World.CURRENT_WORLD_INDEX).get(i).isDEAD = true;
                    Player.SCORE += World.AGGRESSOR_LIST.get(
                            World.CURRENT_WORLD_INDEX).get(i).AGGRESSOR_VALUE;
                    dmgDone += (dmg + World.AGGRESSOR_LIST.get(
                            World.CURRENT_WORLD_INDEX).get(i).HEALTH);
                }
                else
                    dmgDone += dmg;
                targetx = (int)World.AGGRESSOR_LIST.get(
                        World.CURRENT_WORLD_INDEX).get(i).HITBOX.getCenterX();
                targety = (int)World.AGGRESSOR_LIST.get(
                        World.CURRENT_WORLD_INDEX).get(i).HITBOX.getCenterY();
            }
        }
        for(int i = 0; i < World.WANDERER_LIST.get(
                World.CURRENT_WORLD_INDEX).size(); ++i)
        {
            if(ROTATED_HITBOX.intersects(World.WANDERER_LIST.get(
                    World.CURRENT_WORLD_INDEX).get(i).HITBOX))
            {
                int dmg = BASE_DAMAGE + (int)(Math.random()*MAX_CRIT_DMG);
                
                World.WANDERER_LIST.get(
                        World.CURRENT_WORLD_INDEX).get(i).HEALTH -= dmg;
                if(World.WANDERER_LIST.get(
                        World.CURRENT_WORLD_INDEX).get(i).HEALTH < 0)
                {
                    World.WANDERER_LIST.get(
                            World.CURRENT_WORLD_INDEX).get(i).dropItem();
                    World.WANDERER_LIST.get(
                            World.CURRENT_WORLD_INDEX).get(i).isDEAD = true;
                    dmgDone += (dmg + World.WANDERER_LIST.get(
                            World.CURRENT_WORLD_INDEX).get(i).HEALTH);
                }
                else
                    dmgDone += dmg;
                targetx = (int)World.WANDERER_LIST.get(
                        World.CURRENT_WORLD_INDEX).get(i).HITBOX.getCenterX();
                targety = (int)World.WANDERER_LIST.get(
                        World.CURRENT_WORLD_INDEX).get(i).HITBOX.getCenterY();
            }
        }
    }    

    
    private boolean isCollision()
    {
        tx = (int)Math.floor((ROTATED_COLLISIONBOX.getMinX()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileWidth());
        tw = ((int)Math.ceil((ROTATED_COLLISIONBOX.getMaxX()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileWidth()) - tx);
        
        ty = (int)Math.floor((ROTATED_COLLISIONBOX.getMinY()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileHeight());
        th = ((int)Math.ceil((ROTATED_COLLISIONBOX.getMaxY()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileHeight()) - ty);        
        
        for(int i = tx; i < tx + tw; ++i)
        {
            for(int j = ty; j < ty + th; ++j)
            {
                if(Slick2DUnixGame.WORLD.overlay.get(
                        World.CURRENT_WORLD_INDEX)[i][j].COLLIDEABLE)
                    return true;
            }
        }
        return false;
    }    
    
    private void resetSwing()
    {
        if(dmgDone > 0)
        {
            World.DAMAGE_DISPLAY.add(new LayeredText(
                    Integer.toString(dmgDone), targetx, targety));
            dmgDone = 0;
        }
        
        isSwinging = false;
        ANGLE_START = 0;
        SWING_DIRECTION = -1;
        dmgDone = 0;
    }
}
