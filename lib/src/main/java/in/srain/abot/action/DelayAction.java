package in.srain.abot.action;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import in.srain.abot.ABotConst;

public class DelayAction extends SimpleAction {

    private static final int STATE_INIT = 0;
    private static final int STATE_START_TIMEOUT = 1;

    private int mState = STATE_INIT;
    private long mTimeout = 0;

    public DelayAction(long timeout) {
        mTimeout = timeout;
    }

    @Override
    public void restart() {
        super.restart();
        mState = STATE_INIT;
    }

    @Override
    protected boolean tryToFinishAction(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        if (mState == STATE_INIT) {
            mState = STATE_START_TIMEOUT;
            ABotConst.sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setActionDone();
                }
            }, mTimeout);
        }
        return false;
    }
}
