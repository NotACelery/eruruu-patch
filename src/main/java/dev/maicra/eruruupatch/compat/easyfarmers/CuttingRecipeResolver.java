package dev.maicra.eruruupatch.compat.easyfarmers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

/**
 * Reflection-safe bridge to Farmer's Delight Cutting Board recipes.
 *
 * Eruruu intentionally does not compile against Farmer's Delight implementation
 * classes. This bridge discovers the runtime `farmersdelight:cutting` recipe type,
 * creates its two-stack recipe input and asks the recipe itself to roll outputs.
 * Datapack/addon cutting recipes therefore remain authoritative.
 */
public final class CuttingRecipeResolver {
    private static final ResourceLocation CUTTING_TYPE = ResourceLocation.fromNamespaceAndPath(
            "farmersdelight", "cutting"
    );
    private static final String INPUT_CLASS = "vectorwing.farmersdelight.common.crafting.CuttingBoardRecipeInput";

    public record Result(ResourceLocation recipeId, List<ItemStack> outputs, Optional<SoundEvent> sound) {
        public Result {
            outputs = List.copyOf(outputs);
            sound = sound == null ? Optional.empty() : sound;
        }
    }

    private CuttingRecipeResolver() {
    }

    public static Optional<Result> resolve(Level level, ItemStack input, ItemStack tool, int fortuneLevel) {
        if (level == null || input == null || input.isEmpty() || tool == null || tool.isEmpty()) {
            return Optional.empty();
        }

        try {
            Class<?> inputClass = Class.forName(INPUT_CLASS);
            Constructor<?> constructor = inputClass.getConstructor(ItemStack.class, ItemStack.class);
            Object recipeInput = constructor.newInstance(input.copyWithCount(1), tool.copyWithCount(1));

            for (RecipeHolder<?> holder : level.getRecipeManager().getRecipes()) {
                Recipe<?> recipe = holder.value();
                ResourceLocation typeId = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
                if (!CUTTING_TYPE.equals(typeId)) {
                    continue;
                }

                Method matches = findMethod(recipe.getClass(), "matches", 2);
                if (matches == null || !Boolean.TRUE.equals(matches.invoke(recipe, recipeInput, level))) {
                    continue;
                }

                Method rollResults = findMethod(recipe.getClass(), "rollResults", 3);
                if (rollResults == null) {
                    return Optional.empty();
                }

                Object raw = rollResults.invoke(recipe, level.random, Math.max(0, fortuneLevel), null);
                if (!(raw instanceof List<?> rawList)) {
                    return Optional.empty();
                }

                List<ItemStack> outputs = new ArrayList<>();
                for (Object value : rawList) {
                    if (value instanceof ItemStack stack && !stack.isEmpty()) {
                        outputs.add(stack.copy());
                    }
                }
                Optional<SoundEvent> sound = Optional.empty();
                Method getSoundEvent = findMethod(recipe.getClass(), "getSoundEvent", 0);
                if (getSoundEvent != null) {
                    Object rawSound = getSoundEvent.invoke(recipe);
                    if (rawSound instanceof Optional<?> optional && optional.orElse(null) instanceof SoundEvent event) {
                        sound = Optional.of(event);
                    }
                }
                return Optional.of(new Result(holder.id(), outputs, sound));
            }
        } catch (ReflectiveOperationException | LinkageError | RuntimeException ignored) {
            // Farmer's Delight absent/incompatible: sandbox caller simply has no cutting recipe.
        }
        return Optional.empty();
    }

    public static Optional<Result> resolve(Level level, ItemStack input, ItemStack tool) {
        return resolve(level, input, tool, 0);
    }

    private static Method findMethod(Class<?> type, String name, int parameterCount) {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == parameterCount) {
                return method;
            }
        }
        return null;
    }
}
