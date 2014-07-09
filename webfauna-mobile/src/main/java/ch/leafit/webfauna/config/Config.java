package ch.leafit.webfauna.config;

import android.graphics.Color;
import ch.leafit.gdc.GDCDefaultStyleConfig;
import ch.leafit.webfauna.gdc.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by marius on 07/07/14.
 */
public final class Config {

    public static boolean debug;

    /*colors*/
    public static int actionBarColor;

    public static int gdcDataFieldBackgroundColor;

    public static int gdcDataFieldNameFontColor;
    /*font color if the value of a DataField is valid*/
    public static int gdcDataFieldNameValidFontColor;
    /*font color if the value of a DataField is invalid*/
    public static int gdcDataFieldNameInValidFontColor;
    public static int gdcDataFieldValueFontColor;

    public static int gdcDataFieldDisclosureIndicatorColor;

    public static int gdcDataFieldSeperatorBackgroundColor;
    public static int gdcDataFieldSeperatorFontColor;

    /*Webfauna-Webservice stuff*/
    public static String webfaunaWebserviceBaseURL;
    public static Integer webfaunaWebserviceRequestTimeout;
    public static Integer webfaunaWebservicePort;
    /*sent to server, that it knows from what kind of device the observation comes*/
    public static final String webfaunaAppCodeForWebservice = "WFA-MOB";

    /*REALM-codes*/
    public static final String webfaunaIdentificationMethodRealmRestID = "MTH";
    public static final String webfaunaPrecisionRealmRestID = "PRD";
    public static final String webfaunaEnvironmentRealmRestID = "ENV";
    public static final String webfaunaMilieuRealmRestID = "TYP";
    public static final String webfaunaStructureRealmRestID = "TSP";
    public static final String webfaunaSubstratRealmRestID = "SBT";

    /**
     * defines which WebfaunaGroups are to be downloaded
     */
    public static ArrayList<NeededWebfaunaGroup> neededWebfaunaGroups;

    public static class NeededWebfaunaGroup {
        public String groupRestID;
        public String localImageName;

        public NeededWebfaunaGroup(String groupRestID,String localImageName) {
            this.groupRestID = groupRestID;
            this.localImageName = localImageName;
        }
    }

    static {
        debug = true;

        /*set default Styles of GDC*/
        GDCDefaultStyleConfig.clickDataFieldDefaultStyle = new GDCClickDataFieldStyle();
        GDCDefaultStyleConfig.dateDataFieldDefaultStyle = new GDCDateDataFieldStyle();
        GDCDefaultStyleConfig.integerDataFieldDefaultStyle = new GDCIntegerDataFieldStyle();
        GDCDefaultStyleConfig.listDataFieldDefaultStyle = new GDCListDataFieldStyle();
        GDCDefaultStyleConfig.sectionTitleDataFieldDefaultStyle = new GDCSectionTitleDataFieldStyle();
        GDCDefaultStyleConfig.stringDataFieldDefaultStyle = new GDCStringDataFieldStyle();

        /*initialize member-variables*/

        /*colors*/
        actionBarColor = Color.argb(255,28,140,255);

        gdcDataFieldBackgroundColor = Color.WHITE;

        gdcDataFieldNameFontColor = Color.argb(255,28,140,255);
        gdcDataFieldNameValidFontColor = Color.GREEN;
        gdcDataFieldNameInValidFontColor = Color.RED;
        gdcDataFieldValueFontColor = Color.BLACK;

        gdcDataFieldDisclosureIndicatorColor = Color.BLACK;

        /*background-color of GDCSectionTitleDataField*/
        gdcDataFieldSeperatorBackgroundColor = Color.argb(255,230,230,230);
        gdcDataFieldSeperatorFontColor = Color.argb(255,50,50,50);

        /*Webfauna-Webservice stuff*/
        if(debug) {
            webfaunaWebserviceBaseURL = "https://webfauna-api-test.cscf.ch/api/v1/";
        } else {
            webfaunaWebserviceBaseURL = "https://webfauna-api.cscf.ch/api/v1/";
        }
        webfaunaWebserviceRequestTimeout  = 5000;
        webfaunaWebservicePort = 443;

        neededWebfaunaGroups = new ArrayList<NeededWebfaunaGroup>();
        /*Mammifières*/
        neededWebfaunaGroups.add(new NeededWebfaunaGroup("4","mammifere.jpg"));
        /*Amphibiens*/
        neededWebfaunaGroups.add(new NeededWebfaunaGroup("2","amphibien.jpg"));
        /*Reptiles*/
        neededWebfaunaGroups.add(new NeededWebfaunaGroup("3","reptile.jpg"));
        /*Libellules*/
        neededWebfaunaGroups.add(new NeededWebfaunaGroup("22","odo.jpg"));
        /*Orthoptères*/
        neededWebfaunaGroups.add(new NeededWebfaunaGroup("23","ortho.jpg"));
        /*Lépidoptères*/
        neededWebfaunaGroups.add(new NeededWebfaunaGroup("30","pap.jpg"));
    }

    public static void init() {
        /*initialisation*/
    }

}
