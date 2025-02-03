package com.manticoreprojects.tools.xmldoclet;

import net.sf.saxon.lib.ResourceRequest;
import net.sf.saxon.lib.ResourceResolver;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

/**
 * Resolves resources from the classpath.
 */
class ClasspathResourceURIResolver implements ResourceResolver {
    @Override
    public Source resolve(final ResourceRequest request) {
        final var inputStream = getClass().getClassLoader().getResourceAsStream(request.uri);
        return inputStream == null ? null : new StreamSource(inputStream);
    }
}
