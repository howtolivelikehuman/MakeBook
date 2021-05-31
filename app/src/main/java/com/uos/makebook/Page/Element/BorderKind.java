package com.uos.makebook.Page.Element;

// ElementData.recognizeBorderOn의 반환값으로 사용.
public class BorderKind {
    private boolean left, right, up, down;

    public BorderKind() {
        left = false;
        right = false;
        up = false;
        down = false;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isSet() {
        return left || right || up || down;
    }
}
