package slacknotifications.teamcity;

import jetbrains.buildServer.BuildAgent;
import jetbrains.buildServer.LicenseNotGrantedException;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.agentPools.AgentPool;
import jetbrains.buildServer.serverSide.comments.Comment;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.User;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MockSBuildAgent implements SBuildAgent, BuildAgent {

	private String name;
	private String hostame;
	private String ipAddress;
	private int agentId;
	private String osVersion;
	private SRunningBuild sRunningBuild;

	public MockSBuildAgent(String agentName, String hostname, String ipAddress,
			int agentId, String osVersion) {
		this.name = agentName;
		this.hostame = hostname;
		this.ipAddress = ipAddress;
		this.agentId = agentId;
		this.osVersion = osVersion;
	}

	public String getAuthorizationToken() {
		// TODO Auto-generated method stub
		return null;
	}

	public Comment getAuthorizeComment() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getAvailableParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<RunType> getAvailableRunTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@NotNull
	@Override
	public Set<String> getAvailableRunTypeIds() {
		return null;
	}

	public List<SBuildType> getBuildConfigurationsBuilt() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getDefinedParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getHostAddress() {
		return this.ipAddress;
	}

	public String getHostName() {
		return this.hostame;
	}

	public Date getLastCommunicationTimestamp() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getOperatingSystemName() {
		return this.osVersion;
	}

	public String getPluginsSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@NotNull
	@Override
	public String getCommunicationProtocolDescription() {
		return null;
	}

	@NotNull
	@Override
	public String getCommunicationProtocolType() {
		return null;
	}

	public Date getRegistrationTimestamp() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRunningBuild(SRunningBuild sRunningBuild){
		this.sRunningBuild = sRunningBuild;
	}
	
	public SRunningBuild getRunningBuild() {
		return this.sRunningBuild;
	}

	public Comment getStatusComment() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUnregistrationComment() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAuthorized() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isOutdated() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPluginsOutdated() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRegistered() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean ping() {
		// TODO Auto-generated method stub
		return false;
	}

	public void releaseSources() {
		// TODO Auto-generated method stub

	}

	public void releaseSources(SBuildType arg0) {
		// TODO Auto-generated method stub

	}

	public void setAuthorized(boolean arg0, SUser arg1, String arg2)
			throws LicenseNotGrantedException {
		// TODO Auto-generated method stub

	}

	public void setEnabled(boolean arg0, SUser arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	public boolean stopBuild(User arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public int getCpuBenchmarkIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAgentPoolId() {
		return 0;
	}

	public int getId() {
		return this.agentId;
	}

	public String getName() {
		return this.name;
	}

	public boolean isUpgrading() {
		// TODO Auto-generated method stub
		return false;
	}

	public int compareTo(BuildAgent o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<String> getAvailableVcsPlugins() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getBuildParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getConfigurationParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isCaseInsensitiveEnvironment() {
		// TODO Auto-generated method stub
		return false;
	}

	public List<SFinishedBuild> getBuildHistory(User arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getAgentStatusRestoringTimestamp() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean getAgentStatusToRestore() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getIdleTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@NotNull
	@Override
	public String describe(boolean b) {
		return null;
	}

	@NotNull
	@Override
	public AgentPool getAgentPool() {
		return null;
	}

	@Override
	public boolean isCloudAgent() {
		return false;
	}

	@Override
	public boolean canStartBuildIfAgentOutdated() {
		return false;
	}

	public void setEnabled(boolean arg0, SUser arg1, String arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	
	// From tc 7.1

	@Override
	public int getAgentTypeId() {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
