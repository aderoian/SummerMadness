package dev.armenderoian.summod.custom.item;

import dev.armenderoian.summod.SummerModded;
import dev.armenderoian.summod.feature.death.DeathFeature;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class DeathResetItem extends Item {
    public DeathResetItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return TypedActionResult.pass(user.getStackInHand(hand));

        if (user instanceof ServerPlayerEntity player) {
            if (DeathFeature.reduceDeathCount(player)) {
                player.getStackInHand(hand).decrement(1);
                world.sendEntityStatus(player, DeathFeature.USE_DEATH_RESET_ITEM_STATUS);
                return TypedActionResult.success(player.getStackInHand(hand));
            }
        }

        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.summermodded.death_reset_item.desc").setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(true)));
    }
}
