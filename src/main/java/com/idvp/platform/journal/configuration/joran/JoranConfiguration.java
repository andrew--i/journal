/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 2006-2011, QOS.ch. All rights reserved.
 * <p>
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 * <p>
 * or (per the licensee's choosing)
 * <p>
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */

package com.idvp.platform.journal.configuration.joran;


import ch.qos.logback.core.joran.JoranConfiguratorBase;
import ch.qos.logback.core.joran.action.ContextPropertyAction;
import ch.qos.logback.core.joran.action.NestedBasicPropertyIA;
import ch.qos.logback.core.joran.action.NestedComplexPropertyIA;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.joran.spi.RuleStore;


public class JoranConfiguration extends JoranConfiguratorBase {

  @Override
  public void addInstanceRules(RuleStore rs) {
    rs.addRule(new ElementSelector("journals"), new JournalFactoryAction());
    rs.addRule(new ElementSelector("journals/journal"), new JournalAction());
    rs.addRule(new ElementSelector("journals/journal/appender"), new JournalAppenderAction());
    rs.addRule(new ElementSelector("journals/journal/reader"), new JournalReaderAction());
  }

  @Override
  protected void addImplicitRules(Interpreter interpreter) {
    // The following line adds the capability to parse nested components
    NestedComplexPropertyIA nestedComplexPropertyIA = new NestedComplexPropertyIA(getBeanDescriptionCache());
    nestedComplexPropertyIA.setContext(context);
    interpreter.addImplicitAction(nestedComplexPropertyIA);

    NestedBasicPropertyIA nestedBasicIA = new NestedBasicPropertyIA(getBeanDescriptionCache());
    nestedBasicIA.setContext(context);
    interpreter.addImplicitAction(nestedBasicIA);
  }

}
