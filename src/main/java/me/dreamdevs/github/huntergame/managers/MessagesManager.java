package me.dreamdevs.github.huntergame.managers;

import lombok.Getter;
import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import me.dreamdevs.github.huntergame.utils.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MessagesManager {

    private Map<String, String> messages;

    public MessagesManager(HunterGameMain plugin) {
        messages = new HashMap<>();
        load(plugin);
    }

    public void load(HunterGameMain plugin) {
        messages.clear();
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("messages");
        section.getKeys(false).forEach(s -> messages.put(s, ColourUtil.colorize(section.getString(s))));
        Util.sendPluginMessage("&aLoaded messages!");
    }

}