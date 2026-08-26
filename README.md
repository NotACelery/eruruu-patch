# Eruruu Patch

Small NeoForge 1.21.1 compatibility/QoL patch for **Eruruu's Cult OneBlock**.

The design goal is to remove OneBlock-specific resource locks without turning the pack into a vending machine: most new acquisition methods require renewable inputs, infrastructure, time, or low-probability processing.

## Sandbox / migration policy
Eruruu Patch remains the **integration sandbox for the OneBlock pack**. Version **1.2.0** regularizes the charcoal progression with a real Charcoal Block and Dense Charcoal while retaining the 1.1.0 Blank Spawn Egg animal-recovery system.

The ownership rule established after 1.0.28 remains strict: once an experimental feature has moved into another project, Eruruu removes the implementation completely instead of keeping compatibility hooks or a hidden legacy copy.

That means Eruruu Patch now contains only its own OneBlock/QoL systems. It no longer calls into, mixes into, aliases ids from, or requires **Stonecutter Sifting** or **Easy Farmer's Delight Compat**.

### 1.0.28 final decoupling
Version 1.0.27 moved the main Farmer/Cutter and generic sifting experiments into their permanent projects, but some leftovers still remained in Eruruu. Version 1.0.28 removes the rest:
- the complete legacy Cutter runtime and all of its helpers, renderers, menus, resources and Jade integration;
- the old Farmer Knife/Harvest Tool compatibility code and the historical `EruruuKnife` migration hook;
- the old Stonecutter Sifting mixin and compile-time development JAR;
- all registry aliases for `eruruu_patch:cutter`;
- stale Cutter and migrated-sifting resources/localization.

There is intentionally **no old-world migration layer** for those sandbox-only experiments. They only existed in the private development/test world used while the features were being designed.

Crimson and Warped Cultures are entirely native Eruruu mechanics: **9 vanilla Crimson Roots craft a Crimson Culture, and 9 vanilla Warped Roots craft a Warped Culture**. Stonecutter Sifting is not involved in creating either item.

## Features

### Blank Spawn Egg — NeoBlock animal recovery
Version 1.1.0 introduced a controlled recovery path for passive/utility mobs that can become inaccessible when NeoBlock progression or trader delivery fails. The system is intentionally finite: only the recipes listed below exist, and every result is the **real vanilla Spawn Egg** for that mob.

#### Getting a Blank Spawn Egg
A Blank Spawn Egg can be obtained in either of two ways:

**Crafting — 9 Chicken Eggs:**

```text
E E E
E E E
E E E
```

`E = minecraft:egg`  
Result: `1 eruruu_patch:blank_spawn_egg`

**Farmer trade — guaranteed Journeyman offer:**
- Every vanilla Farmer gets **10 Emeralds -> 1 Blank Spawn Egg** at Journeyman / level 3.
- The offer has **4 uses before restock** and replaces only the vanilla Cookie offer; the useful Melon trade remains.
- Villager reputation/curing applies normally. The intended cured price is **2 Emeralds**, with a 2-Emerald floor for this specific recovery trade. Normal demand may still raise the price temporarily.

#### Standard animal crafting pattern
Unless a recipe below says otherwise, put the **Blank Spawn Egg in the center** and surround it with 8 copies of the listed ingredient:

```text
I I I
I B I
I I I
```

`B = eruruu_patch:blank_spawn_egg`  
`I = the mob-specific ingredient below`

| Result | Surrounding ingredient | Notes |
| --- | --- | --- |
| Chicken Spawn Egg | 8 Wheat Seeds | `minecraft:wheat_seeds` |
| Cow Spawn Egg | 8 Wheat | `minecraft:wheat` |
| Pig Spawn Egg | 8 Carrots | `minecraft:carrot` |
| Horse Spawn Egg | 8 Golden Carrots | `minecraft:golden_carrot` |
| Donkey Spawn Egg | 8 Hay Bales | `minecraft:hay_block` |
| Wolf Spawn Egg | 8 Bones | `minecraft:bone` |
| Cat Spawn Egg | 8 raw Cod | `minecraft:cod` |
| Bee Spawn Egg | 8 Flowers | Uses `#minecraft:flowers`; flower types may be mixed |
| Fox Spawn Egg | 8 Sweet Berries | `minecraft:sweet_berries` |
| Panda Spawn Egg | 8 Bamboo | `minecraft:bamboo` |
| Turtle Spawn Egg | 8 Seagrass | `minecraft:seagrass` |
| Armadillo Spawn Egg | 8 Spider Eyes | `minecraft:spider_eye` |
| Camel Spawn Egg | 8 Cactus | `minecraft:cactus` |
| Strider Spawn Egg | 8 Warped Fungus | `minecraft:warped_fungus` |
| Hoglin Spawn Egg | 8 Crimson Fungus | `minecraft:crimson_fungus` |

