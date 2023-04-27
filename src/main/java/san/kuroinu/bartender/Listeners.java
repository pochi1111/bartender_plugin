package san.kuroinu.bartender;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import javax.security.auth.login.Configuration;
import java.util.*;

import static san.kuroinu.bartender.BarTender.*;

public class Listeners implements Listener {
    @EventHandler
    public void Bartender_clock(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getCustomName() == null) {
            return;
        }
        if (event.getRightClicked().getCustomName().equals("Bartender") && event.getRightClicked().getType() == EntityType.VILLAGER) {
            event.setCancelled(true);
            Player e = event.getPlayer();
            if (plugin.getConfig().getInt("turn") == 0 && !e.isOp()){
                e.sendMessage(prefix+ChatColor.RED+"現在は営業時間外です");
                return;
            }
            ConfigurationSection sec = plugin.getConfig().getConfigurationSection("sakes");
            Set<String> sake_set = sec.getKeys(false);
            int sake_num = sake_set.size();
            int gyusuu = sake_num / 9+1;
            Inventory inv = Bukkit.createInventory(null, gyusuu*9, prefix);
            String name;
            List<String> lore;
            List<String> sakes = new ArrayList<>();
            for (String s : sake_set) {
                sakes.add(s);
            }
            ItemStack item = null;
            for (int i = 0; i < sake_num; i++) {
                lore = plugin.getConfig().getStringList("sakes." + sakes.get(i) + ".description");
                lore.add("§e" + plugin.getConfig().getString("sakes." + sakes.get(i) + ".price") + "円");
                lore.add("§b"+plugin.getConfig().getString("sakes." + sakes.get(i) + ".get_price")+"円獲得できます");
                lore.add("期待度 "+plugin.getConfig().getString("sakes." + sakes.get(i) + ".expectation"));
                name = sakes.get(i);
                item = createGuiItem(Material.POTION, name , lore.toArray(new String[0]));
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                int Color_R = plugin.getConfig().getInt("sakes." + sakes.get(i) + ".Color_R");
                int Color_G = plugin.getConfig().getInt("sakes." + sakes.get(i) + ".Color_G");
                int Color_B = plugin.getConfig().getInt("sakes." + sakes.get(i) + ".Color_B");
                meta.setColor(Color.fromRGB(Color_R, Color_G, Color_B));
                item.setItemMeta(meta);
                inv.addItem(item);
            }
            e.openInventory(inv);
        }
    }
    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(prefix)) {
            Player e = (Player) event.getWhoClicked();
            OfflinePlayer p = (OfflinePlayer) event.getWhoClicked();
            event.setCancelled(true);
            String name = event.getCurrentItem().getItemMeta().getDisplayName();
            ConfigurationSection sec = plugin.getConfig().getConfigurationSection("sakes");
            Set<String> sake_set = sec.getKeys(false);
            List<String> sakes = new ArrayList<>();
            for (String s : sake_set) {
                sakes.add(s);
            }

            if (sakes.contains(name)) {
                int price = plugin.getConfig().getInt("sakes." + name + ".price");
                int get_price = plugin.getConfig().getInt("sakes." + name + ".get_price");
                if (!event.isShiftClick()){
                    e.closeInventory();
                }
                int money = (int) econ.getBalance(p);
                if (money >= price) {
                    if (e.getInventory().firstEmpty() == -1){
                        e.sendMessage("§cインベントリがいっぱいです");
                        return;
                    }
                    EconomyResponse with = econ.withdrawPlayer(p, price);
                    if (with.transactionSuccess()){
                        e.sendMessage(name + "§aを購入しました");
                        ItemStack item = event.getCurrentItem();
                        e.getInventory().addItem(item);
                        return;
                    }else{
                        e.sendMessage(name + "§cを購入できませんでした");
                    }
                } else {
                    e.sendMessage("§cお金が足りません");
                }
            }else{
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void sake_drink(PlayerItemConsumeEvent event){
        ItemStack item = event.getItem();
        if (item.getItemMeta().getDisplayName() == null){
            return;
        }
        String name = item.getItemMeta().getDisplayName();
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("sakes");
        Set<String> sake_set = sec.getKeys(false);
        List<String> sakes = new ArrayList<>();
        for (String s : sake_set) {
            sakes.add(s);
        }
        if (sakes.contains(name)){
            int get_price = plugin.getConfig().getInt("sakes." + name + ".get_price");
            int kitai = plugin.getConfig().getInt("sakes." + name + ".probability");
            Random rand = new Random();int random = rand.nextInt(kitai)+1;
            int nannbunnnonannka = plugin.getConfig().getInt("sakes." + name + ".nannbunnnonannka");
            if (plugin.getConfig().getInt("debug_log") == 1 && event.getPlayer().isOp()){
                event.getPlayer().sendMessage("§a"+name+"§eの期待度は"+kitai+"です");
                event.getPlayer().sendMessage("§a"+name+"§eの確率は"+nannbunnnonannka+"です");
                event.getPlayer().sendMessage("§a"+name+"§eの乱数は"+random+"です");
            }
            if (nannbunnnonannka >= random){
                OfflinePlayer e = (OfflinePlayer) event.getPlayer();
                EconomyResponse with = econ.depositPlayer(e, get_price);
                if (with.transactionSuccess()){
                    event.getPlayer().sendMessage(name + ChatColor.GREEN +"を飲んで" +ChatColor.GOLD+ get_price+ "円"+ChatColor.GREEN+"獲得しました");
                    List<String> commands = plugin.getConfig().getStringList("sakes." + name + ".commands");
                    for (String command : commands){
                        //commandの中の[name]をプレイヤー名に置き換える
                        String player_name = event.getPlayer().getName();
                        command = command.replace("[name]", player_name);
                        //commandを実行
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                    if (plugin.getConfig().get("sakes." + name + ".mugen") != null){
                        if (plugin.getConfig().getBoolean("sakes." + name + ".mugen")){
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }else{
                event.getPlayer().sendMessage(name + "§cを飲んで何も起こりませんでした");
            }
            //メインハンドを空にする
            event.setCancelled(true);
            event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        }
    }protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

}
