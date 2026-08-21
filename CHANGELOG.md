# Eruruu Patch 1.0.28 — final migration cleanup / project decoupling

## Argentum composting QoL
- Added Argentum planting items to NeoForge's standard Composter data map so automated farms have a vanilla-style sink for excess crop stock.
- Yerba Mate Seed, Tea Seed and Membrillo Seed compost at 30%, matching vanilla seed-tier items.
- Batata composts at 65%, matching vanilla root-crop/produce-tier items such as Potato.
- The three seed items are also marked as valid Villager compost inputs.
- The implementation is data-driven and does not mix into or modify Argentum code.

## Mushroom Bone Meal visual correction
- Restored the missing visible Bone Meal particles for valid dark/damp mushroom-generation attempts.
- The guaranteed vanilla Bone Meal effect now targets the clicked Dirt block instead of the empty mushroom spawn position; successful mushroom generation still keeps its additional effect on the spawned mushroom.

Version 1.0.28 completes the cleanup that **1.0.27 left incomplete** after the experimental Farmer/Cutter and Stonecutter Sifting additions were moved into their permanent projects.

## Easy Farmer's Delight sandbox leftovers removed
- Removed Eruruu's complete legacy Cutter implementation: block, item, BlockEntity, menu, screen, renderers, processing runtime, automation handlers and stored-villager helpers.
- Removed the remaining `compat/easyfarmers` code for Knife/Axe classification, Cutting Board recipe resolution, Axe actions, output simulation and Cutter log variants.
- Removed all Cutter Jade code, models, blockstates, item resources, tool-slot texture, tags and localization.
- Removed the `eruruu_patch:cutter` registry aliases. The old Cutter existed only in the private sandbox/test world and is no longer carried as a compatibility contract.
- Removed the historical `EruruuKnife` migration mixin. Eruruu no longer reads, writes or migrates Farmer tool data of any kind.
- Removed the required Easy Farmer's Delight Compat dependency. Eruruu 1.0.28 has no runtime calls or mixins targeting Easy FD.

## Stonecutter Sifting leftovers removed
- Removed `StonecutterSiftingTablesMixin` completely. Eruruu no longer changes Soul Sand outputs or any other Stonecutter Sifting table.
- Removed the old Stonecutter Sifting compile-only dev JAR and the required Stonecutter Sifting dependency.
- Removed leftover generic sifting localization that had already moved to Stonecutter Sifting.
- Crimson and Warped Cultures remain normal Eruruu crafting recipes using **9 vanilla Crimson Roots** or **9 vanilla Warped Roots** respectively; they do not depend on sifting.

## Final ownership rule
- Eruruu Patch now implements only its own OneBlock/QoL mechanics and recipes.
- Stonecutter Sifting and Easy Farmer's Delight Compat can coexist in the same modpack, but Eruruu neither requires nor modifies them.
- Historical changelog entries below are intentionally preserved as development history of the sandbox period.

# Eruruu Patch 1.0.27 — migration cleanup compile fix

- `build-dev.bat` now reads `mod_version` from `gradle.properties` instead of hardcoding 1.0.26.
- `build-dev.bat` automatically runs the idempotent `cleanup-migrated-features.bat` before Gradle, preventing stale migrated sources from reaching `compileJava`.
- The cleanup now verifies that `CompatFarmerBlockMixin.java` and `CutterAxeEmiRecipe.java` were actually removed and aborts the build if cleanup is incomplete.
- No gameplay behavior changes; this only makes the 1.0.27 migration/cleanup flow safe to execute in one step.

# Eruruu Patch 1.0.27 — Migration Cleanup

- Completed ownership transfer of generic Stonecutter Sifting additions and Easy Farmer's Delight Farmer/Cutter mechanics into their 1.1.0 destination mods.
- Stonecutter integration is now culture-only: Soul Sand Crimson/Warped Roots are replaced by Eruruu Crimson/Warped Cultures, while Sniffer Egg and Prismarine outputs remain owned by Stonecutter Sifting.
- Removed Eruruu Farmer Knife mixins, Farmer menus/screens, Knife harvest helpers, migrated JEI/EMI/Jade categories, and migrated display-recipe serializers from active code.
- Kept `eruruu_patch:cutter` registered as a hidden legacy machine so existing worlds do not lose already-placed sandbox Cutters; its old crafting recipe is removed.
- Added `cleanup-migrated-features.bat`, a one-time exact-path cleanup for source files/resources that a ZIP overlay cannot delete, plus stale build output and the no-longer-needed EasyFD 1.0.0 compile JAR.
- Easy Farmer's Delight Compat and Stonecutter Sifting now require 1.1.0+.

