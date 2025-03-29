package dev.armenderoian.summod.mixin;

import dev.armenderoian.summod.feature.death.DeathFeature;
import dev.armenderoian.summod.registry.ItemContent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow
    private ClientWorld world;

    @Inject(at = @At("TAIL"), method = "onEntityStatus")
    public void onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        Entity entity = packet.getEntity(this.world);
        MinecraftClient client = MinecraftClient.getInstance();

        if (entity != null) {
            if (packet.getStatus() == DeathFeature.USE_DEATH_RESET_ITEM_STATUS) {
                // TODO: Retexture the totem particle
                //client.particleManager.addEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
                this.world.playSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0f, 1.0f, false);
                if (entity == client.player)
                    client.gameRenderer.showFloatingItem(new ItemStack(ItemContent.DEATH_RESET_ITEM));
            }
        }
    }
}
