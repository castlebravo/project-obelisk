package slick2dunixgame;

import org.newdawn.slick.tiled.*;
import org.newdawn.slick.*;
import org.newdawn.slick.Graphics;
import java.util.ArrayList;

import org.newdawn.slick.particles.ParticleSystem;
import java.io.File;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;

import org.newdawn.slick.Image;

/**
 *
 * @author chris
 */
public class World
{
    public ArrayList<TiledMap> map = new ArrayList<TiledMap>();
    public ArrayList<Tile[][]> overlay = new ArrayList<Tile[][]>();
    
    public static ArrayList<LayeredText>    
            DAMAGE_DISPLAY = new ArrayList<LayeredText>();
    public static ArrayList<ArrayList<ItemDrop>>       
            ITEM_DROPS = new ArrayList<ArrayList<ItemDrop>>();
    
    public static ArrayList<ArrayList<Entity>>         
            WANDERER_LIST = new ArrayList<ArrayList<Entity>>();
    public static ArrayList<ArrayList<Entity>>          
            AGGRESSOR_LIST = new ArrayList<ArrayList<Entity>>();
    
    public static ArrayList<ArrayList<Tile>>           
            AGGRESSOR_SPAWNS = new ArrayList<ArrayList<Tile>>();
    public static ArrayList<ArrayList<Tile>>           
            WANDERER_SPAWNS = new ArrayList<ArrayList<Tile>>();
    
    public static int   CURRENT_WORLD_INDEX = 0;
    
    public static ParticleSystem            PARTICLE_SYSTEM;
    public static File                      entityParticles;
    
    public static AStarPathFinder finder;
    
    public static int[] MAX_WANDERERS = new int[4];
    public static int[] MAX_AGGRESSORS = new int[4];
    
    public static Image sSword;
    public static Image sArrow;
    
    public static Image sBerryBush;
    public static Image sBerries;
    public static Image sBeef;
    public static Image sCoin;
    public static Image sPlayer;
    public static Image sAggressor;
    public static Image sWanderer;
    
    private static int WANDERER_RESPAWN = 15000;
    private static int AGGRESSOR_RESPAWN = 30000;
    private int ms_since_wanderer = 0;
    private int ms_since_aggressor = 0;
    
    public World()
    {
        for(int i = 0; i < 4; ++i)
        {
            MAX_WANDERERS[i] = 0;
            MAX_AGGRESSORS[i] = 0;
        }
        
        //order below:
        //
        //  3   2
        //  1   0
        //
        AGGRESSOR_LIST.add(CURRENT_WORLD_INDEX, new ArrayList<Entity>());
        AGGRESSOR_SPAWNS.add(new ArrayList<Tile>());
        WANDERER_LIST.add( new ArrayList<Entity>());
        WANDERER_SPAWNS.add(new ArrayList<Tile>());
       
        ITEM_DROPS.add(new ArrayList<ItemDrop>());
        setTileMap("slick2dunixgame/resources/GRID_0.tmx");
       
        CURRENT_WORLD_INDEX = 1;
        AGGRESSOR_LIST.add( new ArrayList<Entity>());
        AGGRESSOR_SPAWNS.add(new ArrayList<Tile>());
        WANDERER_LIST.add( new ArrayList<Entity>());
        WANDERER_SPAWNS.add(new ArrayList<Tile>());
       
        ITEM_DROPS.add(new ArrayList<ItemDrop>());
        setTileMap("slick2dunixgame/resources/GRID_1.tmx"); 
       
        CURRENT_WORLD_INDEX = 2;
        AGGRESSOR_LIST.add( new ArrayList<Entity>());
        AGGRESSOR_SPAWNS.add(new ArrayList<Tile>());
        WANDERER_LIST.add( new ArrayList<Entity>());
        WANDERER_SPAWNS.add(new ArrayList<Tile>());
       
        ITEM_DROPS.add(new ArrayList<ItemDrop>());
        setTileMap("slick2dunixgame/resources/GRID_2.tmx"); 
       
        CURRENT_WORLD_INDEX = 3;
        AGGRESSOR_LIST.add( new ArrayList<Entity>());
        AGGRESSOR_SPAWNS.add(new ArrayList<Tile>());
        WANDERER_LIST.add( new ArrayList<Entity>());
        WANDERER_SPAWNS.add(new ArrayList<Tile>());
       
        ITEM_DROPS.add(new ArrayList<ItemDrop>());
        setTileMap("slick2dunixgame/resources/GRID_3.tmx"); 
        CURRENT_WORLD_INDEX = 0;
        try
        {
            sSword = new Image("slick2dunixgame/resources/sword.png");
            sArrow = new Image("slick2dunixgame/resources/arrow.png");
            
            sBerryBush = new Image("slick2dunixgame/resources/RedBerryBush Final.png");
            sBerries = new Image("slick2dunixgame/resources/berry3.png");
            sBeef = new Image("slick2dunixgame/resources/beef.png");
            sCoin = new Image("slick2dunixgame/resources/coin.png");
            sPlayer = new Image("slick2dunixgame/resources/Player.png");
            sAggressor = new Image("slick2dunixgame/resources/Aggressor.png");
            sWanderer= new Image("slick2dunixgame/resources/Wanderer.png");           
       }
       catch(SlickException e)
       {
           System.out.println("WORLD: " + e.toString());
       }
    }
    
