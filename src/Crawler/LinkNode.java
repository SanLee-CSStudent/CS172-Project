package Crawler;

import java.net.MalformedURLException;
import java.net.URL;

public class LinkNode {
	public LinkNode(String link) throws MalformedURLException {
		removeRef(link);
		
		this.link = new URL(link);
		depth = 1;
	}
	
	public LinkNode(String link, int depth) throws MalformedURLException {
		removeRef(link);
		
		this.link = new URL(link);
		this.depth = depth;
	}
	
	// from the URL, this function decides whether URL is functional or not
	public boolean checkURL() {
		// if protocol is not http, return false
		if(!checkProtocol()) {
			return false;
		}
		// if the link is file rather than a link, return false
		if(!checkFiles()) {
			return false;
		}
		// if the link is pointing to non-edu sites, return false
		if(!checkHost()) {
			return false;
		}
		// if there exists an anchor, remove it
		return true;
	}
	
	// check if the link is in http protocol or not
	private boolean checkProtocol() {
		if(!link.getProtocol().equals("http")) {
			return false;
		}
		return true;
	}
	
	// check if the URL points to a file
	// since files other than html will not be parsed, they will be ignored by crawlers
	private boolean checkFiles() {
		if(!link.getFile().equals("")) {
			return false;
		}
		return true;
	}
	
	// check if the URL links to .edu hosts
	// if it does not contain .edu ignore it
	private boolean checkHost() {
		if(!link.getHost().contains(".edu")) {
			return false;
		}
		return true;
	}
	
	private String removeRef(String link){
		if(link.contains("#")) {
			return link.substring(0, link.indexOf("#"));
		}
		
		return link;
	}
	
	public String getLink() {
		return link.toString();
	}
	
	public int getDepth() {
		return depth;
	}
	
	private URL link;
	private int depth;
}