#### Rabbit recipe
Rabbit is deliberately different from Pig so both recipes remain unambiguous:

```text
C C C
C B C
C D C
```

`C = minecraft:carrot`  
`D = minecraft:dandelion`  
`B = eruruu_patch:blank_spawn_egg`  
Result: `1 minecraft:rabbit_spawn_egg`

#### Sheep color recipes
Sheep use the standard ring pattern, but **all 8 Wool blocks must be the same color**. The output is a vanilla `minecraft:sheep_spawn_egg` carrying the matching Sheep `Color` in `minecraft:entity_data`, so the spawned Sheep has that exact color.

Supported variants:

| Wool used | Spawned Sheep color |
| --- | --- |
| White Wool | White |
| Orange Wool | Orange |
| Magenta Wool | Magenta |
| Light Blue Wool | Light Blue |
| Yellow Wool | Yellow |
| Lime Wool | Lime |
| Pink Wool | Pink |
| Gray Wool | Gray |
| Light Gray Wool | Light Gray |
| Cyan Wool | Cyan |
| Purple Wool | Purple |
| Blue Wool | Blue |
| Brown Wool | Brown |
| Green Wool | Green |
| Red Wool | Red |
| Black Wool | Black |

Mixed Wool colors do not match a recovery recipe. Arbitrary mob Spawn Eggs remain unavailable unless a specific OneBlock recovery recipe is deliberately added to Eruruu Patch.

### Renewable / OneBlock-friendly recipes
- 3 Bamboo (horizontal) -> 3 Paper
- Cobblestone -> Sand in the Stonecutter (1:1)
- 2 Cobblestone + 2 Dirt in a 2x2 checkerboard -> 4 Gravel
- 1 Coarse Dirt -> 1 Dirt
- 2 Bone Blocks + 2 Sand in a 2x2 checkerboard -> 4 Soul Sand
- 2 Cobblestone + 2 raw-meat items in a 2x2 checkerboard -> 4 Netherrack
- 9 Short Grass (3x3) -> Fertilizer
- 8 Sticks around any `#minecraft:saplings` item -> Dead Bush
- 9 Crimson Roots -> Crimson Culture
- 9 Warped Roots -> Warped Culture
- 9 Charcoal (3x3) -> Charcoal Block
- 1 Charcoal Block -> 9 Charcoal
- 9 Charcoal Blocks (3x3) -> Dense Charcoal
- 5 Moss Blocks in a helmet pattern -> Moss Helmet
- 4 Sugar in a 2x2 -> Sugar Block
- 8 Sugar around a Wooden Pickaxe -> Sugar Rush Pickaxe (Haste I food)
- 8 Sugar Blocks around a Wooden Pickaxe -> Enchanted Sugar Rush Pickaxe (Haste II food)

### Nether cultures
- Crimson Culture + right-click Netherrack -> Crimson Nylium
- Warped Culture + right-click Netherrack -> Warped Nylium
- Cultures are consumed in Survival and remain in Creative.
- Once Nylium exists, vanilla Bone Meal behavior handles Nether vegetation normally.

### Renewable Nether base blocks
- Soul Sand uses 2 Bone Blocks and 2 Sand. The Bone Blocks represent 6 Bones total, keeping the route renewable but costly.
- Netherrack accepts Raw Beef, Raw Porkchop, Raw Chicken, Raw Mutton, Rabbit, or Rotten Flesh. Cooked food is intentionally excluded.

### Starter mushrooms
Use Bone Meal on normal Dirt when all of these are true:
- the room has exactly two air blocks of interior height above the Dirt;
- the ceiling is a full solid block;
- source water is directly above that ceiling;
- local light at the mushroom position is 7 or less.

Every valid attempt consumes one Bone Meal. Each attempt has a 10% total success chance, split evenly between Brown Mushroom and Red Mushroom (5% each).

### Farmer's Delight access in OneBlock
- Bone Meal on Grass Block keeps vanilla behavior. Every newly generated Short Grass in the spread independently has an 8% chance to become Farmer's Delight Wild Cabbage, Wild Onion, or Wild Tomato.
- Short Grass that remains after that Wild Crop pass has an independent 3% chance to become a vanilla Sweet Berry Bush.
- Bone Meal on Dirt under one block of shallow source water keeps vanilla aquatic growth. Every newly generated Seagrass candidate in the spread independently has an 8% chance to become Wild Rice.

