package edu.mit.scansite.server.dispatch.handler.motif;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.scansite.server.ServiceLocator;
import edu.mit.scansite.shared.DataAccessException;
import edu.mit.scansite.shared.dispatch.motif.LightWeightMotifGroupRetrieverAction;
import edu.mit.scansite.shared.dispatch.motif.LightWeightMotifGroupRetrieverResult;
import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

/**
 * @author Tobieh
 * @author Konstantin Krismer
 */
public class LightWeightMotifGroupRetrieverHandler
		implements ActionHandler<LightWeightMotifGroupRetrieverAction, LightWeightMotifGroupRetrieverResult> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Class<LightWeightMotifGroupRetrieverAction> getActionType() {
		return LightWeightMotifGroupRetrieverAction.class;
	}

	@Override
	public LightWeightMotifGroupRetrieverResult execute(LightWeightMotifGroupRetrieverAction action,
			ExecutionContext context) throws DispatchException {
		try {
			return new LightWeightMotifGroupRetrieverResult(
					ServiceLocator.getDaoFactory().getGroupsDao().getAllLightWeight());
		} catch (DataAccessException e) {
			logger.error("Error retrieving motif groups: " + e.getMessage());
			throw new ActionException(e.getMessage(), e);
		}
	}

	@Override
	public void rollback(LightWeightMotifGroupRetrieverAction action, LightWeightMotifGroupRetrieverResult result,
			ExecutionContext context) throws DispatchException {
	}
}
