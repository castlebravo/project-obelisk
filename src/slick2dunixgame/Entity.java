package slick2dunixgame;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Transform;

import org.newdawn.slick.util.pathfinding.Path;

/**
 *
 * @author chris
 */
public class Entity
{
    public static final int N  = 0; 
    public static final int NE = 1;
    public static final int E  = 2;
    public static final int SE = 3;
    public static final int S  = 4;
    public static final int SW = 5;
    public static final int W  = 6;
    public static final int NW = 7;
    public static final int MS_PER_PIXEL = 5;
    
    public final int    START_HEALTH = 300;
    public final int    AGGRESSOR_VALUE = 10;
    private static final double diagPenalty = 1.414;
    
    public Rectangle    HITBOX;
    public Ellipse      AoE;
    private Ellipse     LoS;
    
    public int          HEALTH = START_HEALTH;
    public int          ID = -1;
    
    private int         FACING = -1;
    
    private Path        toPLAYER= new Path();
    
    private int         MS_SINCE_ACTION;
    private final int   MS_PER_ACTION = 3000;
    private final int   PX_PER_ACTION = 200;
    private final int   CYCLES_PER_NEW_PATH = 5;
    private final int   AoE_RADIUS = 40;
    private final int   LoS_RADIUS = 200;
    private final int   AoE_DAMAGE = 6;
    
    int toMove = 0;
    int toWait = (int)(Math.random()*MS_PER_ACTION); 
    
    int cycles = 0;
    
    public boolean isWandering = false;
    private boolean pathNotSet = true;
    private boolean notAggressive = true;
    public boolean isDEAD = false;
    
    
    public Entity(int x, int y, int id)
    {       
        ID = id;
        HITBOX = new Rectangle(x, y, 30, 30);
        FACING = N;
        MS_SINCE_ACTION = 0;
        //if aggressor
        if(ID == 102)
        {
            AoE = new Ellipse(HITBOX.getCenterX(), 
                    HITBOX.getCenterY(), AoE_RADIUS, AoE_RADIUS);
            LoS = new Ellipse(HITBOX.getCenterX(), 
                    HITBOX.getCenterY(), LoS_RADIUS, LoS_RADIUS);
        }
    }
    
