package othello;

import java.awt.Color;                 // 色を表すクラス
import java.awt.Graphics2D;            // 図形描画のための高機能な道具箱
import java.awt.RenderingHints;        // 描画品質(アンチエイリアスなど)の指定に使う
import java.awt.image.BufferedImage;   // メモリ上で組み立てる画像データを表すクラス
import java.io.File;                   // ファイルシステム上のファイル・フォルダを表すクラス
import java.io.IOException;            // 入出力エラー時に投げられる例外
import java.nio.file.Files;            // ファイル・フォルダ操作のユーティリティクラス
import java.nio.file.Path;             // ファイルパス(場所)を表すクラス
import javax.imageio.ImageIO;          // 画像ファイルの読み書きを行うクラス

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

    // 1タイルの大きさ。実際にゲームで使うBoardImagesと同じ値を参照することで、
    // 「生成する画像のタイルサイズ」と「読み込む側が期待するタイルサイズ」がズレないようにしている。
    private static final int TILE_SIZE = BoardImages.TILE_SIZE;
    // タイルシートに並べるタイルの枚数(空き・黒・白の3枚)。
    private static final int TILE_COUNT = 3;

    // このクラス自体もmainメソッドを持っており、
    // "java othello.PlaceholderImageGenerator" のように単独で実行できる
    // (Main.javaのゲーム本体とは別の、画像を作るためだけの実行入り口)。
    public static void main(String[] args) throws IOException {
        // resourcesフォルダがまだ存在しない場合は作成する。すでにある場合は何もしない。
        Files.createDirectories(Path.of("resources"));
        // 実際にタイルシート画像を作ってファイルに保存する処理を呼び出す。
        saveTileSheetImage();
    }

    // 3タイル分のタイルシート画像を組み立てて、PNGファイルとして保存する。
    private static void saveTileSheetImage() throws IOException {
        // 横幅 = 1タイルの幅 × 3枚分、高さ = 1タイル分、というサイズの空の画像を新しく作る。
        // TYPE_INT_ARGB は「透明度(A)+赤(R)+緑(G)+青(B)」の情報を持てる画像形式。
        BufferedImage sheet = new BufferedImage(TILE_SIZE * TILE_COUNT, TILE_SIZE,
                BufferedImage.TYPE_INT_ARGB);
        // sheetという画像に「絵を描くための筆(Graphics2D)」を用意してもらう。
        Graphics2D g = sheet.createGraphics();
        // アンチエイリアス(円などの輪郭のギザギザを滑らかにする処理)を有効にする設定。
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 0番目(一番左)には石のない、緑地だけのタイルを描く。
        drawEmptyTile(g, 0);
        // 1番目には緑地の上に黒い丸(黒石)を描いたタイルを描く。
        drawDiscTile(g, 1, Color.BLACK);
        // 2番目には緑地の上に白い丸(白石)を描いたタイルを描く。
        drawDiscTile(g, 2, Color.WHITE);
        // 描画に使った筆(Graphics2D)の後片付けをする。使い終わったら必ず呼ぶ約束になっている。
        g.dispose();
        // 組み立てた画像を、PNG形式で"resources/tiles.png"というファイルに書き出す。
        ImageIO.write(sheet, "png", new File("resources/tiles.png"));
    }

    // 指定した番号(tileIndex)の位置に、緑地だけの「空きマス」タイルを描く。
    private static void drawEmptyTile(Graphics2D g, int tileIndex) {
        // タイルは横一列に並ぶので、左端のx座標は「番号 × 1タイルの幅」で求まる。
        int x = tileIndex * TILE_SIZE;
        // オセロ盤らしい緑色(RGB値で 赤=0, 緑=128, 青=0)を指定する。
        g.setColor(new Color(0, 128, 0));
        // (x, 0)を左上として、幅TILE_SIZE・高さTILE_SIZEの四角形を緑色で塗りつぶす。
        g.fillRect(x, 0, TILE_SIZE, TILE_SIZE);
        // マスの境目が分かるように、黒い枠線も描いておく。
        g.setColor(Color.BLACK);
        // "-1" しているのは、線の太さの分だけ画像の外にはみ出さないようにするための微調整。
        g.drawRect(x, 0, TILE_SIZE - 1, TILE_SIZE - 1);
    }

    // 緑地のタイルの上に、指定した色の丸(石)を重ねて描いた「石入りマス」タイルを描く。
    private static void drawDiscTile(Graphics2D g, int tileIndex, Color discColor) {
        // まず土台として、緑地だけのタイルを描いておく(処理の使い回し)。
        drawEmptyTile(g, tileIndex);
        int x = tileIndex * TILE_SIZE;
        g.setColor(discColor);
        // fillOval(x, y, 幅, 高さ) で塗りつぶした円(石)を描く。
        // タイルの端から4ピクセルずつ内側に余白を作ることで、
        // マスいっぱいではなく少し小さめの、それらしい大きさの石にしている。
        g.fillOval(x + 4, 4, TILE_SIZE - 8, TILE_SIZE - 8);
        // 石の輪郭をグレーの線で縁取りして、見た目にメリハリをつける。
        g.setColor(Color.GRAY);
        g.drawOval(x + 4, 4, TILE_SIZE - 8, TILE_SIZE - 8);
    }
}
