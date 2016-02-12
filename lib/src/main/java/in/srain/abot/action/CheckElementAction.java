package in.srain.abot.action;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import in.srain.abot.element.Element;

import java.util.List;

public class CheckElementAction extends SimpleAction {

    private Element mElement;

    public CheckElementAction(Element targetElement) {
        mElement = targetElement;
    }

    @Override
    protected boolean tryToFinishAction(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        List<AccessibilityNodeInfo> nodeInfoList = Element.findElements(nodeInfo, mElement);
        if (nodeInfoList.size() > 0) {
            for (int i = 0; i < nodeInfoList.size(); i++) {
                AccessibilityNodeInfo item = nodeInfoList.get(i);
                if (item != null) {
                    ElementClickFilter.shouldClick(mElement, nodeInfo, item);
                }
            }
        }
        return false;
    }
}