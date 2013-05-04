package slick2dunixgame;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Transform;

/**
 *
 * @author chris
 */
public class Projectile
{
    public static final int N  = 0; 
    public static final int W  = 1;
    public static final int E  = 2;
    public static final int S  = 3;
    public static final int NE = 4; 
    public static final int SE = 5;
    public static final int SW = 6;
    public static final int NW = 7;
    private static final double diagPenalty = 1.414;
    
    private final float     MS_PER_PIXEL = (float)2;
    private final int       PROJECTILE_LENGTH = 25;
    private final int       PROJECTILE_WIDTH = 2;
    private final int       BASE_DAMAGE = 100;
    private final int       MAX_CRIT_DMG = 20;
    
    private int             DIRECTION = -1;
    
    private Rectangle       HITBOX;
    private Shape           ROTATED_HITBOX;
    
    public boolean          remove = false;
    
    int tx = 0;
    int ty = 0;
    int tw = 0;
    int th = 0;
    int dmgDone = 0;
    int targetx = 0;
    int targety = 0;
    
    public Projectile(int x, int y)
    {
        DIRECTION = Player.FACING;
        switch(DIRECTION)
        {
            case  N:
            case NE:
            case NW: HITBOX = new Rectangle(x - PROJECTILE_WIDTH/2, 
                                  y - PROJECTILE_LENGTH, PROJECTILE_WIDTH,
                                  PROJECTILE_LENGTH);
                break;
            case  S:
            case SE:
            case SW: HITBOX = new Rectangle(x - PROJECTILE_WIDTH/2, 
                                  y, PROJECTILE_WIDTH, PROJECTILE_LENGTH);
                break;
            case E: HITBOX = new Rectangle(x, y - PROJECTILE_WIDTH/2,
                                 PROJECTILE_LENGTH, PROJECTILE_WIDTH);
                break;
            case W: HITBOX = new Rectangle(x - PROJECTILE_LENGTH, 
                                 y - PROJECTILE_WIDTH/2, PROJECTILE_LENGTH, 
                                 PROJECTILE_WIDTH);
                break;
        }
    }
    
    public void update(int delta)
    {   
        if(!remove)
        {
            double toAddX = 0;
            double toAddY = 0;

            switch(DIRECTION)
            {
                case  N: toAddY -= delta/MS_PER_PIXEL;
                    break;
                case NE: toAddY -= (delta/MS_PER_PIXEL)/diagPenalty;
                         toAddX += (delta/MS_PER_PIXEL)/diagPenalty;
                    break;
                case NW: toAddY -= (delta/MS_PER_PIXEL)/diagPenalty;
                         toAddX -= (delta/MS_PER_PIXEL)/diagPenalty;
                    break;
                case  S: toAddY += delta/MS_PER_PIXEL;
                    break;
                case SE: toAddY += (delta/MS_PER_PIXEL)/diagPenalty;
                         toAddX += (delta/MS_PER_PIXEL)/diagPenalty;
                    break;
                case SW: toAddY += (delta/MS_PER_PIXEL)/diagPenalty;
                         toAddX -= (delta/MS_PER_PIXEL)/diagPenalty;
                    break;
                case  E: toAddX += delta/MS_PER_PIXEL;
                    break;
                case  W: toAddX -= delta/MS_PER_PIXEL;
                    break;

            }

            while((Math.abs(toAddX) > 1 || Math.abs(toAddY) > 1) && !remove)
            {

               if(toAddX >= 1)
                {
                    HITBOX.setX(HITBOX.getX() + 1);
                    toAddX -= 1;
                } 
                else if(toAddX <= -1)
                {
                    HITBOX.setX(HITBOX.getX() - 1);
                    toAddX += 1;
                }

                if(toAddY >= 1)
                {
                    HITBOX.setY(HITBOX.getY() + 1);
                    toAddY -= 1;
                }
                else if(toAddY <= -1)
                {
                    HITBOX.setY(HITBOX.getY() - 1);
                    toAddY += 1;
                }

                checkHit();
                if(isCollision())
                    remove = true;
            }// end while  
        }
    }
    
