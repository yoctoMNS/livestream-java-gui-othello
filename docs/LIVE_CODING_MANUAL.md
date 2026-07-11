# オセロ見本プログラム 実装手順台本(配信用マニュアル)

このドキュメントは、`src/othello/` にあるオセロ見本プログラムを
**1時間程度の配信の中で、画面に何か映るところまでを最優先にしながら**
組み立てていくための「読み上げ台本 + 完成形サンプルコード」です。

視聴者から見て「今何を作っていて、なぜこの順番なのか」が分かるように、
各ステップは次の4つのブロックで構成しています。

- **🎙 台本**: そのまま読み上げられる語り口調の説明
- **🎯 このステップのゴール**: 何ができるようになるか
- **💻 コード**: そのステップで新しく書く・書き換えるファイルの完成形全文
- **✅ 確認ポイント**: 動かして確認できること、次に進む前に押さえておきたい話のネタ

## 全体の流れとタイムテーブル(合計 約60分)

「まず画面に何か表示する → ルールを実装する → 操作できるようにする → 仕上げる」
という順序にしているのは、配信の早い段階で視聴者に進捗が見えるようにするためです。
ルールから先に作ると、動くものが画面に出るまでの時間がどうしても長くなってしまいます。

| ステップ | 内容 | 目安時間 | マイルストーン |
|---|---|---|---|
| Step 0 | プロジェクトの準備・画像素材の生成 | 5分 | - |
| Step 1 | データの土台(`Disc`, `Position`)を作る | 5分 | - |
| Step 2 | 画面に初期配置を表示する | 15分 | 🏁 起動すると盤面が見える |
| Step 3 | オセロのルールを実装する | 15分 | - |
| Step 4 | WASD操作・Enter配置・効果音を実装する | 15分 | 🏁 実際に遊べる・音が鳴る |
| Step 5 | 通し動作確認・まとめ | 5分 | 完成 |

---

## Step 0. プロジェクトの準備・画像素材の生成 (5分)

### 🎙 台本

> 今日はJavaの標準APIだけを使って、オセロを1時間で作っていきます。
> Swing・javax.sound.sampled・ImageIOといった、JDKに最初から入っている機能だけで
> 完結させるので、追加のライブラリを一切インストールしなくて大丈夫です。
>
> まずはプロジェクトのフォルダ構成を作ります。`src/othello` にJavaのソースコードを、
> `resources` に画像ファイルを置く、というシンプルな構成にします。ビルドツール
> (MavenやGradle)も使いません。`javac` と `java` を直接叩いて、動かしながら進めます。
>
> 盤面や石の画像も自分たちで用意しますが、絵心がなくても大丈夫です。
> Graphics2Dで丸や四角を描くだけの「プレースホルダー画像」を、
> 専用のツールクラスで自動生成してしまいましょう。

### 🎯 このステップのゴール

- `src/othello/` フォルダと `resources/` フォルダを用意する
- 盤面のマス目画像(緑地・黒石入り・白石入り の3タイル)を **1枚のPNG画像** として生成する
- なぜ1枚の画像にまとめる(タイルマップにする)のかを説明できる

### 💻 コード: `src/othello/PlaceholderImageGenerator.java`

> 🎙 「このクラスは本番のゲーム実行では使いません。画像を1回だけ作るための
> 開発用ツールです。実行すると `resources/tiles.png` という、
> 横に3つのタイル(空きマス・黒石マス・白石マス)が並んだ画像ファイルが1枚できます。
> 1枚にまとめておくことで、あとで画像を読み込むコードもシンプルになります。」

```java
package othello;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/**
 * 盤面描画用のタイルマップ画像を生成し、resources フォルダに
 * PNG1枚として保存するための一回限りの開発用ツール。
 *
 * <p>本番のゲーム実行(Main)からは呼び出されない。実際の画像素材を
 * 用意する代わりに、この {@code main} メソッドを一度だけ実行して
 * {@code resources/tiles.png} を生成しておく、という位置づけである。</p>
 *
 * <p>生成される1枚の画像には、左から順に
 * 「空きマス(緑地のみ)」「黒石入りマス」「白石入りマス」の
 * 3タイルが横一列に並んでおり、{@link BoardImages} がこれを
 * タイルマップとして読み込み、マスごとに切り出して使う。</p>
 */
public final class PlaceholderImageGenerator {

    private static final int TILE_SIZE = BoardImages.TILE_SIZE;
    private static final int TILE_COUNT = 3;

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("resources"));
        saveTileSheetImage();
    }

    private static void saveTileSheetImage() throws IOException {
        BufferedImage sheet = new BufferedImage(TILE_SIZE * TILE_COUNT, TILE_SIZE,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = sheet.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawEmptyTile(g, 0);
        drawDiscTile(g, 1, Color.BLACK);
        drawDiscTile(g, 2, Color.WHITE);
        g.dispose();
        ImageIO.write(sheet, "png", new File("resources/tiles.png"));
    }

    private static void drawEmptyTile(Graphics2D g, int tileIndex) {
        int x = tileIndex * TILE_SIZE;
        g.setColor(new Color(0, 128, 0));
        g.fillRect(x, 0, TILE_SIZE, TILE_SIZE);
        g.setColor(Color.BLACK);
        g.drawRect(x, 0, TILE_SIZE - 1, TILE_SIZE - 1);
    }

    private static void drawDiscTile(Graphics2D g, int tileIndex, Color discColor) {
        drawEmptyTile(g, tileIndex);
        int x = tileIndex * TILE_SIZE;
        g.setColor(discColor);
        g.fillOval(x + 4, 4, TILE_SIZE - 8, TILE_SIZE - 8);
        g.setColor(Color.GRAY);
        g.drawOval(x + 4, 4, TILE_SIZE - 8, TILE_SIZE - 8);
    }
}
```

> ⚠️ この時点ではまだ `BoardImages` クラスが存在しないため、
> `BoardImages.TILE_SIZE` を参照している1行はコンパイルエラーになります。
> 台本では「あとでBoardImagesを作るときに、ここも一緒に繋がりますよ」と
> 一言添えておくと視聴者が混乱しません。先に `BoardImages` だけ最小限作ってから
> このツールを実行してもよいですし、`TILE_SIZE = 60` と直接書いた仮バージョンで
> 動かしてしまっても構いません。

