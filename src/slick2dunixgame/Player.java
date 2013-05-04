package slick2dunixgame;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;
import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class Player
{
    public static final int N  = 0; 
    public static final int W  = 1;
    public static final int E  = 2;
    public static final int S  = 3;
    public static final int NE = 4; 
    public static final int SE = 5;
    public static final int SW = 6;
    public static final int NW = 7;
    public static final double MS_PER_PIXEL = 4;
    
    private static final double diagPenalty = 1.414;
    
    static public boolean UP;
    static public boolean DOWN;
    static public boolean LEFT;
    static public boolean RIGHT;
    static public boolean isDEAD = false;
    
    static public boolean START_SWORD = false;
    static public boolean START_BOW = false;
    
    public static Rectangle HITBOX = new Rectangle(
              (Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getWidth()*
               Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getTileWidth())/2,
              (Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getHeight()*
               Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getTileHeight())/2,
               Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getTileWidth()-2, 
               Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getTileWidth()-2);
    static int FACING = N;
    
    public static final int START_HEALTH = 3000;
    public static final int PROJECTILE_COOLDOWN = 750;
    
    public static int HEALTH = START_HEALTH;
    public static int SCORE = 0;
    
    public static int MS_SINCE_ARROW = 0;
    
    static Weapon sword = new Weapon();
    
    public static ArrayList<Projectile> ARROWS = new ArrayList<Projectile>();
    
    private static int tx,ty,th,tw;
         
    public Player()
    {
        UP = false;
        DOWN = false;
        LEFT = false;
        RIGHT = false;
    }
    
    public static void update(int delta)
    {   
        if(HEALTH <= 0)
        {
            isDEAD = true;
            GameplayState.FLAG_MENU = true;
        }
        else
        {
            if(HITBOX.getMinX() < 32 || HITBOX.getMaxX() > 1248 ||
                HITBOX.getMinY() < 32 || HITBOX.getMaxY() > 928)
            {
                World.transitionWorlds(HITBOX.getX(), HITBOX.getY());
            }
                
                
            if(GameplayState.ENABLE_MOUSE_MOVEMENT)
                pointToDirection();

            double toAddX = 0;
            double toAddY = 0;

            sword.update(START_SWORD, delta);
            checkItemDrops();
            
            int i = 0;
            while(i < ARROWS.size())
            {
                if(ARROWS.get(i).remove)
                    ARROWS.remove(i);
                else
                {
                    ARROWS.get(i).update(delta);
                    ++i;
                }
            }
            
            if(START_BOW && MS_SINCE_ARROW <= 0)
            {
                ARROWS.add(new Projectile((int)HITBOX.getCenterX(), 
                        (int)HITBOX.getCenterY()));
                MS_SINCE_ARROW = PROJECTILE_COOLDOWN;
            }
            else
                MS_SINCE_ARROW -= delta;

            boolean stopX = false;
            boolean stopY = false;
            double prevX = 0;
            double prevY = 0;

            if(UP || DOWN || LEFT || RIGHT)
            {       
                if((UP && !DOWN && !LEFT && !RIGHT) ||
                        (UP && !DOWN && LEFT && RIGHT))
                {
                    FACING = N;
                    toAddY -= delta/MS_PER_PIXEL;
                }    
                else if(UP && !DOWN && !LEFT && RIGHT)
                {
                    FACING = NE;
                    toAddY -= (delta/MS_PER_PIXEL)/diagPenalty;
                    toAddX += (delta/MS_PER_PIXEL)/diagPenalty;
                }
                else if((!UP && !DOWN && !LEFT && RIGHT) ||
                        (UP && DOWN && !LEFT && RIGHT))
                {
                    FACING = E;
                    toAddX += delta/MS_PER_PIXEL;
                }
                else if(!UP && DOWN && !LEFT && RIGHT)
                {
                    FACING = SE;
                    toAddY += (delta/MS_PER_PIXEL)/diagPenalty;
                    toAddX += (delta/MS_PER_PIXEL)/diagPenalty;
                }    
                else if((!UP && DOWN && !LEFT && !RIGHT) ||
                        (!UP && DOWN && LEFT && RIGHT))
                {
                    FACING = S;
                    toAddY += delta/MS_PER_PIXEL;
                }
                else if(!UP && DOWN && LEFT && !RIGHT)
                {
                    FACING = SW;
                    toAddY += (delta/MS_PER_PIXEL)/diagPenalty;
                    toAddX -= (delta/MS_PER_PIXEL)/diagPenalty;
                }
                else if((!UP && !DOWN && LEFT && !RIGHT) ||
                        (UP && DOWN && LEFT && !RIGHT))
                {    
                    FACING = W;
                    toAddX -= delta/MS_PER_PIXEL;
                }
                else if(UP && !DOWN && LEFT && !RIGHT)
                {    
                    FACING = NW;
                    toAddY -= (delta/MS_PER_PIXEL)/diagPenalty;
                    toAddX -= (delta/MS_PER_PIXEL)/diagPenalty;
                }

                while(Math.abs(toAddX) > 1 || Math.abs(toAddY) > 1 || (stopX && stopY))
                {
                    prevX = HITBOX.getX();
                    prevY = HITBOX.getY();

                    if(!stopX)
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

                        if(isCollision())
                        {
                            HITBOX.setX((float)prevX);
                            stopX = true;
                            toAddX = 0;
                        }
                    }

                    if(!stopY)
                    {
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

                        if(isCollision())
                        {
                            HITBOX.setY((float)prevY); 
                            stopY = true;
                            toAddY = 0;
                        }
                    }
                    if(stopX && stopY)
                        break;
                }// end while


                if(HITBOX.getCenterX() > Slick2DUnixGame.WINDOW_WIDTH/2 && 
                   HITBOX.getCenterX() < (Slick2DUnixGame.WORLD.map.get(
                        World.CURRENT_WORLD_INDEX).getWidth()*
                   Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getTileWidth()) - 
                   Slick2DUnixGame.WINDOW_WIDTH/2)

                    GameplayState.offsetX = -(HITBOX.getCenterX() - 
                            Slick2DUnixGame.WINDOW_WIDTH/2);

                if(HITBOX.getCenterY() > Slick2DUnixGame.WINDOW_HEIGHT/2 && 
                   HITBOX.getCenterY() < (Slick2DUnixGame.WORLD.map.get(
                        World.CURRENT_WORLD_INDEX).getHeight()*
                   Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getTileHeight()) - 
                   Slick2DUnixGame.WINDOW_HEIGHT/2)

                    GameplayState.offsetY = -(HITBOX.getCenterY() - 
                            Slick2DUnixGame.WINDOW_HEIGHT/2);
            }
        }
    }
    
    public static void draw(Graphics g)
    {
        for(int i = 0; i < ARROWS.size(); ++i)
        {
            ARROWS.get(i).draw(g);
        }
        
        sword.draw(g);
          
        switch(FACING)
        {
            case  N: World.sPlayer.draw(HITBOX.getX(), HITBOX.getY());
                break;
            case NE: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 45);
                     World.sPlayer.draw(HITBOX.getX(), HITBOX.getY());
                     g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -45);
                break;
            case  E: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 90);
                     World.sPlayer.draw(HITBOX.getX(), HITBOX.getY());
                     g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -90);
                break;
            case SE: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 135);
                     World.sPlayer.draw(HITBOX.getX(), HITBOX.getY());
                     g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -135);
                break;
            case  S: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 180);
                     World.sPlayer.draw(HITBOX.getX(), HITBOX.getY());
                     g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -180);
                break;
            case SW: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -135);
                     World.sPlayer.draw(HITBOX.getX(), HITBOX.getY());
                     g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 135);
                break;
            case  W: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -90);
                     World.sPlayer.draw(HITBOX.getX(), HITBOX.getY());
                     g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 90);
                break;
            case NW: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -45);
                     World.sPlayer.draw(HITBOX.getX(), HITBOX.getY());
                     g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 45);
                break;
        }
            
        g.setColor(Color.black);
        if(GameplayState.ENABLE_MOUSE_MOVEMENT)
            g.drawLine(HITBOX.getCenterX(), HITBOX.getCenterY(),
                       GameplayState.MOUSE_POINT.x, GameplayState.MOUSE_POINT.y);
    }
    
    
    private static boolean isCollision()
    {
        for(int i = 0; i < World.AGGRESSOR_LIST.get(
                World.CURRENT_WORLD_INDEX).size(); ++i)
        {
            if(World.AGGRESSOR_LIST.get(
                    World.CURRENT_WORLD_INDEX).get(i).HITBOX.intersects(HITBOX))
                return true;
        }
        for(int i = 0; i < World.WANDERER_LIST.get(
                World.CURRENT_WORLD_INDEX).size(); ++i)
        {
            if(World.WANDERER_LIST.get(
                    World.CURRENT_WORLD_INDEX).get(i).HITBOX.intersects(HITBOX))
                return true;
        }
        
        if(Slick2DUnixGame.WORLD.overlay.get(World.CURRENT_WORLD_INDEX)
                [(int)HITBOX.getX() / Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileWidth()]
                [(int)HITBOX.getY() / Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileHeight()].COLLIDEABLE)
            return true;
        if(Slick2DUnixGame.WORLD.overlay.get(World.CURRENT_WORLD_INDEX)
                [(int)HITBOX.getX() / Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileWidth()]
                [((int)HITBOX.getY() + (int)HITBOX.getHeight()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileHeight()].COLLIDEABLE)
            return true;
        if(Slick2DUnixGame.WORLD.overlay.get(World.CURRENT_WORLD_INDEX)
                [((int)HITBOX.getX() + (int)HITBOX.getWidth()) / 
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileWidth()]
                [(int)HITBOX.getY() / Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileHeight()].COLLIDEABLE)
            return true;
        if(Slick2DUnixGame.WORLD.overlay.get(World.CURRENT_WORLD_INDEX)
                [((int)HITBOX.getX() + (int)HITBOX.getWidth()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileWidth()]
                [((int)HITBOX.getY() + (int)HITBOX.getHeight()) / 
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileHeight()].COLLIDEABLE)
            return true;
        
        return false;
    }    
    
    private static void pointToDirection()
    {
        int mx = GameplayState.MOUSE_POINT.x;
        int my = GameplayState.MOUSE_POINT.y;
        int x = (int)HITBOX.getCenterX();
        int y = (int)HITBOX.getCenterY();
        
        float BASE = 8;
        
        UP = false;
        DOWN = false;
        LEFT = false;
        RIGHT = false;
        
        if(GameplayState.NO_INPUT_ZONE.contains(mx, my))
        {
            UP = true;
            DOWN = true;
            LEFT = true;
            RIGHT = true;
        }
        else
        {
            double radians = (Math.atan2((float)(y - my) ,(float)(x - mx))) / Math.PI;
            
            if(radians >= (3/BASE) && radians < (5/BASE))
            {
                UP = true;
            }
            else if(radians >= (1/BASE) && radians < (3/BASE))
            {
                UP = true;
                LEFT = true;
            }
            else if((radians >= 0 && radians < (1/BASE)) || 
                    (radians <= 0 && radians > -(1/BASE)))
            {
                LEFT = true;
            }
            else if(radians <= -(1/BASE) && radians > -(3/BASE))
            {
                LEFT = true;
                DOWN = true;
            }
               
            else if(radians <= -(3/BASE) && radians > -(5/BASE))
            {
                DOWN = true;
            }
            else if(radians <= -(5/BASE) && radians > -(7/BASE))
            {
                DOWN = true;
                RIGHT = true;
            }
            else if((radians >= (7/BASE) && radians <= 1) ||
                    (radians <= -(7/BASE) && radians >= -1))
            {
                RIGHT = true;
            }
            else if(radians >= (5/BASE) && radians < (7/BASE))
            {
                RIGHT = true;
                UP = true;
            }
        }
    }
    
    private static void checkItemDrops()
    {
        tx = (int)Math.floor((HITBOX.getMinX()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileWidth());
        tw = ((int)Math.ceil((HITBOX.getMaxX()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileWidth()) - tx);
        
        ty = (int)Math.floor((HITBOX.getMinY()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileHeight());
        th = ((int)Math.ceil((HITBOX.getMaxY()) /
                Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileHeight()) - ty);        
        
        for(int i = tx; i < tx + tw; ++i)
        {
            for(int j = ty; j < ty + th; ++j)
            {
                //berry bush
                if(Slick2DUnixGame.WORLD.overlay.get(
                        World.CURRENT_WORLD_INDEX)[i][j].ID == 201 &&
                        HITBOX.intersects(Slick2DUnixGame.WORLD.overlay.get(
                        World.CURRENT_WORLD_INDEX)[i][j].HITBOX)
                        && Slick2DUnixGame.WORLD.overlay.get(
                        World.CURRENT_WORLD_INDEX)[i][j].HAS_HEALTH && 
                        HEALTH < START_HEALTH)
                {
                    HEALTH += Slick2DUnixGame.WORLD.overlay.get(
                            World.CURRENT_WORLD_INDEX)[i][j].HEALTH_GAIN;
                    Slick2DUnixGame.WORLD.overlay.get(
                            World.CURRENT_WORLD_INDEX)[i][j].HAS_HEALTH = false;
                    if(HEALTH > START_HEALTH)
                        HEALTH = START_HEALTH;
                }
                //coin
                else if(Slick2DUnixGame.WORLD.overlay.get(
                        World.CURRENT_WORLD_INDEX)[i][j].ID == 202 &&
                        HITBOX.intersects(Slick2DUnixGame.WORLD.overlay.get(
                        World.CURRENT_WORLD_INDEX)[i][j].HITBOX)
                        && !Slick2DUnixGame.WORLD.overlay.get(
                        World.CURRENT_WORLD_INDEX)[i][j].DESTROYED)
                {
                    SCORE += Slick2DUnixGame.WORLD.overlay.get(
                            World.CURRENT_WORLD_INDEX)[i][j].COIN_VALUE;
                    Slick2DUnixGame.WORLD.overlay.get(
                            World.CURRENT_WORLD_INDEX)[i][j].DESTROYED = true;
                }
            }
        }
        for(int i = 0; i < World.ITEM_DROPS.get(World.CURRENT_WORLD_INDEX).size(); ++i)
        {
            if(World.ITEM_DROPS.get(
                    World.CURRENT_WORLD_INDEX).get(i).SIZE.intersects(HITBOX) && 
                    !World.ITEM_DROPS.get(
                    World.CURRENT_WORLD_INDEX).get(i).remove && HEALTH < START_HEALTH)
            {
                HEALTH += World.ITEM_DROPS.get(
                        World.CURRENT_WORLD_INDEX).get(i).HEALTH_GAINED;
                World.ITEM_DROPS.get(
                        World.CURRENT_WORLD_INDEX).get(i).remove = true;
                if(HEALTH > START_HEALTH)
                    HEALTH = START_HEALTH;
            }
        }
    }
}