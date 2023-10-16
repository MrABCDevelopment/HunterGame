package me.dreamdevs.github.huntergame.api.scoreboard;

import lombok.Getter;
import me.dreamdevs.github.huntergame.HunterGameMain;
import me.dreamdevs.github.huntergame.game.Game;
import me.dreamdevs.github.huntergame.game.GameState;
import me.dreamdevs.github.huntergame.utils.ColourUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

@Getter
public class ScoreboardAPI {

    private static final FileConfiguration config;
    private static List<String> elements;

    public static void createScoreboard(Game game) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("hg-"+game.getId(), "dummy", ColourUtil.colorize(config.getString("Scoreboard.Title")));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        elements = ColourUtil.colouredLore(config.getStringList("Scoreboard.Waiting"));

        game.setScoreboard(scoreboard);
        game.setObjective(objective);

        int score = 15;
        for(int x = elements.size()-1; x>=0; x--) {
            Team team = scoreboard.registerNewTeam("line-"+x);
            team.addEntry(ChatColor.values()[x].toString());
            team.setPrefix(elements.get(x)
                    .replace("{CURRENT}", String.valueOf(game.getPlayers().size()))
                    .replace("{MAX}", String.valueOf(game.getMaxPlayers()))
                    .replace("{GOAL}", String.valueOf(game.getGoal())));
            objective.getScore(ChatColor.values()[x].toString()).setScore(score);
            score--;
        }
    }

    static {
        config = HunterGameMain.getInstance().getConfigManager().getConfig("scoreboards.yml");
    }

    public static void updateElements(Game game) {
        if(game.getGameState() == GameState.WAITING || game.getGameState() == GameState.STARTING) {
            elements = ColourUtil.colouredLore(config.getStringList("Scoreboard.Waiting"));
        } else {
            elements = ColourUtil.colouredLore(config.getStringList("Scoreboard.Game"));
        }
    }

    public static void update(Game game) {
        int score = 0;

        for (int position = elements.size() - 1; position >= 0; position--) {
            Team team = game.getScoreboard().getTeam("line-" + position);
            if(team == null) {
                team = game.getScoreboard().registerNewTeam("line-"+position);
            }

            team.setPrefix(elements.get(position)
                    .replace("{TIME}", String.valueOf(game.getTimer()))
                    .replace("{GOAL}", String.valueOf(game.getGoal()))
                    .replace("{CURRENT}", String.valueOf(game.getPlayers().size()))
                    .replace("{MAX}", String.valueOf(game.getMaxPlayers())));
            game.getObjective().getScore(ChatColor.values()[position].toString()).setScore(score);
            score++;
        }
    }

    //public void restore() {
    //    scoreboard = null;
    //    objective = null;
    //    createBoard();
    //}

}