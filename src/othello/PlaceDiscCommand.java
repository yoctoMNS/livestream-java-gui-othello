package othello;

/**
 * Enterキーの入力に対応する「カーソルの位置に石を置く」コマンド。
 */
public final class PlaceDiscCommand implements GameCommand {

    // このコマンドが必要とする道具一式。MoveCursorCommandと似ているが、
    // カーソルの座標をもとに実際にルール判定を行うため Board も持っている。
    private final Board board;
    private final Cursor cursor;
    private final OthelloFrame view;
    private final GameSoundEffects soundEffects;

    // コンストラクタ: 必要な道具を受け取ってフィールドに保存するだけ。
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
        // 1. 今カーソルがある座標を取得し、そこに石を置けるかをBoard(Model)に判断してもらう。
        //    placeDiscAt は「置けた場合はtrue、置けなかった場合はfalse」を返す。
        boolean placed = board.placeDiscAt(cursor.getPosition());
        // 2. 実際に置けた場合だけ、置いたことを知らせる音を鳴らす。
        //    置けなかった場合(合法手ではない場所だった場合)は音を鳴らさない。
        if (placed) {
            soundEffects.playDiscPlacedSound();
        }
        // 3. 置けた・置けなかったに関わらず、最新の盤面状態を画面に反映する。
        //    (例えば「置けません」だとしても手番メッセージ自体は変わらず表示され続ける)
        view.refreshBoardDisplay();
    }
}