# Eruruu Patch 1.0.26 — Final Sandbox

- Added Jade status for the protected Knife equipped in Rich Farmer and Rich Paddy Farmer.
- The Knife line is server-backed, preserves the real ItemStack hover name, and is shown only for rich variants with a Knife equipped.
- Normal Farmer/Paddy Jade remains unchanged; Easy Farmer's Delight Compat continues to own crop/growth/Rich Soil data and Jade/Jade-universal output presentation.
- Marks the Eruruu laboratory implementation as feature-complete pending only final viewer regression checks and migration into the permanent destination mods.
- No Cutter processing, THE Pick, Moss, vegetation, Stonecutter Sifting, recipe, JEI/EMI, hopper, variant or stacking behavior changes in this version.

# 1.0.25 hotfix — Jade Cutter output rows

- Replaced the Cutter's unreliable Jade item-storage extension registration with explicit server-backed output synchronization through the already-working `CutterJadeProvider`.
- Jade now serializes only the Cutter's four finished output slots, aggregates identical products, and renders one row per product using Jade's public UI elements: small item icon + `amount× item name`, matching the Rich Farmer storage presentation.
- Inputs, Villager and protected Knife/Axe slot are never included in these output rows.
- Removing items through the GUI/hopper updates the displayed totals on the next Jade server-data refresh.
- No Cutter processing, hopper capability, JEI, recipe, variant, stacking or gameplay behavior is changed.


# 1.0.25 compile hotfix — stale Creative listener

- Removed the stale `ModCreativeTabs::onBuildCreativeTab` event-bus registration left behind after the Creative/JEI rollback.
- `ModCreativeTabs` no longer defines that listener; keeping the registration caused the final JEI/Jade recovery source to fail at `compileJava`.
- No gameplay, JEI, Jade, Cutter, Creative-tab, variant, stacking or processing behavior is changed by this hotfix.


# 1.0.25 final JEI/Jade recovery hotfix

- Removed the experimental standalone `Cutter Variants` JEI category and its dead data model completely.
- Restored the Cutter to the same normal Creative/JEI discovery path that was runtime-validated before the variants experiment, so its real crafting recipe can be indexed again.
- Jade output presentation now uses Jade's universal item-storage renderer for the Cutter's four output slots, matching Rich Farmer presentation (item icon + amount + item name) instead of a custom textual product list.
- The Cutter's status component remains separate and shows only variant + equipped Knife/Axe.
- The Jade storage provider is explicitly registered on both server and client; it reads only finished output slots and does not alter hopper/pipe capabilities.

# 1.0.25 hotfix — JEI recipe recovery + Jade output parity

- Removed the experimental **Cutter Variants** JEI category and its extra-ingredient/subtype hooks.
- Restored the last known-good Cutter discovery path: the canonical Cutter is again a normal Eruruu creative-tab entry, allowing JEI to index the real Cutter crafting recipe as it did before the variants experiment.
- Cutter variants remain a crafting/runtime feature; only the separate JEI variants documentation tab was removed.
- Jade now registers the Cutter's four output slots through Jade's public item-storage extension API. Products therefore use Jade's native storage layout, matching Rich Farmers: small item icon + amount + item name.
- The Cutter's custom Jade component remains responsible only for **Variant** and equipped **Tool**.
- The Jade storage provider is display-only and does not alter hopper/pipe sided automation or expose the protected tool slot.

# Eruruu Patch 1.0.25 — JEI/Jade recovery hotfix

- Restores Cutter visibility in JEI without reverting the Creative Search tooltip fix.
- Registers all nine Cutter material stacks through JEI's extra-ingredient API.
- Registers Cutter material as JEI subtype identity so Oak/Spruce/Birch/Jungle/Acacia/Dark Oak/Mangrove/Cherry/Bamboo stay distinct.
- Keeps the dedicated `Cutter Variants` category and the Farmer's Delight Cutting catalyst.
- Registers the Jade integration correctly with `@WailaPlugin`.
- Moves Cutter Jade state to a server data provider instead of relying on sided item capabilities/client BlockEntity guesses.
- Jade now reports the actual stored output totals, aggregated by product, plus variant and equipped tool.
- Does not change Cutter processing, recipes, stacking, rendering, automation, or variant persistence.

