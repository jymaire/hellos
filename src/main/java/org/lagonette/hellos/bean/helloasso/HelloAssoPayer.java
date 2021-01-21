package org.lagonette.hellos.bean.helloasso;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HelloAssoPayer {

    private String email;
    private String address;
    private String city;
    private String zipCode;
    private String country;
    private String firstName;
    private String lastName;

    public HelloAssoPayer() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static final class HelloAssoPayerBuilder {
        private String email;
        private String address;
        private String city;
        private String zipCode;
        private String country;
        private String firstName;
        private String lastName;

        private HelloAssoPayerBuilder() {
        }

        public static HelloAssoPayerBuilder aHelloAssoPayer() {
            return new HelloAssoPayerBuilder();
        }

        public HelloAssoPayerBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public HelloAssoPayerBuilder withAddress(String address) {
            this.address = address;
            return this;
        }

        public HelloAssoPayerBuilder withCity(String city) {
            this.city = city;
            return this;
        }

        public HelloAssoPayerBuilder withZipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public HelloAssoPayerBuilder withCountry(String country) {
            this.country = country;
            return this;
        }

        public HelloAssoPayerBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public HelloAssoPayerBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public HelloAssoPayer build() {
            HelloAssoPayer helloAssoPayer = new HelloAssoPayer();
            helloAssoPayer.setEmail(email);
            helloAssoPayer.setAddress(address);
            helloAssoPayer.setCity(city);
            helloAssoPayer.setZipCode(zipCode);
            helloAssoPayer.setCountry(country);
            helloAssoPayer.setFirstName(firstName);
            helloAssoPayer.setLastName(lastName);
            return helloAssoPayer;
        }
    }
}
