/**
 * 
 */
package mx.randalf.moduli.servlet.core.exception;

/**
 * @author massi
 *
 */
public class StdModuliException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5065598953006847516L;

	/**
	 * 
	 */
	public StdModuliException()
	{
	}

	/**
	 * @param message
	 */
	public StdModuliException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public StdModuliException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public StdModuliException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
