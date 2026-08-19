package dev.maicra.eruruupatch.mixin;

import dev.maicra.stonecuttersifting.sifting.SiftingOutput;
import dev.maicra.stonecuttersifting.sifting.SiftingTable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mixin(targets = "dev.maicra.stonecuttersifting.sifting.SiftingTables", remap = false)
public abstract class StonecutterSiftingTablesMixin {
    private static final float SNIFFER_EGG = 0.0025F;

    @Shadow
    @Final
    @Mutable
    public static List<SiftingTable> TABLES;

    @Shadow
    @Final
    @Mutable
    private static Map<Item, SiftingTable> BY_INPUT;

    @Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void eruruu$extendSiftingTables(CallbackInfo ci) {
        List<SiftingTable> extended = new ArrayList<>(TABLES.size() + 3);
        for (SiftingTable table : TABLES) {
            extended.add(table.input() == Items.SAND ? eruruu$withSnifferEgg(table) : table);
        }
        extended.add(eruruu$prismarine());
        extended.add(eruruu$prismarineBricks());
        extended.add(eruruu$darkPrismarine());
        TABLES = List.copyOf(extended);

        Map<Item, SiftingTable> byInput = new HashMap<>();
        for (SiftingTable table : TABLES) {
            byInput.put(table.input(), table);
        }
        BY_INPUT = Map.copyOf(byInput);
    }

    private static SiftingTable eruruu$withSnifferEgg(SiftingTable original) {
        List<SiftingOutput> outputs = new ArrayList<>(original.outputs());
        outputs.add(eruruu$output(Items.SNIFFER_EGG, "eruruu_patch.sifting.chance", "0.25%"));
        return new SiftingTable(
                original.id(),
                original.input(),
                List.copyOf(outputs),
                original.description(),
                random -> {
                    List<ItemStack> results = new ArrayList<>(original.roll(random));
                    if (random.nextFloat() < SNIFFER_EGG) {
                        results.add(new ItemStack(Items.SNIFFER_EGG));
                    }
                    return eruruu$merge(results);
                }
        );
    }

    private static SiftingTable eruruu$prismarine() {
        List<SiftingOutput> outputs = List.of(
                eruruu$output(new ItemStack(Items.PRISMARINE_SHARD, 2), "eruruu_patch.sifting.guaranteed"),
                eruruu$output(new ItemStack(Items.PRISMARINE_SHARD, 2), "eruruu_patch.sifting.chance", "25%"),
                eruruu$output(Items.PRISMARINE_CRYSTALS, "eruruu_patch.sifting.chance", "8%"),
                eruruu$output(Items.WET_SPONGE, "eruruu_patch.sifting.chance", "1%"),
                eruruu$output(Items.HEART_OF_THE_SEA, "eruruu_patch.sifting.chance", "0.5%")
        );
        return eruruu$table("prismarine", Items.PRISMARINE, outputs, random -> {
            List<ItemStack> results = new ArrayList<>();
            results.add(new ItemStack(Items.PRISMARINE_SHARD, 2));
            if (random.nextFloat() < 0.25F) results.add(new ItemStack(Items.PRISMARINE_SHARD, 2));
            if (random.nextFloat() < 0.08F) results.add(new ItemStack(Items.PRISMARINE_CRYSTALS));
            if (random.nextFloat() < 0.01F) results.add(new ItemStack(Items.WET_SPONGE));
            if (random.nextFloat() < 0.005F) results.add(new ItemStack(Items.HEART_OF_THE_SEA));
            return eruruu$merge(results);
        });
    }

    private static SiftingTable eruruu$prismarineBricks() {
        List<SiftingOutput> outputs = List.of(
                eruruu$output(new ItemStack(Items.PRISMARINE_SHARD, 4), "eruruu_patch.sifting.guaranteed"),
                eruruu$output(new ItemStack(Items.PRISMARINE_SHARD, 2), "eruruu_patch.sifting.chance", "35%"),
                eruruu$output(new ItemStack(Items.PRISMARINE_SHARD, 3), "eruruu_patch.sifting.chance", "10%"),
                eruruu$output(Items.PRISMARINE_CRYSTALS, "eruruu_patch.sifting.chance", "12%"),
                eruruu$output(Items.WET_SPONGE, "eruruu_patch.sifting.chance", "1.5%"),
                eruruu$output(Items.HEART_OF_THE_SEA, "eruruu_patch.sifting.chance", "0.75%"),
                eruruu$output(Items.TUBE_CORAL, "eruruu_patch.sifting.random_coral", "3%")
        );
        return eruruu$table("prismarine_bricks", Items.PRISMARINE_BRICKS, outputs, random -> {
            List<ItemStack> results = new ArrayList<>();
            results.add(new ItemStack(Items.PRISMARINE_SHARD, 4));
            if (random.nextFloat() < 0.35F) results.add(new ItemStack(Items.PRISMARINE_SHARD, 2));
            if (random.nextFloat() < 0.10F) results.add(new ItemStack(Items.PRISMARINE_SHARD, 3));
            if (random.nextFloat() < 0.12F) results.add(new ItemStack(Items.PRISMARINE_CRYSTALS));
            if (random.nextFloat() < 0.015F) results.add(new ItemStack(Items.WET_SPONGE));
            if (random.nextFloat() < 0.0075F) results.add(new ItemStack(Items.HEART_OF_THE_SEA));
            if (random.nextFloat() < 0.03F) results.add(eruruu$randomCoral(random));
            return eruruu$merge(results);
        });
    }

