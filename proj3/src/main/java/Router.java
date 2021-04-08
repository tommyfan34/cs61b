import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Objects;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     * @param g The graph to use.
     * @param stlon The longitude of the start location.
     * @param stlat The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {
        List<Long> ret = new ArrayList<>();
        long sid = g.closest(stlon, stlat);
        GraphDB.Node s = g.nodes.get(sid);
        long tid = g.closest(destlon, destlat);
        GraphDB.Node t = g.nodes.get(tid);
        GraphDB.Node n = s;
        for (Long l : g.vertices()) {
            g.nodes.get(l).distToSrc = Double.MAX_VALUE;
            g.nodes.get(l).marked = false;
            g.nodes.get(l).edgeTo = -1;
        }
        s.distToSrc = 0;

        Comparator<GraphDB.Node> cmp = new Comparator<GraphDB.Node>() {
            @Override
            public int compare(GraphDB.Node o1, GraphDB.Node o2) {
                double h1 = g.distance(o1.lon, o1.lat, t.lon, t.lat);
                double h2 = g.distance(o2.lon, o2.lat, t.lon, t.lat);
                double res = h1 + o1.distToSrc - h2 - o2.distToSrc;
                if (res < 0) {
                    return -1;
                } else if (res > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
        PriorityQueue<GraphDB.Node> fringe = new PriorityQueue<>(cmp);

        fringe.add(n);
        while (!fringe.isEmpty()) {
            n = fringe.remove();
            n.marked = true;
            if (n.ref == t.ref) {
                break;
            }
            for (Long l : g.adjacent(n.ref)) {
                if (!g.nodes.get(l).marked) {
                    if (n.distToSrc + g.distance(n.ref, l) < g.nodes.get(l).distToSrc) {
                        g.nodes.get(l).distToSrc = n.distToSrc + g.distance(n.ref, l);
                        g.nodes.get(l).edgeTo = n.ref;
                    }
                    fringe.add(g.nodes.get(l));
                }
            }
        }
        long pt = tid;
        while (pt != sid) {
            ret.add(pt);
            pt = g.nodes.get(pt).edgeTo;
        }
        ret.add(pt);
        Collections.reverse(ret);
        return ret;
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     * @param g The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        List<NavigationDirection> ret = new ArrayList<>();
        NavigationDirection current = new NavigationDirection();
        double dist = 0;
        double bearing = 0;
        for (int i = 0; i < route.size(); i++) {
            Long l = route.get(i);
            if (i == 0) {
                current.direction = NavigationDirection.START;
                current.way = g.getWay(l, route.get(i + 1));
                dist = g.distance(l, route.get(i + 1));
                bearing = g.bearing(l, route.get(i + 1));
            } else if (i == route.size() - 1) {
                current.distance = dist;
                ret.add(current);
            } else {
                if (g.getWay(l, route.get(i + 1)).equals(current.way)) {
                    dist += g.distance(l, route.get(i + 1));
                    bearing = g.bearing(l, route.get(i + 1));
                } else {
                    current.distance = dist;
                    ret.add(current);
                    current = new NavigationDirection();
                    current.way = g.getWay(l, route.get(i + 1));
                    current.direction = getDirection(bearing, g.bearing(l, route.get(i + 1)));
                    dist = g.distance(l, route.get(i + 1));
                    bearing = g.bearing(l, route.get(i + 1));
                }
            }
        }
        return ret;
    }

    private static int getDirection(double b1, double b2) {
        double shift = b2 - b1;
        if (shift > 180) {
            shift -= 360;
        } else if (shift < -180) {
            shift += 360;
        }
        if (shift <= 15 && shift >= -15) {
            return NavigationDirection.STRAIGHT;
        } else if (shift < -15 && shift >= -30) {
            return NavigationDirection.SLIGHT_LEFT;
        } else if (shift > 15 && shift <= 30) {
            return NavigationDirection.SLIGHT_RIGHT;
        } else if (shift < -30 && shift >= -100) {
            return NavigationDirection.LEFT;
        } else if (shift > 30 && shift <= 100) {
            return NavigationDirection.RIGHT;
        } else if (shift < -100) {
            return NavigationDirection.SHARP_LEFT;
        } else {
            return NavigationDirection.SHARP_RIGHT;
        }
    }


    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions.*/
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /** Default name for an unknown way. */
        public static final String UNKNOWN_ROAD = "unknown road";
        
        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction a given NavigationDirection represents.*/
        int direction;
        /** The name of the way I represent. */
        String way;
        /** The distance along this way I represent. */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                    && way.equals(((NavigationDirection) o).way)
                    && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
