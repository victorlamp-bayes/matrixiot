package com.victorlamp.matrixiot.service.rule.obj;

import com.victorlamp.matrixiot.service.rule.enums.RuleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Objects;

/**
 * 规则内容
 *
 * @author: Dylan
 * @date: 2023/8/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleContent {

    /**
     * 规则类型
     */
    private RuleType type;

    /**
     * 规则触发器
     */
    private Trigger trigger;

    /**
     * 规则执行条件
     */
    private Condition condition;

    /**
     * 规则执行动作
     */
    private ArrayList<Action> action;

    /**
     * drl 规则内容
     */
    private DRLData drl;

    /**
     * 判断2个对象的 TCA 数据是否相等
     *
     * @param that
     * @return: boolean
     * @author: Dylan-孙林
     * @Date: 2023/9/18
     */
    public boolean tacEquals(RuleContent that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        return type == that.type && trigger.equals(that.trigger) && condition.equals(that.condition) && action.equals(that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, trigger, condition, action);
    }

    public boolean equals(RuleContent that) {
        return this.tacEquals(that);
    }
}
