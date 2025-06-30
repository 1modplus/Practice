package com.hysteria.practice.essentials.abilities.impl;

import com.hysteria.practice.HyPractice;
import com.hysteria.practice.essentials.abilities.utils.DurationFormatter;
import com.hysteria.practice.essentials.abilities.Ability;
import com.hysteria.practice.player.profile.Profile;
import com.hysteria.practice.utilities.PlayerUtil;
import com.hysteria.practice.utilities.chat.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Scrambler extends Ability {

    private final HyPractice plugin = HyPractice.get();

    public Scrambler() {
        super("SCRAMBLER");
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Profile profile = Profile.get(damager.getUniqueId());
            if (!isAbility(damager.getItemInHand())) return;

            if (profile.getScrambler().onCooldown(damager)) {
                damager.sendMessage(CC.translate("&7You are on &c&lScrambler &7cooldown for &4" + DurationFormatter.getRemaining(profile.getScrambler().getRemainingMillis(damager), true, true)));
                damager.updateInventory();
                return;
            }

            if(profile.getPartneritem().onCooldown(damager)){
                damager.sendMessage(CC.translate("&7You are on &c&lPartner Item &7cooldown &7for &4" + DurationFormatter.getRemaining(profile.getPartneritem().getRemainingMillis(damager), true, true)));
                damager.updateInventory();
                return;
            }

            PlayerUtil.decrement(damager);

            Player victim = (Player) event.getEntity();

            profile.getScrambler().applyCooldown(damager, 60 * 1000);
            profile.getPartneritem().applyCooldown(damager,  10 * 1000);

            shuffleInventory(victim);

            plugin.getAbilityManager().cooldownExpired(damager, this.getName(), this.getAbility());
            plugin.getAbilityManager().playerMessage(damager, this.getAbility());
            plugin.getAbilityManager().targetMessage(victim, damager, this.getAbility());
        }
    }

    @EventHandler
    public void checkCooldown(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Profile profile = Profile.get(player.getUniqueId());
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            if (!isAbility(player.getItemInHand())) {
                return;
            }
            if (isAbility(player.getItemInHand())) {
                if (this.hasCooldown(player)) {
                    player.sendMessage(CC.translate("&7You are on cooldown for &4" + DurationFormatter.getRemaining(profile.getCombo().getRemainingMillis(player), true)));
                    event.setCancelled(true);
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!isAbility(event.getItem())) return;

        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            event.setCancelled(true);

            if (this.hasCooldown(player)) {
                plugin.getAbilityManager().cooldown(player, this.getName(), this.getCooldown(player));
                player.updateInventory();
            }
        }
    }

    private void shuffleInventory(Player player) {
        List<ItemStack> playerHotbar = new ArrayList<>(9);
        PlayerInventory inventory = player.getInventory();
        ItemStack empty = new ItemStack(Material.AIR);

        for (int i = 0; i < 9; i++) {
            playerHotbar.add(inventory.getItem(i));
        }

        Collections.shuffle(playerHotbar);

        for (int i = 0; i < playerHotbar.size(); i++) {
            ItemStack item = playerHotbar.get(i);
            inventory.setItem(i, item == null ? empty : item);
        }

        player.updateInventory();
        playerHotbar.clear();
    }
}
