package in.srain.abot.action.condition;

import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import in.srain.abot.action.ActionParser;
import in.srain.abot.action.BaseAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Dispatch event to any one of its children, until one of them finish.
 * Event will not be dispatched, once it has been consumed by one of its children.
 */
public class OrAction extends BaseConditionAction {

    @Override
    protected boolean tryToFinishAction(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        boolean anyConsumed = false;
        for (int i = 0; i < mActionList.size(); i++) {
            BaseAction item = mActionList.get(i);
            if (!item.hasDone()) {
                if (item.consumeEvent(event, nodeInfo)) {
                    anyConsumed = true;
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
            list.add(tab + ActionParser.CHAR_FOR_OR_ACTION + mActionList.get(i).debugStatus(level));
        }
        return TextUtils.join(CHAR_NEW_LINE, list);
    }
}
