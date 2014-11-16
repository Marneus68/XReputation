package crawlers;

import references.ReferenceHolder;

public class ExampleCrawler extends Crawler {

	public ExampleCrawler(ReferenceHolder rh) {
		super(rh);
		setServiceName("Example");
	}

	@Override
	protected void crawl() {
		System.out.print("Crawling \"" + serviceName + "\"");
	}

	@Override
	protected void complain() {
		System.out.println("No url provided for this service. Can't crawl \"" + serviceName + "\" service.");
	}

}
