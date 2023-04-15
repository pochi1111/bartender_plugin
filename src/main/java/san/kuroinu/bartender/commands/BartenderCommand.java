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
        return false;
    }
}