### Charcoal Block and Dense Charcoal
- 9 vanilla Charcoal craft into 1 placeable Charcoal Block; 1 Charcoal Block can be unpacked back into 9 Charcoal.
- Charcoal Block uses the vanilla Block of Coal geometry with a warmer charcoal-brown palette and is mined with a Pickaxe.
- Charcoal Block burns for `16,000` ticks. Vanilla Charcoal burns for `1,600`, so this reproduces the same 10x block-to-single-item burn-time ratio used by vanilla Coal Blocks.
- 9 Charcoal Blocks craft into 1 non-stackable **Dense Charcoal**, for a total material cost of 81 Charcoal.
- Dense Charcoal intentionally retains the legacy registry ID `eruruu_patch:endless_charcoal` so existing worlds keep old stacks after upgrading.
- Dense Charcoal keeps the vanilla Charcoal appearance with permanent enchantment glint.
- Dense Charcoal furnace burn time remains `Integer.MAX_VALUE` = 2,147,483,647 ticks (~3.4 years of continuously loaded furnace time).
- The old custom 64-item special recipe and deferred post-craft inventory consumption were removed; all three charcoal conversions are normal recipe JSONs.

### Fertilizer
- Custom item using the vanilla Wheat Seeds appearance.
- Right-click Dirt to replace the entire block with Grass Block.
- Consumes one Fertilizer in Survival; Creative mode does not consume it.

### Extra renewable mob loot
- Zombie: independent Gold Ingot roll matching vanilla Iron Ingot's individual rare-drop probability, with Looting scaling and player-kill requirement.
- Witch: Nether Wart at 10% base, scaling by +2.5 percentage points per Looting level.
- Witch: Blaze Rod at 5% base, scaling by +1.25 percentage points per Looting level.
- Easy Mob Farm is supported. Witch drops naturally pass through NeoForge global loot modifiers; Zombie farms receive an equivalent bonus table when no attacking-player context exists, avoiding duplicate Gold rolls when Sword Enhancement supplies one.

### Moss Helmet starter
- Crafted from 5 Moss Blocks in the vanilla helmet pattern.
- Uses leather-helmet armor behavior and a fixed green dyed appearance when equipped.
- Since 1.0.17 the inventory, hotbar, dropped-item and in-hand representation uses a dedicated baked moss-green helmet texture instead of the broken layered item model; this is a visual-only fix and does not alter the equipped armor renderer.
- While equipped in the head slot, using Bone Meal directly on Cobblestone converts that block into a Moss Block and schedules the same moss-spread bonemeal behavior used by vanilla Moss.
- Survival consumes one Bone Meal; creative mode does not.
- With the Moss Helmet equipped, right-clicking a Moss Block with any Hoe converts it to Dirt and consumes 1 point of Hoe durability.

### Argentum crop compatibility
Eruruu Patch keeps lightweight Farmer/Composter compatibility for the Argentum crops that are relevant to its automation layer. Membrillo planting material is recognized through the normal villager seed path, while Batata remains compatible with the crop workflow. Their excess planting/harvest items can be recycled through a normal Composter using vanilla-style seed/produce probabilities.

When Ars Nouveau is installed, Magebloom follows the same crop rule without becoming a required dependency: Magebloom Crop composts at 30% and harvested Magebloom at 65%. Magebloom Fiber is intentionally excluded as a processed product.

This uses NeoForge's standard compostable item data map, so manual composting, hopper-fed Composters and normal Composter behavior remain unchanged.

### Crafting recipe conflicts
Eruruu Patch no longer implements its experimental crafting-result selector. Recipe conflicts are intentionally left to dedicated modpack tooling instead of being intercepted by Eruruu's crafting menus or result slots. This keeps Eruruu independent from whichever recipe-conflict mod the pack chooses to use.

### Recipe viewers / recipe discovery
- Normal Eruruu crafting/stonecutting recipes are real recipe JSONs, so the vanilla Recipe Book, JEI and EMI discover them normally.
- Charcoal Block, Charcoal unpacking and Dense Charcoal are normal crafting recipes and are indexed automatically by the vanilla Recipe Book, JEI and EMI.
- JEI retains Eruruu-owned **World Interaction** and **Mob Drops** categories; EMI retains native **World Interaction** displays plus its custom **Mob Drops** category.
#### Migration status
The first migration landed in 1.0.27. Version **1.0.28 completes the cleanup** by deleting every remaining runtime hook, alias, mixin, devlib and resource that connected Eruruu to the migrated Farmer/Cutter or generic sifting experiments. The companion mods can still be installed in the same modpack, but Eruruu does not depend on or modify them.

