package io.github.yin.proxyinfospigot

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.util.*


class ProxyInfoSpigotMain : JavaPlugin(), TabExecutor, PluginMessageListener {

    companion object {
        lateinit var instance: ProxyInfoSpigotMain
        const val prefix = "§f[§3代理信息§f] "
        const val pluginChannel = "proxyinfo:channel"
        var serverName = ""
        var players = listOf<String>()
    }

    override fun onEnable() {
        instance = this

        server.messenger.registerOutgoingPluginChannel(this, pluginChannel)
        server.messenger.registerIncomingPluginChannel(this, pluginChannel, this)

        getCommand("proxyinfospigot")?.setExecutor(this)

        server.consoleSender.sendMessage(prefix + "插件开始加载 " + description.version)
    }

    override fun onDisable() {
        server.messenger.unregisterOutgoingPluginChannel(this, pluginChannel)
        server.messenger.unregisterIncomingPluginChannel(this, pluginChannel, this)

        server.consoleSender.sendMessage(prefix + "插件开始卸载 " + description.version)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, arguments: Array<out String>): Boolean {
        when (arguments.size) {
            1 -> {
                when {
                    suggestion(sender, "servername", arguments[0]) -> {
                        sender.sendMessage(prefix + "在代理的服务器名是 " + serverName)
                    }
                    suggestion(sender, "players", arguments[0]) -> {
                        sender.sendMessage(prefix + "代理玩家列表 " + players)
                    }
                }
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, arguments: Array<out String>): List<String> {
        return when (arguments.size) {
            1 -> {
                listMatches(arguments[0], listOf("servername", "players"))
            }
            else -> {
                emptyList()
            }
        }
    }

    private fun listMatches(argument: String, suggest: Iterable<String>): MutableList<String> {
        return suggest.filter { it.contains(argument) }.toMutableList()
    }

    private fun permissionMessage(sender: CommandSender, permission: String): Boolean {
        if (sender.hasPermission(permission)) {
            return true
        }
        sender.sendMessage("${prefix}您没有 $permission 权限")
        return false
    }

    private val pluginName = instance.description.name.lowercase()
    private fun suggestion(sender: CommandSender, argument: String, vararg suggest: String): Boolean {
        val lowerCaseArgument = argument.lowercase(Locale.getDefault())
        if (lowerCaseArgument in suggest) {
            return permissionMessage(
                sender, "$pluginName.command.$lowerCaseArgument"
            )
        }
        return false
    }

    @EventHandler(priority = EventPriority.NORMAL)
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel == pluginChannel) {
            DataInputStream(ByteArrayInputStream(message)).use { input ->
                val action = input.readUTF().lowercase()
                when (action) {
                    "servername" -> {
                        serverName = input.readUTF()
                    }
                    "players" -> {
                        players = input.readUTF().split(",")
                    }
                }
            }
        }
    }



}





    /*
    // 手动模式
    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (serverName.isEmpty()) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            DataOutputStream(byteArrayOutputStream).use { out ->
                out.writeUTF("servername")
            }
            Bukkit.getOnlinePlayers().first().sendPluginMessage(this, pluginChannel, byteArrayOutputStream.toByteArray())
            loadPlayers()
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel == pluginChannel) {
            DataInputStream(ByteArrayInputStream(message)).use { input ->
                val action = input.readUTF()
                when (action) {
                    "servername" -> {
                        serverName = input.readUTF()
                    }
                    "players" -> {
                        players = input.readUTF().split(", ")
                    }
                }
            }
        }
    }

    fun loadPlayers() {
        val byteArrayOutputStream = ByteArrayOutputStream()
        DataOutputStream(byteArrayOutputStream).use { out ->
            out.writeUTF("players")
        }
        Bukkit.getOnlinePlayers().first().sendPluginMessage(this, pluginChannel, byteArrayOutputStream.toByteArray())
    }

     */




