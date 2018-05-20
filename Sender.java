
/**
 *
 * @author a80052136
 */
public class Sender implements Comparable{
    private String username;
    private String email;
    private String password;
    SenderDA da = new SenderDA();
    

    public Sender(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        
    }

    public void setUsername(String username) {
        this.username = username;
    }

    
    public void setEmail(String email) {
        this.email = email;
        
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
    
    @Override
    public String toString() {
        return "Sender{" + "email=" + email + ", password=" + password + '}';
    }

    @Override
    public int compareTo(Object t) {
        return this.getUsername().compareTo(((Sender) t).getUsername());
    }
    
    public boolean saveSender() {
        boolean save = da.save(this);
        return save;
    }
    
}
