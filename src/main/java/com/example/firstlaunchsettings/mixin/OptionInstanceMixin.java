package com.example.firstlaunchsettings.mixin;

import net.minecraft.client.NarratorStatus;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 設定値の変更入口で、ナレーターがOFF以外になる変更を拒否する。
 * Ctrl+B、設定画面、options.txt読み込み、他MODからの変更がすべて対象。
 */
@Mixin(OptionInstance.class)
public abstract class OptionInstanceMixin {

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    private void firstlaunchsettings$rejectNarrator(Object value, CallbackInfo ci) {
        if (value instanceof NarratorStatus status && status != NarratorStatus.OFF) {
            ci.cancel();
        }
    }
}