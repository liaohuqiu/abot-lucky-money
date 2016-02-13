package in.srain.abot.element;

import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Element {

    public static final String CHAR_DEFINE = "=>";
    public static final String CHAR_SP_COMMA = ":";
    public static final String CHAR_SP_ANY = "*";
    public static final String CHAR_SP_NEXT = ">";
    public static final String CHAR_SP_ATTR = "\\|";
    public static final String CHAR_SP_EQUAL = "=";
    public static final String CHAR_AND = "&";

    // how many children should it have?
    public static final String ATTR_COUNT = "childCount";

    // go back to parent
    public static final String ATTR_PARENT = "parent";

    // contain some text
    public static final String ATTR_TEXT_CONTAINS = "textContains";

    private static Map<String, Element> sMap = new HashMap<>();
    private static List<AccessibilityNodeInfo> EMPTY_LIST = new ArrayList<AccessibilityNodeInfo>();
    private String mName;
    private String mPatten;
    private List<NodeInfo> mNodeInfoList = new ArrayList<NodeInfo>();

    public Element(String name, String patten) {
        mName = name;
        mPatten = patten;
        readNodeList();
    }

    public static Element getElement(String key) {
        if (sMap.containsKey(key)) {
            return sMap.get(key);
        } else {
            throw new IllegalArgumentException("Can not find the definition fot this element:" + key);
        }
    }

    /**
     * Parse all the elements from config
     *
     * @param content
     * @return
     */
    public static Map<String, Element> parse(String content) {
        Map<String, Element> map = new HashMap<String, Element>();
        String[] lines = content.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String item = lines[i];
            if (!TextUtils.isEmpty(item) && item.contains(CHAR_DEFINE)) {
                String[] sp = item.split(CHAR_DEFINE);
                String key = sp[0].trim();
                String value = sp[1].trim();
                map.put(key, new Element(key, value));
            }
        }
        sMap = map;
        return map;
    }

    public static Map<String, Element> getElementMap() {
        return sMap;
    }

    public static boolean hasElement(AccessibilityNodeInfo nodeInfo, Element element) {
        return findElements(nodeInfo, element.getNodeInfoList()).size() > 0;
    }

    public static List<AccessibilityNodeInfo> findElements(AccessibilityNodeInfo nodeInfo, Element element) {
        return findElements(nodeInfo, element.getNodeInfoList());
    }

    public static List<AccessibilityNodeInfo> findElements(AccessibilityNodeInfo nodeInfo, String patten) {
        Element element = new Element(patten, patten);
        List<AccessibilityNodeInfo> list = findElements(nodeInfo, element);
        return list;
    }

    public static AccessibilityNodeInfo findFirst(AccessibilityNodeInfo nodeInfo, String patten) {
        List<AccessibilityNodeInfo> list = findElements(nodeInfo, patten);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public static List<AccessibilityNodeInfo> findElementsByClassName(AccessibilityNodeInfo nodeInfo, String className) {
        List<AccessibilityNodeInfo> list = new ArrayList<>();
        if (nodeInfo != null && nodeInfo.getClassName().equals(className)) {
            list.add(nodeInfo);
        }
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            list.addAll(findElementsByClassName(nodeInfo.getChild(i), className));
        }
        return list;
    }

    public static List<String> extractTexts(List<AccessibilityNodeInfo> nodeInfos) {
        List<String> textList = new ArrayList<>();
        for (int i = 0; i < nodeInfos.size(); i++) {
            textList.add(nodeInfos.get(i).getText().toString());
        }
        return textList;
    }

    public static List<AccessibilityNodeInfo> findElements(AccessibilityNodeInfo nodeInfo, List<NodeInfo> wantList) {

        if (wantList.size() == 0 || nodeInfo == null) {
            return EMPTY_LIST;
        }

        NodeInfo head = wantList.remove(0);
        while (true) {

            if (nodeInfo == null) {
                return EMPTY_LIST;
            }

            // check current
            // 1. check class name
            if (!nodeInfo.getClassName().equals(head.mClassName)) {
                return EMPTY_LIST;
            }

            // 2. check the other attributes
            if (head.mNodeAttribute != null) {
                NodeAttribute attribute = head.mNodeAttribute;
                if (attribute.mChildCount > 0 && nodeInfo.getChildCount() != attribute.mChildCount) {
                    return EMPTY_LIST;
                }

                if (!TextUtils.isEmpty(attribute.mTextContains)) {
                    if (TextUtils.isEmpty(nodeInfo.getText()) || !nodeInfo.getText().toString().contains(attribute.mTextContains)) {
                        return EMPTY_LIST;
                    }
                }
            }

            // check if this is the last level
            if (wantList.size() == 0) {
                if (head.mNodeAttribute != null && head.mNodeAttribute.mParentLevel > 0) {
                    int level = head.mNodeAttribute.mParentLevel;
                    while (level > 0 && nodeInfo.getParent() != null) {
                        nodeInfo = nodeInfo.getParent();
                        level--;
                    }
                }
                List<AccessibilityNodeInfo> resultList = new ArrayList<AccessibilityNodeInfo>();
                resultList.add(nodeInfo);
                return resultList;
            }

            // goto next level
            int nextElementIndex = head.mNextElementIndex;
            int count = nodeInfo.getChildCount();
            if (head.mAnyChild) {
                List<AccessibilityNodeInfo> resultList = new ArrayList<AccessibilityNodeInfo>();
                int anyIndex = head.mAnyIndex;
                int anyCount = 0;
                for (int i = 0; i < count; i++) {
                    List<NodeInfo> wantSubList = new ArrayList<>(wantList);
                    List<AccessibilityNodeInfo> subList = findElements(nodeInfo.getChild(i), wantSubList);
                    if (subList.size() == 0) {
                        continue;
                    }
                    if (anyIndex < 0) {
                        resultList.addAll(subList);
                    } else {
                        if (anyIndex == anyCount) {
                            resultList.addAll(subList);
                        }
                    }
                    anyCount++;
                }
                return resultList;
            } else {
                // from the last
                if (nextElementIndex < 0) {
                    nextElementIndex = count + nextElementIndex;
                }
                if (nextElementIndex >= 0 && nextElementIndex < count) {
                    head = wantList.remove(0);
                    nodeInfo = nodeInfo.getChild(nextElementIndex);
                } else {
                    return EMPTY_LIST;
                }
            }
        }
    }

    @Override
    public String toString() {
        return mName + " => " + mPatten;
    }

    public String getName() {
        return mName;
    }

    private void readNodeList() {
        String elements[] = mPatten.split(CHAR_SP_COMMA);
        for (int i = 1; i < elements.length; i++) {
            String item = elements[i];
            String parts[] = item.split(CHAR_SP_NEXT);

            NodeInfo element = new NodeInfo();
            element.setNodeAttribute(parts[0]);
            if (parts.length > 1) {
                element.mHasNext = true;
                String indexString = parts[1];
                if (indexString.startsWith(CHAR_SP_ANY)) {
                    element.mAnyChild = true;
                    String anyIndex = indexString.substring(1);
                    if (TextUtils.isEmpty(anyIndex)) {
                        element.mAnyIndex = -1;
                    } else {
                        element.mAnyIndex = Integer.parseInt(anyIndex);
                    }
                } else {
                    element.mNextElementIndex = Integer.parseInt(indexString);
                }
            } else {
                element.mHasNext = false;
            }
            mNodeInfoList.add(element);
        }
    }

    public List<NodeInfo> getNodeInfoList() {
        return new ArrayList<NodeInfo>(mNodeInfoList);
    }

    public static class NodeInfo {

        private boolean mAnyChild;
        private int mAnyIndex;

        private int mNextElementIndex;
        private CharSequence mClassName;
        private boolean mHasNext;
        private NodeAttribute mNodeAttribute;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Any: " + mAnyChild);
            builder.append("; NextElementIndex: " + mNextElementIndex);
            builder.append("; ClassName: " + mClassName);
            builder.append("; HasNext: " + mHasNext);
            return builder.toString();
        }

        private void setNodeAttribute(String attributeStr) {
            String parts[] = attributeStr.split(CHAR_SP_ATTR);
            if (parts.length <= 0) {
                return;
            }
            mClassName = parts[0];
            if (parts.length == 1) {
                return;
            }
            String attrs[] = parts[1].split(CHAR_AND);
            NodeAttribute nodeAttribute = new NodeAttribute();
            for (int i = 0; i < attrs.length; i++) {
                String kv[] = attrs[i].split(CHAR_SP_EQUAL);
                String key = kv[0];
                String value = kv[1];
                if (key.equals(ATTR_COUNT)) {
                    nodeAttribute.mChildCount = Integer.parseInt(value);
                } else if (key.equals(ATTR_PARENT)) {
                    nodeAttribute.mParentLevel = Integer.parseInt(value);
                } else if (key.equals(ATTR_TEXT_CONTAINS)) {
                    nodeAttribute.mTextContains = value;
                }
            }
            mNodeAttribute = nodeAttribute;
        }
    }

    public static class NodeAttribute {
        private int mChildCount;
        private int mParentLevel;
        private String mTextContains;
    }
}