package org.vinerdream.serverTeleportPaper;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class ServerTeleportPaper extends JavaPlugin implements Listener, PluginMessageListener {
    private static final String CHANNEL = "servertp:tp";
    private final Map<String, String> pending;

    public ServerTeleportPaper() {
        pending = new HashMap<>();
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, this);

    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this, CHANNEL, this);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player p, @NotNull byte[] message) {
        if (!channel.equals(CHANNEL)) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String playerName = in.readUTF();
        String destPlayerName = in.readUTF();

        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            Player destPlayer = Bukkit.getPlayer(destPlayerName);
            if (destPlayer != null) {
                player.teleport(destPlayer);
            }
        } else {
            pending.put(playerName, destPlayerName);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        if (pending.containsKey(name)) {
            Player destPlayer = Bukkit.getPlayer(pending.get(name));
            if (destPlayer != null) {
                event.getPlayer().teleport(destPlayer);
            }
            pending.remove(name);
        }
    }
}
