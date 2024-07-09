package maxhyper.dttwilightforest.blocks;

import com.ferreusveritas.dynamictrees.block.branch.BasicRootsBlock;
import maxhyper.dttwilightforest.trees.TwilightMangroveFamily;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TwilightMangroveRootsBlock extends BasicRootsBlock {

    public static final BooleanProperty GRASSY = BooleanProperty.create("grassy");

    public TwilightMangroveRootsBlock(ResourceLocation name, Properties properties) {
        super(name, properties);
        registerDefaultState(defaultBlockState().setValue(GRASSY, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
       super.createBlockStateDefinition(builder);
       builder.add(GRASSY);
    }

    private Optional<Block> getPrimitiveGrassIfGrassy (BlockState state){
        if (isFullBlock(state) && getFamily() instanceof TwilightMangroveFamily tmf
                && state.hasProperty(GRASSY) && state.getValue(GRASSY)){
            return tmf.getPrimitiveGrassyRoots();
        }
        return Optional.empty();
    }

    private Block getPrimitiveAny (BlockState state){
        return getPrimitiveGrassIfGrassy(state).orElseGet(
                () -> state.getValue(LAYER).getPrimitive(getFamily()).orElse(null));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {

        if (isFullBlock(state)){
            //We override this function to change setValue to EXPOSED instead of FILLED (twilight mangrove roots cannot be filled)
            level.setBlock(pos, state.setValue(LAYER, Layer.EXPOSED).setValue(GRASSY, false), level.isClientSide ? 11 : 3);
            this.spawnDestroyParticles(level, player, pos, state);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            Block primitive = getPrimitiveAny(state);
            if (!player.isCreative() && primitive != null) dropResources(primitive.defaultBlockState(), level, pos);
            return false;
        }
        return this.removedByEntity(state, level, pos, player);
    }

    @Override
    public int setRadius(LevelAccessor level, BlockPos pos, int radius, @javax.annotation.Nullable Direction originDir, int flags) {
        int rad = super.setRadius(level, pos, radius, originDir, flags);
        BlockState newBranchState = level.getBlockState(pos);
        if (newBranchState.is(this) && newBranchState.getValue(LAYER) == Layer.COVERED){
            updateIsGrassy(level, pos, newBranchState, flags);
        }
        return rad;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        //if (pFromPos == pPos.above()){
            updateIsGrassy(pLevel, pPos, pState, 3);
        //}
    }

    protected void updateIsGrassy(LevelAccessor level, BlockPos pos, BlockState branchState, int flags){
        BlockPos upPos = pos.above();
        BlockState upState = level.getBlockState(upPos);
        boolean exposed = !upState.isCollisionShapeFullBlock(level, upPos) && upState.getFluidState().isEmpty();
        level.setBlock(pos, branchState.setValue(GRASSY, exposed), flags);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        Optional<Block> primitiveGrass = getPrimitiveGrassIfGrassy(state);
        if (primitiveGrass.isPresent())
            return primitiveGrass.get().getSoundType(state, level, pos, entity);
        return super.getSoundType(state, level, pos, entity);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        Optional<Block> primitiveGrass = getPrimitiveGrassIfGrassy(state);
        if (primitiveGrass.isPresent())
            return primitiveGrass.get().getCloneItemStack(state, target, level, pos, player);
        return super.getCloneItemStack(state, target, level, pos, player);
    }

}
