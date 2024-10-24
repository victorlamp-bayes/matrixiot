package com.victorlamp.matrixiot.service.agent.utils.script;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.script.ScriptUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.victorlamp.matrixiot.service.management.api.ThingModelService;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.ThingModelScript;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.time.Duration;
import java.util.concurrent.Executors;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.agent.constant.ErrorCodeConstants.*;

@Component
@Slf4j
public class ThingModelScriptExecutor {

    private final static String RAW_DATA_TO_PROTOCOL = "rawDataToProtocol";
    private final static String PROTOCOL_TO_RAW_DATA = "protocolToRawData";

    @DubboReference
    private static ThingModelService thingModelService;

    private static final LoadingCache<String, ScriptEngine> CACHE = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(5L))
            .maximumSize(10)
            .executor(Executors.newCachedThreadPool())
            .build(new CacheLoader<>() {
                @Override
                public @Nullable ScriptEngine load(String key) {
                    if (StrUtil.isBlank(key)) {
                        log.error("产品Id不能为空");
                        return null;
                    }

                    return loadScript(key);
                }
            });

    /**
     * 脚本执行器是否存在，建议先调用该函数进行前置判断，直接调用rawDataToProtocol或protocolToRawData可能会抛出异常
     *
     * @param productId 产品Id
     * @return 脚本执行器是否存在
     */
    public static boolean existsScriptExecutor(String productId) {
        return CACHE.get(productId) != null;
    }

    public static String rawDataToProtocol(String productId, String rawData) {
        Invocable scriptEngine = (Invocable) CACHE.get(productId);
        if (scriptEngine == null) {
            throw exception(THING_AGENT_THING_MODEL_SCRIPT_NOT_EXISTS);
        }

        try {
            String protocolData = (String) scriptEngine.invokeFunction(RAW_DATA_TO_PROTOCOL, rawData);

            if (StrUtil.isBlankOrUndefined(protocolData)) {
                return null;
            }

            // 转换脚本报错信息
            JSONObject protocolDataJson = JSON.parseObject(protocolData);
            if (protocolDataJson.keySet().size() == 1 && protocolDataJson.containsKey("message")) {
                log.error("转换报错：{}", protocolDataJson.getString("message"));
                return null;
            }

            return protocolData;
        } catch (ScriptException e) {
            log.error("{}[{}], rawData={}", THING_AGENT_RAW_DATA_TO_PROTOCOL_FAILED.getMsg(), productId, rawData);
            throw exception(THING_AGENT_RAW_DATA_TO_PROTOCOL_FAILED);
        } catch (NoSuchMethodException e) {
            log.error("{}[{}]", THING_AGENT_RAW_DATA_TO_PROTOCOL_NOT_EXISTS.getMsg(), productId);
            throw exception(THING_AGENT_RAW_DATA_TO_PROTOCOL_NOT_EXISTS);
        }
    }

    public static String protocolToRawData(String productId, String jsonStr) {
        Invocable scriptEngine = (Invocable) CACHE.get(productId);
        if (scriptEngine == null) {
            throw exception(THING_AGENT_THING_MODEL_SCRIPT_NOT_EXISTS);
        }
        try {
            return (String) scriptEngine.invokeFunction(PROTOCOL_TO_RAW_DATA, jsonStr);
        } catch (ScriptException e) {
            log.error("{}[{}], jsonStr={}", THING_AGENT_PROTOCOL_TO_RAW_DATA_FAILED.getMsg(), productId, jsonStr);
            throw exception(THING_AGENT_PROTOCOL_TO_RAW_DATA_FAILED);
        } catch (NoSuchMethodException e) {
            log.error("{}[{}]", THING_AGENT_PROTOCOL_TO_RAW_DATA_NOT_EXISTS.getMsg(), productId);
            throw exception(THING_AGENT_PROTOCOL_TO_RAW_DATA_NOT_EXISTS);
        }
    }

    private static synchronized ScriptEngine loadScript(String productId) {
        // 静态方法内：手工获取远程服务实例，避免启动时Autowired注入导致启动失败
        if (thingModelService == null) {
            thingModelService = SpringUtil.getBean(ThingModelService.class);
        }
        ThingModelScript thingModelScript = thingModelService.describeThingModelScript(productId);
        if (ObjUtil.isEmpty(thingModelScript)) {
            log.info("{}[{}]", THING_AGENT_THING_MODEL_SCRIPT_NOT_EXISTS.getMsg(), productId);
            return null;
        }

        switch (thingModelScript.getType()) {
            case JS -> {
                ScriptEngine scriptEngine = ScriptUtil.createScript("graal.js");
                try {
                    scriptEngine.eval(thingModelScript.getContent());
                } catch (ScriptException e) {
                    log.error("{}[{}]", THING_AGENT_ILLEGAL_THING_MODEL_SCRIPT.getMsg(), productId);
                    throw exception(THING_AGENT_ILLEGAL_THING_MODEL_SCRIPT);
                }

                return scriptEngine;
            }
            case PRESET -> {
                ScriptEngine scriptEngine = ScriptUtil.createGroovyEngine();

                // TODO groovy脚本仅作为预置脚本，当前保存的是文件名，存放在classpath，后续需优化
                String scriptContent;
                try {
                    scriptContent = ResourceUtil.readUtf8Str("groovy/" + thingModelScript.getContent() + ".groovy");
                } catch (Exception e) {
                    log.error("{}[{}]", THING_AGENT_GET_PRESET_SCRIPT_FILE_FAILED.getMsg(), thingModelScript.getContent());
                    throw exception(THING_AGENT_GET_PRESET_SCRIPT_FILE_FAILED);
                }

                if (StrUtil.isBlank(scriptContent)) {
                    log.error("{}[{}]", THING_AGENT_GET_PRESET_SCRIPT_IS_BLANK.getMsg(), thingModelScript.getContent());
                    throw exception(THING_AGENT_GET_PRESET_SCRIPT_IS_BLANK);
                }

                try {
                    scriptEngine.eval(scriptContent);
                    scriptEngine.put("convertUtils", new ConvertUtils());  // 现有Groovy脚本中会调用Java工具类
                } catch (ScriptException e) {
                    log.error("{}[{}]", THING_AGENT_ILLEGAL_THING_MODEL_SCRIPT.getMsg(), productId);
                    throw exception(THING_AGENT_ILLEGAL_THING_MODEL_SCRIPT);
                }

                return scriptEngine;
            }
            default -> {
                log.error("无效的脚本类型[{}]", thingModelScript.getType());
                return null;
            }
        }
    }
}
