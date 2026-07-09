package othello;

/**
 * プレイヤーがWASDキーで動かす「今どのマスを選択しているか」を表す状態。
 *
 * <p>これはオセロのルールそのものではなく、UI操作のための状態なので
 * {@link Board} には持たせず、独立したクラスとして切り出している
 * (盤面ロジックと画面操作の関心を分離するため)。</p>
 */
public final class Cursor {

    private int row;
    private int col;

    /** 盤面中央付近のマスを選択した状態で開始する。 */
    public Cursor() {
        this.row = 3;
        this.col = 3;
    }

    /**
     * カーソルを指定した方向に1マス動かす。
     *
     * <p>盤面の外に出ようとした場合は端で止まり、
     * 例外を発生させたり無効な座標を保持したりしないようにしている。</p>
     *
     * @param rowDelta 行方向の移動量(上へ移動なら -1、下へ移動なら +1)
     * @param colDelta 列方向の移動量(左へ移動なら -1、右へ移動なら +1)
     */
    public void moveBy(int rowDelta, int colDelta) {
        row = clampToBoardRange(row + rowDelta);
        col = clampToBoardRange(col + colDelta);
    }

    /** 現在カーソルが指しているマスの座標を返す。 */
    public Position getPosition() {
        return new Position(row, col);
    }

    private int clampToBoardRange(int value) {
        return Math.max(0, Math.min(Board.SIZE - 1, value));
    }
}
