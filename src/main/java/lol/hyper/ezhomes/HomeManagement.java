package lol.hyper.ezhomes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomeManagement {
    private static FileWriter writer;
    private static FileReader reader;

    public static void createHome(Player player, String homeName) {
        File homeFile = new File(EzHomes.getInstance().homesPath.toFile(), player.getUniqueId() + ".json");
        // Checks if the player has a home file already.
        // If they do, then read current file then add new JSONObject to it.
        // If they don't, then just put a new JSONObject there.
        // There is probably a better way of doing this, but I have done this method in the past.
        try {
            Location homeLocation = player.getLocation();
            if (homeFile.exists()) {
                JSONParser parser = new JSONParser();
                reader = new FileReader(homeFile);
                Object obj = parser.parse(reader);
                reader.close();
                JSONObject currentHomeFileJSON = (JSONObject) obj;
                Map m = new LinkedHashMap(5);
                m.put("x", homeLocation.getX());
                m.put("y", homeLocation.getY());
                m.put("z", homeLocation.getZ());
                m.put("pitch", homeLocation.getPitch());
                m.put("yaw", homeLocation.getYaw());
                m.put("world", homeLocation.getWorld().getName());
                currentHomeFileJSON.put(homeName, m);
                writer = new FileWriter(homeFile);
                writer.write(currentHomeFileJSON.toJSONString());
            } else {
                JSONObject homeObject = new JSONObject();
                Map m = new LinkedHashMap(5);
                m.put("x", homeLocation.getX());
                m.put("y", homeLocation.getY());
                m.put("z", homeLocation.getZ());
                m.put("pitch", homeLocation.getPitch());
                m.put("yaw", homeLocation.getYaw());
                m.put("world", homeLocation.getWorld().getName());
                homeObject.put(homeName, m);
                writer = new FileWriter(homeFile);
                writer.write(homeObject.toJSONString());
            }
            writer.close();
        } catch (ParseException | IOException e) {
            Bukkit.getLogger().severe("[EzHomes] There was an issue reading file " + homeFile + "!");
            e.printStackTrace();
        }
    }

    public static Location getHomeLocation(Player player, String homeName) {
        File homeFile = new File(EzHomes.getInstance().homesPath.toFile(), player.getUniqueId() + ".json");
        try {
            if (homeFile.exists()) {
                JSONParser parser = new JSONParser();
                reader = new FileReader(homeFile);
                Object obj = parser.parse(reader);
                reader.close();
                JSONObject homeFileJSON = (JSONObject) obj;
                JSONObject home = (JSONObject) homeFileJSON.get(homeName);
                double x = Double.parseDouble(home.get("x").toString());
                double y = Double.parseDouble(home.get("y").toString());
                double z = Double.parseDouble(home.get("z").toString());
                float pitch = Float.parseFloat(home.get("pitch").toString());
                float yaw = Float.parseFloat(home.get("yaw").toString());
                World w = Bukkit.getWorld(home.get("world").toString());
                return new Location(w, x, y, z, pitch, yaw);
            } else {
                return null;
            }
        } catch (ParseException | IOException e) {
            Bukkit.getLogger().severe("[EzHomes] There was an issue reading file " + homeFile + "!");
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> getPlayerHomes(Player player) {
        File homeFile = new File(EzHomes.getInstance().homesPath.toFile(), player.getUniqueId() + ".json");
        try {
            if (homeFile.exists()) {
                ArrayList<String> playerHomes = new ArrayList<>();
                JSONParser parser = new JSONParser();
                reader = new FileReader(homeFile);
                Object obj = parser.parse(reader);
                reader.close();
                JSONObject currentHomeFileJSON = (JSONObject) obj;
                for (Object o : currentHomeFileJSON.keySet()) {
                    playerHomes.add((String) o);
                }
                playerHomes.sort(String.CASE_INSENSITIVE_ORDER);
                return playerHomes;
            } else {
                return null;
            }
        } catch (ParseException | IOException e) {
            Bukkit.getLogger().severe("[EzHomes] There was an issue reading file " + homeFile + "!");
            e.printStackTrace();
            return null;
        }
    }

    public static boolean canPlayerTeleport(Player player) {
        if (EzHomes.getInstance().teleportCooldowns.containsKey(player)) {
            long timeLeft = TimeUnit.NANOSECONDS.toSeconds((System.nanoTime() - EzHomes.getInstance().teleportCooldowns.get(player)) - (long) EzHomes.getInstance().config.getInt("teleport-cooldown"));
            return timeLeft >= (long) EzHomes.getInstance().config.getInt("teleport-cooldown");
        } else {
            return true;
        }
    }

    public static void updateHome(Player player, String homeName) {
        File homeFile = new File(EzHomes.getInstance().homesPath.toFile(), player.getUniqueId() + ".json");
        try {
            Location newLocation = player.getLocation();
            JSONParser parser = new JSONParser();
            reader = new FileReader(homeFile);
            Object obj = parser.parse(reader);
            reader.close();
            JSONObject homeFileJSON = (JSONObject) obj;
            homeFileJSON.remove(homeName);
            Map m = new LinkedHashMap(5);
            m.put("x", newLocation.getX());
            m.put("y", newLocation.getY());
            m.put("z", newLocation.getZ());
            m.put("pitch", newLocation.getPitch());
            m.put("yaw", newLocation.getYaw());
            m.put("world", newLocation.getWorld().getName());
            homeFileJSON.put(homeName, m);
            writer = new FileWriter(homeFile);
            writer.write(homeFileJSON.toJSONString());
            writer.close();
        } catch (ParseException | IOException e) {
            Bukkit.getLogger().severe("[EzHomes] There was an issue reading file " + homeFile + "!");
            e.printStackTrace();
        }
    }
    public static void deleteHome(Player player, String homeName) {
        File homeFile = new File(EzHomes.getInstance().homesPath.toFile(), player.getUniqueId() + ".json");
        try {
            JSONParser parser = new JSONParser();
            reader = new FileReader(homeFile);
            Object obj = parser.parse(reader);
            reader.close();
            JSONObject homeFileJSON = (JSONObject) obj;
            homeFileJSON.remove(homeName);
            writer = new FileWriter(homeFile);
            writer.write(homeFileJSON.toJSONString());
            writer.close();
        } catch (ParseException | IOException e) {
            Bukkit.getLogger().severe("[EzHomes] There was an issue reading file " + homeFile + "!");
            e.printStackTrace();
        }
    }
}
