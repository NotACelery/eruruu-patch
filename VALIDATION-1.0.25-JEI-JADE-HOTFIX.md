# Eruruu Patch 1.0.25 — JEI/Jade recovery hotfix validation

Only these points changed in this hotfix.

## JEI

- [ ] Searching `Cutter` shows the Cutter again in JEI.
- [ ] The Oak Cutter no longer needs normal Creative Search parent membership to be visible in JEI.
- [ ] `Cutter Variants` still exists.
- [ ] It shows all 9 entries: Oak, Spruce, Birch, Jungle, Acacia, Dark Oak, Mangrove, Cherry, Bamboo.
- [ ] Each variant is treated as a distinct JEI subtype.
- [ ] The normal Cutter crafting recipe remains visible.
- [ ] Cutter is still a catalyst/workstation for Farmer's Delight Cutting recipes.
- [ ] `Cutter Axe Actions` still exists.
- [ ] No duplicate/fake crafting recipes were introduced.

## Creative Search

- [ ] Oak still does not show the extra blue `Eruruu Patch` line between its name and `Variante`.
- [ ] All nine variants remain visible in Creative Search.

## Jade

With Jade installed on both sides for multiplayer/server data:

- [ ] Looking at a Cutter shows its material variant.
- [ ] Equipped Knife/Axe is shown.
- [ ] Empty outputs show `Productos generados: ninguno`.
- [ ] Process one product and confirm its name + stored count appears.
- [ ] Process enough to increase the stored stack and confirm the number updates.
- [ ] If the same product occupies multiple output slots, Jade shows the aggregated total.
- [ ] Hopper extraction decreases the displayed amount.
- [ ] Removing all outputs returns to the empty message.

## Regression

- [ ] Cutter processing unchanged.
- [ ] Variant rendering unchanged.
- [ ] Variant stacking unchanged.
- [ ] Hopper sided behavior unchanged.
- [ ] Pick Block variant preservation unchanged.
