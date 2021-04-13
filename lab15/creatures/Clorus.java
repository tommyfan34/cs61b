package creatures;

import huglife.Action;
import huglife.Creature;
import huglife.Direction;
import huglife.Occupant;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Clorus extends Creature {
    private Random random = new Random();
    /** red color. */
    private int r;
    /** green color. */
    private int g;
    /** blue color. */
    private int b;

    public Clorus(double e) {
        super("clorus");
        r = 34;
        g = 0;
        b = 231;
    }

    public Clorus() {
        this(1);
    }

    @Override
    public Color color() {
        return color(r, g, b);
    }

    @Override
    public void attack(Creature c) {
        energy += c.energy();
    }

    @Override
    public void move() {
        energy -= 0.03;
    }

    @Override
    public void stay() {
        energy -= 0.01;
    }

    @Override
    public Clorus replicate() {
        Clorus ret = new Clorus(energy / 2);
        energy /= 2;
        return ret;
    }

    @Override
    public Action chooseAction(Map<Direction, Occupant> neighbors) {
        ArrayList<Direction> emptyDirs = new ArrayList<>();
        ArrayList<Direction> plips = new ArrayList<>();
        for (Direction d : neighbors.keySet()) {
            Occupant o = neighbors.get(d);
            if (o.name().equals("empty")) {
                emptyDirs.add(d);
            } else if (o.name().equals("plip")) {
                plips.add(d);
            }
        }
        if (emptyDirs.isEmpty()) {
            return new Action(Action.ActionType.STAY);
        } else {
            if (!plips.isEmpty()) {
                int index = random.nextInt(plips.size());
                return new Action(Action.ActionType.ATTACK, plips.get(index));
            } else if (energy >= 1) {
                int index = random.nextInt(emptyDirs.size());
                return new Action(Action.ActionType.REPLICATE, emptyDirs.get(index));
            } else {
                int index = random.nextInt(emptyDirs.size());
                return new Action(Action.ActionType.MOVE, emptyDirs.get(index));
            }
        }
    }
}
