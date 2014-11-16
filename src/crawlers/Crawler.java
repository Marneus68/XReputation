package crawlers;

import persons.User;
import references.ReferenceHolder;

public abstract class Crawler {
	protected ReferenceHolder refHold;
	protected User user;
	protected String serviceName = "";

	public Crawler(ReferenceHolder rh) {
		refHold = rh;
		user = refHold.getUser();
	}
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	protected boolean canCrawl() {
		return user.getCredential(serviceName) != "";
	}
	
	public void startCrawl() {
		if(canCrawl()) {
			crawl();
		} else {
			complain();
		}
	}
	
	abstract protected void crawl();
	abstract protected void complain(); 
}
