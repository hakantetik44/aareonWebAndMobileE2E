package utils;

public class OS {
    public static String OS = "Android";

    public static boolean isAndroid() {
        return OS.equalsIgnoreCase("Android");
    }

    public static boolean isIOS() {
        return OS.equalsIgnoreCase("iOS");
    }

    public static boolean isWeb() {
        return OS.equalsIgnoreCase("Web");
    }
}