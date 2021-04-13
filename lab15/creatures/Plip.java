package creatures;
import huglife.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Random;

/** An implementation of a motile pacifist photosynthesizer.
 *  @author Josh Hug
 */
public class Plip extends Creature {
    private static final double loss = 0.15;
    private static final double gain = 0.2;
    private static final double maxEnergy = 2;
    private Random random = new Random();

    /** red color. */
    private int r;
    /** green color. */
    private int g;
    /** blue color. */
    private int b;

    /** creates plip with energy equal to E. */
    public Plip(double e) {
        super("plip");
        r = 99;
        g = 0;
        b = 76;
        energy = e;
        if (energy >= 2) {
            energy = 2;
        }
    }

    /** creates a plip with energy equal to 1. */
    public Plip() {
        this(1);
    }

    /** Should return a color with red = 99, blue = 76, and green that varies
     *  linearly based on the energy of the Plip. If the plip has zero energy,
     *  it should have a green value of 63. If it has max energy, it should
     *  have a green value of 255. The green value should vary with energy
     *  linearly in between these two extremes. It's not absolutely vital
     *  that you get this exactly correct.
     */
    public Color color() {
        g = (int) ((255 - 63) * energy / maxEnergy + 63);
        return color(r, g, b);
    }

    /** Do nothing with C, Plips are pacifists. */
    public void attack(Creature c) {
    }

    /** Plips should lose 0.15 units of energy when moving. If you want to
     *  to avoid the magic number warning, you'll need to make a
     *  private static final variable. This is not required for this lab.
     */
    public void move() {
        energy -= 0.15;
    }


    /** Plips gain 0.2 energy when staying due to photosynthesis. */
    public void stay() {
        energy += 0.2;
        if (energy >= maxEnergy) {
            energy = maxEnergy;
        }
    }

    /** Plips and their offspring each get 50% of the energy, with none
     *  lost to the process. Now that's efficiency! Returns a baby
     *  Plip.
     */
    public Plip replicate() {
        Plip ret = new Plip(energy / 2);
        energy /= 2;
        return ret;
    }

    /** Plips take exactly the following actions based on NEIGHBORS:
     *  1. If no empty adjacent spaces, STAY.
     *  2. Otherwise, if energy >= 1, REPLICATE.
     *  3. Otherwise, if any Cloruses, MOVE with 50% probability.
     *  4. Otherwise, if nothing else, STAY
     *
     *  Returns an object of type Action. See Action.java for the
     *  scoop on how Actions work. See SampleCreature.chooseAction()
     *  for an example to follow.
     */
    public Action chooseAction(Map<Direction, Occupant> neighbors) {
        ArrayList<Direction> emptyDirs = new ArrayList<>();
        boolean hasClorus = false;
        for (Direction d : neighbors.keySet()) {
            Occupant o = neighbors.get(d);
            if (o.name().equals("empty")) {
                emptyDirs.add(d);
            } else if (o.name().equals("clorus")) {
                hasClorus = true;
            }
        }
        if (emptyDirs.isEmpty()) {
            return new Action(Action.ActionType.STAY);
        } else {
            if (energy() >= 1) {
                int index = random.nextInt(emptyDirs.size());
                return new Action(Action.ActionType.REPLICATE, emptyDirs.get(index));
            } else if (hasClorus) {
                int move = random.nextInt(2);
                if (move == 1) {
                    int index = random.nextInt(emptyDirs.size());
                    return new Action(Action.ActionType.MOVE, emptyDirs.get(index));
                }
            }
        }
        return new Action(Action.ActionType.STAY);
    }
}
