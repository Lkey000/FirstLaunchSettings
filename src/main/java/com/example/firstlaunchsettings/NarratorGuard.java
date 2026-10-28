package com.example.firstlaunchsettings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NarratorGuard {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new NarratorGuard());
    }

    private static void forceOff() {
        Options options = Minecraft.getInstance().options;
        if (options != null && options.narrator().get() != NarratorStatus.OFF) {
            options.narrator().set(NarratorStatus.OFF);
        }
    }

    // Mixinの保険：初期値がOFFでなかった場合などに戻す
    @SubscribeEvent
    public void onKey(InputEvent.Key event) {
        forceOff();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            forceOff();
        }
    }

    // 初回起動のアクセシビリティ案内画面をスキップ
    @SubscribeEvent
    public void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof AccessibilityOnboardingScreen) {
            Minecraft.getInstance().options.onboardAccessibility = false;
            event.setNewScreen(new TitleScreen());
        }
    }

    // 設定画面のナレーターボタンをグレーアウト
    @SubscribeEvent
    public void onScreenInit(ScreenEvent.Init.Post event) {
        Options options = Minecraft.getInstance().options;
        for (GuiEventListener listener : event.getListenersList()) {
            if (listener instanceof OptionsList list) {
                AbstractWidget widget = list.findOption(options.narrator());
                if (widget != null) {
                    widget.active = false;
                }
            }
        }
    }
}