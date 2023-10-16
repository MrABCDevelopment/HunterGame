package me.dreamdevs.github.huntergame.game;

import lombok.Getter;
import lombok.Setter;
import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.api.events.HGWinGameEvent;
import me.dreamdevs.github.huntergame.api.scoreboard.ScoreboardAPI;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import me.dreamdevs.github.huntergame.utils.Util;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class Game extends BukkitRunnable implements Listener {

    private static final ItemStack SWORD = new ItemStack(Material.IRON_SWORD);

    private String id;
    private int minPlayers;
    private int maxPlayers;
    private int goal;
    private int gameTime;
    private int timer;
    private int spawnMobsTime;
    private boolean enabled = false;
   // private GameType gameType;
    private Location startSpawnLocation;
    private List<Location> mobsLocations;
    private GameState gameState;
    private Map<Player, Integer> players;
    private BukkitTask animalTask;

    private Scoreboard scoreboard;
    private Objective objective;

    private File file;
    private String winner;

    public Game(String id) {
        this.id = id;
        this.gameState = GameState.WAITING;
        this.players = new ConcurrentHashMap<>();
        mobsLocations = new ArrayList<>();

        ScoreboardAPI.createScoreboard(this);

        ItemMeta itemMeta = SWORD.getItemMeta();
        itemMeta.setUnbreakable(true);
        SWORD.setItemMeta(itemMeta);
        Bukkit.getPluginManager().registerEvents(this, HunterGameMain.getInstance());
    }

    public void startGame() {
        runTaskTimer(HunterGameMain.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        ScoreboardAPI.update(this);
        if(gameState == GameState.WAITING) {
            if(players.isEmpty()) return;
            sendTitleToAllPlayers("", HunterGameMain.getInstance().getMessagesManager().getMessage("title-waiting-game").replace("{CURRENT}", String.valueOf(players.size()).replace("{REQUIRE}", String.valueOf(minPlayers))), 0, 25, 25);
            return;
        }
        if(gameState == GameState.STARTING) {
            sendTitleToAllPlayers("", HunterGameMain.getInstance().getMessagesManager().getMessage("title-waiting-game").replace("{TIME}", String.valueOf(timer)), 0, 25, 25);
            if(timer == 0) {
                spawnAnimals();
                setGameState(GameState.RUNNING);

                sendTitleToAllPlayers(HunterGameMain.getInstance().getMessagesManager().getMessage("title-started-game"), HunterGameMain.getInstance().getMessagesManager().getMessage("subtitle-started-game"), 10, 20, 10);

                players.keySet().forEach(player -> {
                    player.getInventory().clear();
                    player.getInventory().setItem(0, SWORD);
                });
                setTimer(gameTime);

                ScoreboardAPI.updateElements(this);
            }
            timer--;
        }
        if(gameState == GameState.RUNNING) {
            players.keySet().forEach(player -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtil.colorize("&6Your place: "+getPlace(player)))));
            if(timer == 0) {
                winner = getWinner();
                Player player = Bukkit.getPlayer(winner);

                HGWinGameEvent hgWinGameEvent = new HGWinGameEvent(player, this);
                Bukkit.getPluginManager().callEvent(hgWinGameEvent);
                setTimer(10);
                setGameState(GameState.ENDING);
                return;
            }
            timer--;
        }
        if(gameState == GameState.ENDING) {
            animalTask.cancel();
            startSpawnLocation.getWorld().getEntities().stream().filter(entity -> (entity.getType() == EntityType.CHICKEN
                    || entity.getType() == EntityType.PIG
                    || entity.getType() == EntityType.COW)).forEachOrdered(Entity::remove);
            if(timer == 0) {
                sendTitleToAllPlayers("", HunterGameMain.getInstance().getMessagesManager().getMessage("title-won-player").replace("{WINNER}", winner), 0, 25, 25);
                setGameState(GameState.RESTARTING);
            }
            timer--;
        }
        if(gameState == GameState.RESTARTING) {
            players.keySet().forEach(player -> {
                player.sendMessage(HunterGameMain.getInstance().getMessagesManager().getMessage("game-stopped"));
                HunterGameMain.getInstance().getGameManager().leaveGame(player, this);
            });
            ScoreboardAPI.updateElements(this);
            setGameState(GameState.WAITING);
        }
    }

    private void sendTitleToAllPlayers(String title, String subTitle, int fadeIn, int stayIn, int fadeOut) {
        players.keySet().forEach(player -> player.sendTitle(ColourUtil.colorize(title), ColourUtil.colorize(subTitle), fadeIn, stayIn, fadeOut));
    }

    public void updatePoints(Player player, int score) {
        this.players.put(player, players.get(player)+score);
        player.setLevel(players.get(player));
        if(players.get(player) >= goal) {
            players.keySet().forEach(p -> p.getInventory().clear());
            winner = player.getDisplayName();
            HGWinGameEvent hgWinGameEvent = new HGWinGameEvent(player, this);
            Bukkit.getPluginManager().callEvent(hgWinGameEvent);
            setTimer(10);
            setGameState(GameState.ENDING);
        }
    }

    private void spawnAnimals() {
        animalTask = Bukkit.getScheduler().runTaskTimer(HunterGameMain.getInstance(), () -> getMobsLocations().forEach(location -> {
            int value = 1;
            MetadataValue metadataValue = null; //new FixedMetadataValue(HunterGameMain.getInstance(), );
            if(Util.chance(0.25)) {
                value = 2;
            } else if(Util.chance(0.15)) {
                value = 3;
            } else if(Util.chance(0.20)) {
                value = 0;
            }
            metadataValue = new FixedMetadataValue(HunterGameMain.getInstance(), value);
            Entity entity = startSpawnLocation.getWorld().spawnEntity(location, EntityType.valueOf(GameMob.values()[Util.getRandomNumber(GameMob.values().length)].name()));
            if (value == 0) {
                entity.setCustomName(ColourUtil.colorize("&d&l-50%"));
            } else {
                entity.setCustomName(ColourUtil.colorize("&a&l+"+value));
            }
            entity.setMetadata("points", metadataValue);
            entity.setCustomNameVisible(true);
        }), 20L, spawnMobsTime*20L);
    }

    public int getPlace(Player player) {
        int place = 1;
        for(Map.Entry<Player, Integer> entry : getPlayers().entrySet()) {
            if(players.get(player) < entry.getValue() && players.get(player) != entry.getValue()) {
                place++;
            }
        }
        return place;
    }

    public String getWinner() {
        Player player = players.keySet().stream().findFirst().get();
        for(Map.Entry<Player, Integer> entry : getPlayers().entrySet()) {
            if(players.get(player) < entry.getValue()) {
                player = entry.getKey();
            }
        }
        return player.getName();
    }

}