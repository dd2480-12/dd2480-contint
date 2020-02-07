package contint;

/**
 * A class to represent relevant data from payload given by the request from the server as JSON.
 */
public class Payload {
	String ref;
	Repository repository;
	
	class Repository {
		String clone_url;
	}
}