## 1.0.25 hotfix 2 — Creative tooltip cleanup + JEI Cutter Variants
- The canonical Cutter remains in Eruruu Patch's own Creative tab, but is now parent-tab-only there; all nine Cutter variants are injected directly into vanilla's Search tab. This preserves Creative access/search while preventing vanilla Search from adding a second blue `Eruruu Patch` category line above the Cutter variant tooltip.
- Added a dedicated JEI **Cutter Variants** category with one documentation entry per supported work surface: Oak, Spruce, Birch, Jungle, Acacia, Dark Oak, Mangrove, Cherry and Bamboo.
- Each JEI variant entry shows the actual Log/Bamboo Block as input, a right-facing arrow, and the Cutter stack carrying that exact variant as output. This is viewer documentation only and does not create nine crafting recipes; the real crafting recipe remains the single tag-driven Cutter recipe.
- The Cutter itself is a JEI catalyst for the variant category so looking up the Cutter exposes the material choices directly.


## 1.0.25 hotfix — Cutter variant item rendering / Pick Block
- Restores vanilla BlockItem display transforms to the dynamic Cutter item renderer so the inventory/JEI icon once again reads as the full Cutter enclosure instead of a flat glass face.
- Renders the stored log/Bamboo variant at the same workstation anchor used by the placed Cutter.
- Creative Pick Block now preserves the Cutter material variant while deliberately discarding Villager/tool/input/output/progress data.

# Eruruu Patch 1.0.25 — Cutter Variant Polish + Jade

## Cutter variant polish
- Added **Bamboo** as a ninth Cutter work-surface variant using `minecraft:bamboo_block`. The single Cutter recipe still displays the canonical Oak result and still uses one shared `#eruruu_patch:cutter_logs` ingredient; no duplicate recipe entries are created.
- Cutter BlockItems now show a localized `Variant: ...` / `Variante: ...` line below the item name.
- Replaced the static Cutter item model with one variant-aware BEWLR renderer. The normal glass/bricks Cutter enclosure is still rendered, but the stored Oak/Spruce/Birch/Jungle/Acacia/Dark Oak/Mangrove/Cherry/Bamboo work block is rendered prominently inside the item icon so different Cutters can be distinguished visually in inventories and recipe viewers.
- Variant identity remains stored in canonical BlockEntity data, so empty Cutters stack only with empty Cutters of the same material. Bamboo follows the same stacking/persistence rules as the existing log variants.

## Jade integration
- Added optional Jade support for the Cutter using Jade's normal `IWailaPlugin` / `IBlockComponentProvider` API. Jade is compile-only and remains optional at runtime.
- Looking at a placed Cutter now reports its localized variant and equipped Knife/Axe.
- Jade also lists the Cutter's **currently generated/stored output products**, including stack counts. Empty outputs are reported explicitly rather than inventing expected recipe results.
- The Jade provider reads the already-synchronized Cutter BlockEntity state; it does not create a second processing simulation or alter server gameplay.

## Migration contract
- The Bamboo variant, item tooltip/icon identity and Jade component are part of the Cutter feature contract and must migrate together with the Cutter to Easy Farmer's Delight Compat.

---

# Eruruu Patch 1.0.24 — Cutter discovery + log variants

## Cutter recipe discovery
- Reworked the Cutter recipe as one real custom `ShapedRecipe` so the vanilla Recipe Book, JEI and EMI all receive a normal 3x3 crafting representation while runtime assembly can still preserve the log species used in the craft.
- The single displayed result is the canonical Oak Cutter. The `L` ingredient remains `#eruruu_patch:cutter_logs`, so viewers/recipe placement accept the full supported log set without creating eight duplicate recipes.
- JEI now registers Cutter against Farmer's Delight's native `farmersdelight:cutting` recipe type using the same vanilla-to-JEI recipe-type conversion used by Farmer's Delight itself.
- EMI keeps Farmer's Delight's exact native Cutting category instance and uses a classloader-tolerant lookup before registering Cutter as a workstation. This avoids creating a visually similar but unrelated category key.

