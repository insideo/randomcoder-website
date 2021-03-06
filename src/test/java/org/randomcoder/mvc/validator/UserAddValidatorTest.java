package org.randomcoder.mvc.validator;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.Role;
import org.randomcoder.db.User;
import org.randomcoder.mvc.command.UserAddCommand;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.ArrayList;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserAddValidatorTest {
  private UserAddValidator validator;
  private IMocksControl control;
  private UserBusiness ub;

  @Before public void setUp() {
    control = createControl();
    ub = control.createMock(UserBusiness.class);
    validator = new UserAddValidator();
    validator.setMinimumPasswordLength(6);
    validator.setMinimumUsernameLength(6);
    validator.setUserBusiness(ub);
  }

  @After public void tearDown() {
    validator = null;
    control = null;
    ub = null;
  }

  @Test public void testSupports() {
    assertTrue("Validator doesn't support command class",
        validator.supports(UserAddCommand.class));
  }

  @Test public void testValidate() {
    FieldError error;
    BindException errors;

    // setup
    UserAddCommand command = new UserAddCommand();

    User user = new User();
    user.setUserName("existing-user");
    user.setEmailAddress("existing@example.com");
    user.setPassword(User.hashPassword("Password1"));
    user.setRoles(new ArrayList<Role>());
    user.setEnabled(true);

    expect(ub.findUserByName("existing-user")).andStubReturn(user);
    expect(ub.findUserByName("new-user")).andStubReturn(null);
    control.replay();

    // null command
    errors = new BindException(command, "command");
    validator.validate(null, errors);
    assertEquals("Wrong number of errors occurred", 1, errors.getErrorCount());

    // no data supplied
    errors = new BindException(command, "command");
    validator.validate(command, errors);
    assertEquals("Wrong number of errors occurred", 4, errors.getErrorCount());
    assertEquals("Wrong error count for userName", 1,
        errors.getFieldErrorCount("userName"));
    assertEquals("Wrong error count for emailAddress", 1,
        errors.getFieldErrorCount("emailAddress"));
    assertEquals("Wrong error count for password", 1,
        errors.getFieldErrorCount("password"));
    assertEquals("Wrong error count for password2", 1,
        errors.getFieldErrorCount("password2"));

    // username too short
    command.setUserName("short");
    errors = new BindException(command, "command");
    validator.validate(command, errors);
    assertEquals("Wrong error count for userName", 1,
        errors.getFieldErrorCount("userName"));
    error = errors.getFieldErrors("userName").get(0);
    assertEquals("Wrong error code", "error.user.username.tooshort",
        error.getCode());

    // username exists
    command.setUserName("existing-user");
    errors = new BindException(command, "command");
    validator.validate(command, errors);
    assertEquals("Wrong error count for userName", 1,
        errors.getFieldErrorCount("userName"));
    error = errors.getFieldErrors("userName").get(0);
    assertEquals("Wrong error code", "error.user.username.exists",
        error.getCode());

    // correct username
    command.setUserName("new-user");
    errors = new BindException(command, "command");
    validator.validate(command, errors);
    assertEquals("Wrong error count for userName", 0,
        errors.getFieldErrorCount("userName"));

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

    // password 2 specified, but not password 1
    command.setPassword(null);
    errors = new BindException(command, "command");
    validator.validate(command, errors);
    assertEquals("Wrong error count for password", 1,
        errors.getFieldErrorCount("password"));
    assertEquals("Wrong error count for password2", 1,
        errors.getFieldErrorCount("password2"));
    error = errors.getFieldErrors("password").get(0);
    assertEquals("Wrong error code", "error.user.password.required",
        error.getCode());
    error = errors.getFieldErrors("password2").get(0);
    assertEquals("Wrong error code", "error.user.password.nomatch",
        error.getCode());

    // all data valid
    command.setPassword("Password2");
    errors = new BindException(command, "command");
    validator.validate(command, errors);
    assertEquals("Errors occurred", 0, errors.getErrorCount());

    control.verify();
  }
}