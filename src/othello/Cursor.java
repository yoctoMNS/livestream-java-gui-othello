package othello;

/**
 * プレイヤーがWASDキーで動かす「今どのマスを選択しているか」を表す状態。
 *
 * <p>これはオセロのルールそのものではなく、UI操作のための状態なので
 * {@link Board} には持たせず、独立したクラスとして切り出している
 * (盤面ロジックと画面操作の関心を分離するため)。</p>
 */
public final class Cursor {

    // 今カーソルがある行番号・列番号。Boardのcellsと同じく0始まり。
    // フィールドの初期値は書いていないが、この後のコンストラクタで必ず設定される。
    private int row;
    private int col;

    /** 盤面中央付近のマスを選択した状態で開始する。 */
    public Cursor() {
        // "this.row" の "this" は「今作られようとしているCursorインスタンス自身」を指す。
        // 引数がない今回は必須ではないが、「フィールドのrow」であることを明示するために書いている。
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
        // 現在地に移動量を足した「新しい行番号の候補」を求め、
        // それが0〜7の範囲を超えていたら0や7に丸め込む(clampToBoardRangeが行う)。
        row = clampToBoardRange(row + rowDelta);
        col = clampToBoardRange(col + colDelta);
    }

    /** 現在カーソルが指しているマスの座標を返す。 */
    // 内部で持っているrow, colから、盤面座標を表すPositionオブジェクトを作って返す。
    public Position getPosition() {
        return new Position(row, col);
    }

    // 与えられた値を「0以上、Board.SIZE-1(=7)以下」の範囲に収める処理。
    // 例えば value が -1 なら 0 に、8なら7に、3ならそのまま3になる。
    private int clampToBoardRange(int value) {
        // Math.min(7, value) で「7より大きくならないように」上限を抑え、
        // さらに Math.max(0, ...) で「0より小さくならないように」下限を抑える。
        return Math.max(0, Math.min(Board.SIZE - 1, value));
    }
}
