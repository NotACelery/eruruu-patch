# Eruruu Patch — Development Guide

## Purpose

Eruruu Patch is the NeoForge 1.21.1 compatibility and quality-of-life layer used by Eruruu's Cult OneBlock.

The project intentionally owns only mechanics that remain specific to the OneBlock pack. Features that graduated into standalone projects are not mirrored here. Stonecutter Sifting and Easy Farmer's Delight Compat can coexist with Eruruu Patch, but Eruruu does not call into either project.

Current baseline:

- Minecraft: 1.21.1
- NeoForge: 21.1.235+
- Java: 21
- Mod version: 1.2.0
- Required runtime integrations: Farmer's Delight and Argentum
- Optional viewer integrations: JEI, EMI and Jade
- Optional crop compatibility: Ars Nouveau when present

This document is the canonical maintenance reference for implementation rationale that does not belong inside runtime classes.

## Repository contract

The active source tree should remain small and release-oriented.

Expected top-level files:

- `README.md`: current user-facing behavior.
- `CHANGELOG.md`: historical release record.
- `build.gradle`, `settings.gradle`, `gradle.properties`: canonical build configuration.
- `build.bat`: supported Windows build entry point.
- `cleanup-obsolete-files.bat`: one-shot migration helper included only in cleanup distributions.
- `.editorconfig` and `.gitattributes`: shared formatting and line-ending contract.
- `docs/DEVELOPMENT.md`: this document.
- `docs/TESTING-1.2.0.md`: current release regression checklist.

Generated build logs, Gradle distributions, IDE files and local run directories do not belong in source archives.

## Clean-code policy

Runtime Java should explain itself through names, small methods and explicit control flow.

Implementation comments are deliberately kept near zero. The reasons behind unusual behavior belong here instead:

- progression constraints;
- compatibility boundaries;
- save-compatibility choices;
- event ordering;
- anti-exploit rules;
- viewer fallbacks;
- client/server authority;
- inventory or automation invariants.

Formatting contract:

- Java: four spaces, 120-character target.
- Gradle and TOML: four spaces.
- JSON and MCMeta: two spaces.
- Shell: LF.
- Batch and PowerShell: CRLF.
- UTF-8 everywhere.

Formatting-only changes must not be mixed with gameplay changes.

## Package map

### Root package

`EruruuPatch`

- NeoForge mod entry point.
- Registers blocks, items, menus and event listeners.
- Connects client-only registration through the correct distribution-safe hooks.

`ModItems`

- Registers standalone gameplay items.
- Owns the Blank Spawn Egg.
- Owns Fertilizer.
- Owns Crimson and Warped Cultures.
- Owns the Moss Helmet.
- Owns Sugar Block and the two Sugar Rush foods.
- Keeps Dense Charcoal on the legacy `endless_charcoal` registry ID.
- Owns the hidden creative-tab icon.

`ModCreativeTabs`

- Builds the Eruruu Patch creative tab.
- Uses the dedicated visual icon item.
- Keeps the tab focused on Eruruu-owned gameplay objects.

### `item`

`NyliumCultureItem`

- Applies the configured Nylium block to vanilla Netherrack.
- Crimson and Warped variants share this implementation.
- Survival consumes the culture; creative mode does not.

`DenseCharcoalItem`

- Supplies the permanent enchantment glint.
- The actual furnace burn duration comes from the NeoForge furnace-fuel data map.
- Registry compatibility is maintained by the old `endless_charcoal` ID.

`FertilizerItem`

- Converts Dirt directly to Grass Block.
- Plays the normal server-side placement feedback.
- Survival consumes one item; creative mode does not.

### `client`

`AutoMiningController`

- Client-side state machine for THE Pick Auto Mining.
- Stores the player position used as the stationary safety anchor.
- Continues mining the current block under the crosshair.
- Treats temporary air as a pause rather than a session stop.
- Cancels when the player no longer satisfies the runtime requirements.

`AutoMiningClientEvents`

- Handles the world left-click toggle.
- Prevents GUI clicks from being interpreted as mining-toggle clicks.
- Advances the controller on client ticks.
- Renders the small ON indicator only when no screen obscures the world HUD.

