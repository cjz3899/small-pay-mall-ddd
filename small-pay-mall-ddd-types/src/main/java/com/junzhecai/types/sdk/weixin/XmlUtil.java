package com.junzhecai.types.sdk.weixin;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class XmlUtil {

    /**
     * 解析微信发来的请求(xml)
     */
    public static Map<String, String> xmlToMap(HttpServletRequest request) throws Exception {
        try (InputStream inputStream = request.getInputStream()) {
            return xmlToMap(inputStream);
        }
    }

    public static Map<String, String> xmlToMap(String xml) throws Exception {
        return xmlToMap(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

    public static Map<String, String> xmlToMap(InputStream inputStream) throws Exception {
        SAXReader reader = new SAXReader();
        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        Document document = reader.read(inputStream);
        Element root = document.getRootElement();
        List<Element> elementList = root.elements();
        Map<String, String> map = new LinkedHashMap<>();
        for (Element e : elementList) {
            map.put(e.getName(), e.getText());
        }
        return map;
    }

    /**
     * 将map转化成xml响应给微信服务器
     */
    public static String mapToXML(Map<String, ?> map) {
        StringBuilder sb = new StringBuilder("<xml>");
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            sb.append('<').append(entry.getKey()).append('>');
            appendValue(sb, entry.getValue());
            sb.append("</").append(entry.getKey()).append('>');
        }
        sb.append("</xml>");
        return sb.toString();
    }

    private static void appendValue(StringBuilder sb, Object value) {
        String text = value == null ? "" : String.valueOf(value);
        if (value instanceof Number || StringUtils.isNumeric(text)) {
            sb.append(text);
        } else {
            sb.append("<![CDATA[").append(text.replace("]]>", "]]]]><![CDATA[>")).append("]]>");
        }
    }

    /**
     * bean转成微信的xml消息格式
     */
    public static String beanToXml(Object object) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                continue;
            }
            try {
                field.setAccessible(true);
                Object value = field.get(object);
                if (value != null) {
                    map.put(toXmlTag(field.getName()), value);
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("读取字段失败: " + field.getName(), e);
            }
        }
        return mapToXML(map);
    }

    /**
     * xml转成bean泛型方法
     */
    public static <T> T xmlToBean(String resultXml, Class<T> clazz) throws Exception {
        Map<String, String> map = xmlToMap(resultXml);
        T bean = clazz.getDeclaredConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                continue;
            }
            String value = map.get(toXmlTag(field.getName()));
            if (value != null) {
                field.setAccessible(true);
                field.set(bean, value);
            }
        }
        return bean;
    }

    private static String toXmlTag(String fieldName) {
        return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

}