### Branding
- Eruruu artwork is used as the mod logo.
- A hidden visual-only `eruruu_icon` item is used as the Eruruu Patch creative tab icon.
- The creative tab lists the gameplay items, including the Moss Helmet and Sugar Rush crafting/food items.

## Required mods
- Minecraft 1.21.1
- NeoForge 21.1.235+
- Argentum 1.0.0+
- Farmer's Delight 1.3.2+

JEI and EMI integrations are optional at runtime. **Ars Nouveau is also optional**; when present, Eruruu only adds the Magebloom composting QoL described above.

**Stonecutter Sifting and Easy Farmer's Delight Compat are not dependencies of Eruruu Patch.** They can be installed alongside it in the OneBlock pack, but 1.2.0 contains no runtime integration with either project.

## Development
This project targets Java 21 and ModDevGradle. The supplied Gradle files are the canonical source build. The distributed JAR is also compile-checked directly against NeoForge 1.21.1 classes and the exact JEI/EMI API JARs used by the Eruruu OneBlock instance.

## Reinforced pickaxes

Two identical, fully repaired and unenchanted pickaxes can be fused in an anvil for 3 levels. Their maximum durability is added together. Fusion remains restricted when enchantments are involved, but the finished reinforced pickaxe can be enchanted normally through the Enchanting Table or compatible enchanted books in an anvil. Reinforced pickaxes may only be repaired with the original pickaxe repair material, and each repair material restores only one quarter of the base pickaxe durability. Repair XP is intentionally low: one level per four repair materials, rounded up. Reinforced pickaxes keep a localized THE/LA prefix when renamed.

Version 1.0.16 defines reinforced progression by **real pickaxe units**, not by historical anvil operations. Level 1 means one normal base pickaxe; THE Pick begins at level 2 and caps at level 30. Levels add directly (`10 + 5 = 15`), closing the old branch-merging exploit. Existing 1.0.15 tools use their already-correct `eruruu_reinforced_units` value as the authoritative level, while the old fusion counter remains only as compatibility metadata. Haste/Haste II continue to affect mining normally because the reinforced stack remains the original vanilla/modded `PickaxeItem`; only its durability components and Eruruu metadata change.

Conventional modded pickaxes are supported automatically when they extend Minecraft's normal `PickaxeItem` behaviour. Special mining tools can be excluded with the datapack tag `#eruruu_patch:reinforcement_blacklist`; 1.0.16 ships an optional Twilight Forest Giant Pickaxe exclusion. This keeps drills, giant/area tools, paxels, hammers or other special cases out of THE Pick without hardcoding a vanilla-only material list.

### Admin/dev command
Operators may create test picks with:

```text
/eruruu givepick <pickaxe> <level> [player]
```

Examples:

```text
/eruruu givepick minecraft:diamond_pickaxe 10
/eruruu givepick minecraft:netherite_pickaxe 30
/eruruu givepick some_mod:osmium_pickaxe 15 @s
```

The item argument uses the normal registry, so modded pickaxe IDs autocomplete naturally. Level 1 intentionally gives the ordinary base pickaxe; levels 2–30 create THE Pick through the same centralized construction rules used by anvil fusion.

### THE Pick Auto Mining
- With a reinforced pickaxe in the main hand, left-click a valid block in reach to toggle Auto Mining on.
- Auto Mining follows the current block under the crosshair and uses normal mining behaviour; it does not pin one coordinate.
- Temporary air while a OneBlock/cobblestone/stone/basalt generator regenerates does not turn the toggle off.
- Inventory, chests, backpacks, crafting screens, JEI/EMI, chat and other GUIs may stay open while mining continues. Clicking inside those interfaces does not toggle Auto Mining. The ON indicator is hidden while a screen is open so it never overlays GUI slots, and returns when the player closes the screen.
- A second left click in the world toggles it off. It also cancels when the player moves, removes THE Pick from the main hand, dies, changes dimension, or leaves the world. Rotating the camera does not cancel it.

## Historical sandbox archive

The sections below document how features were developed and validated inside Eruruu before migration. They are retained as project history and **do not describe active Farmer/Cutter or generic Stonecutter gameplay in Eruruu Patch 1.2.0**.

### Laboratory integrations in 1.0.15

#### THE Pick HUD and enchanting
- Auto Mining shows a persistent localized HUD indicator while active in the world. Since 1.0.16 the indicator is hidden while screens are open, while mining itself continues normally behind them.
- The singleplayer pause screen stops pausing the integrated server while Auto Mining is active; normal pause behavior returns when it is disabled.
- Reinforced THE Picks can receive normal pickaxe enchantments after fusion. Fusion itself still rejects enchanted inputs.

