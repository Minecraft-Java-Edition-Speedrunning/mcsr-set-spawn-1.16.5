package net.set.spawn.mod.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.text.Text;
import net.set.spawn.mod.Conditionals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
    protected CreateWorldScreenMixin(Text title) {
        super(title);
    }
    @Inject(method = "createLevel", at = @At("HEAD"))
    public void setGeneratingTrue(CallbackInfo ci) {
        Conditionals.isAWorldGenerating = true;
    }
}
