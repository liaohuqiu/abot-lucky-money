package in.srain.abot.action;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import in.srain.abot.element.Element;

public class HasElement extends BaseAction {

    private Element mElement;

    public HasElement(Element element) {
        mElement = element;
    }

    @Override
    protected boolean processEvent(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        if (Element.hasElement(nodeInfo, mElement)) {
            setActionDone();
            return true;
        }
        return false;
    }
}
