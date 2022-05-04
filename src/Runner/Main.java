package Runner;

import Crawler.Crawler;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Crawler crawler = new Crawler(10, 1000000);
		crawler.crawl();
	}
}
