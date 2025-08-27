package net.yiran.jeitetra;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.yiran.jeitetra.effect.ItemEffectIngredientHelper;
import net.yiran.jeitetra.effect.ItemEffectIngredientRenderer;
import net.yiran.jeitetra.effect.ItemEffectIngredientTypeWithSubtypes;
import net.yiran.jeitetra.effect.ItemEffectSimpleRecipeManagerPlugin;
import net.yiran.jeitetra.material.MaterialIngredientHelper;
import net.yiran.jeitetra.material.MaterialIngredientRenderer;
import net.yiran.jeitetra.material.MaterialIngredientTypeWithSubtypes;
import net.yiran.jeitetra.material.MaterialSimpleRecipeManagerPlugin;
import net.yiran.jeitetra.recipe.ActionRecipeCategory;
import net.yiran.jeitetra.recipe.EffectRecipeCategory;
import net.yiran.jeitetra.recipe.MaterialRecipeCategory;
import net.yiran.jeitetra.recipe.ReplacementRecipeCategory;
import net.yiran.jeitetra.subtype.ModularSubtypeInterpreter;
import net.yiran.jeitetra.subtype.ScrollSubtypeInterpreter;
import se.mickelus.mutil.data.DataStore;
import se.mickelus.tetra.ClientScheduler;
import se.mickelus.tetra.ServerScheduler;
import se.mickelus.tetra.blocks.scroll.ScrollItem;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.effect.ItemEffect;

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
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(ItemEffectIngredientTypeWithSubtypes.INSTANCE,
                ShowAllEffects.get() ? getAllEffects() : List.of(),
                ItemEffectIngredientHelper.INSTANCE, ItemEffectIngredientRenderer.INSTANCE);
        registration.register(MaterialIngredientTypeWithSubtypes.INSTANCE,
                ShowAllMaterials.get() ? DataManager.instance.materialData.getData().values().stream().toList() : List.of()
                , MaterialIngredientHelper.INSTANCE, MaterialIngredientRenderer.INSTANCE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new ActionRecipeCategory(guiHelper),
                new ReplacementRecipeCategory(guiHelper),
                new EffectRecipeCategory(guiHelper),
                new MaterialRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        currentSchedule(() -> {
            try {
                var clazz = ItemEffect.class;
                var flied = clazz.getDeclaredField("effectMap");
                flied.setAccessible(true);
                registration.addRecipes(EffectRecipeCategory.recipeType, new ArrayList<>(((Map<String, ItemEffect>) flied.get(clazz)).values()));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            registration.addRecipes(ActionRecipeCategory.recipeType, flatData(DataManager.instance.actionData));
            registration.addRecipes(ReplacementRecipeCategory.recipeType, flatData(DataManager.instance.replacementData));
            registration.addRecipes(MaterialRecipeCategory.recipeType, DataManager.instance.materialData.getData().values().stream().toList());
        });
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

    public static List<ItemEffect> getAllEffects() {
        try {
            var clazz = ItemEffect.class;
            var field = clazz.getDeclaredField("effectMap");
            field.setAccessible(true);
            return new ArrayList<>(((Map<String, ItemEffect>) field.get(clazz)).values());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
