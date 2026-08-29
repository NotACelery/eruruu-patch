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

### Filtered Hopper
- Crafted from a vanilla Hopper, String, Redstone and a Comparator.
- Keeps the normal five Hopper storage slots plus one dedicated filter slot.
- The filter stores one representative item and matches by Item ID only; damage, custom names, enchantments and other components do not affect the match.
- The filter item is not consumed and is not part of the Hopper's normal container storage.
- Incoming automation respects the filter. Outgoing extraction remains unrestricted.
- Automation cannot insert into or remove the filter item.
- Comparators and normal Hopper storage behavior continue to use the five storage slots.
- Jade can display a compact server-backed summary of contents and filter state when installed.

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

JEI, EMI and Jade integrations are optional at runtime. **Ars Nouveau is also optional**; when present, Eruruu only adds the Magebloom composting QoL described above.

**Stonecutter Sifting and Easy Farmer's Delight Compat are not dependencies of Eruruu Patch.** They can be installed alongside it in the OneBlock pack, but 1.2.0 contains no runtime integration with either project.

## Development
This project targets Java 21 and ModDevGradle. Use `build.bat` from the repository root for the supported Windows build path.

Technical architecture, invariants and maintenance rules live in [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md). The current release regression checklist lives in [`docs/TESTING-1.2.0.md`](docs/TESTING-1.2.0.md).

The source tree intentionally keeps implementation comments to a minimum. Design rationale belongs in the documentation so runtime classes remain readable without losing the reasons behind progression, compatibility or safety rules.

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
