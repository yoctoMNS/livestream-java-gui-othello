package othello;

import java.awt.BorderLayout;     // 部品を「上下左右中央」に配置するレイアウト方式
import javax.swing.JFrame;        // OSのウィンドウそのものを表すSwingのクラス
import javax.swing.JLabel;        // 文字列を表示するための部品(ラベル)
import javax.swing.SwingConstants; // 文字の配置(左寄せ・中央寄せなど)を表す定数集

/**
 * ゲームウィンドウ全体を組み立てるトップレベルのView(MVC における View)。
 *
 * <p>状態メッセージ用のラベルと盤面パネルを配置するだけの薄いクラスにし、
 * 描画の詳細は {@link BoardPanel} に、ゲームルールは {@link Board} に
 * それぞれ委譲している。</p>
 */
// "extends JFrame" により、このクラスは「OSのウィンドウ」としての性質
// (タイトルバーを持つ、閉じるボタンがある、など)をそのまま受け継ぐ。
public final class OthelloFrame extends JFrame {

    // 状態メッセージ(手番など)を取得するために、Boardへの参照を持っておく。
    private final Board board;
    // 画面上部に表示する、状態メッセージ用のラベル部品。
    private final JLabel statusLabel;
    // 盤面そのものを描画する部品(BoardPanel)。
    private final BoardPanel boardPanel;

    // コンストラクタ: ウィンドウの中身(ラベルと盤面パネル)を組み立てて配置する。
    public OthelloFrame(Board board, Cursor cursor, BoardImages images) {
        // "super(...)" は親クラス(JFrame)のコンストラクタを呼び出す文。
        // ここではウィンドウのタイトルバーに表示する文字列を渡している。
        // Javaのルールで、コンストラクタの一番最初の行にしか書けない。
        super("オセロ (WASDで移動 / Enterで配置)");
        this.board = board;
        // JLabel(初期表示文字列, 配置方法) でラベルを作る。
        // 起動直後の状態メッセージ(例: "黒の番です")を最初から表示しておく。
        this.statusLabel = new JLabel(board.getStatusMessage(), SwingConstants.CENTER);
        // 盤面パネルを作る。実際の描画処理の中身はBoardPanel側に任せている。
        this.boardPanel = new BoardPanel(board, cursor, images);
        // 部品の配置と、ウィンドウ自体の設定を、それぞれ専用のメソッドに分けて呼び出す。
        layoutComponents();
        configureWindow();
    }

    // ラベルと盤面パネルを、ウィンドウのどこに配置するかを決める。
    private void layoutComponents() {
        // BorderLayoutは、部品を「北(上)・南(下)・東(右)・西(左)・中央」の
        // 5つの領域に割り当てて配置するレイアウト方式。
        setLayout(new BorderLayout());
        // ラベルを上部(NORTH)に配置。
        add(statusLabel, BorderLayout.NORTH);
        // 盤面パネルを中央(CENTER)に配置。中央は残りのスペースいっぱいに広がる。
        add(boardPanel, BorderLayout.CENTER);
    }

    // ウィンドウそのものの見た目・振る舞いに関する設定をまとめて行う。
    private void configureWindow() {
        // ウィンドウの閉じるボタンが押されたら、アプリケーション全体を終了させる設定。
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // pack() は、中に入れた部品(ラベルや盤面パネル)が
        // ちょうど収まる大きさに、ウィンドウのサイズを自動調整するメソッド。
        pack();
        // ウィンドウの大きさをユーザーが変更できないようにする
        // (盤面のマス目がずれて見えることを防ぐため)。
        setResizable(false);
        // setLocationRelativeTo(null) は、ウィンドウを画面の中央に表示する指定。
        setLocationRelativeTo(null);
    }

    /**
     * モデルの状態変化を画面に反映する。石を置いた・カーソルを動かした等、
     * 盤面の見た目に影響する操作のたびに Controller から呼び出される。
     */
    public void refreshBoardDisplay() {
        // ラベルの文字列を、Boardが持っている最新の状態メッセージに書き換える。
        statusLabel.setText(board.getStatusMessage());
        // repaint() は「このパネルを描き直してください」とSwingに依頼するメソッド。
        // 実際に画面が再描画されるタイミングでBoardPanel.paintComponent()が呼ばれる。
        boardPanel.repaint();
    }

    /** このウィンドウでキー入力を受け付け始める。 */
    public void startListeningForKeyInput(GameController controller) {
        // このウィンドウに対して「キーが押されたらcontrollerに知らせてほしい」と登録する。
        addKeyListener(controller);
        // キーボードの入力を受け取れる状態(フォーカスを持てる状態)にする。
        setFocusable(true);
        // 実際にこのウィンドウへキーボードの入力対象(フォーカス)を移す。
        requestFocusInWindow();
    }

    /** ウィンドウを画面に表示する。 */
    public void showWindow() {
        // setVisible(true) を呼ぶことで、初めてウィンドウが画面上に表示される。
        setVisible(true);
    }
}
