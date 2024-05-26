package toast.cook_it.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

public class CuttingBoardRecipe implements Recipe<SimpleInventory> {
    private final ItemStack output;
    private final Ingredient ingredient;
    private final int count;
    private final ItemStack tool;
    private static final Codec<Item> ITEM_CODEC = Codecs.validate(Registries.ITEM.getCodec(), DataResult::success);
    private static final Codec<ItemStack> ITEMSTACK_CODEC = ITEM_CODEC.xmap(ItemStack::new, ItemStack::getItem);

    public CuttingBoardRecipe(Ingredient ingredient, ItemStack itemStack, int count, ItemStack tool) {
        this.output = itemStack;
        this.ingredient = ingredient;
        this.count = count;
        this.tool = tool;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if(world.isClient()) {
            return false;
        }
        return ingredient.test(inventory.getStack(0));
    }

    @Override
    public boolean isIgnoredInRecipeBook() { return true; }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output;
    }

    public ItemStack getTool() {
        return tool;
    }
    public int getOutputCount() {
        return count;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<CuttingBoardRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<CuttingBoardRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final Codec<CuttingBoardRecipe> CODEC = RecordCodecBuilder.create(in -> in.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input").forGetter(r -> r.ingredient),
                ItemStack.RECIPE_RESULT_CODEC.fieldOf("output").forGetter(r -> r.output),
                Codec.INT.optionalFieldOf("count", 1).forGetter(r -> r.count),
                ITEMSTACK_CODEC.fieldOf("tool").forGetter(r -> r.tool)
        ).apply(in, CuttingBoardRecipe::new));

        @Override
        public Codec<CuttingBoardRecipe> codec() {
            return CODEC;
        }

        @Override
        public CuttingBoardRecipe read(PacketByteBuf buf) {

            Ingredient ingredient = Ingredient.fromPacket(buf);
            ItemStack output = buf.readItemStack();
            ItemStack tool = buf.readItemStack();
            int count = buf.readInt();
            return new CuttingBoardRecipe(ingredient, output, count, tool);
        }

        @Override
        public void write(PacketByteBuf buf, CuttingBoardRecipe recipe) {
            recipe.ingredient.write(buf);
            buf.writeItemStack(recipe.getResult(null));
            buf.readInt();
            buf.writeItemStack(recipe.getTool());
        }
    }
}
