/**
 * 
 */
package slacknotifications.teamcity.payload.format;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import slacknotifications.teamcity.Loggers;
import slacknotifications.teamcity.payload.SlackNotificationPayload;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;


public class SlackNotificationPayloadNameValuePairs extends SlackNotificationPayloadGeneric implements SlackNotificationPayload {
	
	public SlackNotificationPayloadNameValuePairs(SlackNotificationPayloadManager manager) {
		super(manager);
	}

	Integer rank = 100;
	String charset = "UTF-8";
	
	public void setPayloadManager(SlackNotificationPayloadManager slackNotificationPayloadManager){
		myManager = slackNotificationPayloadManager;
	}
	
	public void register(){
		myManager.registerPayloadFormat(this);
	}
	
	public String getFormatDescription() {
		return "Name Value Pairs";
	}

	public String getFormatShortName() {
		return "nvpairs";
	}

	public String getFormatToolTipText() {
		return "Send the payload as a set of normal Name/Value Pairs";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String getStatusAsString(SlackNotificationPayloadContent content){
		String returnString = ""; 
		
		Map<String, String> contentMap = null;
		try {
			 contentMap = BeanUtils.describe(content);
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (contentMap != null && contentMap.size() > 0){
			
			for(Iterator<String> param = contentMap.keySet().iterator(); param.hasNext();)
			{
				String key = param.next();
				String pair = "&";
				try {
					if (key != null){
						pair += URLEncoder.encode(key, this.charset);
						if (contentMap.get(key) != null){
							pair += "=" + URLEncoder.encode((String)contentMap.get(key), this.charset);
						} else {
							pair += "=" + URLEncoder.encode("null", this.charset);
						}
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Need a better way to handle to string.
					e.printStackTrace();
					pair = "";
				} catch (ClassCastException e){
					// TODO Need a better way to handle to string.
					e.printStackTrace();
					pair = "";
				}
				returnString += pair;
				//Loggers.SERVER.debug(this.getClass().getSimpleName() + ": payload is " + returnString);
			}
		
		}
		
		if (content != null && content.getExtraParameters() != null  && content.getExtraParameters().size() > 0){
			
			for(Iterator<String> param = content.getExtraParameters().keySet().iterator(); param.hasNext();)
			{
				String key = param.next();
				String pair = "&";
				try {
					if (key != null){
						System.out.println(this.getClass().getSimpleName() + ": key is " + key);
						pair += URLEncoder.encode(key, this.charset);
						System.out.println(this.getClass().getSimpleName() + ": value is " + (String)content.getExtraParameters().get(key));
						if (content.getExtraParameters().get(key) != null){
							pair += "=" + URLEncoder.encode((String)content.getExtraParameters().get(key), this.charset);
						} else {
							pair += "=" + URLEncoder.encode("null", this.charset);
						}
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Need a better way to handle to string.
					e.printStackTrace();
					pair = "";
				} catch (ClassCastException e){
					// TODO Need a better way to handle to string.
					e.printStackTrace();
					pair = "";
				}
				returnString += pair;
				Loggers.SERVER.debug(this.getClass().getSimpleName() + ": payload is " + returnString);
			}
		}
		
		Loggers.SERVER.debug(this.getClass().getSimpleName() + ": payload is " + returnString);
		if (returnString.length() > 0){
			return returnString.substring(1);
		} else {
			return returnString;
		}
	}



	public String getContentType() {
		return "application/x-www-form-urlencoded";
	}

	public Integer getRank() {
		return this.rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getCharset() {
		return this.charset;
	}



	
}
