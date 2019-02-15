package com.artifex.mupdf;

public class LinkInfoExternal{
//	final public String url;

	public LinkInfoExternal(float l, float t, float r, float b, int p) {
//		super(l, t, r, b);
//		url = u;
	}



	public void acceptVisitor(LinkInfoVisitor visitor) {
		visitor.visitExternal(this);
	}
}
