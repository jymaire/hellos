package org.lagonette.hellos.bean;

public class Notification {

    private int id;
    private String name;
    private String firstName;
    private String email;
    private String formSlug;
    private String date;
    private int amount;

    public Notification() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFormSlug() {
        return formSlug;
    }

    public void setFormSlug(String formSlug) {
        this.formSlug = formSlug;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


    public static final class NotificationBuilder {
        private int id;
        private String name;
        private String firstName;
        private String email;
        private String formSlug;
        private String date;
        private int amount;

        private NotificationBuilder() {
        }

        public static NotificationBuilder aNotification() {
            return new NotificationBuilder();
        }

        public NotificationBuilder withId(int id) {
            this.id = id;
            return this;
        }

        public NotificationBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public NotificationBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public NotificationBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public NotificationBuilder withFormSlug(String formSlug) {
            this.formSlug = formSlug;
            return this;
        }

        public NotificationBuilder withDate(String date) {
            this.date = date;
            return this;
        }

        public NotificationBuilder withAmount(int amount) {
            this.amount = amount;
            return this;
        }

        public Notification build() {
            Notification notification = new Notification();
            notification.setId(id);
            notification.setName(name);
            notification.setFirstName(firstName);
            notification.setEmail(email);
            notification.setFormSlug(formSlug);
            notification.setDate(date);
            notification.setAmount(amount);
            return notification;
        }
    }
}
