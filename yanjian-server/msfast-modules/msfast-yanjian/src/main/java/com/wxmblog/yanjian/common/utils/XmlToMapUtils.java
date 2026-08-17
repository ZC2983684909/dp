package com.wxmblog.yanjian.common.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlToMapUtils {
    public static Map<String, String> xmlToMap(String xmlStr) {
        Map<String, String> resultMap = new HashMap<>();
        Document document = null;
        try {
            document = DocumentHelper.parseText(xmlStr);
        } catch (DocumentException e) {
            return resultMap;
        }
        Element root = document.getRootElement();
        List<Element> elements = root.elements();

        for (Element element : elements) {
            resultMap.put(element.getName(), element.getText());
        }
        return resultMap;
    }
}

