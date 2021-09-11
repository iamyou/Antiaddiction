package cc.silenter.antiaddictionbyqq;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.xephi.authme.api.v3.AuthMeApi;
import me.dreamvoid.miraimc.api.MiraiMC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileReader;
import java.util.*;

import static cc.silenter.antiaddictionbyqq.AntiaddictionByQQ.*;

public class Tasks extends BukkitRunnable {

    @Override
    public void run() {
        boolean rightTime = checkPlay();
        for (Player i : Bukkit.getOnlinePlayers()) {
            if (Storage.isChecked.get(i.getUniqueId().toString())) {
                if (Storage.isAdult.get(i.getUniqueId().toString())) {
                    continue;
                } else {
                    if (!rightTime) {
                        i.kickPlayer(instance.getConfig().getString("message.not_allow").replaceAll("&","§"));
                    }
                    continue;
                }
            }
            String QQ = String.valueOf(MiraiMC.getBinding(i.getUniqueId().toString()));
            if (Storage.time.get(i.getUniqueId().toString()) == 0) {
                i.kickPlayer(instance.getConfig().getString("message.timeout").replaceAll("&","§"));
                continue;
            }
            //-1S...-1S...
            if (instance.getConfig().getBoolean("settings.authme")) {
                if (!AuthMeApi.getInstance().isAuthenticated(i)) {
                    continue;
                } else {
                    Storage.time.put(i.getUniqueId().toString(), Storage.time.get(i.getUniqueId().toString()) - 1);
                }
            } else {
                Storage.time.put(i.getUniqueId().toString(), Storage.time.get(i.getUniqueId().toString()) - 1);
            }

            if (QQ.equals("0")) {
                i.sendMessage(new String[] {instance.getConfig().getString("message.not_bind1"), instance.getConfig().getString("message.not_bind2")});
                continue;
            }
            if (!Storage.isChecking.get(i.getUniqueId().toString()) || !Storage.isChecked.get(i.getUniqueId().toString())) {
                checkAdult(i.getUniqueId(), QQ);
            }
        }
    }

    private void checkAdult(UUID UUID, String QQ) {
        Storage.isChecking.put(UUID.toString(), true);
        Objects.requireNonNull(Bukkit.getPlayer(UUID)).sendMessage(new String[]{instance.getConfig().getString("message.checking1").replaceAll("&","§"), instance.getConfig().getString("message.checking2").replaceAll("&","§")});
        JsonObject back = GetHttpRequest.SendPostJsonObject("https://www.wegame.com.cn/api/middle/lua/realname/check_user_real_name", "{\"qq_login_key\":{\"qq_key_type\":3,\"uint64_uin\":" + QQ + "},\"acc_type\":1,\"uint64_uin\":" + QQ + "}\n");
        if (back == null) {
            Storage.isChecking.put(UUID.toString(), false);
            return;
        }
        if (!back.get("result").getAsString().equals("0")) {
            Storage.isChecking.put(UUID.toString(), false);
        } else {
            if (!back.get("is_realname").getAsString().equals("1")) {
                Objects.requireNonNull(Bukkit.getPlayer(UUID)).kickPlayer(instance.getConfig().getString("message.not_realname").replaceAll("&","§"));
            }
            if (!back.get("is_adult").getAsString().equals("1")) {
                Storage.isAdult.put(UUID.toString(), false);
                Storage.isChecked.put(UUID.toString(), true);
                Storage.isChecking.put(UUID.toString(), false);
                Objects.requireNonNull(Bukkit.getPlayer(UUID)).sendMessage(new String[]{instance.getConfig().getString("message.not_adult1").replaceAll("&","§"), instance.getConfig().getString("message.not_adult2").replaceAll("&","§")});
            } else {
                Storage.isAdult.put(UUID.toString(), true);
                Storage.isChecked.put(UUID.toString(), true);
                Storage.isChecking.put(UUID.toString(), false);
                Objects.requireNonNull(Bukkit.getPlayer(UUID)).sendMessage(new String[]{instance.getConfig().getString("message.is_adult1").replaceAll("&","§"), instance.getConfig().getString("message.is_adult2").replaceAll("&","§")});
            }
        }
    }

    private boolean checkPlay() {
        Date date = new Date();
        String Week = String.format(Locale.US, "%ta", date);
        ArrayList<String> allowWeek = new ArrayList<>(Arrays.asList("Fri", "Sat", "Sun"));
        String day = String.format("%td", date);
        String month = String.format("%tm", date);
        JsonObject Holiday;
        String allow;
        switch (instance.getConfig().getInt("settings.use_holiday")) {
            case 0:
                if (allowWeek.contains(Week)) {
                    String Hour = String.format("%tk", date);
                    return Hour.equals("20");
                }
                break;
            case 1:
                try {
                    Holiday = new JsonParser().parse(new FileReader(new File(instance.getDataFolder(), "Holiday.json"))).getAsJsonObject();
                } catch (Exception e) {
                    instance.getConfig().set("settings.use_holiday", 0);
                    log.info("Fail to load json, reset use_holiday to 0");
                    return true;
                }
                assert Holiday != null;
                try {
                    if (Holiday.getAsJsonObject("holiday").getAsJsonObject(month + "-" + day) == null) {
                        allow = "false";
                    } else {
                        allow = Holiday.getAsJsonObject("holiday").getAsJsonObject(month + "-" + day).getAsJsonObject("holiday").toString();
                    }
                } catch (Exception e) {
                    instance.getConfig().set("settings.use_holiday", 0);
                    log.info("Fail to load json, reset use_holiday to 0");
                    return true;
                }
                if (allowWeek.contains(Week) && allow.equals("true")) {
                    String Hour = String.format("%tk", date);
                    return Hour.equals("20");
                }
                break;
            case 2:
                assert recv != null;
                try {
                    if (recv.getAsJsonObject("holiday").getAsJsonObject(month + "-" + day) == null) {
                        allow = "false";
                    } else {
                        allow = recv.getAsJsonObject("holiday").getAsJsonObject(month + "-" + day).getAsJsonObject("holiday").toString();
                    }
                } catch (Exception e) {
                    instance.getConfig().set("settings.use_holiday", 0);
                    log.info("Fail to load json, reset use_holiday to 0");
                    return true;
                }
                if (allowWeek.contains(Week) && allow.equals("true")) {
                    String Hour = String.format("%tk", date);
                    return Hour.equals("20");
                }
                break;
            default:return false;
        }
        return false;
    }
}
