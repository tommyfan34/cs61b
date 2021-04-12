package lab14;

import lab14lib.Generator;

public class AcceleratingSawToothGenerator implements Generator {
    private int period;
    private int state;
    private double factor;

    public AcceleratingSawToothGenerator(int period, double factor) {
        this.period = period;
        this.factor = factor;
        state = 0;
    }

    @Override
    public double next() {
        state += 1;
        if (state == period) {
            state = 0;
            period *= factor;
        }
        return normalize((state));
    }

    private double normalize(int num) {
        double ret = (double) num * 2 / period;
        return ret - 1;
    }
}
