package dev.maicra.eruruupatch.blockentity;

import dev.maicra.eruruupatch.compat.easyfarmers.AxeActionResolver;
import dev.maicra.eruruupatch.compat.easyfarmers.CutterVillagerAdapter;
import dev.maicra.eruruupatch.compat.easyfarmers.CutterLogVariant;
import dev.maicra.eruruupatch.compat.easyfarmers.CuttingRecipeResolver;
import dev.maicra.eruruupatch.compat.easyfarmers.FarmerToolSupport;
import dev.maicra.eruruupatch.compat.easyfarmers.OutputSimulator;
import dev.maicra.eruruupatch.registry.ModBlockEntities;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * Laboratory implementation of the Easy Farmer's Delight Cutter.
 *
 * One adult Easy Villagers VillagerItem powers one serial processing lane. The
 * tool slot is exposed only through the UP insertion view so automation may
 * provision a Knife/Axe; no external face may extract that tool. Horizontal
 * faces are material-input only and DOWN is output extract-only.
 */
public final class CutterBlockEntity extends BlockEntity {
    public static final int PROCESS_TICKS = 10;
    public static final int INPUT_SLOTS = 4;
    public static final int OUTPUT_SLOTS = 4;

    private static final String KEY_VILLAGER = "CutterVillager";
    private static final String KEY_TOOL = "CutterTool";
    private static final String KEY_INPUT = "CutterInput";
    private static final String KEY_OUTPUT = "CutterOutput";
    private static final String KEY_PROGRESS = "CutterProgress";

    private final CutterVillagerAdapter villagerAdapter = new CutterVillagerAdapter(this);
    private ItemStack villager = ItemStack.EMPTY;
    private Block logVariant = Blocks.OAK_LOG;
    private int progress;
    private boolean loadingState;

