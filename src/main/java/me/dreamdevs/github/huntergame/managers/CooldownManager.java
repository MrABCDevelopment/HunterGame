package me.dreamdevs.github.huntergame.managers;

import lombok.Getter;
import me.dreamdevs.github.huntergame.HunterGameMain;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CooldownManager
{

    private @Getter Map<HashMap<UUID, String>, AtomicInteger> cooldowns;

    public CooldownManager() {
        cooldowns = new ConcurrentHashMap<>();
        onSecond();
    }

    public void setCooldown(UUID uuid, String id, int seconds) {
        HashMap<UUID, String> map = new HashMap<>();
        map.put(uuid, id);
        cooldowns.put(map, new AtomicInteger(seconds));
    }

    private void onSecond() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(HunterGameMain.getInstance(), () -> {
            for(HashMap<UUID, String> map : cooldowns.keySet()) {
                int value = cooldowns.get(map).decrementAndGet();
                if(value<=0) {
                    cooldowns.remove(map);
                }
            }
        }, 0L, 20L);
    }

    public int getPlayerCooldown(UUID uuid, String id) {
        HashMap<UUID, String> map = new HashMap<>();
        map.put(uuid, id);
        if(cooldowns.containsKey(map))
            return cooldowns.get(map).get();
        return 0;
    }

    public boolean isOnCooldown(UUID uuid, String id) {
        return getPlayerCooldown(uuid, id) > 0;
    }

}