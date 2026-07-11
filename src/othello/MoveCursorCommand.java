package othello;

/**
 * WASDキーの入力に対応する「カーソルを1マス動かす」コマンド。
 *
 * <p>移動量(rowDelta, colDelta)をコンストラクタで受け取ることで、
 * 上下左右4つの動きをすべて同じクラスで表現できるようにしている。</p>
 */
// "implements GameCommand" は「このクラスはGameCommandインタフェースの約束(execute())を
// 守ります」という宣言。これにより GameController は
// MoveCursorCommand と PlaceDiscCommand を区別せず、どちらも
// 「GameCommand」として同じ書き方で扱える。
public final class MoveCursorCommand implements GameCommand {

    // このコマンドが実行時に使う「道具」をまとめてフィールドに保持している。
    // すべて final なので、一度コンストラクタで受け取ったら差し替えられない。
    private final Cursor cursor;             // 動かす対象のカーソル
    private final int rowDelta;              // 行方向の移動量(例: 上なら-1)
    private final int colDelta;              // 列方向の移動量(例: 右なら+1)
    private final OthelloFrame view;         // 画面を再描画してもらうためのView
    private final GameSoundEffects soundEffects; // 効果音を鳴らすための道具

    // コンストラクタ: このコマンドを作るときに、必要な道具をすべて受け取って
    // 対応するフィールドに保存しておく(あとでexecute()から使うため)。
    public MoveCursorCommand(Cursor cursor, int rowDelta, int colDelta,
                              OthelloFrame view, GameSoundEffects soundEffects) {
        this.cursor = cursor;
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
        this.view = view;
        this.soundEffects = soundEffects;
    }

    /**
     * カーソルを移動させ、移動音を鳴らしたうえで画面を再描画する。
     */
    // "@Override" は「これはGameCommandインタフェースで決められたexecute()を
    // 上書き(実装)していますよ」とコンパイラに伝えるための注釈(アノテーション)。
    // 書き間違い(メソッド名のタイプミスなど)があった場合にエラーで気づける。
    @Override
    public void execute() {
        // 1. カーソルをコンストラクタで受け取った移動量ぶんだけ動かす。
        cursor.moveBy(rowDelta, colDelta);
        // 2. 「カーソルが動いた」ことをプレイヤーに音で伝える。
        soundEffects.playCursorMoveSound();
        // 3. 画面上のカーソル表示を最新の状態に描き直す。
        view.refreshBoardDisplay();
    }
}
