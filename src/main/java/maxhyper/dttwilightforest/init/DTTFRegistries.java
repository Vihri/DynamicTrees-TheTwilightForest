package maxhyper.dttwilightforest.init;

import com.dtteam.dynamictrees.api.cell.CellKit;
import com.dtteam.dynamictrees.api.worldgen.FeatureCanceller;
import com.dtteam.dynamictrees.block.soil.SoilProperties;
import com.dtteam.dynamictrees.event.RegistryEvent;
import com.dtteam.dynamictrees.event.TypeRegistryEvent;
import com.dtteam.dynamictrees.systems.genfeature.GenFeature;
import com.dtteam.dynamictrees.systems.growthlogic.GrowthLogicKit;
import com.dtteam.dynamictrees.tree.family.Family;
import com.dtteam.dynamictrees.tree.family.UndergroundRootsFamily;
import com.dtteam.dynamictrees.tree.species.Species;
import maxhyper.dttwilightforest.DynamicTreesTheTwilightForest;
import maxhyper.dttwilightforest.canceller.SimpleFeatureCanceller;
import maxhyper.dttwilightforest.cellkits.DTTFCellKits;
import maxhyper.dttwilightforest.genfeatures.DTTFGenFeatures;
import maxhyper.dttwilightforest.genfeatures.UndergroundRootsGenFeature;
import maxhyper.dttwilightforest.growthlogic.DTTFGrowthLogicKits;
import maxhyper.dttwilightforest.trees.GigaSpruceSpecies;
import maxhyper.dttwilightforest.trees.MagicFamily;
import maxhyper.dttwilightforest.trees.TwilightMangroveFamily;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import twilightforest.world.components.feature.config.TFTreeFeatureConfig;

@EventBusSubscriber
public class DTTFRegistries {

    public static void setup() {
    }

    @SubscribeEvent
    public static void registerFamilyTypes(final TypeRegistryEvent<Family> event) {
        event.registerType(DynamicTreesTheTwilightForest.location("magic"), MagicFamily.TYPE);
    }
    @SubscribeEvent
    public static void registerUndergroundRootFamilyTypes(final TypeRegistryEvent<UndergroundRootsFamily> event) {
        event.registerType(DynamicTreesTheTwilightForest.location("mangrove"), TwilightMangroveFamily.TYPE);
    }

    @SubscribeEvent
    public static void registerSpeciesTypes(final TypeRegistryEvent<Species> event) {
        event.registerType(DynamicTreesTheTwilightForest.location("giga_spruce"), GigaSpruceSpecies.TYPE);
    }

    @SubscribeEvent
    public static void registerSoilPropertiesTypes(final TypeRegistryEvent<SoilProperties> event) {
        //event.registerType(DynamicTreesTheTwilightForest.location("uberous_soil"), UberousSoilProperties.TYPE);
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
