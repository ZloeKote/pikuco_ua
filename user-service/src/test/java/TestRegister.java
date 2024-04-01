import com.pikuco.userservice.dto.RegisterRequest;
import com.pikuco.userservice.exception.ObjectNotValidException;
import com.pikuco.userservice.validator.ObjectsValidator;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Calendar;
import java.util.Date;

public class TestRegister {
    private final ObjectsValidator<RegisterRequest> validator = new ObjectsValidator<>();
    RegisterRequest request = new RegisterRequest("someUsername",
            "email@gmail.com",
            "password1",
            new Date(100, Calendar.JUNE, 13));

    @Test
    public void testRegisterValid() {
        // ✅ valid user data
        Assertions.assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    public void testRegisterUsername() {
        // ❌ username is empty
        request.setUsername("");
        Assertions.assertThrows(ObjectNotValidException.class, () -> validator.validate(request));
        // ❌ username isn't in character range 3-25
        request.setUsername("me");
        Assertions.assertThrows(ObjectNotValidException.class, () -> validator.validate(request));

        request.setUsername("pneumonoultramicroscopicsilicovolcanoconiosis");
        Assertions.assertThrows(ObjectNotValidException.class, () -> validator.validate(request));

        // ❌ username has special symbols
        request.setUsername("-ThE_BeS/t");
        Assertions.assertThrows(ObjectNotValidException.class, () -> validator.validate(request));
    }

    @Test
    public void testRegisterEmail() {
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
    public void testRegisterPassword() {
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

    @Test
    public void testRegisterBirthdate() {
        // ✅ birthdate is null
        request.setBirthdate(null);
        Assertions.assertDoesNotThrow(() -> validator.validate(request));

        // ❌ birthdate doesn't in date range 1900-01-01 - 2014-01-01
        // 1890-02-05
        request.setBirthdate(new Date(-10, Calendar.FEBRUARY, 5));
        Assertions.assertThrows(ObjectNotValidException.class, () ->
                validator.validate(request)
        );
        // 2050-09-17
        request.setBirthdate(new Date(150, Calendar.AUGUST, 17));
        Assertions.assertThrows(ObjectNotValidException.class, () ->
                validator.validate(request)
        );
    }
}
