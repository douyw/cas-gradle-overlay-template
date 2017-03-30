package org.apereo.cas.infusionsoft.webflow;

import org.apereo.cas.web.flow.AbstractCasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.execution.Action;

public class InfusionsoftWebflowConfigurer extends AbstractCasWebflowConfigurer {

    private final Action infusionsoftFlowSetupAction;

    public InfusionsoftWebflowConfigurer(final Action infusionsoftFlowSetupAction) {
        super();
        this.infusionsoftFlowSetupAction = infusionsoftFlowSetupAction;
    }

    @Override
    protected void doInitialize() throws Exception {
        final Flow loginFlow = getLoginFlow();
        if (loginFlow != null) {
            final ViewState state = (ViewState) loginFlow.getTransitionableState(CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
            state.getEntryActionList().add(this.infusionsoftFlowSetupAction);
        }
    }

}