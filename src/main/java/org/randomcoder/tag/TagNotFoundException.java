package org.randomcoder.tag;

/**
 * Exception thrown when a requested tag cannot be found.
 */
public class TagNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 8212072324579650157L;

  /**
   * Default constructor.
   */
  public TagNotFoundException() {
    super();
  }

  /**
   * Constructor taking an optional message to display.
   *
   * @param message message to assoicate with this exception.
   */
  public TagNotFoundException(String message) {
    super(message);
  }

  /**
   * Gets the message (if any) associated with this exception.
   *
   * @return message
   */
  @Override public String getMessage() {
    return super.getMessage();
  }
}
