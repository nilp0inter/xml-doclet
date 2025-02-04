package com.manticore.tools.xmldoclet;

import com.manticore.tools.xmldoclet.xjc.Class;
import com.manticore.tools.xmldoclet.xjc.Package;
import com.manticore.tools.xmldoclet.xjc.Root;

/**
 * @author Manoel Campos
 */
public record JavaDocElements(Root rootNode, Package packageNode, Class classNode) {
}
