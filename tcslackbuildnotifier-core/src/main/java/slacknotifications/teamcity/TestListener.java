package slacknotifications.teamcity;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.ResponsibilityInfo;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.tests.TestName;

public class TestListener extends BuildServerAdapter {
	
	private SBuildServer myBuildServer;

	public TestListener(SBuildServer sBuildServer, ProjectSettingsManager settings) {
		myBuildServer = sBuildServer;
		logit("TestListener :: Starting");
	}

	public void register(){
		myBuildServer.addListener(this);
		logit("TestListener :: Registering");
	}

	/** No longer used in TC 7.x
	 * 
	 * @param runningBuild
	 * @param buildFailed
	 */
	public void beforeBuildFinish(SRunningBuild runningBuild,
			boolean buildFailed) {
		logit("beforeBuildFinish(SRunningBuild runningBuild,boolean buildFailed)");
	}

	@Override
	public void beforeBuildFinish(SRunningBuild runningBuild) {
		logit("beforeBuildFinish(SRunningBuild runningBuild)");
	}

	@Override
	public void buildFinished(SRunningBuild build) {
		logit("buildFinished(SRunningBuild build)");
	}

	@Override
	public void buildInterrupted(SRunningBuild build) {
		logit("buildInterrupted(SRunningBuild build)");
	}

	@Override
	public void buildStarted(SRunningBuild build) {
		logit("buildStarted(SRunningBuild build)");
	}

	public void responsibleChanged(SBuildType bt, ResponsibilityInfo oldValue,
			ResponsibilityInfo newValue, boolean isUserAction) {
		logit("responsibleChanged(SBuildType bt, ResponsibilityInfo oldValue,	ResponsibilityInfo newValue, boolean isUserAction)");
		String oldUser = "Nobody";
		String newUser = "Nobody";
		try {
			oldUser = oldValue.getResponsibleUser().getDescriptiveName();
		} catch (Exception e) {}
		try {
			newUser = newValue.getResponsibleUser().getDescriptiveName();
		} catch (Exception e) {}
		logit("Build " + bt.getFullName().toString()
				+ " has changed responsibility from " 
				+ oldUser + " to " + newUser);
	}
	
	/**   
	 * @param bt
	 * @param oldValue
	 * @param newValue
	 * @since 7.0
	 */
	@Override
	public void responsibleChanged(@NotNull SBuildType bt,
            @NotNull ResponsibilityEntry oldValue,
            @NotNull ResponsibilityEntry newValue){
		logit("responsibleChanged(@NotNull SBuildType bt, @NotNull ResponsibilityEntry oldValue, @NotNull ResponsibilityEntry newValue)");
	}
	
	@Override
	public void responsibleChanged(SProject project,
			Collection<TestName> testNames, ResponsibilityEntry entry,
			boolean isUserAction) {
		logit("responsibleChanged(SProject project, Collection<TestName> testNames, ResponsibilityEntry entry, boolean isUserAction)");
	}

	@Override
	public void responsibleChanged(SProject project,
			TestNameResponsibilityEntry oldValue,
			TestNameResponsibilityEntry newValue, boolean isUserAction) {
		logit("responsibleChanged(SProject project, TestNameResponsibilityEntry oldValue, TestNameResponsibilityEntry newValue, boolean isUserAction)");
	}

	private void logit(String s){
		Loggers.SERVER.info("#####################################################################");
		Loggers.SERVER.info("# " + s);
		Loggers.SERVER.info("#####################################################################");
	}
	
	
}
