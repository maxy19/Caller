package com.maxy.caller.persistent.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskBaseInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TaskBaseInfoExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andGroupKeyIsNull() {
            addCriterion("group_key is null");
            return (Criteria) this;
        }

        public Criteria andGroupKeyIsNotNull() {
            addCriterion("group_key is not null");
            return (Criteria) this;
        }

        public Criteria andGroupKeyEqualTo(String value) {
            addCriterion("group_key =", value, "groupKey");
            return (Criteria) this;
        }

        public Criteria andGroupKeyNotEqualTo(String value) {
            addCriterion("group_key <>", value, "groupKey");
            return (Criteria) this;
        }

        public Criteria andGroupKeyGreaterThan(String value) {
            addCriterion("group_key >", value, "groupKey");
            return (Criteria) this;
        }

        public Criteria andGroupKeyGreaterThanOrEqualTo(String value) {
            addCriterion("group_key >=", value, "groupKey");
            return (Criteria) this;
        }

        public Criteria andGroupKeyLessThan(String value) {
            addCriterion("group_key <", value, "groupKey");
            return (Criteria) this;
        }

        public Criteria andGroupKeyLessThanOrEqualTo(String value) {
            addCriterion("group_key <=", value, "groupKey");
            return (Criteria) this;
        }

        public Criteria andGroupKeyLike(String value) {
            addCriterion("group_key like", value, "groupKey");
            return (Criteria) this;
        }

        public Criteria andGroupKeyNotLike(String value) {
            addCriterion("group_key not like", value, "groupKey");
            return (Criteria) this;
        }

        public Criteria andGroupKeyIn(List<String> values) {
            addCriterion("group_key in", values, "groupKey");
            return (Criteria) this;
        }

        public Criteria andGroupKeyNotIn(List<String> values) {
            addCriterion("group_key not in", values, "groupKey");
            return (Criteria) this;
        }

        public Criteria andGroupKeyBetween(String value1, String value2) {
            addCriterion("group_key between", value1, value2, "groupKey");
            return (Criteria) this;
        }

        public Criteria andGroupKeyNotBetween(String value1, String value2) {
            addCriterion("group_key not between", value1, value2, "groupKey");
            return (Criteria) this;
        }

        public Criteria andBizKeyIsNull() {
            addCriterion("biz_key is null");
            return (Criteria) this;
        }

        public Criteria andBizKeyIsNotNull() {
            addCriterion("biz_key is not null");
            return (Criteria) this;
        }

        public Criteria andBizKeyEqualTo(String value) {
            addCriterion("biz_key =", value, "bizKey");
            return (Criteria) this;
        }

        public Criteria andBizKeyNotEqualTo(String value) {
            addCriterion("biz_key <>", value, "bizKey");
            return (Criteria) this;
        }

        public Criteria andBizKeyGreaterThan(String value) {
            addCriterion("biz_key >", value, "bizKey");
            return (Criteria) this;
        }

        public Criteria andBizKeyGreaterThanOrEqualTo(String value) {
            addCriterion("biz_key >=", value, "bizKey");
            return (Criteria) this;
        }

        public Criteria andBizKeyLessThan(String value) {
            addCriterion("biz_key <", value, "bizKey");
            return (Criteria) this;
        }

        public Criteria andBizKeyLessThanOrEqualTo(String value) {
            addCriterion("biz_key <=", value, "bizKey");
            return (Criteria) this;
        }

        public Criteria andBizKeyLike(String value) {
            addCriterion("biz_key like", value, "bizKey");
            return (Criteria) this;
        }

        public Criteria andBizKeyNotLike(String value) {
            addCriterion("biz_key not like", value, "bizKey");
            return (Criteria) this;
        }

        public Criteria andBizKeyIn(List<String> values) {
            addCriterion("biz_key in", values, "bizKey");
            return (Criteria) this;
        }

        public Criteria andBizKeyNotIn(List<String> values) {
            addCriterion("biz_key not in", values, "bizKey");
            return (Criteria) this;
        }

        public Criteria andBizKeyBetween(String value1, String value2) {
            addCriterion("biz_key between", value1, value2, "bizKey");
            return (Criteria) this;
        }

        public Criteria andBizKeyNotBetween(String value1, String value2) {
            addCriterion("biz_key not between", value1, value2, "bizKey");
            return (Criteria) this;
        }

        public Criteria andTopicIsNull() {
            addCriterion("topic is null");
            return (Criteria) this;
        }

        public Criteria andTopicIsNotNull() {
            addCriterion("topic is not null");
            return (Criteria) this;
        }

        public Criteria andTopicEqualTo(String value) {
            addCriterion("topic =", value, "topic");
            return (Criteria) this;
        }

        public Criteria andTopicNotEqualTo(String value) {
            addCriterion("topic <>", value, "topic");
            return (Criteria) this;
        }

        public Criteria andTopicGreaterThan(String value) {
            addCriterion("topic >", value, "topic");
            return (Criteria) this;
        }

        public Criteria andTopicGreaterThanOrEqualTo(String value) {
            addCriterion("topic >=", value, "topic");
            return (Criteria) this;
        }

        public Criteria andTopicLessThan(String value) {
            addCriterion("topic <", value, "topic");
            return (Criteria) this;
        }

        public Criteria andTopicLessThanOrEqualTo(String value) {
            addCriterion("topic <=", value, "topic");
            return (Criteria) this;
        }

        public Criteria andTopicLike(String value) {
            addCriterion("topic like", value, "topic");
            return (Criteria) this;
        }

        public Criteria andTopicNotLike(String value) {
            addCriterion("topic not like", value, "topic");
            return (Criteria) this;
        }

        public Criteria andTopicIn(List<String> values) {
            addCriterion("topic in", values, "topic");
            return (Criteria) this;
        }

        public Criteria andTopicNotIn(List<String> values) {
            addCriterion("topic not in", values, "topic");
            return (Criteria) this;
        }

        public Criteria andTopicBetween(String value1, String value2) {
            addCriterion("topic between", value1, value2, "topic");
            return (Criteria) this;
        }

        public Criteria andTopicNotBetween(String value1, String value2) {
            addCriterion("topic not between", value1, value2, "topic");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNull() {
            addCriterion("description is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNotNull() {
            addCriterion("description is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionEqualTo(String value) {
            addCriterion("description =", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotEqualTo(String value) {
            addCriterion("description <>", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThan(String value) {
            addCriterion("description >", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("description >=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThan(String value) {
            addCriterion("description <", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThanOrEqualTo(String value) {
            addCriterion("description <=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLike(String value) {
            addCriterion("description like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotLike(String value) {
            addCriterion("description not like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionIn(List<String> values) {
            addCriterion("description in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotIn(List<String> values) {
            addCriterion("description not in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionBetween(String value1, String value2) {
            addCriterion("description between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotBetween(String value1, String value2) {
            addCriterion("description not between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andAlarmEmailIsNull() {
            addCriterion("alarm_email is null");
            return (Criteria) this;
        }

        public Criteria andAlarmEmailIsNotNull() {
            addCriterion("alarm_email is not null");
            return (Criteria) this;
        }

        public Criteria andAlarmEmailEqualTo(String value) {
            addCriterion("alarm_email =", value, "alarmEmail");
            return (Criteria) this;
        }

        public Criteria andAlarmEmailNotEqualTo(String value) {
            addCriterion("alarm_email <>", value, "alarmEmail");
            return (Criteria) this;
        }

        public Criteria andAlarmEmailGreaterThan(String value) {
            addCriterion("alarm_email >", value, "alarmEmail");
            return (Criteria) this;
        }

        public Criteria andAlarmEmailGreaterThanOrEqualTo(String value) {
            addCriterion("alarm_email >=", value, "alarmEmail");
            return (Criteria) this;
        }

        public Criteria andAlarmEmailLessThan(String value) {
            addCriterion("alarm_email <", value, "alarmEmail");
            return (Criteria) this;
        }

        public Criteria andAlarmEmailLessThanOrEqualTo(String value) {
            addCriterion("alarm_email <=", value, "alarmEmail");
            return (Criteria) this;
        }

        public Criteria andAlarmEmailLike(String value) {
            addCriterion("alarm_email like", value, "alarmEmail");
            return (Criteria) this;
        }

        public Criteria andAlarmEmailNotLike(String value) {
            addCriterion("alarm_email not like", value, "alarmEmail");
            return (Criteria) this;
        }

        public Criteria andAlarmEmailIn(List<String> values) {
            addCriterion("alarm_email in", values, "alarmEmail");
            return (Criteria) this;
        }

        public Criteria andAlarmEmailNotIn(List<String> values) {
            addCriterion("alarm_email not in", values, "alarmEmail");
            return (Criteria) this;
        }

        public Criteria andAlarmEmailBetween(String value1, String value2) {
            addCriterion("alarm_email between", value1, value2, "alarmEmail");
            return (Criteria) this;
        }

        public Criteria andAlarmEmailNotBetween(String value1, String value2) {
            addCriterion("alarm_email not between", value1, value2, "alarmEmail");
            return (Criteria) this;
        }

        public Criteria andExecutorRouterStrategyIsNull() {
            addCriterion("executor_router_strategy is null");
            return (Criteria) this;
        }

        public Criteria andExecutorRouterStrategyIsNotNull() {
            addCriterion("executor_router_strategy is not null");
            return (Criteria) this;
        }

        public Criteria andExecutorRouterStrategyEqualTo(Byte value) {
            addCriterion("executor_router_strategy =", value, "executorRouterStrategy");
            return (Criteria) this;
        }

        public Criteria andExecutorRouterStrategyNotEqualTo(Byte value) {
            addCriterion("executor_router_strategy <>", value, "executorRouterStrategy");
            return (Criteria) this;
        }

        public Criteria andExecutorRouterStrategyGreaterThan(Byte value) {
            addCriterion("executor_router_strategy >", value, "executorRouterStrategy");
            return (Criteria) this;
        }

        public Criteria andExecutorRouterStrategyGreaterThanOrEqualTo(Byte value) {
            addCriterion("executor_router_strategy >=", value, "executorRouterStrategy");
            return (Criteria) this;
        }

        public Criteria andExecutorRouterStrategyLessThan(Byte value) {
            addCriterion("executor_router_strategy <", value, "executorRouterStrategy");
            return (Criteria) this;
        }

        public Criteria andExecutorRouterStrategyLessThanOrEqualTo(Byte value) {
            addCriterion("executor_router_strategy <=", value, "executorRouterStrategy");
            return (Criteria) this;
        }

        public Criteria andExecutorRouterStrategyIn(List<Byte> values) {
            addCriterion("executor_router_strategy in", values, "executorRouterStrategy");
            return (Criteria) this;
        }

        public Criteria andExecutorRouterStrategyNotIn(List<Byte> values) {
            addCriterion("executor_router_strategy not in", values, "executorRouterStrategy");
            return (Criteria) this;
        }

        public Criteria andExecutorRouterStrategyBetween(Byte value1, Byte value2) {
            addCriterion("executor_router_strategy between", value1, value2, "executorRouterStrategy");
            return (Criteria) this;
        }

        public Criteria andExecutorRouterStrategyNotBetween(Byte value1, Byte value2) {
            addCriterion("executor_router_strategy not between", value1, value2, "executorRouterStrategy");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}