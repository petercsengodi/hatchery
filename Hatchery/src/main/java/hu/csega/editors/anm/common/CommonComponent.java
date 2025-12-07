package hu.csega.editors.anm.common;

public interface CommonComponent {

    void invalidate();

    void addDependent(CommonComponent dependent);

}
