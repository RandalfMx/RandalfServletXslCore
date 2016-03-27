/**
 * 
 */
package mx.randalf.moduli.servlet.core.exception;

/**
 * @author massi
 *
 */
public class StdNotificheException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5065598953006847516L;

	/**
	 * 
	 */
	public StdNotificheException()
	{
	}

	/**
	 * @param message
	 */
	public StdNotificheException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public StdNotificheException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public StdNotificheException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
