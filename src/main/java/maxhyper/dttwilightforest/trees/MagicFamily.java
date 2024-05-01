package maxhyper.dttwilightforest.trees;

import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

public class MagicFamily extends Family {

    private Supplier<BranchBlock> strippedBranch;
    private Block primitiveStrippedLog = Blocks.AIR;

    public MagicFamily(ResourceLocation name) {
        super(name);
    }
}
