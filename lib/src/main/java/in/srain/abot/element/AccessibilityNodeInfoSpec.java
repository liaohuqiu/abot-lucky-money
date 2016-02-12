package in.srain.abot.element;

import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import in.srain.abot.ABotConst;

import java.util.ArrayList;
import java.util.List;

public class AccessibilityNodeInfoSpec {

    private static String TEXT_PRE = ";text:";
    private static String CONTENT_DESCRIPTION_PRE = ";contentDescription:";
    private static String CHAR_PIPE = "|";
    private static String CHAR_LEFT_BRACKET = "[";
    private static String CHAR_RIGHT_BRACKET = "]";
    private int mIndex;
    private CharSequence mClassName;

    private AccessibilityNodeInfoSpec(int index, CharSequence className) {
        mIndex = index;
        mClassName = className;
    }

    public static String withNodeInfo(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return null;
        }
        return String.format(":[text: %s; contentDescription: %s]", nodeInfo.getText(), nodeInfo.getContentDescription());
    }

    public static void printNodeMap(AccessibilityNodeInfo nodeInfo) {
        if (!ABotConst.DEBUG) {
            return;
        }
        List<AccessibilityNodeInfoSpec> list = new ArrayList<AccessibilityNodeInfoSpec>();
        if (nodeInfo != null) {
            list.add(new AccessibilityNodeInfoSpec(0, nodeInfo.getClassName()));
        }
        printAllChildren(nodeInfo, list);
    }

    public static String pathToNode(AccessibilityNodeInfo nodeInfo) {
        AccessibilityNodeInfo origin = nodeInfo;
        List<AccessibilityNodeInfoSpec> list = new ArrayList<AccessibilityNodeInfoSpec>();
        list.add(0, new AccessibilityNodeInfoSpec(0, nodeInfo.getClassName()));
        AccessibilityNodeInfo parent = nodeInfo.getParent();
        while (parent != null) {
            int index = 0;
            for (int i = 0; i < parent.getChildCount(); i++) {
                AccessibilityNodeInfo child = parent.getChild(i);
                if (child != null && child.equals(nodeInfo)) {
                    index = i;
                    break;
                }
            }
            list.add(0, new AccessibilityNodeInfoSpec(index, parent.getClassName()));
            nodeInfo = parent;
            parent = nodeInfo.getParent();
        }
        list.add(0, new AccessibilityNodeInfoSpec(0, nodeInfo.getClassName()));
        String string = TextUtils.join(Element.CHAR_SP_NEXT, list) + withNodeInfo(origin);
        return string;
    }

    private static void printAllChildren(AccessibilityNodeInfo nodeInfo, List<AccessibilityNodeInfoSpec> list) {
        if (nodeInfo == null || nodeInfo.getChildCount() == 0) {
            String string = TextUtils.join(Element.CHAR_SP_NEXT, list);
            Log.d(ABotConst.TAG_FOR_NODE, string + withNodeInfo(nodeInfo));
            return;
        } else {
            String string = TextUtils.join(Element.CHAR_SP_NEXT, list);
            Log.d(ABotConst.TAG_FOR_NODE, string + withNodeInfo(nodeInfo));
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo child = nodeInfo.getChild(i);
                if (child == null) {
                    continue;
                }
                List<AccessibilityNodeInfoSpec> newList = new ArrayList<>(list);
                AccessibilityNodeInfoSpec accessibilityNodeInfoSpec = new AccessibilityNodeInfoSpec(i, child.getClassName());
                newList.add(accessibilityNodeInfoSpec);
                printAllChildren(child, newList);
            }
        }
    }

    public static String getNodeIdentity(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder(nodeInfo.getClassName());
        stringBuilder.append(TEXT_PRE);
        stringBuilder.append(nodeInfo.getText());
        stringBuilder.append(CONTENT_DESCRIPTION_PRE);
        stringBuilder.append(nodeInfo.getContentDescription());
        if (nodeInfo.getChildCount() > 0) {
            stringBuilder.append(CHAR_LEFT_BRACKET);
            int len = nodeInfo.getChildCount();
            for (int i = 0; i < len; i++) {
                stringBuilder.append(getNodeIdentity(nodeInfo.getChild(i)));
                if (i != len - 1) {
                    stringBuilder.append(CHAR_PIPE);
                }
            }
            stringBuilder.append(CHAR_RIGHT_BRACKET);
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return mIndex + Element.CHAR_SP_COMMA + mClassName;
    }
}
