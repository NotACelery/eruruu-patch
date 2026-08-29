# Eruruu Patch 1.2.0 — Regression Checklist

Use this checklist against the JAR built from the final 1.2.0 source.

## Build and startup

- [ ] Run `build.bat` with Java 21.
- [ ] Build completes without compile errors.
- [ ] Produced JAR reports version 1.2.0.
- [ ] Dedicated/server-side class loading does not touch client-only classes.
- [ ] Existing 1.1.x/1.0.x test world opens without registry-remap errors.

## Required dependencies

- [ ] Minecraft 1.21.1.
- [ ] NeoForge 21.1.235 or newer within the declared range.
- [ ] Farmer's Delight 1.3.2+.
- [ ] Argentum 1.0.0+.

## Optional integrations

- [ ] Game starts without JEI.
- [ ] Game starts without EMI.
- [ ] Game starts without Jade.
- [ ] Game starts without Ars Nouveau.
- [ ] JEI integration loads when JEI is installed.
- [ ] EMI integration loads when EMI is installed.
- [ ] Jade integration loads when Jade is installed.
- [ ] Magebloom composting compatibility appears only when the relevant item exists.

## Charcoal Block

- [ ] 9 Charcoal craft one Charcoal Block.
- [ ] One Charcoal Block unpacks into 9 Charcoal.
- [ ] Block places correctly.
- [ ] Block mines with expected Coal Block-like behavior.
- [ ] Block drops itself.
- [ ] Texture/model render correctly.
- [ ] Furnace accepts it as fuel.
- [ ] Burn time is 16,000 ticks.

## Dense Charcoal

- [ ] 9 Charcoal Blocks craft one Dense Charcoal.
- [ ] Result uses the existing `eruruu_patch:endless_charcoal` registry ID.
- [ ] Display name is Dense Charcoal.
- [ ] Item is non-stackable.
- [ ] Item has permanent glint.
- [ ] Furnace accepts it.
- [ ] Burn duration remains `Integer.MAX_VALUE`.
- [ ] Existing legacy Endless Charcoal stack from an older world remains valid.

## Charcoal viewer regression

- [ ] Vanilla Recipe Book exposes normal charcoal conversions where applicable.
- [ ] JEI shows Charcoal -> Charcoal Block.
- [ ] JEI shows Charcoal Block -> Charcoal.
- [ ] JEI shows Charcoal Blocks -> Dense Charcoal.
- [ ] No duplicate JEI entries.
- [ ] EMI shows the same three conversions.
- [ ] No duplicate EMI entries.

## Blank Spawn Egg acquisition

- [ ] 9 Chicken Eggs craft one Blank Spawn Egg.
- [ ] Farmer Journeyman trade is guaranteed.
- [ ] Trade is 10 Emeralds -> 1 Blank Spawn Egg before discounts/demand.
- [ ] Trade has four uses before restock.
- [ ] Vanilla Journeyman Cookie sale is the listing replaced.
- [ ] Useful Melon trade remains available.
- [ ] Curing/reputation can reduce the recovery trade to 2 Emeralds.
- [ ] Price does not fall below 2 Emeralds.
- [ ] Repeated use can still increase price through normal demand.

## Animal Spawn Egg recipes

Verify each output is the real vanilla Spawn Egg:

- [ ] Chicken — Wheat Seeds.
- [ ] Cow — Wheat.
- [ ] Pig — Carrots.
- [ ] Horse — Golden Carrots.
- [ ] Donkey — Hay Bales.
- [ ] Rabbit — seven Carrots plus one Dandelion.
- [ ] Wolf — Bones.
- [ ] Cat — raw Cod.
- [ ] Bee — flowers tag.
- [ ] Fox — Sweet Berries.
- [ ] Panda — Bamboo.
- [ ] Turtle — vanilla Seagrass in the current 1.2.0 source.
- [ ] Armadillo — Spider Eyes.
- [ ] Camel — Cactus.
- [ ] Strider — Warped Fungus.
- [ ] Hoglin — Crimson Fungus.

## Sheep recipes

- [ ] All 16 Wool colors have recipes.
- [ ] Eight matching Wool blocks plus Blank Spawn Egg craft successfully.
- [ ] Mixed Wool colors do not match.
- [ ] Spawned Sheep uses the crafted color.
- [ ] Result remains a vanilla Sheep Spawn Egg carrying entity data.

## Renewable recipes

