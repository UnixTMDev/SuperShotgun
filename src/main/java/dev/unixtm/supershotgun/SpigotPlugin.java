package dev.unixtm.supershotgun;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class SpigotPlugin extends JavaPlugin implements Listener {
    private HashMap<String, Integer> blockHits = new HashMap<>();
    private HashMap<String, Arrow[]> blockArrows = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("shotgun").setExecutor(new ShotgunCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("bye bye");
    }

    @EventHandler
    public void onArrowLand(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow)) {
            return;
        }

        Block hitBlock;
        hitBlock = event.getHitBlock();
        if (hitBlock == null) {
            return;
        }

        // Create a unique identifier for the block
        String blockId = hitBlock.getLocation().toString();

        // Increment hit counter for this block
        int hits = blockHits.getOrDefault(blockId, 0) + 1;
        blockHits.put(blockId, hits);

        // If block has been hit 3 times, destroy it
        if (hits >= 3) {
            deleteConnectedBlocks(hitBlock);
            hitBlock.breakNaturally();
            blockHits.remove(blockId);

        }
        event.getEntity().remove();
    }
    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        // Check if shooter is a player

        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        // Check if the bow has specific NBT (in this case, a custom name)
        ItemStack bow = event.getBow();
        if (bow == null || !bow.hasItemMeta() || !bow.getItemMeta().hasDisplayName())return;
        if (bow.getItemMeta().hasCustomModelData() && bow.getItemMeta().getCustomModelData() != 420){return;}  // Custom name

        // Cancel the default shot
        event.getProjectile().remove();

        // Spawn 50 arrows
        int arrowCount = bow.getItemMeta().getEnchantLevel(Enchantment.ARROW_INFINITE);
        float arrowForce = (float)bow.getItemMeta().getEnchantLevel(Enchantment.DURABILITY);
        float arrowSpread = (float)bow.getItemMeta().getEnchantLevel(Enchantment.ARROW_KNOCKBACK);
        if(arrowSpread == 1) arrowSpread = 0;
        for (int i = 0; i < arrowCount; i++) {
            Arrow arrow = player.getWorld().spawnArrow(
                    player.getLocation().add(0, 1.5, 0),
                    player.getLocation().getDirection(),
                    arrowForce, // Speed
                    arrowSpread // Spread (degrees)
            );

            // Make arrows belong to the player
            arrow.setShooter(player);
            arrow.setCritical(event.getForce() == 1.0f); // Critical if fully charged
        }


        return;
    }

    public static void deleteConnectedBlocks(Block startBlock) {
        if (startBlock == null) return;

        Material targetType = startBlock.getType();
        Set<Location> visited = new HashSet<>();
        deleteBlocksRecursively(startBlock, targetType, visited,0);
    }

    private static void deleteBlocksRecursively(Block block, Material targetType, Set<Location> visited, int recurses) {
        // Avoid visiting the same block twice
        if (visited.contains(block.getLocation())) return;

        if (recurses > 5) return;

        visited.add(block.getLocation());

        // Check if the block is the same type
        if (block.getType() != targetType) return;

        // Delete the block
        block.breakNaturally();

        // Recursively delete adjacent blocks
        for (Block adjacent : getAdjacentBlocks(block)) {
            deleteBlocksRecursively(adjacent, targetType, visited, recurses+1);
        }
    }

    private static Block[] getAdjacentBlocks(Block block) {
        return new Block[]{
                block.getRelative(1, 0, 0),  // +X
                block.getRelative(-1, 0, 0), // -X
                block.getRelative(0, 1, 0),  // +Y
                block.getRelative(0, -1, 0), // -Y
                block.getRelative(0, 0, 1),  // +Z
                block.getRelative(0, 0, -1)  // -Z
        };
    }

    // Optional: Create the custom bow
    public ItemStack createShotgunItem(int arrowCount, int arrowForce, int arrowSpread) {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        assert meta != null;
        meta.setDisplayName("§cShotgun"); // Fancy red name
        meta.addEnchant(Enchantment.ARROW_INFINITE, arrowCount, true);// Infinity tracks amount
        meta.addEnchant(Enchantment.DURABILITY, arrowForce, true);// Unbreaking for force
        meta.addEnchant(Enchantment.ARROW_KNOCKBACK, arrowSpread, true);// Knockback for spread
        meta.setCustomModelData(420);
        bow.setItemMeta(meta);
        return bow;
    }
}

