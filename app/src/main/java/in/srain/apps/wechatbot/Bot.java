package in.srain.apps.wechatbot;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import in.srain.abot.ABotConst;
import in.srain.abot.action.BaseAction;
import in.srain.abot.action.ElementClickFilter;
import in.srain.abot.element.AccessibilityNodeInfoSpec;
import in.srain.abot.element.Element;
import in.srain.apps.wechatbot.wechat.WeChatElementFilter;
import in.srain.cube.util.CLog;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Bot {

    private static Bot sInstance;
    private BaseAction mNeverEndAction;

    private Bot() {
        ElementClickFilter.setFilter(new WeChatElementFilter());
    }

    public static Bot getInstance() {
        if (sInstance == null) {
            sInstance = new Bot();
        }
        return sInstance;
    }

    public static void printNodeInfo(AccessibilityNodeInfo nodeInfo) {
        Iterator<Map.Entry<String, Element>> it = Element.getElementMap().entrySet().iterator();
        while (it.hasNext()) {
            Element element = it.next().getValue();
            List<AccessibilityNodeInfo> resultList = Element.findElements(nodeInfo, element.getNodeInfoList());
            if (resultList.size() == 0) {
                Log.d(ABotConst.TAG_FOR_NODE, "findElements not found: " + element.toString());
            } else {
                Log.d(ABotConst.TAG_FOR_NODE, "findElements found: " + element.toString());
                for (int i = 0; i < resultList.size(); i++) {
                    String string = AccessibilityNodeInfoSpec.pathToNode(resultList.get(i));
                    Log.d(ABotConst.TAG_FOR_NODE, "  element list: " + string);
                }
            }
        }
    }

    public void setMainAction(BaseAction action) {
        if (action == null) {
            return;
        }
        action.setRestart(true);
        mNeverEndAction = action;
    }

    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || !event.getPackageName().toString().equals(Const.MM_PACKAGE_NAME)) {
            return;
        }

        CLog.d(ABotConst.TAG_FOR_NODE, "printEventInfo");
        CLog.d(ABotConst.TAG_FOR_NODE, event.toString());

        AccessibilityNodeInfo nodeInfo = event.getSource();
        while (nodeInfo != null && nodeInfo.getParent() != null) {
            nodeInfo = nodeInfo.getParent();
        }

        AccessibilityNodeInfoSpec.printNodeMap(nodeInfo);
        printNodeInfo(nodeInfo);

        if (mNeverEndAction != null) {
            boolean consumed = mNeverEndAction.consumeEvent(event, nodeInfo);
            String status = mNeverEndAction.debugStatus(0);
            CLog.d(Const.TAG_FOR_ACTION, "consumed: %s, after event:\n %s", consumed, status);
        }
    }
}