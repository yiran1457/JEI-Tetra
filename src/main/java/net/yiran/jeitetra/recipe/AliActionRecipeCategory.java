package net.yiran.jeitetra.recipe;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Either;
import com.yanny.aci.api.IWidget;
import com.yanny.aci.api.Rect;
import com.yanny.aci.api.RelativeRect;
import com.yanny.aci.compatibility.ScrollableTooltip;
import com.yanny.aci.tooltip.CoreTooltipUtils;
import com.yanny.aci.tooltip.TooltipLine;
import com.yanny.aci.tooltip.TooltipNodePalette;
import com.yanny.ali.api.IClientUtils;
import com.yanny.ali.api.IDataNode;
import com.yanny.ali.api.IItemNode;
import com.yanny.ali.api.IWidgetUtils;
import com.yanny.ali.compatibility.common.GameplayLootType;
import com.yanny.ali.compatibility.common.GenericUtils;
import com.yanny.ali.configuration.AliConfig;
import com.yanny.ali.jei.compatibility.jei.JeiLootSlotWidget;
import com.yanny.ali.jei.compatibility.jei.JeiScrollWidget;
import com.yanny.ali.jei.compatibility.jei.JeiWidgetWrapper;
import com.yanny.ali.manager.AliClientRegistry;
import com.yanny.ali.manager.PluginManager;
import com.yanny.ali.plugin.client.TooltipUtils;
import com.yanny.ali.plugin.client.widget.LootTableWidget;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.api.gui.widgets.ISlottedRecipeWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.yiran.jeitetra.util.Drawables;
import net.yiran.jeitetra.util.I18nWrapper;
import net.yiran.jeitetra.util.ItemUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiString;
import se.mickelus.tetra.blocks.workbench.action.ConfigActionImpl;
import se.mickelus.tetra.blocks.workbench.gui.ToolRequirementGui;
import se.mickelus.tetra.properties.PropertyHelper;

import java.util.*;
import java.util.function.BiFunction;

/**
 * 带 AdvancedLootInfo (ALI) 战利品表预览的 Tetra 工作台动作分类。
 * <p>
 * 注意：这里用到的都是 ALI 的内部类（非公开 API），ALI 每次大版本更新都可能改签名。
 * 当前实现针对 ALI 2.4.0+（已对 2.4.0 / 2.5.0 的字节码逐一核对过引用）。
 * 与 1.x 相比的主要变化：
 * <ul>
 *     <li>{@code GenericUtils.decompressLootData} 改为返回 (战利品表, 交易) 两个 {@code Map}，{@code GenericUtils.LootData} 被移除；</li>
 *     <li>{@code IItemNode.getCount()} 的返回类型从 {@code RangeValue} 改为 {@code NumberExpr}，数量显示改用 {@link TooltipUtils#getSlotCount}；</li>
 *     <li>提示框改用 {@link CoreTooltipUtils#toLines} + {@link ScrollableTooltip}；</li>
 *     <li>2.5.0 起 {@code ClientUtils.addSlotWidget(Either, IDataNode, RelativeRect)} 变为 {@code final}，
 *     并新增抽象方法 {@code addSlotWidget(List, RelativeRect)}，所以不能再继承 {@code ClientUtils}，
 *     改用 {@link LootSlotCollector} 直接实现接口，同时兼容两种签名。</li>
 * </ul>
 */
@SuppressWarnings({"removal", "all"})
public class AliActionRecipeCategory extends ActionRecipeCategory {
    private static final int ACTION_HEIGHT = 42;
    private static final int LOOT_TOP = ACTION_HEIGHT - 2;
    private static final int LOOT_TITLE_HEIGHT = 10;
    private static final int LOOT_WIDTH = 9 * 18;
    private static final int LOOT_HEIGHT = 6 * 18;
    private static final int WIDTH = LOOT_WIDTH + JeiScrollWidget.getScrollbarExtraWidth();
    private static final int HEIGHT = LOOT_TOP + LOOT_HEIGHT;
    private static final Map<ResourceLocation, GameplayLootType> LOOT_DATA = Collections.synchronizedMap(new HashMap<>());
    private static final Map<ConfigActionImpl, AliLootRenderState> RENDER_STATES = Collections.synchronizedMap(new IdentityHashMap<>());

    private final Minecraft mc;
    private final IGuiHelper guiHelper;