    private final ItemStackHandler tool = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return FarmerToolSupport.isProcessingTool(stack);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (!loadingState) {
                setChangedAndSync();
            }
        }
    };

    private final ItemStackHandler input = new ItemStackHandler(INPUT_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            if (!loadingState) {
                setChangedAndSync();
            }
        }
    };

    private final ItemStackHandler output = new ItemStackHandler(OUTPUT_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            if (!loadingState) {
                setChangedAndSync();
            }
        }
    };

    private final IItemHandler topAutomation = new TopInsertHandler(tool, input);
    private final IItemHandler sideInputAutomation = new InputOnlyHandler(input, true);
    private final IItemHandler outputAutomation = new OutputOnlyHandler(output);

    public CutterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CUTTER.get(), pos, state);
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, CutterBlockEntity cutter) {
        if (cutter.hasVillager()) {
            cutter.villagerAdapter.advanceAge();
            if (level.getGameTime() % 20L == 0L) {
                cutter.villagerAdapter.flushToOwner();
                cutter.syncBlock();
            }
        }

        if (!cutter.canWork()) {
            cutter.setProgress(0);
            return;
        }

        cutter.setProgress(cutter.progress + 1);
        if (cutter.progress < PROCESS_TICKS) {
            return;
        }
        cutter.setProgress(0);
        cutter.tryProcess(level);
    }

    private boolean canWork() {
        return villagerAdapter.hasAdultVillager()
                && FarmerToolSupport.isProcessingTool(tool.getStackInSlot(0))
                && hasAnyInput();
    }

    private boolean hasAnyInput() {
        for (int slot = 0; slot < input.getSlots(); slot++) {
            if (!input.getStackInSlot(slot).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void tryProcess(ServerLevel level) {
        ItemStack equippedTool = tool.getStackInSlot(0);
        int fortune = fortuneLevel(level, equippedTool);

        for (int slot = 0; slot < input.getSlots(); slot++) {
            ItemStack source = input.getStackInSlot(slot);
            if (source.isEmpty()) {
                continue;
            }

            Optional<CuttingRecipeResolver.Result> cutting = CuttingRecipeResolver.resolve(level, source, equippedTool, fortune);
            if (cutting.isPresent()) {
                CuttingRecipeResolver.Result result = cutting.get();
                if (OutputSimulator.canFitAll(output, result.outputs())) {
                    completeOperation(level, slot, result.outputs(), result.sound().orElse(SoundEvents.VILLAGER_WORK_BUTCHER));
                    return;
                }
                continue;
            }

            Optional<AxeActionResolver.Result> axe = AxeActionResolver.resolve(source, equippedTool);
            if (axe.isPresent()) {
                AxeActionResolver.Result result = axe.get();
                List<ItemStack> outputs = List.of(result.output());
                if (OutputSimulator.canFitAll(output, outputs)) {
                    completeOperation(level, slot, outputs, result.sound());
                    return;
                }
            }
        }
    }

    private void completeOperation(ServerLevel level, int inputSlot, List<ItemStack> results, SoundEvent sound) {
        // Re-check before any mutation. OutputSimulator is the atomicity gate.
        if (!OutputSimulator.canFitAll(output, results)) {
            return;
        }

        ItemStack source = input.getStackInSlot(inputSlot);
        if (source.isEmpty()) {
            return;
        }
        source.shrink(1);
        input.setStackInSlot(inputSlot, source);

        if (!OutputSimulator.insertAll(output, results)) {
            // Should be unreachable on the server thread because nothing else can
            // mutate this inventory between the simulation and insertion.
            source.grow(1);
            input.setStackInSlot(inputSlot, source);
            return;
        }

        ItemStack equipped = tool.getStackInSlot(0);
        if (!equipped.isEmpty() && equipped.isDamageableItem()) {
            equipped.hurtAndBreak(1, level, null, broken -> level.playSound(
                    null, worldPosition, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.8F, 1.0F
            ));
            tool.setStackInSlot(0, equipped);
        }

        level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 0.8F, 1.0F);
        setChangedAndSync();
    }

    private static int fortuneLevel(ServerLevel level, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        try {
            Holder<Enchantment> fortune = level.registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.FORTUNE);
            return EnchantmentHelper.getItemEnchantmentLevel(fortune, stack);
        } catch (RuntimeException ignored) {
            return 0;
        }
    }

    public boolean isVillagerItem(ItemStack stack) {
        return villagerAdapter.isVillagerItem(stack);
    }

    public boolean hasVillager() {
        return !villager.isEmpty();
    }

    public boolean insertVillager(ItemStack stack) {
        if (hasVillager() || !isVillagerItem(stack)) {
            return false;
        }
        villager = stack.copyWithCount(1);
        villagerAdapter.reset();
        setChangedAndSync();
        return true;
    }

    public ItemStack removeVillager() {
        if (villager.isEmpty()) {
            return ItemStack.EMPTY;
        }
        villagerAdapter.flushToOwner();
        ItemStack result = villager.copyWithCount(1);
        villager = ItemStack.EMPTY;
        villagerAdapter.reset();
        setChangedAndSync();
        return result;
    }

    public ItemStack eruruu$getStoredVillager() {
        return villager;
    }

    /** Internal callback used only by the reflection adapter after aging. */
    public void eruruu$updateVillagerFromAdapter(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        villager = stack.copyWithCount(1);
        setChanged();
    }

    public CutterVillagerAdapter villagerAdapter() {
        return villagerAdapter;
    }

    public Block logVariant() {
        return logVariant;
    }

    public void setLogVariant(Block logVariant) {
        Block normalized = logVariant == null ? Blocks.OAK_LOG : logVariant;
        if (this.logVariant != normalized) {
            this.logVariant = normalized;
            setChangedAndSync();
        }
    }

    /** True when the dropped item must be non-stackable because it carries machine contents. */
    public boolean hasStoredContents() {
        if (!villager.isEmpty() || progress != 0) {
            return true;
        }
        return !handlerEmpty(tool) || !handlerEmpty(input) || !handlerEmpty(output);
    }

    public ItemStackHandler toolHandler() {
        return tool;
    }

    public ItemStackHandler inputHandler() {
        return input;
    }

    public ItemStackHandler outputHandler() {
        return output;
    }

    /**
     * Automation contract:
     * - UP: insert one Knife/Axe into the protected tool slot, or materials into inputs.
     * - DOWN: extract outputs only.
     * - sides/null: insert materials only; processing tools are rejected so they do
     *   not accidentally occupy a material slot.
     * The tool can never be extracted through an external capability.
     */
    public IItemHandler getAutomationHandler(@Nullable Direction direction) {
        if (direction == Direction.DOWN) {
            return outputAutomation;
        }
        if (direction == Direction.UP) {
            return topAutomation;
        }
        return sideInputAutomation;
    }

    /** One visual unit of the current material for the client-side Cutting Board renderer. */
    public ItemStack displayInput() {
        if (!hasVillager() || !FarmerToolSupport.isProcessingTool(tool.getStackInSlot(0))) {
            return ItemStack.EMPTY;
        }
        for (int slot = 0; slot < input.getSlots(); slot++) {
            ItemStack stack = input.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                return stack.copyWithCount(1);
            }
        }
        return ItemStack.EMPTY;
    }

    public int progress() {
        return progress;
    }

    private void setProgress(int value) {
        int normalized = Math.max(0, Math.min(PROCESS_TICKS, value));
        if (progress != normalized) {
            progress = normalized;
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        villagerAdapter.flushToOwner();
        // Variant is canonical item identity. Oak is deliberately omitted so
        // pre-1.0.24/plain Cutters remain the default Oak variant and stack with it.
        CutterLogVariant.write(tag, logVariant);
        if (!villager.isEmpty()) {
            tag.put(KEY_VILLAGER, villager.save(registries));
        }
        if (!handlerEmpty(tool)) {
            tag.put(KEY_TOOL, tool.serializeNBT(registries));
        }
        if (!handlerEmpty(input)) {
            tag.put(KEY_INPUT, input.serializeNBT(registries));
        }
        if (!handlerEmpty(output)) {
            tag.put(KEY_OUTPUT, output.serializeNBT(registries));
        }
        if (progress != 0) {
            tag.putInt(KEY_PROGRESS, progress);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        logVariant = CutterLogVariant.read(tag);
        villager = tag.contains(KEY_VILLAGER)
                ? ItemStack.parseOptional(registries, tag.getCompound(KEY_VILLAGER))
                : ItemStack.EMPTY;
        // Update packets can load into an existing client BlockEntity. Clear first
        // so omitted empty tags really mean empty instead of preserving old state.
        loadingState = true;
        try {
            clearHandler(tool);
            clearHandler(input);
            clearHandler(output);
            if (tag.contains(KEY_TOOL, Tag.TAG_COMPOUND)) {
                tool.deserializeNBT(registries, tag.getCompound(KEY_TOOL));
            }
            if (tag.contains(KEY_INPUT, Tag.TAG_COMPOUND)) {
                input.deserializeNBT(registries, tag.getCompound(KEY_INPUT));
            }
            if (tag.contains(KEY_OUTPUT, Tag.TAG_COMPOUND)) {
                output.deserializeNBT(registries, tag.getCompound(KEY_OUTPUT));
            }
        } finally {
            loadingState = false;
        }
        progress = Math.max(0, Math.min(PROCESS_TICKS, tag.getInt(KEY_PROGRESS)));
        villagerAdapter.reset();
    }

    private static boolean handlerEmpty(ItemStackHandler handler) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            if (!handler.getStackInSlot(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static void clearHandler(ItemStackHandler handler) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            handler.setStackInSlot(slot, ItemStack.EMPTY);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    private void setChangedAndSync() {
        setChanged();
        syncBlock();
    }

    private void syncBlock() {
        Level level = getLevel();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    /** Material-only insertion view used by the horizontal faces. */
    private static final class InputOnlyHandler implements IItemHandler {
        private final IItemHandler delegate;
        private final boolean rejectProcessingTools;

        private InputOnlyHandler(IItemHandler delegate, boolean rejectProcessingTools) {
            this.delegate = delegate;
            this.rejectProcessingTools = rejectProcessingTools;
        }

        @Override
        public int getSlots() {
            return delegate.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return delegate.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (rejectProcessingTools && FarmerToolSupport.isProcessingTool(stack)) {
                return stack;
            }
            return delegate.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return delegate.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return (!rejectProcessingTools || !FarmerToolSupport.isProcessingTool(stack))
                    && delegate.isItemValid(slot, stack);
        }
    }

    /**
     * Five-slot insertion view for the top face: slot 0 is the Knife/Axe and
     * slots 1-4 are material inputs. Tools are never allowed to spill into the
     * material slots if the equipment slot is already occupied.
     */
    private static final class TopInsertHandler implements IItemHandler {
        private final IItemHandler tool;
        private final IItemHandler input;

        private TopInsertHandler(IItemHandler tool, IItemHandler input) {
            this.tool = tool;
            this.input = input;
        }

        @Override
        public int getSlots() {
            return 1 + input.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return slot == 0 ? tool.getStackInSlot(0) : input.getStackInSlot(slot - 1);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot == 0) {
                return tool.insertItem(0, stack, simulate);
            }
            if (slot < 1 || slot >= getSlots() || FarmerToolSupport.isProcessingTool(stack)) {
                return stack;
            }
            return input.insertItem(slot - 1, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 0 ? tool.getSlotLimit(0) : input.getSlotLimit(slot - 1);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) {
                return tool.isItemValid(0, stack);
            }
            return slot >= 1 && slot < getSlots()
                    && !FarmerToolSupport.isProcessingTool(stack)
                    && input.isItemValid(slot - 1, stack);
        }
    }

    private static final class OutputOnlyHandler implements IItemHandler {
        private final IItemHandler delegate;

        private OutputOnlyHandler(IItemHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public int getSlots() {
            return delegate.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return delegate.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return delegate.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return delegate.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    }
}
