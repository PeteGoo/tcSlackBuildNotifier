package slacknotifications.teamcity;

import com.intellij.util.containers.HashMap;
import jetbrains.buildServer.BuildProject;
import jetbrains.buildServer.BuildTypeDescriptor.CheckoutType;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.parameters.ParametersProvider;
import jetbrains.buildServer.parameters.ValueResolver;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.identifiers.DuplicateExternalIdException;
import jetbrains.buildServer.users.User;
import jetbrains.buildServer.vcs.DuplicateVcsRootNameException;
import jetbrains.buildServer.vcs.SVcsRoot;
import jetbrains.buildServer.vcs.UnknownVcsException;
import jetbrains.buildServer.vcs.VcsRootInstance;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class MockSProject implements SProject {

	private String name;
	private String description;
	private String projectId;
	private String projectExternalId;
	private File configDirectory;
	private Status status;
	private SBuildType buildType;
	private SProject parentProject;
	private Map<String,SBuildType> buildTypes = new HashMap<String, SBuildType>();
	private List<SProject> parentPath = new ArrayList<SProject>();
	private List<SProject> childProjects = new ArrayList<SProject>();

	public MockSProject(String name, String description, String projectId, String projectExternalId, 
						SBuildType buildType)
	{
		this.name = name;
		this.description = description;
		this.projectId = projectId;
		this.projectExternalId = projectExternalId;
		this.buildType = buildType;
		this.parentPath.add(this);
		addANewBuildTypeToTheMock(buildType);
	}
	
	public boolean containsBuildType(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public SBuildType createBuildType(SBuildType arg0, String arg1,
			CopyOptions arg2) throws MaxNumberOfBuildTypesReachedException {
		// TODO Auto-generated method stub
		return null;
	}

	public SBuildType createBuildType(String arg0, String arg1, int arg2,
			CheckoutType arg3) throws DuplicateBuildTypeNameException,
			MaxNumberOfBuildTypesReachedException {
		// TODO Auto-generated method stub
		return null;
	}

	public SBuildType createBuildType(SBuildType arg0, String arg1,
			boolean arg2, boolean arg3)
			throws MaxNumberOfBuildTypesReachedException {
		// TODO Auto-generated method stub
		return null;
	}

	public SBuildType findBuildTypeById(String btName) {
		return buildTypes.get(btName);
	}

	@Nullable
	@Override
	public SBuildType findBuildTypeByExternalId(@Nullable String s) {
		return null;
	}

	public void addANewBuildTypeToTheMock(SBuildType build){
		this.buildTypes.put(build.getBuildTypeId(), build);
	}
	
	public SBuildType findBuildTypeByName(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public File getArtifactsDirectory() {
		// TODO Auto-generated method stub
		return this.buildType.getArtifactsDirectory();
	}

	public List<SBuildType> getBuildTypes() {
		return new ArrayList<SBuildType>(this.buildTypes.values());
	}

	public File getConfigDirectory() {
		return this.configDirectory;
	}

	public File getConfigurationFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@NotNull
	@Override
	public File getConfigurationFile(@NotNull File file) {
		return null;
	}

	public List<SVcsRoot> getVcsRoots() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasBuildTypes() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInModel() {
		// TODO Auto-generated method stub
		return false;
	}

	public void persist() throws PersistFailedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void persist(@NotNull ConfigAction configAction) throws PersistFailedException {

	}

	@NotNull
	@Override
	public PersistTask schedulePersisting(@NotNull ConfigAction configAction) throws PersistFailedException {
		return null;
	}

	@NotNull
	@Override
	public PersistTask schedulePersisting(@NotNull String s) throws PersistFailedException {
		return null;
	}

	public void removeBuildType(String arg0) {
		// TODO Auto-generated method stub

	}

	public void removeBuildTypes() {
		// TODO Auto-generated method stub

	}

	public void setDescription(String arg0) {
		this.description = arg0;
	}

	@Nullable
	@Override
	public String getDefaultTemplateId() {
		return null;
	}

	@Nullable
	@Override
	public String getOwnDefaultTemplateId() {
		return null;
	}

	@Nullable
	@Override
	public BuildTypeTemplate getDefaultTemplate() {
		return null;
	}

	@Nullable
	@Override
	public BuildTypeTemplate getOwnDefaultTemplate() {
		return null;
	}

	public void setName(String arg0) {
		this.name = arg0;
	}

//	public void updateProjectInTransaction(ProjectUpdater arg0)
//			throws PersistFailedException {
//		 TODO Auto-generated method stub
//
//	}

	public void writeTo(Element arg0) {
		// TODO Auto-generated method stub

	}

	public String getDescription() {
		return this.description;
	}

	public String getName() {
		return this.name;
	}

	public String getProjectId() {
		return this.projectId;
	}

	public Status getStatus() {
		return this.status;
	}

	public int compareTo(BuildProject o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void addParameter(Parameter arg0) {
		// TODO Auto-generated method stub
		
	}

	public Map<String, String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Nullable
	@Override
	public String getParameterValue(@NotNull String s) {
		return null;
	}

	public Collection<Parameter> getParametersCollection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Nullable
	@Override
	public Parameter getParameter(@NotNull String s) {
		return null;
	}

	public void removeParameter(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public SBuildType createBuildType(String arg0)
			throws DuplicateBuildTypeNameException,
			MaxNumberOfBuildTypesReachedException {
		// TODO Auto-generated method stub
		return null;
	}

	public SBuildType createBuildTypeFromTemplate(BuildTypeTemplate arg0,
			String arg1, CopyOptions arg2)
			throws MaxNumberOfBuildTypesReachedException,
			InvalidVcsRootScopeException {
		// TODO Auto-generated method stub
		return null;
	}

	public BuildTypeTemplate createBuildTypeTemplate(String arg0)
			throws DuplicateTemplateNameException {
		// TODO Auto-generated method stub
		return null;
	}

	public BuildTypeTemplate createBuildTypeTemplate(BuildTypeTemplate arg0,
			String arg1, CopyOptions arg2) throws InvalidVcsRootScopeException {
		// TODO Auto-generated method stub
		return null;
	}

	public BuildTypeTemplate createBuildTypeTemplate(SBuildType arg0,
			String arg1, CopyOptions arg2) throws InvalidVcsRootScopeException {
		// TODO Auto-generated method stub
		return null;
	}

	public BuildTypeTemplate findBuildTypeTemplateById(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public BuildTypeTemplate findBuildTypeTemplateByName(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getArchivingTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public User getArchivingUser() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<BuildTypeTemplate> getBuildTypeTemplates() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getExtendedName() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> getPotentiallyResponsibleUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<VcsRootInstance> getVcsRootInstances() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isArchived() {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeBuildTypeTemplate(String arg0)
			throws TemplateCannotBeRemovedException {
		// TODO Auto-generated method stub
		
	}

	public void setArchived(boolean arg0, User arg1) {
		// TODO Auto-generated method stub
		
	} 
	
	// From 7.1

	@Override
	public ParametersProvider getParametersProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueResolver getValueResolver() {
		// TODO Auto-generated method stub
		return null;
	}

	// From 8.0

	@NotNull
	@Override
	public String getConfigId() {
		return null;
	}

	@Override
	public String getExternalId() {
		return projectExternalId;
	}

	@Override
	public String getFullName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentProjectExternalId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentProjectId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRootProject() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SPersistentEntity getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@NotNull
	@Override
	public PersistentEntityVersion getVersion() {
		return null;
	}

	@Override
	public void markPersisted(long l) {

	}

	@Override
	public Map<String, String> getOwnParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Nullable
	@Override
	public Parameter getOwnParameter(@NotNull String s) {
		return null;
	}

	@NotNull
	@Override
	public Collection<Parameter> getOwnParametersWithoutInheritedSpec() {
		return null;
	}

	@NotNull
	@Override
	public Collection<Parameter> getInheritedParametersCollection() {
		return null;
	}

	@Override
	public Collection<Parameter> getOwnParametersCollection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String describe(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean belongsTo(SProject arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SBuildType copyBuildType(SBuildType arg0, String arg1, String arg2,
			CopyOptions arg3) throws MaxNumberOfBuildTypesReachedException,
			InvalidVcsRootScopeException, DuplicateExternalIdException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BuildTypeTemplate copyBuildTypeTemplate(BuildTypeTemplate arg0,
			String arg1, String arg2) throws InvalidVcsRootScopeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SVcsRoot copyVcsRoot(SVcsRoot arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SBuildType createBuildType(String arg0, String arg1)
			throws DuplicateExternalIdException,
			DuplicateBuildTypeNameException,
			MaxNumberOfBuildTypesReachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SBuildType createBuildTypeFromTemplate(BuildTypeTemplate arg0,
			String arg1, String arg2)
			throws MaxNumberOfBuildTypesReachedException,
			InvalidVcsRootScopeException, DuplicateExternalIdException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BuildTypeTemplate createBuildTypeTemplate(String arg0, String arg1)
			throws DuplicateTemplateNameException, DuplicateExternalIdException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SProject createProject(String arg0, String arg1)
			throws InvalidIdentifierException, InvalidNameException,
			DuplicateProjectNameException, DuplicateExternalIdException {
		// TODO Auto-generated method stub
		return null;
	}

	@NotNull
	@Override
	public SProjectFeatureDescriptor addFeature(@NotNull String s, @NotNull Map<String, String> map) {
		return null;
	}

	@Override
	public void addFeature(@NotNull SProjectFeatureDescriptor sProjectFeatureDescriptor) {

	}

	@NotNull
	@Override
	public Collection<SProjectFeatureDescriptor> getOwnFeatures() {
		return null;
	}

	@NotNull
	@Override
	public Collection<SProjectFeatureDescriptor> getOwnFeaturesOfType(@NotNull String s) {
		return null;
	}

	@NotNull
	@Override
	public Collection<SProjectFeatureDescriptor> getAvailableFeaturesOfType(@NotNull String s) {
		return null;
	}

	@NotNull
	@Override
	public Collection<SProjectFeatureDescriptor> getAvailableFeatures() {
		return null;
	}

	@Nullable
	@Override
	public SProjectFeatureDescriptor removeFeature(@NotNull String s) {
		return null;
	}

	@Override
	public boolean updateFeature(@NotNull String s, @NotNull String s1, @NotNull Map<String, String> map) {
		return false;
	}

	@Nullable
	@Override
	public SProjectFeatureDescriptor findFeatureById(@NotNull String s) {
		return null;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Nullable
	@Override
	public String getReadOnlyReason() {
		return null;
	}

	@NotNull
	@Override
	public CustomDataStorage getCustomDataStorage(@NotNull String s) {
		return null;
	}

	@NotNull
	@Override
	public Status getStatus(@NotNull String s) {
		return null;
	}

	@Override
	public SVcsRoot createVcsRoot(String arg0, String arg1,
			Map<String, String> arg2) throws UnknownVcsException,
			DuplicateVcsRootNameException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SVcsRoot createVcsRoot(String arg0, String arg1, String arg2)
			throws DuplicateExternalIdException, DuplicateVcsRootNameException {
		// TODO Auto-generated method stub
		return null;
	}

	@NotNull
	@Override
	public SVcsRoot createDummyVcsRoot(@NotNull String s, @NotNull Map<String, String> map) {
		return null;
	}

	@Override
	public BuildTypeTemplate extractBuildTypeTemplate(SBuildType arg0,
			String arg1, String arg2) throws InvalidVcsRootScopeException,
			InvalidIdentifierException, DuplicateExternalIdException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BuildTypeIdentity findBuildTypeIdentityByName(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BuildTypeTemplate findBuildTypeTemplateByExternalId(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SProject findProjectByName(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SVcsRoot findVcsRootByName(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BuildTypeTemplate> getAvailableTemplates() {
		// TODO Auto-generated method stub
		return null;
	}

	@NotNull
	@Override
	public List<SVcsRoot> getAvailableVcsRoots() {
		return null;
	}

	@Override
	public String getExtendedFullName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BuildTypeTemplate> getOwnBuildTypeTemplates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SBuildType> getOwnBuildTypes() {
		return getBuildTypes();
	}

	@Override
	public List<SProject> getOwnProjects() {
		return this.childProjects;
	}

	@Override
	public List<SVcsRoot> getOwnVcsRoots() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SProject getParentProject() {
		return this.parentProject;
	}
	
	public void setParentProject(SProject project){
		this.parentPath = new ArrayList<SProject>();
		this.parentPath.add(project);
		this.parentPath.add(this);
		this.parentProject = project;
	}

	@Override
	public File getPluginDataDirectory(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getPluginSettingsFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SProject> getProjectPath() {
		return this.parentPath;
	}

	@Override
	public List<SProject> getProjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SVcsRoot> getUsedVcsRoots() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void moveToProject(SProject arg0) throws CyclicDependencyException,
			InvalidVcsRootScopeException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveToProject(@NotNull ConfigAction configAction, @NotNull SProject sProject) throws CyclicDependencyException, InvalidVcsRootScopeException {

	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setExternalId(String arg0) throws InvalidIdentifierException,
			DuplicateExternalIdException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setExternalId(@NotNull ConfigAction configAction, @NotNull String s) throws InvalidIdentifierException, DuplicateExternalIdException, ObsoleteEntityException {

	}

	public void addChildProjectToMock(SProject sProject) {
		this.childProjects.add(sProject);
	}

	
	
	

}
