package com.victorlamp.matrixiot.common.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * Key Value 的键值对
 *
 * @author 芋道源码
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class KeyValue<K, V> implements Serializable {
    @Serial
    private static final long serialVersionUID = -1008078599558911991L;
    
    private K key;
    private V value;

}
