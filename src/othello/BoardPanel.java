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
