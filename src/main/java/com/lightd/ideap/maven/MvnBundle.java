package com.lightd.ideap.maven;

import com.intellij.CommonBundle;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import javax.swing.*;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

public class MvnBundle {
    private static Reference<ResourceBundle> ourBundle;

    @NonNls
    private static final String PATH_TO_BUNDLE = "messages.MvnBundle";
    public static final Icon MAVEN_RUN_ICON = IconLoader.getIcon("/images/mvn_run.png");

    private MvnBundle() {
    }

    public static String message(@PropertyKey(resourceBundle = PATH_TO_BUNDLE) String key, Object... params) {
        return CommonBundle.message(getBundle(), key, params);
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = null;
        if (ourBundle != null) bundle = ourBundle.get();
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(PATH_TO_BUNDLE);
            ourBundle = new SoftReference<ResourceBundle>(bundle);
        }
        return bundle;
    }
}
