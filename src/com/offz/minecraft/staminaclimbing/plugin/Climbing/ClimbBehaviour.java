package com.offz.minecraft.staminaclimbing.plugin.Climbing;

import com.offz.minecraft.staminaclimbing.plugin.Stamina.StaminaBar;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClimbBehaviour {

    public static Map<UUID, Integer> jumpCount = new HashMap<>();

    public static void onClick(PlayerInteractEvent e) {
        if(!StaminaBar.toggled.contains(e.getPlayer().getUniqueId())) return;

        Player p = e.getPlayer();
        BossBar b = StaminaBar.registeredBars.get(p.getUniqueId());
        double progress = b.getProgress();
        int playerJumpCount = jumpCount.get(p.getUniqueId());

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && playerJumpCount <= 2 && e.getClickedBlock().getType().isSolid() && progress > 0.1 && p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {

            double multipler = 1;
            if(playerJumpCount > 1) //Second jump makes players jump slightly higher
                multipler = 1.1;
            Location L1 = p.getLocation();
            Location L2 = e.getClickedBlock().getLocation();
            L2.setX(L2.getX() + 0.5);   L2.setY(L2.getY() - 0.5);   L2.setZ(L2.getZ() + 0.5);//Get location at center of block
            double distance = L1.distance(L2);

            switch (e.getBlockFace()) {
                case UP:
                    if (distance < 2.4 && !p.isOnGround()) {
                        jumpCount.put(p.getUniqueId(), playerJumpCount + 1);
                        p.setVelocity(p.getVelocity().setY(0.4 * multipler));
                    }else
                        return;
                    break;
                case EAST:
                    if (distance < 2.4 && p.isSneaking()) {
                        p.setVelocity(new Vector(0.4, 0.5 * multipler, p.getVelocity().getZ()));
                        p.setFallDistance(p.getFallDistance() / (float) 1.2);
                    } else {
                        p.setVelocity(new Vector(p.getVelocity().getX(), 0.3 * multipler, p.getVelocity().getZ()));
                        p.setFallDistance(p.getFallDistance() / (float) 1.2);
                    }
                    break;
                case WEST:
                    if (distance < 2.4 && p.isSneaking()) {
                        p.setVelocity(new Vector(-0.4, 0.5 * multipler, p.getVelocity().getZ()));
                        p.setFallDistance(p.getFallDistance() / (float) 1.2);
                    } else {
                        p.setVelocity(new Vector(p.getVelocity().getX(), 0.3 * multipler, p.getVelocity().getZ()));
                        p.setFallDistance(p.getFallDistance() / (float) 1.2);
                    }
                    break;
                case NORTH:
                    if (distance < 2.4 && p.isSneaking()) {
                        p.setVelocity(new Vector(p.getVelocity().getX(), 0.5 * multipler, -0.4));
                        p.setFallDistance(p.getFallDistance() / (float) 1.2);
                    } else {
                        p.setVelocity(new Vector(p.getVelocity().getX(), 0.3 * multipler, p.getVelocity().getZ()));
                        p.setFallDistance(p.getFallDistance() / (float) 1.2);
                    }
                    break;
                case SOUTH:
                    if (distance < 2.4 && p.isSneaking()) {
                        p.setVelocity(new Vector(p.getVelocity().getX(), 0.5 * multipler, 0.4));
                        p.setFallDistance(p.getFallDistance() / (float) 1.2);
                    } else {
                        p.setVelocity(new Vector(p.getVelocity().getX(), 0.3 * multipler, p.getVelocity().getZ()));
                        p.setFallDistance(p.getFallDistance() / (float) 1.2);
                    }
                    break;
            }
            jumpCount.put(p.getUniqueId(), playerJumpCount + 1);
            if (progress - 0.01 >= 0) {
                b.setProgress(progress - 0.02);
            }
        }
    }
    public static void onLeftClick(PlayerAnimationEvent e) {
        if(!StaminaBar.toggled.contains(e.getPlayer().getUniqueId())) return;

        Player p = e.getPlayer();
        BossBar b = StaminaBar.registeredBars.get(p.getUniqueId());
        double progress = b.getProgress();
        int playerJumpCount = jumpCount.get(p.getUniqueId());

        List<Block> blocks = p.getLastTwoTargetBlocks(null, 4); //Get two connected blocks player is looking at
        BlockFace blockFace = blocks.get(1).getFace(blocks.get(0)); //Find the face between both of these blocks
        if (blocks.get(1).getType() != Material.AIR && playerJumpCount <= 4 && playerJumpCount > 0) { //Make sure target block isn't air
            double multipler = 1;
            if (playerJumpCount > 1)
                multipler = 1.1;

            switch (blockFace) {
                case UP:
                    if (!p.isOnGround() && playerJumpCount <= 2) {
                        jumpCount.put(p.getUniqueId(), playerJumpCount + 2);
                        p.setVelocity(p.getVelocity().setY(0.4 * multipler));
                        if (progress - 0.01 >= 0) {
                            b.setProgress(progress - 0.01);
                        }
                    }
                    return;
                case EAST:
                    p.setVelocity(new Vector(p.getVelocity().getX(), 0.3 * multipler, Math.signum(p.getLocation().getDirection().getZ()) * 0.5));
                    p.setFallDistance(p.getFallDistance() / (float) 1.2);
                    break;
                case WEST:
                    p.setVelocity(new Vector(p.getVelocity().getX(), 0.3 * multipler, Math.signum(p.getLocation().getDirection().getZ()) * 0.5));
                    p.setFallDistance(p.getFallDistance() / (float) 1.2);
                    break;
                case NORTH:
                    p.setVelocity(new Vector(Math.signum(p.getLocation().getDirection().getX()) * 0.5, 0.3 * multipler, p.getVelocity().getZ()));
                    p.setFallDistance(p.getFallDistance() / (float) 1.2);
                    break;
                case SOUTH:
                    p.setVelocity(new Vector(Math.signum(p.getLocation().getDirection().getX()) * 0.5, 0.3 * multipler, p.getVelocity().getZ()));
                    p.setFallDistance(p.getFallDistance() / (float) 1.2);
                    break;
            }
            jumpCount.put(p.getUniqueId(), playerJumpCount + 4);
            if (progress - 0.01 >= 0) {
                b.setProgress(progress - 0.02);
            }
        }
    }

    public static void onFall(PlayerMoveEvent e) {
        if(!StaminaBar.toggled.contains(e.getPlayer().getUniqueId())) return;
        if (e.getPlayer().isOnGround()) {
            jumpCount.put(e.getPlayer().getUniqueId(), 0);
        }

    }
}