#### Stonecutter Sifting prototype — runtime validated
These are temporary Eruruu Patch mixins. **NeoBlock runtime testing has now validated the complete temporary Stonecutter Sifting addition set as functional**: Sand's independent Sniffer Egg bonus plus Prismarine, Prismarine Bricks and Dark Prismarine recovery/ocean bonus tables. The next work for this feature is migration into **Stonecutter Sifting** itself, not further Eruruu gameplay changes, unless later testing uncovers a regression.

- **Sand:** independent 0.25% Sniffer Egg bonus (about 1 per 400 Sand on average). It is a bonus roll and does not replace the normal Sand sifting result.
- **Prismarine:** 2 Prismarine Shards guaranteed; independent 25% chance for +2 Shards; 8% Prismarine Crystal; 1% Wet Sponge; 0.5% Heart of the Sea.
- **Prismarine Bricks:** 4 Shards guaranteed; independent 35% chance for +2 Shards; independent 10% chance for +3 Shards; 12% Prismarine Crystal; 1.5% Wet Sponge; 0.75% Heart of the Sea; 3% random coral bonus.
- **Dark Prismarine:** 4 Shards guaranteed; independent 35% chance for +2 Shards; a second independent 10% chance for +2 Shards; 20% Ink Sac; 15% Prismarine Crystal; 2% Wet Sponge; 1% Heart of the Sea; 4% random coral bonus.
- The intended design is material recovery first, rare ocean resources second; the tables are not generic random-loot conversions.

##### Supplied 1.0.0 vs 1.1.1 audit
The supplied Stonecutter Sifting JARs were unpacked and compared path-by-path before migration planning. Both contain **19 files with the same paths**. Every `.class`, language file, tag, icon and manifest is byte-identical. The only differing content is `META-INF/neoforge.mods.toml`, where `version="1.0.0"` became `version="1.1.1"`. The 1.0.0 JAR embedded in the supplied source is also SHA-256 identical to the separately supplied 1.0.0 JAR. We therefore treat **1.1.1 as the current version number**, but there is no missing 1.1.1 functionality to reconstruct before transplanting the already-validated Eruruu additions.

#### Easy Farmer's Delight Compat prototype
This laboratory integration is now split into the already-active Rich Farmer/Rich Paddy Farmer harvesting layer and the **Cutter Laboratory introduced in 1.0.19**. Everything in this section is intended to migrate natively into **Easy Farmer's Delight Compat** after NeoBlock runtime validation. Eruruu Patch is deliberately the sandbox: the functional contract below matters more than preserving the temporary mixin/reflection structure used here.

##### Active Rich Farmer behavior
- Applies to **Rich Farmer and Rich Paddy Farmer** only; their output GUIs gain one dedicated Knife equipment slot accepting `#c:tools/knife`.
- The Knife is stored separately from Easy Villagers' exposed output inventory, persists with the farmer and is not damaged by harvesting. External hopper/pipe access to the normal output handler therefore cannot insert into or extract from the Knife equipment slot.
- Since 1.0.17 an empty Knife slot uses Eruruu's neutral monochrome `empty_knife_slot` equipment placeholder instead of rendering a real Iron Knife. A real Knife is only visible when one is actually equipped.
- Normal age-based crops use the equipped Knife as `LootContextParams.TOOL`; Farmer's Delight/addon loot logic decides whether the tool changes drops. Eruruu does **not** hardcode `+Straw` or any synthetic Knife bonus.
- Mature Rice likewise receives the actual equipped Knife in its harvest loot context. Runtime testing confirmed the proper Rice/byproduct behavior with a Knife equipped.
- Runtime testing also confirmed that a crop without Knife-specific loot receives no artificial byproducts just because a Knife is equipped.
- Mature Mushroom Colonies may grow normally without a Knife, but once mature they wait instead of being harvested until a valid Knife is equipped; this gate is confirmed in-game.
- The Rich Farmer Knife is persistent **equipment**, not a consumable processing tool, so harvesting does not damage it. This intentionally differs from Cutter Knife/Axe tools.
- **1.0.22 direct equip contract:** with an empty equipment slot, normal right-clicking a Rich Farmer or Rich Paddy Farmer while holding a valid Knife equips exactly one Knife immediately. If a Knife is already equipped, the interaction is not intercepted and the normal GUI opens. Sneak-right-click remains reserved for the farmer/crop/villager removal interactions.
- Creative **Pick Block / middle-click** on Paddy, Rich Farmer or Rich Paddy Farmer always returns a clean machine block with no stored Villager, crop, ropes, Knife or other block-entity payload. Normal block breaking/relocation still preserves machine contents. This deliberately prevents clone-stack duplication from becoming a survival exploit through copied block items.

