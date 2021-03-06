package org.sylfra.idea.plugins.revu.actions.issuetree;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import org.sylfra.idea.plugins.revu.ui.toolwindow.RevuToolWindowManager;

/**
 * @author <a href="mailto:syllant@gmail.com">Sylvain FRANCOIS</a>
 * @version $Id$
 */
public class ExpandIssueTreeAction extends AnAction
{
  @Override
  public void actionPerformed(AnActionEvent e)
  {
    Project project = e.getData(DataKeys.PROJECT);
    if (project != null)
    {
      project.getComponent(RevuToolWindowManager.class).getSelectedReviewBrowsingForm().getIssueTree().expandAll();
    }
  }
}
