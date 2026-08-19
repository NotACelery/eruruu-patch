# Eruruu Patch 1.0.23 — Recipe Discovery validation

Only discovery/viewer behavior changed in this version. Gameplay mechanics already validated in 1.0.22 do not need to be repeated unless this build causes a regression.

## Build hotfix
- [ ] `build-dev.bat` reaches `BUILD SUCCESSFUL` on the corrected 1.0.23 source.
- [ ] `build-dev.log` is created and contains the full Gradle output.
- [ ] No `IShapedRecipe` compilation error remains.

## Vanilla Recipe Book
- [ ] Paddy Farmer appears with `GGG / GFG / IWI`.
- [ ] Rich Farmer appears with `GGG / GFG / BRB` using Easy Villagers Farmer in the center.
- [ ] Rich Paddy Farmer appears with `GGG / GPG / BRB` using Paddy Farmer in the center.
- [ ] Cutter appears with its existing `GGG / GCG / BLB` recipe.
- [ ] Clicking a Farmer recipe in the book places the correct ingredients.
- [ ] The actual crafted upgrade still preserves the source Farmer's stored data; the display recipe itself never becomes the authoritative craft.
- [ ] Recipes are automatically unlocked without Creative mode.

## JEI
- [ ] R/U on Paddy Farmer shows its crafting recipe.
- [ ] R/U on Rich Farmer shows its crafting recipe.
- [ ] R/U on Rich Paddy Farmer shows its crafting recipe.
- [ ] R/U on Cutter shows its crafting recipe.
- [ ] Cutter appears as a catalyst for Farmer's Delight Cutting recipes.
- [ ] Searching/using a Cutting Board recipe shows Cutter as a valid machine/catalyst.
- [ ] `Knife Harvesting` category appears and contains Rice + Brown/Red Mushroom Colony entries.
- [ ] Rich Farmer/Rich Paddy Farmer open/associate with Knife Harvesting.
- [ ] `Cutter Axe Actions` appears and shows strip/scrape/unwax transformations supported by the runtime resolver.

## EMI
- [ ] Paddy/Rich/Rich Paddy/Cutter crafting recipes appear.
- [ ] Cutter is a workstation for Farmer's Delight Cutting.
- [ ] Knife Harvesting category and Rich Farmer/Rich Paddy workstations appear.
- [ ] Cutter Axe Actions category and Cutter workstation appear.

## Safety/regression
- [ ] Recipe Book auto-placement does not create duplicate Farmer outputs/recipes.
- [ ] Paddy/Rich recipes still preserve BlockEntity data when actually crafted.
- [ ] JEI absent: game starts normally.
- [ ] EMI absent: game starts normally.
- [ ] Neither viewer installed: vanilla Recipe Book recipes still work.
