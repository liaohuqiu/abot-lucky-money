package in.srain.abot.action.condition;

import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import in.srain.abot.action.ActionParser;
import in.srain.abot.action.BaseAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Once event has been dispatched to one of its children, the event will be dispatched that child until it is finished.
 */
public class BranchAction extends BaseConditionAction {

    private int mSelectedActionIndex = -1;

    private boolean dispatchEventToSelectedAction(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        if (mSelectedActionIndex == -1) {
            return false;
        }
        BaseAction action = mActionList.get(mSelectedActionIndex);
        boolean consumed = action.consumeEvent(event, nodeInfo);
        if (action.hasDone()) {
            setActionDone();
        }
        return consumed;
    }

    @Override
    public void restart() {
        super.restart();
        mSelectedActionIndex = -1;
    }

    @Override
    protected boolean tryToFinishAction(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        if (mSelectedActionIndex != -1) {
            return dispatchEventToSelectedAction(event, nodeInfo);
        }

        boolean anyConsumed = false;
        for (int i = 0; i < mActionList.size(); i++) {
            BaseAction item = mActionList.get(i);
            if (!item.hasDone()) {
                if (item.consumeEvent(event, nodeInfo)) {
                    mSelectedActionIndex = i;
                    break;
                }
            }
        }
        boolean anyDone = false;
        for (int i = 0; i < mActionList.size(); i++) {
            BaseAction item = mActionList.get(i);
            if (item.hasDone()) {
                anyDone = true;
                break;
            }
        }
        if (anyDone) {
            setActionDone();
        }
        return anyConsumed;
    }

    @Override
    protected String genChildName(int level) {
        String tab = new String(new char[level]).replace("\0", CHAR_BLANK);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < mActionList.size(); i++) {
            list.add(tab + ActionParser.CHAR_FOR_BRANCH_ACTION + mActionList.get(i).debugStatus(level));
        }
        return TextUtils.join(CHAR_NEW_LINE, list);
    }
}
