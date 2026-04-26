package Domain;
import java.util.Objects;

public class User extends Entity<Long> {
    private String username;
    private String password;
    private String fullName;
    private Long officeId;

    public User(Long id, String username, String password, String fullName, Long officeId) {
        super(id);
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.officeId = officeId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public Long getOfficeId() {
        return officeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(fullName, user.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, password, fullName);
    }


}