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
