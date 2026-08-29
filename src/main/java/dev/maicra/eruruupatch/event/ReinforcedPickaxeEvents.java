package dev.maicra.eruruupatch.event;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.enchanting.EnchantmentLevelSetEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ReinforcedPickaxeEvents {
    private static final String MARKER = "eruruu_reinforced_pickaxe";
    private static final String UNITS = "eruruu_reinforced_units";
    private static final String BASE_NAME = "eruruu_reinforced_base_name";
    private static final String LEGACY_FUSIONS = "eruruu_reinforced_fusions";

    public static final int MAX_LEVEL = 30;
    public static final TagKey<Item> REINFORCEMENT_BLACKLIST = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("eruruu_patch", "reinforcement_blacklist")
    );

    private ReinforcedPickaxeEvents() {
    }

    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (!(left.getItem() instanceof PickaxeItem)) {
            return;
        }

        boolean leftReinforced = isReinforced(left);
        boolean rightPickaxe = !right.isEmpty() && right.getItem() instanceof PickaxeItem;
        boolean rightReinforced = rightPickaxe && isReinforced(right);

        if (right.isEmpty()) {
            if (leftReinforced && event.getName() != null) {
                handleRename(event);
            }
            return;
        }

        if (rightPickaxe && left.getItem() == right.getItem()) {

            if (!isEligibleBasePickaxe(left) || !isEligibleBasePickaxe(right)) {
                if (leftReinforced || rightReinforced) {
                    event.setCanceled(true);
                }
                return;
            }

            if (left.isEnchanted() || right.isEnchanted()) {
                if (leftReinforced || rightReinforced) {
                    event.setCanceled(true);
                }
                return;
            }

            if (left.getDamageValue() == 0 && right.getDamageValue() == 0) {
                handleFusion(event);
                return;
            }

            if (leftReinforced || rightReinforced) {
                event.setCanceled(true);
            }
            return;
        }

        if (!leftReinforced) {
            return;
        }

        if (left.getItem().isValidRepairItem(left, right)) {
            handleMaterialRepair(event);
            return;
        }

        event.setCanceled(true);
    }

    public static void onEnchantmentLevelSet(EnchantmentLevelSetEvent event) {
        if (isReinforced(event.getItem())) {

            event.setEnchantLevel(0);
        }
    }

    public static void onPlayerTickPost(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide() || event.getEntity().tickCount % 20 != 0) {
            return;
        }

        boolean changed = false;
        var inventory = event.getEntity().getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (normalizeLegacyStack(stack)) {
                changed = true;
            }
        }
        if (changed) {
            inventory.setChanged();
            event.getEntity().containerMenu.broadcastChanges();
        }
    }

    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!isReinforced(stack)) {
            return;
        }

        List<Component> tooltip = event.getToolTip();
        int insertionIndex = -1;
        for (int i = tooltip.size() - 1; i >= 0; i--) {
            if (isReinforcedProgressLore(tooltip.get(i))) {
                insertionIndex = i;
                tooltip.remove(i);
            }
        }

        Component levelLine = Component.translatable(
                "tooltip.eruruu_patch.reinforced_pickaxe_level",
                getLevel(stack),
                MAX_LEVEL
        ).withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC);

        if (insertionIndex >= 0 && insertionIndex <= tooltip.size()) {
            tooltip.add(insertionIndex, levelLine);
        } else {
            tooltip.add(Math.min(1, tooltip.size()), levelLine);
        }
    }

    private static void handleFusion(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        int combinedLevel = getLevel(left) + getLevel(right);
        if (combinedLevel > MAX_LEVEL) {
            event.setCanceled(true);
            return;
        }

        String baseName = getStoredBaseName(left);
        if (baseName == null && !isReinforced(left) && left.has(DataComponents.CUSTOM_NAME)) {
            baseName = left.getHoverName().getString();
        }

        ItemStack output = buildReinforcedPickaxe(left, combinedLevel, baseName);
        if (output.isEmpty()) {
            event.setCanceled(true);
            return;
        }

        event.setOutput(output);
        event.setCost(3);
        event.setMaterialCost(1);
    }

    private static void handleMaterialRepair(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        int damage = left.getDamageValue();
        if (damage <= 0 || right.isEmpty()) {
            event.setCanceled(true);
            return;
        }

        int baseMaxDamage = baseMaxDamage(left);
        if (baseMaxDamage <= 0) {
            event.setCanceled(true);
            return;
        }

        int repairPerMaterial = Math.max(1, (baseMaxDamage + 3) / 4);
        int materialsNeeded = (damage + repairPerMaterial - 1) / repairPerMaterial;
        int materialsUsed = Math.min(right.getCount(), materialsNeeded);
        if (materialsUsed <= 0) {
            event.setCanceled(true);
            return;
        }

        int repaired = Math.min(damage, materialsUsed * repairPerMaterial);
        int level = getLevel(left);
        String baseName = getStoredBaseName(left);

        ItemStack output = left.copy();
        output.setCount(1);
        output.set(DataComponents.DAMAGE, damage - repaired);
        output.set(DataComponents.REPAIR_COST, 0);
        writeReinforcedMetadata(output, level, baseName);
        applyDisplayName(output, baseName);
        applyLevelLore(output, level);

        int levelCost = Math.max(1, (materialsUsed + 3) / 4);

        event.setOutput(output);
        event.setCost(levelCost);
        event.setMaterialCost(materialsUsed);
    }

    private static void handleRename(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        String requested = event.getName();
        if (requested == null) {
            return;
        }

        String baseName;
        String trimmed = requested.trim();
        if (trimmed.isEmpty()) {
            baseName = null;
        } else {
            baseName = stripKnownPrefix(trimmed);
        }

        int level = getLevel(left);
        ItemStack output = left.copy();
        output.setCount(1);
        output.set(DataComponents.REPAIR_COST, 0);
        writeReinforcedMetadata(output, level, baseName);
        applyDisplayName(output, baseName);
        applyLevelLore(output, level);

        event.setOutput(output);
        event.setCost(1);
        event.setMaterialCost(0);
    }

    public static boolean isReinforced(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof PickaxeItem)) {
            return false;
        }
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.copyTag().getBoolean(MARKER);
    }

    public static boolean isEligibleBasePickaxe(ItemStack stack) {
        return stack != null
                && !stack.isEmpty()
                && stack.getItem() instanceof PickaxeItem
                && stack.getMaxDamage() > 0
                && !stack.is(REINFORCEMENT_BLACKLIST);
    }

    public static boolean isBlacklisted(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(REINFORCEMENT_BLACKLIST);
    }

    public static int getLevel(ItemStack stack) {
        if (!isReinforced(stack)) {
            return 1;
        }

        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            CompoundTag tag = data.copyTag();
            if (tag.contains(UNITS)) {
                int units = tag.getInt(UNITS);
                if (units > 0) {
                    return clampLevel(units);
                }
            }
            if (tag.contains(LEGACY_FUSIONS)) {
                int legacyFusions = tag.getInt(LEGACY_FUSIONS);
                if (legacyFusions >= 1) {
                    return clampLevel(legacyFusions + 1);
                }
            }
        }

        int baseMax = baseMaxDamage(stack);
        if (baseMax > 0 && stack.getMaxDamage() >= baseMax * 2L) {
            return clampLevel(Math.max(2, Math.round((float) stack.getMaxDamage() / (float) baseMax)));
        }
        return 2;
    }

    public static ItemStack createPickaxeAtLevel(ItemStack baseStack, int level) {
        if (baseStack == null || baseStack.isEmpty() || level < 1 || level > MAX_LEVEL) {
            return ItemStack.EMPTY;
        }

        ItemStack base = baseStack.getItem().getDefaultInstance();
        if (!isEligibleBasePickaxe(base)) {
            return ItemStack.EMPTY;
        }
        base.setCount(1);
        base.set(DataComponents.DAMAGE, 0);
        base.set(DataComponents.REPAIR_COST, 0);

        if (level == 1) {
            return base;
        }

        return buildReinforcedPickaxe(base, level, null);
    }

    private static ItemStack buildReinforcedPickaxe(ItemStack template, int level, String baseName) {
        if (!isEligibleBasePickaxe(template) || level < 2 || level > MAX_LEVEL) {
            return ItemStack.EMPTY;
        }

        int maxDamage = maxDamageForLevel(template, level);
        if (maxDamage <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack output = template.copy();
        output.setCount(1);
        output.set(DataComponents.MAX_DAMAGE, maxDamage);
        output.set(DataComponents.DAMAGE, 0);
        output.set(DataComponents.REPAIR_COST, 0);
        writeReinforcedMetadata(output, level, baseName);
        applyDisplayName(output, baseName);
        applyLevelLore(output, level);
        return output;
    }

    private static boolean normalizeLegacyStack(ItemStack stack) {
        if (!isReinforced(stack)) {
            return false;
        }

        int level = getLevel(stack);
        int expectedMaxDamage = maxDamageForLevel(stack, level);
        if (expectedMaxDamage <= 0) {
            return false;
        }

        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = data == null ? new CompoundTag() : data.copyTag();
        boolean metadataStale = !tag.contains(UNITS)
                || tag.getInt(UNITS) != level
                || !tag.contains(LEGACY_FUSIONS)
                || tag.getInt(LEGACY_FUSIONS) != Math.max(1, level - 1);

        ItemLore lore = stack.get(DataComponents.LORE);
        boolean loreStale = lore == null
                || lore.lines().stream().noneMatch(ReinforcedPickaxeEvents::isCurrentLevelLore)
                || lore.lines().stream().anyMatch(ReinforcedPickaxeEvents::isLegacyFusionLore);

        boolean durabilityStale = stack.getMaxDamage() != expectedMaxDamage;
        if (!metadataStale && !loreStale && !durabilityStale) {
            return false;
        }

        int oldDamage = stack.getDamageValue();
        stack.set(DataComponents.MAX_DAMAGE, expectedMaxDamage);
        stack.set(DataComponents.DAMAGE, Math.min(oldDamage, Math.max(0, expectedMaxDamage - 1)));
        writeReinforcedMetadata(stack, level, getStoredBaseName(stack));
        applyDisplayName(stack, getStoredBaseName(stack));
        applyLevelLore(stack, level);
        return true;
    }

    private static int clampLevel(int level) {
        return Math.max(1, Math.min(MAX_LEVEL, level));
    }

    private static int baseMaxDamage(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        return stack.getItem().getDefaultInstance().getMaxDamage();
    }

    private static int maxDamageForLevel(ItemStack stack, int level) {
        int baseMax = baseMaxDamage(stack);
        if (baseMax <= 0) {
            return -1;
        }
        long maxDamage = (long) baseMax * (long) level;
        if (maxDamage <= 0L || maxDamage > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) maxDamage;
    }

    private static String getStoredBaseName(ItemStack stack) {
        if (!isReinforced(stack)) {
            return null;
        }
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return null;
        }
        CompoundTag tag = data.copyTag();
        if (!tag.contains(BASE_NAME)) {
            return null;
        }
        String name = tag.getString(BASE_NAME);
        return name.isBlank() ? null : name;
    }

    private static void writeReinforcedMetadata(ItemStack stack, int level, String baseName) {
        int normalizedLevel = Math.max(2, Math.min(MAX_LEVEL, level));
        CustomData previous = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = previous == null ? new CompoundTag() : previous.copyTag();
        tag.putBoolean(MARKER, true);
        tag.putInt(UNITS, normalizedLevel);

        tag.putInt(LEGACY_FUSIONS, normalizedLevel - 1);

        if (baseName == null || baseName.isBlank()) {
            tag.remove(BASE_NAME);
        } else {
            tag.putString(BASE_NAME, baseName);
        }
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static void applyDisplayName(ItemStack stack, String baseName) {
        Component inner = baseName == null || baseName.isBlank()
                ? stack.getItem().getName(stack)
                : Component.literal(baseName);
        stack.set(DataComponents.CUSTOM_NAME,
                Component.translatable("item.eruruu_patch.reinforced_pickaxe_name", inner));
    }

    private static void applyLevelLore(ItemStack stack, int level) {
        stack.set(DataComponents.LORE, new ItemLore(List.of(
                Component.translatable(
                        "tooltip.eruruu_patch.reinforced_pickaxe_level",
                        clampLevel(level),
                        MAX_LEVEL
                )
        )));
    }

    private static boolean isReinforcedProgressLore(Component component) {
        return isLegacyFusionLore(component) || isCurrentLevelLore(component);
    }

    private static boolean isLegacyFusionLore(Component component) {
        return component.getContents() instanceof TranslatableContents translatable
                && "tooltip.eruruu_patch.reinforced_pickaxe_fusions".equals(translatable.getKey());
    }

    private static boolean isCurrentLevelLore(Component component) {
        return component.getContents() instanceof TranslatableContents translatable
                && "tooltip.eruruu_patch.reinforced_pickaxe_level".equals(translatable.getKey());
    }

    private static String stripKnownPrefix(String name) {

        if (name.regionMatches(true, 0, "THE ", 0, 4)) {
            return name.substring(4).trim();
        }
        if (name.regionMatches(true, 0, "LA ", 0, 3)) {
            return name.substring(3).trim();
        }
        return name;
    }
}