### ✅ 確認ポイント

```sh
javac -d out $(find src -name "*.java")
java -cp out othello.PlaceholderImageGenerator
```

- `resources/tiles.png` (180×60ピクセル) が生成されることを確認する
- 画像ビューアで開き、左から「緑」「黒丸」「白丸」の3タイルが並んでいることを見せる
- 🎙 「これでゲーム画面の見た目の材料が揃いました。次はいよいよ、この画像を使って
  盤面を表示するコードを書いていきます。」

---

## Step 1. データの土台(`Disc`, `Position`)を作る (5分)

### 🎙 台本

> プログラムの中身に入る前に、まず「石の種類」と「マスの座標」という
> 一番基本的なデータの形を決めておきます。ここを最初にきっちり決めておくと、
> あとのコードがとても読みやすくなります。
>
> 石の種類は「空き」「黒」「白」の3つしかないので、booleanではなくenum(列挙型)
> で表現します。座標は行番号と列番号のペアなので、Javaのrecordという機能を使って
> 「ただの値」として表現します。

### 🎯 このステップのゴール

- 石の状態を表す `Disc` を実装する
- 盤面の座標を表す `Position` を実装する(このステップでは移動計算はまだ実装しない)

### 💻 コード: `src/othello/Disc.java`

```java
package othello;

/**
 * 盤面のマスに置かれる石の種類を表す列挙型。
 *
 * <p>オセロの石は「空きマス」「黒石」「白石」の3状態しか取らないため、
 * boolean や文字コードではなく列挙型として表現することで、
 * コンパイル時に不正な状態を作れないようにしている。</p>
 */
public enum Disc {
    EMPTY,
    BLACK,
    WHITE;

    /**
     * この石の対戦相手側の石を返す。
     *
     * <p>手番交代や、挟み判定(相手の石が続いているかどうか)のロジックで
     * 頻繁に使われるため、Board クラス側に判定式を重複させず
     * ここに一箇所だけ用意している。</p>
     *
     * @return 自分が BLACK なら WHITE、WHITE なら BLACK
     * @throws IllegalStateException EMPTY に対して呼び出した場合
     */
    public Disc opponent() {
        return switch (this) {
            case BLACK -> WHITE;
            case WHITE -> BLACK;
            case EMPTY -> throw new IllegalStateException("空きマスに対戦相手は存在しません");
        };
    }
}
```

### 💻 コード: `src/othello/Position.java` (このステップでの仮バージョン)

> 🎙 「Positionには本来『指定した方向に1マス進む』というmovedメソッドを
> 持たせる予定ですが、それには『方向』を表すDirectionクラスが必要です。
> Directionはルール実装のステップで作るので、いったんは
> 行番号・列番号を持つだけのシンプルな形にしておきます。」

```java
package othello;

/**
 * 盤面上の1マスの座標(行・列)を表す不変データ。
 *
 * <p>行(row)・列(col)ともに 0 から {@link Board#SIZE} - 1 の範囲を想定している。
 * record として定義することで、equals/hashCode/toString を自動生成させ、
 * 「座標を表すだけの値」であることを型で明示している。</p>
 *
 * @param row 上から数えた行番号(0始まり)
 * @param col 左から数えた列番号(0始まり)
 */
public record Position(int row, int col) {
}
```

### ✅ 確認ポイント

- この時点ではまだ `main` メソッドがないので実行はできない。コンパイルが通ることだけ
  頭の片隅に置いておく(次のStep 2でまとめてビルド確認する)。
- 🎙 「enumとrecordという2つの新しめの書き方を紹介しました。どちらも
  “値の種類・組み合わせを限定して、コンパイラに守ってもらう” という考え方は共通しています。」

---

## Step 2. 画面に初期配置を表示する (15分) 🏁最初のマイルストーン

### 🎙 台本

> ここが今日の最初の山場です。オセロのルールはまだ1行も書きませんが、
> 「起動すると盤面と初期配置の4つの石が表示される」ところまで一気に持っていきます。
> ルールより先に画面を作ることで、視聴者のみなさんにも早い段階で
> 「動いてる感」を見てもらえます。
>
> ここで作るBoardは、あとでルールを実装する時にガラッと拡張しますが、
> 今はまだ「盤面の状態を持っているだけの入れ物」で十分です。

### 🎯 このステップのゴール

- 画像を読み込んで保持する `BoardImages`
- 盤面の初期状態だけを持つ最小限の `Board`
- 盤面を描画する `BoardPanel`
- ウィンドウを組み立てる `OthelloFrame`
- すべてを繋いで起動する `Main`
- 🏁 **`java othello.Main` を実行すると、初期配置(中央に黒白2枚ずつ)が表示される**

### 💻 コード: `src/othello/BoardImages.java`

```java
package othello;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * 盤面描画に使うマス目画像(タイル)をまとめて保持するクラス。
 *
 * <p>画像ファイルは {@code resources/tiles.png} の1枚だけを用意してある。
 * この1枚には「空きマス(緑地のみ)」「黒石入りマス」「白石入りマス」の
 * 3つのタイルが横一列に並んでおり、タイルマップとして扱う。
 * このクラスは起動時に一度だけ {@link ImageIO} でシート全体を読み込み、
 * {@link BufferedImage#getSubimage} で3つのタイルに切り出しておくことで、
 * 描画のたびにファイルI/Oや画像分割が発生しないようにしている。</p>
 */
public final class BoardImages {

    /** タイルシート内の1タイルの一辺のピクセル数。 */
    public static final int TILE_SIZE = 60;

    private static final int EMPTY_TILE_INDEX = 0;
    private static final int BLACK_TILE_INDEX = 1;
    private static final int WHITE_TILE_INDEX = 2;

    private final BufferedImage emptyTile;
    private final BufferedImage blackTile;
    private final BufferedImage whiteTile;

    private BoardImages(BufferedImage emptyTile, BufferedImage blackTile, BufferedImage whiteTile) {
        this.emptyTile = emptyTile;
        this.blackTile = blackTile;
        this.whiteTile = whiteTile;
    }

    /**
     * resources フォルダのタイルシート画像(1枚)を読み込み、
     * 空きマス・黒石マス・白石マスの3タイルに切り出す。
     *
     * @return 切り出し済みのタイル一式
     * @throws IOException タイルシート画像が見つからない、または読み込みに失敗した場合
     */
    public static BoardImages loadFromResourceFiles() throws IOException {
        BufferedImage tileSheet = ImageIO.read(new File("resources/tiles.png"));
        return new BoardImages(
                cutOutTile(tileSheet, EMPTY_TILE_INDEX),
                cutOutTile(tileSheet, BLACK_TILE_INDEX),
                cutOutTile(tileSheet, WHITE_TILE_INDEX));
    }

    private static BufferedImage cutOutTile(BufferedImage tileSheet, int tileIndex) {
        return tileSheet.getSubimage(tileIndex * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE);
    }

    /**
     * 指定した石の状態に対応するマス目タイル画像を返す。
     * {@link Disc#EMPTY} の場合は緑地のみのタイルを返す。
     */
    public BufferedImage getTileFor(Disc disc) {
        return switch (disc) {
            case EMPTY -> emptyTile;
            case BLACK -> blackTile;
            case WHITE -> whiteTile;
        };
    }
}
```

