/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package playeriplocation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.System.out;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
/**
 *
 * @author Administrator
 */
public class PlayerIPLocation extends JavaPlugin{
    public QQWry wry=new QQWry();
    YamlConfiguration CfgFile;
    Boolean OPFlag;
    
    public class EventList implements Listener{
        @EventHandler
        public void Pjoin(PlayerJoinEvent event)
        {
            if(event.getPlayer().isOp())
            {
                if(!OPFlag)
                    return;
            }
            byte ip[]=GetPlayerIPaddress(event.getPlayer());
            String Loc=wry.QQWryGetLocation(ip);
            if(Loc.length()>2)
            {
                int pos=Loc.indexOf("CZ88.NET");
                if(pos!=-1)
                {
                    Loc=Loc.substring(0,pos);
                }
                event.setJoinMessage("来自"+Loc+"的玩家"+event.getPlayer().getName()+"加入了游戏!");
            }
        }
        private byte []GetPlayerIPaddress(Player p){
            return p.getAddress().getAddress().getAddress();
        }
    }
    
    public void PluginInit(){
        OPFlag=true;
        File cfg=new File(getDataFolder(),"config.yml");
        File dat=new File(getDataFolder(),"qqwry.dat");
        if(!cfg.exists())
        {
            this.saveDefaultConfig();
        }
        if(!dat.exists())
        {
            this.saveResource("qqwry.dat", true);
        }
        CfgFile=YamlConfiguration.loadConfiguration(cfg);
        OPFlag=CfgFile.getBoolean("DisplayOP");
        try {
        if(wry.Init(dat))
        {
            getServer().getPluginManager().registerEvents(new EventList(), this);
            out.print("加载成功-文件长度:"+wry.DataBuffLen+"起点："+wry.StartPos+"终点:"+wry.EndPos);
        }
        else{
            Logger.getLogger("加载出错");
        }
        } catch (IOException e) {
             Logger.getLogger("加载出错");
        }
    }
   /* public static void main(String[] args) {
        PlayerIPLocation t=new PlayerIPLocation();
        File dat=new File("C:\\Users\\Administrator\\Desktop\\qqwry (2).dat");
        if(!dat.exists())
        {
            out.print("错误");
        }
        try {
            if(!t.wry.Init(dat))
            {
                out.print("错误");
            }
        } catch (IOException e) {
        }
        byte a[]={127,0,0,1};
        String loc=t.wry.QQWryGetLocation(a);
        out.print(loc);
    }*/
    @Override
    public void onEnable(){
        out.print("123");
        out.print("开始加载插件");
        PluginInit();
    }
}
