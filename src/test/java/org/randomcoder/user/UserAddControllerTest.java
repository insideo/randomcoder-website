package org.randomcoder.user;

import junit.framework.TestCase;

import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import org.randomcoder.test.mock.dao.UserDaoMock;
import org.randomcoder.test.mock.user.UserAddControllerMock;

public class UserAddControllerTest extends TestCase
{
	private UserAddControllerMock controller;
	private UserBusinessImpl userBusiness;
	private UserDaoMock userDao;

	@Override
	public void setUp() throws Exception
	{
		userDao = new UserDaoMock();
		userBusiness = new UserBusinessImpl();
		userBusiness.setUserDao(userDao);
		controller = new UserAddControllerMock();
		controller.setUserBusiness(userBusiness);
		controller.setSuccessView("success");
	}

	public void testOnBindOnNewForm() throws Exception
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		
		UserAddCommand command = new UserAddCommand();
		
		controller.onBindOnNewForm(request, command);
		
		assertTrue("User not enabled", command.isEnabled());
	}

	public void testOnSubmit()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		UserAddCommand command = new UserAddCommand();
		
		command.setUserName("on-submit");
		command.setEnabled(true);
		command.setPassword("Password1");
		command.setPassword2("Password1");
		command.setEmailAddress("onsubmit@example.com");
		command.setRoles(new Role[] {});
		
		BindException errors = new BindException(command, "command");
		
		ModelAndView mav = controller.onSubmit(request, response, command, errors);
		assertNotNull("Null model and view", mav);
		assertEquals("Wrong view name", "success", mav.getViewName());
		
		User user = userDao.findByUserName("on-submit");
		assertNotNull("Null user", user);
		assertEquals("Wrong username", "on-submit", user.getUserName());
	}

	@Override
	public void tearDown() throws Exception
	{
		controller = null;
		userBusiness = null;
		userDao = null;
	}
}