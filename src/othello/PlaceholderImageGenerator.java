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
 * 盤面・黒石・白石のプレースホルダー画像を生成し、resources フォルダに
 * PNG として保存するための一回限りの開発用ツール。
 *
 * <p>本番のゲーム実行(Main)からは呼び出されない。実際の画像素材を
 * 用意する代わりに、この {@code main} メソッドを一度だけ実行して
 * {@code resources/board.png} などを生成しておく、という位置づけである。</p>
 */
public final class PlaceholderImageGenerator {

    private static final int BOARD_IMAGE_SIZE = 480;
    private static final int DISC_IMAGE_SIZE = 60;

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("resources"));
        saveBoardImage();
        saveDiscImage(Color.BLACK, "black.png");
        saveDiscImage(Color.WHITE, "white.png");
    }

    private static void saveBoardImage() throws IOException {
        BufferedImage image = new BufferedImage(BOARD_IMAGE_SIZE, BOARD_IMAGE_SIZE,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(0, 128, 0));
        g.fillRect(0, 0, BOARD_IMAGE_SIZE, BOARD_IMAGE_SIZE);
        g.setColor(Color.BLACK);
        int cellSize = BOARD_IMAGE_SIZE / 8;
        for (int i = 0; i <= 8; i++) {
            g.drawLine(i * cellSize, 0, i * cellSize, BOARD_IMAGE_SIZE);
            g.drawLine(0, i * cellSize, BOARD_IMAGE_SIZE, i * cellSize);
        }
        g.dispose();
        ImageIO.write(image, "png", new File("resources/board.png"));
    }

    private static void saveDiscImage(Color color, String fileName) throws IOException {
        BufferedImage image = new BufferedImage(DISC_IMAGE_SIZE, DISC_IMAGE_SIZE,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.fillOval(4, 4, DISC_IMAGE_SIZE - 8, DISC_IMAGE_SIZE - 8);
        g.setColor(Color.GRAY);
        g.drawOval(4, 4, DISC_IMAGE_SIZE - 8, DISC_IMAGE_SIZE - 8);
        g.dispose();
        ImageIO.write(image, "png", new File("resources/" + fileName));
    }
}
