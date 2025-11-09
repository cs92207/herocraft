package de.christoph.herocraft.basiccommands;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class ReplyCommand implements CommandExecutor {

    private final Map<UUID, UUID> replyMap;

    public ReplyCommand(Map<UUID, UUID> replyMap) {
        this.replyMap = replyMap;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Constant.PREFIX + "§7Nur Spieler können diesen Befehl benutzen.");
            return true;
        }

        Player senderPlayer = (Player) sender;

        if (args.length < 1) {
            senderPlayer.sendMessage(Constant.PREFIX + "§7Benutzung: /r <Nachricht>");
            return true;
        }

        UUID lastMessagedUUID = replyMap.get(senderPlayer.getUniqueId());
        if (lastMessagedUUID == null) {
            senderPlayer.sendMessage(Constant.PREFIX + "§7Du hast niemandem, dem du antworten kannst.");
            return true;
        }

        Player target = Bukkit.getPlayer(lastMessagedUUID);
        if (target == null || !target.isOnline()) {
            senderPlayer.sendMessage(Constant.PREFIX + "§7Der Spieler ist nicht mehr online.");
            return true;
        }

        String message = String.join(" ", args);

        senderPlayer.sendMessage("§7[§eDu §7-> §e" + target.getName() + "§7] §f" + message);
        target.sendMessage("§7[§e" + senderPlayer.getName() + " §7-> §eDir§7] §f" + message);

        // Antwortverlauf aktualisieren
        replyMap.put(target.getUniqueId(), senderPlayer.getUniqueId());

        return true;
    }

}
