/*
 * Copyright (c) 2018 Arthur Naseef
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amlinv.xml.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlGetElementAttribute {
    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(XmlGetElementAttribute.class);

    private Logger log = DEFAULT_LOGGER;
    private DocumentBuilder docBuilder;
    private Document doc;

    public static void main(String[] args) {
        if (args.length < 2 || "-?".equals(args[0]) || "--help".equals(args[0])) {
            System.out.println("Usage: XmlGetElementAttribute file path [path ...]");
            System.out.println("Note: the last element of path is the attribute name; paths are in the form ele1.ele2.attribute");
            System.out.println("Note: don't specify the root element");
            System.out.println("Note: use '*' to match any one element name");
            System.out.println("Note: use '**' to match any hierarchy of elements");
            System.exit(1);
        }

        try {
            new XmlGetElementAttribute(args);
        } catch (Throwable var3) {
            var3.printStackTrace();
        }

    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public XmlGetElementAttribute(String[] args) throws Exception {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

        documentFactory.setValidating(false);
        this.docBuilder = documentFactory.newDocumentBuilder();
        this.ignoreDtdResolution();
        this.doc = this.docBuilder.parse(new File(args[0]));

        for (String oneArg : Arrays.copyOfRange(args, 1, args.length)) {
            log.debug("performing lookup on path {}", oneArg);
            this.performPathLookup(oneArg, this.doc.getDocumentElement());
        }
    }

    public void performPathLookup(String path, Node node) {
        String[] pathParts = path.split("\\.");
        String[] elementParts = Arrays.copyOf(pathParts, pathParts.length - 1);

        List<Node> elements = this.lookupPath(elementParts, node);

        for (Node oneNode : elements) {
            if (oneNode instanceof Element) {
                Element ele = (Element) oneNode;
                System.out.println(ele.getAttribute(pathParts[pathParts.length - 1]));
            } else {
                System.out.println("");
            }

        }
    }

    public List<Node> lookupPath(String[] pathParts, Node root) {
        LinkedList<Node> result = new LinkedList<>();

        log.debug("lookup path {} from root {}", pathParts, root);

        if ((pathParts != null) && (pathParts.length != 0)) {
            String elementName = pathParts[0];
            String[] remainder = (String[]) Arrays.copyOfRange(pathParts, 1, pathParts.length);

            if (elementName.equals("**")) {
                //
                // Match any depth to the next named node
                //
                if (remainder.length > 0) {
                    for (Node oneDescendant : this.findDescendants(remainder[0], root)) {
                        result.addAll(
                                this.lookupPath(Arrays.copyOfRange(remainder, 1, remainder.length), oneDescendant));
                    }
                } else {
                    log.info("ignoring \"**\" at end of path");
                }
            } else {
                for (Node oneNode : this.nodeListAsList(root.getChildNodes())) {
                    if (oneNode instanceof Element) {
                        Element ele = (Element) oneNode;
                        if ((elementName.equals("*") || (elementName.equals(ele.getTagName())))) {
                            result.addAll(this.lookupPath(remainder, ele));
                        }
                    }
                }
            }

            return result;
        } else {
            result.add(root);
            return result;
        }
    }

    public List<Node> findDescendants(String name, Node root) {
        LinkedList<Node> result = new LinkedList<>();

        log.debug("lookup descendants name \"{}\" in {}", name, root);

        //
        // Iterate through all of the child nodes, adding those that match, and searching the descendants of all.
        //
        for (Node oneNode : this.nodeListAsList(root.getChildNodes())) {
            if (oneNode instanceof Element) {
                Element ele = (Element) oneNode;
                if (name.equals(ele.getTagName())) {
                    log.debug("added descendant {}", ele);
                    result.add(ele);
                }
            }

            result.addAll(this.findDescendants(name, oneNode));
        }

        return result;
    }

    protected void ignoreDtdResolution() {
        this.docBuilder.setEntityResolver(new EntityResolver() {
            public InputSource resolveEntity(String var1, String var2) throws SAXException, IOException {
                return var2.contains(".dtd") ? new InputSource(new StringReader("")) : null;
            }
        });
    }

    protected List<Node> nodeListAsList(final NodeList nodeList) {
        return new AbstractList<Node>() {
            @Override
            public Node get(int index) {
                return nodeList.item(index);
            }

            @Override
            public int size() {
                return nodeList.getLength();
            }
        };
    }

    protected void dumpNodeTree(Node node) {
        if (node instanceof Element) {
            Element var3 = (Element) node;
            System.out.println("ELEMENT tag=" + var3.getTagName());
        } else {
            System.out.println(node.toString());
        }

        for (Node child : this.nodeListAsList(node.getChildNodes())) {
            this.dumpNodeTree(child);
        }
    }
}

