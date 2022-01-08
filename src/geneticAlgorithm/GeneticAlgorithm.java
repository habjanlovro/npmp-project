package geneticAlgorithm;

import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.particle.BSimBacterium;
import processing.core.PGraphics3D;

import javax.vecmath.Vector3d;
import java.awt.*;

public class GeneticAlgorithm {

    public static void main(String args[]) {
        BSim sim = new BSim();

        final BSimBacterium bacterium = new BSimBacterium(sim, new Vector3d(50,50,50));

        sim.setTicker(new BSimTicker() {
            @Override
            public void tick() {
                bacterium.action();
                bacterium.updatePosition();
            }
        });

        sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
            @Override
            public void scene(PGraphics3D p3d) {
                draw(bacterium, Color.YELLOW);
            }
        });

        sim.preview();
    }
}
