package com.victorlamp.matrixiot.service.route.utils.script;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.script.ScriptUtil;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.victorlamp.matrixiot.service.route.DataRouteService;
import com.victorlamp.matrixiot.service.route.entity.DataRoute;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.time.Duration;
import java.util.concurrent.Executors;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.route.constant.ErrorCodeConstants.*;
import static com.victorlamp.matrixiot.service.route.enums.DataRouteTransformerTypeEnum.JS;

@Component
@Slf4j
public class DataRouteScriptExecutor {
    final static String TRANSFORM = "transform";
    private static DataRouteService dataRouteService;

    private static final LoadingCache<String, ScriptEngine> CACHE = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(5L))
            .maximumSize(10)
            .executor(Executors.newCachedThreadPool())
            .build(new CacheLoader<>() {
                @Override
                public @Nullable ScriptEngine load(String key) {
                    if (StrUtil.isBlank(key)) {
                        log.error("物模型脚本或数据路由脚本ID不能为空");
                        return null;
                    }

                    return loadScript(key);
                }
            });

    public static String transform(String dataRouteId, String data) {
        Invocable scriptEngine = (Invocable) CACHE.get(dataRouteId);

        if (scriptEngine == null) {
            log.error("{}[{}]", DATA_ROUTE_SCRIPT_NOT_EXISTS.getMsg(), dataRouteId);
            throw exception(DATA_ROUTE_SCRIPT_NOT_EXISTS);
        }

        try {
            return (String) scriptEngine.invokeFunction(TRANSFORM, data);
        } catch (ScriptException e) {
            log.error("{}[{}]", DATA_ROUTE_TRANSFORM_FAILED.getMsg(), dataRouteId);
            throw exception(DATA_ROUTE_TRANSFORM_FAILED);
        } catch (NoSuchMethodException e) {
            log.error("{}[{}]", DATA_ROUTE_TRANSFORM_NOT_EXISTS.getMsg(), dataRouteId);
            throw exception(DATA_ROUTE_TRANSFORM_NOT_EXISTS);
        }
    }

    private static synchronized ScriptEngine loadScript(String dataRouteId) {
        // 静态方法内：手工获取远程服务实例，避免启动时Autowired注入导致启动失败
        if (dataRouteService == null) {
            dataRouteService = SpringUtil.getBean(DataRouteService.class);
        }

        DataRoute dataRoute = dataRouteService.getDataRoute(dataRouteId);
        if (ObjUtil.isEmpty(dataRoute) || ObjUtil.isEmpty(dataRoute.getTransformer())) {
            log.warn("数据路由脚本为空[{}]", dataRouteId);
            return null;
        }

        if (dataRoute.getTransformer().getType() != JS) {
            log.error("无效的脚本类型[{}]", dataRoute.getTransformer().getType());
            return null;
        }

        ScriptEngine scriptEngine = ScriptUtil.createScript("graal.js");
        try {
            scriptEngine.eval(dataRoute.getTransformer().getContent());
        } catch (ScriptException e) {
            log.error("{}[{}]", ILLEGAL_DATA_ROUTE_SCRIPT.getMsg(), dataRouteId);
            throw exception(ILLEGAL_DATA_ROUTE_SCRIPT);
        }

        return scriptEngine;
    }
}
