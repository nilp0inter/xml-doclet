module xml.doclet.main {
    requires jakarta.xml.bind;
    requires java.logging;
    requires jdk.javadoc;
    requires Saxon.HE;
    requires org.apache.commons.cli;

    exports com.github.markusbernhardt.xmldoclet;
}
