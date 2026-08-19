# Eruruu Patch 1.0.24 — focused runtime validation

Everything already confirmed through the 1.0.23 audit stays closed unless 1.0.24 touched that exact path. This checklist contains only the Cutter discovery/log-variant work plus regression checks directly adjacent to it.

## A. Cutter crafting discovery
- [ ] Vanilla Recipe Book shows exactly one Cutter recipe.
- [ ] JEI shows exactly one Cutter crafting recipe.
- [ ] EMI shows exactly one Cutter crafting recipe.
- [ ] The displayed result is the normal/Oak Cutter.
- [ ] The log ingredient accepts/cycles Oak, Spruce, Birch, Jungle, Acacia, Dark Oak, Mangrove and Cherry.
- [ ] Clicking/placing the recipe into a crafting grid still crafts successfully.
- [ ] No duplicate/ghost Cutter crafting recipes appear.

## B. Farmer's Delight Cutting discovery
- [ ] JEI: Cutter appears as a catalyst/workstation for Farmer's Delight's native Cutting category.
- [ ] EMI: Cutter appears as a workstation for Farmer's Delight's native Cutting category.
- [ ] Existing Farmer's Delight Cutting Board recipes remain unchanged; Eruruu does not duplicate them into a second fake category.
- [ ] Knife Harvesting category remains present.
- [ ] Cutter Axe Actions category remains present.

## C. Log variants — crafting/rendering
Craft one Cutter with each supported log:
- [ ] Oak.
- [ ] Spruce.
- [ ] Birch.
- [ ] Jungle.
- [ ] Acacia.
- [ ] Dark Oak.
- [ ] Mangrove.
- [ ] Cherry.

For each:
- [ ] Place it: the log below the Cutting Board is the species used in the craft.
- [ ] Rotate NORTH/SOUTH/EAST/WEST: existing Cutter layout remains correct.
- [ ] Break/re-place it empty: log species persists.
- [ ] Put Villager/tool/input inside, break/re-place: contents and log species both persist.

## D. Stacking contract
- [ ] Two fresh empty Oak Cutters stack.
- [ ] Two fresh empty Cutters of the same non-Oak species stack.
- [ ] Oak + Spruce do not stack.
- [ ] Any two different log species do not stack.
- [ ] A populated Cutter item is max stack size 1.
- [ ] A Cutter may be used, then completely emptied (no Villager/tool/input/output/progress), broken, and stacks again with empty Cutters of the same log species.
- [ ] That emptied used Cutter still does NOT stack with another log species.
- [ ] No contents/Villagers can be duplicated by attempting to stack populated machines.

## E. Backwards compatibility
- [ ] A Cutter made/placed before 1.0.24 loads as Oak.
- [ ] Its Villager/tool/input/output data still loads normally.
- [ ] Breaking an old empty Cutter produces the canonical Oak empty Cutter and allows normal Oak stacking.

## Already validated — do not repeat unless regression appears
THE Pick; Moss spread/Moss->Dirt; Wild Crops/Berries; Farmer/Rich Paddy Knife behavior and byproducts; tool right-click insertion; Farmer/Paddy clone safety; Cutter base processing, Knife/Axe durability, item-on-board rendering, Villager incubation/extraction, GUI layout, sided hopper automation; Stonecutter Sifting temporary loot additions; Paddy/Rich/Rich Paddy Recipe Book discovery; Knife Harvesting; Cutter Axe Actions.
