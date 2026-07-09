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
 */
public final class BoardPanel extends JPanel {

    private static final int CELL_SIZE = 60;

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
     * 盤面全体を「背景 → 石 → カーソル」の順に重ねて描画する。
     * 描画順を上から下に読むだけで全体の見た目が組み立てられるようにしている。
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoardBackground(g);
        drawAllDiscs(g);
        drawCursorHighlight(g);
    }

    private void drawBoardBackground(Graphics g) {
        g.drawImage(images.getBoardImage(), 0, 0, getWidth(), getHeight(), this);
    }

    private void drawAllDiscs(Graphics g) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                drawDiscIfPresent(g, new Position(row, col));
            }
        }
    }

    private void drawDiscIfPresent(Graphics g, Position position) {
        Disc disc = board.discAt(position);
        if (disc == Disc.EMPTY) {
            return;
        }
        Image discImage = (disc == Disc.BLACK) ? images.getBlackDiscImage() : images.getWhiteDiscImage();
        g.drawImage(discImage, position.col() * CELL_SIZE, position.row() * CELL_SIZE,
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
