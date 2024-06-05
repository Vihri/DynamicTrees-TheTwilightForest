package maxhyper.dttwilightforest.canceller;

import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors;
import com.ferreusveritas.dynamictrees.worldgen.featurecancellation.TreeFeatureCanceller;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import twilightforest.world.components.feature.config.TFTreeFeatureConfig;

import java.util.Set;

public class DTTFTreeFeatureCanceller<T extends FeatureConfiguration> extends TreeFeatureCanceller<T> {

    public DTTFTreeFeatureCanceller(final ResourceLocation registryName, Class<T> treeFeatureConfigClass) {
        super(registryName, treeFeatureConfigClass);
    }

    @Override
    public boolean shouldCancel(ConfiguredFeature<?, ?> configuredFeature, BiomePropertySelectors.NormalFeatureCancellation featureCancellations) {
        final FeatureConfiguration featureConfig = configuredFeature.config();

        return featureConfig instanceof TFTreeFeatureConfig;
    }

}
