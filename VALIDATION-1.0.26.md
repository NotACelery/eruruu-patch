# Eruruu Patch 1.0.26 — Final sandbox validation

This checklist contains only behavior that still needs an explicit final confirmation after the late viewer/Jade hotfixes. Everything else listed under **Closed / already validated** should not be repeated unless a regression is observed.

## 1. Rich Farmer / Rich Paddy Jade Knife — NEW
- [ ] Rich Farmer with no Knife shows no Knife line.
- [ ] Rich Farmer with a Knife shows `Knife/Cuchillo: <real knife name>`.
- [ ] Rich Paddy Farmer behaves the same.
- [ ] A modded Knife using `#c:tools/knife` shows its own name correctly.
- [ ] Removing the Knife removes the Jade line.
- [ ] Normal Farmer and normal Paddy Farmer never show the Knife line.
- [ ] Existing crop, growth, Rich Soil and harvested-output rows remain unchanged.

## 2. Cutter viewer regression — FINAL CONFIRMATION
These paths were touched repeatedly during the 1.0.25 JEI rollback/recovery and still need one explicit post-hotfix confirmation.

- [ ] Searching `Cutter` in JEI shows the Cutter.
- [ ] Its single real crafting recipe `GGG / GCG / BLB` appears.
- [ ] The bottom-center material cycles/accepts the supported logs plus Bamboo Block.
- [ ] `Cutter Axe Actions` still appears.
- [ ] Cutter is reachable as a Farmer's Delight Cutting catalyst/workstation in JEI.
- [ ] Cutter is reachable as a Farmer's Delight Cutting workstation in EMI.
- [ ] No `Cutter Variants` viewer category remains.
- [ ] No duplicate/ghost Cutter crafting recipes appear.

## 3. Cutter Jade output — ALREADY REPORTED WORKING
- [x] Variant is shown.
- [x] Equipped Knife/Axe is shown.
- [x] Finished outputs are shown as Rich-Farmer-style icon + amount + item-name rows.
- [x] Inputs are not shown as products.

## 4. Closed / already validated — do not repeat
- [x] THE Pick core, Auto Mining, enchantments, corrected level/fusion math, `/eruruu givepick`, legacy level migration and GUI-safe HUD.
- [x] Moss Helmet Stone/Cobblestone/Moss spread and Moss + Hoe -> Dirt.
- [x] Wild Crops and Sweet Berry Bush generation from Bone Meal.
- [x] Rich Farmer/Rich Paddy Knife equipment by right-click, persistence/sync, no harvesting durability, byproducts/Rice behavior and Mushroom Colony Knife gating.
- [x] Farmer/Paddy creative Pick Block clone safety.
- [x] Farmer/Paddy Recipe Book discovery and data-preserving upgrade crafting.
- [x] Knife Harvesting viewer category.
- [x] Cutter base processing with Knife/Axe, Cutting Board recipes, Axe fallback, durability, Fortune, output safety and one-process-at-a-time behavior.
- [x] Cutter Villager storage/extraction, baby incubation, save/reload/relocation behavior and sided hopper automation.
- [x] Cutter world renderer, rotations, Cutting Board work item rendering and GUI layout.
- [x] Cutter log variants + Bamboo, same-variant empty stacking, non-stackability with contents and Pick Block variant preservation.
- [x] Cutter Jade generated-output display.
- [x] Complete temporary Stonecutter Sifting additions: Sand Sniffer Egg + Prismarine/Prismarine Bricks/Dark Prismarine recovery/ocean tables.

## 5. Not implementation work anymore — migration
After sections 1 and 2 pass, the sandbox is frozen. Remaining work is migration, not another Eruruu gameplay feature:

- Stonecutter additions -> Stonecutter Sifting 1.1.1.
- Rich Farmer/Rich Paddy Knife system -> Easy Farmer's Delight Compat.
- Cutter + variants + automation + Recipe Book/JEI/EMI/Jade -> Easy Farmer's Delight Compat.
- Build/test destination mods.
- Remove temporary integration code from Eruruu only after destination builds are validated.
