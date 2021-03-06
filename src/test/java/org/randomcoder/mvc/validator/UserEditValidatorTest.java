package org.randomcoder.mvc.validator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.db.Role;
import org.randomcoder.db.User;
import org.randomcoder.mvc.command.UserEditCommand;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserEditValidatorTest {
  private UserEditValidator validator;

  @Before public void setUp() {
    validator = new UserEditValidator();
    validator.setMinimumPasswordLength(6);
    validator.setMinimumUsernameLength(6);
  }

  @After public void tearDown() {
    validator = null;
  }

  @Test public void testSupports() {
    assertTrue("Validator doesn't support command class",
        validator.supports(UserEditCommand.class));
  }

  @Test public void testValidate() {
    FieldError error;
    BindException errors;

    // setup
    UserEditCommand command = new UserEditCommand();

    User user = new User();
    user.setUserName("existing-user");
    user.setEmailAddress("existing@example.com");
    user.setPassword(User.hashPassword("Password1"));
    user.setRoles(new ArrayList<Role>());
    user.setEnabled(true);
    user.setId(1L);

    command.setId(1L);
    command.consume(user);

    // no command
    errors = new BindException(command, "command");
    validator.validate(null, errors);
    assertEquals("Wrong number of errors occurred", 1, errors.getErrorCount());

    // no data supplied
    command.setId(null);
    command.setEmailAddress(null);
    command.setPassword(null);
    command.setPassword2(null);
    command.setRoles(new Role[] {});
    errors = new BindException(command, "command");

    validator.validate(command, errors);

    assertEquals("Wrong number of errors occurred", 2, errors.getErrorCount());
    assertEquals("Wrong error count for emailAddress", 1,
        errors.getFieldErrorCount("emailAddress"));

    command.setId(1L);

    // email address invalid
    command.setEmailAddress("bogus email address");
    errors = new BindException(command, "command");

    validator.validate(command, errors);
    assertEquals("Wrong error count for emailAddress", 1,
        errors.getFieldErrorCount("emailAddress"));
    error = errors.getFieldErrors("emailAddress").get(0);
    assertEquals("Wrong error code", "error.user.emailaddress.invalid",
        error.getCode());

    // email address valid
    command.setEmailAddress("valid@example.com");
    errors = new BindException(command, "command");
    validator.validate(command, errors);
    assertEquals("Wrong error count for emailAddress", 0,
        errors.getFieldErrorCount("emailAddress"));

    // password too short
    command.setPassword("short");
    errors = new BindException(command, "command");
    validator.validate(command, errors);
    assertEquals("Wrong error count for password", 1,
        errors.getFieldErrorCount("password"));
    error = errors.getFieldErrors("password").get(0);
    assertEquals("Wrong error code", "error.user.password.tooshort",
        error.getCode());

    // password valid
    command.setPassword("Password1");
    errors = new BindException(command, "command");
    validator.validate(command, errors);
    assertEquals("Wrong error count for password", 0,
        errors.getFieldErrorCount("password"));

    // password 2 doesn't match
    command.setPassword2("Password2");
    errors = new BindException(command, "command");
    validator.validate(command, errors);
    assertEquals("Wrong error count for password2", 1,
        errors.getFieldErrorCount("password2"));
    error = errors.getFieldErrors("password2").get(0);
    assertEquals("Wrong error code", "error.user.password.nomatch",
        error.getCode());

    // all data valid
    command.setPassword("Password2");
    errors = new BindException(command, "command");
    validator.validate(command, errors);
    assertEquals("Errors occurred", 0, errors.getErrorCount());
  }
}