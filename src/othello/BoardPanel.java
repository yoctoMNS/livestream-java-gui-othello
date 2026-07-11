package othello;

import java.awt.Color;      // 色を表すクラス(赤・緑・青の組み合わせなど)
import java.awt.Dimension;  // 幅と高さの組を表すクラス(部品のサイズ指定に使う)
import java.awt.Graphics;   // 図形や画像を描画するための基本的な道具箱
import java.awt.Graphics2D; // Graphicsをより高機能にしたもの(線の太さなどを指定できる)
import java.awt.Image;      // 画像を表す抽象的な型(BufferedImageもこの一種)
import javax.swing.JPanel;  // Swingで「部品を描画するための四角い領域」を表すクラス

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
// "extends JPanel" は「JPanelという既存のクラスの機能を引き継いで、
// 独自の描画処理を追加した新しいクラスを作る」という継承の宣言。
// JPanelにもとから備わっている「Swingの部品として画面に表示される」性質を
// そのまま利用しつつ、盤面の描き方(paintComponent)だけを独自に定義している。
public final class BoardPanel extends JPanel {

    // 1マスあたりのピクセルサイズ。BoardImagesが定義している定数をそのまま使うことで、
    // 「タイル画像の実寸」と「盤面に描画するときのマスのサイズ」が食い違わないようにしている。
    private static final int CELL_SIZE = BoardImages.TILE_SIZE;

    // 描画に必要な情報源(盤面の状態・カーソル位置・タイル画像)への参照を保持する。
    // このクラス自身はこれらの状態を書き換えず、ただ「読んで描く」だけに徹する。
    private final Board board;
    private final Cursor cursor;
    private final BoardImages images;

    // コンストラクタ: 描画に必要な情報源を受け取り、
    // あわせてこのパネル自体の「望ましい大きさ」をSwingに伝える。
    public BoardPanel(Board board, Cursor cursor, BoardImages images) {
        this.board = board;
        this.cursor = cursor;
        this.images = images;
        // setPreferredSize は「このパネルをこのくらいの大きさで表示してほしい」という
        // 希望をSwingに伝えるメソッド。8マス分の幅と高さ(60px × 8 = 480px)を指定している。
        setPreferredSize(new Dimension(CELL_SIZE * Board.SIZE, CELL_SIZE * Board.SIZE));
    }

    /**
     * 盤面全体を「マスのタイル → カーソル」の順に重ねて描画する。
     * 描画順を上から下に読むだけで全体の見た目が組み立てられるようにしている。
     */
    // paintComponentは、JPanelが「自分の絵を描き直したい」と判断したタイミングで
    // Swingが自動的に呼び出してくれるメソッド。開発者が直接呼ぶことは基本的にない
    // (再描画したいときは後述のOthelloFrame.refreshBoardDisplay()からrepaint()を呼ぶ)。
    @Override
    protected void paintComponent(Graphics g) {
        // 親クラス(JPanel)の標準的な描画処理(背景消去など)を先に行っておく。
        // これを省略すると、前回描いた絵が残ったままになるなどの不具合が起きることがある。
        super.paintComponent(g);
        // 1. まず盤面64マス分のタイル画像をすべて描く。
        drawAllTiles(g);
        // 2. その上から、今カーソルがあるマスを赤枠で強調表示する。
        drawCursorHighlight(g);
    }

    // 盤面のすべてのマス(8x8=64マス)について、タイル画像を描画する処理を呼び出す。
    private void drawAllTiles(Graphics g) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                drawTileAt(g, new Position(row, col));
            }
        }
    }

    // 指定した1マスについて、その状態(空き/黒/白)にあったタイル画像を描画する。
    private void drawTileAt(Graphics g, Position position) {
        // そのマスに置かれている石の種類を調べ、対応するタイル画像を取得する。
        Image tile = images.getTileFor(board.discAt(position));
        // g.drawImage(画像, x座標, y座標, 幅, 高さ, 監視役) で画像を描画する。
        // x座標は「列番号 × 1マスの幅」、y座標は「行番号 × 1マスの高さ」で求まる。
        // 最後の "this" は、画像の読み込みが非同期に完了した際に
        // 再描画を依頼する相手(ImageObserver)としてこのパネル自身を渡している。
        g.drawImage(tile, position.col() * CELL_SIZE, position.row() * CELL_SIZE,
                CELL_SIZE, CELL_SIZE, this);
    }

    // 現在カーソルがあるマスの周囲に、赤い枠線を描いて目立たせる。
    private void drawCursorHighlight(Graphics g) {
        Position position = cursor.getPosition();
        // Graphics2DはGraphicsをより高機能にしたクラス。線の太さなどを指定するために
        // ここでキャスト(型を変換)している。Swingの実装では実際には常にGraphics2Dの
        // インスタンスが渡されてくるため、このキャストは安全に行える。
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.RED); // これから描く線の色を赤に設定
        g2.setStroke(new java.awt.BasicStroke(3)); // 線の太さを3ピクセルに設定
        // drawRect(x, y, 幅, 高さ) で四角い枠線を描く。
        // マスの内側に少し余白(2ピクセル)を空けて描くことで、
        // マスの区切り線と重ならず見やすくしている。
        g2.drawRect(position.col() * CELL_SIZE + 2, position.row() * CELL_SIZE + 2,
                CELL_SIZE - 4, CELL_SIZE - 4);
    }
}
