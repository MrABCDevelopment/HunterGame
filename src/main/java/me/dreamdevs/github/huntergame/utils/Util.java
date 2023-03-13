package me.dreamdevs.github.huntergame.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Util {

    public static Location getStringLocation(String location, boolean precised) {
        if(location == null) return null;
        try {
            String[] params = location.split(":");
            if(precised) {
                double x = Double.parseDouble(params[0]);
                double y = Double.parseDouble(params[1]);
                double z = Double.parseDouble(params[2]);
                double pitch = Double.parseDouble(params[3]);
                double yaw  = Double.parseDouble(params[4]);
                String world = params[5];
                return new Location(Bukkit.getWorld(world),x,y,z, (float) pitch, (float) yaw);
            }
            int x = Integer.parseInt(params[0]);
            int y = Integer.parseInt(params[1]);
            int z = Integer.parseInt(params[2]);
            String world = params[3];
            return new Location(Bukkit.getWorld(world),x,y,z);
        } catch (Exception e) {

        }
        return null;
    }

    public static String getLocationString(Location location, boolean precised) {
        if(location == null) return null;
        if(precised)
            return location.getX()+":"+location.getY()+":"+location.getZ()+":"+location.getPitch()+":"+location.getYaw()+":"+location.getWorld().getName();
        return location.getBlockX()+":"+location.getBlockY()+":"+location.getBlockZ()+":"+location.getWorld().getName();
    }

    public static void sendPluginMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(ColourUtil.colorize(message));
    }


}
