package org.jboss.wise.core.client.impl.reflection;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class SecurityActions {
    
    /**
     * Get context classloader.
     * 
     * @return the current context classloader
     */
    static ClassLoader getContextClassLoader()
    {
       SecurityManager sm = System.getSecurityManager();
       if (sm == null)
       {
          return Thread.currentThread().getContextClassLoader();
       }
       else
       {
          return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
             public ClassLoader run()
             {
                return Thread.currentThread().getContextClassLoader();
             }
          });
       }
    }
    
    /**
     * Set context classloader.
     *
     * @param cl the classloader
     * @return previous context classloader
     * @throws Throwable for any error
     */
    static ClassLoader setContextClassLoader(final ClassLoader cl)
    {
       if (System.getSecurityManager() == null)
       {
          ClassLoader result = Thread.currentThread().getContextClassLoader();
          if (cl != null)
             Thread.currentThread().setContextClassLoader(cl);
          return result;
       }
       else
       {
          try
          {
             return AccessController.doPrivileged(new PrivilegedExceptionAction<ClassLoader>() {
                public ClassLoader run() throws Exception
                {
                   try
                   {
                      ClassLoader result = Thread.currentThread().getContextClassLoader();
                      if (cl != null)
                         Thread.currentThread().setContextClassLoader(cl);
                      return result;
                   }
                   catch (Exception e)
                   {
                      throw e;
                   }
                   catch (Error e)
                   {
                      throw e;
                   }
                   catch (Throwable e)
                   {
                      throw new RuntimeException("ERROR_SETTING_CONTEXT_CLASSLOADER",  e);
                   }
                }
             });
          }
          catch (PrivilegedActionException e)
          {
             throw new RuntimeException("ERROR_RUNNING_PRIVILEGED_ACTION",  e.getCause());
          }
       }
    }
}