##### 1.0.18 reusable farmer/tool foundation
The migration-oriented helper layer remains the basis for 1.0.19 instead of duplicating processing rules:

- **`FarmerToolSupport`** is the shared classifier. Knives use `#c:tools/knife`; axes use Minecraft's `#minecraft:axes` tag, allowing conventional modded tools that advertise normal axe semantics.
- **`HarvestResolver`** is the central decision point for Rich Farmer harvest tools and Mushroom Colony Knife gating.
- Tomato remains a **non-Knife-special harvest path**. Its existing Tomato/Rope persistent-harvest implementation is unchanged rather than receiving a fabricated Knife bonus.
- **`CuttingRecipeResolver`** resolves runtime Farmer's Delight `farmersdelight:cutting` recipes using the real input + equipped tool, rolls their own outputs, carries recipe-defined sound, and accepts Fortune. Datapacks/addons remain authoritative instead of Eruruu hardcoding foods or byproducts.
- **`AxeActionResolver`** is used only when no Cutting Board recipe exists. Its fallback order is `strip -> scrape one Copper oxidation stage -> remove wax`; unwaxing returns only the unwaxed block and never fabricates Honeycomb.
- **`OutputSimulator`** atomically simulates the complete result set first. If every output cannot fit, processing does not consume input or tool durability.

##### Cutter Laboratory — 1.0.19
The Cutter is now a real experimental block in Eruruu Patch. Its permanent home is **Easy Farmer's Delight Compat** after validation.

###### Crafting

```text
G G G
G C G
B L B
```

- `G` = Glass Pane.
- `C` = Farmer's Delight Cutting Board.
- `B` = Bricks.
- `L` = one supported work-surface block: Oak, Spruce, Birch, Jungle, Acacia, Dark Oak, Mangrove, Cherry, or Bamboo Block.
- **1.0.24 variant contract:** this remains **one recipe**, not eight. Recipe Book/JEI/EMI present the canonical Oak Cutter result while the `L` ingredient accepts/cycles the full `#eruruu_patch:cutter_logs` tag.
- The crafted Cutter remembers the exact log supplied in `L`, and the world renderer uses that species below the Cutting Board.

###### Villager contract
- The Cutter stores one real Easy Villagers `VillagerItem` and renders the contained Villager inside the enclosure.
- Any **adult Villager** can operate it; no profession is required in the laboratory build.
- A baby Villager may be stored and continues aging, but the Cutter does not process until that Villager becomes an adult.
- Right-click with a VillagerItem inserts it while empty. Sneak-right-click removes the stored Villager even when another item is held.
- The Cutter BlockItem preserves its stored Villager when broken/replaced through its block-entity data.

###### Inventory and GUI — polished in 1.0.20

The Cutter now deliberately mirrors Easy Villagers' Breeder/InputOutput layout instead of using one long machine row:

```text
        [IN][IN][IN][IN]        [Knife/Axe]

        [OUT][OUT][OUT][OUT]
```

- Exactly **4 input slots**, **1 protected tool slot**, and **4 output slots**.
- Inputs use Easy Villagers' canonical `x = 52 + 18*i, y = 20` row; outputs use the same X positions at `y = 51`.
- The Knife/Axe slot is at `x = 142, y = 20`, matching the extra Rich Farmer Knife slot's right-side placement while remaining aligned with the inputs.
- The screen reuses Easy Villagers' own `input_output.png` background, so the player inventory and Input/Output rows visually match Breeder/Converter-style screens.
- The tool slot accepts either a valid Knife (`#c:tools/knife`) or Axe (`#minecraft:axes`), maximum stack size 1.
- The empty hybrid tool slot uses the same neutral **Knife outline** approved for Rich Farmers. It remains only a placeholder even though the Cutter accepts either Knife or Axe.
- The progress bar is synchronized through normal menu data; processing remains server-authoritative and is rendered below the tool slot.
- Shift-click sends a valid Knife/Axe only to the tool slot. If one is already equipped, an extra tool stays with the player instead of being dumped into a material input slot. Other player items go to the input area. Machine slots shift-click back to the player inventory.

###### Sided automation — polished in 1.0.20
The Cutter keeps the tool **non-extractable** by automation, but the top face can now provision a replacement Knife/Axe automatically.

