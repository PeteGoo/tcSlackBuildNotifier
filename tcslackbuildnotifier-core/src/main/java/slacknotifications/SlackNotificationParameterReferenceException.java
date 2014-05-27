package slacknotifications;

@SuppressWarnings("serial")
public class SlackNotificationParameterReferenceException extends Exception {
	String key;

	public SlackNotificationParameterReferenceException(String key){
		super();
		this.key = key;
	}
	
	public String getKey(){
		return this.key;
	}
}
