package net.set.spawn.mod.mixin;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.DebugHud;
import net.set.spawn.mod.Conditionals;
import net.set.spawn.mod.config.SetSpawnProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin extends DrawableHelper {

    @Inject(method = "getRightText", at = @At("RETURN"))
    public void displaySetSpawnOnF3(CallbackInfoReturnable<List<String>> cir) {
        List<String> currentF3 = cir.getReturnValue();
        if (Conditionals.isModActive) {
            currentF3.add("Setting Spawn on seed " + SetSpawnProperties.seed + " to " + SetSpawnProperties.coordinates);
        }
    }

}
