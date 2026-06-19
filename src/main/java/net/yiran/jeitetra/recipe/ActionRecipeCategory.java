package net.yiran.jeitetra.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.blaze3d.platform.InputConstants;
import com.yanny.aci.api.IWidget;
import com.yanny.aci.api.Rect;
import com.yanny.aci.api.RelativeRect;
import com.yanny.ali.api.IDataNode;
import com.yanny.ali.api.IItemNode;
import com.yanny.ali.api.IWidgetUtils;
import com.yanny.ali.api.ITooltipNode;
import com.yanny.ali.compatibility.common.GameplayLootType;
import com.yanny.ali.compatibility.common.GenericUtils;
import com.yanny.ali.jei.compatibility.jei.JeiBaseLoot;
import com.yanny.ali.jei.compatibility.jei.JeiLootSlotWidget;
import com.yanny.ali.jei.compatibility.jei.JeiScrollWidget;
import com.yanny.ali.jei.compatibility.jei.JeiWidgetWrapper;
import com.yanny.ali.plugin.client.ClientUtils;
import com.yanny.ali.plugin.client.widget.LootTableWidget;
import com.yanny.ali.plugin.common.NodeUtils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.api.gui.widgets.ISlottedRecipeWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import net.yiran.jeitetra.util.Drawables;
import net.yiran.jeitetra.util.I18nWrapper;
import net.yiran.jeitetra.util.ItemUtil;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiString;
import se.mickelus.tetra.blocks.workbench.action.ConfigActionImpl;
import se.mickelus.tetra.blocks.workbench.gui.ToolRequirementGui;
import se.mickelus.tetra.properties.PropertyHelper;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"removal", "all"})
public class ActionRecipeCategory extends AbstractRecipeCategory<ConfigActionImpl> {
    private static final int ACTION_HEIGHT = 42;
    private static final int LOOT_TOP = ACTION_HEIGHT - 2;
    private static final int LOOT_WIDTH = 9 * 18;
    private static final int LOOT_HEIGHT = 6 * 18;
    private static final int WIDTH = LOOT_WIDTH + JeiScrollWidget.getScrollbarExtraWidth();
    private static final int HEIGHT = LOOT_TOP + LOOT_HEIGHT;
    private static final Map<ResourceLocation, GameplayLootType> LOOT_DATA = Collections.synchronizedMap(new java.util.HashMap<>());
    private static final Map<ConfigActionImpl, AliLootRenderState> RENDER_STATES = Collections.synchronizedMap(new IdentityHashMap<>());

    public static RecipeType<ConfigActionImpl> recipeType = RecipeType.create("jeitetra", "action", ConfigActionImpl.class);
    public Minecraft mc;
    private final IGuiHelper guiHelper;

    public ActionRecipeCategory(IGuiHelper guiHelper) {
        super(
                recipeType,
                Component.translatable("jeitetra.action.title"),
                guiHelper.createDrawableItemLike(ForgeRegistries.ITEMS.getValue(new ResourceLocation("tetra:geode"))),
                WIDTH,
                HEIGHT
        );
        this.guiHelper = guiHelper;
        mc = Minecraft.getInstance();
    }

