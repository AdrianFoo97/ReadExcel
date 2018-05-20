/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author a80052136
 */
public class Subcontractor implements Comparable{
    protected String name;
    protected String email;

    public Subcontractor(String name, String email) {
        this.name = name.toUpperCase();
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Subcontractor{" + "name=" + name + ", email=" + email + '}';
    }

    @Override
    public int compareTo(Object t) {
        return this.getName().compareTo(((Subcontractor) t).getName());
    }
    
    
}
