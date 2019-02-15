package com.artifex.mupdf;

public class LinkInfoInternal{
	final public int pageNumber;

	public LinkInfoInternal(float l, float t, float r, float b, int p) {
		super();
		pageNumber = p;
	}

	public void acceptVisitor(LinkInfoVisitor visitor) {
		visitor.visitInternal(this);
	}
}