## Cutter log variants
- Cutter now remembers the exact supported log used in the bottom-center crafting slot: Oak, Spruce, Birch, Jungle, Acacia, Dark Oak, Mangrove or Cherry.
- The world renderer uses the stored log variant under the Cutting Board instead of always rendering Oak.
- Oak is the zero-data/default representation, preserving compatibility with all pre-1.0.24 Cutters, which load as Oak.
- Empty Cutters of the same log variant stack normally; different log variants do not stack because their canonical block-entity data differs.
- A Cutter that previously contained a Villager/tool/input/output can stack again with empty Cutters of its own log species after all machine contents/progress are actually empty and it is broken again.
- Populated Cutters are forced to max stack size 1 while carried, preserving legitimate relocation without allowing machines containing duplicated inventories/Villagers to merge into stacks.

## Stonecutter Sifting source audit
- Audited the supplied `stonecutter_sifting-1.21.1-1.0.0` and `1.1.1` JARs. They contain the exact same 19 paths and every class/resource is byte-identical; the only changed file content is `META-INF/neoforge.mods.toml`, where the declared version changes from `1.0.0` to `1.1.1`.
- The supplied 1.0.0 source build JAR is SHA-256 identical to the separately supplied 1.0.0 JAR. Functionally the existing source is therefore not missing 1.1.1 code; future migration can use it as the code base while adopting 1.1.1 as the current published version metadata.

---

# Eruruu Patch 1.0.23 — Recipe Discovery + compile hotfix

## Recipe discovery
- Paddy Farmer, Rich Farmer and Rich Paddy Farmer now expose display-only 3x3 crafting representations for the vanilla Recipe Book, JEI and EMI while Easy Farmer's Delight Compat remains authoritative for the real data-preserving craft.
- The display recipes deliberately never match or assemble an output themselves, preventing a data-less Farmer from bypassing EasyFD's BlockEntity-preserving upgrade recipe.
- Cutter remains a real shaped crafting recipe and is discoverable normally.
- JEI/EMI register the Cutter as a Farmer's Delight Cutting workstation/catalyst instead of duplicating Cutting Board recipes.
- JEI/EMI add dedicated documentation for Knife Harvesting (Rice/Mushroom Colonies) and Cutter Axe Actions (strip/scrape/unwax).

## 1.0.23 compile hotfix
- Removed the obsolete Forge `IShapedRecipe` assumption that does not exist on NeoForge 1.21.1.
- `FarmerUpgradeDisplayRecipe` now extends Minecraft's real `ShapedRecipe`, giving the vanilla Recipe Book an actual 3x3 shaped recipe object while still overriding `matches()`/`assemble()` so EasyFD remains the only recipe that performs the craft.
- Updated `build-dev.bat` to save the complete Gradle output to `build-dev.log`; on failure the log can be sent directly instead of hunting through the terminal scrollback.

---

# Eruruu Patch 1.0.22 — interaction and clone-safety audit fixes

## Direct tool equipment
- Replaced the unreliable Rich Farmer block-method injection with one NeoForge `RightClickBlock` equipment path shared by Rich Farmer, Rich Paddy Farmer and Cutter.
- Rich Farmer / Rich Paddy: normal right-click with a valid Knife equips exactly one Knife when the slot is empty.
- Cutter: normal right-click with a valid Knife or Axe equips exactly one tool when the slot is empty.
- If the relevant slot is already occupied, 1.0.22 does not overwrite/delete the equipped tool and does not consume the interaction; the block opens its normal GUI.
- Sneak-right-click is never intercepted, preserving crop/Villager/rope removal semantics.
- Survival consumes one equipped tool from the held stack; Creative does not.

## Creative Pick Block duplication hardening
- Paddy, Rich Farmer and Rich Paddy creative middle-click now return a **clean block item** with no copied block-entity data.
- Stored Villager, crop, ropes, Knife and progress can no longer be duplicated by cloning a populated machine and placing the clone.
- Normal block breaking remains unchanged and still preserves contents for legitimate relocation.

## Audit status
- Moss Helmet spread is now runtime validated after 1.0.21.
- Cutter GUI/title, Knife outline and Villager extraction are runtime validated after 1.0.21.
- Stonecutter Sifting laboratory additions remain fully runtime validated and ready for migration.

