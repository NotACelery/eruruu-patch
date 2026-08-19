# Eruruu Patch 1.0.25 — final JEI/Jade hotfix validation

Only re-test the paths touched by this hotfix.

## JEI
- [ ] Search `Cutter`: the Cutter appears in JEI's ingredient list.
- [ ] Open the Cutter crafting recipe: the single real `GGG / GCG / BLB` recipe appears.
- [ ] The log ingredient accepts/cycles the supported Cutter materials, including Bamboo Block.
- [ ] There is **no** standalone `Cutter Variants` category/tab anymore.
- [ ] `Cutter Axe Actions` still appears.
- [ ] The Cutter can still be reached as a catalyst/workstation for Farmer's Delight Cutting recipes.
- [ ] No duplicate or ghost Cutter crafting recipes appear.

## Jade
- [ ] Looking at a Cutter still shows its material variant.
- [ ] Looking at a Cutter with a Knife/Axe still shows the equipped tool.
- [ ] Finished products are displayed with Jade's Rich-Farmer-style item-storage rows: small item icon + stored amount + item name.
- [ ] Multiple non-empty output slots are all represented.
- [ ] Removing output with a hopper lowers/disappears the Jade amounts accordingly.
- [ ] Input items and the protected Knife/Axe are **not** listed as generated products.
- [ ] Hopper sidedness and Cutter processing behavior are unchanged.

## Regression guard
- [ ] Cutter log/Bamboo variants still craft/place/render correctly.
- [ ] Same-empty-variant stacking still works; different variants still do not stack.
- [ ] Pick Block still preserves only the Cutter variant and does not clone machine contents.
