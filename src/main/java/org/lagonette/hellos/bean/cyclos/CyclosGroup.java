package org.lagonette.hellos.bean.cyclos;

public class CyclosGroup {
    private String id;
    private String name;
    private String internalName;

    public CyclosGroup() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    @Override
    public String toString() {
        return "CyclosGroup{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", internalName='" + internalName + '\'' +
                '}';
    }
}
