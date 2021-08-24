package app.kenavo.billapplication.model;

import static java.lang.Float.parseFloat;

public class Account {

    @Override public String toString() { return getName(); }

    public String id;

    public String name;

    public String address;

    public String Contact;

    public String email;

    public String phone;

    public float ca;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public float getCa() {
        return ca;
    }

    public void setCa(String ca) {
        if(ca != null) {
            this.ca = parseFloat(ca);
        } else {
            this.ca = 0.0F;
        }

    }
}
