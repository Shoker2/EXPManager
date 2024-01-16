package org.ts2.expmanager;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Player;

public class AddToMaxLevelCommand {
    public static void init() {
        new CommandAPICommand("addmaxlvl")
                .withPermission(CommandPermission.fromString("expmanager.addmaxlvl"))
                .withArguments(new EntitySelectorArgument.OnePlayer("player"))
                .withArguments(new StringArgument("add to limit level"))
                .executes((sender, args) -> {

                    @SuppressWarnings("unchecked")
                    Player player = (Player) args.get(0);

                    String addMaxLevel = (String) args.get(1);
                    if (Integer.valueOf(addMaxLevel) != null) {
                        String maxLevelOld = EXPManager.instance.getConfig().getString("players." + player.getName());
                        if (Integer.valueOf(maxLevelOld) == null) {
                            maxLevelOld = "0";
                        }

                        int maxLevel = Integer.parseInt(maxLevelOld) + Integer.parseInt(addMaxLevel);
                        EXPManager.instance.getConfig().set("players." + player.getName(), maxLevel);
                    }

                })
                .register();
    }
}
