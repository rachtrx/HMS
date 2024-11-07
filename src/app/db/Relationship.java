package app.db;

public class Relationship {
    private final int localColIdx;
    private final int remoteColIdx;
    private final DeleteBehavior deleteBehavior;

    public Relationship(int localColIdx, int remoteColIdx, DeleteBehavior deleteBehavior) {
        this.localColIdx = localColIdx;
        this.remoteColIdx = remoteColIdx;
        this.deleteBehavior = deleteBehavior;
    }

    public int getLocalColIdx() {
        return localColIdx;
    }

    public int getRemoteColIdx() {
        return remoteColIdx;
    }

    public DeleteBehavior getDeleteBehavior() {
        return deleteBehavior;
    }
}
