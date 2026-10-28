# First Launch Settings

Minecraft 1.20.1 (Forge) 用のクライアント専用MODです。

## 機能
- 新規プロファイルの初回起動時に、フルスクリーン・日本語を自動設定
- 音声読み上げ（ナレーター）を常にOFFに固定（Ctrl+Bなどの切り替えも無効）
- 初回起動のアクセシビリティ案内画面をスキップ

## 動作環境
- Minecraft 1.20.1 / Forge 47.x / Java 17
- クライアント専用（サーバーには不要）

## 注意
- 初回設定は `options.txt` が存在しない（＝新規プロファイル）場合のみ適用されます
- ナレーター固定は毎回の起動で有効です

## ビルド
    ./gradlew build

出力: `build/libs/firstlaunchsettings-1.0.0.jar`

## ライセンス
MIT