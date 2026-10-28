package com.example.firstlaunchsettings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.Options;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Files;

public class ClientInit {

    private static final String LANGUAGE = "ja_jp";

    public static void init() {
        // ナレーター封じは新規・既存プロファイルを問わず常に有効
        NarratorGuard.register();

        // options.txt が無ければ新規プロファイルとみなす
        boolean isNewProfile = !Files.exists(FMLPaths.GAMEDIR.get().resolve("options.txt"));
        if (isNewProfile) {
            MinecraftForge.EVENT_BUS.register(new ClientInit());
        }
    }

    private boolean applied = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || applied) return;

        Minecraft mc = Minecraft.getInstance();
        // 起動時のロード画面が終わるまで待つ
        if (mc.getOverlay() != null || mc.getWindow() == null) return;

        applied = true;
        MinecraftForge.EVENT_BUS.unregister(this);

        Options options = mc.options;

        options.narrator().set(NarratorStatus.OFF);
        options.fullscreen().set(true);

        if (!LANGUAGE.equals(options.languageCode)) {
            options.languageCode = LANGUAGE;
            mc.getLanguageManager().setSelected(LANGUAGE);
            mc.reloadResourcePacks();
        }

        // options.txt を書き出す（次回以降は新規扱いにならない）
        options.save();
    }
}