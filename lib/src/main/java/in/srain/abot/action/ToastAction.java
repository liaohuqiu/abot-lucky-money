package in.srain.abot.action;

import android.app.Notification;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class ToastAction extends SimpleAction {

    @Override
    protected boolean tryToFinishAction(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            Parcelable parcelable = event.getParcelableData();
            // Status Bar Notification
            if (parcelable instanceof Notification) {
            } else {
                setActionDone();
            }
        }
        return false;
    }
}
