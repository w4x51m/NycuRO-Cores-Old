package nycuro.jobs.handlers;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import nycuro.API;
import nycuro.database.Database;
import nycuro.database.objects.Profile;

/**
 * author: NycuRO
 * NycuRO-Factions Project
 * API 1.0.0
 */
public class JobsHandlers implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (API.getMechanicAPI().isOnSpawn(player)) return;
        if (API.getMechanicAPI().isOnPvP(player)) return;
        Profile profile = Database.profile.get(player.getUniqueId());
        int job = profile.getJob();
        switch (job) {
            case 1:
                switch (block.getId()) {
                    case Block.WOOD:
                    case Block.WOOD2:
                        profile.addCoins(1.0);
                        profile.addExperience(1.0);
                        break;
                }
                break;
            case 2:
                switch (block.getId()) {
                    case Block.COBBLESTONE:
                    case Block.STONE:
                    case Block.COAL_ORE:
                    case Block.IRON_ORE:
                    case Block.GOLD_ORE:
                    case Block.DIAMOND_ORE:
                    case Block.LAPIS_ORE:
                    case Block.REDSTONE_ORE:
                        profile.addCoins(2.5);
                        profile.addExperience(2.0);
                        break;
                }
                break;
            case 3:
                switch (block.getId()) {
                    case 59:
                    case 104:
                    case 105:
                    case 244:
                    case 141:
                    case 142:
                        switch (block.getDamage()) {
                            case 7:
                                profile.addCoins(0.5);
                                profile.addExperience(1.0);
                                break;
                        }
                    case Block.PUMPKIN:
                    case Block.MELON_BLOCK:
                    case Block.SUGARCANE_BLOCK:
                    case Block.CACTUS:
                    case 175:
                    case Block.SAPLING:
                    case Block.FLOWER:
                        profile.addCoins(0.5);
                        profile.addExperience(1.0);
                        break;
                    case 127:
                        switch (block.getDamage()) {
                            case 2:
                                profile.addCoins(0.5);
                                profile.addExperience(1.0);
                                break;
                        }
                }
                break;
        }
    }

    @EventHandler
    public void onHurt(EntityDamageEvent event) {
        Entity eventEntity = event.getEntity();
        if (eventEntity == null) return;
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) event;
            if (ev instanceof EntityDamageByChildEntityEvent) {
                EntityDamageByChildEntityEvent evc = (EntityDamageByChildEntityEvent) ev;
                if (evc.getDamager() instanceof Player) {
                    Player damager = (Player) evc.getDamager();
                    if (!API.getMechanicAPI().isOnSpawn(damager)) {
                        if (!API.getMechanicAPI().isOnSpawn(eventEntity)) {
                            sendToRespawn(eventEntity, damager, event);
                            Profile profile = Database.profile.get(damager.getUniqueId());
                            int job = profile.getJob();
                            switch (job) {
                                case 4:
                                    if (eventEntity instanceof Player) {
                                        profile.addCoins(0.4);
                                        profile.addExperience(2.0);
                                    }
                                    break;
                                case 5:
                                    if (!(eventEntity instanceof Player)) {
                                        profile.addCoins(0.4);
                                        profile.addExperience(1.0);
                                    }
                                    break;
                            }
                        }
                    }
                }
            } else if (ev.getDamager() instanceof Player) {
                Player damager = (Player) ev.getDamager();
                if (!API.getMechanicAPI().isOnSpawn(damager)) {
                    if (!API.getMechanicAPI().isOnSpawn(eventEntity)) {
                        sendToRespawn(eventEntity, damager, event);
                        Profile profile = Database.profile.get(damager.getUniqueId());
                        int job = profile.getJob();
                        switch (job) {
                            case 4:
                                if (eventEntity instanceof Player) {
                                    profile.addCoins(0.4);
                                    profile.addExperience(2.0);
                                }
                                break;
                            case 5:
                                if (!(eventEntity instanceof Player)) {
                                    profile.addCoins(0.4);
                                    profile.addExperience(1.0);
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity eventEntity = event.getEntity();
        if (eventEntity instanceof Player) return;
        if ((eventEntity.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
            if (((EntityDamageByEntityEvent) eventEntity.getLastDamageCause()).getDamager() instanceof Player) {
                Player killer = (Player) ((EntityDamageByEntityEvent) eventEntity.getLastDamageCause()).getDamager();
                Profile profile = Database.profile.get(killer.getUniqueId());
                int job = profile.getJob();
                switch (job) {
                    case 5:
                        profile.addCoins(1.0);
                        profile.addExperience(5.0);
                        break;
                }
            }
        }
    }

    /** Credits: @Nora. Thanks! */
    /** FIX: When adding mobs */
    private void sendToRespawn(Entity entity, Player damager, EntityDamageEvent event) {
        Player player = (Player) entity;
        API.getMessageAPI().sendHitBowMessage(player, damager);
        if (event.getDamage() > player.getHealth()) {
            for (Item item : player.getInventory().getContents().values()) {
                player.getLevel().dropItem(new Vector3(player.getX(), player.getY() + 1, player.getZ()), item);
            }
            event.setCancelled();
            player.setHealth(20);
            player.getFoodData().setLevel(20);
            player.teleport(player.getServer().getDefaultLevel().getSpawnLocation());
            player.removeAllEffects();
            player.getInventory().clearAll();
            API.getMessageAPI().sendDeadMessage(player, damager);
            Profile profilePlayer = Database.profile.get(player.getUniqueId());
            Profile profileDamager = Database.profile.get(damager.getUniqueId()); // Todo: Zombies, Monsters.
            profilePlayer.addDeaths(1);
            profileDamager.addKills(1);

        }
        if (player.getPosition().getY() < 0) {
            event.setCancelled();
            player.setHealth(20);
            player.getFoodData().setLevel(20);
            player.teleport(player.getServer().getDefaultLevel().getSpawnLocation());
            player.removeAllEffects();
            player.getInventory().clearAll();
            Profile profilePlayer = Database.profile.get(player.getUniqueId());
            profilePlayer.addDeaths(1);
        }
    }
}
