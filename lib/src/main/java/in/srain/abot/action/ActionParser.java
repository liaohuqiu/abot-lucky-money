package in.srain.abot.action;

import android.text.TextUtils;
import in.srain.abot.action.condition.AndAction;
import in.srain.abot.action.condition.BaseConditionAction;
import in.srain.abot.action.condition.BranchAction;
import in.srain.abot.action.condition.OrAction;
import in.srain.abot.element.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * Can not define action in queue action or hit-nay action
 */
public class ActionParser {

    private static final String CHAR_DEFINE = "=>";
    private static final String CHAR_FOR_SINGLE_ACTION = ":";

    public static final String CHAR_FOR_AND_ACTION = "->";
    public static final String CHAR_FOR_BRANCH_ACTION = "~>";
    public static final String CHAR_FOR_OR_ACTION = "|";
    private static final String CHAR_FOR_OR_ACTION_SP = "\\|";

    private static final String CHAR_COMMENT = "//";

    private static final String CHAR_PARENTHESIS = "(";

    private static final String ACTION_TRY_CLICK = "click";
    private static final String ACTION_DELAY = "delay";
    private static final String ACTION_TOAST = "toast";
    private static final String ACTION_CHECK_ELEMENT = "check_element";
    private static final String ACTION_NO_ELEMENT = "no_element";
    private static final String ACTION_HAS_ELEMENT = "has_element";

    private static final Map<String, String> sMap = new HashMap<>();

    public static BaseAction parse(String content) {

        String[] lines = content.split("\n");
        for (int i = 0; i < lines.length; i++) {
            parseLine(lines[i]);
        }

        BaseAction mainAction = parseAction("main", sMap.get("main"));
        return mainAction;
    }

    public static void parseLine(String content) {
        if (TextUtils.isEmpty(content) || !content.contains(CHAR_DEFINE)) {
            return;
        }
        if (content.startsWith(CHAR_COMMENT)) {
            return;
        }
        String[] kvs = content.split(CHAR_DEFINE);
        String key = kvs[0].trim();
        String value = kvs[1].trim();
        value = removeParenthesis(value);
        sMap.put(key, value);
    }

    private static String removeParenthesis(String content) {
        if (!TextUtils.isEmpty(content) && content.startsWith(CHAR_PARENTHESIS)) {
            return content.substring(1, content.length() - 1);
        }
        return content;
    }

    private static BaseAction parseAction(String name, String patten) {
        BaseAction action = null;
        if (patten.contains(CHAR_FOR_SINGLE_ACTION)) {
            action = parseSingleAction(patten);
        } else if (patten.contains(CHAR_FOR_AND_ACTION)) {
            action = parseAndAction(patten);
        } else if (patten.contains(CHAR_FOR_BRANCH_ACTION)) {
            action = parseBranchAction(patten);
        } else {
            action = parseOrAction(patten);
        }
        if (action != null) {
            action.setName(name);
        }
        return action;
    }

    private static BaseAction parseBranchAction(String patten) {
        String[] items = patten.split(CHAR_FOR_BRANCH_ACTION);
        BaseConditionAction action = new BranchAction();
        for (int i = 0; i < items.length; i++) {
            String actionName = items[i];
            BaseAction itemAction = parseActionForCondition(actionName);
            if (itemAction != null) {
                action.addSubAction(itemAction);
            }
        }
        return action;
    }

    private static BaseAction parseAndAction(String patten) {
        String[] items = patten.split(CHAR_FOR_AND_ACTION);
        BaseConditionAction action = new AndAction();
        for (int i = 0; i < items.length; i++) {
            String actionName = items[i];
            BaseAction itemAction = parseActionForCondition(actionName);
            if (itemAction != null) {
                action.addSubAction(itemAction);
            }
        }
        return action;
    }

    private static BaseAction parseActionForCondition(String actionName) {
        if (!sMap.containsKey(actionName)) {
            throw new IllegalArgumentException("Can not find definition for this action: " + actionName);
        }
        String actionDefinition = sMap.get(actionName);
        BaseAction itemAction = parseAction(actionName, actionDefinition);
        return itemAction;
    }

    private static BaseAction parseOrAction(String patten) {
        String[] items = patten.split(CHAR_FOR_OR_ACTION_SP);
        BaseConditionAction action = new OrAction();
        for (int i = 0; i < items.length; i++) {
            String actionName = items[i];
            BaseAction itemAction = parseActionForCondition(actionName);
            if (itemAction != null) {
                action.addSubAction(itemAction);
            }
        }
        return action;
    }

    private static BaseAction parseSingleAction(String patten) {
        String[] items = patten.split(CHAR_FOR_SINGLE_ACTION);
        String name = items[0];
        String definition = null;
        if (items.length > 1) {
            definition = items[1];
        }

        BaseAction action = null;

        switch (name) {
            case ACTION_TRY_CLICK:
                action = new ClickAction(Element.getElement(definition));
                break;

            case ACTION_DELAY:
                action = new DelayAction(Integer.parseInt(definition));
                break;

            case ACTION_TOAST:
                action = new ToastAction();
                break;
            case ACTION_CHECK_ELEMENT:
                action = new CheckElementAction(Element.getElement(definition));
                break;
            case ACTION_NO_ELEMENT:
                action = new NoElement(Element.getElement(definition));
                break;
            case ACTION_HAS_ELEMENT:
                action = new HasElement(Element.getElement(definition));
                break;
        }

        return action;
    }
}
