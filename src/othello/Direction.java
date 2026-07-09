package othello;

/**
 * 盤面上で石を挟めるかどうかを調べるための8方向を表す列挙型。
 *
 * <p>オセロのルールでは、石を置いたマスから縦・横・斜めの8方向すべてを
 * 走査して相手の石を挟めているか確認する必要がある。
 * この繰り返し処理を {@code for (Direction d : Direction.values())} という
 * 1行で書けるようにするために、方向そのものを列挙型として切り出している。</p>
 */
public enum Direction {
    NORTH(-1, 0),
    SOUTH(1, 0),
    EAST(0, 1),
    WEST(0, -1),
    NORTH_EAST(-1, 1),
    NORTH_WEST(-1, -1),
    SOUTH_EAST(1, 1),
    SOUTH_WEST(1, -1);

    private final int rowDelta;
    private final int colDelta;

    Direction(int rowDelta, int colDelta) {
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
    }

    /** この方向に1マス進むときの行番号の増分。 */
    public int rowDelta() {
        return rowDelta;
    }

    /** この方向に1マス進むときの列番号の増分。 */
    public int colDelta() {
        return colDelta;
    }
}
