/*
 * Copyright 2015-2020 Ray Fowler
 * 
 * Licensed under the GNU General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://www.gnu.org/licenses/gpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rotp.model.galaxy;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import rotp.model.game.IGameOptions;

// modnar: custom map shape, Cluster
public class GalaxyClusterShape extends GalaxyShape {
    public static final List<String> options1;
    //public static final List<String> options2;
    private static final long serialVersionUID = 1L;
    static {
        options1 = new ArrayList<>();
        options1.add("SETUP_CLUSTER_0");
        options1.add("SETUP_CLUSTER_1");
        options1.add("SETUP_CLUSTER_2");
        //options2 = new ArrayList<>();
        //options2.add("SETUP_NOT_AVAILABLE");
    }
    
	private Point.Float cc1, cc2, cc3, cc4, cc5, cc6, cc7, cc8;
	
    public GalaxyClusterShape(IGameOptions options) {
        opts = options;
    }
    @Override
    public float maxScaleAdj()               { return 0.95f; }
    @Override
    protected int galaxyWidthLY() { 
        return (int) (Math.sqrt(1.7*4.0/3.0*opts.numberStarSystems()*adjustedSizeFactor()));
    }
    @Override
    protected int galaxyHeightLY() { 
        return (int) (Math.sqrt(1.7*3.0/4.0*opts.numberStarSystems()*adjustedSizeFactor()));
    }
	@Override
    public List<String> options1()  { return options1; }
    //@Override
    //public List<String> options2()  { return options2; }
    @Override
    public String defaultOption1()  { return options1.get(0); }
    //@Override
    //public String defaultOption2()  { return options2.get(0); }
    @Override
    public void init(int n) {
        super.init(n);
        
        int option1 = max(0, options1.indexOf(opts.selectedGalaxyShapeOption1()));
        //int option2 = max(0, options2.indexOf(opts.selectedGalaxyShapeOption2()));
		
		cc1 = new Point.Float();
		cc2 = new Point.Float();
		cc3 = new Point.Float();
		cc4 = new Point.Float();
		cc5 = new Point.Float();
		cc6 = new Point.Float();
		cc7 = new Point.Float();
		cc8 = new Point.Float();
		
		// choose cluster locations with options1
		switch(option1) {
            case 0: {
                // 4 distinct clusters
                cc1.x = (float) 0.25f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc1.y = (float) 0.75f*galaxyHeightLY() + galaxyEdgeBuffer();
                cc2.x = (float) 0.38f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc2.y = (float) 0.44f*galaxyHeightLY() + galaxyEdgeBuffer();
                cc3.x = (float) 0.75f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc3.y = (float) 0.25f*galaxyHeightLY() + galaxyEdgeBuffer();
                cc4.x = (float) 0.62f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc4.y = (float) 0.56f*galaxyHeightLY() + galaxyEdgeBuffer();
                
                cc5.x = (float) 0.25f*galaxyWidthLY() + galaxyEdgeBuffer(); // same as cc1
                cc5.y = (float) 0.75f*galaxyHeightLY() + galaxyEdgeBuffer(); // same as cc1
                cc6.x = (float) 0.25f*galaxyWidthLY() + galaxyEdgeBuffer(); // same as cc1
                cc6.y = (float) 0.75f*galaxyHeightLY() + galaxyEdgeBuffer(); // same as cc1
                cc7.x = (float) 0.25f*galaxyWidthLY() + galaxyEdgeBuffer(); // same as cc1
                cc7.y = (float) 0.75f*galaxyHeightLY() + galaxyEdgeBuffer(); // same as cc1
                cc8.x = (float) 0.25f*galaxyWidthLY() + galaxyEdgeBuffer(); // same as cc1
                cc8.y = (float) 0.75f*galaxyHeightLY() + galaxyEdgeBuffer(); // same as cc1
                break;
            }
            case 1: {
                // 6 distinct clusters
                cc1.x = (float) 0.16f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc1.y = (float) 0.52f*galaxyHeightLY() + galaxyEdgeBuffer();
                cc2.x = (float) 0.5f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc2.y = (float) 0.67f*galaxyHeightLY() + galaxyEdgeBuffer();
                cc3.x = (float) 0.5f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc3.y = (float) 0.33f*galaxyHeightLY() + galaxyEdgeBuffer();
                cc4.x = (float) 0.84f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc4.y = (float) 0.48f*galaxyHeightLY() + galaxyEdgeBuffer();
                cc5.x = (float) 0.3f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc5.y = (float) 0.78f*galaxyHeightLY() + galaxyEdgeBuffer();
                cc6.x = (float) 0.7f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc6.y = (float) 0.22f*galaxyHeightLY() + galaxyEdgeBuffer();
                
                cc7.x = (float) 0.18f*galaxyWidthLY() + galaxyEdgeBuffer(); // same as cc1
                cc7.y = (float) 0.5f*galaxyHeightLY() + galaxyEdgeBuffer(); // same as cc1
                cc8.x = (float) 0.18f*galaxyWidthLY() + galaxyEdgeBuffer(); // same as cc1
                cc8.y = (float) 0.5f*galaxyHeightLY() + galaxyEdgeBuffer(); // same as cc1
                break;
            }
            case 2: {
                // 8 distinct clusters
                cc1.x = (float) 0.15f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc1.y = (float) 0.5f*galaxyHeightLY() + galaxyEdgeBuffer();
                cc2.x = (float) 0.3f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc2.y = (float) 0.22f*galaxyHeightLY() + galaxyEdgeBuffer();
                cc3.x = (float) 0.85f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc3.y = (float) 0.5f*galaxyHeightLY() + galaxyEdgeBuffer();
                cc4.x = (float) 0.7f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc4.y = (float) 0.78f*galaxyHeightLY() + galaxyEdgeBuffer();
                cc5.x = (float) 0.45f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc5.y = (float) 0.42f*galaxyHeightLY() + galaxyEdgeBuffer();
                cc6.x = (float) 0.42f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc6.y = (float) 0.74f*galaxyHeightLY() + galaxyEdgeBuffer();
                cc7.x = (float) 0.55f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc7.y = (float) 0.58f*galaxyHeightLY() + galaxyEdgeBuffer();
                cc8.x = (float) 0.58f*galaxyWidthLY() + galaxyEdgeBuffer();
                cc8.y = (float) 0.26f*galaxyHeightLY() + galaxyEdgeBuffer();
                break;
            }
        }
		
        // reset w/h vars since aspect ratio may have changed
        initWidthHeight();
    }
    @Override
	public void setRandom(Point.Float pt) {
        pt.x = randomLocation(width, galaxyEdgeBuffer());
        pt.y = randomLocation(height, galaxyEdgeBuffer());
    }
    @Override
    public void setSpecific(Point.Float pt) { // modnar: add possibility for specific placement of homeworld/orion locations
        setRandom(pt);
    }
    @Override
    public boolean valid(float x, float y) {
		// calculate the distances from the random point to ClusterCenters
		float dcc1 = distance(x,y,cc1.x,cc1.y);
		float dcc2 = distance(x,y,cc2.x,cc2.y);
		float dcc3 = distance(x,y,cc3.x,cc3.y);
		float dcc4 = distance(x,y,cc4.x,cc4.y);
		float dcc5 = distance(x,y,cc5.x,cc5.y);
		float dcc6 = distance(x,y,cc6.x,cc6.y);
		float dcc7 = distance(x,y,cc7.x,cc7.y);
		float dcc8 = distance(x,y,cc8.x,cc8.y);
		
		// cRadius defines approx. cluster radius
		// using two for variety
		float cRadius1 = 0.22f*galaxyHeightLY();
		float cRadius2 = 0.15f*galaxyHeightLY();
		
		float min_dcc = min(dcc1/cRadius1, dcc2/cRadius1, dcc3/cRadius1, dcc4/cRadius1, dcc5/cRadius2, dcc6/cRadius2, dcc7/cRadius2, dcc8/cRadius2);
		
		// accept based on distance vs radius (not worth doing ?)
		// more likely if closer to center, less likely further away		
        return (random() >= min_dcc);
    }
    float randomLocation(float max, float buff) {
        return buff + (random() * (max-buff-buff));
    }
    @Override
    protected float sizeFactor(String size) {
        float adj = 1.0f;
        switch (opts.selectedStarDensityOption()) {
            case IGameOptions.STAR_DENSITY_LOWEST:  adj = 1.3f; break;
            case IGameOptions.STAR_DENSITY_LOWER:   adj = 1.2f; break;
            case IGameOptions.STAR_DENSITY_LOW:     adj = 1.1f; break;
            case IGameOptions.STAR_DENSITY_HIGH:    adj = 0.9f; break;
            case IGameOptions.STAR_DENSITY_HIGHER:  adj = 0.8f; break;
            case IGameOptions.STAR_DENSITY_HIGHEST: adj = 0.7f; break;
        }
        switch (opts.selectedGalaxySize()) {
            case IGameOptions.SIZE_TINY:      return adj*10; 
            case IGameOptions.SIZE_SMALL:     return adj*15; 
            case IGameOptions.SIZE_SMALL2:    return adj*17;
            case IGameOptions.SIZE_MEDIUM:    return adj*19; 
            case IGameOptions.SIZE_MEDIUM2:   return adj*20; 
            case IGameOptions.SIZE_LARGE:     return adj*21; 
            case IGameOptions.SIZE_LARGE2:    return adj*22; 
            case IGameOptions.SIZE_HUGE:      return adj*23; 
            case IGameOptions.SIZE_HUGE2:     return adj*24; 
            case IGameOptions.SIZE_MASSIVE:   return adj*25; 
            case IGameOptions.SIZE_MASSIVE2:  return adj*26; 
            case IGameOptions.SIZE_MASSIVE3:  return adj*27; 
            case IGameOptions.SIZE_MASSIVE4:  return adj*28; 
            case IGameOptions.SIZE_MASSIVE5:  return adj*29; 
            case IGameOptions.SIZE_INSANE:    return adj*32; 
            case IGameOptions.SIZE_LUDICROUS: return adj*36; 
            default:             return adj*19; 
        }
    }

}
