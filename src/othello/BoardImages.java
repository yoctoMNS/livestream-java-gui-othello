package othello;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * 盤面・黒石・白石の描画に使う画像をまとめて保持するクラス。
 *
 * <p>画像ファイルは {@code resources/} フォルダにあらかじめ用意してある
 * (board.png / black.png / white.png)。このクラスは起動時に一度だけ
 * それらを {@link ImageIO} で読み込み、以降は読み込み済みの
 * {@link BufferedImage} を使い回すことで、描画のたびにファイルI/Oが
 * 発生しないようにしている。</p>
 */
public final class BoardImages {

    private final BufferedImage boardImage;
    private final BufferedImage blackDiscImage;
    private final BufferedImage whiteDiscImage;

    private BoardImages(BufferedImage boardImage, BufferedImage blackDiscImage,
                         BufferedImage whiteDiscImage) {
        this.boardImage = boardImage;
        this.blackDiscImage = blackDiscImage;
        this.whiteDiscImage = whiteDiscImage;
    }

    /**
     * resources フォルダから盤面・黒石・白石の画像を読み込む。
     *
     * @return 読み込み済みの画像一式
     * @throws IOException 画像ファイルが見つからない、または読み込みに失敗した場合
     */
    public static BoardImages loadFromResourceFiles() throws IOException {
        BufferedImage board = ImageIO.read(new File("resources/board.png"));
        BufferedImage black = ImageIO.read(new File("resources/black.png"));
        BufferedImage white = ImageIO.read(new File("resources/white.png"));
        return new BoardImages(board, black, white);
    }

    /** 盤面の背景画像を返す。 */
    public BufferedImage getBoardImage() {
        return boardImage;
    }

    /** 黒石の画像を返す。 */
    public BufferedImage getBlackDiscImage() {
        return blackDiscImage;
    }

    /** 白石の画像を返す。 */
    public BufferedImage getWhiteDiscImage() {
        return whiteDiscImage;
    }
}
