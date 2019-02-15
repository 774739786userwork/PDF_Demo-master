package com.artifex.mupdf;

/**
 * Created by bangware on 2019/1/29.
 */

abstract public class LinkInfoVisitor {
    public abstract void visitInternal(LinkInfoInternal li);
    public abstract void visitExternal(LinkInfoExternal li);
    public abstract void visitRemote(LinkInfoRemote li);
}