`FilteredHopperScreen`

- Vanilla-style screen for the Filtered Hopper.
- Renders the normal Hopper storage and the dedicated filter slot.

`FilteredHopperClientEvents`

- Registers the Filtered Hopper screen against its menu type.

### `block`, `blockentity`, `menu`

`FilteredHopperBlock`

- Preserves Hopper-like geometry, facing and redstone enable/disable behavior.
- Opens the Filtered Hopper menu server-side.
- Delegates ticking to `FilteredHopperBlockEntity`.
- Drops the filter item separately when the block is removed.

`FilteredHopperBlockEntity`

- Maintains five ordinary storage slots.
- Stores one filter item independently from the normal container.
- Uses vanilla-style Hopper transfer timing.
- Exposes only the five storage slots to automation.
- Rejects incoming stacks that do not match the configured filter Item ID.
- Does not restrict extraction.

`FilteredHopperMenu`

- Maps five storage slots, one direct-click-only filter slot and the player inventory.
- Shift-click cannot configure the filter.
- Storage slot insertion follows the block entity's filter predicate.

### `registry`

`ModBlocks`

- Registers Charcoal Block and Filtered Hopper with their BlockItems.

`ModBlockEntities`

- Registers the Filtered Hopper block entity.
- Registers its item-handler capability.

`ModMenus`

- Registers the Filtered Hopper menu type and network factory.

### `event`

`VillagerTradeEvents`

- Adds the guaranteed Journeyman Farmer recovery offer.
- Removes only the vanilla Journeyman Cookie sale that would otherwise compete for the same listing slot.
- Leaves unrelated modded Farmer trades alone.

`BlankSpawnEggTradePriceMixin`

- Enforces the special two-Emerald minimum for the Blank Spawn Egg recovery trade.
- Vanilla demand may still raise the price.
- The mixin exists because normal positive reputation can otherwise drive the offer to the global one-Emerald floor.

`ReinforcedPickaxeEvents`

- Owns THE Pick anvil fusion, repair, migration, tooltip and construction rules.
- Defines reinforced progression by represented base-pickaxe units.
- Supports normal modded `PickaxeItem` implementations unless excluded through the blacklist tag.

`ReinforcedEnchantingCompat`

- Restores enchanting-table behavior for reinforced tools after Eruruu's durability manipulation.
- Reopens enchanted-book anvil operations without weakening the fusion restrictions.

`BonemealEvents`

- Owns the OneBlock-specific Bone Meal additions.
- Handles Moss Helmet propagation.
- Handles Farmer's Delight wild surface crops and Wild Rice.
- Handles the damp/dark starter mushroom mechanic.
- Uses deferred scans where vanilla Bone Meal needs to finish generating before Eruruu examines the result.

`EruruuWorldInteractions`

- Handles the Sweet Berry post-pass after grass generation.
- Handles Moss Helmet Hoe conversion.
- Keeps delayed world-interaction work separate from the main Bone Meal event.

### `command`

`EruruuCommands`

- Registers `/eruruu givepick`.
- Intended for administration and development.
- Uses the same centralized THE Pick construction logic as normal progression.

### `integration`

`WorldInteractionInfo`

- Immutable viewer data describing a block/item world interaction.

`MobDropInfo`

- Immutable viewer data describing Eruruu-owned mob-drop additions and Looting scaling.

`RecipeViewerData`

- Shared viewer source for JEI and EMI.
- Viewer percentages are presentation data for mechanics implemented elsewhere.
- This class is not the gameplay authority for loot or world interactions.

`EruruuJeiPlugin`

- Registers World Interaction and Mob Drop categories.
- Republishes the real 1.2.0 charcoal crafting recipe holders when the pack's viewer fails to index them.
- The RecipeManager remains authoritative.

`EruruuEmiPlugin`

- Registers equivalent interaction/drop documentation.
- Supplies explicit charcoal displays when native EMI indexing misses them.
- Removes duplicate native display entries before adding the fallback versions.

`EruruuJadePlugin`

- Provides a compact server-backed Hopper summary.
- Supports vanilla Hoppers and the Eruruu Filtered Hopper.
- For the filtered variant it also displays the filter state.