- [ ] Bamboo -> Paper.
- [ ] Cobblestone -> Sand in Stonecutter.
- [ ] Cobblestone + Dirt -> Gravel.
- [ ] Coarse Dirt -> Dirt.
- [ ] Bone Blocks + Sand -> Soul Sand.
- [ ] Allowed raw meats + Cobblestone -> Netherrack.
- [ ] Cooked foods do not satisfy the Netherrack recipe.
- [ ] Short Grass -> Fertilizer.
- [ ] Sapling tag + Sticks -> Dead Bush.
- [ ] Crimson Roots -> Crimson Culture.
- [ ] Warped Roots -> Warped Culture.
- [ ] Moss Blocks -> Moss Helmet.
- [ ] Sugar -> Sugar Block.
- [ ] Sugar + Wooden Pickaxe -> Sugar Rush Pickaxe.
- [ ] Sugar Blocks + Wooden Pickaxe -> Enchanted Sugar Rush Pickaxe.
- [ ] Filtered Hopper recipe loads.

## Nether cultures

- [ ] Crimson Culture converts Netherrack to Crimson Nylium.
- [ ] Warped Culture converts Netherrack to Warped Nylium.
- [ ] Survival consumes one culture.
- [ ] Creative does not consume it.
- [ ] Normal Bone Meal works after Nylium exists.

## Fertilizer

- [ ] Use on Dirt converts it to Grass Block.
- [ ] Survival consumes one.
- [ ] Creative does not.
- [ ] Invalid target does not consume item.

## Surface Bone Meal additions

- [ ] Bone Meal on Grass still performs normal vanilla vegetation generation.
- [ ] Generated Short Grass can become Wild Cabbage.
- [ ] Generated Short Grass can become Wild Onion.
- [ ] Generated Short Grass can become Wild Tomato.
- [ ] Replacement behavior does not modify unrelated blocks.
- [ ] Sweet Berry post-pass can replace remaining Short Grass.
- [ ] Existing vegetation outside the generated candidate set is not rewritten.

## Wild Rice

- [ ] Shallow-water Dirt Bone Meal keeps normal vanilla aquatic behavior.
- [ ] Eligible generated Seagrass can become Wild Rice.
- [ ] Deep/invalid water setup does not use the special path incorrectly.

## Starter mushrooms

Build the documented damp/dark room:

- [ ] Dirt floor.
- [ ] two blocks of air.
- [ ] solid ceiling.
- [ ] source water immediately above ceiling.
- [ ] local light <= 7.

Then verify:

- [ ] valid attempt consumes Bone Meal.
- [ ] failures still consume Bone Meal.
- [ ] successful output is Brown or Red Mushroom.
- [ ] invalid room does not run the special mushroom attempt.

## Moss Helmet

- [ ] Equips as Leather helmet behavior.
- [ ] Item appearance is moss-green.
- [ ] Bone Meal on Cobblestone creates Moss behavior.
- [ ] Bone Meal on Stone follows intended Moss behavior.
- [ ] Survival consumes Bone Meal.
- [ ] Creative does not.
- [ ] Spread remains local.
- [ ] Hoe on Moss while wearing helmet converts it to Dirt.
- [ ] Hoe loses one durability.
- [ ] Without helmet the special Hoe interaction does not run.

## Sugar Rush foods

- [ ] Sugar Rush Pickaxe restores 2 hunger.
- [ ] Saturation behavior matches configured 0.75 modifier.
- [ ] Grants Haste I for ten minutes.
- [ ] Enchanted Sugar Rush Pickaxe grants Haste II for ten minutes.
- [ ] Enchanted variant keeps glint.
- [ ] Both remain usable as food, not mining tools.

## Extra mob loot

### Zombie

- [ ] Gold Ingot bonus can occur on valid player kills.
- [ ] Looting scaling behaves as documented.
- [ ] Easy Mob Farm compatibility does not produce duplicate Gold rolls.

### Witch

- [ ] Nether Wart bonus works.
- [ ] Blaze Rod bonus works.
- [ ] Looting increases both according to their configured scaling.
- [ ] Easy Mob Farm path works where expected.

## Filtered Hopper

### UI

- [ ] GUI opens.
- [ ] Five storage slots render.
- [ ] Dedicated filter slot renders.
- [ ] Filter tooltip/hint is localized.
- [ ] Shift-click does not configure the filter.

### Matching

- [ ] Empty filter accepts normal input.
- [ ] Configured filter accepts matching Item ID.
- [ ] Configured filter rejects different Item ID.
- [ ] Different damage/components on same Item ID still match.
- [ ] Filter sample is not consumed by normal transfer.

### Automation

