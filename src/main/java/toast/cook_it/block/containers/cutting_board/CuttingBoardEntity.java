package toast.cook_it.block.containers.cutting_board;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.math.BlockPos;
import toast.cook_it.block.CookingBlockEntity;
import toast.cook_it.block.ImplementedInventory;
import toast.cook_it.recipes.CuttingBoardRecipe;
import toast.cook_it.registries.CookItBlockEntities;

import java.util.List;
import java.util.Objects;

public class CuttingBoardEntity extends CookingBlockEntity implements ImplementedInventory {

    public CuttingBoardEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.CUTTING_BOARD_ENTITY, pos, state, 1);
    }

    public void processRecipe(Item tool) {
        for (RecipeEntry<CuttingBoardRecipe> cuttingBoardRecipeRecipeEntry : getCurrentRecipe()) {
            if (cuttingBoardRecipeRecipeEntry.value() != null && isValidTool(tool)) {
                Item item = cuttingBoardRecipeRecipeEntry.value().getResult(null).getItem();
                ItemStack output = new ItemStack(item, cuttingBoardRecipeRecipeEntry.value().getOutputCount());
                this.setStack(0, output);
            }
        }
    }

    public boolean isValidTool(Item tool) {
        for (RecipeEntry<CuttingBoardRecipe> cuttingBoardRecipeRecipeEntry : getCurrentRecipe()) {
            Item item = cuttingBoardRecipeRecipeEntry.value().getTool().getItem();
            if (item.equals(tool)) {
                return true;
            }
        }
        return false;
    }

    public List<RecipeEntry<CuttingBoardRecipe>> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size());
        for (int i = 0; i < this.size(); i++) {
            inv.setStack(i, this.getStack(i));
        }
        return Objects.requireNonNull(getWorld()).getRecipeManager().getAllMatches(CuttingBoardRecipe.Type.INSTANCE, inv, getWorld());
    }
}
