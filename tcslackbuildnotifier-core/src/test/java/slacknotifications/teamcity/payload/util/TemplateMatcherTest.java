package slacknotifications.teamcity.payload.util;

import static org.junit.Assert.*;

import org.junit.Test;

import slacknotifications.teamcity.payload.util.TemplateMatcher.VariableResolver;

public class TemplateMatcherTest {

	/*-------------------------------------------------[ Testing ]---------------------------------------------------*/

	@Test
	public void test() {
		
		

	        String test1 = (new TemplateMatcher("${", "}").replace("this is ${santhosh}ghgjh\n ${kumar} sdf ${tekuri}abc", new VariableResolver(){
	            @Override
	            public String resolve(String variable){
	                if(variable.equals("santhosh"))
	                    return null;
	                return variable.toUpperCase();
	            }
	        }));
	        
	        assertTrue(test1.equals("this is ${santhosh}ghgjh\n KUMAR sdf TEKURIabc"));

	        String test2 = (new TemplateMatcher("$").replace("this is $santhosh ghgjh\n $kumar sdf $tekuri\n$ abc", new VariableResolver(){
	            @Override
	            public String resolve(String variable){
	                return variable.toUpperCase();
	            }
	        }));
	        
	        assertTrue(test2.equals("this is SANTHOSH ghgjh\n KUMAR sdf TEKURI\n abc"));
	}

}
