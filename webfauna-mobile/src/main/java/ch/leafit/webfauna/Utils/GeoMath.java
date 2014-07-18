package ch.leafit.webfauna.Utils;

/**
 * Created by marius on 15/07/14.
 *
 * Make calculations with coordinate-stuff
 */
public final class GeoMath {

    /**
     *
     * @param lat lat in WGS-system
     * @param lng long in WGS-system
     * @return x of CH03-System
     */
    public static double getCHx(double lat, double lng) {

        // Converts degrees dec to sex
        lat = decimalAngleToSexagesimalAngle(lat);
        lng = decimalAngleToSexagesimalAngle(lng);

        // Converts degrees to seconds (sex)
        lat = sexagesimalAngleToSeconds(lat);
        lng = sexagesimalAngleToSeconds(lng);

        // Axiliary values (% Bern)
        double lat_aux = (lat - 169028.66) / 10000;
        double lng_aux = (lng - 26782.5) / 10000;

        // Process X
        double x = (600072.37 + (211455.93 * lng_aux)) - (10938.51 * lng_aux * lat_aux) - (0.36 * lng_aux * Math.pow(lat_aux, 2)) - (44.54 * Math.pow(lng_aux, 3));
        return x;
    }

    /**
     *
     * @param lat lat in WGS-system
     * @param lng long in WGS-system
     * @return y of CH03-System
     */
    public static double getCHy(double lat, double lng) {
        // Converts degrees dec to sex
        lat = decimalAngleToSexagesimalAngle(lat);
        lng = decimalAngleToSexagesimalAngle(lng);

        // Converts degrees to seconds (sex)
        lat = sexagesimalAngleToSeconds(lat);
        lng = sexagesimalAngleToSeconds(lng);

        // Axiliary values (% Bern)
        double lat_aux = (lat - 169028.66) / 10000;
        double lng_aux = (lng - 26782.5) / 10000;

        double y = ((200147.07 + (308807.95 * lat_aux) + (3745.25 * Math.pow(lng_aux, 2)) + (76.63 * Math.pow(lat_aux, 2))) - (194.56 * Math.pow(lng_aux, 2) * lat_aux)) + (119.79 * Math.pow(lat_aux, 3));
        return y;
    }

    /**
     *
     * @param chX x coordinate in CH03-system
     * @param chY y coordinate in CH03-system
     * @return lat in WGS-system
     */
    public static double getLat(double chX, double chY) {
        // Converts militar to civil and  to unit = 1000km
        // Axiliary values (% Bern)
        double x_aux = (chX - 600000)/1000000;
        double y_aux = (chY - 200000)/1000000;

        // Process lat
        double lat = 16.9023892 +  3.238272 * y_aux -  0.270978 * Math.pow(x_aux, 2) -  0.002528 * Math.pow(y_aux, 2)  -  0.0447   * Math.pow(x_aux, 2) * y_aux -  0.0140   * Math.pow(y_aux, 3);

        // Unit 10000" to 1 " and converts seconds to degrees (dec)
        lat = lat * 100/36;

        return lat;
    }

    /**
     *
     * @param chX x coordinate in CH03-system
     * @param chY y coordinate in CH03-system
     * @return lng in WGS-system
     */
    public static double getLng(double chX, double chY) {
        // Converts militar to civil and  to unit = 1000km
        // Axiliary values (% Bern)
        double x_aux = (chX - 600000)/1000000;
        double y_aux = (chY - 200000)/1000000;

        // Process long
        double lng = 2.6779094 + 4.728982 * x_aux + 0.791484 * x_aux * y_aux + 0.1306   * x_aux * Math.pow(y_aux, 2) - 0.0436   * Math.pow(x_aux, 3);

        // Unit 10000" to 1 " and converts seconds to degrees (dec)
        lng = lng * 100/36;

        return lng;
    }


    /*helper methods*/

    /**
     * Convert decimal angle (degrees) to sexagesimal angle (degrees, minutes and seconds dd.mmss,ss)
     */
    private static double decimalAngleToSexagesimalAngle(double decimalAngle) {
        int deg = (int)  Math.floor(decimalAngle);
        int min = (int)  Math.floor((decimalAngle - deg) * 60);
        double sec = (((decimalAngle - deg) * 60) - min) * 60;

        // Output: dd.mmss(,)ss
        return deg + ((double) min / 100) + (sec / 10000);
    }

    /**
     * Convert sexagesimal angle (degrees, minutes and seconds dd.mmss,ss) to seconds
     */
    private static double sexagesimalAngleToSeconds(double sexagesimalAngle) {
        double deg = 0;
        double min = 0;
        double sec = 0;

        deg = Math.floor(sexagesimalAngle);
        min = Math.floor((sexagesimalAngle - deg) * 100);
        sec = (((sexagesimalAngle - deg) * 100) - min) * 100;

        // Result in degrees sex (dd.mmss)
        return sec + (min * 60) + (deg * 3600);
    }

}
