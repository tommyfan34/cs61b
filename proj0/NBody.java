public class NBody {
    public static double readRadius(String path) {
        In in = new In(path);
        in.readInt();
        double radius = in.readDouble();
        return radius;
    }

    public static Planet[] readPlanets(String path) {
        In in = new In(path);
        int num = in.readInt();
        in.readDouble();
        Planet[] all_planets = new Planet[num];
        for (int i = 0; i < num; i++) {
            double xPos = in.readDouble();
            double yPos = in.readDouble();
            double xVel = in.readDouble();
            double yVel = in.readDouble();
            double mass = in.readDouble();
            String img = in.readString();
            all_planets[i] = new Planet(xPos, yPos, xVel, yVel, mass, img);
        }
        return all_planets;
    }

    public static void main(String[] args) {
        double T = Double.valueOf(args[0]);
        double dt = Double.valueOf(args[1]);
        String filename = args[2];
        double univ_radius = readRadius(filename);
        Planet[] all_planets = readPlanets(filename);
        StdDraw.enableDoubleBuffering();
        double time = 0;
        while (time != T) {
            double[] xForces = new double[all_planets.length];
            double[] yForces = new double[all_planets.length];
            for (int i = 0; i < all_planets.length; i++) {
                xForces[i] = all_planets[i].calcNetForceExertedByX(all_planets);
                yForces[i] = all_planets[i].calcNetForceExertedByY(all_planets);
                all_planets[i].update(dt, xForces[i], yForces[i]);
            }
            StdDraw.setScale(-univ_radius, univ_radius);
            StdDraw.clear();
            StdDraw.picture(0, 75, "images/starfield.jpg");
            for (int i = 0; i < all_planets.length; i++) {
                all_planets[i].draw();
            }
            StdDraw.show();
            StdDraw.pause(10);
            time += dt;
        }
        StdOut.printf("%d\n", all_planets.length);
        StdOut.printf("%.2e\n", univ_radius);
        for (int i = 0; i < all_planets.length; i++) {
            StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                    all_planets[i].xxPos, all_planets[i].yyPos, all_planets[i].xxVel,
                    all_planets[i].yyVel, all_planets[i].mass, all_planets[i].imgFileName);
        }
    }
}
