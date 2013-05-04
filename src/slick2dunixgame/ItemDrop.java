package slick2dunixgame;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Ellipse;


/**
 *
 * @author chris
 */
public class ItemDrop
{
    private static final int DURATION = 30000;
    private static final int ITEM_AMPLITUDE = 5;
    
    public final int HEALTH_GAINED = 300;
    
    
    private final int SIZE_RADIUS = 10;
    public Ellipse SIZE;
    public boolean remove = false;
    
    int ms_since_drop = 0;
    int xc = 0;
    int yc = 0;
    
    public ItemDrop(int id, int x, int y)
    {
        xc = x;
        yc = y;
        SIZE = new Ellipse(xc + 10, yc + 10, SIZE_RADIUS, SIZE_RADIUS);
    }
    
    public void draw(Graphics g)
    {
        if(!remove)
        {
            g.drawImage(World.sBeef, xc, yc + (float)Math.sin(ms_since_drop/100f)*ITEM_AMPLITUDE);
        }
    }
    
    public void update(int delta)
    {
        if(!remove)
        {
            ms_since_drop += delta;
            if(ms_since_drop >= DURATION)
                remove = true;
        }
    }
}
