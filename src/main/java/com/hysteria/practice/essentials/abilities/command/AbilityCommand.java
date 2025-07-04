package com.hysteria.practice.essentials.abilities.command;

import com.hysteria.practice.HyPractice;
import com.hysteria.practice.essentials.abilities.Ability;
import com.hysteria.practice.utilities.JavaUtils;
import com.hysteria.practice.utilities.chat.CC;
import com.hysteria.practice.api.command.BaseCommand;
import com.hysteria.practice.api.command.Command;
import com.hysteria.practice.api.command.CommandArgs;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
@Setter
public class AbilityCommand extends BaseCommand {

    private HyPractice plugin = HyPractice.get();

    @Command(name = "ability", permission = "hypractice.command.ability")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();
        if (args.length < 1) {
            this.getUsage(player, "ability");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "give":
                if (args.length < 4) {
                    CC.sender(player, "&cUsage: /ability give <player> <ability|all> <amount>");
                    return;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    CC.sender(player, "&cPlayer '" + args[1] + "' not found.");
                    return;
                }

                Integer amount = JavaUtils.tryParseInt(args[3]);

                if (amount == null) {
                    CC.sender(player, "&cAmount must be a number.");
                    return;
                }
                if (amount <= 0) {
                    CC.sender(player, "&cAmount must be positive.");
                    return;
                }

                plugin.getAbilityManager().getAbilities().forEach(ability -> {
                    String displayName = HyPractice.get().getAbilityConfig().getString(ability + ".ICON.DISPLAYNAME");
                    if (args[2].equalsIgnoreCase(ability)) {
                        plugin.getAbilityManager().giveAbility(player, target, ability, displayName, amount);
                        return;
                    }
                    if (args[2].equals("all")) {
                        plugin.getAbilityManager().giveAbility(player, target, ability, displayName, amount);
                    }
                });
                break;
            case "list":
                CC.sender(player, "&7&m-----------------------------");
                CC.sender(player, "&c&lAbilities List &7(" + Ability.getAbilities().size() + ")");
                CC.sender(player, "");
                plugin.getAbilityManager().getAbilities().forEach(
                        ability -> CC.sender(player, " &7- &4" + ability));
                CC.sender(player, "&7&m-----------------------------");
                break;
        }
        return;
    }

    private void getUsage(CommandSender sender, String label) {
        CC.sender(sender, "&7&m-----------------------------");
        CC.sender(sender, "&c&lAbility Help");
        CC.sender(sender, "");
        CC.sender(sender, "&4/" + label + " give <player> <ability|all> <amount>");
        CC.sender(sender, "&4/" + label + " list");
        CC.sender(sender, "&7&m-----------------------------");
    }
}