## Blank Spawn Egg system

The Blank Spawn Egg is an anti-RNG-lock recovery mechanism, not a generic Spawn Egg factory.

Acquisition:

- 9 vanilla Eggs -> 1 Blank Spawn Egg.
- Every vanilla Farmer gets a guaranteed Journeyman offer.
- Base trade: 10 Emeralds -> 1 Blank Spawn Egg.
- Uses: 4 before restock.
- The offer replaces the vanilla Journeyman Cookie sale, preserving the useful Melon trade.

Price rule:

- Normal reputation and curing apply.
- The custom price multiplier is intended to make a cured Farmer reach 2 Emeralds.
- The dedicated mixin prevents this recovery trade from dropping below 2.
- Vanilla demand can still increase the price.

Recipe policy:

- Outputs are real vanilla Spawn Eggs.
- Only explicitly supported recovery targets exist.
- Arbitrary mobs are intentionally unavailable.

Standard ring recipes use a Blank Spawn Egg in the center and eight matching ingredients around it.

Special cases:

- Rabbit uses seven Carrots plus one Dandelion to avoid colliding with Pig.
- Sheep require eight Wool blocks of exactly one color.
- Sheep results store the chosen color in `minecraft:entity_data`.
- Bee accepts the vanilla flowers tag and therefore permits mixed flower types.

Important current-source fact:

- The 1.2.0 Turtle recipe still uses `minecraft:seagrass`.
- Replacing it with a pack-specific Seaweed item is a gameplay/content change and must not be folded into a formatting-only cleanup.
- If that progression change is made later, update the recipe, README, localization/viewer documentation if necessary, tests and changelog together.

## Renewable crafting

The current recipe data owns the following OneBlock recovery paths:

- Bamboo -> Paper.
- Cobblestone -> Sand through Stonecutting.
- Cobblestone + Dirt -> Gravel.
- Coarse Dirt -> Dirt.
- Bone Blocks + Sand -> Soul Sand.
- Cobblestone + raw meat tag -> Netherrack.
- Short Grass -> Fertilizer.
- Sapling tag + Sticks -> Dead Bush.
- Crimson Roots -> Crimson Culture.
- Warped Roots -> Warped Culture.
- Charcoal packing/unpacking.
- Dense Charcoal compression.
- Moss Helmet.
- Sugar Block.
- Sugar Rush foods.
- Blank Spawn Egg and its supported animal recipes.
- Filtered Hopper.

Recipe JSONs are the gameplay source of truth unless a mechanic explicitly lives in an event or data map.

## Charcoal progression

### Charcoal Block

- 9 vanilla Charcoal -> 1 `eruruu_patch:charcoal_block`.
- 1 Charcoal Block -> 9 vanilla Charcoal.
- Physical block behavior copies the vanilla Coal Block baseline.
- The block has its own visual texture.
- The NeoForge furnace-fuel data map assigns 16,000 ticks.

The 16,000-tick value preserves the vanilla ten-times block-to-single-item burn-time relationship.

### Dense Charcoal

- 9 Charcoal Blocks -> 1 Dense Charcoal.
- Material cost: 81 vanilla Charcoal.
- Non-stackable.
- Permanent glint.
- Furnace duration: `Integer.MAX_VALUE`.

Save compatibility:

- The registry ID remains `eruruu_patch:endless_charcoal`.
- Do not rename the registry ID merely to match the display name.
- Existing worlds may contain stacks serialized under the legacy ID.

The previous custom 64-Charcoal serializer and deferred inventory-consumption mechanism are gone. Do not reintroduce them for normal crafting.

## Nether cultures

Crimson and Warped Cultures are Eruruu-owned mechanics.

Creation:

- 9 Crimson Roots -> Crimson Culture.
- 9 Warped Roots -> Warped Culture.

Use:

- right-click normal Netherrack;
- replace it with the corresponding Nylium;
- consume the culture in survival;
- preserve the culture in creative.

Once Nylium exists, normal vanilla Bone Meal progression takes over.

## Fertilizer

Fertilizer is deliberately simple:

