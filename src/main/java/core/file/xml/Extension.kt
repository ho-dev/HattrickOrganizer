package core.file.xml;


public class Extension {

	private float release = -1;
	private float minimumHOVersion = -1;
	
	public float getMinimumHOVersion() {
		return minimumHOVersion;
	}

	public void setMinimumHOVersion(float d) {
		minimumHOVersion = d;
	}


	public float getRelease() {
		return release;
	}

	public void setRelease(float d) {
		release = d;
	}

}
