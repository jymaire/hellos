package org.lagonette.hellos.bean.helloasso;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HelloAssoOrderItem {
    private String name;
    private List<HelloAssoItemCustomField> customFields;

    public HelloAssoOrderItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HelloAssoItemCustomField> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<HelloAssoItemCustomField> customFields) {
        this.customFields = customFields;
    }

    @Override
    public String toString() {
        return "HelloAssoOrderItem{" +
                "name='" + name + '\'' +
                ", customFields=" + customFields +
                '}';
    }
}
