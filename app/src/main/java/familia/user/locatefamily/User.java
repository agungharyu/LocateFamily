package familia.user.locatefamily;

/**
 * Created by User on 1/13/2018.
 */

public class User {
    private String email,status;

    public User(String email, String status) {
        this.email = email;
        this.status = status;
    }

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
