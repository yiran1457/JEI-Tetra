package net.yiran.jeitetra.compat.jade;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import se.mickelus.tetra.items.modular.impl.ModularDoubleHeadedItem;
import se.mickelus.tetra.util.ToolActionHelper;
import snownee.jade.addon.harvest.HarvestToolProvider;
import snownee.jade.addon.harvest.ToolHandler;

import java.util.List;

public class TetraHammerToolHandler implements ToolHandler {
    public List<ItemStack> tools = Lists.newArrayList();

    public static void init(){
        HarvestToolProvider.registerHandler(new TetraHammerToolHandler());
    }

    public TetraHammerToolHandler() {
        tools.addAll(ModularDoubleHeadedItem.getCreativeTabItemStacks());
    }

    @Override
    public ItemStack test(BlockState blockState, Level level, BlockPos blockPos) {
        if (matchesBlock(blockState)) {
            for (ItemStack tool : tools) {
                if (tool.isCorrectToolForDrops(blockState)) {
                    var tags = blockState.getTags().toList();
                    return tool;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean matchesBlock(BlockState state) {
        return state.is(ToolActionHelper.hammerMineable);
    }

    @Override
    public List<ItemStack> getTools() {
        return tools;
    }

    @Override
    public String getName() {
        return "jeitetra";
    }
}
