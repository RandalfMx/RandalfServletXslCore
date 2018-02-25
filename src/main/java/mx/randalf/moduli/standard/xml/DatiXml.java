/**
 * 
 */
package mx.randalf.moduli.standard.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import mx.randalf.converter.text.ConvertToURI;
import mx.randalf.converter.text.ConvertToUTF8;
import mx.randalf.interfacException.interfacce.IMagException;

import org.apache.axis.message.MessageElement;
import org.apache.axis.utils.XMLUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 * @author massi
 * 
 */
public class DatiXml {

	/**
	 * Questa variabile raccoglie la lista dei fogli di stile associati alla
	 * pagina
	 */
	private Vector<String> styleSheet = null;

	/**
	 * Questa variabile raccoglie la lista dei Java script associati alla pagina
	 */
	private Vector<String> javaScript = null;

	/**
	 * Questa variabile viene utilizzata per indicare la lista degli elementi
	 * aggiuntivi della pagina
	 */
	private Vector<MessageElement> elements = null;

	/**
	 * Questa variabile indica il titolo della pagina
	 */
	private String title = null;

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = Logger.getLogger(DatiXml.class);

	/**
	 * Questa variabile viene utilizzata per la conversione in UTF8
	 */
	protected ConvertToUTF8 convert = null;

	/**
	 * Questa variabile viene utilizzata per la conversione in formato URL
	 */
	protected ConvertToURI convertUri = null;

	/**
	 * Costruttore
	 */
	public DatiXml(IMagException magException) {
		convert = new ConvertToUTF8(magException, "");
		convert.setConvertText(false);
		convertUri = new ConvertToURI(magException, "");
		convertUri.setConvertText(false);
	}

	/**
	 * Questo metodo viene utilizzato per aggiungere i fogli di Style alla
	 * pagina
	 * 
	 * @param styleSheet
	 *            Foglio di Stile da aggiungere
	 */
	public void addStyleSheet(String styleSheet) {
		if (this.styleSheet == null)
			this.styleSheet = new Vector<String>();
		this.styleSheet.add(styleSheet);
	}

	/**
	 * Questo metodo viene utilizzato per aggiungere i Java Script alla pagina
	 * 
	 * @param javaScript
	 *            Java Script da aggiungere
	 */
	public void addJavaScript(String javaScript) {
		if (this.javaScript == null)
			this.javaScript = new Vector<String>();
		this.javaScript.add(javaScript);
	}

	/**
	 * Questo metodo viene utilizzato per indicare la lista degli elementi
	 * aggiuntivi
	 * 
	 * @param element
	 *            Elementi aggiuntivi
	 */
	public void addElement(MessageElement element) {
		if (elements == null)
			elements = new Vector<MessageElement>();

		elements.add(element);
	}

	/**
	 * Questo metodo viene utilizzata per indicare il titolo della pagina
	 * 
	 * @param title
	 *            Titolo della pagina
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Questo metodo viene utilizzato per esportare il foglio xml in InputStream
	 * 
	 * @return Input Stream del foglio Xml
	 * @throws SOAPException
	 */
	public void printOutputStream(OutputStream out) throws SOAPException {
		Document doc = null;
		MessageElement document = null;

		try {

			doc = XMLUtils.newDocument();

			document = new MessageElement();
			document.setName("MxServlet");
			document.setEncodingStyle("UTF-8");

			if (title != null) {
				convert.setSezione("title");
				convert.addChildElement(document, "title", title, true);
			}
			if (styleSheet != null)
				document.addChildElement(genStyleSheet());

			if (javaScript != null)
				document.addChildElement(genJavaScript());

			if (elements != null) {
				for (int x = 0; x < elements.size(); x++)
					document.addChildElement((MessageElement) elements.get(x));
			}

			XMLUtils.DocumentToStream(doc, out);
			XMLUtils.ElementToStream(document, out);
		} catch (ParserConfigurationException e) {
			log.error(e.getMessage(), e);
		}

	}

	/**
	 * Questo metodo viene utilizzato per esportare il foglio xml in InputStream
	 * 
	 * @return Input Stream del foglio Xml
	 * @throws SOAPException
	 */
	@SuppressWarnings("unused")
	public InputStream toInputStream() throws SOAPException {
		ByteArrayOutputStream out = null;

		out = new ByteArrayOutputStream();
		printOutputStream(out);
		log.info("\n"+"Xml Output:\n" + new String(out.toByteArray()) + "\n");
		if (out != null) {
			return new ByteArrayInputStream(out.toByteArray());
		} else {
			return null;
		}
	}

	/**
	 * Questo metodo viene utilizzato per la generazione del nodo del tag
	 * StyleSheet
	 * 
	 * @return Nodo Generato
	 * @throws SOAPException
	 */
	private MessageElement genStyleSheet() throws SOAPException {
		MessageElement styleSheets = null;
		String[] values = null;

		if (styleSheet != null) {
			styleSheets = new MessageElement();
			styleSheets.setName("styleSheets");
			convert.setSezione("styleSheets");

			for (int x = 0; x < styleSheet.size(); x++) {
				values = ((String) styleSheet.get(x)).split("#");
				if (values.length == 1)
					convert.addChildElement(styleSheets, "styleSheet",
							values[0], true);
				else
					convert.addChildElement(styleSheets, "styleSheet",
							values[0], "check", values[1], true, true);
			}
		}

		return styleSheets;
	}

	/**
	 * Questo metodo viene utilizzato per la generazione del nodo del tag
	 * JavaScript
	 * 
	 * @return Nodo Generato
	 * @throws SOAPException
	 */
	private MessageElement genJavaScript() throws SOAPException {
		MessageElement javaScripts = null;

		if (javaScript != null) {
			javaScripts = new MessageElement();
			javaScripts.setName("javaScripts");
			convert.setSezione("javaScripts");

			for (int x = 0; x < javaScript.size(); x++)
				convert.addChildElement(javaScripts, "javaScript",
						(String) javaScript.get(x), true);
		}

		return javaScripts;
	}

	/**
	 * @return the convert
	 */
	public ConvertToUTF8 getConvert() {
		return convert;
	}

	/**
	 * @return the convertUri
	 */
	public ConvertToURI getConvertUri() {
		return convertUri;
	}
}