    private static SiftingTable eruruu$darkPrismarine() {
        List<SiftingOutput> outputs = List.of(
                eruruu$output(new ItemStack(Items.PRISMARINE_SHARD, 4), "eruruu_patch.sifting.guaranteed"),
                eruruu$output(new ItemStack(Items.PRISMARINE_SHARD, 2), "eruruu_patch.sifting.chance", "35%"),
                eruruu$output(new ItemStack(Items.PRISMARINE_SHARD, 2), "eruruu_patch.sifting.chance", "10%"),
                eruruu$output(Items.INK_SAC, "eruruu_patch.sifting.chance", "20%"),
                eruruu$output(Items.PRISMARINE_CRYSTALS, "eruruu_patch.sifting.chance", "15%"),
                eruruu$output(Items.WET_SPONGE, "eruruu_patch.sifting.chance", "2%"),
                eruruu$output(Items.HEART_OF_THE_SEA, "eruruu_patch.sifting.chance", "1%"),
                eruruu$output(Items.TUBE_CORAL, "eruruu_patch.sifting.random_coral", "4%")
        );
        return eruruu$table("dark_prismarine", Items.DARK_PRISMARINE, outputs, random -> {
            List<ItemStack> results = new ArrayList<>();
            results.add(new ItemStack(Items.PRISMARINE_SHARD, 4));
            if (random.nextFloat() < 0.35F) results.add(new ItemStack(Items.PRISMARINE_SHARD, 2));
            if (random.nextFloat() < 0.10F) results.add(new ItemStack(Items.PRISMARINE_SHARD, 2));
            if (random.nextFloat() < 0.20F) results.add(new ItemStack(Items.INK_SAC));
            if (random.nextFloat() < 0.15F) results.add(new ItemStack(Items.PRISMARINE_CRYSTALS));
            if (random.nextFloat() < 0.02F) results.add(new ItemStack(Items.WET_SPONGE));
            if (random.nextFloat() < 0.01F) results.add(new ItemStack(Items.HEART_OF_THE_SEA));
            if (random.nextFloat() < 0.04F) results.add(eruruu$randomCoral(random));
            return eruruu$merge(results);
        });
    }

    private static SiftingTable eruruu$table(String id, Item input, List<SiftingOutput> outputs,
                                              Function<RandomSource, List<ItemStack>> roller) {
        return new SiftingTable(
                ResourceLocation.fromNamespaceAndPath("eruruu_patch", id),
                input,
                outputs,
                Component.translatable("eruruu_patch.sifting." + id),
                roller
        );
    }

    private static SiftingOutput eruruu$output(Item item, String noteKey, Object... args) {
        return eruruu$output(new ItemStack(item), noteKey, args);
    }

    private static SiftingOutput eruruu$output(ItemStack stack, String noteKey, Object... args) {
        ItemStack display = stack.copy();
        return new SiftingOutput(display::copy, Component.translatable(noteKey, args));
    }

    private static ItemStack eruruu$randomCoral(RandomSource random) {
        return switch (random.nextInt(5)) {
            case 0 -> new ItemStack(Items.TUBE_CORAL);
            case 1 -> new ItemStack(Items.BRAIN_CORAL);
            case 2 -> new ItemStack(Items.BUBBLE_CORAL);
            case 3 -> new ItemStack(Items.FIRE_CORAL);
            default -> new ItemStack(Items.HORN_CORAL);
        };
    }

    private static List<ItemStack> eruruu$merge(List<ItemStack> stacks) {
        Map<Item, Integer> counts = new HashMap<>();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                counts.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }
        List<ItemStack> merged = new ArrayList<>();
        counts.forEach((item, count) -> merged.add(new ItemStack(item, count)));
        return merged;
    }
}
