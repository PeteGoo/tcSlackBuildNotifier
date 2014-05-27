package slacknotifications.teamcity.settings;

import org.jdom.Element;

public class CustomMessageTemplate {
	String templateType;
	String templateText;
	boolean enabled;
	
	public static final String XML_ELEMENT_NAME = "custom-template";
	public static final String TYPE = "type";
	public static final String TEMPLATE = "template";
	public static final String ENABLED = "enabled";
	
	public static CustomMessageTemplate create(String templateType, String template, boolean enabled){
		CustomMessageTemplate t = new CustomMessageTemplate();
		t.templateType = templateType;
		t.templateText = template;
		t.enabled = enabled;
		return t;
	}

	public Element getAsElement() {
		Element e = new Element(XML_ELEMENT_NAME);
			e.setAttribute(TYPE, this.templateType);
			e.setAttribute(TEMPLATE, this.templateText);
			e.setAttribute(ENABLED, String.valueOf(this.enabled));
		return e;
	}
}