    public void setNavMesh()
    {
        finder = new AStarPathFinder(new TileBasedMapExtender(), 1000, true);
    }
    
    
    public void setTileMap(String filename)
    {
        try
        {
            map.add(new TiledMap(filename));
            overlay.add(new Tile[map.get(CURRENT_WORLD_INDEX).getWidth()][map.get(CURRENT_WORLD_INDEX).getHeight()]);
            
            for(int i = 0; i < map.get(CURRENT_WORLD_INDEX).getWidth(); ++i)
            {
                for(int j = 0; j < map.get(CURRENT_WORLD_INDEX).getHeight(); ++j)
                {          
                    int ID = Integer.parseInt(map.get(CURRENT_WORLD_INDEX).getTileProperty(map.get(CURRENT_WORLD_INDEX).getTileId(i, j, 0), "ID", "-1"));
                    if(ID > 0)
                    {
                        overlay.get(CURRENT_WORLD_INDEX)[i][j] = new Tile((i*map.get(CURRENT_WORLD_INDEX).getTileWidth()), 
                                (j*map.get(CURRENT_WORLD_INDEX).getTileHeight()), map.get(CURRENT_WORLD_INDEX).getTileWidth(),
                                map.get(CURRENT_WORLD_INDEX).getTileHeight(), ID);
                        if(ID == 101)
                            WANDERER_SPAWNS.get(CURRENT_WORLD_INDEX).add(overlay.get(CURRENT_WORLD_INDEX)[i][j]);
                        else if(ID == 102)
                            AGGRESSOR_SPAWNS.get(CURRENT_WORLD_INDEX).add(overlay.get(CURRENT_WORLD_INDEX)[i][j]);
                    }
                }
            }
        }
        catch(SlickException e)
        {
            System.out.println(e.toString());
        }
    }
    
    public void draw(Graphics g)
    {
        map.get(CURRENT_WORLD_INDEX).render(0, 0);
        
        for(int i = 0; i < map.get(CURRENT_WORLD_INDEX).getWidth(); ++i)
        {
            for(int j = 0; j < map.get(CURRENT_WORLD_INDEX).getHeight(); ++j)
            {          
                if(overlay.get(CURRENT_WORLD_INDEX)[i][j].DESTRUCTABLE || overlay.get(CURRENT_WORLD_INDEX)[i][j].HAS_HEALTH)
                    overlay.get(CURRENT_WORLD_INDEX)[i][j].render(g);
            }
        }
        
        for(int i = 0; i < AGGRESSOR_LIST.get(CURRENT_WORLD_INDEX).size(); ++i)
        {
            AGGRESSOR_LIST.get(CURRENT_WORLD_INDEX).get(i).draw(g);
        }
        for(int i = 0; i < WANDERER_LIST.get(CURRENT_WORLD_INDEX).size(); ++i)
        {
            WANDERER_LIST.get(CURRENT_WORLD_INDEX).get(i).draw(g);
        }

        for(int i = 0; i < DAMAGE_DISPLAY.size(); ++i)
        {
            DAMAGE_DISPLAY.get(i).write(g);
        }
        
        for(int i = 0; i < ITEM_DROPS.get(CURRENT_WORLD_INDEX).size(); ++i)
        {
            ITEM_DROPS.get(CURRENT_WORLD_INDEX).get(i).draw(g);
        }
    }
    
    public void update(int delta)
    {
        int i = 0;

        while(i < DAMAGE_DISPLAY.size())
        {
            if(DAMAGE_DISPLAY.get(i).remove)
                DAMAGE_DISPLAY.remove(i);
            else
            {
                DAMAGE_DISPLAY.get(i).update(delta);
                ++i;
            }
        }
        
        updateAggressors(delta);
        updateWanderers(delta);
        updateItemDrops(delta);
        
        
        for(i = 0; i < map.get(CURRENT_WORLD_INDEX).getWidth(); ++i)
        {
            for(int j = 0; j < map.get(CURRENT_WORLD_INDEX).getHeight(); ++j)
            {          
                if(overlay.get(CURRENT_WORLD_INDEX)[i][j].DESTRUCTABLE)
                    overlay.get(CURRENT_WORLD_INDEX)[i][j].update(delta);
            }
        }
    }
    
    
    
