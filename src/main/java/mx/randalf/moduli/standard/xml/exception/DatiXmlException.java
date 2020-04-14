/**
 * 
 */
package mx.randalf.moduli.standard.xml.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mx.randalf.interfacException.exception.MagException;
import mx.randalf.interfacException.interfacce.IMagException;

/**
 * @author massi
 * 
 */
public class DatiXmlException implements IMagException {

	private Logger log = LogManager.getLogger(DatiXmlException.class);

	/**
	 * 
	 */
	public DatiXmlException() {
	}

	/**
	 * @see mx.magEdit.mag.interfacce.IMagException#add(mx.magEdit.mag.exception.MagException)
	 */
	public void add(MagException exc) {
		if (exc.getTipo().equals(MagException.ERROR))
			log.error(exc.getMessage(), exc);
		else if (exc.getTipo().equals(MagException.FATALERROR))
			log.error(exc.getMessage(), exc);
		else if (exc.getTipo().equals(MagException.WARNING))
			log.warn(exc.getMessage(), exc);
	}

	/**
	 * @see mx.magEdit.mag.interfacce.IMagException#getError(int)
	 */
	public MagException getError(int i) {
		return null;
	}

	/**
	 * @see mx.magEdit.mag.interfacce.IMagException#getFatalError(int)
	 */
	public MagException getFatalError(int i) {
		return null;
	}

	/**
	 * @see mx.magEdit.mag.interfacce.IMagException#getNumErr()
	 */
	public int getNumErr() {
		return 0;
	}

	/**
	 * @see mx.magEdit.mag.interfacce.IMagException#getNumFatalErr()
	 */
	public int getNumFatalErr() {
		return 0;
	}

	/**
	 * @see mx.magEdit.mag.interfacce.IMagException#getNumWar()
	 */
	public int getNumWar() {
		return 0;
	}

	/**
	 * @see mx.magEdit.mag.interfacce.IMagException#getWarning(int)
	 */
	public MagException getWarning(int i) {
		return null;
	}

}
