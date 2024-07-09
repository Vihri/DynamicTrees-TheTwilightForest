package maxhyper.dttwilightforest.trees;

import com.ferreusveritas.dynamictrees.api.registry.TypedRegistry;
import com.ferreusveritas.dynamictrees.block.branch.BasicRootsBlock;
import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import com.ferreusveritas.dynamictrees.tree.family.MangroveFamily;
import com.ferreusveritas.dynamictrees.util.Optionals;
import maxhyper.dttwilightforest.blocks.TwilightMangroveRootsBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class TwilightMangroveFamily extends MangroveFamily {

    public static final TypedRegistry.EntryType<Family> TYPE = TypedRegistry.newType(TwilightMangroveFamily::new);

    public TwilightMangroveFamily(ResourceLocation name) {
        super(name);
    }

    @Override
    protected BranchBlock createRootsBlock(ResourceLocation name) {
        final BasicRootsBlock branch = new TwilightMangroveRootsBlock(name, this.getProperties());
        if (this.isFireProof()) branch.setFireSpreadSpeed(0).setFlammability(0);
        return branch;
    }

    private Block primitiveRootsGrassy;

    public void setPrimitiveRootsGrassy(Block primitiveRootsCovered) {
        this.primitiveRootsGrassy = primitiveRootsCovered;
    }

    public Optional<Block> getPrimitiveGrassyRoots() {
        return Optionals.ofBlock(primitiveRootsGrassy);
    }

    @Override
    public void setPrimitiveRoots(Block primitiveRoots) {
        //We set filled roots to the same, we will ignore filled roots anyway.
        super.setPrimitiveRoots(primitiveRoots);
        setPrimitiveRootsFilled(primitiveRoots);
    }
}
