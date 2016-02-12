package in.srain.abot.action;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import in.srain.abot.element.Element;

import java.util.List;

public class ScrollListAction extends ShouldCheckAction {

    private Element mTargetElement;
    private Element mChildElement;

    private List<String> mStartList;
    private List<String> mEndList;
    private boolean mReachEnd;
    private int mLastToIndex = -1;
    private int mToIndexHasNotChanged = 0;

    public ScrollListAction(Element targetElement, Element childElement) {
        mTargetElement = targetElement;
        mChildElement = childElement;
    }

    public static boolean hasAny(List list, List list2) {
        for (int i = 0; i < list2.size(); i++) {
            if (list.contains(list2.get(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean tryToFinishAction(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        List<AccessibilityNodeInfo> nodeList = Element.findElements(nodeInfo, mChildElement);
        if (nodeList.size() == 0) {
            return false;
        }
        mStartList = Element.extractTexts(nodeList);
        List<AccessibilityNodeInfo> nodeInfoList = Element.findElements(nodeInfo, mTargetElement);
        if (nodeInfoList.size() > 0) {
            for (int i = 0; i < nodeInfoList.size(); i++) {
                AccessibilityNodeInfo item = nodeInfoList.get(i);
                if (item != null) {
                    item.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                }
            }
        }
        return true;
    }

    @Override
    protected boolean checkHasBeenDone(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        List<AccessibilityNodeInfo> nodeList = Element.findElements(nodeInfo, mChildElement);
        int toIndex = event.getToIndex();
        mReachEnd = event.getItemCount() == toIndex + 1;
        if (toIndex == mLastToIndex) {
            mToIndexHasNotChanged++;
        } else {
            mLastToIndex = toIndex;
            mToIndexHasNotChanged = 0;
        }
        mEndList = Element.extractTexts(nodeList);
        if (mToIndexHasNotChanged > 0 || mReachEnd || mStartList.containsAll(mEndList) || !hasAny(mStartList, mEndList)) {
            return true;
        }
        return false;
    }

    public boolean hasReachedEnd() {
        return mReachEnd;
    }

    public List<String> getStartList() {
        return mStartList;
    }

    public List<String> getEndList() {
        return mEndList;
    }

    @Override
    public String toString() {
        return "Scroll: [target: " + mTargetElement + "]";
    }
}