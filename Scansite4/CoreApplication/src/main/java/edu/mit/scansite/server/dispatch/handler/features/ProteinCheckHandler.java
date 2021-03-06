package edu.mit.scansite.server.dispatch.handler.features;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.scansite.server.ServiceLocator;
import edu.mit.scansite.shared.DataAccessException;
import edu.mit.scansite.shared.dispatch.BooleanResult;
import edu.mit.scansite.shared.dispatch.features.ProteinCheckAction;
import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

/**
 * @author Tobieh
 * @author Konstantin Krismer
 */
public class ProteinCheckHandler implements ActionHandler<ProteinCheckAction, BooleanResult> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Class<ProteinCheckAction> getActionType() {
		return ProteinCheckAction.class;
	}

	@Override
	public BooleanResult execute(ProteinCheckAction action, ExecutionContext context) throws DispatchException {
		try {
			List<String> accs = ServiceLocator.getDaoFactory().getProteinDao()
					.getAccessionsStartingWith(action.getAccessionContains(), action.getDataSource(), 1);
			return new BooleanResult(accs.size() == 1);
		} catch (DataAccessException e) {
			logger.error("Error checking database if protein exists: " + e.toString());
			throw new ActionException(e.getMessage(), e);
		}
	}

	@Override
	public void rollback(ProteinCheckAction action, BooleanResult result, ExecutionContext context)
			throws DispatchException {
	}
}
