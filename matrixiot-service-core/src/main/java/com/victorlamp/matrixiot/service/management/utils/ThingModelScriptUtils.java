package com.victorlamp.matrixiot.service.management.utils;

import com.victorlamp.matrixiot.service.common.exception.ExceptionInfoBuilder;
import com.victorlamp.matrixiot.service.common.exception.ExceptionTemplate;
import com.victorlamp.matrixiot.service.common.exception.ServiceException;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.PresetScript;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThingModelScriptUtils {
    private final static String GROOVY_PATH = "groovy/";
    private final static String GROOVY_FILE_EXTENSION = ".groovy";
    private final static ResourceLoader resourceLoader = new DefaultResourceLoader();

    public static List<PresetScript> readGroovyDirectory() {
        List<PresetScript> groovyFileDTOList = new ArrayList<>();

        Resource[] resources;
        try {
            // 读取目录文件列表
            List<String> fileNames = new ArrayList<>();
            resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources("classpath:" + GROOVY_PATH + "*.groovy");
            for (Resource resource : resources) {
                fileNames.add(resource.getFilename());
            }
            log.info("Find {} Groovy files in classpath.", fileNames.size());

            int idCounter = 0;
            for (String fileName : fileNames) {
                PresetScript groovyFileDTO = new PresetScript(++idCounter, fileName.split(GROOVY_FILE_EXTENSION)[0]);
                groovyFileDTOList.add(groovyFileDTO);
            }
        } catch (Exception e) {
            log.error("加载groovy脚本目录失败：{}", e.getMessage());
            throw new ServiceException(
                    ServiceException.ExceptionType.INTERNAL_FAILURE,
                    ExceptionInfoBuilder.build(ExceptionTemplate.INTERNAL_FAILURE_OTHER, "加载groovy脚本目录失败"));
        }

        return groovyFileDTOList;
    }
}
