package com.randomcoder.article;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.randomcoder.article.comment.*;
import com.randomcoder.article.moderation.*;
import com.randomcoder.io.*;
import com.randomcoder.security.UnauthorizedException;
import com.randomcoder.tag.*;
import com.randomcoder.user.*;

/**
 * Business implementation which handles articles.
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
public class ArticleBusinessImpl implements ArticleBusiness
{
	private static final Log logger = LogFactory.getLog(ArticleBusinessImpl.class);
	
	private static final String ROLE_MANAGE_ARTICLES = "ROLE_MANAGE_ARTICLES";
	private static final String ROLE_MANAGE_COMMENTS = "ROLE_MANAGE_COMMENTS";
	
	private UserDao userDao;
	private RoleDao roleDao;
	private ArticleDao articleDao;
	private TagDao tagDao;
	private CommentDao commentDao;
	private CommentReferrerDao commentReferrerDao;
	private CommentIpDao commentIpDao;
	private CommentUserAgentDao commentUserAgentDao;
	private Moderator moderator;
		
	/**
	 * Sets the UserDao implementation to use.
	 * @param userDao UserDao implementation
	 */
	@Required
	public void setUserDao(UserDao userDao) { this.userDao = userDao; }
	
	/**
	 * Sets the RoleDao implementation to use.
	 * @param roleDao RoleDao implementation
	 */
	@Required
	public void setRoleDao(RoleDao roleDao) { this.roleDao = roleDao; }
	
	/**
	 * Sets the ArticleDao implementation to use.
	 * @param articleDao ArticleDao implementation
	 */
	@Required
	public void setArticleDao(ArticleDao articleDao) { this.articleDao = articleDao; }
	
	/**
	 * Sets the TagDao implementation to use.
	 * @param tagDao TagDao implementation
	 */
	@Required
	public void setTagDao(TagDao tagDao) { this.tagDao = tagDao; }

	/**
	 * Sets the CommentDao implementation to use.
	 * @param commentDao CommentDao implementation
	 */
	@Required
	public void setCommentDao(CommentDao commentDao) { this.commentDao = commentDao; }
	
	/**
	 * Sets the CommentReferrerDao implementation to use.
	 * @param commentReferrerDao CommentReferrerDao implementation
	 */
	@Required
	public void setCommentReferrerDao(CommentReferrerDao commentReferrerDao)
	{
		this.commentReferrerDao = commentReferrerDao;
	}
	
	/**
	 * Sets the CommentIpDao implementation to use.
	 * @param commentIpDao CommentIpDao implementation
	 */
	@Required
	public void setCommentIpDao(CommentIpDao commentIpDao)
	{
		this.commentIpDao = commentIpDao;
	}
	
	/**
	 * Sets the CommentUserAgentDao implementation to use.
	 * @param commentUserAgentDao CommentUserAgentDao implementation
	 */
	@Required
	public void setCommentUserAgentDao(CommentUserAgentDao commentUserAgentDao)
	{
		this.commentUserAgentDao = commentUserAgentDao;
	}
	
	/**
	 * Sets the moderator to use for automatic comment moderation
	 * @param moderator comment moderator
	 */
	@Required
	public void setModerator(Moderator moderator)
	{
		this.moderator = moderator;
	}
	
	@Transactional
	public void createArticle(Producer<Article> producer, String userName)
	{
		User user = findUserByName(userName);

		Article article = new Article();

		producer.produce(article);
		
		article.setCreatedByUser(user);
		article.setCreationDate(new Date());
		
		// save tags
		for (Tag tag : article.getTags())
		{
			if (tag.getId() == null) tagDao.create(tag);
		}

		articleDao.create(article);		
	}

	@Transactional
	public void createComment(Producer<Comment> producer, Long articleId, String userName, String referrer, String ipAddress, String userAgent)
	{
		User user = userName == null ? null : findUserByName(userName);

		Article article = loadArticle(articleId);
		
		Comment comment = new Comment();
		
		producer.produce(comment);
		
		comment.setCreatedByUser(user);
		comment.setCreationDate(new Date());
		comment.setArticle(article);
		
		if (user == null)
		{
			comment.setVisible(false); // anonymous users have to pass the checks
			comment.setModerationStatus(ModerationStatus.PENDING);
		}
		else
		{
			comment.setVisible(true); // allow comment to display initially
			boolean trusted = isUserTrustedForComments(user);			
			comment.setModerationStatus(trusted ? ModerationStatus.HAM : ModerationStatus.PENDING);
		}
		
		referrer = StringUtils.trimToNull(referrer);
		if (referrer != null)
		{
			CommentReferrer ref = commentReferrerDao.findByUri(referrer);
			if (ref == null)
			{
				ref = new CommentReferrer();
				ref.setCreationDate(new Date());
				ref.setReferrerUri(referrer);
				commentReferrerDao.create(ref);
			}
			comment.setReferrer(ref);
		}
				
		ipAddress = StringUtils.trimToNull(ipAddress);
		if (ipAddress != null)
		{
			CommentIp ip = commentIpDao.findByIpAddress(ipAddress);
			if (ip == null)
			{
				ip = new CommentIp();
				ip.setCreationDate(new Date());
				ip.setIpAddress(ipAddress);
				commentIpDao.create(ip);
			}
			comment.setIpAddress(ip);
		}
		
		userAgent = StringUtils.trimToNull(userAgent);
		if (userAgent != null)
		{
			CommentUserAgent ua = commentUserAgentDao.findByName(userAgent);
			if (ua == null)
			{
				ua = new CommentUserAgent();
				ua.setCreationDate(new Date());
				ua.setUserAgentName(userAgent);
				commentUserAgentDao.create(ua);
			}
			comment.setUserAgent(ua);
		}
		
		commentDao.create(comment);
		
		article.getComments().add(comment);
		
		articleDao.update(article);
	}

	@Transactional
	public void updateArticle(Producer<Article> producer, Long articleId, String userName)
	{
		User user = findUserByName(userName);

		Article article = loadArticle(articleId);

		checkAuthorUpdate(user, article);

		producer.produce(article);
		
		article.setModifiedByUser(user);
		article.setModificationDate(new Date());

		// save tags
		for (Tag tag : article.getTags())
		{
			if (tag.getId() == null) tagDao.create(tag);
		}
		
		articleDao.update(article);		
	}

	@Transactional(readOnly=true)
	public void loadArticleForEditing(Consumer<Article> consumer, Long articleId, String userName)
	{
		User user = findUserByName(userName);		
		Article article = loadArticle(articleId);

		checkAuthorUpdate(user, article);
		
		consumer.consume(article);
	}
	
	@Transactional
	public void deleteArticle(String userName, Long articleId)
	{
		User user = findUserByName(userName);
		Article article = loadArticle(articleId);
		
		checkAuthorDelete(user, article);
		
		articleDao.delete(article);
	}
	
	@Transactional(rollbackFor=ModerationException.class)
	public Article approveComment(Long commentId) throws ModerationException
	{
		Comment comment = loadComment(commentId);
		
		logger.info("Approving comment #" + comment.getId());
		
		Article article = comment.getArticle();		
		comment.setModerationStatus(ModerationStatus.HAM);
		comment.setVisible(true);
		commentDao.update(comment);
		
		moderator.markAsHam(comment);
		
		return article;
	}

	@Transactional(rollbackFor=ModerationException.class)
	public Article disapproveComment(Long commentId) throws ModerationException
	{
		Comment comment = loadComment(commentId);
		
		logger.info("Disapproving comment #" + comment.getId());
		
		Article article = comment.getArticle();
		comment.setModerationStatus(ModerationStatus.SPAM);
		comment.setVisible(false);
		commentDao.update(comment);

		moderator.markAsSpam(comment);
		
		return article;
	}

	@Transactional
	public Article deleteComment(Long commentId)
	{
		Comment comment = loadComment(commentId);
		
		logger.info("Deleting comment #" + comment.getId());
		
		Article article = comment.getArticle();
		
		article.getComments().remove(comment);
		
		commentDao.delete(comment);		
		articleDao.update(article);
		
		return article;
	}

	@Transactional
	public boolean moderateComments(int count) throws ModerationException
	{
		Iterator<Comment> comments = commentDao.iterateForModerationInRange(0, count);
		if (!comments.hasNext()) return false;
		
		while (comments.hasNext())
		{
			Comment comment = comments.next();
			
			logger.info("Moderating comment #" + comment.getId());
			
			boolean valid = moderator.validate(comment);
			
			logger.info(valid ? "  HAM" : "  SPAM");
			
			comment.setVisible(valid);
			comment.setModerationStatus(valid ? ModerationStatus.HAM : ModerationStatus.SPAM);
			commentDao.update(comment);
		}
		return true;
	}

	private void checkAuthorUpdate(User user, Article article)
	{
		checkAuthor(user, article, "You are not allowed to edit articles you did not create.");
	}

	private void checkAuthorDelete(User user, Article article)
	{
		checkAuthor(user, article, "You are not allowed to delete articles you did not create.");
	}

	private void checkAuthor(User user, Article article, String errorMessage)
	{
		Role articleAdmin = findRoleByName(ROLE_MANAGE_ARTICLES);
		
		if (!user.getRoles().contains(articleAdmin))
		{
			// make sure user created this article
			User createdBy = article.getCreatedByUser();

			if (createdBy == null || !user.getId().equals(createdBy.getId()))
				throw new UnauthorizedException(errorMessage);
		}
	}
	
	private boolean isUserTrustedForComments(User user)
	{
		if (user == null) return false; // anonymous users are not trusted
		
		List<Role> roles = user.getRoles();
		
		// trust users who can post / modify articles
		if (roles.contains(findRoleByName(ROLE_MANAGE_ARTICLES)))
				return true;
		
		// trust users who can modify comments
		if (roles.contains(findRoleByName(ROLE_MANAGE_COMMENTS)))
				return true;
		
		return false;
	}
	
	private User findUserByName(String userName) throws UserNotFoundException
	{
		User user = userDao.findByUserName(userName);
		
		if (user == null)
			throw new UserNotFoundException("Unknown user: " + userName);
		return user;
	}
	
	private Article loadArticle(Long articleId) throws ArticleNotFoundException
	{
		if (articleId == null)
			throw new ArticleNotFoundException("Invalid id specified.");
		
		Article article = articleDao.read(articleId);

		if (article == null)
			throw new ArticleNotFoundException("No article exists with id: " + articleId);
		return article;
	}

	private Comment loadComment(Long commentId) throws CommentNotFoundException
	{
		if (commentId == null)
			throw new CommentNotFoundException("Invalid id specified.");
		
		Comment comment = commentDao.read(commentId);

		if (comment == null)
			throw new CommentNotFoundException("No comment exists with id: " + commentId);
		
		return comment;
	}
	
	private Role findRoleByName(String roleName) throws RoleNotFoundException
	{
		Role role = roleDao.findByName(roleName);
		
		if (role == null)
			throw new RoleNotFoundException("Unknown role: " + roleName);
		
		return role;
	}
}
