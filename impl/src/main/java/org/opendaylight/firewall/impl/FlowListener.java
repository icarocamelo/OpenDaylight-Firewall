package org.opendaylight.firewall.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.fw.rev150904.Rule;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class FlowListener implements DataChangeListener, AutoCloseable {

    private DataBroker dataBroker;

    /**
     * ListenerRegistration Object to perform registration.
     */
    private ListenerRegistration<DataChangeListener> registration;

    public FlowListener(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    @Override
    public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> changes) {
        if (changes.getCreatedData() instanceof Rule) {
            // TODO: Handle Rule creation
            final Rule rule = (Rule) changes.getCreatedData();

            // Create an OF flow and push to device
            FlowManager.pushFlow(rule);
        }
    }

    /**
     * Method to register listener.
     * 
     * @param dataBroker
     *            the
     *            {@link org.opendaylight.controller.md.sal.binding.api.DataBroker}
     */
    public void registerListener(DataBroker dataBroker) {
        InstanceIdentifier<Rule> path = InstanceIdentifier.builder(Rule.class).build();
        registration = this.dataBroker.registerDataChangeListener(LogicalDatastoreType.OPERATIONAL, path, this,
                DataChangeScope.SUBTREE);
    }

    @Override
    public void close() throws Exception {
        if (registration != null) {
            registration.close();
        }
    }
}
