package ru.hogwarts.school.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

public class GlobalExceptionHandler extends FatalBeanException {
  @Nullable
  private final String beanName;
  @Nullable
  private final String resourceDescription;
  @Nullable
  private List<Throwable> relatedCauses;

  public GlobalExceptionHandler(String msg) {
    super(msg);
    this.beanName = null;
    this.resourceDescription = null;
  }

  public GlobalExceptionHandler(String msg, Throwable cause) {
    super(msg, cause);
    this.beanName = null;
    this.resourceDescription = null;
  }

  public GlobalExceptionHandler(String beanName, String msg) {
    super("Error creating bean with name '" + beanName + "': " + msg);
    this.beanName = beanName;
    this.resourceDescription = null;
  }

  public GlobalExceptionHandler(String beanName, String msg, Throwable cause) {
    this(beanName, msg);
    this.initCause(cause);
  }

  public GlobalExceptionHandler(@Nullable String resourceDescription, @Nullable String beanName, @Nullable String msg) {
    super("Error creating bean with name '" + beanName + "'" + (resourceDescription != null ? " defined in " + resourceDescription : "") + ": " + msg);
    this.resourceDescription = resourceDescription;
    this.beanName = beanName;
    this.relatedCauses = null;
  }

  public GlobalExceptionHandler(@Nullable String resourceDescription, String beanName, @Nullable String msg, Throwable cause) {
    this(resourceDescription, beanName, msg);
    this.initCause(cause);
  }

  @Nullable
  public String getResourceDescription() {
    return this.resourceDescription;
  }

  @Nullable
  public String getBeanName() {
    return this.beanName;
  }

  public void addRelatedCause(Throwable ex) {
    if (this.relatedCauses == null) {
      this.relatedCauses = new ArrayList();
    }

    this.relatedCauses.add(ex);
  }

  @Nullable
  public Throwable[] getRelatedCauses() {
    return this.relatedCauses == null ? null : (Throwable[])this.relatedCauses.toArray(new Throwable[0]);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder(super.toString());
    if (this.relatedCauses != null) {
      for(Throwable relatedCause : this.relatedCauses) {
        sb.append("\nRelated cause: ");
        sb.append(relatedCause);
      }
    }

    return sb.toString();
  }

  public void printStackTrace(PrintStream ps) {
    synchronized(ps) {
      super.printStackTrace(ps);
      if (this.relatedCauses != null) {
        for(Throwable relatedCause : this.relatedCauses) {
          ps.println("Related cause:");
          relatedCause.printStackTrace(ps);
        }
      }

    }
  }

  public void printStackTrace(PrintWriter pw) {
    synchronized(pw) {
      super.printStackTrace(pw);
      if (this.relatedCauses != null) {
        for(Throwable relatedCause : this.relatedCauses) {
          pw.println("Related cause:");
          relatedCause.printStackTrace(pw);
        }
      }

    }
  }

  public boolean contains(@Nullable Class<?> exClass) {
    if (super.contains(exClass)) {
      return true;
    } else {
      if (this.relatedCauses != null) {
        for(Throwable relatedCause : this.relatedCauses) {
          if (relatedCause instanceof NestedRuntimeException) {
            NestedRuntimeException nested = (NestedRuntimeException)relatedCause;
            if (nested.contains(exClass)) {
              return true;
            }
          }
        }
      }

      return false;
    }
  }
}