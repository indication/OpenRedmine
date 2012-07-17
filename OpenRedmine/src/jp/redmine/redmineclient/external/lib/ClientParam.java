package jp.redmine.redmineclient.external.lib;

public class ClientParam{
	private boolean sllTrustAll;
	private String certKey;
	private int httpport = 80;
	private int httpsport = 443;
	private int timeout = 10000;

	/**
	 * @param sllTrustAll セットする sllTrustAll
	 */
	public void setSLLTrustAll(boolean sllTrustAll) {
		this.sllTrustAll = sllTrustAll;
	}
	/**
	 * @return sllTrustAll
	 */
	public boolean isSLLTrustAll() {
		return sllTrustAll;
	}
	/**
	 * @param certKey セットする certKey
	 */
	public void setCertKey(String certKey) {
		this.certKey = certKey;
	}
	/**
	 * @return certKey
	 */
	public String getCertKey() {
		return certKey;
	}
	/**
	 * @param httpport セットする httpport
	 */
	public void setHttpPort(int httpport) {
		this.httpport = httpport;
	}
	/**
	 * @return httpport
	 */
	public int getHttpPort() {
		return httpport;
	}
	/**
	 * @param httpsport セットする httpsport
	 */
	public void setHttpsPort(int httpsport) {
		this.httpsport = httpsport;
	}
	/**
	 * @return httpsport
	 */
	public int getHttpsPort() {
		return httpsport;
	}
	/**
	 * @param timeout セットする timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	/**
	 * @return timeout
	 */
	public int getTimeout() {
		return timeout;
	}
}