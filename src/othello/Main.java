package othello;

import java.io.IOException;
import javax.swing.JOptionPane;

/**
 * アプリケーションの起動クラス。
 *
 * <p>{@link #main(String[])} は「新しい対局を始める」という
 * 1つのことだけを行い、実際の準備手順は {@link #startNewGame()} に
 * 追い出している。{@code startNewGame} を読むだけで
 * 「盤面を作る → 画像を読み込む → ウィンドウを作る → 効果音を用意する →
 * キー入力とコマンドを結びつける → 表示する」という起動の流れが
 * 日本語の文章のようにそのまま読めることを意図している。</p>
 */
public final class Main {

    public static void main(String[] args) {
        try {
            startNewGame();
        } catch (IOException e) {
            showFatalErrorDialog(e);
        }
    }

    private static void startNewGame() throws IOException {
        Board board = new Board();
        Cursor cursor = new Cursor();
        BoardImages images = BoardImages.loadFromResourceFiles();
        OthelloFrame view = new OthelloFrame(board, cursor, images);
        GameSoundEffects soundEffects = new GameSoundEffects();
        GameController controller = new GameController(board, cursor, view, soundEffects);
        view.startListeningForKeyInput(controller);
        view.showWindow();
    }

    private static void showFatalErrorDialog(IOException e) {
        JOptionPane.showMessageDialog(null,
                "画像の読み込みに失敗しました。resources フォルダを確認してください: " + e.getMessage());
    }
}
