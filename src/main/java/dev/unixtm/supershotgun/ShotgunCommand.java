package dev.unixtm.supershotgun;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShotgunCommand implements CommandExecutor {
    SpigotPlugin plugin;

    public ShotgunCommand(SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String cmdName = cmd.getName().toLowerCase();
        int arrowCount = 50;
        int arrowForce = 6;
        int arrowSpread = 12;
        if(args.length == 1){
            try{
                arrowCount = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e) {
                arrowCount = 50;
            }
        }
        if(args.length == 2){
            try{
                arrowCount = Integer.parseInt(args[0]);
                arrowForce = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                arrowCount = 50;
                arrowForce = 6;
            }
        }
        if(args.length == 3){
            try{
                arrowCount = Integer.parseInt(args[0]);
                arrowForce = Integer.parseInt(args[1]);
                arrowSpread = Integer.parseInt(args[2]);
            }
            catch (NumberFormatException e) {
                arrowCount = 50;
                arrowForce = 6;
                arrowSpread = 12;
            }
        }
        if (!cmdName.equals("shotgun")) {
            return false;
        }

        sender.sendMessage("Scout TF2 reference here.");
        if((sender.isOp() || sender.getName() == "UnixTMDev") && sender instanceof Player){
            Player gamer = (Player)sender;
            gamer.getInventory().addItem(plugin.createShotgunItem(arrowCount,arrowForce,arrowSpread));
        }
        return true;
    }
}
