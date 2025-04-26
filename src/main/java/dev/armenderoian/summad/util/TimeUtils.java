package dev.armenderoian.summad.util;

public class TimeUtils {

    public static String formatTime(String format, long seconds) {
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        return String.format(format, days, hours, minutes, secs);
    }

    public static String formatTime(String format, long seconds, boolean showSeconds) {
        if (showSeconds) {
            return formatTime(format, seconds);
        } else {
            long days = seconds / 86400;
            long hours = (seconds % 86400) / 3600;
            long minutes = (seconds % 3600) / 60;

            return String.format(format, days, hours, minutes);
        }
    }
}