---

# Eruruu Patch 1.0.21 — audit fixes

## Moss Helmet
- Added Stone as a Moss Helmet Bone Meal target and repaired the Moss spread path for Stone/Cobblestone/Moss.

## Farmer/Cutter UX
- Restored Cutter title/text/Knife placeholder presentation, added Paddy title handling and fixed Cutter Villager extraction through sneak-right-click.

---

# Eruruu Patch 1.0.20 — Cutter Polish

## Cutter world rendering
- Repositioned the Oak Log to Easy Villagers' canonical Farmer crop/workstation transform instead of the old ad-hoc corner placement.
- The Farmer's Delight Cutting Board now sits directly on top of that scaled Log, making the Log read as the Cutter's work table/support.
- Added a display-only render of one current input item on top of the Cutting Board while the Cutter has a Villager, valid Knife/Axe and material. Flat items use Farmer's Delight-style laying-down transforms, while 3D block items use its raised block presentation. The real stack remains authoritative in the four input slots.

## Cutter GUI
- Rebuilt the screen around Easy Villagers' native `input_output.png` / Breeder-style layout.
- Inputs: four slots at the canonical top row (`x 52..106`, `y 20`).
- Outputs: four slots directly below (`x 52..106`, `y 51`).
- Knife/Axe equipment: right-side slot at `x 142`, `y 20`, aligned with the inputs like the Rich Farmer Knife slot.
- Player inventory now aligns with the Easy Villagers InputOutput background.
- Kept the neutral Knife/Axe placeholder and moved the progress indicator below the tool slot.
- Shift-clicking an extra Knife/Axe no longer allows it to fall through into a material input when the equipment slot is occupied.

## Hopper / pipe automation
- UP now exposes an insert-only combined equipment+input handler.
- Knives/Axes inserted from above fill the protected equipment slot; ordinary materials fill the four inputs.
- Extra processing tools are rejected from material inputs rather than clogging them.
- Horizontal sides remain material-input only; DOWN remains output extract-only.
- No external capability can extract the equipped Knife/Axe or insert directly into outputs.

## Validation state
- Stonecutter Sifting laboratory additions are now documented as runtime validated in NeoBlock and need migration rather than further sandbox implementation.
- README expanded with the exact 1.0.20 GUI coordinates, renderer contract and sided automation semantics for later Easy Farmer's Delight Compat migration.

---

# Eruruu Patch 1.0.19 — Cutter Laboratory

## Cutter block
- Added the first real Cutter laboratory block for the future Easy Farmer's Delight Compat migration.
- Recipe: `GGG / GCG / BLB` where G = Glass Pane, C = Farmer's Delight Cutting Board, B = Bricks and L = one normal vanilla overworld Log.
- Added Easy-Villagers-style enclosure model, Brick work floor, stored Villager renderer, Oak Log workpiece and Farmer's Delight Cutting Board work surface.
- The initial renderer always shows Oak; preserving the exact crafting Log species is intentionally deferred to later visual polish.

## Villager + inventory
- Cutter stores one Easy Villagers VillagerItem; any adult Villager can work and babies may remain stored/age until adulthood.
- Right-click a vacant Cutter with a VillagerItem to insert it; sneak + empty-hand right-click removes it.
- Added one protected Knife/Axe equipment slot, four input slots and four output slots.
- Added a neutral crossed Knife/Axe empty-slot outline plus a synchronized 10-tick processing progress bar.
- Villager, tool, inputs, outputs and progress persist in block-entity data; the dropped Cutter BlockItem carries that data when relocated.

## Processing
- One serial operation is attempted every 10 server ticks; four inputs do not process in parallel.
- Farmer's Delight Cutting Board recipes have first priority and use the actual input + tool pair, recipe-defined outputs/sounds and the tool's Fortune level.
- If no cutting recipe exists, Axes fall back to strip -> scrape Copper -> remove wax.
- Unwaxing never returns Honeycomb.
- Every successful operation damages a damageable Knife/Axe by 1 through Minecraft's normal enchantment-aware durability path; Rich Farmer Knife equipment remains zero-durability and separate from this rule.
- `OutputSimulator` gates every commit: full/incompatible outputs consume no input, cause no tool damage and lose no byproducts.

