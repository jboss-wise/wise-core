package org.jboss.wise.core.exception;

import java.io.Serializable;

/**
 * The GWT module and wise-core contain duplicate copies of this class
 * because this exception is passed from wise-core to the GWT code.
 * GWT requires a copy in order to generate the corresponding javascript.
 * wise-core requires a copy because it is the source of the exception.
 *
 * User: rsearls
 * Date: 6/23/15
 */
public class WiseWebServiceException extends Exception implements Serializable {

   private static final long serialVersionUID = 3803266852951478259L;
   private String message;

   public WiseWebServiceException() {

   }

   public WiseWebServiceException(String message, Throwable cause) {
      super(message, cause);
      this.message = message;
   }

   public String getMessage() {
      return message;
   }
}
