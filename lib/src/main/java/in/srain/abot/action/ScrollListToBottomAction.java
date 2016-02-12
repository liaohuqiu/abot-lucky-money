package in.srain.abot.action;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import in.srain.abot.element.Element;

import java.util.HashSet;
import java.util.Set;

public class ScrollListToBottomAction extends ShouldCheckAction {

    private Element mTargetElement;
    private Element mChildElement;
    private ScrollListAction mScrollListAction;
    private Set<String> mValueList;

    public ScrollListToBottomAction(Element targetElement, Element childElement) {
        mTargetElement = targetElement;
        mChildElement = childElement;
        mValueList = new HashSet<String>();
        mScrollListAction = new ScrollListAction(mTargetElement, mChildElement);
    }

    @Override
    public boolean tryToFinishAction(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        boolean needMore = mScrollListAction.consumeEvent(event, nodeInfo);
        if (!needMore) {
            boolean hasReachedEnd = mScrollListAction.hasReachedEnd();
            mValueList.addAll(mScrollListAction.getStartList());
            mValueList.addAll(mScrollListAction.getEndList());
            if (mScrollListAction.hasReachedEnd()) {
                return true;
            } else {
                mScrollListAction = new ScrollListAction(mTargetElement, mChildElement);
                mScrollListAction.consumeEvent(event, nodeInfo);
            }
        }
        return false;
    }

    @Override
    protected boolean checkHasBeenDone(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        // confirm at once
        String[] list = new String[]{};
        list = mValueList.toArray(list);
        return true;
    }

    @Override
    public String toString() {
        return "ScrollListToBottomAction: [target: " + mTargetElement + "]";
    }

    @Override
    protected boolean confirmAtOnce() {
        return false;
    }
}