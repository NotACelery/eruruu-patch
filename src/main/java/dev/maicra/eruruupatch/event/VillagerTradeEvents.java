package dev.maicra.eruruupatch.event;

import dev.maicra.eruruupatch.ModItems;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

public final class VillagerTradeEvents {
    private static final ResourceLocation FARMER_ID = ResourceLocation.withDefaultNamespace("farmer");
    private static final int JOURNEYMAN_LEVEL = 3;

    private VillagerTradeEvents() {
    }

    public static void onVillagerTrades(VillagerTradesEvent event) {
        ResourceLocation professionId = BuiltInRegistries.VILLAGER_PROFESSION.getKey(event.getType());
        if (!FARMER_ID.equals(professionId)) {
            return;
        }

        List<VillagerTrades.ItemListing> journeymanTrades = event.getTrades().get(JOURNEYMAN_LEVEL);
        if (journeymanTrades == null) {
            return;
        }

        removeVanillaCookieTrade(journeymanTrades);
        journeymanTrades.add((trader, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 10),
                new ItemStack(ModItems.BLANK_SPAWN_EGG.get()),
                4,
                10,
                0.08F
        ));
    }

    private static void removeVanillaCookieTrade(List<VillagerTrades.ItemListing> trades) {
        Iterator<VillagerTrades.ItemListing> iterator = trades.iterator();
        while (iterator.hasNext()) {
            VillagerTrades.ItemListing listing = iterator.next();
            MerchantOffer offer;
            try {

                offer = listing.getOffer(null, RandomSource.create(0L));
            } catch (RuntimeException ignored) {
                continue;
            }

            if (isVanillaJourneymanCookieOffer(offer)) {
                iterator.remove();
                return;
            }
        }
    }

    private static boolean isVanillaJourneymanCookieOffer(MerchantOffer offer) {
        if (offer == null) {
            return false;
        }

        ItemStack cost = offer.getBaseCostA();
        ItemStack result = offer.getResult();
        return cost.is(Items.EMERALD)
                && cost.getCount() == 3
                && result.is(Items.COOKIE)
                && result.getCount() == 18
                && offer.getMaxUses() == 12
                && offer.getXp() == 10;
    }
}
