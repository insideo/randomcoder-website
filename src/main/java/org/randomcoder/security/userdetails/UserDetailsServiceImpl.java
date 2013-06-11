package org.randomcoder.security.userdetails;

import org.acegisecurity.userdetails.*;
import org.apache.commons.logging.*;
import org.randomcoder.bo.UserBusiness;
import org.randomcoder.user.User;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

/**
 * Acegi UserDetailsService implementation.
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
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
public class UserDetailsServiceImpl implements UserDetailsService
{
	private static final Log logger = LogFactory.getLog(UserDetailsServiceImpl.class);

	private UserBusiness userBusiness;
	private boolean debug = false;

	/**
	 * Sets the UserBusiness implementation to use.
	 * 
	 * @param userBusiness
	 *            UserBusiness implementation.
	 */
	@Required
	public void setUserBusiness(UserBusiness userBusiness)
	{
		this.userBusiness = userBusiness;
	}

	/**
	 * Turns debug logging of ppid and issuerhash on / off.
	 * 
	 * @param debug
	 *            true if debugging is to be enabled.
	 */
	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	/**
	 * Retrieves the user with the given username.
	 * 
	 * @param username
	 *            user name to lookup
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException
	{
		User user = userBusiness.findUserByName(username);
		if (user == null || user.getPassword() == null)
			throw new UsernameNotFoundException(username);
		return new UserDetailsImpl(user);
	}
}