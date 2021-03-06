package thesis.buyproducts.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CustomerDto {

    private Long id;

    @NotNull(message = "First name cannot be null!")
    @Size(min = 2, max = 32, message = "The first name must be between 2 and 32 characters")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @Size(min = 2, max = 32, message = "The last name must be between 2 and 32 characters")
    private String lastName;

    @NotNull(message = "User name cannot be null!")
    @Size(min = 2, max = 32, message = "The user name must be between 2 and 32 characters")
    private String userName;

    private Double points;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }
}
