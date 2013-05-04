package slick2dunixgame;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

/**
 *
 * @author ben
 */
public class Tile
{
    private static final int REGROWTH_TIME = 30000;
    private static final int ITEM_AMPLITUDE = 5;
    
    public Rectangle    HITBOX;
    
    public boolean      COLLIDEABLE = false;
    
    public boolean      DESTRUCTABLE = false;
    public boolean      DESTROYED = false;
    public boolean      HAS_HEALTH = false;
    public final int    START_HEALTH = 50;
    public int          HEALTH = START_HEALTH;
    public final int    HEALTH_GAIN = 100;
    public final int    COIN_VALUE = 100;
    
    public boolean      SPAWN_WANDERER = false;
    public boolean      SPAWN_AGGRESSOR = false;
    
    private int time_destroyed = 0;
    public int ID = -1;
    
    public Tile(int x, int y, int width, int height, int id)
    {
        ID = id;
        HITBOX = new Rectangle(x, y, width, height);
        switch(ID)
        {
            
            case   1:                      //case granite wall
            case   2: COLLIDEABLE = true;  //case wooden wall
                break;
                
            //case spawn wanderer
            case 101: World.WANDERER_LIST.get(World.CURRENT_WORLD_INDEX).add(new Entity(x, y, ID));
                      World.MAX_WANDERERS[World.CURRENT_WORLD_INDEX]++;
                      SPAWN_WANDERER = true;
                break;
            //case spawn aggressor
            case 102: World.AGGRESSOR_LIST.get(World.CURRENT_WORLD_INDEX).add(new Entity(x, y, ID));
                      World.MAX_AGGRESSORS[World.CURRENT_WORLD_INDEX]++;
                      SPAWN_AGGRESSOR = true;
                break;
                
            case 201: //case berry bush
            case 202: DESTRUCTABLE = true; //case coin
                break;
                
            case 301:// case long grass
            case 302:// case dungeon ground
                break;
            
            default: //default case; send error message
                System.out.println("!!!ERROR: unhandled tile ID number!!!\n" + ID);
                break;
        }      
    }
    
    public void render(Graphics g)
    {   
        if(ID == 201)
        {
            if(!DESTROYED)
                g.drawImage(World.sBerryBush, HITBOX.getX(), HITBOX.getY());
            else if(HAS_HEALTH)
            {
                g.drawImage(World.sBerries, HITBOX.getX(), 
                   HITBOX.getY() + (float)Math.sin(time_destroyed/100f)*ITEM_AMPLITUDE);
            }
        }
        else if(ID ==202)
            if(!DESTROYED)
                g.drawImage(World.sCoin, HITBOX.getX(), HITBOX.getY() + 
                        (float)Math.sin(time_destroyed/100f)*ITEM_AMPLITUDE);
    }
    
    public void update(int delta)
    {
        if(ID == 201)
        {
            if(DESTROYED)
                time_destroyed += delta;
            if(time_destroyed >= REGROWTH_TIME)
            {
                DESTROYED = false;
                time_destroyed = 0;
                HAS_HEALTH = false;
            }
        }
        else if(ID ==202 && !DESTROYED)
        {
            time_destroyed += delta;
            if(time_destroyed/100 > 2*Math.PI)
                time_destroyed -= 200*Math.PI;
        }
    }
}
