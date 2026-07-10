package othello;

// import文: 他のパッケージ(ここではJava標準ライブラリ)にあるクラスを、
// このファイルの中で名前だけで(パッケージ名を省略して)使えるようにする宣言。
import java.awt.event.KeyEvent;   // キーボードのイベント(押された・離されたなど)を表すクラス
import java.awt.event.KeyListener; // キー入力を受け取るために実装すべきインタフェース
import java.util.HashMap;          // Mapインタフェースの実装の1つ(データの対応表)
import java.util.Map;              // 「キー」と「値」の組を管理するためのインタフェース

/**
 * キーボード入力を受け取り、対応する {@link GameCommand} を実行するコントローラ
 * (MVC における Controller)。
 *
 * <p>キーコードとコマンドの対応表を持つだけで、移動やゲームルールの詳細は
 * 一切知らない。実際の処理は Command / Model 側に委譲することで、
 * このクラスの責務を「入力の振り分け」だけに絞っている。</p>
 */
// "implements KeyListener" は、SwingのウィンドウにaddKeyListenerで登録できるように
// するための宣言。KeyListenerが要求する3つのメソッド
// (keyPressed / keyReleased / keyTyped)をすべて実装する必要がある。
public final class GameController implements KeyListener {

    // 「どのキーコードが押されたら、どのコマンドを実行するか」の対応表(辞書)。
    // Map<Integer, GameCommand> は「Integer(キーコード)をキーとして、
    // GameCommand(実行する処理)を値として持つ」というデータ構造を表す。
    // "= new HashMap<>()" でその場で空の対応表を作って初期化している。
    private final Map<Integer, GameCommand> keyToCommand = new HashMap<>();

    /**
     * WASD(移動)とEnter(配置)のキー割り当てを構築する。
     */
    // コンストラクタ: GameControllerを作るときに必要な部品(Model・View・効果音)を
    // すべて受け取り、それを使ってキーとコマンドの対応表を組み立てる。
    public GameController(Board board, Cursor cursor, OthelloFrame view,
                           GameSoundEffects soundEffects) {
        // 移動用のキー(WASD)をまとめて登録する処理を呼び出す。
        registerCursorMovementKeys(cursor, view, soundEffects);
        // 配置用のキー(Enter)を登録する処理を呼び出す。
        registerDiscPlacementKey(board, cursor, view, soundEffects);
    }

    // WASDそれぞれに「カーソルを1マス動かすコマンド」を割り当てる。
    private void registerCursorMovementKeys(Cursor cursor, OthelloFrame view,
                                             GameSoundEffects soundEffects) {
        // keyToCommand.put(キー, 値) で対応表に1件登録する。
        // KeyEvent.VK_W のような定数は「Wキー」を表す固定の整数値。
        // new MoveCursorCommand(...) で「上に1マス動く」動作を表すコマンドを作り、
        // Wキーに結びつけている。
        keyToCommand.put(KeyEvent.VK_W, new MoveCursorCommand(cursor, -1, 0, view, soundEffects)); // W: 上
        keyToCommand.put(KeyEvent.VK_S, new MoveCursorCommand(cursor, 1, 0, view, soundEffects));   // S: 下
        keyToCommand.put(KeyEvent.VK_A, new MoveCursorCommand(cursor, 0, -1, view, soundEffects));  // A: 左
        keyToCommand.put(KeyEvent.VK_D, new MoveCursorCommand(cursor, 0, 1, view, soundEffects));   // D: 右
    }

    // Enterキーに「カーソル位置に石を置くコマンド」を割り当てる。
    private void registerDiscPlacementKey(Board board, Cursor cursor, OthelloFrame view,
                                           GameSoundEffects soundEffects) {
        keyToCommand.put(KeyEvent.VK_ENTER, new PlaceDiscCommand(board, cursor, view, soundEffects));
    }

    /** 押されたキーに対応するコマンドがあれば実行する。 */
    // KeyListenerインタフェースの約束により、キーが押された瞬間にSwingから自動的に呼ばれるメソッド。
    @Override
    public void keyPressed(KeyEvent event) {
        // event.getKeyCode() で「どのキーが押されたか」を表す整数値を取得し、
        // それをキーとして対応表からコマンドを検索する。
        // 対応表に登録されていないキーの場合は null(何もない)が返る。
        GameCommand command = keyToCommand.get(event.getKeyCode());
        // 対応するコマンドが見つかった場合だけ実行する(nullのままexecute()すると
        // エラーになってしまうため、必ずnullチェックしてから呼び出す)。
        if (command != null) {
            command.execute();
        }
    }

    // KeyListenerインタフェースを実装する以上、このメソッドも用意する必要があるが、
    // このゲームでは「キーを離した瞬間」に何かする必要がないため中身は空にしている。
    @Override
    public void keyReleased(KeyEvent event) {
        // このゲームではキーを離した瞬間の処理は不要。
    }

    // 同様に、文字入力(日本語入力などのIME経由の入力)に反応する必要がないため空にしている。
    @Override
    public void keyTyped(KeyEvent event) {
        // 文字入力(IME等)は扱わないため何もしない。
    }
}
