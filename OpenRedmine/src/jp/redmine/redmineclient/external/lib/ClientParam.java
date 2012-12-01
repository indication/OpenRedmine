package jp.redmine.redmineclient.external.lib;

public class ClientParam{
	private boolean sllTrustAll;
	private String certKey;
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