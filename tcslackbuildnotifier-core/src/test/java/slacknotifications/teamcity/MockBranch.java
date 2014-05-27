package slacknotifications.teamcity;

import jetbrains.buildServer.serverSide.Branch;

public class MockBranch implements Branch {
	
	private String name = "refs/heads/master";
	private String displayName = "master";
	private boolean defaultBranch = false;

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isDefaultBranch() {
		return defaultBranch;
	}
}