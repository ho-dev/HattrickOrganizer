package core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * News from HO site
 * 
 * @author Draghetto
 */
public class News {
	
	public static final int HO = 0;
	public static final int EPV =1;		
	public static final int PLUGIN = 2;
	public static final int MESSAGE = 3;
	public static final int RATINGS = 4;
	
	private int type = -1;
	private int id = -1;
	private String link;
	private float version = -1;
	private float minimumHOVersion = -1;
	
	private List<String> msg = new ArrayList<String>();
	

	public int getType() {
		return type;
	}

	public void setId(int i) {
		id = i;
	}

	public void setLink(String string) {
		link = string;
	}

	public void setType(int i) {
		type = i;
	}


	public void setVersion(float d) {
		version = d;
	}

	public void addMessage(String string) {
		msg.add(string);
	}
	
	public List<String> getMessages() {
		return msg;
	}

	public void setMinimumHOVersion(float d) {
		minimumHOVersion = d;
	}

	public int getId() {
		return id;
	}


	public float getVersion() {
		return version;
	}

	public String getLink() {
		return link;
	}

	public float getMinimumHOVersion() {
		return minimumHOVersion;
	}

}
