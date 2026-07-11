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
// record(レコード)は「値を2つ以上まとめて持つだけの、変更できないデータの入れ物」
// を簡単に作るためのJavaの機能。
// 通常のクラスなら自分で書く必要がある
//   ・フィールド(row, col)
//   ・コンストラクタ(値を受け取って保存する処理)
//   ・row()やcol()のような値を取り出すメソッド
//   ・equals / hashCode / toString(等しいかどうかの比較や文字列表示)
// を、この1行 "record Position(int row, int col)" だけで自動的に作ってくれる。
public record Position(int row, int col) {

    /**
     * この座標から指定した方向に1マス進んだ座標を返す。
     *
     * @param direction 進む方向
     * @return 移動後の座標(盤面の範囲外になる場合もある)
     */
    // direction(方向)を受け取り、その方向に1マス進んだ「新しい」Positionを作って返す。
    // record は値を変更できない(row, colを後から書き換えられない)ため、
    // 「今の座標を書き換える」のではなく「進んだ先の座標を新しく作って返す」形になる。
    public Position moved(Direction direction) {
        // row(行)に、方向が持つ行方向の増分(rowDelta)を足す。
        // col(列)に、方向が持つ列方向の増分(colDelta)を足す。
        // その2つの値で新しいPositionを作って呼び出し元に返す。
        return new Position(row + direction.rowDelta(), col + direction.colDelta());
    }
}
