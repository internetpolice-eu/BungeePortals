package pl.qurek.bungeeportals.Listeners;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.qurek.bungeeportals.BungeePortals;

public class EventListener implements Listener {
   private BungeePortals plugin;
   private Map<String, Boolean> statusData = new HashMap();

   public EventListener(BungeePortals plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onPlayerMove(PlayerMoveEvent event) throws IOException {
      Player player = event.getPlayer();
      String playerName = player.getName();
      if (!this.statusData.containsKey(playerName)) {
         this.statusData.put(playerName, false);
      }

      Block block = player.getWorld().getBlockAt(player.getLocation());
      String data = block.getWorld().getName() + "#" + block.getX() + "#" + block.getY() + "#" + block.getZ();
      if (this.plugin.portalData.containsKey(data)) {
         if (!(Boolean)this.statusData.get(playerName)) {
            this.statusData.put(playerName, true);
            String destination = (String)this.plugin.portalData.get(data);
            if (!player.hasPermission("BungeePortals.portal." + destination) && !player.hasPermission("BungeePortals.portal.*")) {
               player.sendMessage(this.plugin.configFile.getString("NoPortalPermissionMessage").replace("{destination}", destination).replaceAll("(&([a-f0-9l-or]))", "§$2"));
            } else {
               ByteArrayOutputStream baos = new ByteArrayOutputStream();
               DataOutputStream dos = new DataOutputStream(baos);
               dos.writeUTF("Connect");
               dos.writeUTF(destination);
               player.sendPluginMessage(this.plugin, "BungeeCord", baos.toByteArray());
               baos.close();
               dos.close();
            }
         }
      } else if ((Boolean)this.statusData.get(playerName)) {
         this.statusData.put(playerName, false);
      }

   }
}
