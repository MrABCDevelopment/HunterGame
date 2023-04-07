package me.dreamdevs.github.huntergame.api.scoreboard;

import lombok.Getter;
import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.game.Game;
import me.dreamdevs.github.huntergame.game.GameState;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

@Getter
public class HunterBoard {

    private Scoreboard scoreboard;
    private Objective objective;
    private final FileConfiguration config;
    private final List<String> waitingList;
    private final List<String> runningList;
    private Game game;

    public HunterBoard(Game game) {
        config = HunterGameMain.getInstance().getConfig();
        waitingList = ColourUtil.colouredLore(config.getStringList("GlobalGameSettings.WaitingScoreboard"));
        runningList = ColourUtil.colouredLore(config.getStringList("GlobalGameSettings.GameScoreboard"));
        this.game = game;

        createBoard();
    }

    public void update(GameState gameState) {
        if(gameState == GameState.WAITING || gameState == GameState.STARTING) {
            for (int x = waitingList.size()-1; x >= 0; x--) {
                Team team = scoreboard.getTeam("line-" + x);
                team.setPrefix(waitingList.get(x)
                        .replace("{TIME}", String.valueOf(game.getTimer()))
                        .replace("{GOAL}", String.valueOf(game.getGoal()))
                        .replace("{CURRENT}", String.valueOf(game.getPlayers().size()))
                        .replace("{MAX}", String.valueOf(game.getMaxPlayers())));
                objective.getScore(ChatColor.values()[x].toString()).setScore(x);
            }
        } else if(gameState == GameState.RUNNING) {
            for (int x = runningList.size()-1; x >= 0; x--) {
                Team team = scoreboard.getTeam("line-" + x);
                if(team == null) {
                    team = scoreboard.registerNewTeam("line-"+x);
                }

                team.setPrefix(runningList.get(x)
                        .replace("{TIME}", String.valueOf(game.getTimer()))
                        .replace("{GOAL}", String.valueOf(game.getGoal()))
                        .replace("{CURRENT}", String.valueOf(game.getPlayers().size()))
                        .replace("{MAX}", String.valueOf(game.getMaxPlayers())));
                objective.getScore(ChatColor.values()[x].toString()).setScore(x);
            }
        }
    }

    public void createBoard() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("HB-"+game.getId(), "dummy", ColourUtil.colorize(config.getString("GlobalGameSettings.Title")));
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for(int x = waitingList.size()-1; x>=0; x--) {
            Team team = scoreboard.registerNewTeam("line-"+x);
            team.addEntry(ChatColor.values()[x].toString());
            team.setPrefix(waitingList.get(x)
                    .replace("{TIME}", String.valueOf(game.getTimer()))
                    .replace("{GOAL}", String.valueOf(game.getGoal()))
                    .replace("{CURRENT}", String.valueOf(game.getPlayers().size()))
                    .replace("{MAX}", String.valueOf(game.getMaxPlayers())));
            objective.getScore(ChatColor.values()[x].toString()).setScore(x);
        }
    }

    public void restore() {
        this.scoreboard = null;
        this.objective = null;
        createBoard();
    }

}