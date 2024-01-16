package org.ts2.expmanager;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetMaxLevelCommand {
    public static void init() {
        new CommandAPICommand("setmaxlvl")
                .withPermission(CommandPermission.fromString("expmanager.setmaxlvl"))
                .withArguments(new EntitySelectorArgument.OnePlayer("player"))
                .withArguments(new StringArgument("max level"))
                .executes((sender, args) -> {

                    @SuppressWarnings("unchecked")
                    Player player = (Player) args.get(0);

//                    String maxLevel = EXPManager.instance.getConfig().getString("players." + player.getName());
                    String maxLevel = (String) args.get(1);
                    if (Integer.valueOf(maxLevel) != null) {
                        EXPManager.instance.getConfig().set("players." + player.getName(), Integer.parseInt(maxLevel));
                    }

                })
                .register();
    }
}
