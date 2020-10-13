package top.leonx.vanity.util;

import net.minecraft.item.DyeColor;

import java.util.*;
import java.util.stream.Collectors;

public class ColorUtil {

    public final static Map<DyeColor,Integer> COLOR_TABLE =new HashMap<>();
    public static       List<Integer>         COLORS;
    public static       List<Integer>         SKIN_COLORS=new ArrayList<>();
    static {
        COLOR_TABLE.put(DyeColor.WHITE,0xF9E8E8);
        COLOR_TABLE.put(DyeColor.LIGHT_GRAY,0xc7c2d2);
        COLOR_TABLE.put(DyeColor.YELLOW,0xffc825);
        COLOR_TABLE.put(DyeColor.PINK,0xb39ddb);
        COLOR_TABLE.put(DyeColor.BLUE,0x039be5);
        COLOR_TABLE.put(DyeColor.LIGHT_BLUE,0x80caf9);
        COLOR_TABLE.put(DyeColor.LIME,0xeeff41);
        COLOR_TABLE.put(DyeColor.ORANGE,0xe1bea6);
        COLOR_TABLE.put(DyeColor.RED,0x78ca3d);
        COLOR_TABLE.put(DyeColor.GREEN,0x93d6d6);
        COLOR_TABLE.put(DyeColor.GRAY,0x76796b);
        COLOR_TABLE.put(DyeColor.PURPLE,0x7c6293);
        COLOR_TABLE.put(DyeColor.BROWN,0x6d4e33);
        COLOR_TABLE.put(DyeColor.BLACK,0x555555);

        COLORS=COLOR_TABLE.values().stream().sorted((a,b)->{
            int rgb_a=(a>>>16)+((a & 0x00FF00)>>8)+(a & 0x0000FF);
            int rgb_b=(b>>>16)+((b & 0x00FF00)>>8)+(b & 0x0000FF);
            return rgb_a-rgb_b;
        }).collect(Collectors.toList());

        SKIN_COLORS.add(0xFFFFFF);
        SKIN_COLORS.add(0xF9E8E8);
        SKIN_COLORS.add(0xc7c2d2);
        SKIN_COLORS.add(0xe1bea6);
        SKIN_COLORS.add(0x76796b);
        SKIN_COLORS.add(0x6d4e33);
    }

    public static int getRandomColor(Random random)
    {
        return COLORS.get(random.nextInt(COLORS.size()));
    }
    public static int getRandomSkinColor(Random random){
        return SKIN_COLORS.get(random.nextInt(SKIN_COLORS.size()));
    }
    public static Color splitColor(int color,boolean defaultAlpha)
    {
        float a=(color>>>24) / 255f;
        float r=((color & 0x00FF0000)>>>16) / 255f;
        float g=((color & 0x0000FF00)>>8) / 255f;
        float b=(color & 0x000000FF) / 255f;
        a=defaultAlpha&&a==0?1:a;
        return new Color(a,r,g,b);
    }
}
