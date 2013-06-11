package org.randomcoder.bo;

import java.util.List;

import org.randomcoder.io.*;
import org.randomcoder.tag.*;

/**
 * Tag Management interface.
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
public interface TagBusiness
{
	/**
	 * Gets a list of TagCloudEntry objects to produce a tag cloud.
	 * @return list of TagCloudEntry objects sorted by display name.
	 */
	public List<TagCloudEntry> getTagCloud();
	
	/**
	 * Create a new tag.
	 * @param producer tag producer
	 */
	public void createTag(Producer<Tag> producer);

	/**
	 * Loads a tag for editing.
	 * @param consumer consumer
	 * @param tagId id of tag to load
	 */
	public void loadTagForEditing(Consumer<Tag> consumer, Long tagId);

	/**
	 * Update an existing tag.
	 * @param producer tag producer
	 * @param tagId tag id
	 */
	public void updateTag(Producer<Tag> producer, Long tagId);
	
	
	/**
	 * Deletes the tag with the given id.
	 * @param tagId tag id
	 */
	public void deleteTag(Long tagId);

	/**
	 * Finds a given {@code Tag} by name.
	 * 
	 * @param name
	 *            tag name
	 * @return {@code Tag} instance, or null if not found
	 */
	public Tag findTagByName(String name);

	/**
	 * Lists all Tag statistics.
	 * @return List of TagStatistics
	 */
	public List<TagStatistics> queryTagStatistics();

	/**
	 * Lists all Tag statistics in range.
	 * @param start starting result
	 * @param limit maximum number of results
	 * @return List of TagStatistics
	 */
	public List<TagStatistics> queryTagStatisticsInRange(int start, int limit);
	
	/**
	 * Calculates the maximum number of articles per tag.
	 * 
	 * @return article count
	 */
	public int queryTagMostArticles();

	/**
	 * Counts all tags.
	 * 
	 * @return count of tags
	 */
	public int countTags();	
}
