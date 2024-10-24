package com.victorlamp.matrixiot.service.common.util;

import com.victorlamp.matrixiot.service.common.enums.ResourceType;

/**
 * 资源编号生成器
 * 格式：
 * rn:<tenantId>:<region>:<resourceType>/<resourceId>
 *
 * @author: Dylan
 * @date: 2023/8/23
 */
public class RnGenerator {

    /**
     * 获得 rn
     *
     * @param tenantId     租户ID
     * @param resourceType 资源类型
     * @param resourceId   资源ID
     * @return: java.lang.String
     * @author: Dylan-孙林
     * @Date: 2023/10/18
     */
    public static String get(String tenantId, ResourceType resourceType, String resourceId) {

        return get(tenantId, resourceType, null, resourceId);
    }

    /**
     * 获得 rn
     *
     * @param tenantId     租户ID
     * @param resourceType 资源类型
     * @param region       资源所在的区域
     * @param resourceId   资源ID
     * @return: java.lang.String
     * @author: Dylan-孙林
     * @Date: 2023/10/18
     */
    public static String get(String tenantId, ResourceType resourceType, String region, String resourceId) {

        if (tenantId == null || tenantId.trim().length() == 0) {

            throw new RuntimeException("tenantId 不能为空！");
        }

        if (resourceType == null) {

            throw new RuntimeException("resourceType 不能为空！");
        }

        if (resourceId == null || resourceId.trim().length() == 0) {

            throw new RuntimeException("resourceId 不能为空！");
        }

        String regionTemp = region == null || region.trim().length() == 0 ? "" : region.trim();

        return String.format("rn:%s:%s:%s", tenantId.trim(), regionTemp, resourceType.getValue() + "/" + resourceId.trim());
    }
}
