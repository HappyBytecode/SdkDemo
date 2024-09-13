package anda.travel.driver.util.keyboard.callback;

/**
 * 用于接收键盘事件的回调
 *
 * @author Simon
 */
interface IkeyBoardCallback {
    /**
     * 当键盘显示时回调
     */
    void onKeyBoardShow();

    /**
     * 当键盘隐藏时回调
     */
    void onKeyBoardHidden();
}
