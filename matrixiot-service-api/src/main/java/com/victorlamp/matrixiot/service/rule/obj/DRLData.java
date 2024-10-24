package com.victorlamp.matrixiot.service.rule.obj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * DRL 字符串数据
 * @author: Dylan
 * @date: 2023/8/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DRLData {

    /**
     * drl 文件存储的绝对路径，含文件名，当没存储到磁盘中时为空
     */
    @Nullable
    private String filepath;

    /**
     * drl 文件的内容，字符串形式展示
     */
    private String content;

    /**
     * 构造函数,构造一个一样的数据
     * @param drlData
     * @return:
     * @author: Dylan-孙林
     * @Date: 2023/9/14
     */
    public DRLData(DRLData drlData) {

        this.filepath = new String(drlData.getFilepath());
        this.content = new String(drlData.getContent());
    }
}
