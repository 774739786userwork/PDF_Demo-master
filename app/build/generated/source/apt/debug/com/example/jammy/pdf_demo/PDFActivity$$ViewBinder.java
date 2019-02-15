// Generated code from Butter Knife. Do not modify!
package com.example.jammy.pdf_demo;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class PDFActivity$$ViewBinder<T extends com.example.jammy.pdf_demo.PDFActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492947, "field 'readerView'");
    target.readerView = finder.castView(view, 2131492947, "field 'readerView'");
    view = finder.findRequiredView(source, 2131492950, "field 'rlSign'");
    target.rlSign = finder.castView(view, 2131492950, "field 'rlSign'");
    view = finder.findRequiredView(source, 2131492953, "field 'mScreenShot'");
    target.mScreenShot = finder.castView(view, 2131492953, "field 'mScreenShot'");
    view = finder.findRequiredView(source, 2131492956, "field 'rlClear'");
    target.rlClear = finder.castView(view, 2131492956, "field 'rlClear'");
    view = finder.findRequiredView(source, 2131492960, "field 'rlSave'");
    target.rlSave = finder.castView(view, 2131492960, "field 'rlSave'");
    view = finder.findRequiredView(source, 2131492958, "field 'rlSubmit'");
    target.rlSubmit = finder.castView(view, 2131492958, "field 'rlSubmit'");
    view = finder.findRequiredView(source, 2131492952, "field 'textSign'");
    target.textSign = finder.castView(view, 2131492952, "field 'textSign'");
    view = finder.findRequiredView(source, 2131492954, "field 'imgBtnSign'");
    target.imgBtnSign = finder.castView(view, 2131492954, "field 'imgBtnSign'");
  }

  @Override public void unbind(T target) {
    target.readerView = null;
    target.rlSign = null;
    target.mScreenShot = null;
    target.rlClear = null;
    target.rlSave = null;
    target.rlSubmit = null;
    target.textSign = null;
    target.imgBtnSign = null;
  }
}
