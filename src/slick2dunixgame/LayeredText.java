package slick2dunixgame;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 *
 * @author chris
 */
public class LayeredText
{
    private final Color COLOR = Color.orange;
    private final static float DURATION = 750;
    private final static float DISTANCE = 50;
    
    private String  toDisplay;
    private int     lifespan = 0;
    private int     x;
    private float   y;
    public boolean  remove = false;
    
    
    public LayeredText(String text, float xc, float yc)
    {
        toDisplay = text;
        x = (int)xc;
        y = yc;
    }
    
    public void update(int delta)
    {
        if(lifespan >= DURATION)
            remove = true;    
        
        else if(!remove)
        {
            lifespan += delta;
            y -= (DISTANCE/DURATION)*delta;
        }
    }
    
    public void write(Graphics g)
    {
        if(!remove)
        {
            g.setColor(COLOR);
            g.drawString(toDisplay, x, y);
        }
    }
}
