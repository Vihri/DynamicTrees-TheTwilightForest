package maxhyper.dttwilightforest.blocks;

import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.ferreusveritas.dynamictrees.block.branch.ThickBranchBlock;
import maxhyper.dttwilightforest.trees.MagicFamily;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.PacketDistributor;
import twilightforest.TFSounds;
import twilightforest.client.particle.TFParticleType;
import twilightforest.data.tags.EntityTagGenerator;
import twilightforest.item.OreMagnetItem;
import twilightforest.network.ChangeBiomePacket;
import twilightforest.network.ParticlePacket;
import twilightforest.network.TFPacketHandler;
import twilightforest.util.WorldUtil;
import twilightforest.world.registration.biomes.BiomeKeys;

import java.util.*;

public class MagicCoreBranchBlock extends ThickBranchBlock {

    protected static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public enum coreType {
        TIME,
        TRANSFORMATION,
        SORTING,
        MINING
    }
    private final coreType type;

    public static int tickRate = 20;

    public MagicCoreBranchBlock(ResourceLocation name, Properties properties, coreType type) {
        super(name, properties);
        this.type = type;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(ACTIVE));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        InteractionResult result = super.use(state, world, pos, player, handIn, hit);

        if (result != InteractionResult.SUCCESS){
            if (!state.getValue(ACTIVE)) {
                world.setBlockAndUpdate(pos, state.setValue(ACTIVE, true));
                world.scheduleTick(pos, this, tickRate);
                return InteractionResult.SUCCESS;
            } else if (state.getValue(ACTIVE)) {
                world.setBlockAndUpdate(pos, state.setValue(ACTIVE, false));
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        }
        return result;
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random rand) {
        if (!world.isClientSide && (Boolean)state.getValue(ACTIVE)) {
            this.playSound(world, pos, rand);
            this.performTreeEffect(world, pos, rand);
            world.scheduleTick(pos, this, tickRate);
        }
    }

    protected void performTreeEffect(Level world, BlockPos pos, Random rand){
        switch (type){
            case TIME -> performTimeEffect(world, pos, rand);
            case MINING -> performMineEffect(world, pos, rand);
            case SORTING -> performSortEffect(world, pos, rand);
            case TRANSFORMATION -> performTransEffect(world, pos, rand);
        }
    };

    protected void playSound(Level level, BlockPos pos, Random rand) {
        switch (type){
            case TIME -> level.playSound(null, pos, TFSounds.TIME_CORE, SoundSource.BLOCKS, 0.1F, 0.5F);
            case TRANSFORMATION -> level.playSound(null, pos, TFSounds.TRANSFORMATION_CORE, SoundSource.BLOCKS, 0.1F, rand.nextFloat() * 2F);
        }
    }

