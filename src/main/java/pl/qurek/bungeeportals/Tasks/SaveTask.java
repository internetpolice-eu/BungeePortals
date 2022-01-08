package pl.qurek.bungeeportals.Tasks;

import org.bukkit.scheduler.BukkitRunnable;
import pl.qurek.bungeeportals.BungeePortals;

public class SaveTask extends BukkitRunnable {
   private BungeePortals plugin;

   public SaveTask(BungeePortals plugin) {
      this.plugin = plugin;
   }

   public void run() {
      if (this.plugin.configFile.getBoolean("SaveTask.Enabled")) {
         this.plugin.savePortalsData();
      }

   }
}
