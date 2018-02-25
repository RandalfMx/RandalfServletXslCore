/**
 * 
 */
package mx.randalf.moduli.servlet.core;

import mx.randalf.configuration.Configuration;
import mx.randalf.configuration.exception.ConfigurationException;
import mx.randalf.converter.xsl.ConverterXsl;
import mx.randalf.converter.xsl.exception.ConvertXslException;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPException;

import mx.randalf.moduli.servlet.core.exception.StdModuliException;
import mx.randalf.moduli.servlet.core.exception.StdNotificheException;
import mx.randalf.moduli.standard.xml.DatiXml;
import mx.randalf.moduli.standard.xml.exception.DatiXmlException;

import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;

/**
 * Questa classe viene utilizzata per indcare le operazione Stdandard da
 * utilizzare per i singoli Moduli
 * 
 * @author Massimiliano Randazzo
 *
 */
public abstract class StdModuliCore {

	/**
	 * Questa variabile viene utilizzata per gestire i nodi del login
	 */
	protected MessageElement element = null;

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private Logger log = Logger.getLogger(StdModuliCore.class);

	/**
	 * Questa variabile viene utilizzata per gestire le informazioni provenienti
	 * dal Client
	 */
	protected HttpServletRequest request = null;

	/**
	 * Questa variabile viene utilizzata per gestire il risultato verso il
	 * Client
	 */
	protected HttpServletResponse response = null;

	/**
	 * Questa variabile viene utilizzata per indicare il nome del foglio Xsl da
	 * utilizzare per la conversione
	 */
	protected String fileXsl = "";

	/**
	 * Questo metodo viene utilizzato per gestire la creazione del foglio Xml di
	 * risposta
	 */
	protected DatiXml datiXml = null;

	/**
	 * Questa varabile viene utilizzata per indicare il messaggio di Errore
	 */
	private String msgErr = null;

	/**
	 * Questa varabile viene utilizzata per indicare il messaggio di Errore
	 */
	private String sqlErr = null;

	/**
	 * Questa variabile viene utilizzata per gestire la path Xsl
	 */
	protected String pathXsl = "";

	/**
	 * Questa variabile viene utilizzata per indicare il nome del Modulo
	 */
	protected String modulo = null;

	/**
	 * Questa variabile viene utilizzata per indicare l'azione del modulo
	 */
	protected String azione = null;

	/**
	 * Variabile utilizzata per indicare il prefisso nel file di configurazione
	 * per le configurazioni relative l'applicativo
	 */
	protected String prefix = null;

