package in.srain.apps.amoney.wechat;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;
import in.srain.abot.action.ElementClickFilter;
import in.srain.abot.element.AccessibilityNodeInfoSpec;
import in.srain.abot.element.Element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WeChatElementFilter extends ElementClickFilter {

    private static final String LUCKY_MONKEY_ITEM = "chat_item_lucky_money";
    private static final String NEW_MESSAGE_ITEM = "new_message";

    private static Set<String> sClickList = new HashSet<>();
    private String mLastMessageIdentity = null;

    public static void clear() {
        sClickList.clear();
    }

    private String getIdentityStringForLuckyMonkeyItem(AccessibilityNodeInfo root, AccessibilityNodeInfo nodeInfo) {
        int checkLength = 3;
        AccessibilityNodeInfo itemNode = nodeInfo.getParent();
        AccessibilityNodeInfo listNode = itemNode.getParent();

        AccessibilityNodeInfoCompat itemNodeInfoCompat = new AccessibilityNodeInfoCompat(itemNode);
        int index = itemNodeInfoCompat.getCollectionItemInfo().getRowIndex();

        List<AccessibilityNodeInfo> checkNodes = new ArrayList<>();
        boolean found = false;
        for (int i = listNode.getChildCount() - 1; i >= 0; i--) {
            AccessibilityNodeInfo item = listNode.getChild(i);
            if (item.equals(itemNode)) {
                found = true;
            }
            if (found && checkNodes.size() < checkLength) {
                checkNodes.add(item);
            }
        }
        if (index > checkLength && checkNodes.size() < checkLength) {
            return null;
        }

        List<CharSequence> identityList = new ArrayList<>();
        for (int i = 0; i < checkNodes.size(); i++) {
            identityList.add(AccessibilityNodeInfoSpec.getNodeIdentity(checkNodes.get(i)));
        }
        AccessibilityNodeInfo groupNameNode = Element.findFirst(root, "0:android.widget.FrameLayout>0:android.widget.FrameLayout>0:android.widget.LinearLayout>1:android.widget.TextView");
        String itemIdentity = TextUtils.join("->", identityList);
        if (groupNameNode != null) {
            itemIdentity = groupNameNode.getText() + itemIdentity;
        }
        return itemIdentity;
    }

    @Override
    public boolean checkShouldClick(Element element, AccessibilityNodeInfo root, AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null && nodeInfo.getParent() != null) {
            if (element.getName().equals(LUCKY_MONKEY_ITEM)) {
                String identity = getIdentityStringForLuckyMonkeyItem(root, nodeInfo);
                if (identity == null) {
                    return false;
                }
                return !sClickList.contains(identity);

            } else if (element.getName().equals(NEW_MESSAGE_ITEM)) {
                String identity = AccessibilityNodeInfoSpec.getNodeIdentity(nodeInfo);
                if (identity.equals(mLastMessageIdentity)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void setClicked(Element element, AccessibilityNodeInfo root, AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null && nodeInfo.getParent() != null) {
            if (element.getName().equals(LUCKY_MONKEY_ITEM)) {
                String itemIdentity = getIdentityStringForLuckyMonkeyItem(root, nodeInfo);
                sClickList.add(itemIdentity);
            } else if (element.getName().equals(NEW_MESSAGE_ITEM)) {
                String identity = AccessibilityNodeInfoSpec.getNodeIdentity(nodeInfo);
                mLastMessageIdentity = identity;
            }
        }
    }
}