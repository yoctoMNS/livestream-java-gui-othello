package othello;

/**
 * プレイヤーのキー入力1回分に対応する操作を表すコマンド(Commandパターン)。
 *
 * <p>「どのキーが押されたか」と「押されたときに何をするか」を分離するために
 * このインタフェースを導入している。{@link GameController} はキーコードから
 * GameCommand への対応表を持つだけでよく、キー入力処理を追加・変更したい場合も
 * 新しい GameCommand の実装クラスを1つ追加するだけで済む(拡張に対して開かれ、
 * 修正に対して閉じている = SOLIDのOpen/Closed原則)。</p>
 */
// interface(インタフェース)とは「中身の処理を持たない、決まり事(規約)だけを定義する型」。
// 「GameCommandを名乗るクラスは、必ずexecute()というメソッドを持っていなければならない」
// というルールだけを決めており、実際の処理は後述の
// MoveCursorCommand や PlaceDiscCommand といったクラス側に書かれる。
public interface GameCommand {

    /** このコマンドが表す操作を実行する。 */
    // 中身({...})がない、宣言だけのメソッド。
    // 実装クラス(このインタフェースを implements したクラス)は、
    // 必ずこのexecute()の中身を用意しなければコンパイルが通らない。
    void execute();
}
