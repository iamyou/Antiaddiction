package cc.silenter.antiaddictionbyqq;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class AntiaddictionByQQ extends JavaPlugin {
    public static Plugin instance;
    public static Logger log;
    public static JsonObject recv;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance=this;
        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
        log = this.getLogger();
        if(!getDataFolder().exists())
        {
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdirs();
            saveDefaultConfig();
        }
        if(!this.getServer().getPluginManager().isPluginEnabled("AuthMe")||this.getConfig().getBoolean("settings.authme")){
            this.getConfig().set("settings.authme",false);
            log.info("Didn't find AuthMe, reset authme to false");
        }
        for (Player i: Bukkit.getOnlinePlayers()){
            Storage.time.put(i.getUniqueId().toString(),instance.getConfig().getInt("settings.time"));
            Storage.isChecked.put(i.getUniqueId().toString(),false);
            Storage.isChecking.put(i.getUniqueId().toString(),false);
        }
        getCommand("aa").setExecutor(this);
        Tasks task = new Tasks();
        task.runTaskTimer(this,0,20);
        if (instance.getConfig().getInt("settings.use_holiday")==2){
            recv = GetHttpRequest.sendGet("http://timor.tech/api/holiday/year","");
            if (recv==null){this.getConfig().set("use_holiday",0);log.info("Fail to load json, reset use_holiday to 0");}
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("Anti")){ // your command name
            if(args.length != 1 ){
                sender.sendMessage("Incorrect Syntax!!"); //prints "incorrect syntax"
                return true;
            }
            if (args[0].equals("reload")) {
            reloadConfig();
            sender.sendMessage("Reloaded");
            }
        }
        return false;
    }
    public List<String> onTabComplete(CommandSender sender, //registers the auto tab completer
                                      Command command,
                                      String alias,
                                      String[] args){
        if(command.getName().equalsIgnoreCase("aa")){
            List<String> l = new ArrayList<String>();
            if (args.length==1){
                l.add("reload");
            }
            return l;
        }
        return null;
    }

}