### 💻 コード: `src/othello/Board.java` (このステップでの仮バージョン)

> 🎙 「今はまだ『置けるかどうかの判定』や『手番』は実装しません。
> “盤面の状態を8x8の配列で持っていて、外から読み取れる” というところだけ作ります。」

```java
package othello;

/**
 * (Step 3でルールを実装する前の、画面表示専用の仮バージョン)
 * オセロの盤面の状態だけを保持するクラス。
 */
public final class Board {

    /** 盤面の一辺のマス数。オセロは常に8x8。 */
    public static final int SIZE = 8;

    private final Disc[][] cells = new Disc[SIZE][SIZE];

    /** 初期配置(中央に黒白2枚ずつ)から始まる新しい対局を作成する。 */
    public Board() {
        setUpInitialFourDiscs();
    }

    /** 指定したマスにある石を返す。 */
    public Disc discAt(Position position) {
        return cells[position.row()][position.col()];
    }

    private void setUpInitialFourDiscs() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = Disc.EMPTY;
            }
        }
        cells[3][3] = Disc.WHITE;
        cells[3][4] = Disc.BLACK;
        cells[4][3] = Disc.BLACK;
        cells[4][4] = Disc.WHITE;
    }
}
```

### 💻 コード: `src/othello/BoardPanel.java` (このステップでの仮バージョン)

> 🎙 「BoardPanelはJPanelを継承した部品で、paintComponentというメソッドの中に
> “どう描くか” を書きます。カーソル(選択中のマス)を描く処理は、Cursorクラスを
> 作るStep 4で追加するので、今はタイルを敷き詰めるだけです。」

```java
package othello;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * (Step 4でカーソル表示を追加する前の、画面表示専用の仮バージョン)
 * 8x8の盤面のタイルだけを描画する部品。
 */
public final class BoardPanel extends JPanel {

    private static final int CELL_SIZE = BoardImages.TILE_SIZE;

    private final Board board;
    private final BoardImages images;

    public BoardPanel(Board board, BoardImages images) {
        this.board = board;
        this.images = images;
        setPreferredSize(new Dimension(CELL_SIZE * Board.SIZE, CELL_SIZE * Board.SIZE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawAllTiles(g);
    }

    private void drawAllTiles(Graphics g) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                drawTileAt(g, new Position(row, col));
            }
        }
    }

    private void drawTileAt(Graphics g, Position position) {
        Image tile = images.getTileFor(board.discAt(position));
        g.drawImage(tile, position.col() * CELL_SIZE, position.row() * CELL_SIZE,
                CELL_SIZE, CELL_SIZE, this);
    }
}
```

### 💻 コード: `src/othello/OthelloFrame.java` (このステップでの仮バージョン)

```java
package othello;

import javax.swing.JFrame;

/**
 * (Step 4でキー入力・状態表示を追加する前の、画面表示専用の仮バージョン)
 */
public final class OthelloFrame extends JFrame {

    private final BoardPanel boardPanel;

    public OthelloFrame(Board board, BoardImages images) {
        super("オセロ (画面表示のみ・まだ操作はできません)");
        this.boardPanel = new BoardPanel(board, images);
        add(boardPanel);
        configureWindow();
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /** ウィンドウを画面に表示する。 */
    public void showWindow() {
        setVisible(true);
    }
}
```

### 💻 コード: `src/othello/Main.java` (このステップでの仮バージョン)

```java
package othello;

import java.io.IOException;
import javax.swing.JOptionPane;

/**
 * (Step 4で完成させる前の、画面表示だけを行う仮バージョン)
 */
public final class Main {

    public static void main(String[] args) {
        try {
            startNewGame();
        } catch (IOException e) {
            showFatalErrorDialog(e);
        }
    }

    private static void startNewGame() throws IOException {
        Board board = new Board();
        BoardImages images = BoardImages.loadFromResourceFiles();
        OthelloFrame view = new OthelloFrame(board, images);
        view.showWindow();
    }

    private static void showFatalErrorDialog(IOException e) {
        JOptionPane.showMessageDialog(null,
                "画像の読み込みに失敗しました。resources フォルダを確認してください: " + e.getMessage());
    }
}
```

### ✅ 確認ポイント

```sh
javac -d out $(find src -name "*.java")
java -cp out othello.Main
```

- ウィンドウが開き、緑の盤面の中央に黒白2枚ずつの初期配置が表示されることを確認する
- 🎙 「見てください、これでもうオセロっぽい画面が出ました。ルールはまだ1行も
  書いていませんが、“動いているものを見せながら作る” というのが今日のコツです。
  ここから先は、この画面に少しずつ機能を足していく作業になります。」
- 話のネタ: なぜBoard・BoardPanel・OthelloFrameを分けているか(MVCで
  「データ」「見た目」「入口」の役割を分離している、という設計意図に軽く触れる)

---

## Step 3. オセロのルールを実装する (15分)

### 🎙 台本

