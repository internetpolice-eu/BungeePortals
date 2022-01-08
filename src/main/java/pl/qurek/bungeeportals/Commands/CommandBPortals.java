package pl.qurek.bungeeportals.Commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.qurek.bungeeportals.BungeePortals;

public class CommandBPortals implements CommandExecutor {
   private BungeePortals plugin;
   private Map<String, List<String>> selections = new HashMap();

   public CommandBPortals(BungeePortals plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
      if (commandLabel.equalsIgnoreCase("BPortals")) {
         if (sender.hasPermission("BungeePortals.command.BPortals")) {
            if (args.length == 0) {
               sender.sendMessage(ChatColor.BLUE + "BungeePortals v" + this.plugin.getDescription().getVersion() + " by YoFuzzy3");
               sender.sendMessage(ChatColor.GREEN + "/BPortals reload " + ChatColor.RED + "Reload all files and data.");
               sender.sendMessage(ChatColor.GREEN + "/BPortals forcesave " + ChatColor.RED + "Force-save portals.");
               sender.sendMessage(ChatColor.GREEN + "/BPortals select <filter,list> " + ChatColor.RED + "Get selection.");
               sender.sendMessage(ChatColor.GREEN + "/BPortals clear " + ChatColor.RED + "Clear selection.");
               sender.sendMessage(ChatColor.GREEN + "/BPortals create <destination> " + ChatColor.RED + "Create portals.");
               sender.sendMessage(ChatColor.GREEN + "/BPortals remove <destination> " + ChatColor.RED + "Remove portals.");
               sender.sendMessage(ChatColor.BLUE + "Visit www.spigotmc.org/resources/bungeeportals.19 for help.");
            } else {
               String playerName;
               if (args.length == 1) {
                  if (args[0].equalsIgnoreCase("reload")) {
                     this.plugin.loadConfigFiles();
                     this.plugin.loadPortalsData();
                     sender.sendMessage(ChatColor.GREEN + "All configuration files and data have been reloaded.");
                  } else if (args[0].equalsIgnoreCase("forcesave")) {
                     this.plugin.savePortalsData();
                     sender.sendMessage(ChatColor.GREEN + "Portal data saved!");
                  } else if (args[0].equalsIgnoreCase("clear")) {
                     if (sender instanceof Player) {
                        playerName = sender.getName();
                        if (this.selections.containsKey(playerName)) {
                           this.selections.remove(playerName);
                           sender.sendMessage(ChatColor.GREEN + "Selection cleared.");
                        } else {
                           sender.sendMessage(ChatColor.RED + "You haven't selected anything.");
                        }
                     } else {
                        sender.sendMessage(ChatColor.RED + "Only players can use that command.");
                     }
                  } else {
                     sender.sendMessage(ChatColor.RED + "Type /BPortals for help!");
                  }
               } else if (args.length == 2) {
                  if (args[0].equalsIgnoreCase("select")) {
                     if (sender instanceof Player) {
                        Player player = (Player)sender;
                        playerName = player.getName();
                        BukkitPlayer bPlayer = BukkitAdapter.adapt(player);
                        LocalSession session = WorldEdit.getInstance().getSessionManager().get(bPlayer);
                        Region selection = null;

                        try {
                           selection = session.getSelection(bPlayer.getWorld());
                        } catch (IncompleteRegionException var22) {
                           player.sendMessage(ChatColor.RED + "You have to first create a WorldEdit selection!");
                        }

                        if (selection != null) {
                           if (selection instanceof CuboidRegion) {
                              List<Location> locations = this.getLocationsFromCuboid((CuboidRegion)selection);
                              List<String> blocks = new ArrayList();
                              String[] ids = new String[0];
                              int count = 0;
                              int filtered = 0;
                              boolean filter = false;
                              if (!args[1].equals("0")) {
                                 ids = args[1].split(",");
                                 filter = true;
                              }

                              Iterator var17 = locations.iterator();

                              while(true) {
                                 while(var17.hasNext()) {
                                    Location location = (Location)var17.next();
                                    Block block = player.getWorld().getBlockAt(location);
                                    if (filter) {
                                       boolean found = false;

                                       for(int i = 0; i < ids.length; ++i) {
                                          String id = ids[i];
                                          if (id.equalsIgnoreCase(block.getType().toString())) {
                                             found = true;
                                             break;
                                          }
                                       }

                                       if (found) {
                                          blocks.add(block.getWorld().getName() + "#" + block.getX() + "#" + block.getY() + "#" + block.getZ());
                                          ++count;
                                       } else {
                                          ++filtered;
                                       }
                                    } else {
                                       blocks.add(block.getWorld().getName() + "#" + block.getX() + "#" + block.getY() + "#" + block.getZ());
                                       ++count;
                                    }
                                 }

                                 this.selections.put(playerName, blocks);
                                 sender.sendMessage(ChatColor.GREEN + String.valueOf(count) + " blocks have been selected, " + filtered + " filtered.");
                                 sender.sendMessage(ChatColor.GREEN + "Use the selection in the create and remove commands.");
                                 break;
                              }
                           } else {
                              sender.sendMessage(ChatColor.RED + "Must be a cuboid selection!");
                           }
                        } else {
                           sender.sendMessage(ChatColor.RED + "You have to first create a WorldEdit selection!");
                        }
                     } else {
                        sender.sendMessage(ChatColor.RED + "Only players can use that command.");
                     }
                  } else {
                     String block;
                     Iterator var27;
                     if (args[0].equalsIgnoreCase("create")) {
                        if (sender instanceof Player) {
                           playerName = sender.getName();
                           if (this.selections.containsKey(playerName)) {
                              List<String> selection = (List)this.selections.get(playerName);
                              var27 = selection.iterator();

                              while(var27.hasNext()) {
                                 block = (String)var27.next();
                                 this.plugin.portalData.put(block, args[1]);
                              }

                              sender.sendMessage(ChatColor.GREEN + String.valueOf(selection.size()) + " portals have been created.");
                           } else {
                              sender.sendMessage(ChatColor.RED + "You haven't selected anything.");
                           }
                        } else {
                           sender.sendMessage(ChatColor.RED + "Only players can use that command.");
                        }
                     } else if (args[0].equalsIgnoreCase("remove")) {
                        if (sender instanceof Player) {
                           playerName = sender.getName();
                           if (this.selections.containsKey(playerName)) {
                              int count = 0;
                              var27 = ((List)this.selections.get(playerName)).iterator();

                              while(var27.hasNext()) {
                                 block = (String)var27.next();
                                 if (this.plugin.portalData.containsKey(block)) {
                                    this.plugin.portalData.remove(block);
                                    ++count;
                                 }
                              }

                              sender.sendMessage(ChatColor.GREEN + String.valueOf(count) + " portals have been removed.");
                           } else {
                              sender.sendMessage(ChatColor.RED + "You haven't selected anything.");
                           }
                        } else {
                           sender.sendMessage(ChatColor.RED + "Only players can use that command.");
                        }
                     } else {
                        sender.sendMessage(ChatColor.RED + "Type /BPortals for help!");
                     }
                  }
               } else {
                  sender.sendMessage(ChatColor.RED + "Type /BPortals for help!");
               }
            }
         } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
         }
      }

      return true;
   }

   private List<Location> getLocationsFromCuboid(CuboidRegion cuboid) {
      List<Location> locations = new ArrayList();
      BlockVector3 minLocation = cuboid.getMinimumPoint();
      BlockVector3 maxLocation = cuboid.getMaximumPoint();

      for(int i1 = minLocation.getBlockX(); i1 <= maxLocation.getBlockX(); ++i1) {
         for(int i2 = minLocation.getBlockY(); i2 <= maxLocation.getBlockY(); ++i2) {
            for(int i3 = minLocation.getBlockZ(); i3 <= maxLocation.getBlockZ(); ++i3) {
               String worldName = cuboid.getWorld().getName();
               locations.add(new Location(this.plugin.getServer().getWorld(worldName), (double)i1, (double)i2, (double)i3));
            }
         }
      }

      return locations;
   }
}
