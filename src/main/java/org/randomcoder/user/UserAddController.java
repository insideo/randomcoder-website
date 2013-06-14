package org.randomcoder.user;

import javax.servlet.http.*;

import org.randomcoder.mvc.command.UserAddCommand;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller class which handles adding users.
 * 
 * <pre>
 * Copyright (c) 2006-2007, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
public class UserAddController extends AbstractUserController
{
	
	/**
	 * Sets default values for the form.
	 * @param request HTTP request
	 * @param command command object
	 * @throws Exception if an error occurs
	 */
	@Override
	protected void onBindOnNewForm(HttpServletRequest request, Object command) throws Exception
	{
		super.onBindOnNewForm(request, command);
		
		UserAddCommand cmd = (UserAddCommand) command;
		
		cmd.setEnabled(true);
	}

	/**
	 * Creates a new user based on form submission.
	 * @param request HTTP request
	 * @param response HTTP response
	 * @param command command object
	 * @param errors error object
	 * @return ModelAndView from {@link #getSuccessView()}
	 */
	@Override
	public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
	{
		UserAddCommand cmd = (UserAddCommand) command;

		userBusiness.createUser(cmd);
		
		return new ModelAndView(getSuccessView());
	}
	
}
