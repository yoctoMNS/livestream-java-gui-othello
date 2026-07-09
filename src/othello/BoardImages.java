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
