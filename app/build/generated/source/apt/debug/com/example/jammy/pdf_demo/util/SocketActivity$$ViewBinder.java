// Generated code from Butter Knife. Do not modify!
package com.example.jammy.pdf_demo.util;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SocketActivity$$ViewBinder<T extends com.example.jammy.pdf_demo.util.SocketActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492968, "field 'mRl_logout'");
    target.mRl_logout = finder.castView(view, 2131492968, "field 'mRl_logout'");
  }

  @Override public void unbind(T target) {
    target.mRl_logout = null;
  }
}
