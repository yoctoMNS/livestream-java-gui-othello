# livestream-java-gui-othello

放送内で1時間程度で実装できるボリュームを想定した、Java標準API(Swing / javax.sound.sampled / ImageIO)のみで作るオセロゲームの見本コードです。

## 特徴

- MVC + Command パターンによる責務分離
  - Model: `Board`, `Disc`, `Position`, `Direction`（ゲームルール）
  - View: `OthelloFrame`, `BoardPanel`（画面描画）
  - Controller: `GameController`（キー入力の振り分け）
  - Command: `GameCommand`, `MoveCursorCommand`, `PlaceDiscCommand`
- WASDキーでカーソル移動、Enterキーで石を配置
- カーソル移動・石の配置それぞれで効果音を再生（`javax.sound.sampled` でサイン波をその場生成、音声ファイル不要）
- 盤面・黒石・白石の画像は `resources/` にPNGとして格納し `ImageIO` で読み込み

## 実行方法

ビルドツールは使わず、`javac` / `java` を直接使います。

```sh
javac -d out $(find src -name "*.java")
java -cp out othello.Main
```

## 画像素材について

`resources/board.png` `resources/black.png` `resources/white.png` は、
`othello.PlaceholderImageGenerator`（`src/othello/PlaceholderImageGenerator.java`）を
一度だけ実行して生成したプレースホルダー画像です。実行時にこのツールが
呼ばれることはなく、生成済みのPNGファイルをそのまま `Main` が読み込みます。

再生成する場合:

```sh
javac -d out $(find src -name "*.java")
java -cp out othello.PlaceholderImageGenerator
```

## ルール（最小実装）

- 石を置いたマスから縦・横・斜めの8方向のいずれかで相手の石を挟める場合のみ着手可能
- 着手すると挟んだ相手の石をすべて反転
- 次の手番のプレイヤーが置ける場所を持たない場合は自動的にパス
- 両者とも置ける場所がなくなった時点でゲーム終了、石数の多い方の勝ち
