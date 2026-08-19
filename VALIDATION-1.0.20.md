# Eruruu Patch 1.0.20 — Remaining runtime validation

This checklist intentionally excludes behavior already reported as working in NeoBlock. Re-test an excluded area only if a later patch changes its code.

## Already validated — do not repeat unless changed

- THE Pick core behavior / Auto Mining is working correctly in normal gameplay.
- Rich Farmer and Rich Paddy Farmer perform their intended farming behavior.
- Mature Mushroom Colonies correctly stop harvesting when no Knife is equipped.
- Rice correctly uses an equipped Knife and returns its real Farmer's Delight products/byproducts.
- Crops without Knife-specific loot do not receive artificial byproducts.
- Moss Helmet item texture now renders correctly.
- Rich Farmer empty Knife outline is visually correct.
- Cutter 1.0.19 core processing works correctly.
- Temporary Stonecutter Sifting laboratory additions are functional in-game: Sand Sniffer Egg bonus and all Prismarine-family sifting additions.

---

# Still worth validating

## 1. THE Pick — 1.0.16 admin / compatibility edges

The ordinary THE Pick gameplay itself is already considered good. Only these newer administration/compatibility paths have not been explicitly confirmed:

- [ ] `/eruruu givepick minecraft:diamond_pickaxe 10` creates the expected level-10 THE Pick.
- [ ] Level 1 command returns the intended ordinary base pick behavior.
- [ ] Level 30 works; level 31+ is rejected.
- [ ] Optional player target gives the tool to the requested player.
- [ ] A conventional modded `PickaxeItem` is accepted.
- [ ] A non-pickaxe item is rejected.
- [ ] A blacklisted special/giant pickaxe is rejected.
- [ ] Existing pre-1.0.16 THE Picks keep the correct real level after migration and no longer show inflated legacy fusion lore.

## 2. Moss Helmet mechanics

The texture is already confirmed fixed. The mechanics themselves have not been explicitly closed in the current audit:

- [ ] Moss Helmet + Bone Meal on Cobblestone -> Moss.
- [ ] Moss conversion also performs the intended vanilla-style Moss spread.
- [ ] Without Moss Helmet, Cobblestone receives no special interaction.
- [ ] Moss Helmet + Hoe on Moss -> Dirt.
- [ ] Hoe loses exactly 1 durability in Survival.
- [ ] Without Moss Helmet, Hoe on Moss does not perform the special conversion.

## 3. Wild vegetation after Bone Meal

- [ ] Wild Cabbage / Onion / Tomato generation still works after the 1.0.15 reconstruction.
- [ ] Sweet Berry Bush can appear from newly-created Short Grass.
- [ ] Berry processing does not reduce/replace the Wild Crop roll (Wild Crop pass first, Berry pass second).
- [ ] Existing Short Grass outside the current Bone Meal growth is not converted unexpectedly.

## 4. Rich Farmer / Rich Paddy — persistence and inventory edges

Core harvesting is already validated; these are durability/networking/container edge cases:

- [ ] Equipped Knife survives save + world/server restart.
- [ ] Equipped Knife survives chunk unload/reload.
- [ ] Knife remains synchronized between client and server when inserted/removed.
- [ ] Shift-click inserts a Knife correctly and does not place invalid items into the equipment slot.
- [ ] Existing hopper/pipe access cannot extract the Rich Farmer/Rich Paddy Knife equipment.
- [ ] Output automation remains unchanged while a Knife is equipped.

## 5. Cutter 1.0.20 — world visual polish

These are new in 1.0.20 and must be checked visually:

- [ ] Oak Log now occupies the same workstation/crop anchor used by an Easy Villagers Farmer instead of the old corner position.
- [ ] Cutting Board sits directly on top of the Log with no visible gap and does not intersect the Villager/glass.
- [ ] Layout rotates correctly for NORTH / SOUTH / EAST / WEST Cutter facing.
- [ ] Current input appears physically on the Cutting Board.
- [ ] Ordinary food/items lie flat similarly to Farmer's Delight's real Cutting Board.
- [ ] 3D block inputs display upright similarly to Farmer's Delight's real Cutting Board.
- [ ] The displayed item changes/disappears correctly as inputs/tool/villager state changes.
- [ ] The display is visual only: no ghost duplication or extra real item is created.

## 6. Cutter 1.0.20 — GUI polish

- [ ] Four inputs are in the top Breeder-style row.
- [ ] Four outputs are directly below them.
- [ ] Knife/Axe slot is on the right at the same height as the input row.
- [ ] Hybrid Knife/Axe outline still looks correct while empty and disappears under a real tool.
- [ ] Player inventory aligns with the Easy Villagers Input/Output background.
- [ ] Progress indicator remains visible and does not overlap slots/text.
- [ ] GUI scale Auto / 1 / 2 / 3 does not introduce overlap or clipping.
- [ ] Shift-clicking an extra Knife/Axe while a tool is already equipped leaves it with the player rather than putting it into an input slot.

## 7. Cutter 1.0.20 — hopper / pipe automation

