package persons;

import java.util.HashMap;
import java.util.Map;

public class User extends Person {
	protected Map<String, String> credentials;
	
	public User(String fname, String lname) {
		super(fname, lname);
		credentials = new HashMap<String, String>();
	}
	
	public User(String fname, String lname, Map<String, String> creds) {
		this(fname, lname);
		credentials.clear();
		credentials = creds;
	}

	public void addCredential(String service, String url) {
		credentials.put(service, url);
	}
	
	public Map<String, String> getCredentials() {
		return credentials;
	}
	
	public String getCredential(String service) {
		if (credentials.containsKey(service))
			return credentials.get(service);
		else
			return "";
	}
	
}
