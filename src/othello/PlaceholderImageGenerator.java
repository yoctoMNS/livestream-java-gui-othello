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