	/**
	 * Costruttore
	 * 
	 * @throws ServletException
	 */
	public StdModuliCore(String prefix) throws ServletException {
		String value = null;
		String st[] = null;

		try {
			this.prefix = prefix;
			datiXml = new DatiXml(new DatiXmlException());

			value = Configuration.getValue(prefix + ".default.xsl.styleSheet");
			if (value != null) {
				st = value.split(",");
				for (int x = 0; x < st.length; x++) {
					datiXml.addStyleSheet(st[x]);
				}
			}

			value = Configuration.getValue(prefix + ".default.xsl.javaScript");
			if (value != null) {
				st = value.split(",");
				for (int x = 0; x < st.length; x++) {
					datiXml.addJavaScript(st[x]);
				}
			}
		} catch (ConfigurationException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(), e);
		}
	}

	public void esegui(HttpServletRequest request, HttpServletResponse response, String pathXsl)
			throws ServletException, IOException {
		esegui(request, response, pathXsl, null);
	}

	/**
	 * Questo metodo viene utilizzato per eseguire le operazioni relative al
	 * modulo selezionato
	 * 
	 * @param request
	 *            Questa variabile viene utilizzata per gestire le informazioni
	 *            provenienti dal Client
	 * @param response
	 *            Questa variabile viene utilizzata per gestire il risultato
	 *            verso il Client
	 * @param pathXsl
	 *            Questa variabile viene utilizzata per indicare la path dove
	 *            introvare i file Xsl
	 * @throws ServletException
	 * @throws IOException
	 */
	public void esegui(HttpServletRequest request, HttpServletResponse response, String pathXsl, String modulo)
			throws ServletException, IOException {

		this.request = request;
		this.response = response;
		this.pathXsl = pathXsl;

		try {
			log.debug("\n"+"init");
			this.modulo = modulo;
			init();
			if (modulo != null)
				datiXml.getConvert().addChildElement(element, "modulo", modulo, true);

			azione = this.request.getParameter("azione");
			log.debug("\n"+"azione: " + azione);
			if (this.request.getParameterValues("azione") != null)
				for (int x = 0; x < this.request.getParameterValues("azione").length; x++)
					log.info("\n"+"azione: " + this.request.getParameterValues("azione")[x]);

			if (azione == null)
				azione = "show";

			log.debug("\n"+"azione - new : " + azione);
			if (azione.equals("show")) {
				log.debug("\n"+"show");
				show();
			} else if (azione.equals("result")) {
				log.debug("\n"+"result");
				result();
			} else if (azione.equals("edit")) {
				log.debug("\n"+"edit");
				edit();
			} else if (azione.equals("write")) {
				log.debug("\n"+"write");
				write();
			}

			log.debug("\n"+"extend");
			extend();
		} catch (StdModuliException e) {
			log.error(e.getMessage(), e);
			this.setSqlErr(e.getMessage());
		} catch (StdNotificheException e) {
			this.setSqlErr(e.getMessage());
		} catch (NoClassDefFoundError e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage());
		} catch (SOAPException e) {
			log.error(e.getMessage(),e);
			throw new ServletException(e.getMessage());
		} finally {
			endEsegui(pathXsl);
		}
	}

	/**
	 * Questo metodo viene utilizzato per eseguire le attivit?? finali per la
	 * visualizzazione del foglio xls
	 * 
	 * @param pathXsl
	 *            Path relativo alla posizione del foglio Xsl
	 * @throws ServletException
	 *             Eccezione Servlet
	 */
	protected void endEsegui(String pathXsl) throws ServletException {
		MessageElement element = null;
		try {

			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			log.debug("\n"+"msgErr: " + msgErr);
			if (msgErr != null) {
				element = new MessageElement();
				element.setName("msgErr");
				datiXml.getConvert().setValue(element, msgErr);
				datiXml.addElement(element);
			}
			log.debug("\n"+"sqlErr: " + sqlErr);
			if (sqlErr != null) {
				element = new MessageElement();
				element.setName("sqlErr");
				datiXml.getConvert().setValue(element, sqlErr);
				datiXml.addElement(element);
			}
			log.debug("\n"+"convert: " + pathXsl + File.separator + fileXsl);
			ConverterXsl.convertXsl(pathXsl + File.separator + fileXsl, datiXml.toInputStream(),
					response.getOutputStream());
		} catch (ConvertXslException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage());
		} catch (SOAPException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage());
		} catch (NoClassDefFoundError e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage());
		}
	}

	/**
	 * Questo metodo &egrave; da implementare per la inizializzazione della
	 * variabili di sistema
	 * 
	 * @throws ServletException
	 */
	protected void init() throws ServletException {
		String line = null;
		String[] st = null;

		try {
			if (Configuration.getValue(prefix + ".modulo." + this.request.getServerName() + "." + modulo,
					"xsl") != null) {
				this.fileXsl = Configuration.getValue(prefix + ".modulo." + this.request.getServerName() + "." + modulo,
						"xsl");
			} else {
				this.fileXsl = Configuration.getValue(prefix + ".modulo.ALL." + modulo, "xsl");
			}

			if (Configuration.getValue(prefix + ".modulo." + this.request.getServerName() + "." + modulo,
					"title") != null) {
				datiXml.setTitle(Configuration
						.getValue(prefix + ".modulo." + this.request.getServerName() + "." + modulo, "title"));
			} else {
				datiXml.setTitle(Configuration.getValue(prefix + ".modulo.ALL." + modulo, "title"));
			}

			if (Configuration.getValue(prefix + ".modulo." + this.request.getServerName() + "." + modulo,
					"styleSheet") != null) {
				line = Configuration.getValue(prefix + ".modulo." + this.request.getServerName() + "." + modulo,
						"styleSheet");
			} else {
				line = Configuration.getValue(prefix + ".modulo.ALL." + modulo, "styleSheet");
			}
			if (line != null) {
				st = line.split(",");
				for (int x = 0; x < st.length; x++) {
					datiXml.addStyleSheet(st[x]);
				}
			}

			if (Configuration.getValue(prefix + ".modulo." + this.request.getServerName() + "." + modulo,
					"javaScript") != null) {
				line = Configuration.getValue(prefix + ".modulo." + this.request.getServerName() + "." + modulo,
						"javaScript");
			} else {
				line = Configuration.getValue(prefix + ".modulo.ALL." + modulo, "javaScript");
			}
			if (line != null) {
				st = line.split(",");
				for (int x = 0; x < st.length; x++) {
					datiXml.addJavaScript(st[x]);
				}
			}

			element = new MessageElement();
			element.setName(modulo);
		} catch (ConfigurationException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(), e);
		}
	}

	/**
	 * Questo metodo &egrave; da implementare per la gestione delle azoni
	 * supplementari del modulo selezionato
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	protected abstract void extend() throws StdNotificheException, StdModuliException, ServletException, IOException;

	/**
	 * Questo metodo &egrave; da implementare per l'azione show del modulo
	 * selezionato
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void show() throws ServletException, IOException {
		log.debug("\n"+"StdModuli - show");
		show(null);
		log.debug("\n"+"SqlErr: " + this.getSqlErr());
		log.debug("\n"+"element: " + element);
		if ((this.getSqlErr() == null || this.getSqlErr().equals("")) && element != null) {
			log.debug("\n"+"AddElement");
			datiXml.addElement(element);
		}
	}

	protected abstract void show(String id) throws ServletException, IOException;

	/**
	 * Questo metodo &egrave; da implementare per l'azione edit del modulo
	 * selezionato
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	protected abstract void edit() throws ServletException, IOException;

	/**
	 * Questo metodo &egrave; da implementare per l'azione result del modulo
	 * selezionato
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	protected abstract void result() throws ServletException, IOException;

	/**
	 * Questo metodo &egrave; da implementare per l'azione write del modulo
	 * selezionato
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	protected abstract void write() throws ServletException, IOException, StdModuliException, StdNotificheException;

	/**
	 * Questo metodo viene utilizzato per indicare il messaggio di errore
	 * dell'applicazione
	 * 
	 * @param msgErr
	 *            the msgErr to set
	 */
	public void setMsgErr(String msgErr) {
		this.msgErr = msgErr;
	}

	/**
	 * Questo metodo viene utilizzato per indicare il messaggio di errore sql
	 * dell'applicazione
	 * 
	 * @param msgErr
	 *            the msgErr to set
	 */
	public void setSqlErr(String sqlErr) {
		this.sqlErr = sqlErr;
	}

	/**
	 * Questo metodo viene utilizzato per restituire il messqggio di Errore Sql
	 * dell'applicazione
	 * 
	 * @return
	 */
	public String getSqlErr() {
		return sqlErr;
	}

	/**
	 * @return the datiXml
	 */
	public DatiXml getDatiXml() {
		return datiXml;
	}

	protected boolean checkUsage(String usage) throws ServletException {
		String ipUsage = null;
		String ipClient = null;
		String[] ipUsages = null;
		String[] ipUsages2 = null;
		String[] ipClients = null;
		boolean ris = false;
		boolean ris2 = false;

		try {
			if (Configuration.getValue(prefix + ".modulo." + this.request.getServerName() + "." + modulo,
					"usage" + usage) != null) {
				ipUsage = Configuration.getValue(prefix + ".modulo." + this.request.getServerName() + "." + modulo,
						"usage" + usage);
			} else {
				ipUsage = Configuration.getValue(prefix + ".modulo.ALL." + modulo, "usage" + usage);
			}

			if (ipUsage != null) {
				ipClient = getClientIpAddr();

				ipClients = ipClient.split("\\.");
				ipUsages = ipUsage.split(",");
				for (int i = 0; i < ipUsages.length; i++) {
					ipUsages2 = ipUsages[i].trim().split("\\.");
					ris2 = true;
					for (int x = 0; x < ipClients.length; x++) {
						if (x < ipUsages2.length) {
							if (!ipUsages2[x].trim().equals("*")) {
								if (!ipUsages2[x].trim().equalsIgnoreCase(ipClients[x].trim())) {
									ris2 = false;
								}
							}
						}
					}
					if (ris2) {
						ris = true;
						break;
					}
				}
			}
		} catch (ConfigurationException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(), e);
		}
		return ris;
	}

	/**
	 * Questo metodo viene utilizzato per ricavare l'indirizzo IP del client
	 * 
	 * @return Indirizzo IP del client
	 */
	protected String getClientIpAddr() {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
