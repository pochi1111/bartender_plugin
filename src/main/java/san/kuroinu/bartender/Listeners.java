package san.kuroinu.bartender;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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

import java.util.Arrays;
import java.util.List;

import static san.kuroinu.bartender.BarTender.*;

public class Listeners implements Listener {
    @EventHandler
    public void Bartender_clock(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getCustomName() == null) {
            return;
        }
        if (event.getRightClicked().getCustomName().equals("Bartender") && event.getRightClicked().getType() == EntityType.VILLAGER) {
            event.setCancelled(true);
            int sake_num = plugin.getConfig().getInt("sake_num");
            int gyusuu = sake_num / 9+1;
            Inventory inv = Bukkit.createInventory(null, gyusuu*9, prefix);
            String name;
            List<String> lore;
            List<String> sakes = plugin.getConfig().getStringList("names");
            for (int i = 0; i < sake_num; i++) {
                lore = plugin.getConfig().getStringList("sakes." + sakes.get(i) + ".description");
                lore.add("§e" + plugin.getConfig().getString("sakes." + sakes.get(i) + ".price") + "円");
                lore.add("§b"+plugin.getConfig().getString("sakes." + sakes.get(i) + ".get_price")+"円獲得できます");
                lore.add("期待度 "+plugin.getConfig().getString("sakes." + sakes.get(i) + ".expectation"));
                name = sakes.get(i);
                inv.addItem(createGuiItem(Material.POTION, name , lore.toArray(new String[0])));
            }
            event.getPlayer().openInventory(inv);
        }
    }
    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(prefix)) {
            Player e = (Player) event.getWhoClicked();
            OfflinePlayer p = (OfflinePlayer) event.getWhoClicked();
            event.setCancelled(true);
            String name = event.getCurrentItem().getItemMeta().getDisplayName();
            List<String> sakes = plugin.getConfig().getStringList("names");

            if (sakes.contains(name)) {
                int price = plugin.getConfig().getInt("sakes." + name + ".price");
                int get_price = plugin.getConfig().getInt("sakes." + name + ".get_price");
                e.closeInventory();
                int money = (int) econ.getBalance(p);
                if (money >= price) {
                    if (e.getInventory().firstEmpty() == -1){
                        e.sendMessage("§cインベントリがいっぱいです");
                        return;
                    }
                    EconomyResponse with = econ.withdrawPlayer(p, price);
                    if (with.transactionSuccess()){
                        e.sendMessage("§a" + name + "を購入しました");
                        ItemStack item = event.getCurrentItem();
                        e.getInventory().addItem(item);
                        return;
                    }else{
                        e.sendMessage("§c" + name + "を購入できませんでした");
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
        int get_price = plugin.getConfig().getInt("sakes." + name + ".get_price");
        List<String> sakes = plugin.getConfig().getStringList("names");
        if (sakes.contains(name)){
            int kitai = plugin.getConfig().getInt("sakes." + name + ".probability");
            int random = (int)(Math.random() * kitai);
            int nannbunnnonannka = plugin.getConfig().getInt("sakes." + name + ".nannbunnnonannka");
            if (random >= 1 && random >= nannbunnnonannka){
                OfflinePlayer e = (OfflinePlayer) event.getPlayer();
                EconomyResponse with = econ.depositPlayer(e, get_price);
                if (with.transactionSuccess()){
                    event.getPlayer().sendMessage("§a" + name + "を飲んで" +ChatColor.GOLD+ get_price+ "円"+ChatColor.GREEN+"獲得しました");
                    return;
                }
            }else{
                event.getPlayer().sendMessage("§c" + name + "を飲んで何も起こりませんでした");
            }
        }
    }
    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
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
