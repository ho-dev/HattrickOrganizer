package core.net;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;
import org.scribe.model.Verb;

public class HattrickAPI extends DefaultApi10a{
	
	private static final String AUTHORIZATION_URL = "https://chpp.hattrick.org/oauth/authorize.aspx";

	@Override
	public String getAccessTokenEndpoint()
	{
		return "https://chpp.hattrick.org/oauth/access_token.ashx"; 
	}

	@Override
	public String getRequestTokenEndpoint()
	{
		return "https://chpp.hattrick.org/oauth/request_token.ashx";
	}

	@Override
	public Verb getAccessTokenVerb()
	{
		return Verb.GET;
	}

	@Override
	public Verb getRequestTokenVerb()
	{
		return Verb.GET;
	}

	@Override
	public String getAuthorizationUrl(Token requestToken)
	{
		return String.format(AUTHORIZATION_URL + "?oauth_token=%s", requestToken.getToken());
	}
	
	
}


