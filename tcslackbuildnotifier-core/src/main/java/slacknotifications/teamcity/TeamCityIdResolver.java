package slacknotifications.teamcity;


import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;

import java.util.List;

public final class TeamCityIdResolver {
	
	private TeamCityIdResolver(){}
	
	public static String getBuildTypeId(SBuildType buildType){
		try {
			return buildType.getExternalId();
		} catch (NoSuchMethodError ex) {
			return buildType.getBuildTypeId();
		}
	}
	
	public static String getExternalBuildId(SBuildType buildType){
		try {
			return buildType.getExternalId();
		} catch (NoSuchMethodError ex) {
			return buildType.getBuildTypeId();
		}
	}
	
	public static String getExternalBuildIdOrNull(SBuildType buildType){
		try {
			return buildType.getExternalId();
		} catch (NoSuchMethodError ex) {
			return null;
		}
	}
	
	public static String getInternalBuildId(SBuildType buildType){
		try {
			return buildType.getInternalId();
		} catch (NoSuchMethodError ex) {
			return buildType.getBuildTypeId();
		}
	}
	
	public static String getInternalBuildIdOrNull(SBuildType buildType){
		try {
			return buildType.getInternalId();
		} catch (NoSuchMethodError ex) {
			return null;
		}
	}
	
	public static String getProjectId(SProject project){
		try {
			return project.getExternalId();
		} catch (NoSuchMethodError ex) {
			return project.getProjectId();
		}
	}

	public static String getInternalProjectId(SProject project){
		return project.getProjectId();
	}
	
	public static String getExternalProjectId(SProject project){
		try {
			return project.getExternalId();
		} catch (NoSuchMethodError ex) {
			return project.getProjectId();
		}
	}
	
	public static String getExternalProjectIdOrNull(SProject project){
		try {
			return project.getExternalId();
		} catch (NoSuchMethodError ex) {
			return null;
		}
	}

	/**
	 * Finds a TeamCity project in the ProjectManager by ProjectId.
	 * Uses findProjectByExternalId() if available, otherwise uses findProjectById()
	 * @param TeamCity projectManager instance
	 * @param projectId string
	 * @return TeamCity Project Config object
	 */
	public static SProject findProjectById(ProjectManager projectManager, String projectId) {
		try {
			return projectManager.findProjectByExternalId(projectId);
		} catch (NoSuchMethodError ex){
			return projectManager.findProjectById(projectId);
		}
	}
	
	/**
	 * Finds a TeamCity BuiltType in the ProjectManager by buildTypeId.
	 * Uses findBuildTypeByExternalId() if available, otherwise uses findBuildTypeById()
	 * @param ProjectManager instance
	 * @param buildTypeId string
	 * @return TeamCity BuildType config object
	 */
	public static SBuildType findBuildTypeById(ProjectManager projectManager, String buildTypeId) {
		try {
			return projectManager.findBuildTypeByExternalId(buildTypeId);
		} catch (NoSuchMethodError ex){
			return projectManager.findBuildTypeById(buildTypeId);
		}
	}
	
	/**
	 * Finds builds that belong the referenced project. Uses new method getOwnBuildTypes() if available.
	 * Does not find builds in sub-projects.
	 * @param project
	 * @return List of BuildTypes corresponding to what is configured in the project.
	 */
	public static List<SBuildType> getOwnBuildTypes(SProject project) {
		try {
			return project.getOwnBuildTypes();
		} catch (NoSuchMethodError ex){
			return project.getBuildTypes();
		}
	}

}
