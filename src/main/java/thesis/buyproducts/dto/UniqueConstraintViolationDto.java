package thesis.buyproducts.dto;


public class UniqueConstraintViolationDto {

    private String constraintName;

    private String message;

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
