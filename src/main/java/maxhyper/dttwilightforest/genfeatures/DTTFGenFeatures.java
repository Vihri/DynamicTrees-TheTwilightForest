package maxhyper.dttwilightforest.genfeatures;

import com.ferreusveritas.dynamictrees.api.registry.Registry;
import com.ferreusveritas.dynamictrees.systems.genfeature.GenFeature;
import maxhyper.dttwilightforest.DynamicTreesTheTwilightForest;

public class DTTFGenFeatures {

//    public static final GenFeature UNDERGROUND_ROOTS = new UndergroundRootsGenFeature(DynamicTreesTheTwilightForest.location("underground_roots"));
    public static final GenFeature MAGIC_CORE = new MagicCoreGenFeature(DynamicTreesTheTwilightForest.location("magic_core"));
    public static final GenFeature FIREFLY = new FireflyGenFeature(DynamicTreesTheTwilightForest.location("firefly"));

    public static void register(final Registry<GenFeature> registry) {
        registry.registerAll(
                MAGIC_CORE, FIREFLY
//              ,UNDERGROUND_ROOTS
        );
    }


}