    public void draw(Graphics g)
    {
        if(!remove)
        {
            
            switch(DIRECTION)
            {
                case  N: World.sArrow.draw(HITBOX.getX(), HITBOX.getY());
                         break;
                case NE: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 45);
                         World.sArrow.draw(HITBOX.getX(), HITBOX.getY());
                         g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -45);
                         break;
                case  E: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 90);
                         World.sArrow.draw(HITBOX.getX(), HITBOX.getY());
                         g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -90);
                         break;
                case SE: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 135);
                         World.sArrow.draw(HITBOX.getX(), HITBOX.getY());
                         g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -135);
                         break;
                case  S: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 180);
                         World.sArrow.draw(HITBOX.getX(), HITBOX.getY());
                         g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -180);
                         break;
                case SW: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -135);
                         World.sArrow.draw(HITBOX.getX(), HITBOX.getY());
                         g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 135);
                         break;
                case  W: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -90);
                         World.sArrow.draw(HITBOX.getX(), HITBOX.getY());
                         g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 90);
                         break;
                case NW: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -45);
                         World.sArrow.draw(HITBOX.getX(), HITBOX.getY());
                         g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 45);
                         break;
            }
           /* g.setColor(Color.cyan);
            switch(DIRECTION)
            {
                case NE: g.fill(HITBOX.transform(Transform.createRotateTransform(
                                (float)Math.PI/4, HITBOX.getCenterX(), 
                                HITBOX.getY() + PROJECTILE_LENGTH)));
                    break;
                case SW: g.fill(HITBOX.transform(Transform.createRotateTransform(
                                (float)Math.PI/4, HITBOX.getCenterX(), 
                                HITBOX.getY())));
                    break;
                case SE:g.fill(HITBOX.transform(Transform.createRotateTransform(
                                -(float)Math.PI/4, HITBOX.getCenterX(), 
                                HITBOX.getY())));
                    break;
                case NW: g.fill(HITBOX.transform(Transform.createRotateTransform(
                                -(float)Math.PI/4, HITBOX.getCenterX(), 
                                HITBOX.getY() + PROJECTILE_LENGTH)));
                    break;

                default: g.fill(HITBOX);
                    break;
            }
            * 
            */
        }
    }
    
    
    
    private boolean isCollision()
    {
        if(HITBOX.getMinX() < 32 || HITBOX.getMaxX() > 1248 ||
                HITBOX.getMinY() < 32 || HITBOX.getMaxY() > 928)
            return true;
         
        rotateHitbox();
        
        tx = (int)Math.floor((ROTATED_HITBOX.getMinX()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileWidth());
        tw = ((int)Math.ceil((ROTATED_HITBOX.getMaxX()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileWidth()) - tx);
        
        ty = (int)Math.floor((ROTATED_HITBOX.getMinY()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileHeight());
        th = ((int)Math.ceil((ROTATED_HITBOX.getMaxY()) /
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
    
    
    private void checkHit()
    {
        rotateHitbox();
        tx = (int)Math.floor((ROTATED_HITBOX.getMinX()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileWidth());
        tw = ((int)Math.ceil((ROTATED_HITBOX.getMaxX()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileWidth()) - tx);
        
        ty = (int)Math.floor((ROTATED_HITBOX.getMinY()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileHeight());
        th = ((int)Math.ceil((ROTATED_HITBOX.getMaxY()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileHeight()) - ty);   
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
                    remove = true;
                }
            }
        }
        for(int i = 0; i < World.AGGRESSOR_LIST.get(
                World.CURRENT_WORLD_INDEX).size(); ++i)
        {
            if(ROTATED_HITBOX.intersects(
                    World.AGGRESSOR_LIST.get(World.CURRENT_WORLD_INDEX).get(i).HITBOX))
            {
                int dmg = BASE_DAMAGE + (int)(Math.random()*MAX_CRIT_DMG);
                
                World.AGGRESSOR_LIST.get(
                        World.CURRENT_WORLD_INDEX).get(i).HEALTH -= dmg;
                if(World.AGGRESSOR_LIST.get(
                        World.CURRENT_WORLD_INDEX).get(i).HEALTH <= 0)
                {
                    //World.AGGRESSOR_LIST.get(i).dropItem();
                    World.AGGRESSOR_LIST.get(
                            World.CURRENT_WORLD_INDEX).get(i).isDEAD = true;
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
                remove = true;
            }
        }
        for(int i = 0; i < World.WANDERER_LIST.get(
                World.CURRENT_WORLD_INDEX).size(); ++i)
        {
            if(ROTATED_HITBOX.intersects(
                    World.WANDERER_LIST.get(
                    World.CURRENT_WORLD_INDEX).get(i).HITBOX))
            {
                int dmg = BASE_DAMAGE + (int)(Math.random()*MAX_CRIT_DMG);
                
                World.WANDERER_LIST.get(
                        World.CURRENT_WORLD_INDEX).get(i).HEALTH -= dmg;
                if(World.WANDERER_LIST.get(
                        World.CURRENT_WORLD_INDEX).get(i).HEALTH <= 0)
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
                remove = true;
            }
        }
        
        if(remove && dmgDone > 0)
        {
            World.DAMAGE_DISPLAY.add(new LayeredText(
                    Integer.toString(dmgDone), targetx, targety));            
        }
    }
    
    private void rotateHitbox()
    {
        switch(DIRECTION)
        {
            case NE: ROTATED_HITBOX = HITBOX.transform(
                    Transform.createRotateTransform(
                            (float)Math.PI/4, HITBOX.getCenterX(), 
                            HITBOX.getY() + PROJECTILE_LENGTH));
                break;
            case SW: ROTATED_HITBOX = HITBOX.transform(
                    Transform.createRotateTransform(
                            (float)Math.PI/4, HITBOX.getCenterX(), 
                            HITBOX.getY()));
                break;
            case SE: ROTATED_HITBOX = HITBOX.transform(
                    Transform.createRotateTransform(
                            -(float)Math.PI/4, HITBOX.getCenterX(), 
                            HITBOX.getY()));
                break;
            case NW: ROTATED_HITBOX = HITBOX.transform(
                    Transform.createRotateTransform(
                            -(float)Math.PI/4, HITBOX.getCenterX(), 
                            HITBOX.getY() + PROJECTILE_LENGTH));
                break;
                
            default: ROTATED_HITBOX = HITBOX;
                break;
        }
    }
}
