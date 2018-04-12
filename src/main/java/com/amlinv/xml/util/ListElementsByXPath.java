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

import org.apache.logging.log4j.util.Strings;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by art on 1/17/18.
 */
public class ListElementsByXPath {

  private boolean includeValues = false;

  public static void main(String[] args) {
    if ((args.length > 0) && ("-?".equals(args[0]) || "--help".equals(args[0]))) {
      System.out.println("Usage: ListElementsByXPath [file]");
      System.exit(1);
    }

    try {
      new ListElementsByXPath().instanceMain(args);
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  public void instanceMain(String[] args) throws Exception {
    int curArg = 0;
    if (args[curArg].equals("--include-values")) {
      this.includeValues = true;
      curArg++;
    }

    InputStream inputStream;
    if (curArg >= args.length) {
      inputStream = System.in;
    } else {
      inputStream = new FileInputStream(args[curArg]);
    }

    DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

    documentFactory.setValidating(false);
    DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
    documentBuilder.setEntityResolver(
        (var1, var2) -> var2.contains(".dtd") ? new InputSource(new StringReader("")) : null);

    org.w3c.dom.Document document = documentBuilder.parse(inputStream);

    Element rootElement = document.getDocumentElement();

    this.listElementContents("", rootElement);
  }

  private void listElementContents(String prefix, Element element) {
    String prefixedTagName = this.formatElementName(element);

    boolean needNewline = true;
    boolean needSeparator = true;
    prefix = prefix + "/" + prefixedTagName;
    System.out.print(prefix);

    NodeList children = element.getChildNodes();

    int cur = 0;
    while ( cur < children.getLength() ) {
      Node childNode = children.item(cur);

      if (childNode instanceof Element) {
        if (needNewline) {
          System.out.println();
          needNewline = false;
        }

        this.listElementContents(prefix, (Element) childNode);
      } else if (this.includeValues) {
        if (childNode instanceof CharacterData) {
          CharacterData characterDataNode = (CharacterData) childNode;
          String data = characterDataNode.getData();

          if (! Strings.isBlank(data)) {
            if (needSeparator) {
              System.out.print("\t");
              needSeparator = false;
            }

            System.out.print(characterDataNode.getData());
          }
        }
      }

      cur++;
    }

    if (needNewline) {
      System.out.println();
    }
  }

  private String formatElementName(Element element) {
    String tagName = element.getTagName();
    String nsPrefix = element.getPrefix();

    if (nsPrefix == null) {
      return tagName;
    }

    return nsPrefix + ":" + tagName;
  }
}