## Automation
- Cutter tool slot is never exposed to external item capabilities.
- DOWN exposes four extract-only output slots.
- Other sides expose four insert-only input slots.
- Hoppers/pipes therefore cannot insert/extract the Knife/Axe or inject items directly into outputs.

## Sandbox documentation
- README now specifies the complete Cutter 1.0.19 contract and explicitly lists what must migrate natively into Easy Farmer's Delight Compat after runtime validation.

---

# Eruruu Patch 1.0.18 — farmer/tool infrastructure

## Shared Farmer tool layer
- Added `FarmerToolSupport` as the common Knife/Axe classifier for the sandbox and future Easy Farmer's Delight Compat migration.
- Knife equipment continues to use `#c:tools/knife`; axes use `#minecraft:axes` for conventional modded-axis compatibility.
- Rich Farmer/Rich Paddy Farmer menu and block-entity integration now consume the shared helper instead of duplicating Knife checks.
- Kept `KnifeSupport` as a deprecated compatibility facade so the source transition does not unnecessarily break old references.

## Harvest resolver
- Added `HarvestResolver` and routed normal/Rice Knife selection and mature Mushroom Colony Knife gating through it.
- Existing in-game behavior is intentionally unchanged: no synthetic Knife byproducts, mature Mushroom Colonies wait without a Knife, and Knife-equipped Rice continues to let the real loot logic decide outputs.
- Tomato is explicitly treated as a non-Knife-special harvest path; the existing persistent Tomato/Rope implementation remains unchanged.

## Future Cutter processing foundation
- Added `CuttingRecipeResolver`, a reflection-safe bridge to runtime Farmer's Delight `farmersdelight:cutting` recipes without adding a hard implementation dependency to Eruruu's compile source.
- Cutting recipes remain authoritative for input/tool matching, datapacks/addons and rolled outputs; resolver accepts Fortune and returns the matched recipe ID plus rolled stacks.
- Added `AxeActionResolver` fallback with vanilla ordering: strip -> scrape Copper -> remove wax. Unwaxing never creates Honeycomb.
- Added `OutputSimulator` for lossless/atomic multi-output insertion. No operation should consume input or damage a tool unless every result fits.
- Cutter itself is deliberately NOT registered in 1.0.18; this version builds the reusable logic first so the later block/menu/entity implementation stays small and migration-friendly.

## Sandbox documentation
- Expanded README with the full 1.0.18 migration contract, active Farmer behavior, Tomato decision, Cutting Board resolver semantics, Axe fallback order, output-safety rules and the future Cutter processing pipeline.

---

# Eruruu Patch 1.0.17 — visual/UX stabilization

## Moss Helmet item icon
- Replaced the broken NeoForge layered item model that rendered as the missing-texture black/magenta icon in inventory and in-hand views.
- Added a dedicated 16x16 Moss Helmet item texture using the same fixed moss-green leather colour as the equipped helmet.
- The item now uses a normal `minecraft:item/generated` model, while the equipped armor remains the same dyed vanilla leather helmet and keeps all Moss Helmet gameplay interactions unchanged.

## Rich Farmer / Rich Paddy Farmer Knife slot
- Replaced the real Farmer's Delight Iron Knife sprite used as the empty equipment-slot background.
- Added a neutral monochrome `empty_knife_slot` sprite in the same visual language as vanilla empty armor/offhand equipment placeholders.
- The placeholder is only visible while the Knife slot is empty; inserting any valid `#c:tools/knife` stack renders the real item normally on top of the slot.
- The slot rules, Knife persistence, harvesting behaviour, Rice byproducts and Mushroom Colony Knife requirement are unchanged.
- The placeholder asset is intentionally owned by Eruruu Patch so the same UX can later migrate with the Knife slot into Easy Farmer's Delight Compat and be reused by the future Cutter.

---

# Eruruu Patch 1.0.16 — THE Pick stabilization

