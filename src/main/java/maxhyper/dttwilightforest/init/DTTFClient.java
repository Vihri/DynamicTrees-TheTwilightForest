package maxhyper.dttwilightforest.init;

import maxhyper.dttwilightforest.DynamicTreesTheTwilightForest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class DTTFClient {

    public static void setup() {
        registerRenderLayers();
        registerColorHandlers();
    }

    private static void registerRenderLayers() {
        Block mangroveRoots = ForgeRegistries.BLOCKS.getValue(DynamicTreesTheTwilightForest.location("mangrove_roots"));
        ItemBlockRenderTypes.setRenderLayer(mangroveRoots, RenderType.cutoutMipped());
//        ItemBlockRenderTypes.setRenderLayer(DTTFRegistries.UNDERGROUND_ROOTS.get(), RenderType.cutoutMipped());
    }

    private static void registerColorHandlers(){
        Block mangroveRoots = ForgeRegistries.BLOCKS.getValue(DynamicTreesTheTwilightForest.location("mangrove_roots"));
        final BlockColors blockColors = Minecraft.getInstance().getBlockColors();

        blockColors.register((state, level, pos, tintIndex) -> {
                    if (tintIndex != 0) return 0xFFFFFF;
                    return level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : GrassColor.get(0.5D, 1.0D);
                },
                mangroveRoots);

    }


}
