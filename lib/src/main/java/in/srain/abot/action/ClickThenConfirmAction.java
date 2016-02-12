package in.srain.abot.action;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import in.srain.abot.element.Element;

import java.util.List;

public class ClickThenConfirmAction extends ShouldCheckAction {

    private Element mTargetElement;
    private Element mExpectElement;

    public ClickThenConfirmAction(Element targetElement, Element expectElement) {
        mTargetElement = targetElement;
        mExpectElement = expectElement;
    }

    @Override
    public boolean tryToFinishAction(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        List<AccessibilityNodeInfo> nodeInfoList = Element.findElements(nodeInfo, mTargetElement);
        if (nodeInfoList.size() > 0) {
            for (int i = 0; i < nodeInfoList.size(); i++) {
                AccessibilityNodeInfo item = nodeInfoList.get(i);
                if (item != null) {
                    item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean checkHasBeenDone(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        return Element.hasElement(nodeInfo, mExpectElement);
    }

    @Override
    protected boolean confirmAtOnce() {
        return false;
    }

    @Override
    public String toString() {
        return "[target: " + mTargetElement + "; expect: " + mExpectElement.toString() + "]";
    }
}