package edu.mit.scansite.server.dispatch.handler.news;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.scansite.server.ServiceLocator;
import edu.mit.scansite.server.dataaccess.NewsDao;
import edu.mit.scansite.shared.DataAccessException;
import edu.mit.scansite.shared.dispatch.news.NewsAddAction;
import edu.mit.scansite.shared.dispatch.news.NewsRetrieverResult;
import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

/**
 * @author Tobieh
 * @author Konstantin Krismer
 */
public class NewsAddHandler implements ActionHandler<NewsAddAction, NewsRetrieverResult> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Class<NewsAddAction> getActionType() {
		return NewsAddAction.class;
	}

	@Override
	public NewsRetrieverResult execute(NewsAddAction action, ExecutionContext context) throws DispatchException {
		try {
			NewsDao newsDao = ServiceLocator.getDaoFactory().getNewsDao();
			newsDao.add(action.getNewsEntry());
			return new NewsRetrieverResult(newsDao.getAll(0));
		} catch (DataAccessException exc) {
			logger.error("Error adding news entry: " + exc.toString());
			throw new ActionException(exc);
		}
	}

	@Override
	public void rollback(NewsAddAction action, NewsRetrieverResult result, ExecutionContext context)
			throws DispatchException {
	}
}
