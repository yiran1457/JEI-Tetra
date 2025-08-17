package net.yiran.jeitetra.recipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiString;
import se.mickelus.tetra.blocks.workbench.action.ConfigActionImpl;
import se.mickelus.tetra.blocks.workbench.gui.ToolRequirementGui;
import se.mickelus.tetra.properties.PropertyHelper;

@SuppressWarnings({"removal", "all"})
public class ActionRecipeCategory extends AbstractRecipeCategory<ConfigActionImpl> {
    public static RecipeType<ConfigActionImpl> recipeType = RecipeType.create("jeitetra", "action", ConfigActionImpl.class);
    public Minecraft mc;

    public ActionRecipeCategory(IGuiHelper guiHelper) {
        super(
                recipeType,
                Component.translatable("jeitetra.action.title"),
                guiHelper.createDrawableItemLike(ForgeRegistries.ITEMS.getValue(new ResourceLocation("tetra:geode"))),
                150,
                42
        );
        mc = Minecraft.getInstance();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ConfigActionImpl configAction, IFocusGroup iFocusGroup) {
        IRecipeSlotBuilder inputSlotBuilder = builder.addInputSlot(66, 1).setBackground(Drawables.SLOT,0,0);//.setStandardSlotBackground();
        if (configAction.requirement.tag != null) {
            inputSlotBuilder.addIngredients(Ingredient.of(configAction.requirement.tag));
        } else {
            inputSlotBuilder.addIngredients(Ingredient.of(configAction.requirement.items.stream().map(ItemStack::new)));
        }
    }

    @Override
    public void draw(ConfigActionImpl recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var entry = recipe.requiredTools.levelMap.entrySet().stream().findFirst().get();
        var toolAction = entry.getKey();
        var level = entry.getValue();
        var group = new GuiElement(0, 0, 150, 42);
        var element = new ToolRequirementGui(40, 1, toolAction);
        element.updateRequirement(level.intValue(), PropertyHelper.getPlayerToolLevel(mc.player, toolAction));
        group.addChild(element);
        var loot = new GuiString(1,20, I18n.get("jeitetra.action.loot.info"));
        group.addChild(loot);
        var text = new GuiString(1, 30, recipe.lootTable.toString());
        group.addChild(text);
        group.draw(guiGraphics, 0, 0,
                mc.getWindow().getScreenWidth(), mc.getWindow().getScreenHeight(),
                (int) mouseX, (int) mouseY,
                1
        );
        group.updateFocusState(0, 0, (int) mouseX, (int) mouseY);

        if (element.getTooltipLines() != null)
            guiGraphics.renderComponentTooltip(mc.font, element.getTooltipLines(), (int) mouseX, (int) mouseY);
    }
}
