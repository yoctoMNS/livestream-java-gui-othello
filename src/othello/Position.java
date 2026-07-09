package othello;

/**
 * 盤面上の1マスの座標(行・列)を表す不変データ。
 *
 * <p>行(row)・列(col)ともに 0 から {@link Board#SIZE} - 1 の範囲を想定している。
 * record として定義することで、equals/hashCode/toString を自動生成させ、
 * 「座標を表すだけの値」であることを型で明示している。</p>
 *
 * @param row 上から数えた行番号(0始まり)
 * @param col 左から数えた列番号(0始まり)
 */
public record Position(int row, int col) {

    /**
     * この座標から指定した方向に1マス進んだ座標を返す。
     *
     * @param direction 進む方向
     * @return 移動後の座標(盤面の範囲外になる場合もある)
     */
    public Position moved(Direction direction) {
        return new Position(row + direction.rowDelta(), col + direction.colDelta());
    }
}
