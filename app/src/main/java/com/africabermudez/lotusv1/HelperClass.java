package com.africabermudez.lotusv1;

public class HelperClass {

    /**
     * Clase que proporciona métodos y propiedades para administrar información relacionada con un usuario.
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */

    String UID, name, email, username, admin_code;

    public HelperClass() {
    }

    public HelperClass(String UID, String name, String email, String username) {
        this.UID = UID;
        this.name = name;
        this.email = email;
        this.username = username;
    }

    public HelperClass(String UID, String name, String email, String username,String admin_code) {
        this.UID = UID;
        this.name = name;
        this.email = email;
        this.username = username;
        this.admin_code = admin_code;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAdmin_code() {
        return admin_code;
    }

    public void setAdmin_code(String admin_code) {
        this.admin_code = admin_code;
    }
}
