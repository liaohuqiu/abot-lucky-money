package in.srain.abot.action;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public abstract class SimpleAction extends BaseAction {

    @Override
    protected boolean processEvent(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        return tryToFinishAction(event, nodeInfo);
    }

    /**
     * return true if event has been consumed
     *
     * @param event
     * @param nodeInfo
     * @return
     */
    protected abstract boolean tryToFinishAction(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo);
}
