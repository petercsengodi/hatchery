package hu.csega.editors.common.resources;

import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.io.File;

public class FileResourceAdapter implements ResourceAdapter {

    private static final String GAME_RESOURCES_FOLDER = "GameResources";
    private static final String GAME_RESOURCES_MARKER = File.separator + GAME_RESOURCES_FOLDER + File.separator;
    private static final int GAME_RESOURCES_MARKER_LENGTH = GAME_RESOURCES_MARKER.length();

    private final String projectName;
    private final String userRoot;
    private final String projectRoot;
    private final String workspaceRoot;
    private final String resourcesRoot;
    private final String shaderRoot;
    private final String textureRoot;
    private final String meshRoot;
    private final String animationRoot;
    private final String shaderFolder;
    private final String textureFolder;
    private final String meshFolder;
    private final String animationFolder;

    public FileResourceAdapter(String projectName) {
        this.projectName = projectName;

        this.userRoot = System.getProperty("user.dir");
        logger.info("User root: " + this.userRoot);

        String search = File.separatorChar + projectName;
        int index = this.userRoot.indexOf(search);
        if(index < 0)
            throw new RuntimeException("Could not initialize path variables for project: " + projectName);

        this.workspaceRoot = this.userRoot.substring(0, index) + File.separator;
        logger.info("Workspace root: " + this.workspaceRoot);

        this.projectRoot = this.workspaceRoot + projectName + File.separator;
        logger.info("Project root: " + this.projectRoot);

        this.resourcesRoot = this.workspaceRoot + GAME_RESOURCES_FOLDER + File.separator;
        logger.info("Resources root: " + this.resourcesRoot);

        this.shaderFolder = this.resourcesRoot + "shaders";
        logger.info("Shader folder (no separator at the end): " + this.shaderFolder);

        this.textureFolder = this.resourcesRoot + "textures";
        logger.info("Textures folder (no separator at the end): " + this.textureFolder);

        this.meshFolder = this.resourcesRoot + "meshes";
        logger.info("Meshes folder (no separator at the end): " + this.meshFolder);

        this.animationFolder = this.resourcesRoot + "animations";
        logger.info("Meshes folder (no separator at the end): " + this.animationFolder);

        this.shaderRoot = this.shaderFolder + File.separator;
        logger.info("Shader root: " + shaderRoot);

        this.textureRoot = this.textureFolder + File.separator;
        logger.info("Texture root: " + textureRoot);

        this.meshRoot = this.meshFolder + File.separator;
        logger.info("Mesh root: " + meshRoot);

        this.animationRoot = this.animationFolder + File.separator;
        logger.info("Animation root: " + animationRoot);
    }

    @Override
    public String projectName() {
        return projectName;
    }

    @Override
    public String userRoot() {
        return userRoot;
    }

    @Override
    public String projectRoot() {
        return projectRoot;
    }

    @Override
    public String workspaceRoot() {
        return workspaceRoot;
    }

    @Override
    public String resourcesRoot() {
        return resourcesRoot;
    }

    @Override
    public String shaderRoot() {
        return shaderRoot;
    }

    @Override
    public String textureRoot() {
        return textureRoot;
    }

    @Override
    public String meshRoot() {
        return meshRoot;
    }

    @Override
    public String animationRoot() {
        return animationRoot;
    }

    @Override
    public String shaderFolder() {
        return shaderFolder;
    }

    @Override
    public String textureFolder() {
        return textureFolder;
    }

    @Override
    public String meshFolder() {
        return meshFolder;
    }

    @Override
    public String animationFolder() {
        return animationFolder;
    }

    @Override
    public String cleanUpResourceFilename(String filename) {
        if(filename == null) {
            return null;
        }

        int index = filename.indexOf(GAME_RESOURCES_MARKER);
        if(index > -1 && filename.length() > GAME_RESOURCES_MARKER_LENGTH) {
            filename = filename.substring(index + GAME_RESOURCES_MARKER_LENGTH);
        }

        if(filename.startsWith("meshes/")) {
            filename = filename.substring("meshes/".length());
        } else if(filename.startsWith("textures/")) {
            filename = filename.substring("textures/".length());
        } else if(filename.startsWith("animations/")) {
            filename = filename.substring("animations/".length());
        } else if(filename.startsWith("shaders/")) {
            filename = filename.substring("shaders/".length());
        }

        return filename;
    }

    private static final Logger logger = LoggerFactory.createLogger(FileResourceAdapter.class);

}
