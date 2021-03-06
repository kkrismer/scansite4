package edu.mit.scansite.server.dispatch.handler.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.scansite.server.ServiceLocator;
import edu.mit.scansite.shared.DataAccessException;
import edu.mit.scansite.shared.dispatch.datasource.DataSourceSizesRetrieverAction;
import edu.mit.scansite.shared.dispatch.datasource.DataSourceSizesRetrieverResult;
import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

/**
 * @author Tobieh
 * @author Konstantin Krismer
 */
public class DataSourceSizesRetrieverHandler
		implements ActionHandler<DataSourceSizesRetrieverAction, DataSourceSizesRetrieverResult> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Class<DataSourceSizesRetrieverAction> getActionType() {
		return DataSourceSizesRetrieverAction.class;
	}

	@Override
	public DataSourceSizesRetrieverResult execute(DataSourceSizesRetrieverAction action, ExecutionContext context)
			throws DispatchException {
		try {
			return new DataSourceSizesRetrieverResult(
					ServiceLocator.getDaoFactory().getDataSourceDao().getDataSourceSizes());
		} catch (DataAccessException e) {
			logger.error(e.toString());
			throw new ActionException(e.getMessage(), e);
		}
	}

	@Override
	public void rollback(DataSourceSizesRetrieverAction action, DataSourceSizesRetrieverResult result,
			ExecutionContext context) throws DispatchException {
	}
}
