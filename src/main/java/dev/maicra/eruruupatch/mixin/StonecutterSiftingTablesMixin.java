package dev.maicra.eruruupatch.mixin;

import dev.maicra.eruruupatch.ModItems;
import dev.maicra.stonecuttersifting.sifting.SiftingOutput;
import dev.maicra.stonecuttersifting.sifting.SiftingTable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

/**
 * Eruruu-only Stonecutter Sifting extension.
 *
 * Stonecutter Sifting 1.1.0 owns the generic Sand/Prismarine tables natively.
 * Eruruu keeps only its custom Soul Sand culture replacement, so the cultures
 * exist exclusively when this patch and Stonecutter Sifting are installed together.
 */
@Mixin(targets = "dev.maicra.stonecuttersifting.sifting.SiftingTables", remap = false)
public abstract class StonecutterSiftingTablesMixin {
    @Shadow @Final @Mutable public static List<SiftingTable> TABLES;
    @Shadow @Final @Mutable private static Map<Item, SiftingTable> BY_INPUT;

    @Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void eruruu$applyCultureOutputs(CallbackInfo ci) {
        List<SiftingTable> patched = new ArrayList<>(TABLES.size());
        for (SiftingTable table : TABLES) {
            patched.add(table.input() == Items.SOUL_SAND ? eruruu$withCultures(table) : table);
        }
        TABLES = List.copyOf(patched);

        Map<Item, SiftingTable> byInput = new HashMap<>();
        for (SiftingTable table : TABLES) {
            byInput.put(table.input(), table);
        }
        BY_INPUT = Map.copyOf(byInput);
    }

    private static SiftingTable eruruu$withCultures(SiftingTable original) {
        List<SiftingOutput> outputs = original.outputs().stream()
                .map(StonecutterSiftingTablesMixin::eruruu$replaceDisplayOutput)
                .toList();

        return new SiftingTable(
                original.id(),
                original.input(),
                outputs,
                original.description(),
                random -> original.roll(random).stream()
                        .map(StonecutterSiftingTablesMixin::eruruu$replaceRolledOutput)
                        .toList()
        );
    }

    private static SiftingOutput eruruu$replaceDisplayOutput(SiftingOutput output) {
        ItemStack display = output.stack();
        if (display.is(Items.CRIMSON_ROOTS)) {
            return new SiftingOutput(
                    () -> new ItemStack(ModItems.CRIMSON_CULTURE.get(), display.getCount()),
                    output.note()
            );
        }
        if (display.is(Items.WARPED_ROOTS)) {
            return new SiftingOutput(
                    () -> new ItemStack(ModItems.WARPED_CULTURE.get(), display.getCount()),
                    output.note()
            );
        }
        return output;
    }

    private static ItemStack eruruu$replaceRolledOutput(ItemStack stack) {
        if (stack.is(Items.CRIMSON_ROOTS)) {
            return new ItemStack(ModItems.CRIMSON_CULTURE.get(), stack.getCount());
        }
        if (stack.is(Items.WARPED_ROOTS)) {
            return new ItemStack(ModItems.WARPED_CULTURE.get(), stack.getCount());
        }
        return stack;
    }
}
