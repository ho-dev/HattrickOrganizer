package core.net;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.Verb;


public class HattrickAPI extends DefaultApi10a {
	
	private static final String AUTHORIZATION_URL = "https://chpp.hattrick.org/oauth/authorize.aspx";

	public HattrickAPI() {}

	private static class InstanceHolder {
		private static final HattrickAPI INSTANCE = new HattrickAPI();
	}

	public static HattrickAPI instance() {
		return InstanceHolder.INSTANCE;
	}

	@Override
	public String getAccessTokenEndpoint()
	{
		return "https://chpp.hattrick.org/oauth/access_token.ashx"; 
	}

	@Override
	protected String getAuthorizationBaseUrl() {
		return AUTHORIZATION_URL;
	}

	@Override
	public String getRequestTokenEndpoint()
	{
		return "https://chpp.hattrick.org/oauth/request_token.ashx";
	}


//	@Override
//	public Verb getAccessTokenVerb()
//	{
//		return Verb.GET;
//	}
//
//	@Override
//	public Verb getRequestTokenVerb()
//	{
//		return Verb.GET;
//	}



}


