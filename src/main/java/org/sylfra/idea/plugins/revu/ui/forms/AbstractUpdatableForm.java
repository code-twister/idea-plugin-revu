package org.sylfra.idea.plugins.revu.ui.forms;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylfra.idea.plugins.revu.RevuBundle;
import org.sylfra.idea.plugins.revu.model.IRevuEntity;
import org.sylfra.idea.plugins.revu.model.Review;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author <a href="mailto:sylfradev@yahoo.fr">Sylvain FRANCOIS</a>
 * @version $Id$
 */
public abstract class AbstractUpdatableForm<T extends IRevuEntity<T>> implements IUpdatableForm<T>
{
  protected final java.util.List<JComponent> errors;
  protected final java.util.List<UpdatableFormListener<T>> listeners;
  protected Review enclosingReview;

  protected AbstractUpdatableForm()
  {
    errors = new ArrayList<JComponent>();
    listeners = new ArrayList<UpdatableFormListener<T>>();
  }

  public Review getEnclosingReview()
  {
    return enclosingReview;
  }

  public void addUpdatableFormListener(UpdatableFormListener<T> listener)
  {
    listeners.add(listener);
  }

  public boolean validateInput()
  {
    errors.clear();
    internalValidateInput();
    return errors.isEmpty();
  }

  protected Object[] buildComboItemsArray(Collection<?> items, boolean required)
  {
    java.util.List<Object> itemsClone = new ArrayList<Object>(items);
    if (required)
    {
      itemsClone.add(0, null);
    }

    return itemsClone.toArray(new Object[itemsClone.size()]);
  }

  protected void updateRequiredError(JComponent component, boolean hasError)
  {
    updateError(component, hasError, RevuBundle.message("general.fieldRequired.text"));
  }

  protected void updateError(JComponent component, boolean hasError, String message)
  {
    // One error max by component
    if (errors.contains(component))
    {
      return;
    }

    boolean hasErrorBorder = component.getBorder() instanceof ErrorBorder;
    if (hasError)
    {
      if (message != null)
      {
        if (!hasErrorBorder)
        {
          component.setBorder(new ErrorBorder(component));
        }
        component.setToolTipText(message);
      }
      errors.add(component);
    }
    else
    {
      if (hasErrorBorder)
      {
        component.setBorder(((ErrorBorder)component.getBorder()).nestedBorder);
      }
      component.setToolTipText(null);
      errors.remove(component);
    }
  }

  public void dispose()
  {
  }

  protected boolean checkEquals(Object o1, Object o2)
  {
    return ((o1 == o2) || ((o1 != null) && (o1.equals(o2))));
  }

  public final void updateUI(Review enclosingReview, @Nullable T data)
  {
    this.enclosingReview = enclosingReview;

    internalUpdateUI(data);
    if (getPreferredFocusedComponent() != null)
    {
      getPreferredFocusedComponent().requestFocusInWindow();
    }

    for (UpdatableFormListener<T> listener : listeners)
    {
      listener.uiUpdated(enclosingReview, data);
    }
  }

  public final boolean updateData(@NotNull T data)
  {
    if (!isModified(data))
    {
      return true;
    }

    if (!validateInput())
    {
      errors.get(0).requestFocusInWindow();
      return false;
    }

    internalUpdateData(data);

    for (UpdatableFormListener<T> listener : listeners)
    {
      listener.dataUpdated(data);
    }

    return true;
  }

  public abstract boolean isModified(@NotNull T data);

  protected abstract void internalValidateInput();

  protected abstract void internalUpdateUI(@Nullable T data);

  protected abstract void internalUpdateData(@NotNull T data);

  private final static class ErrorBorder extends CompoundBorder
  {
    private final Border nestedBorder;

    private ErrorBorder(JComponent component)
    {
      super(BorderFactory.createLineBorder(Color.RED, 1), component.getBorder());
      nestedBorder = component.getBorder();
    }
  }

  public static interface UpdatableFormListener<T>
  {
    void uiUpdated(Review enclosingReview, @Nullable T data);
    void dataUpdated(@NotNull T data);
  }
}