    private void updateAggressors(int delta)
    {
        int i = 0;
        ms_since_aggressor += delta;
        if(AGGRESSOR_LIST.get(CURRENT_WORLD_INDEX).size() < MAX_AGGRESSORS[CURRENT_WORLD_INDEX] &&
                ms_since_aggressor >= AGGRESSOR_RESPAWN)
        {
            int index = (int)(Math.random()*AGGRESSOR_SPAWNS.get(World.CURRENT_WORLD_INDEX).size());
            ms_since_aggressor = 0;
            AGGRESSOR_LIST.get(CURRENT_WORLD_INDEX).add(
                    new Entity((int)AGGRESSOR_SPAWNS.get(CURRENT_WORLD_INDEX).get(index).HITBOX.getX(),
                    (int)AGGRESSOR_SPAWNS.get(CURRENT_WORLD_INDEX).get(index).HITBOX.getY(),102));
        }
        while(i < AGGRESSOR_LIST.get(CURRENT_WORLD_INDEX).size())
        {
            if(AGGRESSOR_LIST.get(CURRENT_WORLD_INDEX).get(i).isDEAD)
                AGGRESSOR_LIST.get(CURRENT_WORLD_INDEX).remove(i);
            else
            {
                AGGRESSOR_LIST.get(CURRENT_WORLD_INDEX).get(i).update(delta);
                ++i;
            }
        }
    }
    
    
    
    private void updateWanderers(int delta)
    {
        int i = 0;
        ms_since_wanderer += delta;
        if(WANDERER_LIST.get(CURRENT_WORLD_INDEX).size() < MAX_WANDERERS[CURRENT_WORLD_INDEX] &&
                ms_since_wanderer >= WANDERER_RESPAWN)
        {
            int index = (int)(Math.random()*WANDERER_SPAWNS.get(World.CURRENT_WORLD_INDEX).size());
            ms_since_wanderer = 0;
            WANDERER_LIST.get(CURRENT_WORLD_INDEX).add(
                    new Entity((int)WANDERER_SPAWNS.get(CURRENT_WORLD_INDEX).get(index).HITBOX.getX(),
                    (int)WANDERER_SPAWNS.get(CURRENT_WORLD_INDEX).get(index).HITBOX.getY(),101));
        }
        while(i < WANDERER_LIST.get(CURRENT_WORLD_INDEX).size())
        {
            if(WANDERER_LIST.get(CURRENT_WORLD_INDEX).get(i).isDEAD)
                WANDERER_LIST.get(World.CURRENT_WORLD_INDEX).remove(i);
            else
            {
                WANDERER_LIST.get(CURRENT_WORLD_INDEX).get(i).update(delta);
                ++i;
            }
        }
    }


    private void updateItemDrops(int delta)
    {
        int i = 0;
        while(i < ITEM_DROPS.get(CURRENT_WORLD_INDEX).size())
        {
            if(ITEM_DROPS.get(CURRENT_WORLD_INDEX).get(i).remove)
                ITEM_DROPS.get(CURRENT_WORLD_INDEX).remove(i);
            else
            {
                ITEM_DROPS.get(CURRENT_WORLD_INDEX).get(i).update(delta);
                ++i;
            }
        }    
    }
    
    
    
    public static void transitionWorlds(float x, float y)
    {        
        //shift left
        if(Player.HITBOX.getMinX() < 32)
        {
            Player.HITBOX.setX(1215);
            GameplayState.offsetX = -480;
            
            if(CURRENT_WORLD_INDEX == 2)
             CURRENT_WORLD_INDEX = 3;
            else if(CURRENT_WORLD_INDEX == 0)
             CURRENT_WORLD_INDEX = 1;
        }
        //shift right
        else if(Player.HITBOX.getMaxX() > 1248)
        {
            Player.HITBOX.setX(33);
            GameplayState.offsetX = 0;
            
            if(CURRENT_WORLD_INDEX == 3)
             CURRENT_WORLD_INDEX = 2;
            else if(CURRENT_WORLD_INDEX == 1)
             CURRENT_WORLD_INDEX = 0;
        }
        //shift down
        else if(Player.HITBOX.getMaxY() > 928)
        {
            Player.HITBOX.setY(33);
            GameplayState.offsetY = 0;
            
            if(CURRENT_WORLD_INDEX == 3)
             CURRENT_WORLD_INDEX = 1;
            else if(CURRENT_WORLD_INDEX == 2)
             CURRENT_WORLD_INDEX = 0;
        }
        //shift up
        else if(Player.HITBOX.getMinY() < 32)
        {
            Player.HITBOX.setY(895);
            GameplayState.offsetY = -360;
            
            if(CURRENT_WORLD_INDEX == 1)
             CURRENT_WORLD_INDEX = 3;
            else if(CURRENT_WORLD_INDEX == 0)
             CURRENT_WORLD_INDEX = 2;
        }
    }
}