## Reinforced level model
- THE Pick level now means the number of real base pickaxes contained in the tool.
- Reinforced levels add directly: level 10 + level 5 = level 15.
- Removed the old `left fusions + right fusions + 1` progression calculation that allowed branch-merging to create free levels.
- The hard cap is now level 30 / 30 real pickaxe units.
- `eruruu_reinforced_units` is the authoritative level value.
- `eruruu_reinforced_fusions` is retained only as backwards-compatible metadata (`level - 1`) and no longer drives progression.
- Existing 1.0.15 tools immediately display their corrected level from `UNITS`, even if their stored legacy lore had an inflated fusion number.
- A lightweight server-side inventory migration runs once per second and only rewrites stale reinforced stacks, correcting legacy metadata/lore and trimming any old 31-unit durability edge case back to the level-30 cap.
- Rename and material-repair operations also normalize legacy reinforced metadata to the 1.0.16 representation.

## Modded pickaxe support and exclusions
- Conventional modded `PickaxeItem` tools may be reinforced just like vanilla pickaxes.
- Added `#eruruu_patch:reinforcement_blacklist` for special mining tools that should never become THE Pick.
- The shipped blacklist contains an optional `twilightforest:giant_pickaxe` entry; missing mods do not cause tag-load failures.
- Blacklisted ordinary tools retain their normal vanilla/mod anvil behavior; the blacklist only blocks Eruruu reinforcement.

## Admin/dev command
- Added `/eruruu givepick <pickaxe> <level> [player]` for operators/cheats.
- Uses Minecraft's item-registry argument, including modded IDs and normal tab completion.
- Level range is 1–30.
- Level 1 gives the normal base pickaxe; levels 2–30 create THE Pick.
- The command rejects non-pickaxes, blacklisted special tools, non-damageable/invalid pickaxe bases and impossible durability values.
- Command-created THE Picks use the same metadata, level, durability, localized name and lore rules as anvil-created tools.

## Auto Mining HUD
- Auto Mining itself remains unchanged from the in-game validated 1.0.15 behavior.
- Removed the screen-post render path that drew the ON indicator over inventory, JEI/EMI, backpacks and other GUIs.
- The indicator is now visible only while viewing the world.
- Auto Mining continues running normally while supported GUIs/chat are open; only the visual indicator is hidden.

---

# Eruruu Patch 1.0.15 — laboratory build

## THE Pick
- Toggle Auto Mining with an in-world left click while holding a reinforced THE Pick.
- Uses Minecraft's normal `continueDestroyBlock` path; enchantments, durability, drops and mod hooks remain in charge.
- Auto Mining survives temporary air/regeneration and stays active while chat, inventory, crafting or other screens are open.
- GUI clicks never toggle it off.
- The singleplayer pause screen does not freeze the integrated server while Auto Mining is active; normal pausing returns as soon as Auto Mining is off.
- Moving from the activation position, left-clicking again in-world, losing THE Pick, dying or changing world cancels it.
- Persistent localized HUD indicator while active.
- Restores normal enchanting-table levels for THE Pick and allows enchanted-book anvil operations.
- Reinforced-pick fusion remains restricted when enchantments are involved.

## Moss Helmet
- Cobblestone + Bone Meal still becomes Moss, then performs the same Moss bonemeal spread Minecraft uses normally.
- With Moss Helmet equipped, using a Hoe on Moss converts it to Dirt and consumes 1 Hoe durability.
- Bone Meal on Grass can now also generate Sweet Berry Bushes from newly-created Short Grass (3% after Wild Crop processing).

## Stonecutter Sifting integration (temporary)
- Sand: independent 0.25% Sniffer Egg bonus.
- Prismarine: 2 shards guaranteed; 25% +2 shards; 8% crystal; 1% wet sponge; 0.5% Heart of the Sea.
- Prismarine Bricks: 4 shards guaranteed; 35% +2; 10% +3; 12% crystal; 1.5% wet sponge; 0.75% Heart; 3% random coral.
- Dark Prismarine: 4 shards guaranteed; 35% +2; 10% +2; 20% ink sac; 15% crystal; 2% wet sponge; 1% Heart; 4% random coral.

## Easy Farmer's Delight Compat integration (temporary)
- Rich Farmer and Rich Paddy Farmer output screens gain a dedicated Knife equipment slot.
- Accepts `#c:tools/knife`; the equipped Knife is persisted in the block entity and is never damaged.
- Normal crop and mature-rice loot contexts use the equipped Knife, allowing Farmer's Delight to provide its own knife-specific products/byproducts (such as Straw).
- Mature Mushroom Colonies wait for a Knife before harvesting; they can still grow normally without one.
