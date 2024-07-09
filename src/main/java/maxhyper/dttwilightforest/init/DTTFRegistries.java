package maxhyper.dttwilightforest.init;

import com.ferreusveritas.dynamictrees.api.cell.CellKit;
import com.ferreusveritas.dynamictrees.api.registry.RegistryEvent;
import com.ferreusveritas.dynamictrees.api.registry.TypeRegistryEvent;
import com.ferreusveritas.dynamictrees.api.worldgen.FeatureCanceller;
import com.ferreusveritas.dynamictrees.block.rooty.SoilProperties;
import com.ferreusveritas.dynamictrees.growthlogic.GrowthLogicKit;
import com.ferreusveritas.dynamictrees.systems.genfeature.GenFeature;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import com.ferreusveritas.dynamictrees.tree.species.Species;
import maxhyper.dttwilightforest.DynamicTreesTheTwilightForest;
import maxhyper.dttwilightforest.blocks.RootSoilProperties;
import maxhyper.dttwilightforest.canceller.SimpleFeatureCanceller;
import maxhyper.dttwilightforest.cellkits.DTTFCellKits;
import maxhyper.dttwilightforest.genfeatures.DTTFGenFeatures;
import maxhyper.dttwilightforest.growthlogic.DTTFGrowthLogicKits;
import maxhyper.dttwilightforest.trees.MagicFamily;
import maxhyper.dttwilightforest.trees.TwilightMangroveFamily;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twilightforest.world.components.feature.config.TFTreeFeatureConfig;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DTTFRegistries {

    public static void setup() {
    }

    @SubscribeEvent
    public static void registerFamilyTypes(final TypeRegistryEvent<Family> event) {
        event.registerType(DynamicTreesTheTwilightForest.location("magic"), MagicFamily.TYPE);
        event.registerType(DynamicTreesTheTwilightForest.location("mangrove"), TwilightMangroveFamily.TYPE);
    }

    @SubscribeEvent
    public static void registerSpeciesTypes(final TypeRegistryEvent<Species> event) {
    }

    @SubscribeEvent
    public static void registerSoilPropertiesTypes(final TypeRegistryEvent<SoilProperties> event) {
        event.registerType(DynamicTreesTheTwilightForest.location("roots"), RootSoilProperties.TYPE);
    }

    public static final FeatureCanceller TREE_CANCELLER = new SimpleFeatureCanceller<>(DynamicTreesTheTwilightForest.location("all_trees"), TFTreeFeatureConfig.class);
    public static final FeatureCanceller MUSHROOM_CANCELLER = new SimpleFeatureCanceller<>(DynamicTreesTheTwilightForest.location("all_mushrooms"), HugeMushroomFeatureConfiguration.class);

    @SubscribeEvent
    public static void onFeatureCancellerRegistry(final RegistryEvent<FeatureCanceller> event) {
        event.getRegistry().registerAll(TREE_CANCELLER, MUSHROOM_CANCELLER);
    }

    @SubscribeEvent
    public static void onGrowthLogicKitRegistry(final RegistryEvent<GrowthLogicKit> event) {
        DTTFGrowthLogicKits.register(event.getRegistry());
    }

    @SubscribeEvent
    public static void onCellKitRegistry(final RegistryEvent<CellKit> event) {
        DTTFCellKits.register(event.getRegistry());
    }

    @SubscribeEvent
    public static void onGenFeatureRegistry(final RegistryEvent<GenFeature> event) {
        DTTFGenFeatures.register(event.getRegistry());
    }

}