    public AliActionRecipeCategory(IGuiHelper guiHelper) {
        super(
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

        // ALI 2.x: (lootData, tradeData)。与 ALI 自己的 JEI 插件一样，直接用 clientRegistry 作为解码用的 IClientUtils
        Map<ResourceLocation, IDataNode> lootData = GenericUtils.decompressLootData(getClientRegistry(), fullCompressedData).getA();

        for (Map.Entry<ResourceLocation, IDataNode> entry : lootData.entrySet()) {
            IDataNode node = entry.getValue();
            LOOT_DATA.put(entry.getKey(), new GameplayLootType(node, entry.getKey(), GenericUtils.collectItems(node)));
        }
    }

    public static void registerLootData(IRecipeRegistration registration) {
        GenericUtils.register(registration, (recipeRegistration, fullCompressedData) -> updateLootData(fullCompressedData));
    }

    private static AliClientRegistry getClientRegistry() {
        return (AliClientRegistry) PluginManager.getInstance().clientRegistry;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ConfigActionImpl configAction, IFocusGroup iFocusGroup) {
        IRecipeSlotBuilder inputSlotBuilder = builder.addInputSlot(66, 1).setBackground(Drawables.SLOT, 0, 0);
        var items = ItemUtil.getItemsFromItemPredicate(configAction.requirement);
        if (items != null && !items.isEmpty()) {
            inputSlotBuilder.addItemStacks(items);
        }

        createAliLootSlots(builder, configAction);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, ConfigActionImpl recipe, IFocusGroup focuses) {
        AliLootRenderState state = RENDER_STATES.get(recipe);

        if (state == null) {
            return;
        }

        Pair<List<IRecipeWidget>, List<IRecipeSlotDrawable>> additionalWidgets = getAliTitleWidgets(state.type());
        List<IRecipeWidget> scrollWidgets = new ArrayList<>(additionalWidgets.getA());
        List<IRecipeSlotDrawable> slotDrawables = new ArrayList<>(additionalWidgets.getB());

        scrollWidgets.add(state.widgetWrapper());

        for (int i = 0; i < state.slots().size(); i++) {
            LootSlot slot = state.slots().get(i);
            builder.getRecipeSlots().findSlotByName(getAliSlotName(recipe, i)).ifPresent(slotDrawable -> {
                // 战利品表的格子只会有一个选项（多选项只出现在交易里），用第一个选项的数量即可
                scrollWidgets.add(new JeiLootSlotWidget(slotDrawable, slot.rect().getX(), slot.rect().getY(),
                        TooltipUtils.getSlotCount((IItemNode) slot.options().get(0))));
                slotDrawables.add(slotDrawable);
            });
        }

        Rect renderRect = new Rect(0, 0, WIDTH, LOOT_HEIGHT);
        Rect contentRect = state.widgetWrapper().getRect();
        JeiScrollWidget scrollWidget = new JeiScrollWidget(renderRect, contentRect.width(), contentRect.height() + LOOT_TITLE_HEIGHT, scrollWidgets);
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

        List<LootSlot> slots = new ArrayList<>();
        RelativeRect rect = new RelativeRect(0, LOOT_TITLE_HEIGHT, LOOT_WIDTH, 0);
        JeiWidgetWrapper widgetWrapper = new JeiWidgetWrapper(new LootTableWidget(new LootSlotCollector(slots), type.entry(), rect, LOOT_WIDTH));

        RENDER_STATES.put(configAction, new AliLootRenderState(type, widgetWrapper, slots));

        type.inputs().forEach(item -> builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStack(item));
        type.outputs().forEach(item -> builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStack(item));

        for (int i = 0; i < slots.size(); i++) {
            LootSlot slot = slots.get(i);
            IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.RENDER_ONLY)
                    .setStandardSlotBackground()
                    .setSlotName(getAliSlotName(configAction, i))
                    .setPosition(slot.rect().getX(), slot.rect().getY())
                    .addRichTooltipCallback((slotView, tooltipBuilder) -> {
                        IDataNode option = slot.getDisplayedOption(slotView.getDisplayedItemStack().orElse(ItemStack.EMPTY));
                        List<TooltipLine> lines = CoreTooltipUtils.toLines(option.getTooltip(), 0,
                                Minecraft.getInstance().options.advancedItemTooltips,
                                TooltipUtils.getStyle(), TooltipUtils.getNumberOptions());

                        if (!lines.isEmpty()) {
                            tooltipBuilder.add(new ScrollableTooltip(lines));
                        }
                    });

            for (IDataNode option : slot.options()) {
                addSlotItems(slotBuilder, (IItemNode) option);
            }
        }
    }

