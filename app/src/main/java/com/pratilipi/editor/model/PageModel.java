package com.pratilipi.editor.model;

/**
 * Created by jeeva on 04/09/17.
 */
public interface PageModel {

    void loadInitialPage();

    void savePage(String htmlContent);

    void discardPage();

    int getPageId();

    void clearAllInstances();
}