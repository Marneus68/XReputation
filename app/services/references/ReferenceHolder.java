package services.references;

import java.util.ArrayList;
import java.util.List;
import services.persons.User;

public class ReferenceHolder {
	protected User user;
	protected List<Reference> refs;
	
	public ReferenceHolder(User u) {
		refs = new ArrayList<Reference>();
		user = u;
	}
	
	public ReferenceHolder(User u, String xmlFile) {
		this(u);
		loadFromXML(xmlFile);
	}
	
	public User getUser() {
		return user;
	}
	
	public void addReference(Reference r) {
		refs.add(r);
	}
	
	public void addReference(String url) {
		refs.add(new Reference(url, 0));
	}
	
	public void addReferenceWithBonus(String url, int bonus) {
		refs.add(new Reference(url, bonus));
	}
	
	public void saveToXML() {
		
	}
	
	public void loadFromXML(String xmlFile) {
		
	}
}
