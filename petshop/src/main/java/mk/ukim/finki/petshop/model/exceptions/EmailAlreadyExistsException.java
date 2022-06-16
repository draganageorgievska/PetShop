package mk.ukim.finki.petshop.model.exceptions;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String email) {
        super(String.format("User with email: %s already exists", email));
    }

}