- crafted from nine Short Grass;
- used directly on Dirt;
- replaces the Dirt with Grass Block;
- consumed in survival only.

It is a deterministic starter resource, not a replacement for Bone Meal.

## Bone Meal additions

### Surface Farmer's Delight crops

The mechanic observes the vegetation generated by Bone Meal on Grass Block.

For each eligible newly generated Short Grass position:

- Eruruu may replace the plant with a Farmer's Delight wild crop;
- replacement chance is 8%;
- available surface crops are Wild Cabbage, Wild Onion and Wild Tomato.

The delayed scan exists because vanilla Bone Meal generation must happen first.

### Wild Rice

Bone Meal on appropriate shallow-water Dirt follows vanilla aquatic generation first.

New eligible Seagrass positions receive an 8% replacement chance for Farmer's Delight Wild Rice.

### Sweet Berry Bush

After the surface pass, Short Grass that remains can independently become a Sweet Berry Bush.

Chance:

- 3% per eligible remaining Short Grass.

The scan runs one game tick later so it observes the final surface-generation result.

### Starter mushrooms

Valid Dirt must have:

- two blocks of interior air;
- a solid ceiling;
- source water directly above that ceiling;
- local light at the mushroom position <= 7.

Every valid attempt consumes Bone Meal.

Outcome:

- 10% total success.
- Brown/Red choice is 50/50.
- Effective chance is 5% Brown and 5% Red.

### Moss Helmet

While equipped:

- Bone Meal on Stone/Cobblestone creates a Moss entry point.
- Existing Moss can be routed through the explicit helmet path.
- Propagation follows a short local spread designed around vanilla Moss behavior.
- Immediate neighbors have the strongest guarantee; later rings are probabilistic.

The current spread probabilities in code are 100%, 55% and 30% for successive local rings.

Hoe interaction:

- use a Hoe on Moss while wearing the helmet;
- converts the Moss Block to Dirt;
- costs one point of Hoe durability.

## Argentum and optional Ars Nouveau composting

Eruruu uses NeoForge compostable item data maps instead of replacing Composter logic.

This preserves:

- manual composting;
- Hopper-fed composting;
- vanilla Composter state behavior.

Argentum crop entries are handled through data/tags relevant to Eruruu's automation workflow.

Ars Nouveau is optional. When present, Magebloom items use Eruruu's compatibility entries without making Ars a required mod.

## Extra mob loot

Gameplay loot is implemented through Eruruu loot modifiers/tables.

Viewer documentation is centralized in `RecipeViewerData`.

Current documented rolls:

### Zombie Gold Ingot

- base: 0.8333333%;
- Looting I reference: 1.1666667%;
- increment: 0.3333333 percentage points per Looting level in the viewer model;
- requires player kill in the normal roll;
- Easy Mob Farm compatibility has its own equivalent path to avoid duplicate handling.

### Witch Nether Wart

- base: 10%;
- Looting I reference: 12.5%;
- +2.5 percentage points per Looting level.

### Witch Blaze Rod

- base: 5%;
- Looting I reference: 6.25%;
- +1.25 percentage points per Looting level.

Do not make viewer data the gameplay authority. If loot math changes, update both implementation resources and viewer documentation in the same change.

## THE Pick progression

### Identity

A reinforced pickaxe remains the original base pickaxe item. Eruruu changes durability and stores metadata rather than registering a replacement tool item.

Primary metadata keys:

- `eruruu_reinforced_pickaxe`
- `eruruu_reinforced_units`
- `eruruu_reinforced_base_name`
- legacy fallback: `eruruu_reinforced_fusions`

### Level model

- level 1 = one ordinary base pickaxe;
- THE Pick begins at level 2;
- maximum level = 30;
- levels represent real base-pickaxe units;
- fusion adds represented units directly.

Example:

- level 10 + level 5 -> level 15.

This unit model prevents branch-merging from manufacturing durability beyond the number of actual pickaxes represented.

### Fusion constraints

Normal fusion requires:

- same base pickaxe item;
- eligible `PickaxeItem`;
- both fully repaired;
- no forbidden enchantment state;
- combined level <= 30.