> ここからが本題、オセロのルールです。実装するのは次の4つだけです。
>
> 1. 石を置いたマスの8方向のどこかで、相手の石を挟めるときだけ置ける
> 2. 置いたら、挟めた方向の相手の石をすべて反転させる
> 3. 次の人が置ける場所がなければパス
> 4. 両方とも置ける場所がなければ終了、石数の多い方の勝ち
>
> このロジックのために、まず「方向」を表すDirectionというenumを作ります。
> 縦・横・斜めの8方向を、それぞれ「行の増分・列の増分」という2つの数字で表現します。
> こうしておくと、8方向のループが `for (Direction d : Direction.values())` の
> たった1行で書けるようになります。
>
> Boardクラスは今日の実装の中で一番大きくなりますが、中心となる
> `placeDiscAt` メソッドだけ見れば「判定する→反転する→手番を進める」という
> 3行で流れが読めるようにしています。挟み判定や反転の細かいアルゴリズムは
> すべてprivateメソッドの中に隠していて、外からは呼べないようにしています。
> これはクラスの「使い方」と「実装の詳細」を分ける、とても大事な考え方です。

### 🎯 このステップのゴール

- 8方向を表す `Direction` を実装する
- `Position` に「指定方向へ1マス進む」機能を追加する
- `Board` にオセロのルールをすべて実装する(仮バージョンから完成形に差し替える)

### 💻 コード: `src/othello/Direction.java`

```java
package othello;

/**
 * 盤面上で石を挟めるかどうかを調べるための8方向を表す列挙型。
 *
 * <p>オセロのルールでは、石を置いたマスから縦・横・斜めの8方向すべてを
 * 走査して相手の石を挟めているか確認する必要がある。
 * この繰り返し処理を {@code for (Direction d : Direction.values())} という
 * 1行で書けるようにするために、方向そのものを列挙型として切り出している。</p>
 */
public enum Direction {
    NORTH(-1, 0),
    SOUTH(1, 0),
    EAST(0, 1),
    WEST(0, -1),
    NORTH_EAST(-1, 1),
    NORTH_WEST(-1, -1),
    SOUTH_EAST(1, 1),
    SOUTH_WEST(1, -1);

    private final int rowDelta;
    private final int colDelta;

    Direction(int rowDelta, int colDelta) {
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
    }

    /** この方向に1マス進むときの行番号の増分。 */
    public int rowDelta() {
        return rowDelta;
    }

    /** この方向に1マス進むときの列番号の増分。 */
    public int colDelta() {
        return colDelta;
    }
}
```

### 💻 コード: `src/othello/Position.java` (完成版に更新)

> 🎙 「Directionができたので、Positionに `moved` メソッドを追加します。
> recordは値を変更できないので、“今の座標を書き換える” のではなく
> “進んだ先の新しい座標を作って返す” という形になります。」

```java
package othello;

/**
 * 盤面上の1マスの座標(行・列)を表す不変データ。
 *
 * <p>行(row)・列(col)ともに 0 から {@link Board#SIZE} - 1 の範囲を想定している。
 * record として定義することで、equals/hashCode/toString を自動生成させ、
 * 「座標を表すだけの値」であることを型で明示している。</p>
 *
 * @param row 上から数えた行番号(0始まり)
 * @param col 左から数えた列番号(0始まり)
 */
public record Position(int row, int col) {

    /**
     * この座標から指定した方向に1マス進んだ座標を返す。
     *
     * @param direction 進む方向
     * @return 移動後の座標(盤面の範囲外になる場合もある)
     */
    public Position moved(Direction direction) {
        return new Position(row + direction.rowDelta(), col + direction.colDelta());
    }
}
```

### 💻 コード: `src/othello/Board.java` (完成版に差し替え)

