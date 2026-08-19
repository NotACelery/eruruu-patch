# Eruruu Patch 1.0.22 — Focused re-test only

Everything not listed here was reported as working in the 1.0.20/1.0.21 runtime audit and should **not** be re-tested unless later code changes touch it. Moss spread is now validated. Stonecutter Sifting is fully validated and closed for sandbox testing.

## 1. Direct right-click equipment

### Rich Farmer
- [ ] Empty Knife slot + normal right-click holding a valid Knife -> exactly one Knife equips immediately and GUI does not need to be opened first.
- [ ] Occupied Knife slot + right-click holding another Knife -> existing Knife remains untouched and the Rich Farmer GUI opens normally.
- [ ] Survival consumes exactly one held Knife when insertion succeeds.
- [ ] Creative does not consume the held Knife.
- [ ] Sneak-right-click with a Knife still follows normal Farmer removal interactions instead of forcing tool insertion.

### Rich Paddy Farmer
- [ ] Same five checks as Rich Farmer.
- [ ] Normal Paddy Farmer does **not** accept a Knife because it has no Knife equipment slot.

### Cutter
- [ ] Empty tool slot + right-click holding Knife -> exactly one Knife equips immediately.
- [ ] Empty tool slot + right-click holding Axe -> exactly one Axe equips immediately.
- [ ] Occupied tool slot + right-click holding Knife/Axe -> current tool remains untouched and Cutter GUI opens normally.
- [ ] Survival consumes exactly one tool on successful insertion; Creative consumes none.
- [ ] Sneak-right-click still prioritizes Villager extraction and does not equip the held tool.

## 2. Creative Pick Block clone safety

For each of **Paddy Farmer, Rich Farmer and Rich Paddy Farmer**:

- [ ] Populate it with a Villager and crop; add Knife/ropes where that variant supports them.
- [ ] Middle-click / Pick Block in Creative.
- [ ] Place the cloned block somewhere else.
- [ ] The clone must be completely clean: no Villager, no crop, no ropes, no Knife and no carried machine progress/content.
- [ ] The original populated block must remain unchanged.
- [ ] Breaking the original normally and replacing its dropped block must **still preserve** its legitimate contents.

## Already closed
- THE Pick / Auto Mining / givepick level handling.
- Moss Helmet spread and Moss->Dirt.
- Wild vegetation.
- Rich Farmer/Rich Paddy harvesting, Rice, Mushroom Knife gating and Knife durability/sync.
- Cutter GUI, renderer, recipes, durability, Villager aging/extraction, display item and hopper sided behavior.
- Complete Stonecutter Sifting laboratory integration.
