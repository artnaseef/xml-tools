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

import com.sun.org.apache.xerces.internal.impl.xs.XSImplementationImpl;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSLoader;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SchemaManager;

import org.w3c.dom.bootstrap.DOMImplementationRegistry;

import java.io.File;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

/**
 * INCOMPLETE
 */
public class XsdFindChildren {
  public static void main(String[] args) {
    try {
      new XsdFindChildren().instanceMain(args);
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  public void instanceMain(String[] args) throws Exception {
    if (args.length < 2) {
      System.err.println("Usage: XsdFindChildren <file> <root-ele>");
      System.exit(1);
    }

//    this.processFile(args[0], args[1]);
    this.analyzeHandlers(args[0]);
  }

  private void processFile(String file, String ele) throws Exception {
    XSModel xsModel = this.loadFileModel(file);

    XSElementDeclaration elementDeclaration = xsModel.getElementDeclaration(ele, null);
    XSTypeDefinition typeDefinition = elementDeclaration.getTypeDefinition();

    System.out.println(typeDefinition.getClass().getName());
//    if (typeDefinition instanceof XSComplexTypeDefinition) {
//      XSComplexTypeDefinition complexTypeDefinition = (XSComplexTypeDefinition) typeDefinition;
//
//      complexTypeDefinition.
//    }

//    if (typeDefinition instanceof XSComplexType) {
//      XSComplexType complexType = (XSComplexType) typeDefinition;
//
//      for (XSElementDecl elementDecl : complexType.getElementDecls()) {
//        System.out.println(elementDecl.toString());
//      }
//    }
  }

  private XSModel loadFileModel(String file) throws Exception {
    System.setProperty(DOMImplementationRegistry.PROPERTY,
                       "com.sun.org.apache.xerces.internal.dom.DOMXSImplementationSourceImpl");

    DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();

    com.sun.org.apache.xerces.internal.impl.xs.XSImplementationImpl
        impl =
        (XSImplementationImpl) registry.getDOMImplementation("XS-Loader");

    StringList result = impl.getRecognizedVersions();
    XSLoader schemaLoader = impl.createXSLoader(null);

    return schemaLoader.loadURI(file);
  }

  private void analyzeHandlers(String filename) throws Exception {
    SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
    schemaFactory.setProperty("http://saxon.sf.net/feature/xsd-version", "1.1");
    Schema schemaGrammar = schemaFactory.newSchema(new File(filename));

    ValidatorHandler handler = schemaGrammar.newValidatorHandler();
    TypeInfoProvider typeInfoProvider = handler.getTypeInfoProvider();

    System.out.println(handler.getClass().getName());
    System.out.println(typeInfoProvider.getClass().getName());
//    Validator schemaValidator = schemaGrammar.newValidator();

//    schemaValidator.validate(new StreamSource(xmlmess));

  }

  public void loadUsingSaxon(String filename) throws Exception {
    Processor processor = new Processor(false);
//    SchemaManager schemaManager = new SchemaManagerImpl(processor);

  }
}
