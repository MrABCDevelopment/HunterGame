package me.dreamdevs.github.huntergame.game;

import lombok.Getter;
import lombok.Setter;
import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter @Setter
public class Arena extends BukkitRunnable {

    private String id;
    private int minPlayers;
    private int maxPlayers;
    private int gameTime;
    private int timer;
    private Location startLocation;
    private List<Location> survivorsLocations;
    private List<Location> pagesLocations;
    private ArenaState arenaState;
    private Map<Player, Role> players;
    private Player slenderMan;

    private BossBar bossBar;

    private int collectedPages;

    private Scoreboard scoreboard;
    private Objective objective;

    private File file;

    public Arena(String id) {
        this.id = id;
        this.arenaState = ArenaState.WAITING;
        this.players = new ConcurrentHashMap<>();
        this.survivorsLocations = new ArrayList<>();
        this.pagesLocations = new ArrayList<>();
        this.bossBar = Bukkit.createBossBar(SlenderMain.getInstance().getMessagesManager().getMessage("arena-boss-bar-waiting"), BarColor.RED, BarStyle.SOLID, BarFlag.DARKEN_SKY);
        this.slenderMan = null;

        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.scoreboard.registerNewObjective(id, "dummy", id);

        this.scoreboard.registerNewTeam("survivors");
        this.scoreboard.registerNewTeam("slenderman");

        this.scoreboard.getTeam("survivors").setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        this.scoreboard.getTeam("slenderman").setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    public void startGame() {
        runTaskTimer(SlenderMain.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        if(arenaState == ArenaState.WAITING) {
            if(players.isEmpty()) return;
            sendTitleToAllPlayers("", SlenderMain.getInstance().getMessagesManager().getMessage("arena-waiting-subtitle"), 0, 25, 25);
            return;
        }

        if(arenaState == ArenaState.STARTING) {
            sendTitleToAllPlayers("", SlenderMain.getInstance().getMessagesManager().getMessage("arena-starting-subtitle").replaceAll("%TIME%", String.valueOf(timer)), 0, 25, 25);
            if(timer == 0) {
                start();
                return;
            }
            timer--;
        }

        if(arenaState == ArenaState.RUNNING) {
            this.bossBar.setTitle(SlenderMain.getInstance().getMessagesManager().getMessage("arena-boss-bar-time-left").replaceAll("%TIME%", String.valueOf(timer)));
            sendActionBar(SlenderMain.getInstance().getMessagesManager().getMessage("arena-collected-pages").replaceAll("%CURRENT%", Integer.toString(collectedPages)));
            if(timer == 0) {
                endGame();
                return;
            }
            timer--;
        }

        if(arenaState == ArenaState.ENDING) {
            this.bossBar.setTitle(SlenderMain.getInstance().getMessagesManager().getMessage("arena-boss-bar-teleport-to-lobby").replaceAll("%TIME%", String.valueOf(timer)));
            if(timer == 0) {
                restart();
                return;
            }
            timer--;
        }

        if(arenaState == ArenaState.RESTARTING) {
            Bukkit.getWorlds().forEach(world -> world.getEntities().stream().filter(Item.class::isInstance).forEach(Entity::remove));
            this.bossBar.setTitle(SlenderMain.getInstance().getMessagesManager().getMessage("arena-boss-bar-waiting"));
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            this.scoreboard.registerNewObjective(id, "dummy", id);

            this.scoreboard.registerNewTeam("survivors");
            this.scoreboard.registerNewTeam("slenderman");
            this.slenderMan = null;
            Bukkit.getScheduler().runTaskLater(SlenderMain.getInstance(), () -> setArenaState(ArenaState.WAITING), 100L);
        }

    }

    public void start() {
        this.bossBar.setTitle(ColourUtil.colorize(HunterGameMain.getInstance().getMessagesManager().getMessage("arena-boss-bar-time-left").replaceAll("%TIME%", String.valueOf(timer))));
        sendTitleToAllPlayers(SlenderMain.getInstance().getMessagesManager().getMessage("arena-title"), SlenderMain.getInstance().getMessagesManager().getMessage("arena-started-subtitle"), 10, 30, 10);
        sendPlayersToGame();
        setArenaState(ArenaState.RUNNING);
        setTimer(gameTime);
        spawnPage();
        //SlenderGameStartEvent slenderGameStartEvent = new SlenderGameStartEvent(this);
        //Bukkit.getPluginManager().callEvent(slenderGameStartEvent);
    }

    private void sendPlayersToGame() {
        List<Player> tempList = new ArrayList<>(players.keySet());
        int size = tempList.size();
        int random = Util.getRandomNumber(size);
        this.slenderMan = tempList.get(random);
        tempList.remove(slenderMan);
        tempList.forEach(player -> players.put(player, Role.SURVIVOR));
        players.put(slenderMan, Role.SLENDER);

        tempList.forEach(player -> {
            player.teleport(survivorsLocations.get(Util.getRandomNumber(survivorsLocations.size())));
            player.getScoreboard().getTeam("survivors").addPlayer(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, Integer.MAX_VALUE));
            player.getInventory().clear();
            player.getInventory().setItem(0, CustomItem.SURVIVOR_WEAPON.toItemStack());
            player.getInventory().setItem(1, new ItemStack(Material.TORCH, 3));
        });

        slenderMan.teleport(slenderSpawnLocation);
        slenderMan.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
        slenderMan.setHealth(40);
        slenderMan.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, Integer.MAX_VALUE));
        if(SlenderMain.getInstance().isUseLibsDisguises()) {
            DisguiseAPI.disguiseToAll(slenderMan, new MobDisguise(DisguiseType.ENDERMAN));
            DisguiseAPI.setActionBarShown(slenderMan, false);
            DisguiseAPI.setViewDisguiseToggled(slenderMan, false);
        }

    }

    private void sendTitleToAllPlayers(String title, String subTitle, int fadeIn, int stayIn, int fadeOut) {
        players.keySet().forEach(player -> player.sendTitle(ColourUtil.colorize(title), ColourUtil.colorize(subTitle), fadeIn, stayIn, fadeOut));
    }

    private void sendActionBar(String message) {
        players.keySet().forEach(player -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColourUtil.colorize(message))));
    }

    public void restart() {
        Bukkit.getWorlds().forEach(world -> world.getEntities().stream().filter(Item.class::isInstance).forEach(Entity::remove));

        SlenderMain.getInstance().getPlayerManager().getPlayers().stream().filter(gamePlayer -> gamePlayer.isInArena() && gamePlayer.getArena().equals(this) && gamePlayer.isAutoJoinMode()).forEach(gamePlayer -> {
            SlenderMain.getInstance().getGameManager().leaveGame(gamePlayer.getPlayer(), this);
            Arena arena = SlenderMain.getInstance().getGameManager().getAvailableArena();
            if(arena == null) {
                gamePlayer.getPlayer().sendMessage(SlenderMain.getInstance().getMessagesManager().getMessage("no-available-arenas"));
                return;
            }
            SlenderMain.getInstance().getGameManager().joinGame(gamePlayer.getPlayer(), arena);
        });

        players.keySet().forEach(player -> {
            player.sendMessage(ColourUtil.colorize(SlenderMain.getInstance().getMessagesManager().getMessage("arena-game-stopped")));
            SlenderMain.getInstance().getGameManager().leaveGame(player, this);
        });

        setArenaState(ArenaState.RESTARTING);
        this.scoreboard = null;
        players.clear();
        setCollectedPages(0);
    }

    public void endGame() {
        this.bossBar.setTitle(SlenderMain.getInstance().getMessagesManager().getMessage("arena-boss-bar-teleport-to-lobby").replaceAll("%TIME%", String.valueOf(timer)));

        if(getCollectedPages() < 8) {
            sendTitleToAllPlayers(SlenderMain.getInstance().getMessagesManager().getMessage("arena-title"), SlenderMain.getInstance().getMessagesManager().getMessage("arena-slenderman-win-subtitle"), 10, 50, 10);

            GamePlayer gamePlayer = SlenderMain.getInstance().getPlayerManager().getPlayer(slenderMan);
            gamePlayer.setWins(gamePlayer.getWins()+1);

        } else {
            sendTitleToAllPlayers(SlenderMain.getInstance().getMessagesManager().getMessage("arena-title"), SlenderMain.getInstance().getMessagesManager().getMessage("arena-survivors-win-subtitle"), 10, 50, 10);
            getPlayers().entrySet().stream().filter(playerRoleEntry -> playerRoleEntry.getValue() == Role.SURVIVOR).forEach(playerRoleEntry -> {
                GamePlayer gamePlayer = SlenderMain.getInstance().getPlayerManager().getPlayer(playerRoleEntry.getKey());
                gamePlayer.setWins(gamePlayer.getWins()+1);
            });
        }

        getPlayers().keySet().forEach(player -> {
            getPlayers().put(player, Role.NONE);
            player.getInventory().clear();
            player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
            player.getInventory().setItem(7, CustomItem.PLAY_AGAIN.toItemStack());
            player.getInventory().setItem(8, CustomItem.LEAVE.toItemStack());
        });

        setArenaState(ArenaState.ENDING);
        setTimer(15);
        SlenderGameEndEvent slenderGameEndEvent = new SlenderGameEndEvent(this);
        Bukkit.getPluginManager().callEvent(slenderGameEndEvent);
    }

    public void sendMessage(String message) {
        players.keySet().forEach(player -> player.sendMessage(ColourUtil.colorize(message)));
    }

    public boolean isRunning() {
        return getArenaState() == ArenaState.RUNNING || getArenaState() == ArenaState.ENDING;
    }

}