    public void update(int delta)
    {       
        if(!isDEAD)
        {
            if(ID == 101)
                updateWander(delta);
            //else if aggressor
            else if(ID == 102)
            {
                if(notAggressive)
                    updateWander(delta);
                else
                {
                    if(pathNotSet)
                    {
                        toPLAYER = World.finder.findPath(null, (int)HITBOX.getX()/32, 
                            (int) HITBOX.getY()/32, (int)Player.HITBOX.getX()/32,
                            (int)Player.HITBOX.getY()/32);         
                        pathNotSet = false; 
                    }

                    if(cycles > CYCLES_PER_NEW_PATH)
                    {
                        toPLAYER = World.finder.findPath(null, (int)HITBOX.getX()/32, 
                            (int) HITBOX.getY()/32, (int)Player.HITBOX.getCenterX()/32,
                            (int)Player.HITBOX.getCenterY()/32);
                        cycles = 0;
                    }
                    else
                        cycles++;

                    double toAddX = 0;
                    double toAddY = 0;

                    boolean stopX = false;
                    boolean stopY = false;
                    double prevX = 0;
                    double prevY = 0;
                    double toMoveX = 0;
                    double toMoveY = 0;

                    if(toPLAYER != null && toPLAYER.getLength() >= 2)
                    {
                        toMoveX = toPLAYER.getX(1)*32 - HITBOX.getX();
                        toMoveY = toPLAYER.getY(1)*32 - HITBOX.getY();

                        if(Math.abs(toMoveX) < 10 && Math.abs(toMoveY) < 10)
                        {
                            if(toPLAYER.getLength() >= 4)
                            {
                                toMoveX = toPLAYER.getX(3)*32 - HITBOX.getX();
                                toMoveY = toPLAYER.getY(3)*32 - HITBOX.getY();
                            }
                            else
                            {
                                toMoveX = 0;
                                toMoveY = 0;
                            }
                        }
                    }

                    if(toMoveX == 0 && toMoveY < 0)
                    {
                        FACING = N;
                        toAddY -= delta/MS_PER_PIXEL;
                    }
                    else if(toMoveX < 0 && toMoveY < 0)
                    {
                        FACING = NW;
                        toAddY -= (delta/MS_PER_PIXEL)/diagPenalty;
                        toAddX -= (delta/MS_PER_PIXEL)/diagPenalty;
                    }
                    else if(toMoveX < 0 && toMoveY == 0)
                    {
                        FACING = W;
                        toAddX -= delta/MS_PER_PIXEL;
                    }
                    else if(toMoveX < 0 && toMoveY > 0)
                    {
                        FACING = SW;
                        toAddY += (delta/MS_PER_PIXEL)/diagPenalty;
                        toAddX -= (delta/MS_PER_PIXEL)/diagPenalty;
                    }
                    else if(toMoveX == 0 && toMoveY > 0)
                    {
                        FACING = S;
                        toAddY += delta/MS_PER_PIXEL;
                    }
                    else if(toMoveX > 0 && toMoveY > 0)
                    {
                        FACING = SE;
                        toAddY += (delta/MS_PER_PIXEL)/diagPenalty;
                        toAddX += (delta/MS_PER_PIXEL)/diagPenalty;
                    }
                    else if(toMoveX > 0 && toMoveY == 0)
                    {
                        FACING = E;
                        toAddX += delta/MS_PER_PIXEL;
                    }
                    else if(toMoveX > 0 && toMoveY < 0)
                    {
                        FACING = NE;
                        toAddY -= (delta/MS_PER_PIXEL)/diagPenalty;
                        toAddX += (delta/MS_PER_PIXEL)/diagPenalty;
                    }


                    while(Math.abs(toAddX) > 1 || Math.abs(toAddY) > 1 
                            || (stopX && stopY))
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
                }
                AoE.setCenterX(HITBOX.getCenterX());
                AoE.setCenterY(HITBOX.getCenterY());
                LoS.setCenterX(HITBOX.getCenterX());
                LoS.setCenterY(HITBOX.getCenterY());
                if(notAggressive && LoS.intersects(Player.HITBOX))
                    notAggressive = false;
                if(AoE.intersects(Player.HITBOX))
                    Player.HEALTH -= AoE_DAMAGE;                
            }
            
        }
    }
    
    
    public void draw(Graphics g)
    {
        if(!isDEAD)
        {
            //DEBUG shows the entity's nav path to player, line of sight, and aoe
            /*if(AoE != null && LoS != null)
            {
                g.setColor(Color.red);
                g.draw(AoE);
                g.setColor(Color.yellow);
                g.draw(LoS);
            }
            
            g.setColor(Color.yellow);
            if(toPLAYER != null)
            {
                for(int i = 0; i < toPLAYER.getLength(); ++i)
                {
                    g.drawRect(toPLAYER.getX(i)*32, toPLAYER.getY(i)*32, 32, 32);
                }
            }*/
            
            if(ID == 101)
            {
                switch(FACING)
                {
                    case  N: World.sWanderer.draw(HITBOX.getX(), HITBOX.getY());
                        break;
                    case NE: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 45);
                             World.sWanderer.draw(HITBOX.getX(), HITBOX.getY());
                             g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -45);
                        break;
                    case  E: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 90);
                             World.sWanderer.draw(HITBOX.getX(), HITBOX.getY());
                             g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -90);
                        break;
                    case SE: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 135);
                             World.sWanderer.draw(HITBOX.getX(), HITBOX.getY());
                             g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -135);
                        break;
                    case  S: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 180);
                             World.sWanderer.draw(HITBOX.getX(), HITBOX.getY());
                             g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -180);
                        break;
                    case SW: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -135);
                             World.sWanderer.draw(HITBOX.getX(), HITBOX.getY());
                             g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 135);
                        break;
                    case  W: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -90);
                             World.sWanderer.draw(HITBOX.getX(), HITBOX.getY());
                             g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 90);
                        break;
                    case NW: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -45);
                             World.sWanderer.draw(HITBOX.getX(), HITBOX.getY());
                             g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 45);
                        break;
                }
            }
            else
            {
                switch(FACING)
                {
                    case  N: World.sAggressor.draw(HITBOX.getX(), HITBOX.getY());
                        break;
                    case NE: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 45);
                             World.sAggressor.draw(HITBOX.getX(), HITBOX.getY());
                             g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -45);
                        break;
                    case  E: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 90);
                             World.sAggressor.draw(HITBOX.getX(), HITBOX.getY());
                             g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -90);
                        break;
                    case SE: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 135);
                             World.sAggressor.draw(HITBOX.getX(), HITBOX.getY());
                             g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -135);
                        break;
                    case  S: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 180);
                             World.sAggressor.draw(HITBOX.getX(), HITBOX.getY());
                             g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -180);
                        break;
                    case SW: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -135);
                             World.sAggressor.draw(HITBOX.getX(), HITBOX.getY());
                             g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 135);
                        break;
                    case  W: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -90);
                             World.sAggressor.draw(HITBOX.getX(), HITBOX.getY());
                             g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 90);
                        break;
                    case NW: g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), -45);
                             World.sAggressor.draw(HITBOX.getX(), HITBOX.getY());
                             g.rotate(HITBOX.getCenterX(), HITBOX.getCenterY(), 45);
                        break;
                }
            }
        }
    }
    
    private boolean isCollision()
    {
        if(Player.HITBOX.intersects(HITBOX))
            return true;
        else if(HITBOX.getMinX() < 32 || HITBOX.getMaxX() > 1248 ||
                HITBOX.getMinY() < 32 || HITBOX.getMaxY() > 928)
            return true;
               
        for(int i = 0; i < World.AGGRESSOR_LIST.get(
                World.CURRENT_WORLD_INDEX).size(); ++i)
        {
            if(!World.AGGRESSOR_LIST.get(
                    World.CURRENT_WORLD_INDEX).get(i).HITBOX.equals(HITBOX) && 
                    World.AGGRESSOR_LIST.get(
                    World.CURRENT_WORLD_INDEX).get(i).HITBOX.intersects(HITBOX))
                return true;
        }
        for(int i = 0; i < World.WANDERER_LIST.get(
                World.CURRENT_WORLD_INDEX).size(); ++i)
        {
            if(!World.WANDERER_LIST.get(
                    World.CURRENT_WORLD_INDEX).get(i).HITBOX.equals(HITBOX) && 
                    World.WANDERER_LIST.get(
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
                Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX
                ).getTileHeight()].COLLIDEABLE)
            return true;
        if(Slick2DUnixGame.WORLD.overlay.get(World.CURRENT_WORLD_INDEX)
                [((int)HITBOX.getX() + (int)HITBOX.getWidth()) / 
                Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX
                ).getTileWidth()]
                [(int)HITBOX.getY() / Slick2DUnixGame.WORLD.map.get(
                World.CURRENT_WORLD_INDEX).getTileHeight()].COLLIDEABLE)
            return true;
        if(Slick2DUnixGame.WORLD.overlay.get(World.CURRENT_WORLD_INDEX)
                [((int)HITBOX.getX() + (int)HITBOX.getWidth()) /
                Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX
                ).getTileWidth()]
                [((int)HITBOX.getY() + (int)HITBOX.getHeight()) / 
                Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX
                ).getTileHeight()].COLLIDEABLE)
            return true;
        
        return false;
    }    
    
    private void updateWander(int delta)
    {
        if(MS_SINCE_ACTION >= toWait)
        {
            MS_SINCE_ACTION = 0;
            toMove = PX_PER_ACTION;
            isWandering = true;
            FACING = (int)(Math.random()*7);
            toWait = (int)(Math.random()*MS_PER_ACTION); 
        }
        else if(isWandering)
        {
            double toAddX = 0;
            double toAddY = 0;

            boolean stopX = false;
            boolean stopY = false;
            double prevX = 0;
            double prevY = 0;

            if(toMove < 0)
            {
                isWandering = false;
                toMove = 0;
            }
            toMove -= delta/MS_PER_PIXEL;

            if(FACING == N)
            {
                toAddY -= delta/MS_PER_PIXEL;
            }    
            else if(FACING == NE)
            {  
                toAddY -= (delta/MS_PER_PIXEL)/diagPenalty;
                toAddX += (delta/MS_PER_PIXEL)/diagPenalty;
            }
            else if(FACING == E)
            {
                toAddX += delta/MS_PER_PIXEL;
            }
            else if(FACING == SE)
            {
                toAddY += (delta/MS_PER_PIXEL)/diagPenalty;
                toAddX += (delta/MS_PER_PIXEL)/diagPenalty;
            }    
            else if(FACING == S)
            {
                toAddY += delta/MS_PER_PIXEL;
            }
            else if(FACING == SW)
            {
                toAddY += (delta/MS_PER_PIXEL)/diagPenalty;
                toAddX -= (delta/MS_PER_PIXEL)/diagPenalty;
            }
            else if(FACING == W)
            {   
                toAddX -= delta/MS_PER_PIXEL;
            }
            else if(FACING == NW)
            {    
                toAddY -= (delta/MS_PER_PIXEL)/diagPenalty;
                toAddX -= (delta/MS_PER_PIXEL)/diagPenalty;
            }
            while(Math.abs(toAddX) > 1 ||
                    Math.abs(toAddY) > 1 || (stopX && stopY))
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
        }
        else
            MS_SINCE_ACTION += delta;
    }
    
    public void dropItem()
    {
        if(ID == 101)
        {
            World.ITEM_DROPS.get(World.CURRENT_WORLD_INDEX).add(
                    new ItemDrop(ID, (int)HITBOX.getX(), (int)HITBOX.getY()));
        }
        //if aggressor
        else if(ID == 102){}
    }
}