    public static void updateLootData(byte[] fullCompressedData) {
        LOOT_DATA.clear();

        var decompressed = GenericUtils.decompressLootData(fullCompressedData);

        for (Map.Entry<ResourceLocation, GenericUtils.LootData> entry : decompressed.getA().entrySet()) {
            GenericUtils.LootData data = entry.getValue();
            LOOT_DATA.put(entry.getKey(), new GameplayLootType(data.node(), entry.getKey(), data.items()));
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ConfigActionImpl configAction, IFocusGroup iFocusGroup) {
        IRecipeSlotBuilder inputSlotBuilder = builder.addInputSlot(66, 1).setBackground(Drawables.SLOT, 0, 0);//.setStandardSlotBackground();
        var items = ItemUtil.getItemsFromItemPredicate(configAction.requirement);
        if (items != null && !items.isEmpty()) {
            inputSlotBuilder.addItemStacks(items);
        }

        createAliLootSlots(builder, configAction);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, ConfigActionImpl recipe, IFocusGroup focuses) {
        AliLootRenderState state = RENDER_STATES.get(recipe);

        if (state == null || state.widgetWrapper == null || state.holders == null) {
            return;
        }

        Pair<List<IRecipeWidget>, List<IRecipeSlotDrawable>> additionalWidgets = getAliTitleWidgets(state.type);
        List<IRecipeWidget> scrollWidgets = new LinkedList<>(additionalWidgets.getA());
        List<IRecipeSlotDrawable> slotDrawables = new LinkedList<>(additionalWidgets.getB());

        scrollWidgets.add(state.widgetWrapper);

        for (int i = 0; i < state.holders.size(); i++) {
            JeiBaseLoot.Holder holder = state.holders.get(i);
            builder.getRecipeSlots().findSlotByName(getAliSlotName(recipe, i)).ifPresent(slotDrawable -> {
                scrollWidgets.add(new JeiLootSlotWidget(slotDrawable, holder.rect().getX(), holder.rect().getY(), ((IItemNode) holder.entry()).getCount()));
                slotDrawables.add(slotDrawable);
            });
        }

        Rect renderRect = new Rect(0, 0, LOOT_WIDTH + JeiScrollWidget.getScrollbarExtraWidth(), LOOT_HEIGHT);
        JeiScrollWidget scrollWidget = new JeiScrollWidget(renderRect, state.widgetWrapper.getRect().height() + 10, scrollWidgets);
        ShiftedAliScrollWidget shiftedScrollWidget = new ShiftedAliScrollWidget(scrollWidget, LOOT_TOP);

        builder.addSlottedWidget(shiftedScrollWidget, slotDrawables);
        builder.addInputHandler(shiftedScrollWidget);
    }

    @Override
    public void draw(ConfigActionImpl recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var entry = recipe.requiredTools.levelMap.entrySet().stream().findFirst().get();
        var toolAction = entry.getKey();
        var level = entry.getValue();
        var group = new GuiElement(0, 0, 150, ACTION_HEIGHT);
        var element = new ToolRequirementGui(LOOT_TOP, 1, toolAction);
        element.updateRequirement(level.intValue(), PropertyHelper.getPlayerToolLevel(mc.player, toolAction));
        group.addChild(element);
        var loot = new GuiString(1, 20, I18nWrapper.get("jeitetra.action.loot.info"));
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

    private void createAliLootSlots(IRecipeLayoutBuilder builder, ConfigActionImpl configAction) {
        GameplayLootType type = LOOT_DATA.get(configAction.lootTable);

        if (type == null) {
            RENDER_STATES.remove(configAction);
            return;
        }

        List<JeiBaseLoot.Holder> holders = new LinkedList<>();
        IWidgetUtils utils = getJeiUtils(holders);
        RelativeRect rect = new RelativeRect(0, 10, LOOT_WIDTH, 0);

        AliLootRenderState state = new AliLootRenderState(type, new JeiWidgetWrapper(new LootTableWidget(utils, type.entry(), rect, LOOT_WIDTH)), holders);
        RENDER_STATES.put(configAction, state);

        type.inputs().forEach(item -> builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStack(item));
        type.outputs().forEach(item -> builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStack(item));

        for (int i = 0; i < holders.size(); i++) {
            JeiBaseLoot.Holder holder = holders.get(i);
            IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.RENDER_ONLY)
                    .setStandardSlotBackground()
                    .setSlotName(getAliSlotName(configAction, i))
                    .setPosition(holder.rect().getX(), holder.rect().getY())
                    .addRichTooltipCallback((slotView, tooltipBuilder)
                            -> tooltipBuilder.addAll(NodeUtils.toComponents((ITooltipNode) holder.entry().getTooltip(), 0, Minecraft.getInstance().options.advancedItemTooltips)));
            Optional<ItemStack> left = holder.item().left();
            Optional<TagKey<? extends ItemLike>> right = holder.item().right();

            left.ifPresent(slotBuilder::addItemStack);
            right.ifPresent(tag -> slotBuilder.addIngredients(Ingredient.of((TagKey<Item>) tag)));
        }
    }

    private Pair<List<IRecipeWidget>, List<IRecipeSlotDrawable>> getAliTitleWidgets(GameplayLootType type) {
        Triplet<Component, Component, Rect> title = GenericUtils.prepareGameplayTitle(type.id(), LOOT_WIDTH - JeiScrollWidget.getScrollbarExtraWidth());

        return new Pair<>(List.of(
                createTextWidget(title.getA(), 0, 0, false),
                new TooltipWidget(title.getB(), title.getC())
        ), List.of());
    }

    @NotNull
    private IRecipeWidget createTextWidget(Component component, int x, int y, boolean centered) {
        return guiHelper.createWidgetFromDrawable(new IDrawable() {
            @Override
            public int getWidth() {
                return LOOT_WIDTH;
            }

            @Override
            public int getHeight() {
                return 8;
            }

            @Override
            public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
                if (centered) {
                    int width = Minecraft.getInstance().font.width(component);
                    guiGraphics.drawString(Minecraft.getInstance().font, component, x - width / 2, y, 0, false);
                } else {
                    guiGraphics.drawString(Minecraft.getInstance().font, component, x, y, 0, false);
                }
            }
        }, x, 0);
    }

