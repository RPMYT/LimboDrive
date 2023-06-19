package limbo.drive.module.util;

import java.text.DecimalFormat;

public class StringHelper {

    public static String formatNumber(double number)
    {
        return formatNumber(number,"0.00");
    }
    public static String formatNumber(double number, String decialFormat)
    {
        if (number > 1000000000000000D)
        {
            return new DecimalFormat(decialFormat+"Q").format((number / 1000000000000000.00D));
        }
        if (number > 1000000000000D)
        {
            return new DecimalFormat(decialFormat+"T").format((number / 1000000000000.00D));
        }
        else if (number > 1000000000D)
        {
            return new DecimalFormat(decialFormat+"B").format((number / 1000000000.00D));
        }
        else if (number > 1000000D) {
            return new DecimalFormat(decialFormat+"M").format((number / 1000000.00D));
        }
        else if (number > 1000D)
        {
            return new DecimalFormat(decialFormat+"K").format((number / 1000.00D));
        }
        else {
            return new DecimalFormat(decialFormat).format(number);
        }
    }

    public static String formatRemainingTime(float seccounds)
    {
        return formatRemainingTime(seccounds,false);
    }

    public static String formatRemainingTime(float seccounds,boolean shotSufix)
    {
        if (seccounds > 3600)
        {
            return String.format("%s%s", Math.round(seccounds / 3600),shotSufix ? "h" : " hr");
        }
        else if (seccounds > 60 && seccounds < 60 * 60)
        {
            return String.format("%s%s", Math.round(seccounds / 60),shotSufix ? "m" : " min");
        }else
        {
            return String.format("%s%s", Math.round(seccounds),shotSufix ? "s" : " sec");
        }
    }

    public static String addPrefix(String name,String prefix)
    {
        if (prefix.endsWith("-"))
        {
            return prefix.substring(0,prefix.length()-2) + Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        else
        {
            return prefix + " " + name;
        }
    }

    public static String addSuffix(String name, String suffix)
    {
        if (suffix.startsWith("-"))
        {
            return name + suffix.substring(1);
        }
        else
        {
            return name + " " + suffix;
        }
    }
}
