package othello;

/**
 * 盤面のマスに置かれる石の種類を表す列挙型。
 *
 * <p>オセロの石は「空きマス」「黒石」「白石」の3状態しか取らないため、
 * boolean や文字コードではなく列挙型として表現することで、
 * コンパイル時に不正な状態を作れないようにしている。</p>
 */
public enum Disc {
    EMPTY,
    BLACK,
    WHITE;

    /**
     * この石の対戦相手側の石を返す。
     *
     * <p>手番交代や、挟み判定(相手の石が続いているかどうか)のロジックで
     * 頻繁に使われるため、Board クラス側に判定式を重複させず
     * ここに一箇所だけ用意している。</p>
     *
     * @return 自分が BLACK なら WHITE、WHITE なら BLACK
     * @throws IllegalStateException EMPTY に対して呼び出した場合
     */
    public Disc opponent() {
        return switch (this) {
            case BLACK -> WHITE;
            case WHITE -> BLACK;
            case EMPTY -> throw new IllegalStateException("空きマスに対戦相手は存在しません");
        };
    }
}
