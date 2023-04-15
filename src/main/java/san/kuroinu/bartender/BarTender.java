package san.kuroinu.bartender;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import san.kuroinu.bartender.commands.BartenderCommand;

public final class BarTender extends JavaPlugin {
    public static JavaPlugin plugin;
    private Listeners listeners;
    public static String prefix = ChatColor.DARK_PURPLE +"[BarTender]"+ChatColor.RESET;
    public static Economy econ = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        this.listeners = new Listeners();
        getServer().getPluginManager().registerEvents(this.listeners, this);
        super.onEnable();
        if (!setupEconomy() ) {
            getServer().getConsoleSender().sendMessage(prefix + " Vault not found, disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getCommand("bartender").setExecutor(new BartenderCommand());
        plugin.saveDefaultConfig();
    }

    private static Boolean setupEconomy() {
        if (getPlugin().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null){
            return false;
        }else{
            econ = rsp.getProvider();
        }
        return econ != null;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private static JavaPlugin getPlugin() {
        return plugin;
    }
}
