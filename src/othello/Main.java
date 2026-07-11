package othello;

import java.io.IOException;       // ファイル読み込みなどで失敗したときに投げられる例外の型
import javax.swing.JOptionPane;   // ちょっとしたメッセージをダイアログ(小さな別ウィンドウ)で表示する部品

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

    // "public static void main(String[] args)" は、
    // JavaでApplicationを実行したときに最初に呼ばれる特別なメソッド
    // (「java othello.Main」と実行すると、このmainが実行される)。
    // args には実行時にコマンドラインから渡された文字列が入るが、このプログラムでは使わない。
    public static void main(String[] args) {
        // try-catch文: tryの中の処理を実行してみて、もし例外(エラー)が発生したら
        // catchの中の処理に飛んで、そこでエラーへの対応を行う、という書き方。
        try {
            // ゲームの起動処理をすべてまとめたメソッドを呼び出す。
            startNewGame();
        } catch (IOException e) {
            // 画像ファイルの読み込みに失敗した場合など、IOExceptionが起きたときにここに来る。
            // eには「何が原因で失敗したか」の情報が入っている。
            showFatalErrorDialog(e);
        }
    }

    // ゲーム開始に必要な準備を、上から順番に1つずつ行っていくメソッド。
    // "throws IOException" は「この中で発生したIOExceptionは自分では処理せず、
    // 呼び出し元(main)に判断を委ねます」という意味。
    private static void startNewGame() throws IOException {
        // 1. ゲームのルールを管理するBoard(Model)を新しく作る。
        Board board = new Board();
        // 2. プレイヤーが動かすカーソルを、初期位置(盤面中央付近)で作る。
        Cursor cursor = new Cursor();
        // 3. 盤面描画に使うタイル画像を、resourcesフォルダから読み込む。
        //    ここで失敗するとIOExceptionが発生し、このメソッドの外(main)まで伝わる。
        BoardImages images = BoardImages.loadFromResourceFiles();
        // 4. 画面(ウィンドウ)を組み立てる。ここまでに用意したboard・cursor・imagesを渡す。
        OthelloFrame view = new OthelloFrame(board, cursor, images);
        // 5. 効果音を鳴らすための道具を用意する。
        GameSoundEffects soundEffects = new GameSoundEffects();
        // 6. キー入力を受け取るControllerを作る。ここまでのすべての部品を渡すことで、
        //    「どのキーが押されたら、盤面・画面・音がどう動くか」の対応表が組み立てられる。
        GameController controller = new GameController(board, cursor, view, soundEffects);
        // 7. 作ったControllerを、実際にウィンドウのキー入力の受け取り先として登録する。
        view.startListeningForKeyInput(controller);
        // 8. 最後に、これまで組み立てたウィンドウを画面に表示する。
        view.showWindow();
    }

    // 画像読み込みなど致命的なエラーが起きたときに、ユーザーに分かりやすく伝えるためのメソッド。
    private static void showFatalErrorDialog(IOException e) {
        // JOptionPane.showMessageDialog(親ウィンドウ, 表示する文字列) で
        // シンプルなメッセージ入りのポップアップウィンドウを表示する。
        // 親ウィンドウがまだ存在しないため、第一引数にはnullを渡している。
        // e.getMessage() で、例外が持っている「エラーの詳しい理由」の文字列を取り出して埋め込む。
        JOptionPane.showMessageDialog(null,
                "画像の読み込みに失敗しました。resources フォルダを確認してください: " + e.getMessage());
    }
}