    private static void addSlotItems(IRecipeSlotBuilder slotBuilder, IItemNode node) {
        Either<ItemStack, TagKey<? extends ItemLike>> item = node.getItem();

        item.left().ifPresent(slotBuilder::addItemStack);
        item.right().ifPresent(tag -> {
            if (tag.isFor(Registries.ITEM)) {
                slotBuilder.addIngredients(Ingredient.of((TagKey<Item>) tag));
            } else {
                // 方块标签等非物品标签不能直接转成 Ingredient，使用 ALI 已解析好的物品列表
                slotBuilder.addItemStacks(node.getItems());
            }
        });
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

    private String getAliSlotName(ConfigActionImpl recipe, int index) {
        return "ali_loot_" + System.identityHashCode(recipe) + "_" + index;
    }

    /**
     * 收集 ALI 布局时生成的物品格子位置。
     * <p>
     * 不继承 ALI 的 {@code ClientUtils}：它的 {@code addSlotWidget(Either, IDataNode, RelativeRect)} 在 2.5.0 起是 {@code final}，
     * 而 2.4.x 只有这个三参数版本。这里直接实现接口，两种签名都接住，其余方法都委托给 ALI 的 clientRegistry
     * （与 {@code ClientUtils} 的做法相同）。
     */
    private static final class LootSlotCollector implements IWidgetUtils, IClientUtils {
        private final List<LootSlot> slots;

        private LootSlotCollector(List<LootSlot> slots) {
            this.slots = slots;
        }

        // ALI 2.4.x 的物品控件调用这个；2.5.0+ 的物品控件同样调用这个（交易控件才调用下面的 List 版本）
        @Override
        public void addSlotWidget(Either<ItemStack, TagKey<? extends ItemLike>> item, IDataNode entry, RelativeRect rect) {
            slots.add(new LootSlot(List.of(entry), rect));
        }

        // ALI 2.5.0+ 新增的接口方法；在 2.4.x 上只是一个不会被调用的普通方法
        public void addSlotWidget(List<IDataNode> options, RelativeRect rect) {
            if (!options.isEmpty()) {
                slots.add(new LootSlot(List.copyOf(options), rect));
            }
        }

        @NotNull
        @Override
        public String getModId() {
            return getClientRegistry().getModId();
        }

        @NotNull
        @Override
        public List<IWidget> createWidgets(IWidgetUtils utils, List<IDataNode> entries, RelativeRect parent, int maxWidth) {
            return getClientRegistry().createWidgets(utils, entries, parent, maxWidth);
        }

        @NotNull
        @Override
        public BiFunction<IClientUtils, FriendlyByteBuf, IDataNode> getDataNodeFactory(ResourceLocation id) {
            return getClientRegistry().getDataNodeFactory(id);
        }

        @NotNull
        @Override
        public List<Entity> createEntities(EntityType<?> type, Level level) {
            return getClientRegistry().createEntities(type, level);
        }

        @NotNull
        @Override
        public AliConfig getConfiguration() {
            return getClientRegistry().getConfiguration();
        }

        // 只在解码网络数据时使用，布局阶段用不到（与 ALI 自己的 JEI 实现一致）
        @Nullable
        @Override
        public String getTranslationKey(int index) {
            return null;
        }

        @NotNull
        @Override
        public TooltipNodePalette getTooltipCache() {
            return getClientRegistry().getTooltipCache();
        }
    }

    private record LootSlot(List<IDataNode> options, RelativeRect rect) {
        private IDataNode getDisplayedOption(ItemStack displayed) {
            if (options.size() > 1) {
                for (IDataNode option : options) {
                    if (((IItemNode) option).getItems().stream().anyMatch(stack -> ItemStack.isSameItemSameTags(stack, displayed))) {
                        return option;
                    }
                }
            }

            return options.get(0);
        }
    }

    private record AliLootRenderState(GameplayLootType type, JeiWidgetWrapper widgetWrapper, List<LootSlot> slots) {
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

    private record ShiftedAliScrollWidget(JeiScrollWidget widget,
                                          int yOffset) implements ISlottedRecipeWidget, IJeiInputHandler {
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
