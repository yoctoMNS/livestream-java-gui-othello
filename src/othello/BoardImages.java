package othello;

import java.awt.image.BufferedImage; // メモリ上に読み込んだ画像データを表すクラス
import java.io.File;                 // ファイルシステム上のファイル・フォルダを表すクラス
import java.io.IOException;          // ファイル入出力で失敗したときに投げられる例外
import javax.imageio.ImageIO;        // 画像ファイルの読み書きを行うためのユーティリティクラス

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
    // 画像内の1マスが60x60ピクセルの正方形であることを表す定数。
    // BoardPanel側でも同じ値を使い回すため public にしている。
    public static final int TILE_SIZE = 60;

    // タイルシートの画像は横に3枚並んでおり、その「何番目か」を表す番号(インデックス)。
    // 一番左が0番目、その隣が1番目、さらに隣が2番目、という意味。
    // "private static final" は「BoardImagesクラス全体で共有する、変更不可の定数」であることを示す。
    private static final int EMPTY_TILE_INDEX = 0;
    private static final int BLACK_TILE_INDEX = 1;
    private static final int WHITE_TILE_INDEX = 2;

    // タイルシートから切り出した後の、3種類のタイル画像を保持するフィールド。
    private final BufferedImage emptyTile;
    private final BufferedImage blackTile;
    private final BufferedImage whiteTile;

    // このコンストラクタは private(非公開)になっている。
    // つまり外部から "new BoardImages(...)" と直接書くことはできず、
    // 必ず下の loadFromResourceFiles() という入り口を通ってインスタンスを作らせる設計になっている
    // (ファイル読み込みに失敗する可能性がある処理を、コンストラクタではなく
    //  分かりやすい名前のstaticメソッドにまとめるためのテクニック)。
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
    // "static" が付いているので、インスタンスを作らなくても
    // "BoardImages.loadFromResourceFiles()" のように直接呼び出せるメソッド。
    // "throws IOException" は「このメソッドの中でファイル読み込みエラーが起きる可能性があるので、
    // 呼び出し側で対処してください」とコンパイラに伝える宣言。
    public static BoardImages loadFromResourceFiles() throws IOException {
        // resourcesフォルダのtiles.pngを画像として読み込み、メモリ上のBufferedImageにする。
        BufferedImage tileSheet = ImageIO.read(new File("resources/tiles.png"));
        // 読み込んだ1枚の画像から、3つのタイルをそれぞれ切り出し、
        // それらをまとめて新しいBoardImagesインスタンスとして作って返す。
        return new BoardImages(
                cutOutTile(tileSheet, EMPTY_TILE_INDEX),
                cutOutTile(tileSheet, BLACK_TILE_INDEX),
                cutOutTile(tileSheet, WHITE_TILE_INDEX));
    }

    // タイルシート画像の中から、指定した番号(tileIndex)のタイルだけを切り出す。
    private static BufferedImage cutOutTile(BufferedImage tileSheet, int tileIndex) {
        // getSubimage(x, y, 幅, 高さ) は、画像の一部分を切り抜いて新しい画像として返すメソッド。
        // タイルは横一列に並んでいるので、x座標は「番号 × 1タイルの幅」で計算できる。
        // y座標は常に0(上端)、幅と高さはどちらもTILE_SIZE(60ピクセル)。
        return tileSheet.getSubimage(tileIndex * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE);
    }

    /**
     * 指定した石の状態に対応するマス目タイル画像を返す。
     * {@link Disc#EMPTY} の場合は緑地のみのタイルを返す。
     */
    // Discの値(EMPTY/BLACK/WHITE)に応じて、対応するタイル画像を選んで返す。
    // BoardPanelはこのメソッドを呼ぶだけで、どのタイルを使うべきか気にしなくてよい。
    public BufferedImage getTileFor(Disc disc) {
        return switch (disc) {
            case EMPTY -> emptyTile;
            case BLACK -> blackTile;
            case WHITE -> whiteTile;
        };
    }
}
