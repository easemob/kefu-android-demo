package com.easemob.kefu_remote.control;

public class CtrlAction {
    private int oper;
    private int btn;
    private int x;
    private int y;
    private int index;

    public CtrlAction(int oper, int x, int y) {
        setOper(oper);
        setX(x);
        setY(y);
    }

    public int getOper() {
        return oper;
    }

    public void setOper(int oper) {
        this.oper = oper;
    }

    public int getBtn() {
        return btn;
    }

    public void setBtn(int btn) {
        this.btn = btn;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
