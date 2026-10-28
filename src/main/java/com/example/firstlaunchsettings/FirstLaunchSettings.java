package com.example.firstlaunchsettings;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(FirstLaunchSettings.MODID)
public class FirstLaunchSettings {
    public static final String MODID = "firstlaunchsettings";

    public FirstLaunchSettings() {
        // クライアント専用クラスを直接参照しない（専用サーバーでの落下防止）
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientInit::init);
    }
}