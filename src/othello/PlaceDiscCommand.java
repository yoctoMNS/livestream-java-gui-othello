package othello;

/**
 * Enterキーの入力に対応する「カーソルの位置に石を置く」コマンド。
 */
public final class PlaceDiscCommand implements GameCommand {

    private final Board board;
    private final Cursor cursor;
    private final OthelloFrame view;
    private final GameSoundEffects soundEffects;

    public PlaceDiscCommand(Board board, Cursor cursor, OthelloFrame view,
                             GameSoundEffects soundEffects) {
        this.board = board;
        this.cursor = cursor;
        this.view = view;
        this.soundEffects = soundEffects;
    }

    /**
     * カーソル位置への配置をモデルに依頼し、成功した場合のみ配置音を鳴らす。
     * 置けないマスだった場合は何も起きない(不正な手は静かに無視する)。
     */
    @Override
    public void execute() {
        boolean placed = board.placeDiscAt(cursor.getPosition());
        if (placed) {
            soundEffects.playDiscPlacedSound();
        }
        view.refreshBoardDisplay();
    }
}