```java
package othello;

/**
 * オセロの盤面とルールを管理するモデルクラス(MVC における Model)。
 *
 * <p>このクラスは「8x8の盤面に石を置いたときに何が起こるか」という
 * ゲームルールのみを知っており、画面描画やキー入力については
 * 一切関知しない。View・Controller から独立してテストできることを
 * 意図した設計になっている(関心の分離 / SOLIDの単一責任原則)。</p>
 *
 * <p>実装している最小限のルールは次のとおり:</p>
 * <ul>
 *   <li>石を置けるのは、その場所を起点に少なくとも1方向で
 *       相手の石を挟んで自分の色に変えられる場合のみ</li>
 *   <li>石を置いたら、挟めるすべての方向の相手の石を反転させる</li>
 *   <li>次の手番のプレイヤーが置ける場所を持たない場合はパスする</li>
 *   <li>両者とも置ける場所がない場合はゲーム終了とし、石数で勝敗を決める</li>
 * </ul>
 */
public final class Board {

    /** 盤面の一辺のマス数。オセロは常に8x8。 */
    public static final int SIZE = 8;

    private final Disc[][] cells = new Disc[SIZE][SIZE];
    private Disc currentTurn;
    private boolean gameOver;
    private String statusMessage;

    /**
     * 初期配置(中央に黒白2枚ずつ)から始まる新しい対局を作成する。
     */
    public Board() {
        setUpInitialFourDiscs();
        currentTurn = Disc.BLACK;
        statusMessage = "黒の番です";
    }

    /**
     * 現在の手番のプレイヤーとして、指定したマスに石を置く。
     *
     * <p>これがこのクラスの中心となるメソッドで、
     * 「置けるか判定する→反転させる→手番を進める」という
     * 一連の流れをそのまま読める順序で呼び出している。
     * 個々のアルゴリズムの詳細(挟み判定や反転処理)は
     * private メソッドの中に隠蔽している。</p>
     *
     * @param position 石を置きたいマスの座標
     * @return 実際に石を置けた場合は true、置けなかった場合は false
     */
    public boolean placeDiscAt(Position position) {
        if (gameOver || !isLegalMove(position, currentTurn)) {
            return false;
        }
        flipCapturedDiscs(position, currentTurn);
        cells[position.row()][position.col()] = currentTurn;
        advanceToNextTurn();
        return true;
    }

    /** 指定したマスにある石を返す。 */
    public Disc discAt(Position position) {
        return cells[position.row()][position.col()];
    }

    /** 現在の手番の石の色を返す。 */
    public Disc getCurrentTurn() {
        return currentTurn;
    }

    /** 画面上部に表示すべき状態メッセージ(手番・パス・勝敗)を返す。 */
    public String getStatusMessage() {
        return statusMessage;
    }

    /** ゲームが終了している(両者ともに置ける場所がない)かどうかを返す。 */
    public boolean isGameOver() {
        return gameOver;
    }

    private void setUpInitialFourDiscs() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = Disc.EMPTY;
            }
        }
        cells[3][3] = Disc.WHITE;
        cells[3][4] = Disc.BLACK;
        cells[4][3] = Disc.BLACK;
        cells[4][4] = Disc.WHITE;
    }

    /**
     * 指定したプレイヤーが指定したマスに石を置けるかどうかを判定する。
     *
     * <p>空きマスであり、かつ8方向のうち少なくとも1方向で
     * 相手の石を挟める場合にのみ合法手となる。</p>
     */
    private boolean isLegalMove(Position position, Disc player) {
        if (discAt(position) != Disc.EMPTY) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            if (canCaptureInDirection(position, player, direction)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 指定した方向に相手の石が1枚以上連続し、その先に自分の石があるかどうかを調べる。
     * これが成り立つ場合、その方向の相手の石をすべて反転できる。
     */
    private boolean canCaptureInDirection(Position position, Disc player, Direction direction) {
        Position current = position.moved(direction);
        boolean sawOpponentDisc = false;
        while (isInsideBoard(current) && discAt(current) == player.opponent()) {
            sawOpponentDisc = true;
            current = current.moved(direction);
        }
        return sawOpponentDisc && isInsideBoard(current) && discAt(current) == player;
    }

    private void flipCapturedDiscs(Position position, Disc player) {
        for (Direction direction : Direction.values()) {
            if (canCaptureInDirection(position, player, direction)) {
                flipDiscsInDirection(position, player, direction);
            }
        }
    }

    private void flipDiscsInDirection(Position position, Disc player, Direction direction) {
        Position current = position.moved(direction);
        while (discAt(current) == player.opponent()) {
            cells[current.row()][current.col()] = player;
            current = current.moved(direction);
        }
    }

    /**
     * 手番を次のプレイヤーに進める。
     *
     * <p>次のプレイヤーが置ける場所を持たない場合はパスとして
     * 現在のプレイヤーの手番を継続させ、双方とも置ける場所がなければ
     * ゲームを終了させて勝敗メッセージを組み立てる。</p>
     */
    private void advanceToNextTurn() {
        Disc next = currentTurn.opponent();
        if (hasAnyLegalMove(next)) {
            currentTurn = next;
            statusMessage = discName(next) + "の番です";
        } else if (hasAnyLegalMove(currentTurn)) {
            statusMessage = discName(next) + "は置ける場所がないためパスします";
        } else {
            gameOver = true;
            statusMessage = buildResultMessage();
        }
    }

    private boolean hasAnyLegalMove(Disc player) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (isLegalMove(new Position(row, col), player)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String buildResultMessage() {
        int blackCount = countDiscs(Disc.BLACK);
        int whiteCount = countDiscs(Disc.WHITE);
        String scoreText = String.format("(黒 %d - 白 %d)", blackCount, whiteCount);
        if (blackCount > whiteCount) {
            return "黒の勝ちです " + scoreText;
        } else if (whiteCount > blackCount) {
            return "白の勝ちです " + scoreText;
        } else {
            return "引き分けです " + scoreText;
        }
    }

    private int countDiscs(Disc target) {
        int count = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (cells[row][col] == target) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isInsideBoard(Position position) {
        return position.row() >= 0 && position.row() < SIZE
                && position.col() >= 0 && position.col() < SIZE;
    }

    private String discName(Disc disc) {
        return disc == Disc.BLACK ? "黒" : "白";
    }
}
```

### ✅ 確認ポイント

- この時点ではまだキー入力がないため、`placeDiscAt` を画面から呼び出して
  目視で確認することはできません。台本ではここを正直に伝えます。
- 🎙 「ルールは書けましたが、まだ操作する手段がありません。次のステップで
  WASDとEnterのキー入力を実装して、実際に自分の目で動きを確認しましょう。」
- 話のネタ: `canCaptureInDirection` が「挟めるかどうか」を調べるだけの
  “判定専用” メソッドになっていて、実際に反転する `flipDiscsInDirection` とは
  役割を分けている(判定と実行を分離することで、テストしやすく・読みやすくしている)。

---

## Step 4. WASD操作・Enter配置・効果音を実装する (15分) 🏁2つ目のマイルストーン

### 🎙 台本

> ルールができたので、いよいよ操作できるようにします。ここではCommandパターンという
> 設計手法を使います。「どのキーが押されたか」と「押されたときに何をするか」を分けて考え、
> キーごとに1つずつ「コマンド」というオブジェクトを用意します。
>
> こうしておくメリットは、GameControllerというクラスが
> 「キーコードとコマンドの対応表を持つだけ」のとてもシンプルな作りになることです。
> 将来「Rキーでリセットする」のような機能を足したくなっても、
> 新しいコマンドクラスを1つ追加するだけで済みます。既存のコードを変更しなくていい、
> というのがSOLID原則でいう「オープン・クローズドの原則」です。
>
> 効果音は、WAVファイルなどの音声素材を一切使わず、javax.sound.sampledという
> 標準APIだけでその場でサイン波(電子音)を生成して鳴らします。これも画像と同じく、
> 「素材を用意する手間をなくして、標準APIだけで完結させる」という今日のテーマに沿っています。

### 🎯 このステップのゴール

- カーソル位置を管理する `Cursor`
- 効果音を鳴らす `GameSoundEffects`
- キー入力1回分の操作を表す `GameCommand` / `MoveCursorCommand` / `PlaceDiscCommand`
- キー入力を振り分ける `GameController`
- `OthelloFrame` にカーソル表示・キー入力受付・状態メッセージ表示を追加
- `BoardPanel` にカーソルのハイライト表示を追加
- `Main` を完成させる
- 🏁 **WASDでカーソルが動き、Enterで石が置け、それぞれ効果音が鳴る**

