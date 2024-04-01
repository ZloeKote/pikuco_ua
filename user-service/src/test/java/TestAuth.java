import com.pikuco.userservice.dto.AuthenticationRequest;
import com.pikuco.userservice.exception.ObjectNotValidException;
import com.pikuco.userservice.validator.ObjectsValidator;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class TestAuth {
    private final ObjectsValidator<AuthenticationRequest> validator = new ObjectsValidator<>();
    AuthenticationRequest request = new AuthenticationRequest("email@nure.ua", "password1");

    @Test
    public void TestAuthEmail() {
        // ❌ email is empty
        request.setEmail("");
        Assertions.assertThrows(ObjectNotValidException.class, () ->
                validator.validate(request)
        );

        // ❌ email doesn't match the pattern <address>@<domain>.<TLD>
        request.setEmail("email.com");
        Assertions.assertThrows(ObjectNotValidException.class, () ->
                validator.validate(request)
        );
        // ❌ <address>@<domain>
        request.setEmail("email@com");
        Assertions.assertThrows(ObjectNotValidException.class, () ->
                validator.validate(request)
        );
        // ❌ @<domain>.<LTD>
        request.setEmail("@nure.ua");
        Assertions.assertThrows(ObjectNotValidException.class, () ->
                validator.validate(request)
        );
        // ❌ <address>@<domain>..
        request.setEmail("email@nure..");
        Assertions.assertThrows(ObjectNotValidException.class, () ->
                validator.validate(request)
        );
    }

    @Test
    public void testAuthPassword() {
        // ❌ password is empty
        request.setPassword("");
        Assertions.assertThrows(ObjectNotValidException.class, () ->
                validator.validate(request)
        );

        // ❌ password doesn't have either letter and number
        request.setPassword("myPassword");
        Assertions.assertThrows(ObjectNotValidException.class, () ->
                validator.validate(request)
        );

        request.setPassword("123456789");
        Assertions.assertThrows(ObjectNotValidException.class, () ->
                validator.validate(request)
        );

        // ❌ password isn't in character range 6-20
        request.setPassword("psw");
        Assertions.assertThrows(ObjectNotValidException.class, () ->
                validator.validate(request)
        );

        request.setPassword("longpasswordforbettersecurity");
        Assertions.assertThrows(ObjectNotValidException.class, () ->
                validator.validate(request)
        );
    }
}