- `UP` exposes an **insert-only composite view**: one Knife/Axe equipment slot followed by the four material inputs.
- A Knife/Axe inserted from above is routed into the equipment slot when it is empty; processing tools are rejected from the material slots so spare tools do not clog the input inventory.
- Non-tool items inserted from above skip the tool slot naturally and fill the four inputs.
- Horizontal sides expose the four material inputs as **insert-only** and reject Knife/Axe stacks.
- `DOWN` exposes the four outputs as **extract-only**.
- No external face permits extracting the Knife/Axe, and no face permits inserting into outputs.
- GUI access still permits normal manual insertion/removal of the tool and normal input/output interaction.
- **1.0.22 direct equip contract:** normal right-click with a Knife or Axe equips exactly one tool when the Cutter tool slot is empty. If a tool is already equipped, the click is left to the Cutter and opens its GUI instead of replacing/deleting the tool. Sneak-right-click retains Villager extraction priority.

###### Processing order
The Cutter has one serial processing lane. Four input slots do **not** mean four simultaneous operations.

```text
first processable input
        |
        v
CuttingRecipeResolver
        |
   no recipe
        v
AxeActionResolver
        |
        v
OutputSimulator
        |
        v
commit operation
```

- One successful operation is attempted every **10 server ticks** (about two operations/second at normal 20 TPS).
- Input slots are scanned in order; the first processable item whose complete output set fits is processed.
- A real Farmer's Delight Cutting Board recipe always has priority over generic Axe behavior. This preserves recipe-defined extras/byproducts such as Straw when a compatible recipe provides them.
- Knife processing therefore supports meats, vegetables, Rice/Dough/etc. only when the installed Farmer's Delight/datapack/addon recipe actually declares that input + tool pair.
- Axe fallback supports normal stripping, one-step Copper scraping and wax removal. Wax removal does **not** return Honeycomb.
- Cutting Board recipe rolls receive the equipped tool's Fortune level.

###### Tool durability
Unlike Rich Farmer equipment, Cutter tools are actual processing tools:

- Every **successful** Cutter operation damages the Knife/Axe by 1 durability when the item is damageable.
- Durability goes through Minecraft's normal enchantment-aware damage path, so Unbreaking can affect the result naturally.
- A tool is never damaged when no recipe/action exists or when outputs are full.
- When the tool breaks, the protected slot becomes empty and processing stops until another valid tool is inserted.

###### Output safety and persistence
- All rolled results are simulated against all four output slots before mutating anything.
- If the complete result set cannot fit: **no input is consumed, no tool is damaged, no byproduct is dropped/lost**.
- Tool, four inputs, four outputs, Villager, current progress and the chosen log variant are saved in the Cutter block entity.
- Breaking the Cutter creates a Cutter BlockItem carrying the canonical block-entity data so its working contents can survive relocation.
- Oak is the zero-data/default log variant; old Cutters from before 1.0.24 therefore become Oak without migration work. Non-Oak empty Cutters carry only their log identity.
- **Stacking:** empty Cutters stack only with the same log species. A previously used Cutter becomes stackable again with empty Cutters of that species once it contains no Villager, tool, input, output or processing progress and is broken again. Different log species never stack together. A Cutter carrying real machine contents is forced to max stack size 1 while in item form.

###### Visual laboratory layout — polished in 1.0.20
- Uses the Easy Villagers glass-farm enclosure language.
- Brick floor/base marks it as a processing workstation rather than a crop Farmer.
- Stored Villager uses the same rear/central transform as Easy Villagers' Farmer.
- The Cutter Log uses **the exact Easy Villagers Farmer crop/workstation anchor** (`0.45` scale, two pixels forward from block center) instead of the old ad-hoc corner/foreground position. Since 1.0.24 this is the actual log species remembered from crafting, not always Oak.
- The Farmer's Delight Cutting Board is rendered exactly one local block above that scaled Log, so the Log visually acts as the table/support and the Cutting Board sits directly on top of it, analogous to stacked crop/rope rendering.
- While a Villager, valid Knife/Axe and input are present, one visual unit of the current input is rendered on top of the Cutting Board. Its presentation mirrors Farmer's Delight's own Cutting Board renderer: ordinary/flat-tagged items lie flat, while 3D block items render upright above the board. This display never removes or duplicates inventory contents; it mirrors the working material only.
- **1.0.25 closes Cutter variant polish:** Oak/Spruce/Birch/Jungle/Acacia/Dark Oak/Mangrove/Cherry/Bamboo are persisted and rendered independently while sharing one recipe entry; item tooltips, dynamic item rendering and Jade expose the stored variant clearly.

###### Migration target
When this laboratory implementation is accepted, **Easy Farmer's Delight Compat** should receive native equivalents of:

- Cutter block/item/block entity/menu/screen/renderer and recipe;
- Villager storage/aging using the target mod's direct Easy Villagers integration instead of Eruruu's reflection adapter;
- protected Knife/Axe capability layout;
- `FarmerToolSupport`, `CuttingRecipeResolver`, `AxeActionResolver`, `OutputSimulator` and the relevant harvest resolver logic;
- block-item persistence and processing rules documented above.