    @NotNull
    private IWidgetUtils getJeiUtils(List<JeiBaseLoot.Holder> holders) {
        return new ClientUtils() {
            @Override
            public void addSlotWidget(Either<ItemStack, TagKey<? extends ItemLike>> item, IDataNode entry, RelativeRect rect) {
                holders.add(new JeiBaseLoot.Holder(item, entry, rect));
            }
        };
    }

    private String getAliSlotName(ConfigActionImpl recipe, int index) {
        return "ali_loot_" + System.identityHashCode(recipe) + "_" + index;
    }

    private record AliLootRenderState(GameplayLootType type, JeiWidgetWrapper widgetWrapper,
                                      List<JeiBaseLoot.Holder> holders) {
    }

    private record TooltipWidget(Component component, Rect rect) implements IRecipeWidget {
        @NotNull
        @Override
        public ScreenPosition getPosition() {
            return new ScreenPosition(rect.x(), rect.y());
        }

        @Override
        public void getTooltip(ITooltipBuilder tooltip, double mouseX, double mouseY) {
            if (rect.contains((int) mouseX, (int) mouseY)) {
                tooltip.add(component);
            }
        }
    }

    private record ShiftedAliScrollWidget(JeiScrollWidget widget, int yOffset) implements ISlottedRecipeWidget, IJeiInputHandler {
        @NotNull
        @Override
        public ScreenPosition getPosition() {
            ScreenPosition position = widget.getPosition();
            return new ScreenPosition(position.x(), position.y() + yOffset);
        }

        @NotNull
        @Override
        public ScreenRectangle getArea() {
            ScreenRectangle area = widget.getArea();
            return new ScreenRectangle(area.left(), area.top() + yOffset, area.width(), area.height());
        }

        @NotNull
        @Override
        public Optional<RecipeSlotUnderMouse> getSlotUnderMouse(double mouseX, double mouseY) {
            return widget.getSlotUnderMouse(mouseX, mouseY).map(slot -> slot.addOffset(0, yOffset));
        }

        @Override
        public void drawWidget(GuiGraphics guiGraphics, double mouseX, double mouseY) {
            widget.drawWidget(guiGraphics, mouseX, mouseY);
        }

        @Override
        public void getTooltip(ITooltipBuilder tooltip, double mouseX, double mouseY) {
            widget.getTooltip(tooltip, mouseX, mouseY);
        }

        @Override
        public boolean handleInput(double mouseX, double mouseY, IJeiUserInput userInput) {
            return widget.handleInput(mouseX, mouseY, userInput);
        }

        @Override
        public boolean handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaY) {
            return widget.handleMouseScrolled(mouseX, mouseY, scrollDeltaY);
        }

        @Override
        public boolean handleMouseDragged(double mouseX, double mouseY, InputConstants.Key mouseKey, double dragX, double dragY) {
            return widget.handleMouseDragged(mouseX, mouseY, mouseKey, dragX, dragY);
        }
    }

}
