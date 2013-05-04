package slick2dunixgame;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

/**
 *
 * @author chris
 */
public class HeadsUpDisplay
{
    private final static float HEALTHBAR_WIDTH = 500;
    private static Rectangle HEALTHBAR = new Rectangle(150, 585, HEALTHBAR_WIDTH, 10);
    
    private static int prevHealth = -1;
    
    private static boolean healthDamaged = false;
    private static boolean healthGained = false;
    private static int cycles = 0;
    
    public static void draw(Graphics g)
    {
        if(prevHealth > 0 && Player.HEALTH < prevHealth && 
                !healthDamaged && !healthGained)
            healthDamaged = true;
        else if(prevHealth > 0 && Player.HEALTH > prevHealth
                && !healthDamaged && !healthGained)
            healthGained = true;
        
        HEALTHBAR.setWidth(
                ((Player.HEALTH/(float)Player.START_HEALTH)*HEALTHBAR_WIDTH));
        HEALTHBAR.setLocation(150, 585);
        
        g.setColor(new Color(0,0,0, 0.6f));
        g.fill(new Rectangle(145, 580, 510, 20));
        
        if(healthDamaged)
        {
            if(cycles % 4 == 0)
                g.setColor(new Color(255, 0, 0, 0.6f));
            else
                g.setColor(new Color(0, 255, 0, 0.6f));
            
            if(cycles > 16)
            {
                healthDamaged = false;
                cycles = 0;
            }
            else
                ++cycles;
        }
        else if(healthGained)
        {
            g.setColor(new Color(0, 255, 0, 0.6f));
            if(cycles % 4 == 0)
                g.fill(new Rectangle(145, 570, 510, 30));
            
            if(cycles > 16)
            {
                healthGained = false;
                cycles = 0;
            }
            else
                ++cycles;
        }
        else
            g.setColor(new Color(0, 255, 0, 0.6f));
        
        g.fill(HEALTHBAR);
        
        g.setColor(new Color(0,0,0, 0.35f));
        g.fill(new Rectangle(0, 0, 170, 40));
        
        g.setColor(new Color(0,0,0, 0.45f));
        g.fill(new Rectangle(0, 0, 150, 36));
        
        g.setColor(new Color(0,0,0, 0.55f));
        g.fill(new Rectangle(0, 0, 130, 32));
        
        g.setColor(Color.white);
        g.scale(1.5f, 1.5f);
        g.drawString("SCORE: " + String.valueOf(Player.SCORE), 5, 0);
        
        prevHealth = Player.HEALTH;
    }
    
}
