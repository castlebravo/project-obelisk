package slick2dunixgame;

import org.newdawn.slick.util.pathfinding.TileBasedMap;
import org.newdawn.slick.util.pathfinding.PathFindingContext;


/**
 *
 * @author chris
 */
public class TileBasedMapExtender implements TileBasedMap
{    
    @Override
    public int getWidthInTiles(){
        return Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getWidth();
    }

    @Override
    public int getHeightInTiles(){
        return Slick2DUnixGame.WORLD.map.get(World.CURRENT_WORLD_INDEX).getHeight();
    }

    @Override
    public void pathFinderVisited(int i, int j){}

    @Override
    public boolean blocked(PathFindingContext pfc, int i, int j){
        return Slick2DUnixGame.WORLD.overlay.get(World.CURRENT_WORLD_INDEX)[i][j].COLLIDEABLE;
    }

    @Override
    public float getCost(PathFindingContext pfc, int i, int j){
        return 1;
    }
}