Special tools can opt out through:

`#eruruu_patch:reinforcement_blacklist`

The blacklist should be preferred over hardcoding mod-specific tool IDs.

### Repair

A reinforced pickaxe can be repaired using the original pickaxe repair material.

Each material restores approximately one quarter of one base pickaxe's durability, not one quarter of the enlarged reinforced maximum.

Repair XP intentionally avoids the normal prior-work escalation used for standard anvil combinations.

### Legacy migration

Existing historical stacks are normalized during player inventory scans.

Priority:

1. use authoritative `eruruu_reinforced_units` when present;
2. retain old fusion metadata only as compatibility fallback;
3. normalize outdated display/progression metadata;
4. preserve the original base item identity.

Do not delete legacy-read compatibility casually. The fields are cheap and protect old worlds.

## THE Pick Auto Mining

Auto Mining is client-controlled but uses normal Minecraft block-breaking calls.

Activation requirements:

- local player exists;
- level/game mode exist;
- no GUI is open at activation;
- reinforced pickaxe is in the main hand;
- crosshair points to a valid non-air block.

Behavior:

- activation anchors the player's exact position;
- camera rotation is allowed;
- the current block under the crosshair is mined;
- moving the camera redirects work;
- temporary generator air pauses the break animation but keeps the mode active;
- opening a GUI after activation does not inherently stop the controller;
- GUI clicks never toggle the mode.

Safety cancellation:

- player movement beyond the tiny position epsilon;
- loss of required player/game state;
- invalid main-hand reinforced tool;
- death/dimension/disconnect paths handled by client state checks;
- explicit second world left-click.

The controller deliberately stops the current destroy action while pointed at air instead of interpreting temporary generator downtime as a completed session.

## Filtered Hopper

### Storage model

The block has:

- five normal Hopper storage slots;
- one independent filter ItemStack.

The filter is not part of the five-slot `Container`.

Consequences:

- Comparator fullness remains based on storage.
- Vanilla-style storage capacity remains five stacks.
- Automation capability exposes storage, not filter.
- Filter persistence is handled separately in block-entity NBT.

### Matching model

Incoming acceptance compares Item ID.

It intentionally ignores:

- damage;
- custom name;
- enchantments;
- other stack components.

This makes the filter predictable for OneBlock automation and avoids requiring players to preserve a perfect component-identical sample.

### Transfer model

- incoming item transfer must satisfy the filter when configured;
- outgoing transfer is not restricted by the filter;
- direct player interaction configures the sample item;
- shift-click cannot silently overwrite the filter;
- automation cannot change the filter.

### Redstone and ticking

The block follows Hopper-like enabled/disabled redstone state.

Move speed constant:

- 8 ticks.

## JEI, EMI and Jade

### Shared viewer rules

Normal crafting and Stonecutting recipes should be discovered from Minecraft's real RecipeManager.

Custom viewer data is appropriate only for mechanics that are not ordinary recipes:

- world interactions;
- mob drops;
- compact Hopper state.

### Charcoal fallback

The 1.2.0 charcoal chain was observed disappearing from the recipe index in the target pack even while Minecraft loaded the recipes correctly.

JEI and EMI therefore contain explicit fallback publication for the real recipe holders/displays.

Rules:

- recipe JSON remains gameplay truth;
- do not create a second gameplay recipe in viewer code;
- avoid duplicate viewer entries;
- if upstream viewer behavior is later proven reliable in the actual pack, the fallback may be reevaluated separately.

### Jade Hopper summary

The Jade plugin receives server data and renders a concise summary.

For Filtered Hopper it includes:

- filter item or None;
- storage usage;
- grouped visible item entries;
- overflow count when more groups exist than the compact display permits.

## Resource and data layout

Important resource families:

- `assets/eruruu_patch/lang`: five locale files.
- `assets/eruruu_patch/models`: block and item models.
- `assets/eruruu_patch/textures`: Eruruu-owned textures.
- `data/eruruu_patch/recipe`: normal recipes.
- `data/eruruu_patch/advancement/recipes`: recipe unlock advancements.
- `data/eruruu_patch/loot_modifiers`: Eruruu loot modifier declarations.
- `data/eruruu_patch/loot_table`: injected loot data and block drops.
- `data/eruruu_patch/tags`: Eruruu gameplay tags.
- `data/neoforge/data_maps/item`: furnace fuel and composting values.
- `data/easy_mob_farm`: compatibility loot table data.
- `src/main/templates/META-INF/neoforge.mods.toml`: generated mod metadata template.

