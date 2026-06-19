package net.yiran.jeitetra;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.*;
import com.yanny.ali.compatibility.common.GenericUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.yiran.jeitetra.ingredient.ImprovementDataIngredient;
import net.yiran.jeitetra.ingredient.ItemEffectIngredient;
import net.yiran.jeitetra.ingredient.MaterialDataIngredient;
import net.yiran.jeitetra.itemsubtype.ModularSubtypeInterpreter;
import net.yiran.jeitetra.itemsubtype.ScrollSubtypeInterpreter;
import net.yiran.jeitetra.recipe.*;
import net.yiran.jeitetra.recipemanager.ImprovementSimpleRecipeManagerPlugin;
import net.yiran.jeitetra.recipemanager.ItemEffectSimpleRecipeManagerPlugin;
import net.yiran.jeitetra.recipemanager.MaterialSimpleRecipeManagerPlugin;
import net.yiran.jeitetra.recipemanager.ModuleSimpleRecipeManagerPlugin;
import net.yiran.jeitetra.util.ItemUtil;
import net.yiran.jeitetra.util.ModuleData;
import net.yiran.jeitetra.util.helper.ImprovementDataHelper;
import se.mickelus.mutil.data.DataStore;
import se.mickelus.tetra.ClientScheduler;
import se.mickelus.tetra.ServerScheduler;
import se.mickelus.tetra.blocks.scroll.ScrollItem;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.module.ModuleRegistry;

import java.util.*;

import static net.yiran.jeitetra.Config.*;
import static net.yiran.jeitetra.JEITetra.TMRVLOAD;

@JeiPlugin
public class TetraPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(JEITetra.MODID, "common");
    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        registration.addTypedRecipeManagerPlugin(EffectRecipeCategory.recipeType, ItemEffectSimpleRecipeManagerPlugin.INSTANCE);
        registration.addTypedRecipeManagerPlugin(MaterialRecipeCategory.recipeType, MaterialSimpleRecipeManagerPlugin.INSTANCE);
        registration.addTypedRecipeManagerPlugin(ModuleRecipeCategory.recipeType, ModuleSimpleRecipeManagerPlugin.INSTANCE);
        registration.addTypedRecipeManagerPlugin(ImprovementDataRecipeCategory.recipeType, ImprovementSimpleRecipeManagerPlugin.INSTANCE);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        if (JeiSubtypeScrollItem.get())
            registration.registerSubtypeInterpreter(ScrollItem.instance, ScrollSubtypeInterpreter.INSTANCE);
        JeiSubtypeModularItem.get()
                .stream()
                .map(ResourceLocation::new)
                .map(ForgeRegistries.ITEMS::getValue)
                .filter(Objects::nonNull)
                .forEach(item -> registration.registerSubtypeInterpreter(item, ModularSubtypeInterpreter.INSTANCE));
    }

    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        var list = CreativeModeTabRegistry.getSortedCreativeModeTabs().stream()
                .map(CreativeModeTab::getDisplayItems)
                .flatMap(Collection::stream)
                .filter(stack -> stack.is(ScrollItem.instance))
                .toList();

        registration.addAliases(VanillaTypes.ITEM_STACK, list, "scroll");
        DataManager.instance.materialData.getData().values()
                .forEach(materialData -> {
                    var items = ItemUtil.getItemsFromItemPredicate(materialData.material.getPredicate());
                    if (items != null) {
                        registration.addAliases(VanillaTypes.ITEM_STACK, items, materialData.category);
                        registration.addAliases(VanillaTypes.ITEM_STACK, items, "material");
                    }
                });
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        ImprovementDataHelper.ClearCache();
        registration.register(ItemEffectIngredient.INSTANCE,
                ShowAllEffects.get() ? getAllEffects() : List.of(),
                ItemEffectIngredient.INSTANCE, ItemEffectIngredient.INSTANCE);
        registration.register(MaterialDataIngredient.INSTANCE,
                ShowAllMaterials.get() ? DataManager.instance.materialData.getData().values().stream().toList() : List.of(),
                MaterialDataIngredient.INSTANCE, MaterialDataIngredient.INSTANCE);
        registration.register(ImprovementDataIngredient.INSTANCE,
                ShowAllImprovements.get() ? flatData(DataManager.instance.improvementData) : List.of(),
                ImprovementDataIngredient.INSTANCE, ImprovementDataIngredient.INSTANCE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new ActionRecipeCategory(guiHelper),
                new ReplacementRecipeCategory(guiHelper),
                new EffectRecipeCategory(guiHelper),
                new MaterialRecipeCategory(guiHelper),
                new ModuleRecipeCategory(guiHelper),
                new ImprovementDataRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        currentSchedule(() -> {
            GenericUtils.register(registration, (recipeRegistration, fullCompressedData) -> ActionRecipeCategory.updateLootData(fullCompressedData));
            registration.addRecipes(EffectRecipeCategory.recipeType, getAllEffects());
            registration.addRecipes(ActionRecipeCategory.recipeType, flatData(DataManager.instance.actionData));
            registration.addRecipes(ReplacementRecipeCategory.recipeType,
                    DataManager.instance.replacementData.getData().values()
                            .stream()
                            .flatMap(Arrays::stream)
                            .filter(replacementDefinition -> {
                                if (FilterEmptyReplacement.get()) {
                                    var items = ItemUtil.getItemsFromItemPredicate(replacementDefinition.predicate);
                                    return items != null && !items.isEmpty();
                                }
                                return true;
                            })
                            .toList()
            );
            registration.addRecipes(ImprovementDataRecipeCategory.recipeType, flatData(DataManager.instance.improvementData));
            registration.addRecipes(MaterialRecipeCategory.recipeType, DataManager.instance.materialData.getData().values().stream().toList());
            registration.addRecipes(ModuleRecipeCategory.recipeType, ModuleRegistry.instance.getAllModules().stream().flatMap(module ->
                    Arrays.stream(module.getVariantData()).map(variantData -> new ModuleData(module, variantData))
            ).toList());
        });
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        var basicWorkbench = ForgeRegistries.ITEMS.getValue(new ResourceLocation("tetra:basic_workbench"));
        var forgedWorkbench = ForgeRegistries.ITEMS.getValue(new ResourceLocation("tetra:forged_workbench"));
        registration.addRecipeCatalysts(ActionRecipeCategory.recipeType, basicWorkbench, forgedWorkbench);
        registration.addRecipeCatalysts(ReplacementRecipeCategory.recipeType, basicWorkbench, forgedWorkbench);
    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        if (!OutputReplacementItem.get()) return;
        registration.addExtraItemStacks(
                DataManager.instance.replacementData
                        .getData()
                        .values()
                        .stream()
                        .flatMap(Arrays::stream)
                        .map(r -> r.itemStack)
                        .toList()
        );

    }

    public <T> List<T> flatData(DataStore<T[]> store) {
        return store
                .getData()
                .values()
                .stream()
                .flatMap(Arrays::stream)
                .toList();
    }

    public void currentSchedule(Runnable run) {
        if (TMRVLOAD) {
            run.run();
        } else if (FMLEnvironment.dist.isClient()) {
            ClientScheduler.schedule(1, run);
        } else {
            ServerScheduler.schedule(1, run);
        }
    }

    public static List<ItemEffect> getAllEffects() {
        return new ArrayList<>(ItemEffect.effectMap.values());
    }
}
