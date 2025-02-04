package com.manticoreprojects.tools.xmldoclet;

import com.manticoreprojects.tools.xmldoclet.xjc.Class;
import com.manticoreprojects.tools.xmldoclet.xjc.Package;
import com.manticoreprojects.tools.xmldoclet.xjc.Root;

/**
 * @author Manoel Campos
 */
public class JavaDocElements {
    private final Root rootNode;
    private final Package packageNode;
    private final Class classNode;

    public JavaDocElements(Root rootNode, Package packageNode, Class classNode) {
        this.rootNode = rootNode;
        this.packageNode = packageNode;
        this.classNode = classNode;
    }

    public Root rootNode() {
        return rootNode;
    }

    public Package packageNode() {
        return packageNode;
    }

    public Class classNode() {
        return classNode;
    }
}

