/*
 * Copyright(c) Inocybe Technologies and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.firewall.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.fw.rev150904.Rule;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.fw.rev150904.RuleBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

public class FirewallProvider implements BindingAwareProvider, AutoCloseable {
    public static final InstanceIdentifier<Rule> RULE_IID = InstanceIdentifier.builder(Rule.class).build();
    private static final Logger LOG = LoggerFactory.getLogger(FirewallProvider.class);
    private DataBroker dataBroker;

    @Override
    public void onSessionInitiated(ProviderContext session) {
        LOG.info("FirewallProvider Session Initiated");
        initIntentsConfiguration();
        initIntentsOperational();
    }

    @Override
    public void close() throws Exception {
        LOG.info("FirewallProvider Closed");
    }

    /**
     * Populates Intents' initial operational data into the MD-SAL operational
     * data store.
     */
    protected void initIntentsOperational() {
        // Build the initial intents operational data
        Rule rules = new RuleBuilder().build();

        // Put the Intents operational data into the MD-SAL data store
        WriteTransaction tx = dataBroker.newWriteOnlyTransaction();
        tx.put(LogicalDatastoreType.OPERATIONAL, RULE_IID, rules);

        // Perform the tx.submit asynchronously
        Futures.addCallback(tx.submit(), new FutureCallback<Void>() {

            @Override
            public void onSuccess(final Void result) {
                LOG.info("initIntentsOperational: transaction succeeded");
            }

            @Override
            public void onFailure(final Throwable throwable) {
                LOG.error("initIntentsOperational: transaction failed");
            }
        });

        LOG.info("initIntentsOperational: operational status populated: {}", rules);
    }
    
    /**
     * Populates Intents' default config data into the MD-SAL configuration data
     * store. Note the database write to the tree are done in a synchronous
     * fashion
     */
    protected void initIntentsConfiguration() {
    	// Build the initial intents operational data
        Rule rules = new RuleBuilder().build();

        // Put the Intents operational data into the MD-SAL data store
        WriteTransaction tx = dataBroker.newWriteOnlyTransaction();
        tx.put(LogicalDatastoreType.CONFIGURATION, RULE_IID, rules);
        // Perform the tx.submit synchronously
        tx.submit();

        LOG.info("initIntentsConfiguration: default config populated: {}", rules);
    }

}
