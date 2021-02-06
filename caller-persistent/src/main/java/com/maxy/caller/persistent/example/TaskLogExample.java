package com.maxy.caller.persistent.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskLogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TaskLogExample() {
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

        public Criteria andExecuteParamIsNull() {
            addCriterion("execute_param is null");
            return (Criteria) this;
        }

        public Criteria andExecuteParamIsNotNull() {
            addCriterion("execute_param is not null");
            return (Criteria) this;
        }

        public Criteria andExecuteParamEqualTo(String value) {
            addCriterion("execute_param =", value, "executeParam");
            return (Criteria) this;
        }

        public Criteria andExecuteParamNotEqualTo(String value) {
            addCriterion("execute_param <>", value, "executeParam");
            return (Criteria) this;
        }

        public Criteria andExecuteParamGreaterThan(String value) {
            addCriterion("execute_param >", value, "executeParam");
            return (Criteria) this;
        }

        public Criteria andExecuteParamGreaterThanOrEqualTo(String value) {
            addCriterion("execute_param >=", value, "executeParam");
            return (Criteria) this;
        }

        public Criteria andExecuteParamLessThan(String value) {
            addCriterion("execute_param <", value, "executeParam");
            return (Criteria) this;
        }

        public Criteria andExecuteParamLessThanOrEqualTo(String value) {
            addCriterion("execute_param <=", value, "executeParam");
            return (Criteria) this;
        }

        public Criteria andExecuteParamLike(String value) {
            addCriterion("execute_param like", value, "executeParam");
            return (Criteria) this;
        }

        public Criteria andExecuteParamNotLike(String value) {
            addCriterion("execute_param not like", value, "executeParam");
            return (Criteria) this;
        }

        public Criteria andExecuteParamIn(List<String> values) {
            addCriterion("execute_param in", values, "executeParam");
            return (Criteria) this;
        }

        public Criteria andExecuteParamNotIn(List<String> values) {
            addCriterion("execute_param not in", values, "executeParam");
            return (Criteria) this;
        }

        public Criteria andExecuteParamBetween(String value1, String value2) {
            addCriterion("execute_param between", value1, value2, "executeParam");
            return (Criteria) this;
        }

        public Criteria andExecuteParamNotBetween(String value1, String value2) {
            addCriterion("execute_param not between", value1, value2, "executeParam");
            return (Criteria) this;
        }

        public Criteria andExecutorTimeIsNull() {
            addCriterion("executor_time is null");
            return (Criteria) this;
        }

        public Criteria andExecutorTimeIsNotNull() {
            addCriterion("executor_time is not null");
            return (Criteria) this;
        }

        public Criteria andExecutorTimeEqualTo(Date value) {
            addCriterion("executor_time =", value, "executorTime");
            return (Criteria) this;
        }

        public Criteria andExecutorTimeNotEqualTo(Date value) {
            addCriterion("executor_time <>", value, "executorTime");
            return (Criteria) this;
        }

        public Criteria andExecutorTimeGreaterThan(Date value) {
            addCriterion("executor_time >", value, "executorTime");
            return (Criteria) this;
        }

        public Criteria andExecutorTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("executor_time >=", value, "executorTime");
            return (Criteria) this;
        }

        public Criteria andExecutorTimeLessThan(Date value) {
            addCriterion("executor_time <", value, "executorTime");
            return (Criteria) this;
        }

        public Criteria andExecutorTimeLessThanOrEqualTo(Date value) {
            addCriterion("executor_time <=", value, "executorTime");
            return (Criteria) this;
        }

        public Criteria andExecutorTimeIn(List<Date> values) {
            addCriterion("executor_time in", values, "executorTime");
            return (Criteria) this;
        }

        public Criteria andExecutorTimeNotIn(List<Date> values) {
            addCriterion("executor_time not in", values, "executorTime");
            return (Criteria) this;
        }

        public Criteria andExecutorTimeBetween(Date value1, Date value2) {
            addCriterion("executor_time between", value1, value2, "executorTime");
            return (Criteria) this;
        }

        public Criteria andExecutorTimeNotBetween(Date value1, Date value2) {
            addCriterion("executor_time not between", value1, value2, "executorTime");
            return (Criteria) this;
        }

        public Criteria andExecutorAddressIsNull() {
            addCriterion("executor_address is null");
            return (Criteria) this;
        }

        public Criteria andExecutorAddressIsNotNull() {
            addCriterion("executor_address is not null");
            return (Criteria) this;
        }

        public Criteria andExecutorAddressEqualTo(String value) {
            addCriterion("executor_address =", value, "executorAddress");
            return (Criteria) this;
        }

        public Criteria andExecutorAddressNotEqualTo(String value) {
            addCriterion("executor_address <>", value, "executorAddress");
            return (Criteria) this;
        }

        public Criteria andExecutorAddressGreaterThan(String value) {
            addCriterion("executor_address >", value, "executorAddress");
            return (Criteria) this;
        }

        public Criteria andExecutorAddressGreaterThanOrEqualTo(String value) {
            addCriterion("executor_address >=", value, "executorAddress");
            return (Criteria) this;
        }

        public Criteria andExecutorAddressLessThan(String value) {
            addCriterion("executor_address <", value, "executorAddress");
            return (Criteria) this;
        }

        public Criteria andExecutorAddressLessThanOrEqualTo(String value) {
            addCriterion("executor_address <=", value, "executorAddress");
            return (Criteria) this;
        }

        public Criteria andExecutorAddressLike(String value) {
            addCriterion("executor_address like", value, "executorAddress");
            return (Criteria) this;
        }

        public Criteria andExecutorAddressNotLike(String value) {
            addCriterion("executor_address not like", value, "executorAddress");
            return (Criteria) this;
        }

        public Criteria andExecutorAddressIn(List<String> values) {
            addCriterion("executor_address in", values, "executorAddress");
            return (Criteria) this;
        }

        public Criteria andExecutorAddressNotIn(List<String> values) {
            addCriterion("executor_address not in", values, "executorAddress");
            return (Criteria) this;
        }

        public Criteria andExecutorAddressBetween(String value1, String value2) {
            addCriterion("executor_address between", value1, value2, "executorAddress");
            return (Criteria) this;
        }

        public Criteria andExecutorAddressNotBetween(String value1, String value2) {
            addCriterion("executor_address not between", value1, value2, "executorAddress");
            return (Criteria) this;
        }

        public Criteria andRetryCountIsNull() {
            addCriterion("retry_count is null");
            return (Criteria) this;
        }

        public Criteria andRetryCountIsNotNull() {
            addCriterion("retry_count is not null");
            return (Criteria) this;
        }

        public Criteria andRetryCountEqualTo(Byte value) {
            addCriterion("retry_count =", value, "retryCount");
            return (Criteria) this;
        }

        public Criteria andRetryCountNotEqualTo(Byte value) {
            addCriterion("retry_count <>", value, "retryCount");
            return (Criteria) this;
        }

        public Criteria andRetryCountGreaterThan(Byte value) {
            addCriterion("retry_count >", value, "retryCount");
            return (Criteria) this;
        }

        public Criteria andRetryCountGreaterThanOrEqualTo(Byte value) {
            addCriterion("retry_count >=", value, "retryCount");
            return (Criteria) this;
        }

        public Criteria andRetryCountLessThan(Byte value) {
            addCriterion("retry_count <", value, "retryCount");
            return (Criteria) this;
        }

        public Criteria andRetryCountLessThanOrEqualTo(Byte value) {
            addCriterion("retry_count <=", value, "retryCount");
            return (Criteria) this;
        }

        public Criteria andRetryCountIn(List<Byte> values) {
            addCriterion("retry_count in", values, "retryCount");
            return (Criteria) this;
        }

        public Criteria andRetryCountNotIn(List<Byte> values) {
            addCriterion("retry_count not in", values, "retryCount");
            return (Criteria) this;
        }

        public Criteria andRetryCountBetween(Byte value1, Byte value2) {
            addCriterion("retry_count between", value1, value2, "retryCount");
            return (Criteria) this;
        }

        public Criteria andRetryCountNotBetween(Byte value1, Byte value2) {
            addCriterion("retry_count not between", value1, value2, "retryCount");
            return (Criteria) this;
        }

        public Criteria andExecutorStatusIsNull() {
            addCriterion("executor_status is null");
            return (Criteria) this;
        }

        public Criteria andExecutorStatusIsNotNull() {
            addCriterion("executor_status is not null");
            return (Criteria) this;
        }

        public Criteria andExecutorStatusEqualTo(Byte value) {
            addCriterion("executor_status =", value, "executorStatus");
            return (Criteria) this;
        }

        public Criteria andExecutorStatusNotEqualTo(Byte value) {
            addCriterion("executor_status <>", value, "executorStatus");
            return (Criteria) this;
        }

        public Criteria andExecutorStatusGreaterThan(Byte value) {
            addCriterion("executor_status >", value, "executorStatus");
            return (Criteria) this;
        }

        public Criteria andExecutorStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("executor_status >=", value, "executorStatus");
            return (Criteria) this;
        }

        public Criteria andExecutorStatusLessThan(Byte value) {
            addCriterion("executor_status <", value, "executorStatus");
            return (Criteria) this;
        }

        public Criteria andExecutorStatusLessThanOrEqualTo(Byte value) {
            addCriterion("executor_status <=", value, "executorStatus");
            return (Criteria) this;
        }

        public Criteria andExecutorStatusIn(List<Byte> values) {
            addCriterion("executor_status in", values, "executorStatus");
            return (Criteria) this;
        }

        public Criteria andExecutorStatusNotIn(List<Byte> values) {
            addCriterion("executor_status not in", values, "executorStatus");
            return (Criteria) this;
        }

        public Criteria andExecutorStatusBetween(Byte value1, Byte value2) {
            addCriterion("executor_status between", value1, value2, "executorStatus");
            return (Criteria) this;
        }

        public Criteria andExecutorStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("executor_status not between", value1, value2, "executorStatus");
            return (Criteria) this;
        }

        public Criteria andExecutorResultMsgIsNull() {
            addCriterion("executor_result_msg is null");
            return (Criteria) this;
        }

        public Criteria andExecutorResultMsgIsNotNull() {
            addCriterion("executor_result_msg is not null");
            return (Criteria) this;
        }

        public Criteria andExecutorResultMsgEqualTo(String value) {
            addCriterion("executor_result_msg =", value, "executorResultMsg");
            return (Criteria) this;
        }

        public Criteria andExecutorResultMsgNotEqualTo(String value) {
            addCriterion("executor_result_msg <>", value, "executorResultMsg");
            return (Criteria) this;
        }

        public Criteria andExecutorResultMsgGreaterThan(String value) {
            addCriterion("executor_result_msg >", value, "executorResultMsg");
            return (Criteria) this;
        }

        public Criteria andExecutorResultMsgGreaterThanOrEqualTo(String value) {
            addCriterion("executor_result_msg >=", value, "executorResultMsg");
            return (Criteria) this;
        }

        public Criteria andExecutorResultMsgLessThan(String value) {
            addCriterion("executor_result_msg <", value, "executorResultMsg");
            return (Criteria) this;
        }

        public Criteria andExecutorResultMsgLessThanOrEqualTo(String value) {
            addCriterion("executor_result_msg <=", value, "executorResultMsg");
            return (Criteria) this;
        }

        public Criteria andExecutorResultMsgLike(String value) {
            addCriterion("executor_result_msg like", value, "executorResultMsg");
            return (Criteria) this;
        }

        public Criteria andExecutorResultMsgNotLike(String value) {
            addCriterion("executor_result_msg not like", value, "executorResultMsg");
            return (Criteria) this;
        }

        public Criteria andExecutorResultMsgIn(List<String> values) {
            addCriterion("executor_result_msg in", values, "executorResultMsg");
            return (Criteria) this;
        }

        public Criteria andExecutorResultMsgNotIn(List<String> values) {
            addCriterion("executor_result_msg not in", values, "executorResultMsg");
            return (Criteria) this;
        }

        public Criteria andExecutorResultMsgBetween(String value1, String value2) {
            addCriterion("executor_result_msg between", value1, value2, "executorResultMsg");
            return (Criteria) this;
        }

        public Criteria andExecutorResultMsgNotBetween(String value1, String value2) {
            addCriterion("executor_result_msg not between", value1, value2, "executorResultMsg");
            return (Criteria) this;
        }

        public Criteria andAlarmStatusIsNull() {
            addCriterion("alarm_status is null");
            return (Criteria) this;
        }

        public Criteria andAlarmStatusIsNotNull() {
            addCriterion("alarm_status is not null");
            return (Criteria) this;
        }

        public Criteria andAlarmStatusEqualTo(Byte value) {
            addCriterion("alarm_status =", value, "alarmStatus");
            return (Criteria) this;
        }

        public Criteria andAlarmStatusNotEqualTo(Byte value) {
            addCriterion("alarm_status <>", value, "alarmStatus");
            return (Criteria) this;
        }

        public Criteria andAlarmStatusGreaterThan(Byte value) {
            addCriterion("alarm_status >", value, "alarmStatus");
            return (Criteria) this;
        }

        public Criteria andAlarmStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("alarm_status >=", value, "alarmStatus");
            return (Criteria) this;
        }

        public Criteria andAlarmStatusLessThan(Byte value) {
            addCriterion("alarm_status <", value, "alarmStatus");
            return (Criteria) this;
        }

        public Criteria andAlarmStatusLessThanOrEqualTo(Byte value) {
            addCriterion("alarm_status <=", value, "alarmStatus");
            return (Criteria) this;
        }

        public Criteria andAlarmStatusIn(List<Byte> values) {
            addCriterion("alarm_status in", values, "alarmStatus");
            return (Criteria) this;
        }

        public Criteria andAlarmStatusNotIn(List<Byte> values) {
            addCriterion("alarm_status not in", values, "alarmStatus");
            return (Criteria) this;
        }

        public Criteria andAlarmStatusBetween(Byte value1, Byte value2) {
            addCriterion("alarm_status between", value1, value2, "alarmStatus");
            return (Criteria) this;
        }

        public Criteria andAlarmStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("alarm_status not between", value1, value2, "alarmStatus");
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