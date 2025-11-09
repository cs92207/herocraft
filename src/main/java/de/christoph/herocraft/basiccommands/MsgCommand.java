package de.christoph.herocraft.basiccommands;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class MsgCommand implements CommandExecutor {
    private final Map<UUID, UUID> replyMap;

    public MsgCommand(Map<UUID, UUID> replyMap) {
        this.replyMap = replyMap;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§e§lAnyBlocks §7§l| " + "§7Nur Spieler können diesen Befehl benutzen.");
            return true;
        }

        Player senderPlayer = (Player) sender;

        if (args.length < 2) {
            senderPlayer.sendMessage("§e§lAnyBlocks §7§l| " + "§7Benutzung: /msg <Spieler> <Nachricht>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            senderPlayer.sendMessage("§e§lAnyBlocks §7§l| " + "§7Dieser Spieler ist nicht online.");
            return true;
        }

        if (target.equals(senderPlayer)) {
            senderPlayer.sendMessage("§e§lAnyBlocks §7§l| " + "§7Du kannst dir nicht selbst schreiben.");
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        senderPlayer.sendMessage("§7[§eDu §7-> §e" + target.getName() + "§7] §f" + message);
        target.sendMessage("§7[§e" + senderPlayer.getName() + " §7-> §eDir§7] §f" + message);

        // Für /r speichern, wer wem zuletzt geschrieben hat
        replyMap.put(target.getUniqueId(), senderPlayer.getUniqueId());

        return true;
    }
}
