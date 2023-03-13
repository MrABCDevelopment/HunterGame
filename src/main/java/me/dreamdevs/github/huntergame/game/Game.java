package me.dreamdevs.github.huntergame.game;

import lombok.Getter;
import lombok.Setter;
import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.api.events.HGWinGameEvent;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import org.bukkit.*;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
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
    private int time;
    private Location startSpawnLocation;
    private List<Location> mobsSpawnLocations;
    private Scoreboard scoreboard;
    private Objective objective;
    private GameState gameState;
    private Map<Player, Integer> players;

    private String winner;

    public Game(String id) {
        this.id = id;
        this.gameState = GameState.WAITING;
        players = new ConcurrentHashMap<>();
        mobsSpawnLocations = new ArrayList<>();
        ItemMeta itemMeta = SWORD.getItemMeta();
        itemMeta.setUnbreakable(true);
        SWORD.setItemMeta(itemMeta);
        Bukkit.getPluginManager().registerEvents(this, HunterGameMain.getInstance());
    }

    public void startGame() {
        setupScoreboard();
        runTaskTimer(HunterGameMain.getInstance(), 20L, 20L);
    }

    public void restartAllSettings() {
        cancel();
        startGame();
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
            players.forEach((player, integer) -> player.sendTitle("", ColourUtil.colorize("&8Waiting..."), 0, 25, 25));
            if(players.size() >= minPlayers) {
                time = 30;
                gameState = GameState.STARTING;
            }
            return;
        }
        if(gameState == GameState.STARTING) {
            players.forEach((player, integer) -> player.sendTitle("", ColourUtil.colorize("&aStarting in "+time+" seconds..."), 0, 25, 25));
            if(players.size() < minPlayers) {
                gameState = GameState.WAITING;
                return;
            }
            if(time == 0) {
                gameState = GameState.RUNNING;
                players.keySet().forEach(player -> {
                    player.sendTitle(ColourUtil.colorize("&6&lGOOD LUCK!"), ColourUtil.colorize("&aGame has started!"), 10, 20, 10);
                    player.getInventory().clear();
                    player.getInventory().setItem(0, SWORD);
                });
                time = 120;
            }
            time--;
        }
        if(gameState == GameState.RUNNING) {
            objective.getScore(TIME).setScore(time);
            players.forEach((player, integer) -> objective.getScore(player.getName()+": ").setScore(integer));
            if(time > 0) {
                spawnAnimals(EntityType.CHICKEN, "&aChicken &a&l+1");
                time--;
            } else {
                gameState = GameState.ENDING;
            }
        }
        if(gameState == GameState.ENDING) {
            startSpawnLocation.getWorld().getEntities().stream().filter(entity -> entity.getType() == EntityType.CHICKEN).forEachOrdered(Entity::remove);
            time--;
            if(time>0) {
                players.forEach((player, integer) -> player.sendTitle("", ColourUtil.colorize("&a"+winner+" has won this game!"), 0, 25, 25));
            } else {
                gameState = GameState.RESTARTING;
            }
        }
        if(gameState == GameState.RESTARTING) {
            for(Map.Entry<Player, Integer> entry : players.entrySet()) {
                entry.getKey().sendMessage(ChatColor.RED+"The game has stopped!");
                HunterGameMain.getInstance().getGameManager().leaveGame(entry.getKey(), this);
            }
            setupScoreboard();
            gameState = GameState.WAITING;
        }
    }

    @EventHandler
    public void killEntity(EntityDeathEvent event) {
        if(event.getEntity().getType() == EntityType.CHICKEN) {
            event.setDroppedExp(0);
            event.getDrops().clear();
            updateStatsAndScoreboard(event.getEntity().getKiller(), 1);
        }
    }

    @EventHandler
    public void dropEvent(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void damageEvent(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            event.setCancelled(true);
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
            time = 10;
            players.keySet().forEach(p -> p.getInventory().clear());
            gameState = GameState.ENDING;
            winner = player.getDisplayName();
            HGWinGameEvent hgWinGameEvent = new HGWinGameEvent(player, this);
            Bukkit.getPluginManager().callEvent(hgWinGameEvent);
        }
    }

    private void spawnAnimals(EntityType entityType, String displayName) {
        getMobsSpawnLocations().forEach(location -> {
            Chicken chicken = (Chicken) location.getWorld().spawnEntity(location, entityType);
            chicken.setCustomName(ColourUtil.colorize(displayName));
            chicken.setCustomNameVisible(true);
        });
    }

}