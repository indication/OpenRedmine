package jp.redmine.redmineclient.external.lib;

public class AuthenticationParam {

	private String address;
	private int port;
	private String id;
	private String pass;
	/**
	 * @param address セットする address
	 */
	public void setAddress(String host) {
		this.address = host;
	}
	/**
	 * @return address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param port セットする port
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/**
	 * @return port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @param id セットする id
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param pass セットする pass
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}
	/**
	 * @return pass
	 */
	public String getPass() {
		return pass;
	}
}
