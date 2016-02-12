package in.srain.abot.action.condition;

import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import in.srain.abot.action.ActionParser;
import in.srain.abot.action.BaseAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Dispatch event to its children and finish all of them one by one.
 */
public class AndAction extends BaseConditionAction {

    @Override
    protected boolean tryToFinishAction(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        if (mActionList == null || mActionList.size() == 0) {
            return false;
        }
        boolean consumed = false;
        for (int i = 0; i < mActionList.size(); i++) {
            BaseAction item = mActionList.get(i);
            if (!item.hasDone()) {
                consumed = item.consumeEvent(event, nodeInfo);
                if (consumed || !item.hasDone()) {
                    break;
                }
            }
        }
        boolean allDone = true;
        for (int i = 0; i < mActionList.size(); i++) {
            if (!mActionList.get(i).hasDone()) {
                allDone = false;
                break;
            }
        }
        if (allDone) {
            setActionDone();
        }
        return consumed;
    }

    @Override
    protected String genChildName(int level) {
        String tab = new String(new char[level]).replace("\0", CHAR_BLANK);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < mActionList.size(); i++) {
            list.add(tab + ActionParser.CHAR_FOR_AND_ACTION + mActionList.get(i).debugStatus(level));
        }
        return TextUtils.join(CHAR_NEW_LINE, list);
    }
}
