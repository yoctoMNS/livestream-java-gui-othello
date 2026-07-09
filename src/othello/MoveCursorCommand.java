package othello;

/**
 * WASDキーの入力に対応する「カーソルを1マス動かす」コマンド。
 *
 * <p>移動量(rowDelta, colDelta)をコンストラクタで受け取ることで、
 * 上下左右4つの動きをすべて同じクラスで表現できるようにしている。</p>
 */
public final class MoveCursorCommand implements GameCommand {

    private final Cursor cursor;
    private final int rowDelta;
    private final int colDelta;
    private final OthelloFrame view;
    private final GameSoundEffects soundEffects;

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
    @Override
    public void execute() {
        cursor.moveBy(rowDelta, colDelta);
        soundEffects.playCursorMoveSound();
        view.refreshBoardDisplay();
    }
}
