package org.randomcoder.bo;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.easymock.*;
import org.junit.*;
import org.randomcoder.db.*;
import org.randomcoder.mvc.command.*;
import org.randomcoder.user.UserNotFoundException;

@SuppressWarnings("javadoc")
public class UserBusinessImplTest
{
	private IMocksControl control;
	private UserBusinessImpl ub;
	private UserRepository ur;
	private RoleRepository rr;

	@Before
	public void setUp()
	{
		control = createControl();
		ur = control.createMock(UserRepository.class);
		rr = control.createMock(RoleRepository.class);

		ub = new UserBusinessImpl();
		ub.setUserRepository(ur);
		ub.setRoleRepository(rr);
	}

	@After
	public void tearDown()
	{
		control = null;
		ur = null;
		rr = null;
		ub = null;
	}

	@Test
	public void testChangePassword()
	{
		User user = new User();
		user.setUserName("test-change-password");
		user.setEnabled(true);
		user.setEmailAddress("test@example.com");
		user.setPassword(User.hashPassword("test-password"));

		expect(ur.findByUserName("test-change-password")).andReturn(user);
		expect(ur.save(user)).andReturn(user);
		control.replay();

		ub.changePassword("test-change-password", "test-new-password");
		control.verify();
		assertEquals("Wrong password", User.hashPassword("test-new-password"), user.getPassword());
	}

	@Test(expected = UserNotFoundException.class)
	public void testChangePasswordUserNotFound()
	{
		expect(ur.findByUserName("bogus-user")).andReturn(null);
		control.replay();

		ub.changePassword("bogus-user", "bogus-password");
		control.verify();
	}

	@Test
	public void testCreateUser()
	{
		UserAddCommand cmd = new UserAddCommand();

		cmd.setUserName("test-create");
		cmd.setEmailAddress("test-create@example.com");
		cmd.setPassword("testCreate1");
		cmd.setPassword2("testCreate1");
		cmd.setEnabled(true);

		Role testRole = new Role();
		testRole.setId(1L);
		testRole.setName("test-role");
		testRole.setDescription("Test role");

		cmd.setRoles(new Role[] { testRole });

		Capture<User> created = newCapture();

		expect(ur.save(capture(created))).andReturn(null);
		control.replay();

		ub.createUser(cmd);
		control.verify();

		assertEquals("test-create", created.getValue().getUserName());
	}

	@Test
	public void testCreateAccountByPassword()
	{
		AccountCreateCommand cmd = new AccountCreateCommand();
		cmd.setUserName("test-create");
		cmd.setEmailAddress("test-create@example.com");
		cmd.setPassword("testCreate1");
		cmd.setPassword2("testCreate1");
		cmd.setWebsite("http://www.example.com/");

		Capture<User> created = newCapture();

		expect(ur.save(capture(created))).andReturn(null);
		control.replay();

		ub.createAccount(cmd);
		control.verify();

		assertEquals("test-create", created.getValue().getUserName());
	}

	@Test
	public void testUpdateUser()
	{
		User user = new User();
		user.setUserName("test-update-user");
		user.setEnabled(true);
		user.setEmailAddress("test-update@example.com");
		user.setPassword(User.hashPassword("testPassword1"));
		user.setRoles(new ArrayList<Role>());
		user.setId(1L);

		UserEditCommand cmd = new UserEditCommand();
		cmd.consume(user);
		cmd.setEmailAddress("test-update2@example.com");
		cmd.setPassword("testPassword2");
		cmd.setPassword2("testPassword2");

		Capture<User> saved = newCapture();

		expect(ur.findOne(1L)).andReturn(user);
		expect(ur.save(capture(saved))).andReturn(null);
		control.replay();

		ub.updateUser(cmd, 1L);
		control.verify();

		assertEquals("test-update2@example.com", saved.getValue().getEmailAddress());
	}

	@Test
	public void testDeleteUser()
	{
		User user = new User();
		user.setId(1L);
		ur.delete(1L);
		control.replay();

		ub.deleteUser(1L);
		control.verify();
	}

	@Test
	public void testLoadUserForEditing()
	{
		User user = new User();
		user.setUserName("test-load-user");
		user.setEnabled(true);
		user.setEmailAddress("test-load@example.com");
		user.setPassword(User.hashPassword("testPassword1"));
		user.setRoles(new ArrayList<Role>());
		user.setId(1L);

		UserEditCommand cmd = new UserEditCommand();

		expect(ur.findOne(1L)).andReturn(user);
		control.replay();

		ub.loadUserForEditing(cmd, 1L);
		control.verify();

		assertEquals("Wrong username", "test-load-user", cmd.getUserName());
	}

	@Test
	public void testLoadUserForEditingUserNotFound()
	{
		try
		{
			UserEditCommand cmd = new UserEditCommand();
			ub.loadUserForEditing(cmd, (long) -1);
			fail("UserNotFoundException expected");
		}
		catch (UserNotFoundException e)
		{
			// pass
		}
	}

	@Test
	public void testAuditUsernamePasswordLogin()
	{
		User user = new User();
		user.setUserName("test-audit-user");
		user.setEnabled(true);
		user.setEmailAddress("test-audit@example.com");
		user.setPassword(User.hashPassword("testPassword1"));
		user.setRoles(new ArrayList<Role>());
		user.setId(1L);

		expect(ur.findByUserName("test-audit-user")).andReturn(user);
		expect(ur.save(user)).andReturn(user);
		control.replay();
		
		ub.auditUsernamePasswordLogin("test-audit-user");
		control.verify();
		assertNotNull("Missing last login date", user.getLastLoginDate());
	}

	@Test(expected = UserNotFoundException.class)
	public void testAuditUsernamePasswordLoginNullUser()
	{
		expect(ur.findByUserName(null)).andReturn(null);
		control.replay();

		ub.auditUsernamePasswordLogin(null);
		control.verify();
	}
}