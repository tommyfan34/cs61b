import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {

    private String[][] renderGrid;
    private int depth;
    private double lrlon;
    private double lrlat;
    private double ullon;
    private double ullat;
    private double w;
    private double h;
    private static final int LONGTOFEET = 288200;
    private double rasterullon;
    private double rasterullat;
    private double rasterlrlon;
    private double rasterlrlat;

    public Rasterer() {
        // YOUR CODE HERE
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        Map<String, Object> results = new HashMap<>();
        lrlon = params.get("lrlon");
        lrlat = params.get("lrlat");
        ullon = params.get("ullon");
        ullat = params.get("ullat");
        w = params.get("w");
        h = params.get("h");
        // check the query validity
        boolean valid = ullon <= lrlon && ullat >= lrlat;
        if (lrlon < MapServer.ROOT_ULLON || ullon > MapServer.ROOT_LRLON
        || lrlat > MapServer.ROOT_ULLAT || ullat < MapServer.ROOT_LRLAT) {
            valid = false;
        }
        if (!valid) {
            results.put("query_success", false);
            results.put("depth", -1);
            results.put("render_grid", null);
            results.put("raster_ul_lon", -1);
            results.put("raster_ul_lat", -1);
            results.put("raster_lr_lon", -1);
            results.put("raster_lr_lat", -1);
            return results;
        }
        if (ullon < MapServer.ROOT_ULLON) {
            ullon = MapServer.ROOT_ULLON;
        }
        if (ullat > MapServer.ROOT_ULLAT) {
            ullat = MapServer.ROOT_ULLAT;
        }
        if (lrlon > MapServer.ROOT_LRLON) {
            lrlon = MapServer.ROOT_LRLON;
        }
        if (lrlat < MapServer.ROOT_LRLAT) {
            lrlat = MapServer.ROOT_LRLAT;
        }
        getDepth();
        results.put("depth", depth);
        getImages();
        results.put("render_grid", renderGrid);
        results.put("raster_ul_lon", rasterullon);
        results.put("raster_ul_lat", rasterullat);
        results.put("raster_lr_lon", rasterlrlon);
        results.put("raster_lr_lat", rasterlrlat);
        results.put("query_success", true);
        return results;
    }

    private void getDepth() {
        double lonDPP = (lrlon - ullon) * LONGTOFEET / w;
        double rootLonDPP = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON)
                * LONGTOFEET / MapServer.TILE_SIZE;
        // Have the greatest LonDPP that is less than or equal to the lonDPP of
        // the query box
        int res = (int) Math.floor(Math.log(rootLonDPP / lonDPP) / Math.log(2));
        if (res < 0) {
            res = 0;
        } else if (res > 7) {
            res = 7;
        }
        depth = res;
    }

    private void getImages() {
        double lonDPP = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) / Math.pow(2, depth);
        double latDPP = (MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT) / Math.pow(2, depth);
        int ulx = (int) Math.floor((ullon - MapServer.ROOT_ULLON) / lonDPP);
        int uly = (int) Math.floor((MapServer.ROOT_ULLAT - ullat) / latDPP);
        int lrx = (int) Math.floor((lrlon - MapServer.ROOT_ULLON) / lonDPP);
        int lry = (int) Math.floor((MapServer.ROOT_ULLAT - lrlat) / latDPP);
        if (lrx == (int) Math.pow(2, depth)) {
            lrx -= 1;
        }
        if (lry == (int) Math.pow(2, depth)) {
            lry -= 1;
        }
        renderGrid = new String[lry - uly + 1][lrx - ulx + 1];
        for (int x = ulx; x <= lrx; x++) {
            for (int y = uly; y <= lry; y++) {
                String s = "d" + depth + "_x" + x + "_y" + y + ".png";
                renderGrid[y - uly][x - ulx] = s;
            }
        }
        rasterullon = ulx * lonDPP + MapServer.ROOT_ULLON;
        rasterullat = MapServer.ROOT_ULLAT - uly * latDPP;
        rasterlrlon = (lrx + 1) * lonDPP + MapServer.ROOT_ULLON;
        rasterlrlat = MapServer.ROOT_ULLAT - (lry + 1) * latDPP;
    }

}