### 💻 コード: `src/othello/Cursor.java`

```java
package othello;

/**
 * プレイヤーがWASDキーで動かす「今どのマスを選択しているか」を表す状態。
 *
 * <p>これはオセロのルールそのものではなく、UI操作のための状態なので
 * {@link Board} には持たせず、独立したクラスとして切り出している
 * (盤面ロジックと画面操作の関心を分離するため)。</p>
 */
public final class Cursor {

    private int row;
    private int col;

    /** 盤面中央付近のマスを選択した状態で開始する。 */
    public Cursor() {
        this.row = 3;
        this.col = 3;
    }

    /**
     * カーソルを指定した方向に1マス動かす。
     *
     * <p>盤面の外に出ようとした場合は端で止まり、
     * 例外を発生させたり無効な座標を保持したりしないようにしている。</p>
     *
     * @param rowDelta 行方向の移動量(上へ移動なら -1、下へ移動なら +1)
     * @param colDelta 列方向の移動量(左へ移動なら -1、右へ移動なら +1)
     */
    public void moveBy(int rowDelta, int colDelta) {
        row = clampToBoardRange(row + rowDelta);
        col = clampToBoardRange(col + colDelta);
    }

    /** 現在カーソルが指しているマスの座標を返す。 */
    public Position getPosition() {
        return new Position(row, col);
    }

    private int clampToBoardRange(int value) {
        return Math.max(0, Math.min(Board.SIZE - 1, value));
    }
}
```

### 💻 コード: `src/othello/GameSoundEffects.java`

```java
package othello;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * カーソル移動時と石の配置時に鳴らす効果音を担当するクラス。
 *
 * <p>効果音ファイル(WAV等)は一切使わず、{@code javax.sound.sampled} だけを
 * 使ってその場で短いサイン波(単純な電子音)を生成し再生している。
 * こうすることで音声アセットを用意する手間をなくし、Java標準APIのみで
 * 完結させている。移動音より配置音のほうが低く長い音になるように
 * 周波数と長さを変えて、操作の違いが耳でも分かるようにしている。</p>
 */
public final class GameSoundEffects {

    private static final float SAMPLE_RATE = 44_100f;

    /** カーソル移動時の短く高い音を鳴らす。 */
    public void playCursorMoveSound() {
        playTone(880.0, 40);
    }

    /** 石を配置できたときの少し長く低い音を鳴らす。 */
    public void playDiscPlacedSound() {
        playTone(440.0, 120);
    }

    /**
     * 指定した周波数・長さのサイン波を生成し、別スレッドで再生する。
     *
     * <p>Swing のイベント処理スレッド(EDT)上で音声再生をブロッキングで
     * 行うと画面がカクつくため、専用のデーモンスレッドで再生している。</p>
     */
    private void playTone(double frequencyHz, int durationMs) {
        Thread soundThread = new Thread(() -> {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            byte[] samples = generateSineWaveSamples(frequencyHz, durationMs, format);
            try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
                line.open(format);
                line.start();
                line.write(samples, 0, samples.length);
                line.drain();
            } catch (LineUnavailableException e) {
                // 音声デバイスが使えない環境では効果音を諦めてゲーム自体は続行する。
            }
        });
        soundThread.setDaemon(true);
        soundThread.start();
    }

    private byte[] generateSineWaveSamples(double frequencyHz, int durationMs, AudioFormat format) {
        int sampleCount = (int) (format.getSampleRate() * durationMs / 1000.0);
        byte[] samples = new byte[sampleCount * 2];
        for (int i = 0; i < sampleCount; i++) {
            double angle = 2 * Math.PI * i * frequencyHz / format.getSampleRate();
            short value = (short) (Math.sin(angle) * Short.MAX_VALUE * 0.6);
            samples[i * 2] = (byte) (value & 0xFF);
            samples[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
        }
        return samples;
    }
}
```

### 💻 コード: `src/othello/GameCommand.java`

```java
package othello;

/**
 * プレイヤーのキー入力1回分に対応する操作を表すコマンド(Commandパターン)。
 *
 * <p>「どのキーが押されたか」と「押されたときに何をするか」を分離するために
 * このインタフェースを導入している。{@link GameController} はキーコードから
 * GameCommand への対応表を持つだけでよく、キー入力処理を追加・変更したい場合も
 * 新しい GameCommand の実装クラスを1つ追加するだけで済む(拡張に対して開かれ、
 * 修正に対して閉じている = SOLIDのOpen/Closed原則)。</p>
 */
public interface GameCommand {

    /** このコマンドが表す操作を実行する。 */
    void execute();
}
```

### 💻 コード: `src/othello/MoveCursorCommand.java`

```java
package othello;

/**
 * WASDキーの入力に対応する「カーソルを1マス動かす」コマンド。
 *
 * <p>移動量(rowDelta, colDelta)をコンストラクタで受け取ることで、
 * 上下左右4つの動きをすべて同じクラスで表現できるようにしている。</p>
 */
public final class MoveCursorCommand implements GameCommand {

    private final Cursor cursor;
    private final int rowDelta;
    private final int colDelta;
    private final OthelloFrame view;
    private final GameSoundEffects soundEffects;

    public MoveCursorCommand(Cursor cursor, int rowDelta, int colDelta,
                              OthelloFrame view, GameSoundEffects soundEffects) {
        this.cursor = cursor;
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
        this.view = view;
        this.soundEffects = soundEffects;
    }

    /**
     * カーソルを移動させ、移動音を鳴らしたうえで画面を再描画する。
     */
    @Override
    public void execute() {
        cursor.moveBy(rowDelta, colDelta);
        soundEffects.playCursorMoveSound();
        view.refreshBoardDisplay();
    }
}
```

### 💻 コード: `src/othello/PlaceDiscCommand.java`

