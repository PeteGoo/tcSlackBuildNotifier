package slacknotifications;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SlackNotificationCollection {
	private Map<Integer, SlackNotification> slackNotifications;
	private static final Integer SYSTEM = 0;
	private static final Integer SLACKNOTIFICATION = 1;
	private static final Integer SLACKNOTIFICATION_ID = 2;
	private static final Integer SLACKNOTIFICATION_KEY = 3;
	private static final Integer SLACKNOTIFICATION_PARAMETER_ID = 4;
	private static final Integer SLACKNOTIFICATION_PARAMETER_KEY = 5;
	private Map <String, String> origParams; 
	
	public SlackNotificationCollection(Map<String, String> params) {
		slackNotifications = new HashMap<Integer, SlackNotification>();
		this.origParams = params;
		this.parseParams(params);
	}
	
	private String getValue(String paramKey) throws SlackNotificationParameterReferenceException {
		if (this.origParams.containsKey(paramKey)){
			String value = this.origParams.get(paramKey);
			if (value.startsWith("%") && value.endsWith("%")){
				return this.getValue(value.substring(1,value.length() - 1));
			} 
			return value;
		} else {
			throw new SlackNotificationParameterReferenceException(paramKey);
		}
	}
	private void parseParams(Map <String, String> params) {
		//slackNotifications.add(new SlackNotification("blah"));
        for (Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String,String> entry = (Map.Entry<String,String>) iterator.next();
            String name = (String)entry.getKey();
            String val = (String)entry.getValue();
            System.out.println(name + " .. " + val);
            String tokens[] = name.toLowerCase().split("\\.");
            // First check if it's one of our tokens.
            if ((tokens[SYSTEM].equals("system")) && (tokens[SLACKNOTIFICATION].equals("slackNotification"))
            		&& (this.canConvertToInt(tokens[SLACKNOTIFICATION_ID]))) {
            	// Check if we have already created a slacknotifications instance
            	if (slackNotifications.containsKey(this.convertToInt(tokens[SLACKNOTIFICATION_ID]))){
            		if (tokens[SLACKNOTIFICATION_KEY].equals("url")){
            			slackNotifications.get(this.convertToInt(tokens[SLACKNOTIFICATION_ID])).setUrl(val);
//            		} else if ((tokens[SLACKNOTIFICATION_KEY].equals("bitmask"))
//            				&& (this.canConvertToInt(val))){
//                		slackNotifications.get(this.convertToInt(tokens[SLACKNOTIFICATION_ID])).setTriggerStateBitMask(this.convertToInt(val));
            		} else if (tokens[SLACKNOTIFICATION_KEY].equals("enabled")){
            			slackNotifications.get(this.convertToInt(tokens[SLACKNOTIFICATION_ID])).setEnabled(val);
            		} else if (tokens[SLACKNOTIFICATION_KEY].equals("parameter")
            				&& (this.canConvertToInt(tokens[SLACKNOTIFICATION_PARAMETER_ID]))
            				&& (tokens[SLACKNOTIFICATION_PARAMETER_KEY].equals("name")))
            		{
            			try {
							String myVal = this.getValue("system.slacknotifications." + tokens[SLACKNOTIFICATION_ID] + ".parameter."
									+ tokens[SLACKNOTIFICATION_PARAMETER_ID] + ".value");

							slackNotifications.get(this.convertToInt(tokens[SLACKNOTIFICATION_ID])).addParam(val, myVal);
						} catch (SlackNotificationParameterReferenceException e) {
							slackNotifications.get(this.convertToInt(tokens[SLACKNOTIFICATION_ID])).setErrored(true);
							slackNotifications.get(this.convertToInt(tokens[SLACKNOTIFICATION_ID])).setErrorReason(
									"SlackNotification Listener: The configured slacknotifications parameter ("
									+ name + ") references an alternate non-existant parameter");
						}

            		}
            	} else {
	            	if (tokens[SLACKNOTIFICATION_KEY].equals("url")){
	            		SlackNotification wh = new SlackNotificationImpl(val);
	            		this.slackNotifications.put(this.convertToInt(tokens[SLACKNOTIFICATION_ID]), wh);
//            		} else if ((tokens[SLACKNOTIFICATION_KEY].equals("bitmask"))
//            				&& (this.canConvertToInt(val))){
//            			SlackNotification wh = new SlackNotificationImpl();
//                		wh.setTriggerStateBitMask(this.convertToInt(val)); 	 
//                		this.slackNotifications.put(this.convertToInt(tokens[SLACKNOTIFICATION_ID]), wh);
	            	} else if (tokens[SLACKNOTIFICATION_KEY].equals("enabled")){
	            		SlackNotification wh = new SlackNotificationImpl();
            			wh.setEnabled(true);
            			this.slackNotifications.put(this.convertToInt(tokens[SLACKNOTIFICATION_ID]), wh);
            		} else if (tokens[SLACKNOTIFICATION_KEY].equals("parameter")
            				&& (this.canConvertToInt(tokens[SLACKNOTIFICATION_PARAMETER_ID]))
            				&& (tokens[SLACKNOTIFICATION_PARAMETER_KEY].equals("name")))
            		{
            			try {
							String myVal = this.getValue("system.slacknotifications." + tokens[SLACKNOTIFICATION_ID] + ".parameter."
									+ tokens[SLACKNOTIFICATION_PARAMETER_ID] + ".value");
							SlackNotification wh = new SlackNotificationImpl();
							wh.addParam(val, myVal);
							this.slackNotifications.put(this.convertToInt(tokens[SLACKNOTIFICATION_ID]), wh);
							
						} catch (SlackNotificationParameterReferenceException e) {
							SlackNotification wh = new SlackNotificationImpl();
							wh.setErrored(true);
							wh.setErrorReason("SlackNotification Listener: The configured slacknotifications parameter ("
									+ name + ") references an alternate non-existant parameter");
							this.slackNotifications.put(this.convertToInt(tokens[SLACKNOTIFICATION_ID]), wh);
						}
            		}            			
            	}
            }
        }
	}
	
	public Map<Integer, SlackNotification> getSlackNotifications(){
		return this.slackNotifications;
	}
	
	public Collection<SlackNotification> getSlackNotificationsAsCollection(){
		return this.slackNotifications.values();
	}

	private Boolean canConvertToInt(String s){
		try{
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e){
			return false;
		}
	}
	
	private Integer convertToInt(String s){
		try{
			int myInt = Integer.parseInt(s);
			return myInt;
		} catch (NumberFormatException e){
			return null;
		}		
	}
}
