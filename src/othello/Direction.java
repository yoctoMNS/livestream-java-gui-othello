package othello;

/**
 * 盤面上で石を挟めるかどうかを調べるための8方向を表す列挙型。
 *
 * <p>オセロのルールでは、石を置いたマスから縦・横・斜めの8方向すべてを
 * 走査して相手の石を挟めているか確認する必要がある。
 * この繰り返し処理を {@code for (Direction d : Direction.values())} という
 * 1行で書けるようにするために、方向そのものを列挙型として切り出している。</p>
 */
// この enum は「値だけの列挙型」だった Disc と違い、
// それぞれの値(NORTH, SOUTHなど)が「行の増分」「列の増分」という
// 2つの数値データを一緒に持っている。
public enum Direction {
    // 括弧の中の2つの数値は、後で出てくるコンストラクタ Direction(int, int) に渡される。
    // 1つ目が rowDelta(行の増分)、2つ目が colDelta(列の増分)。
    NORTH(-1, 0),      // 上方向: 行が1減る、列は変わらない
    SOUTH(1, 0),       // 下方向: 行が1増える、列は変わらない
    EAST(0, 1),        // 右方向: 行は変わらない、列が1増える
    WEST(0, -1),       // 左方向: 行は変わらない、列が1減る
    NORTH_EAST(-1, 1), // 右上方向: 行が1減り、列が1増える(斜め)
    NORTH_WEST(-1, -1),// 左上方向: 行が1減り、列が1減る(斜め)
    SOUTH_EAST(1, 1),  // 右下方向: 行が1増え、列が1増える(斜め)
    SOUTH_WEST(1, -1); // 左下方向: 行が1増え、列が1減る(斜め)
    // ↑ 8つの値の定義が終わったので「;」で区切る

    // ここから下は、8つの値それぞれが持つデータとメソッドの定義。
    // "final" は「一度値をセットしたら二度と変更できない」という意味の修飾子。
    private final int rowDelta; // この方向に進むときの行番号の増分
    private final int colDelta; // この方向に進むときの列番号の増分

    // コンストラクタ: NORTH(-1, 0) のように値を作るときに1回だけ呼ばれ、
    // 渡された数値をフィールド(rowDelta, colDelta)に保存する。
    Direction(int rowDelta, int colDelta) {
        this.rowDelta = rowDelta; // 引数の値を自分自身(this)のフィールドに代入
        this.colDelta = colDelta;
    }

    /** この方向に1マス進むときの行番号の増分。 */
    // rowDelta フィールドを外部から読み取るためのメソッド(ゲッター)。
    public int rowDelta() {
        return rowDelta;
    }

    /** この方向に1マス進むときの列番号の増分。 */
    // colDelta フィールドを外部から読み取るためのメソッド(ゲッター)。
    public int colDelta() {
        return colDelta;
    }
}
