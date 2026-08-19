package dev.maicra.eruruupatch.compat.easyfarmers;

import dev.maicra.eruruupatch.blockentity.CutterBlockEntity;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Reflection-only bridge to Easy Villagers' VillagerItem/FarmerTileentity.
 *
 * Cutter owns the ItemStack itself. A virtual FarmerTileentity is used only because
 * Easy Villagers already knows how to turn that item data into the exact villager
 * entity used by its renderers and how to persist age changes back into the item.
 * No crop or Farmer inventory logic is reused here.
 */
public final class CutterVillagerAdapter {
    private static final ResourceLocation EASY_FARMER_ID = ResourceLocation.fromNamespaceAndPath("easy_villagers", "farmer");
    private static final String FARMER_TILEENTITY = "de.maxhenkel.easyvillagers.blocks.tileentity.FarmerTileentity";
    private static final String VILLAGER_ITEM = "de.maxhenkel.easyvillagers.items.VillagerItem";

    private final CutterBlockEntity owner;
    private BlockEntity delegate;
    private boolean failed;

    public CutterVillagerAdapter(CutterBlockEntity owner) {
        this.owner = owner;
    }

    public void reset() {
        delegate = null;
        failed = false;
    }

    public boolean isVillagerItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        try {
            return Class.forName(VILLAGER_ITEM).isInstance(stack.getItem());
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    public Villager getVillagerEntity() {
        BlockEntity farmer = getDelegate();
        if (farmer == null) {
            return null;
        }
        try {
            Object result = farmer.getClass().getMethod("getVillagerEntity").invoke(farmer);
            return result instanceof Villager villager ? villager : null;
        } catch (ReflectiveOperationException e) {
            fail();
            return null;
        }
    }

    public boolean hasAdultVillager() {
        Villager villager = getVillagerEntity();
        return villager != null && !villager.isBaby();
    }

    /** Advance the captured villager by one normal entity-age tick. */
    public void advanceAge() {
        BlockEntity farmer = getDelegate();
        if (farmer == null) {
            return;
        }
        try {
            farmer.getClass().getMethod("advanceAge").invoke(farmer);
        } catch (ReflectiveOperationException e) {
            fail();
        }
    }

    /** Persist virtual entity state (notably baby age) back into Cutter's VillagerItem. */
    public void flushToOwner() {
        BlockEntity farmer = getDelegate();
        if (farmer == null) {
            return;
        }
        try {
            Object value = farmer.getClass().getMethod("getVillager").invoke(farmer);
            if (value instanceof ItemStack stack && !stack.isEmpty()) {
                owner.eruruu$updateVillagerFromAdapter(stack.copyWithCount(1));
            }
        } catch (ReflectiveOperationException e) {
            fail();
        }
    }

    private BlockEntity getDelegate() {
        if (failed || owner.eruruu$getStoredVillager().isEmpty()) {
            return null;
        }
        Level level = owner.getLevel();
        if (delegate != null) {
            if (level != null && delegate.getLevel() != level) {
                delegate.setLevel(level);
            }
            return delegate;
        }

        try {
            Block easyFarmer = BuiltInRegistries.BLOCK.get(EASY_FARMER_ID);
            Class<?> clazz = Class.forName(FARMER_TILEENTITY);
            Constructor<?> constructor = clazz.getConstructor(net.minecraft.core.BlockPos.class, BlockState.class);
            delegate = (BlockEntity) constructor.newInstance(owner.getBlockPos(), easyFarmer.defaultBlockState());
            if (level != null) {
                delegate.setLevel(level);
            }

            findField(clazz, "villager").set(delegate, owner.eruruu$getStoredVillager().copyWithCount(1));
            findField(clazz, "villagerEntity").set(delegate, null);
            return delegate;
        } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            fail();
            return null;
        }
    }

    private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                Field field = current.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    private void fail() {
        failed = true;
        delegate = null;
    }
}
