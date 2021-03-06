package me.kilianlegters;

import me.kilianlegters.TPRequest;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class TP implements CommandExecutor, Listener{

    private RealLightWeightTPRequests realLightWeightTP = RealLightWeightTPRequests.realLightWeightTPRequests;

    private HashMap<Player, TPRequest> requestMap = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player){
            Player player = (Player) commandSender;

            if (strings.length == 1){

                Player player1 = getPlayer(strings[0]);

                if (player1 == null){
                    player.sendMessage(ChatColor.RED + "Couldn't find anyone online with this name.");
                    return true;
                }

                if (player1.equals(player)){
                    player.sendMessage(ChatColor.RED + "You cant TPRequest with yourself.");
                    return true;
                }

                if (s.equalsIgnoreCase("TP") || s.equalsIgnoreCase("TPR")){
                    return teleportRequest(player, player1);
                }

                if (s.equalsIgnoreCase("TPA")){
                    return teleportAccept(player, player1);
                }

                if (s.equalsIgnoreCase("TPD")){
                    return teleportDeny(player, player1);
                }
            }

            if (strings.length == 0) {
                if (s.equalsIgnoreCase("TPA")) {
                    return teleportAccept(player);
                }

                if (s.equalsIgnoreCase("TPD")) {
                    return teleportDeny(player);
                }
            }

            player.sendMessage(ChatColor.RED + "Unknown syntax.");
            return false;

        } else {
            commandSender.sendMessage(ChatColor.RED + "Only for in-game use.");
            return true;
        }
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event){
        requestMap.values().remove(event.getPlayer());
    }

    public void onDisable(){
        requestMap.clear();
    }

    private boolean teleportRequest(Player player, Player player1){
        if (requestMap.containsKey(player)){
            if (!requestMap.get(player).expired()){
                player.sendMessage(ChatColor.RED + "You already sent request in the past 15 seconds.");
                return true;
            }
        }

        requestMap.put(player, new TPRequest(player, player1));
        return true;
    }

    private Player getPlayer(String s){
        for (Player player: Bukkit.getOnlinePlayers()){
            if (s.equals(player.getName()) || s.equals(player.getDisplayName())){
                return player;
            }
        }
        return null;
    }

    private boolean teleportAccept(Player player, Player player1){
        if (!requestMap.get(player1).accept(player)){
            player.sendMessage(ChatColor.GREEN + WordUtils.capitalize(player1.getDisplayName()) + " has not requested to teleport to you.");
        }
        return true;
    }

    private boolean teleportDeny(Player player, Player player1){
        if (!requestMap.get(player1).deny(player)){
            player.sendMessage(ChatColor.GREEN + WordUtils.capitalize(player1.getDisplayName()) + " has not requested to teleport to you.");
        }
        return true;
    }

    private boolean teleportAccept(Player player){
        if (!requestMap.entrySet().removeIf(entry -> entry.getValue().accept(player))){
            player.sendMessage(ChatColor.RED + "You have no request to accept.");
        }
        return true;
    }

    private boolean teleportDeny(Player player){
        if (!requestMap.entrySet().removeIf(entry -> entry.getValue().deny(player))){
            player.sendMessage(ChatColor.RED + "You have no request to deny.");
        }
        return true;
    }

}
