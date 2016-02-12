package in.srain.abot.action;

import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import in.srain.abot.ABotConst;

public abstract class BaseAction {

    protected static final String CHAR_NEW_LINE = "\n";
    protected static final String CHAR_BLANK = "  ";
    private static final String CHAR_COLON = ":";
    private static final String CHAR_DOT = ".";

    private boolean mHasBeenDone = false;

    private String mName;
    private boolean mRestart = false;
    private int mEventCount = 0;

    public void setRestart(boolean restart) {
        mRestart = restart;
    }

    public BaseAction setName(String name) {
        mName = name;
        return this;
    }

    public void restart() {
        if (ABotConst.DEBUG) {
            Log.d(ABotConst.TAG_FOR_ACTION, "restart: " + this);
        }
        mHasBeenDone = false;
        mEventCount = 0;
    }

    /**
     * return true if event has been consumed.
     *
     * @param event
     * @param nodeInfo
     * @return
     */
    public boolean consumeEvent(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        if (hasDone()) {
            return false;
        } else {
            mEventCount++;
            boolean consumed = processEvent(event, nodeInfo);
            if (consumed) {
                if (ABotConst.DEBUG) {
                    Log.d(ABotConst.TAG_FOR_ACTION, "consumeEvent: " + this);
                }
            }
            return consumed;
        }
    }

    protected void setActionDone() {
        if (ABotConst.DEBUG) {
            Log.d(ABotConst.TAG_FOR_ACTION, "setActionDone: " + this);
        }
        mHasBeenDone = true;
        if (mRestart) {
            restart();
        }
    }

    public boolean hasDone() {
        return mHasBeenDone;
    }

    private String mNameForToString;

    @Override
    public String toString() {
        if (mNameForToString == null) {
            String name = super.toString();
            name = mName + CHAR_COLON + name.substring(name.lastIndexOf(CHAR_DOT) + 1);
            mNameForToString = name;
        }
        return mNameForToString;
    }

    /**
     * return true if event has been consumed
     *
     * @param event
     * @param nodeInfo
     * @return
     */
    protected abstract boolean processEvent(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo);

    public String debugStatus(int level) {
        String name = super.toString();
        name = name.substring(name.lastIndexOf(CHAR_DOT) + 1);
        StringBuilder stringBuilder = new StringBuilder(mName);
        stringBuilder.append(CHAR_COLON);
        stringBuilder.append(name);
        stringBuilder.append(", Done: " + hasDone());
        stringBuilder.append(", EventCount: " + mEventCount);
        String childName = genChildName(level + 1);
        if (childName != null && !TextUtils.isEmpty(childName)) {
            stringBuilder.append(CHAR_NEW_LINE + childName);
        }
        return stringBuilder.toString();
    }

    protected String genChildName(int level) {
        return null;
    }
}
