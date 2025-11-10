package maxhyper.dttwilightforest.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ReplaceItemModifier extends LootModifier {
    public static final Supplier<Codec<ReplaceItemModifier>> CODEC = Suppliers.memoize(()
            -> RecordCodecBuilder.create(inst -> codecStart(inst)
            .and(ForgeRegistries.ITEMS.getCodec().fieldOf("old_item").forGetter(m -> m.old_item))
            .and(ForgeRegistries.ITEMS.getCodec().fieldOf("new_item").forGetter(m -> m.new_item))
                .apply(inst, ReplaceItemModifier::new)));
    private final Item old_item;
    private final Item new_item;

    protected ReplaceItemModifier(LootItemCondition[] conditionsIn, Item oldItem, Item newItem) {
        super(conditionsIn);
        this.old_item = oldItem;
        this.new_item = newItem;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        generatedLoot.replaceAll(itemStack -> itemStack.getItem().equals(old_item) ? new ItemStack(new_item) : itemStack);
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
