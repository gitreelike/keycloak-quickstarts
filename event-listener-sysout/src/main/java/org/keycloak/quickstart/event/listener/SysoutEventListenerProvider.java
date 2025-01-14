/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
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

package org.keycloak.quickstart.event.listener;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class SysoutEventListenerProvider implements EventListenerProvider {

    private KeycloakSession session;

    public SysoutEventListenerProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType() == EventType.REGISTER) {
            final UserModel user = session.users().getUserById(session.getContext().getRealm(), event.getUserId());
            user.setSingleAttribute("setByEventListener", "onEvent(Event event)");
            session.getTransactionManager().setRollbackOnly();
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        if (event.getResourceType() == ResourceType.USER && event.getOperationType() == OperationType.CREATE) {
            final String userId = userIdFromResourcePath(event.getResourcePath());
            final UserModel user = session.users().getUserById(session.getContext().getRealm(), userId);
            user.setSingleAttribute("setByEventListener", "onEvent(AdminEvent event, boolean includeRepresentation)");
            session.getTransactionManager().setRollbackOnly();
        }
    }

    private String userIdFromResourcePath(String path) {
        final String[] components = path.split("/");

        return components[components.length - 1];
    }

    @Override
    public void close() {
    }

}