After that migration is validated, Eruruu's temporary Cutter and Farmer integration can be removed without changing the promised gameplay contract.

### Runtime audit status through 1.0.23
- **Validated and closed unless touched later:** THE Pick core/Auto Mining/command and level migration; Moss Helmet mass spread + Moss->Dirt; Wild Crops/Sweet Berries; Rich Farmer/Rich Paddy harvesting, Knife insertion/sync/byproducts; clean creative Pick Block clone-safety; Cutter processing/durability/rotation/display item/Villager extraction+aging/sided hopper behavior; Paddy/Rich/Rich Paddy Recipe Book discovery; Knife Harvesting viewer category; Cutter Axe Actions; and the complete temporary Stonecutter Sifting integration.
- **The only 1.0.23 viewer defect found in audit was Cutter discovery:** its crafting recipe did not appear and Cutter was not shown as a Farmer's Delight Cutting workstation/catalyst. 1.0.24 specifically replaces/fixes those paths and therefore they are the primary re-test items.
- Stonecutter Sifting is runtime-ready for migration. A direct 1.0.0-vs-1.1.1 JAR audit found no code/resource delta at all; only the declared `mods.toml` version differs, so the source currently labelled 1.0.0 remains a functionally exact code base for the published 1.1.1 behavior.


## 1.0.26 final sandbox freeze

### Rich Farmer / Rich Paddy Farmer — Jade Knife status
When Jade is installed, Rich Farmer and Rich Paddy Farmer now add one extra status line whenever their protected equipment slot contains a Knife:

```text
Knife: Iron Knife
```

Spanish locales display `Cuchillo: ...`. The line is server-backed and serializes the real equipped `ItemStack`, so modded/custom-named Knives keep their proper hover name. Normal Farmer/Paddy variants never show this line because they do not own the Knife equipment slot. Existing crop/growth/Rich Soil and output rows continue to come from Easy Farmer's Delight Compat/Jade unchanged.

### Sandbox implementation status
With 1.0.26, the Eruruu laboratory implementation is considered **feature-complete**. No additional gameplay system is planned inside the sandbox before migration. Remaining work is limited to final regression confirmation of viewer paths touched by the late 1.0.25 hotfixes, then transplanting validated features into Stonecutter Sifting and Easy Farmer's Delight Compat.

The migration order remains:

1. migrate the already-validated Stonecutter Sifting tables into the Stonecutter Sifting 1.1.1 code base;
2. migrate Rich Farmer/Rich Paddy Knife equipment + harvesting + Jade/viewer integration into Easy Farmer's Delight Compat;
3. migrate Cutter, variants, automation, recipe discovery, JEI/EMI and Jade into Easy Farmer's Delight Compat;
4. validate both destination mods independently;
5. remove the temporary Stonecutter/Farmer/Cutter integration layers from Eruruu Patch.

### Sugar Rush AFK foods
- 4 Sugar in a 2x2 crafts 1 Sugar Block crafting component.
- 8 Sugar around a Wooden Pickaxe crafts the Sugar Rush Pickaxe: edible even at full hunger, restores 2 hunger and 3 saturation, and grants Haste I for 10 minutes.
- 8 Sugar Blocks around a Wooden Pickaxe crafts the Enchanted Sugar Rush Pickaxe: same food values, Haste II for 10 minutes, and forced enchantment glint.
- Both item models layer the vanilla Sugar texture behind a Wooden Pickaxe. The Haste II version glints as a whole item.

## 1.0.25 final JEI/Jade recovery

The experimental standalone **Cutter Variants** JEI category is removed. Runtime testing showed that the extra category/subtype/creative-search experiment interfered with normal Cutter discovery, so the sandbox returns to the canonical path that was already validated: **one real Cutter crafting recipe**, driven by `#eruruu_patch:cutter_logs`, and the Cutter exposed normally for JEI indexing. Material variants remain a property of the crafted Cutter; they are not separate crafting recipes.

Jade mirrors the Rich Farmer output presentation rather than exposing the Cutter input inventory. `CutterJadeProvider` is server-backed and sends only three kinds of information: material variant, equipped Knife/Axe, and the four finished output slots. Identical output stacks are aggregated and rendered one row per product as **small item icon + stored amount + item name**, matching the visual structure Jade uses for Rich Farmer products.

The Cutter deliberately does not expose its input slots, Villager or protected tool slot as generated products. This Jade view is display-only and does not modify the Cutter's NeoForge capabilities, sided hopper rules or processing inventory.