```java
package othello;

/**
 * Enterキーの入力に対応する「カーソルの位置に石を置く」コマンド。
 */
public final class PlaceDiscCommand implements GameCommand {

    private final Board board;
    private final Cursor cursor;
    private final OthelloFrame view;
    private final GameSoundEffects soundEffects;

    public PlaceDiscCommand(Board board, Cursor cursor, OthelloFrame view,
                             GameSoundEffects soundEffects) {
        this.board = board;
        this.cursor = cursor;
        this.view = view;
        this.soundEffects = soundEffects;
    }

    /**
     * カーソル位置への配置をモデルに依頼し、成功した場合のみ配置音を鳴らす。
     * 置けないマスだった場合は何も起きない(不正な手は静かに無視する)。
     */
    @Override
    public void execute() {
        boolean placed = board.placeDiscAt(cursor.getPosition());
        if (placed) {
            soundEffects.playDiscPlacedSound();
        }
        view.refreshBoardDisplay();
    }
}
```

### 💻 コード: `src/othello/GameController.java`

```java
package othello;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

/**
 * キーボード入力を受け取り、対応する {@link GameCommand} を実行するコントローラ
 * (MVC における Controller)。
 *
 * <p>キーコードとコマンドの対応表を持つだけで、移動やゲームルールの詳細は
 * 一切知らない。実際の処理は Command / Model 側に委譲することで、
 * このクラスの責務を「入力の振り分け」だけに絞っている。</p>
 */
public final class GameController implements KeyListener {

    private final Map<Integer, GameCommand> keyToCommand = new HashMap<>();

    /**
     * WASD(移動)とEnter(配置)のキー割り当てを構築する。
     */
    public GameController(Board board, Cursor cursor, OthelloFrame view,
                           GameSoundEffects soundEffects) {
        registerCursorMovementKeys(cursor, view, soundEffects);
        registerDiscPlacementKey(board, cursor, view, soundEffects);
    }

    private void registerCursorMovementKeys(Cursor cursor, OthelloFrame view,
                                             GameSoundEffects soundEffects) {
        keyToCommand.put(KeyEvent.VK_W, new MoveCursorCommand(cursor, -1, 0, view, soundEffects));
        keyToCommand.put(KeyEvent.VK_S, new MoveCursorCommand(cursor, 1, 0, view, soundEffects));
        keyToCommand.put(KeyEvent.VK_A, new MoveCursorCommand(cursor, 0, -1, view, soundEffects));
        keyToCommand.put(KeyEvent.VK_D, new MoveCursorCommand(cursor, 0, 1, view, soundEffects));
    }

    private void registerDiscPlacementKey(Board board, Cursor cursor, OthelloFrame view,
                                           GameSoundEffects soundEffects) {
        keyToCommand.put(KeyEvent.VK_ENTER, new PlaceDiscCommand(board, cursor, view, soundEffects));
    }

    /** 押されたキーに対応するコマンドがあれば実行する。 */
    @Override
    public void keyPressed(KeyEvent event) {
        GameCommand command = keyToCommand.get(event.getKeyCode());
        if (command != null) {
            command.execute();
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        // このゲームではキーを離した瞬間の処理は不要。
    }

    @Override
    public void keyTyped(KeyEvent event) {
        // 文字入力(IME等)は扱わないため何もしない。
    }
}
```

### 💻 コード: `src/othello/BoardPanel.java` (完成版に差し替え・カーソル表示を追加)

```java
package othello;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * 8x8の盤面・石・カーソルを描画する部品(MVC における View の一部)。
 *
 * <p>このクラスは {@link Board} と {@link Cursor} の状態を読み取って
 * 描画するだけで、状態を変更することは一切ない。画面描画の責務のみを
 * 持たせることで、ゲームルール({@link Board})や入力処理
 * ({@link GameController})から独立させている。</p>
 *
 * <p>背景・黒石・白石を別々の画像として重ね描きするのではなく、
 * {@link BoardImages} が切り出す「マスの状態ごとのタイル画像」を
 * そのマスにそのまま1枚敷き詰めるタイルマップ方式で描画している。</p>
 */
public final class BoardPanel extends JPanel {

    private static final int CELL_SIZE = BoardImages.TILE_SIZE;

    private final Board board;
    private final Cursor cursor;
    private final BoardImages images;

    public BoardPanel(Board board, Cursor cursor, BoardImages images) {
        this.board = board;
        this.cursor = cursor;
        this.images = images;
        setPreferredSize(new Dimension(CELL_SIZE * Board.SIZE, CELL_SIZE * Board.SIZE));
    }

    /**
     * 盤面全体を「マスのタイル → カーソル」の順に重ねて描画する。
     * 描画順を上から下に読むだけで全体の見た目が組み立てられるようにしている。
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawAllTiles(g);
        drawCursorHighlight(g);
    }

    private void drawAllTiles(Graphics g) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                drawTileAt(g, new Position(row, col));
            }
        }
    }

    private void drawTileAt(Graphics g, Position position) {
        Image tile = images.getTileFor(board.discAt(position));
        g.drawImage(tile, position.col() * CELL_SIZE, position.row() * CELL_SIZE,
                CELL_SIZE, CELL_SIZE, this);
    }

    private void drawCursorHighlight(Graphics g) {
        Position position = cursor.getPosition();
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.RED);
        g2.setStroke(new java.awt.BasicStroke(3));
        g2.drawRect(position.col() * CELL_SIZE + 2, position.row() * CELL_SIZE + 2,
                CELL_SIZE - 4, CELL_SIZE - 4);
    }
}
```

### 💻 コード: `src/othello/OthelloFrame.java` (完成版に差し替え)

```java
package othello;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * ゲームウィンドウ全体を組み立てるトップレベルのView(MVC における View)。
 *
 * <p>状態メッセージ用のラベルと盤面パネルを配置するだけの薄いクラスにし、
 * 描画の詳細は {@link BoardPanel} に、ゲームルールは {@link Board} に
 * それぞれ委譲している。</p>
 */
public final class OthelloFrame extends JFrame {

    private final Board board;
    private final JLabel statusLabel;
    private final BoardPanel boardPanel;

    public OthelloFrame(Board board, Cursor cursor, BoardImages images) {
        super("オセロ (WASDで移動 / Enterで配置)");
        this.board = board;
        this.statusLabel = new JLabel(board.getStatusMessage(), SwingConstants.CENTER);
        this.boardPanel = new BoardPanel(board, cursor, images);
        layoutComponents();
        configureWindow();
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        add(statusLabel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /**
     * モデルの状態変化を画面に反映する。石を置いた・カーソルを動かした等、
     * 盤面の見た目に影響する操作のたびに Controller から呼び出される。
     */
    public void refreshBoardDisplay() {
        statusLabel.setText(board.getStatusMessage());
        boardPanel.repaint();
    }

    /** このウィンドウでキー入力を受け付け始める。 */
    public void startListeningForKeyInput(GameController controller) {
        addKeyListener(controller);
        setFocusable(true);
        requestFocusInWindow();
    }

    /** ウィンドウを画面に表示する。 */
    public void showWindow() {
        setVisible(true);
    }
}
```

