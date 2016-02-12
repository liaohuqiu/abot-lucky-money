package in.srain.abot.action;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import in.srain.abot.element.Element;

import java.util.List;

public class ClickAction extends SimpleAction {

    private Element mElement;

    public ClickAction(Element targetElement) {
        mElement = targetElement;
    }

    @Override
    protected boolean tryToFinishAction(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        List<AccessibilityNodeInfo> nodeInfoList = Element.findElements(nodeInfo, mElement);
        if (nodeInfoList.size() > 0) {
            boolean hit = false;
            for (int i = nodeInfoList.size() - 1; i >= 0; i--) {
                AccessibilityNodeInfo item = nodeInfoList.get(i);
                if (item != null && ElementClickFilter.shouldClick(mElement, nodeInfo, item)) {
                    item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    hit = true;
                    break;
                }
            }
            if (hit) {
                setActionDone();
                return true;
            }
        }
        return false;
    }
}