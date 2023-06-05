package com.minis.web;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.URL;
import java.util.Iterator;
@Deprecated
public class ClassPathXmlResource implements Resource {

    Document document;
    Element rootElement;
    Iterator<Element> elementIterator;

    public ClassPathXmlResource(URL xmlPath) {
        SAXReader saxReader = new SAXReader();
        try {
            this.document = saxReader.read(xmlPath);
            this.rootElement = this.document.getRootElement();
            this.elementIterator = this.rootElement.elements().iterator();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        return this.elementIterator.hasNext();
    }

    @Override
    public Object next() {
        return this.elementIterator.next();
    }

}