    protected void performMineEffect(Level level, BlockPos pos, Random rand){
        BlockPos dPos = WorldUtil.randomOffset(rand, pos, 32);
        int moved = OreMagnetItem.doMagnet(level, pos, dPos);
        if (moved > 0) {
            level.playSound(null, pos, TFSounds.MAGNET_GRAB, SoundSource.BLOCKS, 0.1F, 1.0F);
        }
    }
    protected void performSortEffect(Level level, BlockPos pos, Random rand){
        Map<IItemHandler, Vec3> inputHandlers = new HashMap<>();
        Map<IItemHandler, Vec3> outputHandlers = new HashMap<>();

        for (BlockPos blockPos : WorldUtil.getAllAround(pos, 16)) {
            if (!blockPos.equals(pos)) {
                BlockEntity blockEntity = level.getBlockEntity(blockPos);

                if (blockEntity != null) {
                    blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
                        if (Math.abs(blockPos.getX() - pos.getX()) <= 2 && Math.abs(blockPos.getY() - pos.getY()) <= 2 && Math.abs(blockPos.getZ() - pos.getZ()) <= 2) {
                            inputHandlers.put(iItemHandler, Vec3.upFromBottomCenterOf(blockPos, 1.9D));
                        } else outputHandlers.put(iItemHandler, Vec3.upFromBottomCenterOf(blockPos, 1.9D));
                    });
                }
            }
        }

        level.getEntities((Entity)null, new AABB(pos).inflate(2), entity -> entity.isAlive() && entity.getType().is(EntityTagGenerator.SORTABLE_ENTITIES)).forEach(entity ->
                entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(iItemHandler ->
                        inputHandlers.put(iItemHandler, entity.position().add(0D, entity.getBbHeight() + 0.9D, 0D))));

        if (inputHandlers.isEmpty()) return;

        level.getEntities((Entity)null, new AABB(pos).inflate(16), entity -> entity.isAlive() && entity.getType().is(EntityTagGenerator.SORTABLE_ENTITIES)).forEach(entity ->
                entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(iItemHandler -> {
                    if (!inputHandlers.containsKey(iItemHandler)) outputHandlers.put(iItemHandler, entity.position().add(0D, entity.getBbHeight() + 0.9D, 0D));
                }));

        if (outputHandlers.isEmpty()) return;

        for (IItemHandler inputIItemHandler : inputHandlers.keySet()) {
            for (int i = 0; i < inputIItemHandler.getSlots(); i++) {
                ItemStack inputStack = inputIItemHandler.extractItem(i, 1, true);
                if (!inputStack.isEmpty()) {
                    boolean transferred = false;

                    Map<Integer, IItemHandler> outputsByCount = new HashMap<>();

                    for (IItemHandler outputIItemHandler : outputHandlers.keySet()) {
                        int count = 0;
                        for (int j = 0; j < outputIItemHandler.getSlots(); j++) {
                            ItemStack stack = outputIItemHandler.getStackInSlot(j);
                            if (stack.is(inputStack.getItem())) count += stack.getCount();
                        }
                        if (count > 0) outputsByCount.put(count, outputIItemHandler);
                    }

                    for (Integer count : outputsByCount.keySet().stream().sorted(Comparator.comparingInt(Integer::intValue).reversed()).toList()) {
                        IItemHandler outputIItemHandler = outputsByCount.get(count);
                        int firstProperStack = -1;
                        for (int j = 0; j < outputIItemHandler.getSlots(); j++) {
                            ItemStack outputStack = outputIItemHandler.getStackInSlot(j);

                            if (firstProperStack == -1 && outputStack.isEmpty()) {
                                firstProperStack = j; //We reference the index of the first empty slot, in case there is no stacks that aren't at max size
                            } else if (ItemStack.isSameItemSameTags(inputStack, outputStack)
                                    && outputStack.getCount() < outputStack.getMaxStackSize()
                                    && outputStack.getCount() < outputIItemHandler.getSlotLimit(j)) {
                                firstProperStack = j;
                                break;
                            }
                        }
                        if (firstProperStack != -1) { //If there weren't any non-full stacks, we transfer to an empty space instead
                            ItemStack newStack = inputIItemHandler.extractItem(i, 1, false);
                            if (!newStack.isEmpty() && outputIItemHandler.insertItem(firstProperStack, newStack, true).isEmpty()) {//TODO Check
                                outputIItemHandler.insertItem(firstProperStack, newStack, false);
                                transferred = true;

                                Vec3 xyz = outputHandlers.get(outputIItemHandler);
                                Vec3 diff = inputHandlers.get(inputIItemHandler).subtract(xyz);

                                for (ServerPlayer serverplayer : ((ServerLevel)level).players()) {//This is just particle math, we send a particle packet to every player in range
                                    if (serverplayer.distanceToSqr(xyz) < 4096.0D) {
                                        ParticlePacket particlePacket = new ParticlePacket();
                                        double x = diff.x - 0.25D + rand.nextDouble() * 0.5D;
                                        double y = diff.y - 1.75D + rand.nextDouble() * 0.5D;
                                        double z = diff.z - 0.25D + rand.nextDouble() * 0.5D;
                                        particlePacket.queueParticle(TFParticleType.SORTING_PARTICLE.get(), false, xyz, new Vec3(x, y, z).scale(1D / diff.length()));
                                        TFPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverplayer), particlePacket);
                                    }
                                }
                                break;
                            }
                        }
                    }
                    if (transferred) break;
                }
            }
        }
    }
    protected void performTimeEffect(Level world, BlockPos pos, Random rand){
        int numticks = 8 * 3 * tickRate;

        for (int i = 0; i < numticks; i++) {

            BlockPos dPos = WorldUtil.randomOffset(rand, pos, 16);

            BlockState state = world.getBlockState(dPos);

            if (state.isRandomlyTicking()) {
                state.randomTick((ServerLevel) world, dPos, rand);
            }

            BlockEntity entity = world.getBlockEntity(dPos);
            if (entity != null) {
                BlockEntityTicker<BlockEntity> ticker = state.getTicker(world, (BlockEntityType<BlockEntity>) entity.getType());
                if (ticker != null)
                    ticker.tick(world, dPos, state, entity);
            }
        }
    }
    protected void performTransEffect(Level world, BlockPos pos, Random rand){
        ResourceKey<Biome> target = BiomeKeys.ENCHANTED_FOREST;
        Holder<Biome> biome = world.registryAccess().ownedRegistryOrThrow(Registry.BIOME_REGISTRY).getHolderOrThrow(target);
        for (int i = 0; i < 16; i++) {
            BlockPos dPos = WorldUtil.randomOffset(rand, pos, 16, 0, 16);
            if (dPos.distSqr(pos) > 256.0)
                continue;

            if (world.getBiome(dPos).is(target))
                continue;

            int minY = QuartPos.fromBlock(world.getMinBuildHeight());
            int maxY = minY + QuartPos.fromBlock(world.getHeight()) - 1;

            int x = QuartPos.fromBlock(dPos.getX());
            int z = QuartPos.fromBlock(dPos.getZ());

            LevelChunk chunkAt = world.getChunk(dPos.getX() >> 4, dPos.getZ() >> 4);
            for (LevelChunkSection section : chunkAt.getSections()) {
                for (int dy = minY; dy < maxY; dy++) { // TODO: This probably isn't correct and isn't good for performance.
                    int y = Mth.clamp(QuartPos.fromBlock(dy), minY, maxY);
                    if (section.getBiomes().get(x & 3, y & 3, z & 3).is(target))
                        continue;
                    section.getBiomes().set(x & 3, y & 3, z & 3, biome);
                }
            }

            if (world instanceof ServerLevel) {
                sendChangedBiome(chunkAt, dPos, target);
            }
            break;
        }
    }
    private void sendChangedBiome(LevelChunk chunk, BlockPos pos, ResourceKey<Biome> biome) {
        ChangeBiomePacket message = new ChangeBiomePacket(pos, biome);
        TFPacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), message);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 15;
    }

    //The drops must be capped to just one core. The rest are turned to the default branch.
    public float getPrimitiveLogs(float volumeIn, List<ItemStack> drops) {
        float ret = super.getPrimitiveLogs(volumeIn, drops);

        Block primitiveLog;
        if (getFamily().getPrimitiveLog().isPresent())
            primitiveLog = getFamily().getPrimitiveLog().get();
        else return ret;
        Block primitiveCoreLog;
        if (getFamily() instanceof MagicFamily magicFamily && magicFamily.getPrimitiveCoreLog().isPresent())
            primitiveCoreLog = magicFamily.getPrimitiveCoreLog().get();
        else return ret;

        int addLogs = 0;
        for (ItemStack stack : drops){
            if (stack.is(new ItemStack(primitiveCoreLog).getItem())){
                int logCount = stack.getCount();
                if (logCount > 1){
                    stack.setCount(0);
                    addLogs = logCount-1;
                }
                break;
            }
        }
        if (addLogs > 0){
            drops.add(new ItemStack(primitiveLog, addLogs));
        }
        //52 6 8 -> 57 1 8
        return ret;
    }

}