### Hopper / pipe above

- [ ] Knife automatically enters the tool slot if empty.
- [ ] Axe automatically enters the tool slot if empty.
- [ ] Normal materials skip the tool slot and fill input slots.
- [ ] A mixed hopper containing tools + materials routes both correctly.
- [ ] If a tool is already equipped, additional Knife/Axe items remain in the hopper and do not clog material inputs.
- [ ] No automation from above can extract the equipped tool.

### Horizontal sides

- [ ] Materials can be inserted into inputs.
- [ ] Knife/Axe is rejected from material inputs.
- [ ] No output can be extracted horizontally unless intentionally changed later.
- [ ] No tool can be extracted horizontally.

### Bottom

- [ ] Outputs can be extracted.
- [ ] Inputs cannot be extracted from below.
- [ ] Equipped tool cannot be extracted from below.
- [ ] Items cannot be inserted into outputs from below.

### Modded transport

- [ ] At least one non-vanilla pipe/item transport system respects the same sided rules.

## 8. Cutter — Cutting Board recipe coverage

The core Cutter is already reported working, but a representative coverage pass is still useful:

- [ ] Raw meat Knife recipe.
- [ ] Vegetable Knife recipe.
- [ ] Rice / Farmer's Delight ingredient recipe where applicable.
- [ ] Dough/pasta or another non-block food recipe.
- [ ] Recipe with multiple outputs/byproducts.
- [ ] A valid Axe-based Farmer's Delight Cutting Board recipe takes priority over vanilla Axe fallback.
- [ ] A datapack/addon Cutting Board recipe works if one exists in the pack.

## 9. Cutter — Axe fallback coverage

- [ ] Vanilla log -> stripped log.
- [ ] Wood -> stripped wood.
- [ ] Nether stem -> stripped stem.
- [ ] Hyphae -> stripped hyphae.
- [ ] Bamboo Block -> stripped Bamboo Block or Farmer's Delight recipe result when a cutting recipe has priority.
- [ ] Copper scraping moves exactly one oxidation stage per operation.
- [ ] Waxed Copper -> corresponding unwaxed block.
- [ ] Unwaxing does **not** return Honeycomb.

## 10. Cutter — tool durability

- [ ] Exactly one durability attempt per successful operation.
- [ ] No durability loss when no valid recipe/action exists.
- [ ] No durability loss when outputs are full and processing is blocked.
- [ ] Unbreaking affects durability normally.
- [ ] Tool breaks normally at zero durability.
- [ ] Cutter stops processing when the tool breaks and waits for another valid tool.

## 11. Cutter — output safety

- [ ] One-output recipe with room works.
- [ ] Multi-output recipe with room works.
- [ ] Partially-filled compatible output stacks merge correctly.
- [ ] Completely full outputs stop processing.
- [ ] A recipe whose *first* result fits but a later byproduct does not fit consumes nothing.
- [ ] Blocked processing consumes no input.
- [ ] Blocked processing damages no tool.
- [ ] No byproduct is thrown away or dropped into the world due to insufficient output space.

## 12. Cutter — serial processing / four inputs

- [ ] Only one operation occurs per processing interval; four populated inputs do not run four recipes simultaneously.
- [ ] Multiple valid input slots are eventually processed in the intended slot order.
- [ ] An invalid input does not destroy/block valid inputs incorrectly.
- [ ] Switching Knife <-> Axe changes which inputs can process without corrupting progress/items.
- [ ] Removing the tool pauses work safely.

## 13. Cutter — Villager lifecycle

- [ ] No Villager -> no processing.
- [ ] Adult Villager -> processing works.
- [ ] Baby Villager can be stored but cannot work while baby.
- [ ] Stored baby ages and begins working when adult.
- [ ] Villager can be removed without losing its data.
- [ ] Villager can be reinserted without duplication.

## 14. Cutter — persistence / relocation

- [ ] Save + reload preserves Villager, tool, 4 inputs, 4 outputs and progress.
- [ ] Chunk unload/reload preserves all contents.
- [ ] Full server restart preserves all contents.
- [ ] Breaking and replacing the Cutter preserves its stored contents exactly once.
- [ ] No item/Villager duplication when rapidly breaking/replacing.

## 15. Cutter — multiplayer / synchronization

- [ ] Two players can observe the same Cutter without desync.
- [ ] GUI contents update correctly after hopper insertion/extraction.
- [ ] World-rendered input updates after hopper/GUI changes.
- [ ] Tool break/removal updates client visuals promptly.
- [ ] Dedicated server does not produce client-only classloading or renderer errors.

---

# Not pending for Eruruu runtime testing

These are migration tasks, not additional sandbox gameplay tests:

- Move the validated sifting additions into Stonecutter Sifting and remove Eruruu's temporary mixin afterward.
- Move Rich Farmer / Rich Paddy Knife support, shared resolvers and Cutter into Easy Farmer's Delight Compat after the sandbox implementation is accepted.
- Remove the corresponding temporary Eruruu integration code/dependencies only after each destination mod is independently built and validated.
