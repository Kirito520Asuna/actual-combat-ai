package com.actual_combat.aop.all.pdf;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.actual_combat.aop.enums.PDFType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author yan
 * @Date 2023/4/23 0023 16:52
 * @Description
 */
@Data @SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Slf4j
public class PDFFieldMap {
    /**
     * PDF对应名称
     */
    @Schema(description = "PDF对应名称")
    private String name;

    /**
     * java类字段映射名
     */
    @Schema(description = "java类字段映射名")
    private String fieldName;
    //类型
    @Schema(description = "类型")
    private PDFType type;
    //值
    @Schema(description = "值")
    private String fieldValue;
    //是时间类型
    @Schema(description = "是时间类型")
    private boolean isTime;
    //时间格式
    @Schema(description = "时间格式")
    private String datePattern;

    /**
     * 获取一个类里所有的@PDFField 的映射关系
     *
     * @param clazz
     * @return
     */
    public static List<PDFFieldMap> getAllFieldMaps(Class<?> clazz) {
        //Class<Demo> clazz = Demo.class; // 获取Demo类的Class对象
        Field[] fields = clazz.getDeclaredFields(); // 获取Demo类中所有定义的字段
        List<PDFFieldMap> pdfFieldMaps = new ArrayList<>(fields.length);
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(PDFField.class); // 获取当前字段上的PDFField注解
            if (annotation instanceof PDFField) { // 判断当前字段是否有PDFField注解
                PDFField pdfField = (PDFField) annotation; // 将注解强制转换为PDFField类型
                String[] values = pdfField.values();
                List<String> list = Stream.of(values).distinct().collect(Collectors.toList());
                int[] index = {0};
                list.stream().forEach(one -> {
                    String datePattern = DatePattern.NORM_DATETIME_PATTERN;
                    List<String> datePatterns = Arrays.stream(pdfField.datePatterns()).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(datePatterns)) {
                        String datePatternIndex = null;
                        try {
                            datePatternIndex = datePatterns.get(index[0]);
                        } catch (Exception e) {
                            //下标越界异常
                        }
                        datePattern = ObjectUtil.isNotEmpty(datePatternIndex) ? datePatternIndex : datePattern;
                    }
                    PDFFieldMap pdfFieldMap = PDFFieldMap.builder()
                            .name(one)
                            .type(pdfField.type())
                            .isTime(pdfField.isTime())
                            .datePattern(datePattern)
                            .fieldName(field.getName())
                            .build();
                    pdfFieldMaps.add(pdfFieldMap);
                    index[0]++;
                });
            }
        }
        return pdfFieldMaps;
    }

    public static List<PDFFieldMap> getAllForObject(List<PDFFieldMap> fieldMaps, Object o) {
        if (CollUtil.isEmpty(fieldMaps) || ObjectUtil.isEmpty(o)) {
            throw new RuntimeException("fieldMaps or object can not be empty!");
        }
        //====================================
        //时间格式map
        Map<String, String> formatMap = new LinkedHashMap<>();

        fieldMaps.stream().forEach(one -> {
            String fieldName = one.getFieldName();
            boolean isTime = one.isTime();
            String datePattern = one.getDatePattern();
            if (isTime) {
                formatMap.put(fieldName, datePattern);
            }
        });
        //log.info("formatMap:{}", formatMap);
        //====================================
        Map<String, Object> timeMap = new LinkedHashMap<>();
        //====================================
        //object 键值对
        BeanMap beanMap = BeanMap.create(o);
        Map<String, String> dataMap = new LinkedHashMap<>();
        for (Object key : beanMap.keySet()) {
            Object value = beanMap.get(key);
            String v = "";
            if (value instanceof Date || value instanceof LocalDateTime) {
                timeMap.put(key + "", value);
            } else {
                v = String.valueOf(value);
            }
            dataMap.put(key + "", v);
        }
        //log.info("dataMap:{}", dataMap);
        //====================================
        fieldMaps.stream().forEach(one -> {
            String fieldName = one.getFieldName();
            String value = "";
            if (one.isTime()) {
                String pattern = one.getDatePattern();
                Object time = timeMap.get(fieldName);
                if (time instanceof Date) {
                    value = DateUtil.format((Date) time, pattern);
                } else if (time instanceof LocalDateTime) {
                    value = DateUtil.format((LocalDateTime) time, pattern);
                }
            } else {
                value = dataMap.get(fieldName);
            }

            if (ObjectUtil.isNotEmpty(value)) {
                one.setFieldValue(value);
            }
            log.debug("fieldName:{},value:{},pdfFieldName:{}", fieldName, value, one.getName());
        });
        return fieldMaps;
    }

    public static List<PDFFieldMap> getAllForObject(Object o) {
        Class<?> aClass = o.getClass();
        List<PDFFieldMap> allFieldMaps = getAllFieldMaps(aClass);
        List<PDFFieldMap> pdfFieldMaps = getAllForObject(allFieldMaps, o);
        return pdfFieldMaps;
    }
}
