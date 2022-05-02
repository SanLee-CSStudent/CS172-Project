package Crawler;

import java.io.IOException;
import java.util.PriorityQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Crawler {
	
	public Crawler() {
		this.frontier = new PriorityQueue<String>();
	}
	
	public Crawler(int depth) {
		this.RECURSION_DEPTH = depth;
	}
	
	public void crawl() {
		// add seed link to frontier
		frontier.add("");
		
		// run as long as frontier has some links to crawl
		while(!frontier.isEmpty()) {
			String link = frontier.poll();
			try {
				Document doc = Jsoup.connect(link).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// any data structure implementing Queue should work
	private PriorityQueue<String> frontier;
	private int RECURSION_DEPTH = 5;
}
