package com.maxy.caller.client.executor;

import com.google.common.base.Preconditions;
import com.maxy.caller.client.executor.annotation.Registered;
import com.maxy.caller.core.config.DispatchCenter;
import com.maxy.caller.pojo.MethodModel;
import com.maxy.caller.pojo.RegConfigInfo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * @Author maxy
 **/
@Log4j2
public class RegisteredFlowProcessor {

    public static void process(ApplicationContext applicationContext) {
        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        if (ArrayUtils.isEmpty(beanDefinitionNames)) {
            log.warn("process#beanDefinitionNames里没有找到任何元素.");
            return;
        }
        RegConfigInfo regConfigInfo = applicationContext.getBean(RegConfigInfo.class);
        Preconditions.checkArgument(regConfigInfo != null);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            Map<Method, Registered> annotationMethods = getAnnotationMethods(beanDefinitionName, bean);
            if (MapUtils.isEmpty(annotationMethods)) {
                continue;
            }
            for (Map.Entry<Method, Registered> element : annotationMethods.entrySet()) {
                Registered reg = element.getValue();
                if (Objects.isNull(reg)) {
                    continue;
                }
                MethodModel.MethodModelBuilder methodModelBuilder = MethodModel.builder();
                //赋值
                methodModelBuilder.method(element.getKey());
                methodModelBuilder.target(bean);
                String topic = StringUtils.trim(reg.topic());
                Preconditions.checkArgument(StringUtils.isNotBlank(topic), "用户定义Topic为空.beanDefinitionName:" + beanDefinitionName);
                String uniqueName = join(topic, regConfigInfo.getGroupKey(), regConfigInfo.getBizKey());
                log.info("process#uniqueName:{}", uniqueName);
                Preconditions.checkArgument(Objects.isNull(DispatchCenter.get(uniqueName)), "用户定义" + uniqueName + "已经注册存在冲突.beanDefinitionName:" + beanDefinitionName);
                //注册
                DispatchCenter.put(uniqueName, methodModelBuilder.build());
            }
        }
    }

    public static String join(String topic, String groupKey, String bizKey) {
        return String.join(":", groupKey, bizKey, topic);
    }

    private static Map<Method, Registered> getAnnotationMethods(String beanDefinitionName, Object bean) {
        try {
            return MethodIntrospector.selectMethods(bean.getClass(),
                    new MethodIntrospector.MetadataLookup<Registered>() {
                        @Override
                        public Registered inspect(Method method) {
                            return AnnotatedElementUtils.findMergedAnnotation(method, Registered.class);
                        }
                    });
        } catch (Exception e) {
            log.error("getAnnotationMethods#获取注解方法时报错! bean[" + beanDefinitionName + "].", e);
            return null;
        }
    }
}
