package org.lagonette.hellos.bean.helloasso;

public class HelloAssoItemCustomField {
    private String name;
    private CustomItemTypeEnum type;
    private String answer;

    public HelloAssoItemCustomField() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomItemTypeEnum getType() {
        return type;
    }

    public void setType(CustomItemTypeEnum type) {
        this.type = type;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}
