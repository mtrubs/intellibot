package com.millennialmedia.intellibot.ide.icons;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * @author mrubino
 * @since 2016-01-28
 */
public class RobotIcons {

    public static final Icon FILE = IconLoader.findIcon("/images/robot_icon.png");
    public static final Icon HEADING = AllIcons.Nodes.Tag;
    public static final Icon KEYWORD_DEFINITION = AllIcons.Nodes.Method;
    public static final Icon TEST_CASE = AllIcons.RunConfigurations.Junit;
    public static final Icon VARIABLE_DEFINITION = AllIcons.Nodes.Variable;

    private RobotIcons() {
    }
}
