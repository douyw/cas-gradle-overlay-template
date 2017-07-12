package org.apereo.cas.infusionsoft.webflow;

import org.apereo.cas.pm.web.flow.PasswordManagementWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

public class InfusionsoftPasswordManagementWebflowConfigurer extends PasswordManagementWebflowConfigurer {

    private final Action infusionsoftInsertCredentialAction;

    private static final String PASSWORD_CHANGE_ACTION = "passwordChangeAction";

    public InfusionsoftPasswordManagementWebflowConfigurer(final FlowBuilderServices flowBuilderServices, final FlowDefinitionRegistry loginFlowDefinitionRegistry, final Action infusionsoftInsertCredentialAction) {
        super(flowBuilderServices, loginFlowDefinitionRegistry);
        this.infusionsoftInsertCredentialAction = infusionsoftInsertCredentialAction;
    }

    @Override
    protected void doInitialize() throws Exception {
        super.doInitialize();

        final Flow loginFlow = getLoginFlow();
        if (loginFlow != null) {
            //TODO: Remove once issue in CAS is resolved
            createEndState(loginFlow, CasWebflowConstants.STATE_ID_PASSWORD_UPDATE_SUCCESS, CasWebflowConstants.VIEW_ID_PASSWORD_UPDATE_SUCCESS);

            ActionState passwordChangeAction = (ActionState) loginFlow.getTransitionableState(PASSWORD_CHANGE_ACTION);
            passwordChangeAction.getEntryActionList().add(this.infusionsoftInsertCredentialAction);
        }
    }
}
