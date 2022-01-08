package pl.qurek.bungeeportals;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.qurek.bungeeportals.Commands.CommandBPortals;
import pl.qurek.bungeeportals.Listeners.EventListener;
import pl.qurek.bungeeportals.Tasks.SaveTask;

public class BungeePortals extends JavaPlugin {
   private Logger logger = Bukkit.getLogger();
   public Map<String, String> portalData = new HashMap();
   public WorldEditPlugin worldEdit;
   public YamlConfiguration configFile;
   public YamlConfiguration portalsFile;

   public void onEnable() {
      long time = System.currentTimeMillis();
      if (this.getServer().getPluginManager().getPlugin("WorldEdit") == null) {
         this.getPluginLoader().disablePlugin(this);
         throw new NullPointerException("[BungeePortals] WorldEdit not found, disabling...");
      } else {
         this.worldEdit = (WorldEditPlugin)this.getServer().getPluginManager().getPlugin("WorldEdit");
         this.getCommand("BPortals").setExecutor(new CommandBPortals(this));
         this.logger.log(Level.INFO, "[BungeePortals] Commands registered!");
         this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
         this.logger.log(Level.INFO, "[BungeePortals] Events registered!");
         this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
         this.logger.log(Level.INFO, "[BungeePortals] Plugin channel registered!");
         this.loadConfigFiles();
         this.loadPortalsData();
         int interval = this.configFile.getInt("SaveTask.Interval") * 20;
         (new SaveTask(this)).runTaskTimer(this, (long)interval, (long)interval);
         this.logger.log(Level.INFO, "[BungeePortals] Save task started!");
         this.logger.log(Level.INFO, "[BungeePortals] Version " + this.getDescription().getVersion() + " has been enabled. (" + (System.currentTimeMillis() - time) + "ms)");
      }
   }

   public void onDisable() {
      long time = System.currentTimeMillis();
      this.savePortalsData();
      this.logger.log(Level.INFO, "[BungeePortals] Version " + this.getDescription().getVersion() + " has been disabled. (" + (System.currentTimeMillis() - time) + "ms)");
   }

   private void createConfigFile(InputStream in, File file) {
      try {
         OutputStream out = new FileOutputStream(file);
         byte[] buf = new byte[1024];

         int len;
         while((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
         }

         out.close();
         in.close();
      } catch (IOException var6) {
         var6.printStackTrace();
      }

   }

   public void loadConfigFiles() {
      File cFile = new File(this.getDataFolder(), "config.yml");
      if (!cFile.exists()) {
         cFile.getParentFile().mkdirs();
         this.createConfigFile(this.getResource("config.yml"), cFile);
         this.logger.log(Level.INFO, "[BungeePortals] Configuration file config.yml created!");
      }

      this.configFile = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config.yml"));
      this.logger.log(Level.INFO, "[BungeePortals] Configuration file config.yml loaded!");
      File pFile = new File(this.getDataFolder(), "portals.yml");
      if (!pFile.exists()) {
         pFile.getParentFile().mkdirs();
         this.createConfigFile(this.getResource("portals.yml"), pFile);
         this.logger.log(Level.INFO, "[BungeePortals] Configuration file portals.yml created!");
      }

      this.portalsFile = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "portals.yml"));
      this.logger.log(Level.INFO, "[BungeePortals] Configuration file portals.yml loaded!");
   }

   public void loadPortalsData() {
      try {
         long time = System.currentTimeMillis();
         Iterator var4 = this.portalsFile.getKeys(false).iterator();

         while(var4.hasNext()) {
            String key = (String)var4.next();
            String value = this.portalsFile.getString(key);
            this.portalData.put(key, value);
         }

         this.logger.log(Level.INFO, "[BungeePortals] Portal data loaded! (" + (System.currentTimeMillis() - time) + "ms)");
      } catch (NullPointerException var6) {
      }

   }

   public void savePortalsData() {
      long time = System.currentTimeMillis();
      Iterator var4 = this.portalData.entrySet().iterator();

      while(var4.hasNext()) {
         Entry<String, String> entry = (Entry)var4.next();
         this.portalsFile.set((String)entry.getKey(), entry.getValue());
      }

      try {
         this.portalsFile.save(new File(this.getDataFolder(), "portals.yml"));
      } catch (IOException var5) {
         var5.printStackTrace();
      }

      this.logger.log(Level.INFO, "[BungeePortals] Portal data saved! (" + (System.currentTimeMillis() - time) + "ms)");
   }
}