- [ ] Hopper/pipes can access five storage slots.
- [ ] Automation cannot access filter slot.
- [ ] Incoming automation respects filter.
- [ ] Outgoing extraction is not restricted by filter.
- [ ] Redstone disable/enable follows Hopper behavior.
- [ ] Comparator reads storage, not filter.

### Persistence

- [ ] Save/reload preserves storage.
- [ ] Save/reload preserves filter.
- [ ] Breaking the block returns the filter item as intended.

## Jade

- [ ] Vanilla Hopper summary renders when Jade is installed.
- [ ] Filtered Hopper summary renders.
- [ ] Filter line shows configured item.
- [ ] Empty filter shows None.
- [ ] Contents group correctly.
- [ ] More-items overflow line appears when needed.
- [ ] No Jade installation does not break startup.

## THE Pick fusion

- [ ] Two identical repaired eligible pickaxes fuse.
- [ ] Level 1 ordinary pickaxe + level 1 ordinary pickaxe produces level 2 THE Pick.
- [ ] Level 10 + level 5 produces level 15.
- [ ] Result above level 30 is rejected.
- [ ] Damaged fusion inputs are rejected.
- [ ] Ineligible special tool blacklist is respected.
- [ ] Ordinary blacklisted tools retain vanilla anvil behavior instead of being globally blocked.

## THE Pick repair

- [ ] Original repair material works.
- [ ] Wrong repair material does not use Eruruu's special repair path.
- [ ] One material repairs approximately one quarter of base-pickaxe durability.
- [ ] Enlarged reinforced max durability does not multiply per-material repair.
- [ ] Repair XP remains low as designed.

## THE Pick enchanting

- [ ] Reinforced tool can use Enchanting Table.
- [ ] Compatible enchanted books can apply in anvil.
- [ ] Fusion restrictions are not bypassed by enchantment compatibility.

## THE Pick legacy migration

Test at least one older reinforced stack:

- [ ] existing stack is recognized.
- [ ] authoritative represented-unit level is preserved.
- [ ] old fusion metadata does not override valid unit metadata.
- [ ] max durability normalizes correctly.
- [ ] custom/translated prefix does not duplicate.
- [ ] save/reload remains stable.

## Admin command

- [ ] `/eruruu givepick minecraft:diamond_pickaxe 10` works for operator.
- [ ] level 1 returns ordinary base pickaxe.
- [ ] levels 2-30 produce THE Pick.
- [ ] modded normal PickaxeItem IDs autocomplete/work.
- [ ] blacklisted/ineligible tools are rejected cleanly.

## THE Pick Auto Mining

- [ ] Reinforced pickaxe can activate with world left click.
- [ ] ordinary pickaxe cannot activate.
- [ ] camera movement redirects mining.
- [ ] camera rotation does not cancel.
- [ ] temporary air pauses and resumes.
- [ ] player movement cancels.
- [ ] removing THE Pick cancels.
- [ ] death/disconnect/dimension change clears active state.
- [ ] opening inventory after activation does not incorrectly toggle off.
- [ ] clicking inventory/JEI/EMI/chat does not toggle.
- [ ] ON indicator hides while a screen is open and returns afterward.
- [ ] second world left click toggles off.

## Locales

For `en_us`, `es_cl`, `es_es`, `es_mx`, `es_ar`:

- [ ] identical key sets.
- [ ] Blank Spawn Egg text exists.
- [ ] Filtered Hopper text exists.
- [ ] Dense Charcoal display name exists.
- [ ] THE Pick text exists.
- [ ] viewer category text exists.

## Decoupling regression

With companion mods absent:

- [ ] Eruruu Patch starts.
- [ ] no Stonecutter Sifting classes are referenced.
- [ ] no Easy Farmer's Delight Compat classes are referenced.
- [ ] no old Cutter registry alias appears.

With companion mods present:

- [ ] no duplicate Eruruu implementation appears.
- [ ] no duplicate Cutter/Stonecutter Sifting mechanics appear from Eruruu.
- [ ] recipes/viewers remain distinct.

## Final packaging

- [ ] `build-dev.bat` is absent.
- [ ] `build-dev.log` is absent.
- [ ] `PATCH-NOTES.txt` is absent.
- [ ] `build.bat` is present.
- [ ] `.editorconfig` is present.
- [ ] `.gitattributes` is present.
- [ ] `docs/DEVELOPMENT.md` is present.
- [ ] no generated `build.log` is packaged.
- [ ] `cleanup-obsolete-files.bat` self-deletes after migration cleanup.