All locale files should maintain identical key sets.

## Metadata

`neoforge.mods.toml` is generated from Gradle properties.

Current required dependencies:

- NeoForge.
- Minecraft 1.21.1.
- Farmer's Delight.
- Argentum.

Current optional integrations are deliberately not hard requirements:

- JEI.
- EMI.
- Jade.
- Ars Nouveau compatibility is runtime/data driven where applicable.

The mod description must use current user-facing names. In 1.2.0 the legacy registry ID remains `endless_charcoal`, but the feature name is Dense Charcoal.

## Building

Supported Windows path:

```text
build.bat
```

The helper:

1. resolves `mod_version` from `gradle.properties`;
2. finds Java from PATH, `JAVA_HOME` or common Prism/Java installation roots;
3. establishes Java 21 for the build;
4. downloads Gradle 9.2.1 into `.gradle-dist` when absent;
5. runs `clean build --stacktrace`;
6. writes the complete Gradle output to `build.log`;
7. reports the generated JAR from `build/libs`.

`build.log` and `.gradle-dist` are generated artifacts and remain ignored.

## Cleanup migration helper

Some distributed cleanup snapshots contain `cleanup-obsolete-files.bat`.

It is intentionally temporary.

For this cleanup pass it removes the old:

- `build-dev.bat`;
- `build-dev.log`;
- `PATCH-NOTES.txt`;
- historical cleanup helper names if they still exist in an overlaid workspace.

It then deletes itself.

Do not add this temporary helper to permanent release manifests.

## Change discipline

### Formatting/documentation-only pass

Allowed:

- whitespace;
- line wrapping;
- comment removal;
- documentation;
- repository hygiene;
- generated-file removal;
- stable script rename.

Not allowed:

- chance changes;
- recipe changes;
- registry renames;
- NBT key changes;
- event priority changes;
- inventory semantics;
- viewer gameplay duplication.

### Functional pass

When gameplay changes:

1. change the smallest authoritative implementation;
2. update resources/tags/data maps;
3. update viewer documentation if the mechanic is represented there;
4. update README;
5. add CHANGELOG entry;
6. extend the current testing checklist;
7. specifically test save/world compatibility for registry or metadata changes.

## High-risk regression areas

Treat these as release blockers:

- THE Pick level or durability inflation.
- Legacy reinforced stacks losing identity.
- Auto Mining continuing after player movement.
- Auto Mining toggling from GUI clicks.
- Filtered Hopper allowing mismatched incoming items.
- Automation mutating the filter slot.
- Farmer recovery trade becoming random again.
- Blank Spawn Egg price falling below the intended floor.
- Charcoal recipes crafting correctly but disappearing from both viewers.
- Duplicate loot from Easy Mob Farm compatibility.
- Moss Helmet propagation consuming or modifying unintended blocks.
- Wild crop replacement running before vanilla Bone Meal generation.
- locale key drift.

## Pre-release checklist

Before considering a source snapshot publishable:

- `gradle.properties` version matches README/CHANGELOG.
- `build.bat` succeeds on Java 21.
- no generated `build.log` is committed.
- all JSON parses.
- locale key sets match.
- recipes load.
- JEI and EMI show normal recipes plus Eruruu custom documentation.
- Jade is absent-safe and present-safe.
- Filtered Hopper survives save/reload with its filter.
- THE Pick survives save/reload and legacy migration.
- Auto Mining passes stationary-generator tests.
- Blank Spawn Egg trade and recipes pass.
- Charcoal packing/unpacking and Dense Charcoal pass.
- optional companion mods remain optional.
- Stonecutter Sifting and Easy Farmer's Delight Compat remain decoupled.

For the concrete 1.2.0 QA sequence, use `docs/TESTING-1.2.0.md`.
