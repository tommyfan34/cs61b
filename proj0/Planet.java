public class Planet {
    public double xxPos;
    public double yyPos;
    public double xxVel;
    public double yyVel;
    public double mass;
    public String imgFileName;
    private double G = 6.67e-11;

    public Planet(double xP, double yP, double xV, double yV, double m, String img) {
        xxPos = xP;
        yyPos = yP;
        xxVel = xV;
        yyVel = yV;
        mass = m;
        imgFileName = img;
    }

    public Planet(Planet p){
        xxPos = p.xxPos;
        yyPos = p.yyPos;
        xxVel = p.xxVel;
        yyVel = p.yyVel;
        mass = p.mass;
        imgFileName = p.imgFileName;
    }

    public double calcDistance(Planet p) {
        double ret;
        ret = Math.sqrt(Math.pow((xxPos - p.xxPos), 2) + Math.pow((yyPos - p.yyPos), 2));
        return ret;
    }

    public double calcForceExertedBy(Planet p) {
        double force = G * mass * p.mass / (Math.pow(calcDistance(p), 2));
        return force;
    }

    public double calcForceExertedByX(Planet p) {
        double dx = p.xxPos - xxPos;
        double r = calcDistance(p);
        double force = calcForceExertedBy(p);
        return dx * force / r;
    }

    public double calcForceExertedByY(Planet p) {
        double dy = p.yyPos - yyPos;
        double r = calcDistance(p);
        double force = calcForceExertedBy(p);
        return dy * force / r;
    }

    public double calcNetForceExertedByX(Planet[] allPlanets) {
        int len = allPlanets.length;
        double ret = 0;
        for (int i = 0; i < len; i++) {
            if (allPlanets[i] == this) continue;
            ret += calcForceExertedByX(allPlanets[i]);
        }
        return ret;
    }

    public double calcNetForceExertedByY(Planet[] allPlanets) {
        int len = allPlanets.length;
        double ret = 0;
        for (int i = 0; i < len; i++) {
            if (allPlanets[i] == this) continue;
            ret += calcForceExertedByY(allPlanets[i]);
        }
        return ret;
    }

    public void update(double dt, double fx, double fy) {
        double ax = fx / mass;
        double ay = fy / mass;
        this.xxVel += ax * dt;
        this.yyVel += ay * dt;
        this.xxPos += xxVel * dt;
        this.yyPos += yyVel * dt;
    }

    public void draw() {
        StdDraw.picture(xxPos, yyPos, "images/" + imgFileName);
    }
}
