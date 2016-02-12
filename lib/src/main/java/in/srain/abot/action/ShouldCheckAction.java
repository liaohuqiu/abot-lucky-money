package in.srain.abot.action;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public abstract class ShouldCheckAction extends BaseAction {

    private static final int STATE_WAITING_EVENT = 1;
    private static final int STATE_PENDING_CHECK = 2;
    private int mState = STATE_WAITING_EVENT;

    @Override
    protected boolean processEvent(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        if (mState == STATE_WAITING_EVENT) {
            boolean consumed = tryToFinishAction(event, nodeInfo);
            if (consumed) {
                mState = STATE_PENDING_CHECK;
                if (confirmAtOnce()) {
                    checkHasBeenDone(event, nodeInfo);
                    return true;
                }
            }
        }
        if (mState == STATE_PENDING_CHECK) {
            boolean done = checkHasBeenDone(event, nodeInfo);
            if (done) {
                return true;
            }
        }
        return false;
    }

    protected boolean confirmAtOnce() {
        return true;
    }

    protected abstract boolean tryToFinishAction(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo);

    protected abstract boolean checkHasBeenDone(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo);
}
