package hu.csega.editors.common.resources;

public interface ResourceAdapter {

    String projectName();

    String userRoot();

    String projectRoot();

    String workspaceRoot();

    String resourcesRoot();

    String shaderRoot();

    String textureRoot();

    String meshRoot();

    String animationRoot();

    String shaderFolder();

    String textureFolder();

    String meshFolder();

    String animationFolder();

    String cleanUpResourceFilename(String filename);

}
