package slick2dunixgame;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.geom.Rectangle;

import org.newdawn.slick.Image;

/**
 *
 * @author ben
 */
public class MenuState extends BasicGameState
{
    int stateID;
    boolean FLAG_RESUME = false;
    boolean FLAG_MAIN_MENU = false;
    boolean FLAG_SHUTDOWN = false;
    
    public static final Rectangle resumeButtonBox = new Rectangle(175,515,200,50);  
    
    public static final Rectangle restartButtonBox1  = new Rectangle(425,515,200,50);
    public static final Rectangle restartButtonBox2  = new Rectangle(300,515,200,50);
  
    
    private Image pauseBackground;
    private Image gameoverBackground;
    
    private Image resumeButton;
    private Image restartButton;
    
    private float resumeButtonScale = 1;
    private float restartButtonScale = 1;
    
    
    public MenuState(int stateID) 
    {
       this.stateID = stateID;
       try
       {
            pauseBackground = new Image("slick2dunixgame/resources/pause_menu.png");
            gameoverBackground = new Image("slick2dunixgame/resources/gameover_menu.png");
            resumeButton = new Image("slick2dunixgame/resources/resume_button.png");
            restartButton = new Image("slick2dunixgame/resources/restart_button.png");
       }
       catch(SlickException e)
       {
           System.out.println("MENU: " + e.toString());
       }
    }
  
    @Override
    public int getID(){return stateID;}
  
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException
    {}
  
    @Override
    public void render(GameContainer gamec, StateBasedGame sbg, Graphics gc) throws SlickException
    {
        if(Player.isDEAD)
        {
            gameoverBackground.draw(0, 0);
            restartButton.draw(300, 515, restartButtonScale);
            
            gc.setColor(Color.black);
            gc.fillRect(250, 250, 300, 200);
            
            gc.setColor(Color.yellow);
            gc.drawString("Haha you died!", 255, 255);
            gc.drawString("Final Score: " + Player.SCORE, 255, 275);
            if(Player.SCORE  < 100)
                gc.drawString("Better luck next time.", 255, 295); 
            else if(Player.SCORE  < 300)
                gc.drawString("Not bad, but you could do better.", 255, 295); 
            else if(Player.SCORE  < 500)
                gc.drawString("Nice, but there are more coins to be had.", 255, 295);
            else if(Player.SCORE  < 750)
                gc.drawString("Now that's what I call dungeon delving.", 255, 295);
            else if(Player.SCORE  < 1000)
                gc.drawString("You sir are a god amongst giant creepy hamsters.", 255, 295);
        }
        else
        {
            pauseBackground.draw(0, 0);
            resumeButton.draw(175, 515, resumeButtonScale);
            restartButton.draw(425, 515, restartButtonScale);
        }
    }
  
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException
    {
        if(!gc.isShowingFPS())
            gc.setShowFPS(true); 
        
        if(FLAG_SHUTDOWN)
        {
            FLAG_SHUTDOWN = false;
            gc.exit();
        }
        else if(FLAG_RESUME)
        {
            FLAG_RESUME = false;
            sbg.enterState(Slick2DUnixGame.GAMEPLAYSTATE);
        }
        else if(FLAG_MAIN_MENU)
        {
            FLAG_MAIN_MENU = false;
            gc.exit();         
        }
    }
    
    @Override
    public void keyPressed(int key, char c)
    {}
    @Override
    public void keyReleased(int key, char c)
    {}
    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) 
    {
        if(Player.isDEAD)
        {

            if(restartButtonBox2.contains(x, y))
                FLAG_SHUTDOWN = true;
        }
        else
        {
            if(resumeButtonBox.contains(x, y))
                FLAG_RESUME = true;
            else if(restartButtonBox1.contains(x, y))
                FLAG_MAIN_MENU = true;
        }
    }
     @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) 
    {
        if(Player.isDEAD)
        {
            if(restartButtonBox2.contains(newx, newy))
                restartButtonScale = 1.05f;
            else
                restartButtonScale = 1;
        }
        else
        {
            if(resumeButtonBox.contains(newx, newy))
                resumeButtonScale = 1.05f;
            else
                resumeButtonScale = 1;

            if(restartButtonBox1.contains(newx, newy))
                restartButtonScale = 1.05f;
            else
                restartButtonScale = 1;
        }
    }
  
}