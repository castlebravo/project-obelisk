package slick2dunixgame;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.geom.Rectangle;

import org.newdawn.slick.Image;

/**
 *
 * @author chris
 */
public class MainMenuState extends BasicGameState
{
    int stateID;
    boolean FLAG_START;
    
    private final Rectangle startButtonBox = new Rectangle(300, 515, 200, 50);  
    private Image mainMenuBackground;
    private Image startButton;
    
    private float buttonScale = 1;
    
    public MainMenuState(int stateID) 
    {
       this.stateID = stateID;
       FLAG_START = false;
       try
       {
            mainMenuBackground = new Image("slick2dunixgame/resources/main_menu.png");
            startButton = new Image("slick2dunixgame/resources/start_button.png");
       }
       catch(SlickException e)
       {
           System.out.println("MENU: " + e.toString());
       }
    }
  
    @Override
    public int getID(){return stateID;}
  
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException{}
  
    @Override
    public void render(GameContainer gamec, StateBasedGame sbg, Graphics gc) throws SlickException
    {
        mainMenuBackground.draw(0, 0);
        startButton.draw(300, 515, buttonScale);
        //gc.setColor(Color.lightGray);
        //gc.fill(startButtonBox);
        
        //gc.setColor(Color.black);
        //gc.drawString("Start", START_BOX.getX(), START_BOX.getY());
    }
  
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException
    {
        if(gc.isShowingFPS())
            gc.setShowFPS(false); 
        
        if(FLAG_START)
        {
            FLAG_START = false;
            sbg.enterState(Slick2DUnixGame.GAMEPLAYSTATE);
        }
    }
   
    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) 
    {
        if(startButtonBox.contains(x, y))
            FLAG_START = true;
    }
    
    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) 
    {
        if(startButtonBox.contains(newx, newy))
            buttonScale = 1.05f;
        else
            buttonScale = 1;
    }
}