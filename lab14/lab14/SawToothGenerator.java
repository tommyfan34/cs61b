package lab14;

import lab14lib.Generator;

public class SawToothGenerator implements Generator {
    private int state;
    private int period;
    public SawToothGenerator(int period) {
        state = 0;
        this.period = period;
    }

    @Override
    public double next() {
        state += 1;
        state = state % period;
        return normalize(state);
    }

    private double normalize(int num) {
        double ret = (double) num * 2 / period;
        return ret - 1;
    }
}
