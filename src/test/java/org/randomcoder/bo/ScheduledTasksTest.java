package org.randomcoder.bo;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;

public class ScheduledTasksTest {
  private IMocksControl control;
  private ArticleBusiness ab;
  private ScheduledTasks st;

  @Before public void setUp() {
    control = createControl();
    ab = control.createMock(ArticleBusiness.class);
    st = new ScheduledTasks();
    st.setModerationBatchSize(3);
    st.setArticleBusiness(ab);
  }

  @After public void tearDown() {
    st = null;
    ab = null;
    control = null;

  }

  @Test public void testModerateComments() throws Exception {
    expect(ab.moderateComments(3)).andReturn(true);
    expect(ab.moderateComments(3)).andReturn(false);
    control.replay();

    st.moderateComments();
    control.verify();
  }

  @Test public void testModerateCommentsError() throws Exception {
    expect(ab.moderateComments(3)).andThrow(new RuntimeException("test"));
    control.replay();

    st.moderateComments();
    control.verify();
  }

}
