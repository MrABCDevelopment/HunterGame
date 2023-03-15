package me.dreamdevs.github.huntergame.game;

import lombok.Getter;
import lombok.Setter;
import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.api.events.HGWinGameEvent;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import me.dreamdevs.github.huntergame.utils.Util;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class Game extends BukkitRunnable implements Listener {

    private static final String HEADER = ColourUtil.colorize("  &a&lHunter&e&lGame  ");
    private static final String TIME = ColourUtil.colorize("&cTime: ");
    private static final String GOAL = ColourUtil.colorize("&cGoal: ");
    private static final ItemStack SWORD = new ItemStack(Material.IRON_SWORD);

    private String id;
    private int minPlayers;
    private int maxPlayers;
    private int goal;
    private int gameTime;
    private int timer;
    private int spawnMobsTime;
    private GameType gameType;
    private Location startSpawnLocation;
    private Map<Location, String> mobsLocations;
    private Scoreboard scoreboard;
    private Objective objective;
    private GameState gameState;
    private Map<Player, Integer> players;
    private BukkitTask animalTask;

    private File file;
    private String winner;

    public Game(String id) {
        this.id = id;
        this.gameState = GameState.WAITING;
        players = new ConcurrentHashMap<>();
        mobsLocations = new HashMap<>();
        ItemMeta itemMeta = SWORD.getItemMeta();
        itemMeta.setUnbreakable(true);
        SWORD.setItemMeta(itemMeta);
        Bukkit.getPluginManager().registerEvents(this, HunterGameMain.getInstance());
    }

    public void startGame() {
        setupScoreboard();
        runTaskTimer(HunterGameMain.getInstance(), 20L, 20L);
    }

    private void setupScoreboard() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("arenaboard", "ab-"+id);
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(HEADER);
        objective.getScore(GOAL).setScore(goal);
    }

    @Override
    public void run() {
        if(gameState == GameState.WAITING) {
            if(players.isEmpty()) return;
            players.forEach((player, integer) -> player.sendTitle("", HunterGameMain.getInstance().getMessagesManager().getMessages().get("title-waiting-game").replace("{CURRENT}", String.valueOf(players.size())).replace("{REQUIRE}", String.valueOf(minPlayers)), 0, 25, 25));
            if(players.size() >= minPlayers) {
                timer = 30;
                gameState = GameState.STARTING;
            }
            return;
        }
        if(gameState == GameState.STARTING) {
            players.forEach((player, integer) -> player.sendTitle("", HunterGameMain.getInstance().getMessagesManager().getMessages().get("title-starting-game").replace("{TIME}", String.valueOf(timer)), 0, 25, 25));
            if(players.size() < minPlayers) {
                gameState = GameState.WAITING;
                return;
            }
            if(timer == 0) {
                spawnAnimals();
                gameState = GameState.RUNNING;
                players.keySet().forEach(player -> {
                    player.sendTitle(HunterGameMain.getInstance().getMessagesManager().getMessages().get("title-started-game"), HunterGameMain.getInstance().getMessagesManager().getMessages().get("subtitle-started-game"), 10, 20, 10);
                    player.getInventory().clear();
                    player.getInventory().setItem(0, SWORD);
                });
                timer = gameTime;
            }
            timer--;
        }
        if(gameState == GameState.RUNNING) {
            objective.getScore(TIME).setScore(timer);
            players.forEach((player, integer) -> objective.getScore(player.getName()+": ").setScore(integer));
            if(timer > 0) {
                timer--;
            } else {
                Player player = players.keySet().stream().findFirst().get();
                for(Map.Entry<Player, Integer> entry : getPlayers().entrySet()) {
                    if(players.get(player) < entry.getValue()) {
                        player = entry.getKey();
                    }
                }
                winner = player.getName();
                HGWinGameEvent hgWinGameEvent = new HGWinGameEvent(player, this);
                Bukkit.getPluginManager().callEvent(hgWinGameEvent);
                timer = 10;
                gameState = GameState.ENDING;
            }
        }
        if(gameState == GameState.ENDING) {
            animalTask.cancel();
            startSpawnLocation.getWorld().getEntities().stream().filter(entity -> (entity.getType() == EntityType.CHICKEN
                    || entity.getType() == EntityType.PIG
                    || entity.getType() == EntityType.COW)).forEachOrdered(Entity::remove);
            timer--;
            if(timer>0) {
                players.forEach((player, integer) -> player.sendTitle("", HunterGameMain.getInstance().getMessagesManager().getMessages().get("title-won-player").replace("{WINNER}", winner), 0, 25, 25));
            } else {
                gameState = GameState.RESTARTING;
            }
        }
        if(gameState == GameState.RESTARTING) {
            for(Map.Entry<Player, Integer> entry : players.entrySet()) {
                entry.getKey().sendMessage(HunterGameMain.getInstance().getMessagesManager().getMessages().get("game-stopped"));
                HunterGameMain.getInstance().getGameManager().leaveGame(entry.getKey(), this);
            }
            setupScoreboard();
            gameState = GameState.WAITING;
        }
    }

    @EventHandler
    public void killEntity(EntityDeathEvent event) {
        if(event.getEntity() instanceof Player && event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }

        if(event.getEntity().getKiller() != null && (event.getEntity() instanceof Chicken ||
                event.getEntity() instanceof Cow || event.getEntity() instanceof Pig)) {
            event.setDroppedExp(0);
            event.getDrops().clear();
            if(event.getEntity().getCustomName().equalsIgnoreCase(ColourUtil.colorize("&a&l+1")))
                updateStatsAndScoreboard(event.getEntity().getKiller(), 1);
            else if(event.getEntity().getCustomName().equalsIgnoreCase(ColourUtil.colorize("&d&l-50%")))
                updateStatsAndScoreboard(event.getEntity().getKiller(), -(players.get(event.getEntity().getKiller())/2));
        }

    }

    @EventHandler
    public void damageEvent(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            if(gameType == GameType.CLASSIC || gameState == GameState.WAITING || gameState == GameState.ENDING || gameState == GameState.STARTING)
                event.setCancelled(true);
            else
                if(gameType == GameType.PVP) {
                    if(((Player) event.getEntity()).getHealth() <= 4.0) {
                        ((Player) event.getEntity()).setHealth(20);
                        event.getEntity().teleport(startSpawnLocation);
                    }
                }
        }
    }

    @EventHandler
    public void quitEvent(PlayerQuitEvent event) {
        players.remove(event.getPlayer());
        event.setQuitMessage(null);
    }

    private void updateStatsAndScoreboard(Player player, int score) {
        players.put(player, players.get(player)+score);
        objective.getScore(player.getName()+": ").setScore(players.get(player));
        if(players.get(player) == goal) {
            players.keySet().forEach(p -> p.getInventory().clear());
            winner = player.getDisplayName();
            HGWinGameEvent hgWinGameEvent = new HGWinGameEvent(player, this);
            Bukkit.getPluginManager().callEvent(hgWinGameEvent);
            timer = 10;
            gameState = GameState.ENDING;
        }
    }

    private void spawnAnimals() {
        animalTask = Bukkit.getScheduler().runTaskTimer(HunterGameMain.getInstance(), () -> getMobsLocations().forEach((key, value) -> {
            Entity entity = startSpawnLocation.getWorld().spawnEntity(key, EntityType.valueOf(value.toUpperCase()));
            if (Util.chance(0.11)) {
                entity.setCustomName(ColourUtil.colorize("&d&l-50%"));
            } else {
                entity.setCustomName(ColourUtil.colorize("&a&l+1"));
            }
            entity.setCustomNameVisible(true);
        }), 20L, spawnMobsTime*20L);
    }

}