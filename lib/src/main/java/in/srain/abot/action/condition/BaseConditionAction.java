package in.srain.abot.action.condition;

import in.srain.abot.action.BaseAction;
import in.srain.abot.action.SimpleAction;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseConditionAction extends SimpleAction {

    protected List<BaseAction> mActionList;

    public BaseConditionAction() {
    }

    public BaseConditionAction(List<BaseAction> list) {
        addSubActions(list);
    }

    public BaseConditionAction(BaseAction[] actions) {
        for (int i = 0; i < actions.length; i++) {
            addSubAction(actions[i]);
        }
    }

    @Override
    public boolean hasDone() {
        if (mActionList == null || mActionList.size() == 0) {
            return true;
        }
        return super.hasDone();
    }

    @Override
    public void restart() {
        super.restart();
        if (mActionList != null) {
            for (int i = 0; i < mActionList.size(); i++) {
                mActionList.get(i).restart();
            }
        }
    }

    public int getSubActionCount() {
        if (mActionList == null) {
            return 0;
        }
        return mActionList.size();
    }

    private void ensureActionList() {
        if (mActionList == null) {
            mActionList = new ArrayList<>();
        }
    }

    public void addSubAction(BaseAction action) {
        ensureActionList();
        mActionList.add(action);
    }

    public void addSubActions(List<BaseAction> list) {
        ensureActionList();
        mActionList.addAll(list);
    }
}
