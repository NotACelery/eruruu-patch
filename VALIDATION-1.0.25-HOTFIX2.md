# Eruruu Patch 1.0.25 — Hotfix 2 validation

Only these points changed. Previously validated Cutter processing, stacking, Jade, world rendering, recipes and automation do not need to be retested unless one of these checks exposes a regression.

## Creative tooltip/search
- [ ] Open Eruruu Patch's own Creative tab: the canonical Oak Cutter is still present.
- [ ] Search `Cutter` in vanilla Creative Search: all 9 variants appear (Oak, Spruce, Birch, Jungle, Acacia, Dark Oak, Mangrove, Cherry, Bamboo).
- [ ] Hover Oak in Creative Search: there is only one normal `Eruruu Patch` mod-name line; no second blue Creative-category line appears between the item name and `Variant: Oak`.
- [ ] Hover several non-Oak variants: tooltip structure matches Oak apart from the variant name/component count.

## JEI — Cutter Variants
- [ ] Looking up Cutter exposes a separate `Cutter Variants` category.
- [ ] Category contains exactly 9 entries.
- [ ] Each entry visually reads `material -> Cutter`.
- [ ] Inputs are respectively Oak Log, Spruce Log, Birch Log, Jungle Log, Acacia Log, Dark Oak Log, Mangrove Log, Cherry Log and Bamboo Block.
- [ ] Every output tooltip reports the matching Cutter variant.
- [ ] Variant outputs use their correct dynamic item icon/material.
- [ ] The ordinary crafting recipe still appears only once and still accepts the shared `#eruruu_patch:cutter_logs` ingredient.
- [ ] No nine duplicate crafting recipes were created.

## Regression smoke check
- [ ] Craft one non-Oak Cutter and confirm it still stores the chosen variant.
- [ ] Creative Pick Block on that placed Cutter still keeps only its variant, not Villager/tool/inventories/progress.
