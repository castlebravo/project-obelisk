package slick2dunixgame;
  
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import java.awt.Point;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.Color;
  
/**
 *
 * @author chris
 */
public class GameplayState extends BasicGameState
{
    int             stateID;
    static boolean  FLAG_MENU;
    
    static float   offsetX = -(Player.HITBOX.getCenterX() -
            Slick2DUnixGame.WINDOW_WIDTH/2);
    static float   offsetY = -(Player.HITBOX.getCenterY() -
            Slick2DUnixGame.WINDOW_HEIGHT/2);
    
    public static Point         MOUSE_POINT = new Point();
    public static Ellipse       NO_INPUT_ZONE = new Ellipse(
            Player.HITBOX.getX(), Player.HITBOX.getX(), 64, 64);
    
    
    public static final boolean ENABLE_MOUSE_MOVEMENT = false;
    public static final boolean SHOW_NAV_GRID = true;
    public static boolean       SWORD_ACTIVE = true;
    public static boolean       BOW_ACTIVE = false;
    
    
    static float scaleX = (float)Slick2DUnixGame.app.getWidth()/
            (Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getWidth()*
            Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getTileWidth());
    static float scaleY = (float)Slick2DUnixGame.app.getHeight()/
               (Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getHeight()*
            Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getTileHeight());
    
    
    public GameplayState(int stateID) 
    {
       FLAG_MENU = false;
       this.stateID = stateID;
    }
  
    @Override
    public int getID(){return stateID;}
  
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException
    {
        MOUSE_POINT.setLocation(gc.getWidth()/2, gc.getHeight()/2);
    }
  
    @Override
    public void render(GameContainer gamec, StateBasedGame sbg, Graphics gc) throws SlickException
    {
        gc.translate(offsetX, offsetY);
        
        Slick2DUnixGame.WORLD.draw(gc);
        Player.draw(gc);  
        
        if(ENABLE_MOUSE_MOVEMENT)
        {
            gc.setColor(Color.yellow);
            gc.draw(NO_INPUT_ZONE);
            gc.setColor(Color.black);
        }
                
        gc.translate(-offsetX, -offsetY);
        
        HeadsUpDisplay.draw(gc);
    }
  
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException
    {
        if(gc.isShowingFPS())
            gc.setShowFPS(false);
        
        if(ENABLE_MOUSE_MOVEMENT)
        {
            NO_INPUT_ZONE.setCenterX(Player.HITBOX.getCenterX());
            NO_INPUT_ZONE.setCenterY(Player.HITBOX.getCenterY());
        }
        
        if(FLAG_MENU)
        {
            FLAG_MENU = false;
            sbg.enterState(Slick2DUnixGame.MENUSTATE);  
        }
                
        Slick2DUnixGame.WORLD.update(delta);
        Player.update(delta);
    }
    
    @Override
    public void keyPressed(int key, char c)
    {
        //System.out.println(key);
        if(key == 1)
            FLAG_MENU = true;
        if(key == 200 || key == 17)
            Player.UP = true;
        if(key == 203 || key == 30)
            Player.LEFT = true;
        if(key == 208 || key == 31)
            Player.DOWN = true;
        if(key == 205 || key == 32)
            Player.RIGHT = true;
    }
    
    @Override
    public void keyReleased(int key, char c)
    {
        if(key == 200 || key == 17)
            Player.UP = false;
        if(key == 203 || key == 30)
            Player.LEFT = false;
        if(key == 208 || key == 31)
            Player.DOWN = false;
        if(key == 205 || key == 32)
            Player.RIGHT = false;
    }
    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) 
    {
        if(ENABLE_MOUSE_MOVEMENT)
            MOUSE_POINT.setLocation(newx, newy);
    }
    @Override
    public void mousePressed(int button, int x, int y)
    {
        if(button == 0 && !Player.START_BOW)
            Player.START_SWORD = true;
        else if(button == 1 && !Player.START_SWORD)
            Player.START_BOW = true;
    }
    @Override
    public void mouseReleased(int button, int x, int y)
    {
        if(button == 0)
            Player.START_SWORD = false;
        else if(button == 1)
            Player.START_BOW = false;
    }
    @Override
    public void mouseClicked(int button, int x, int y, int clickCount){}
    @Override
    public void mouseWheelMoved(int change){}
}