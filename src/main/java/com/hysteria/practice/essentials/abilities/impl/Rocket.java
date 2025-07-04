package com.hysteria.practice.essentials.abilities.impl;

import com.hysteria.practice.essentials.abilities.utils.DurationFormatter;
import com.hysteria.practice.essentials.abilities.Ability;
import com.hysteria.practice.HyPractice;
import com.hysteria.practice.player.profile.Profile;
import com.hysteria.practice.utilities.PlayerUtil;
import com.hysteria.practice.utilities.chat.CC;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class Rocket extends Ability {

    public Rocket() {
        super("ROCKET");
    }

    @EventHandler
    public void onItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!isAbility(player.getItemInHand())) {
                return;
            }

            if (profile.getRocket().onCooldown(player)) {
                player.sendMessage(CC.translate("&7You are on &c&lRocket &7cooldown for &4" + DurationFormatter.getRemaining(profile.getRocket().getRemainingMillis(player), true, true)));
                player.updateInventory();
                event.setCancelled(true);
                return;
            }

            if (profile.getPartneritem().onCooldown(player)) {
                player.sendMessage(CC.translate("&7You are on &c&lPartner Item &7cooldown &7for &4" + DurationFormatter.getRemaining(profile.getPartneritem().getRemainingMillis(player), true, true)));
                player.updateInventory();
                event.setCancelled(true);
                return;
            }

            if (isAbility(player.getItemInHand())) {
                player.setVelocity(new Vector(0.1D, 2.0D, 0.0D));

                PlayerUtil.decrement(player);

                profile.getRocket().applyCooldown(player,  60 * 1000);
                profile.getPartneritem().applyCooldown(player, 10 * 1000);

                player.setMetadata("rocket", new FixedMetadataValue(HyPractice.get(), true));
            }
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
                    player.sendMessage(CC.translate("&7You are on cooldown for &4" + DurationFormatter.getRemaining(profile.getRocket().getRemainingMillis(player), true)));
                    event.setCancelled(true);
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void fallDamage(final EntityDamageEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            final Player player = (Player)event.getEntity();
            Profile profile = Profile.get(player.getUniqueId());
            if (player.hasMetadata("rocket")) {
                event.setCancelled(true);
                player.removeMetadata("rocket", HyPractice.get());
            }
        }
    }
}
