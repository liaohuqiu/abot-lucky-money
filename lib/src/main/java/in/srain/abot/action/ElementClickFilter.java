package in.srain.abot.action;

import android.view.accessibility.AccessibilityNodeInfo;
import in.srain.abot.element.Element;

public abstract class ElementClickFilter {

    private static ElementClickFilter sFilter;

    public static void setFilter(ElementClickFilter elementClickFilter) {
        sFilter = elementClickFilter;
    }

    public static boolean shouldClick(Element element, AccessibilityNodeInfo root, AccessibilityNodeInfo nodeInfo) {
        if (sFilter != null) {
            boolean ret = sFilter.checkShouldClick(element, root, nodeInfo);
            if (ret) {
                sFilter.setClicked(element, root, nodeInfo);
            }
            return ret;
        }
        return false;
    }

    public abstract boolean checkShouldClick(Element element, AccessibilityNodeInfo root, AccessibilityNodeInfo nodeInfo);

    public abstract void setClicked(Element element, AccessibilityNodeInfo root, AccessibilityNodeInfo nodeInfo);
}
