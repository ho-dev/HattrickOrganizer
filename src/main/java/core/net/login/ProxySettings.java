package core.net.login;

/**
 * Value object for proxy settings.
 * 
 */
public class ProxySettings {

	private String proxyHost;
	private int proxyPort;
	private String username;
	private String password;
	private boolean authenticationNeeded;
	private boolean useProxy;

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public boolean isAuthenticationNeeded() {
		return authenticationNeeded;
	}

	public void setAuthenticationNeeded(boolean authenticationNeeded) {
		this.authenticationNeeded = authenticationNeeded;
	}

	public boolean isUseProxy() {
		return useProxy;
	}

	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
