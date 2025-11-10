package maxhyper.dttwilightforest.init;

import com.dtteam.dynamictrees.block.CommonVoxelShapes;
import com.dtteam.dynamictrees.event.TypeRegistryEvent;
import com.dtteam.dynamictrees.tree.species.Species;
import maxhyper.dttwilightforest.DynamicTreesTheTwilightForest;
import maxhyper.dttwilightforest.trees.MushgloomSpecies;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public class DTTFPlusRegistries {

    public static final VoxelShape SHROOM_AGE0 = Shapes.create(0, 0, 0, 1, 0.75, 1);
    public static final VoxelShape MUSHROOM_CAP_SHORT_ROUND = Block.box(5D, 3D, 5D, 11D, 7D, 11D);
    public static final VoxelShape ROUND_SHORT_MUSHROOM = Shapes.or(CommonVoxelShapes.ROUND_MUSHROOM, MUSHROOM_CAP_SHORT_ROUND);

    public static void setup() {
        CommonVoxelShapes.SHAPES.put(fromNamespaceAndPath(DynamicTreesTheTwilightForest.MOD_ID, "mushgloom_age0").toString(), SHROOM_AGE0);
        CommonVoxelShapes.SHAPES.put(fromNamespaceAndPath(DynamicTreesTheTwilightForest.MOD_ID, "round_short_mushroom").toString(), ROUND_SHORT_MUSHROOM);
    }

    @SubscribeEvent
    public static void registerSpeciesTypes(final TypeRegistryEvent<Species> event) {
        event.registerType(DynamicTreesTheTwilightForest.location("mushgloom"), MushgloomSpecies.TYPE);
    }

}
