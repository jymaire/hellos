package org.lagonette.hellos.bean.cyclos;

public class CyclosUser {

    private String id;
    private String name;
    private String username;
    private String email;
    private CyclosGroup group;

    public CyclosUser() {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CyclosGroup getGroup() {
        return group;
    }

    public void setGroup(CyclosGroup group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "CyclosUser{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
