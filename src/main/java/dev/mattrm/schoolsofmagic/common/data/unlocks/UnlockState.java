package dev.mattrm.schoolsofmagic.common.data.unlocks;

public enum UnlockState {
    UNLOCKED(0),
    NOT_AVAILABLE(1),
    NOT_UNLOCKED(2),
    CAN_PURCHASE(3) {
        @Override
        public int getTextureIndex(boolean flashOn) {
            return textureIndex - (flashOn ? 0 : 1);
        }
    };

    protected int textureIndex;

    UnlockState(int textureIndex) {
        this.textureIndex = textureIndex;
    }

    public int getTextureIndex(boolean flashOn) {
        return textureIndex;
    }
}
