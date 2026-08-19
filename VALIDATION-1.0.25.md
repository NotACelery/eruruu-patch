# Eruruu Patch 1.0.25 — focused validation

Everything previously confirmed by the 1.0.24 audit remains closed unless this patch touches it. This checklist contains only new Cutter variant/Jade behavior.

## 1. Bamboo Cutter
- [ ] `minecraft:bamboo_block` is accepted in the Cutter recipe bottom-center slot.
- [ ] Recipe Book/JEI/EMI still show **one** Cutter recipe, not nine separate recipes.
- [ ] Crafting with Bamboo produces a Bamboo Cutter.
- [ ] Placed Bamboo Cutter renders Bamboo Block below the Cutting Board.
- [ ] Bamboo Cutter processes exactly like every other Cutter.

## 2. Variant tooltip
- [ ] Oak item says `Variant/Variante: Oak/Roble`.
- [ ] Spruce/Birch/Jungle/Acacia/Dark Oak/Mangrove/Cherry show the correct localized variant.
- [ ] Bamboo shows `Bamboo/Bambú`.
- [ ] Tooltip remains correct after placing, using, emptying, breaking and picking the Cutter back up.

## 3. Variant item icon
- [ ] Cutter item icon still clearly reads as the Cutter enclosure rather than only a log.
- [ ] The stored material is visibly present in the item icon.
- [ ] Oak/Spruce/Cherry/Bamboo are visually distinguishable at inventory scale.
- [ ] Icons render correctly in inventory, hotbar, dropped item, JEI/EMI and hand.

## 4. Stacking identity
- [ ] Empty Oak + Oak stacks.
- [ ] Empty Spruce + Spruce stacks.
- [ ] Empty Bamboo + Bamboo stacks.
- [ ] Different variants never stack.
- [ ] A used Cutter that is completely emptied stacks again with empty Cutters of its own variant after being broken.
- [ ] A Cutter carrying Villager/tool/input/output/progress remains non-stackable.

## 5. Jade
- [ ] Game loads normally with Jade installed.
- [ ] Game/server remains valid if Jade is absent (optional integration).
- [ ] Looking at Cutter shows the correct localized variant.
- [ ] Equipped Knife/Axe is shown when present.
- [ ] Generated/stored output products are shown with correct counts.
- [ ] Multiple occupied output slots are all listed.
- [ ] Empty output inventory reports no generated products.
- [ ] Jade values update after the Cutter processes/extracts items without reopening the world.

## 6. Regression smoke test
- [ ] Cutter recipes/processing still work with Knife and Axe.
- [ ] Hopper top/tool/input and bottom/output behavior remains unchanged.
- [ ] Villager/baby incubation behavior remains unchanged.
- [ ] Rich Farmer/Rich Paddy, THE Pick, Moss Helmet, vegetation and Stonecutter Sifting remain untouched by 1.0.25.
