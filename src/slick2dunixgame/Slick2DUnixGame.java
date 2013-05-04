package slick2dunixgame;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author chris
 */
public class Slick2DUnixGame extends StateBasedGame
{
    public static final int MAINMENUSTATE = 0; 
    public static final int GAMEPLAYSTATE = 1;
    public static final int MENUSTATE = 2;
    
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    
    public static World WORLD;
    public static AppGameContainer app;
    
    public Slick2DUnixGame()
    {
        super("PROJECT OBELISK");
    }
        
    @Override
    public void initStatesList(GameContainer gameContainer) throws SlickException 
    {        
        WORLD = new World();
        WORLD.setNavMesh();
        this.addState(new MainMenuState(MAINMENUSTATE));
        this.addState(new GameplayState(GAMEPLAYSTATE));
        this.addState(new MenuState(MENUSTATE));
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SlickException
    {                
        app = new AppGameContainer(new Slick2DUnixGame());
        app.setDisplayMode(WINDOW_WIDTH, WINDOW_HEIGHT, false);
        app.start();
    }
}

