package com.manticoreprojects.tools.xmldoclet;

import com.manticoreprojects.tools.xmldoclet.xjc.Class;
import com.manticoreprojects.tools.xmldoclet.xjc.Package;
import com.manticoreprojects.tools.xmldoclet.xjc.Root;

/**
 * @author Manoel Campos
 */
public record JavaDocElements(Root rootNode, Package packageNode, Class classNode) {
}
