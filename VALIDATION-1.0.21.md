# Eruruu Patch 1.0.21 — Focused re-test only

The 1.0.20 audit marked every item not explicitly reported as broken as validated. Do **not** repeat the old full checklist unless a later change touches that code. Stonecutter Sifting is also closed/validated in the sandbox.

## 1. Moss Helmet spread — changed in 1.0.21

- [ ] Moss Helmet + Bone Meal on **Stone** converts the target to Moss.
- [ ] Moss Helmet + Bone Meal on **Cobblestone** converts the target to Moss.
- [ ] The same action visibly spreads Moss through nearby Stone/Cobblestone instead of changing only the clicked block.
- [ ] Moss Helmet + Bone Meal on an **existing Moss Block** next to Stone/Cobblestone runs the spread path.
- [ ] Vanilla-style Moss vegetation/patch behavior still occurs; Eruruu is not replacing it with a fake loot/placement table.
- [ ] One Bone Meal is consumed per valid Survival use; Creative consumes none.
- [ ] Without the Moss Helmet, Stone/Cobblestone has no Eruruu special Bone Meal interaction.

Moss + Hoe -> Dirt is already validated and unchanged. Wild vegetation is already validated and unchanged.

## 2. Rich Farmer / Rich Paddy Knife insertion — changed in 1.0.21

- [ ] Right-click a Rich Farmer with a valid Knife while its Knife slot is empty -> exactly one Knife equips directly.
- [ ] Same direct insertion works on Rich Paddy Farmer.
- [ ] Survival removes exactly one Knife from the held stack; Creative does not consume it.
- [ ] If a Knife is already equipped, right-click does not overwrite/delete it.
- [ ] Shift-clicking a Knife from the player inventory into the GUI equips exactly one Knife.
- [ ] Shift-clicking/removing the equipped Knife back to the player still works.

Knife synchronization, persistence, zero harvesting durability and hopper non-extraction were already validated and are unchanged.

## 3. Paddy/Cutter GUI presentation — changed in 1.0.21

- [ ] Normal Paddy Farmer shows its actual block name at the upper-left.
- [ ] Rich Farmer and Rich Paddy titles remain correct.
- [ ] Cutter shows `Cutter`/localized block name at the upper-left.
- [ ] Cutter Input/Output text is crisp, single-rendered and matches Easy Villagers' no-shadow font appearance.
- [ ] Cutter still keeps Input/Output/tool/player slots in the already-approved 1.0.20 positions.
- [ ] Empty Cutter tool slot shows the same Knife outline used by Rich Farmers, not the crossed Knife/Axe placeholder.
- [ ] Real Knife or Axe cleanly covers the placeholder.

## 4. Cutter Villager extraction — changed in 1.0.21

- [ ] Sneak-right-click with empty hand removes the stored Villager.
- [ ] Sneak-right-click while holding another item also removes the stored Villager.
- [ ] The Villager item/data is returned exactly once.
- [ ] Reinsert the returned Villager successfully.
- [ ] Baby/adult data survives extraction/reinsertion.

All Cutter processing, hoppers, recipes, durability, rotation, display item, persistence and baby incubation behavior were already validated and are unchanged.
