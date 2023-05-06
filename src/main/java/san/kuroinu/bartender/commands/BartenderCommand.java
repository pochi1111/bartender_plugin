package san.kuroinu.bartender.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static san.kuroinu.bartender.BarTender.plugin;
import static san.kuroinu.bartender.BarTender.prefix;

public class BartenderCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args[0].equals("reload")){
            if (!sender.isOp()){
                sender.sendMessage(prefix+ ChatColor.RED+"権限がありません");
                return false;
            }
            plugin.reloadConfig();
            sender.sendMessage(prefix+ ChatColor.AQUA+"Configを更新しました");
        }

        if (args[0].equals("help")){
            if (!sender.isOp()){
                sender.sendMessage(prefix+ ChatColor.RED+"権限がありません");
                return false;
            }
            sender.sendMessage(prefix+ ChatColor.AQUA+"BarTenderのヘルプです");
            sender.sendMessage(prefix+ ChatColor.AQUA+"/bartender reload: Configを更新します");
            sender.sendMessage(prefix+ ChatColor.AQUA+"/bartender turn on/off: BarTenderを有効/無効にします");
        }

        if (args[0].equals("turn")){
            if (!sender.isOp()){
                sender.sendMessage(prefix+ ChatColor.RED+"権限がありません");
                return false;
            }
            if (args[1] == "on" || args[1] == "off"){
                if (args[1] == "on"){
                    plugin.getConfig().set("turn", 1);
                    plugin.saveConfig();
                    plugin.reloadConfig();
                    sender.sendMessage(prefix+ ChatColor.AQUA+"BarTenderを有効にしました");
                }else{
                    plugin.getConfig().set("turn", 0);
                    plugin.saveConfig();
                    plugin.reloadConfig();
                    sender.sendMessage(prefix+ ChatColor.AQUA+"BarTenderを無効にしました");
                }
            }else{
                sender.sendMessage(prefix+ ChatColor.RED+"引数が不正です");
                sender.sendMessage(prefix+ ChatColor.RED+"/bartender turn on/off");
            }
        }

        return false;
    }
}
