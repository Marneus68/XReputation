package services.references;

import java.util.ArrayList;
import java.util.List;

public class Reference {
	protected List<Reference> links; 
	
	protected String url;
	protected int quality;
	
	public Reference(String url, int quality) {
		links = new ArrayList<Reference>();
	}
	
	public String getUrl() {
		return url;
	}
	
	public int getQuality() {
		return quality;
	}
}
