package ir.taxi1880.operatormanagement.helper;

import org.acra.ACRA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AE_Exception_Interceptor implements Thread.UncaughtExceptionHandler {
  /**
   * Sets up to catch an exception.
   * Call this after initializing ACRA.
   */
  public void setUpFatalExceptionHandling() {
    Thread.setDefaultUncaughtExceptionHandler(this);
  }

  /**
   * Called by the JVM on an uncaught exception.
   * @param t - the thread
   * @param e - the throwable
   */
  @Override
  public void uncaughtException(@Nullable Thread t, @NonNull Throwable e) {
    ACRA.getErrorReporter().handleException(e, true);
  }
}
