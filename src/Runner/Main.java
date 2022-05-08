package Runner;

import java.util.Arrays;

import Crawler.Crawler;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(Arrays.toString(args));
		Crawler crawler;
		if(validate(args)) {
			String seed = args[0];
			int numPages = Integer.parseInt(args[1]);
			int depth = Integer.parseInt(args[2]);
			String output = args[3];
			crawler = new Crawler(depth, numPages, seed, output);
		}
		else {
			System.out.println("Error occurred while reading inputs. Running in Default Settings...");
			System.out.println("seed: src/seed.txt, num-pages: 100, hops-away: 3, output-dir: src/pages/");
			crawler = new Crawler();
		}
		crawler.crawl();
	}
	
	public static boolean validate(String[] args) {
		if(args.length == 0) {
			return false;
		}
		
		if(!(args[0] instanceof String)) {
			System.out.println("Invalid file name");
			return false;
		}
		try {
			int numPages = Integer.parseInt(args[1]);
			if(numPages <= 0) {
				System.out.println("Number of pages cannot be negative");
				return false;
			}
			
			int depths = Integer.parseInt(args[2]);
			if(depths <= 0) {
				System.out.println("Depth cannot be negative");
				return false;
			}
		} catch(NumberFormatException e) {
			System.out.println("Invalid input type");
			return false;
		}
		if(!(args[0] instanceof String)) {
			System.out.println("Invalid output direction");
			return false;
		}
		
		return true;
	}
}
