package contint;

/**
 * A class to represent relevent data from payload given by the request from the server. 
 */
public class Payload {
	String ref;
	Repository repository;
	
	class Repository {
		String clone_url;
	}
}
