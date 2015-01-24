package slacknotifications.teamcity;

import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;

import java.util.Map;

public class MockRunType extends RunType {

	private String description = "Runner for Ant build.xml files";
	private String name = "Ant";
	private String type = "Ant";

	@Override
	public Map<String, String> getDefaultRunnerProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return description;
	}

	@Override
	public String getDisplayName() {
		return name;
	}

	@Override
	public String getEditRunnerParamsJspFilePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PropertiesProcessor getRunnerPropertiesProcessor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		return type ;
	}

	@Override
	public String getViewRunnerParamsJspFilePath() {
		// TODO Auto-generated method stub
		return null;
	}

}