### 💻 コード: `src/othello/Main.java` (完成版に差し替え)

> 🎙 「Mainのstartメソッドは、日本語の文章のように上から読めるように書いています。
> “盤面を作る → カーソルを作る → 画像を読み込む → ウィンドウを作る →
> 効果音を用意する → キー入力とコマンドを結びつける → 表示する” という
> 起動の流れが、そのままコードの並びになっています。」

```java
package othello;

import java.io.IOException;
import javax.swing.JOptionPane;

/**
 * アプリケーションの起動クラス。
 *
 * <p>{@link #main(String[])} は「新しい対局を始める」という
 * 1つのことだけを行い、実際の準備手順は {@link #startNewGame()} に
 * 追い出している。{@code startNewGame} を読むだけで
 * 「盤面を作る → 画像を読み込む → ウィンドウを作る → 効果音を用意する →
 * キー入力とコマンドを結びつける → 表示する」という起動の流れが
 * 日本語の文章のようにそのまま読めることを意図している。</p>
 */
public final class Main {

    public static void main(String[] args) {
        try {
            startNewGame();
        } catch (IOException e) {
            showFatalErrorDialog(e);
        }
    }

    private static void startNewGame() throws IOException {
        Board board = new Board();
        Cursor cursor = new Cursor();
        BoardImages images = BoardImages.loadFromResourceFiles();
        OthelloFrame view = new OthelloFrame(board, cursor, images);
        GameSoundEffects soundEffects = new GameSoundEffects();
        GameController controller = new GameController(board, cursor, view, soundEffects);
        view.startListeningForKeyInput(controller);
        view.showWindow();
    }

    private static void showFatalErrorDialog(IOException e) {
        JOptionPane.showMessageDialog(null,
                "画像の読み込みに失敗しました。resources フォルダを確認してください: " + e.getMessage());
    }
}
```

### ✅ 確認ポイント

```sh
javac -d out $(find src -name "*.java")
java -cp out othello.Main
```

- ウィンドウをクリックしてフォーカスを合わせ、WASDでカーソル(赤い枠)が動くことを確認する
  (移動のたびに短い高い音が鳴る)
- カーソルを黒石の最初の合法手(例: 中央下の空きマス)に合わせてEnterを押し、
  石が置かれて相手の石が反転し、上部のラベルが「白の番です」に変わることを確認する
  (配置のたびに少し低く長い音が鳴る)
- 合法手ではないマスでEnterを押しても何も起きないことを確認する
- 🎙 「これで最初に見せたい機能はすべて揃いました。WASDで動いて、Enterで置けて、
  音も鳴って、ルールも正しく判定されています。」

---

## Step 5. 通し動作確認・まとめ (5分)

### 🎙 台本

> 最後に、今日作ったものを振り返りましょう。
>
> - Model(Board・Disc・Position・Direction)がオセロのルールだけを知っている
> - View(OthelloFrame・BoardPanel・BoardImages)が描画だけを担当している
> - Controller(GameController)がキー入力の振り分けだけをしている
> - Command(GameCommand・MoveCursorCommand・PlaceDiscCommand)が
>   「何かのキーが押されたときの1つの操作」をオブジェクトとして表現している
>
> それぞれのクラスが「1つの役割」だけを持つように分けたので、
> 例えば「音を別の音に差し替えたい」ときはGameSoundEffectsだけを触ればいいですし、
> 「見た目を変えたい」ときはBoardPanelだけを触ればいい、という状態になっています。
> これがSOLID原則の「単一責任の原則」です。

### 🎯 このステップのゴール

- 一通り遊んでみて、パス・勝敗判定まで動作することを確認する
- 発展課題を紹介して締めくくる

### ✅ 最終動作確認チェックリスト

- [ ] 起動直後に初期配置(中央に黒白2枚ずつ)が表示される
- [ ] WASDでカーソルが盤面の端で止まり、外に出ない
- [ ] 合法手にEnterで石が置け、相手の石が反転する
- [ ] 合法手がない側は自動的にパスし、ラベルに「〇〇はパスします」と出る
- [ ] 両者とも置けなくなった時点で「〇〇の勝ちです (黒 xx - 白 yy)」と表示される
- [ ] カーソル移動・石の配置それぞれで異なる効果音が鳴る

### 🎙 台本(締めのひとこと)

> 今日はJava標準APIだけで、MVC・Commandパターンを使いながら
> 画面表示 → ルール → 操作 → 効果音、という順番でオセロを組み立てました。
> もし時間が余ったら、次のような発展課題にも挑戦してみてください。
>
> - 合法手のマスをハイライト表示する
> - Undo(一手戻す)機能をCommandパターンで追加する
> - CPU対戦を実装する(Boardの状態だけを見て手を選ぶStrategyパターンの練習になります)

---

## 付録: 各ファイルの最終的な依存関係

```
Disc ─┐
Position ─┤
Direction ─┴─→ Board(モデル/ルール)
                   │
Cursor ────────────┼─→ BoardPanel(盤面描画)
BoardImages ───────┘        │
                             ▼
                       OthelloFrame(ウィンドウ)
                             ▲
GameSoundEffects ─┐          │
GameCommand ──────┼─→ MoveCursorCommand / PlaceDiscCommand
                  │          │
                  └─→ GameController(キー入力の振り分け)
                             │
                             ▼
                           Main(起動処理)
```

配信中に「なぜこの順番で作るのか」を聞かれたら、この図を見せながら
「依存されている側(矢印の先)から先に作ると、常にコンパイルが通る状態を保ちながら
進められる」と説明すると分かりやすいです。
