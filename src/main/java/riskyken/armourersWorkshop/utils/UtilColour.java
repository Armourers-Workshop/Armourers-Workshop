package riskyken.armourersWorkshop.utils;

public class UtilColour {

    public static int getMinecraftColor(int meta) {
        if (meta == 0) {
            return 16777215;
        }
        if (meta == 1) {
            return 14188339;
        }
        if (meta == 2) {
            return 11685080;
        }
        if (meta == 3) {
            return 6724056;
        }
        if (meta == 4) {
            return 15066419;
        }
        if (meta == 5) {
            return 8375321;
        }
        if (meta == 6) {
            return 15892389;
        }
        if (meta == 7) {
            return 5000268;
        }
        if (meta == 8) {
            return 10066329;
        }
        if (meta == 9) {
            return 5013401;
        }
        if (meta == 10) {
            return 8339378;
        }
        if (meta == 11) {
            return 3361970;
        }
        if (meta == 12) {
            return 6704179;
        }
        if (meta == 13) {
            return 6717235;
        }
        if (meta == 14) {
            return 10040115;
        }
        if (meta == 15) {
            return 1644825;
        }
        return 0;
    }

    public static String getMinecraftColorName(int meta) {
        if (meta == 0) {
            return "White";
        }
        if (meta == 1) {
            return "Orange";
        }
        if (meta == 2) {
            return "Magenta";
        }
        if (meta == 3) {
            return "Light blue";
        }
        if (meta == 4) {
            return "Yellow";
        }
        if (meta == 5) {
            return "Lime";
        }
        if (meta == 6) {
            return "Pink";
        }
        if (meta == 7) {
            return "Gray";
        }
        if (meta == 8) {
            return "Light gray";
        }
        if (meta == 9) {
            return "Cyan";
        }
        if (meta == 10) {
            return "Purple";
        }
        if (meta == 11) {
            return "Blue";
        }
        if (meta == 12) {
            return "Brown";
        }
        if (meta == 13) {
            return "Green";
        }
        if (meta == 14) {
            return "Red";
        }
        if (meta == 15) {
            return "Black";
        }
        return "?";
    }

    public static String getMinecraftColorOreName(int meta) {
        if (meta == 0) {
            return "dyeWhite";
        }
        if (meta == 1) {
            return "dyeOrange";
        }
        if (meta == 2) {
            return "dyeMagenta";
        }
        if (meta == 3) {
            return "dyeLightBlue";
        }
        if (meta == 4) {
            return "dyeYellow";
        }
        if (meta == 5) {
            return "dyeLime";
        }
        if (meta == 6) {
            return "dyePink";
        }
        if (meta == 7) {
            return "dyeGray";
        }
        if (meta == 8) {
            return "dyeLightGray";
        }
        if (meta == 9) {
            return "dyeCyan";
        }
        if (meta == 10) {
            return "dyePurple";
        }
        if (meta == 11) {
            return "dyeBlue";
        }
        if (meta == 12) {
            return "dyeBrown";
        }
        if (meta == 13) {
            return "dyeGreen";
        }
        if (meta == 14) {
            return "dyeRed";
        }
        if (meta == 15) {
            return "dyeBlack";
        }
        return "?";
    }
}
