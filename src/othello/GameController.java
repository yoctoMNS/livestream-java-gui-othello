package othello;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

/**
 * キーボード入力を受け取り、対応する {@link GameCommand} を実行するコントローラ
 * (MVC における Controller)。
 *
 * <p>キーコードとコマンドの対応表を持つだけで、移動やゲームルールの詳細は
 * 一切知らない。実際の処理は Command / Model 側に委譲することで、
 * このクラスの責務を「入力の振り分け」だけに絞っている。</p>
 */
public final class GameController implements KeyListener {

    private final Map<Integer, GameCommand> keyToCommand = new HashMap<>();

    /**
     * WASD(移動)とEnter(配置)のキー割り当てを構築する。
     */
    public GameController(Board board, Cursor cursor, OthelloFrame view,
                           GameSoundEffects soundEffects) {
        registerCursorMovementKeys(cursor, view, soundEffects);
        registerDiscPlacementKey(board, cursor, view, soundEffects);
    }

    private void registerCursorMovementKeys(Cursor cursor, OthelloFrame view,
                                             GameSoundEffects soundEffects) {
        keyToCommand.put(KeyEvent.VK_W, new MoveCursorCommand(cursor, -1, 0, view, soundEffects));
        keyToCommand.put(KeyEvent.VK_S, new MoveCursorCommand(cursor, 1, 0, view, soundEffects));
        keyToCommand.put(KeyEvent.VK_A, new MoveCursorCommand(cursor, 0, -1, view, soundEffects));
        keyToCommand.put(KeyEvent.VK_D, new MoveCursorCommand(cursor, 0, 1, view, soundEffects));
    }

    private void registerDiscPlacementKey(Board board, Cursor cursor, OthelloFrame view,
                                           GameSoundEffects soundEffects) {
        keyToCommand.put(KeyEvent.VK_ENTER, new PlaceDiscCommand(board, cursor, view, soundEffects));
    }

    /** 押されたキーに対応するコマンドがあれば実行する。 */
    @Override
    public void keyPressed(KeyEvent event) {
        GameCommand command = keyToCommand.get(event.getKeyCode());
        if (command != null) {
            command.execute();
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        // このゲームではキーを離した瞬間の処理は不要。
    }

    @Override
    public void keyTyped(KeyEvent event) {
        // 文字入力(IME等)は扱わないため何もしない。
    }
}
