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
import org.opendaylight.controller.md.sal.common.api.TransactionStatus;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.fw.rev150904.Rule;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class FirewallProvider implements BindingAwareProvider, AutoCloseable {

    public static final InstanceIdentifier<Rule> RULE_IID = InstanceIdentifier.builder(Rule.class).build();
    private static final Logger LOG = LoggerFactory.getLogger(FirewallProvider.class);
    private DataBroker dataProvider;

    @Override
    public void onSessionInitiated(ProviderContext session) {
        LOG.info("FirewallProvider Session Initiated");
    }

    @Override
    public void close() throws Exception {
        LOG.info("FirewallProvider Closed");
    }

    public void apply() {
        final WriteTransaction tx = dataProvider.newWriteOnlyTransaction();
        // tx.put(LogicalDatastoreType.CONFIGURATION, buildRule());

        final ListenableFuture<RpcResult<TransactionStatus>> commitFuture = tx.commit();

        Futures.addCallback( commitFuture, new FutureCallback<RpcResult<TransactionStatus>>() {
            @Override
            public void onSuccess( RpcResult<TransactionStatus> result ) {
                if( result.getResult() != TransactionStatus.COMMITED )
                    LOG.error("Failed to update rule: " + result.getErrors());

                //                notifyCallback( result.getResult() == TransactionStatus.COMMITED );
            }

            @Override
            public void onFailure( Throwable t ) {
                // We shouldn't get an OptimisticLockFailedException (or any ex) as no
                // other component should be updating the operational state.
                LOG.error("Failed to update rule", t);

                //                notifyCallback( false );
            }

            // void notifyCallback( boolean result ) {
            // if( resultCallback != null )
            // resultCallback.apply( result );
            // }
        } );

    }

    // private Rule buildRule(String name, String sourceMac, String
    // destinationMac, String sourceIp, String destinationIp,
    // int sourcePort, int destinationPort, Protocol protocol, Action action) {
    // final Rule rule = new
    // RuleBuilder().setName(name).setSourceMacAddress(sourceMac)
    // .setDestinationMacAddress(destinationMac).setSourceIpAddress(destinationIp).setSourceIpAddress(sourceIp)
    // .setSourcePort(sourcePort).setDestinationPort(destinationPort)
    // .build();
    // return rule;
    // }

}
