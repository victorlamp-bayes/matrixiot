package com.victorlamp.matrixiot.service.agent.service;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.thing.Thing;
import com.victorlamp.matrixiot.service.management.entity.thingdata.ThingServiceData;
import lombok.extern.slf4j.Slf4j;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.agent.constant.ErrorCodeConstants.*;

@Slf4j
public abstract class Agent {

    /**
     * 按产品拉取设备列表
     *
     * @param product 产品
     */
    public abstract void pullThing(Product product);

    /**
     * 按产品拉取设备数据
     *
     * @param product 产品
     */
    public abstract void pullThingData(Product product);

    /**
     * 按设备拉取设备数据
     *
     * @param thing 设备
     */
    public abstract void pullThingData(Thing thing);

    /**
     * 接收设备上报数据
     *
     * @param originData 设备上报数据
     */
    public abstract void postThingData(String originData);

    /**
     * 注册设备
     *
     * @param product 产品
     * @param thing 设备
     */
    public abstract void registerDevice(Product product, Thing thing);

    /**
     * 指令下发
     *
     * @param product 产品
     * @param thing 设备
     */
    public abstract ThingServiceData sendCommand(Product product, Thing thing, ThingServiceData serviceData, String rawData);

    /**
     * 指令回调
     *
     * @param message 回调信息
     */
    public abstract void replyCommand(String message);

    protected void validatePayload(String payload) {
        if (StrUtil.isBlank(payload)) {
            log.warn(AGENT_DEVICE_POST_EMPTY_DATA.getMsg());
            throw exception(AGENT_DEVICE_POST_EMPTY_DATA);
        }

        if (!JSON.isValid(payload)) {
            log.error(AGENT_DEVICE_POST_ILLEGAL_DATA.getMsg());
            log.error(payload);
            throw exception(AGENT_DEVICE_POST_ILLEGAL_DATA);
        }
    }

    protected boolean validateProductExternalType(Product product, String agentType) {
        return validateProductExternalType(product, agentType, true);
    }

    protected boolean validateProductExternalType(Product product, String agentType, boolean ignoreException) {
        if (ObjUtil.isEmpty(product)) {
            log.error(THING_AGENT_PRODUCT_IS_NULL.getMsg());
            if (ignoreException) {
                return false;
            }
            throw exception(THING_AGENT_PRODUCT_IS_NULL);
        }

        if (!product.getPublished()) {
            log.error("{}[{}]", THING_AGENT_PRODUCT_NOT_PUBLISHED.getMsg(), product.getId());
            if (ignoreException) {
                return false;
            }
            throw exception(THING_AGENT_PRODUCT_NOT_PUBLISHED);
        }

        if (ObjUtil.isEmpty(product.getExternalConfig()) || StrUtil.isEmpty(product.getExternalConfig().getType()) || ObjUtil.isEmpty(product.getExternalConfig().getConfig())) {
            log.error("{}[{}]", THING_AGENT_PRODUCT_EXTERNAL_CONFIG_IS_NULL.getMsg(), product.getId());
            if (ignoreException) {
                return false;
            }
            throw exception(THING_AGENT_PRODUCT_EXTERNAL_CONFIG_IS_NULL);
        }

        String externalType = product.getExternalConfig().getType();
        if (!StrUtil.equalsIgnoreCase(externalType, agentType)) {
            log.error("{}[{}]. 需求类型:[{}],实际类型:[{}]", THING_AGENT_PRODUCT_EXTERNAL_CONFIG_TYPE_MISMATCH.getMsg(), product.getId(), agentType, externalType);
            if (ignoreException) {
                return false;
            }
            throw exception(THING_AGENT_PRODUCT_EXTERNAL_CONFIG_TYPE_MISMATCH);
        }

        return true;
    }

    protected boolean validateThingExternalType(Thing thing, String agentType) {
        return validateThingExternalType(thing, agentType, true);
    }

    protected boolean validateThingExternalType(Thing thing, String agentType, boolean ignoreException) {
        if (ObjUtil.isEmpty(thing)) {
            log.error(THING_AGENT_THING_IS_NULL.getMsg());
            if (ignoreException) {
                return false;
            }
            throw exception(THING_AGENT_THING_IS_NULL);
        }

        if (!thing.getEnabled()) {
            log.error("{}[{}]", THING_AGENT_THING_IS_DISABLED.getMsg(), thing.getId());
            if (ignoreException) {
                return false;
            }
            throw exception(THING_AGENT_THING_IS_DISABLED);

        }

        if (ObjUtil.isEmpty(thing.getExternalConfig())) {
            log.error("{}[{}]", THING_AGENT_THING_EXTERNAL_CONFIG_IS_NULL.getMsg(), thing.getId());
            if (ignoreException) {
                return false;
            }
            throw exception(THING_AGENT_THING_EXTERNAL_CONFIG_IS_NULL);

        }
        if (!StrUtil.equalsIgnoreCase(thing.getExternalConfig().getType(), agentType)) {
            log.error("{}[{}]. 需求类型:[{}],实际类型:[{}]", THING_AGENT_THING_EXTERNAL_CONFIG_TYPE_MISMATCH.getMsg(), thing.getId(), agentType, thing.getExternalConfig().getType());
            if (ignoreException) {
                return false;
            }
            throw exception(THING_AGENT_THING_EXTERNAL_CONFIG_TYPE_MISMATCH);
        }

        return true;
    }
}
