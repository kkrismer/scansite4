package edu.mit.scansite.server.dispatch.handler.user;

import javax.servlet.ServletContext;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.mit.scansite.server.ServiceLocator;
import edu.mit.scansite.server.dataaccess.UserDao;
import edu.mit.scansite.server.dispatch.BootstrapListener;
import edu.mit.scansite.shared.DataAccessException;
import edu.mit.scansite.shared.dispatch.user.UserAddAction;
import edu.mit.scansite.shared.dispatch.user.UserRetrieverResult;

/**
 * @author Tobieh
 * @author Konstantin Krismer
 */
public class UserAddHandler implements
		ActionHandler<UserAddAction, UserRetrieverResult> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final Provider<ServletContext> contextProvider;

	@Inject
	public UserAddHandler(final Provider<ServletContext> contextProvider) {
		this.contextProvider = contextProvider;
	}

	@Override
	public Class<UserAddAction> getActionType() {
		return UserAddAction.class;
	}

	@Override
	public UserRetrieverResult execute(UserAddAction action,
			ExecutionContext context) throws DispatchException {
		try {
			UserDao userDao = ServiceLocator
					.getInstance()
					.getDaoFactory(
							BootstrapListener.getDbConnector(contextProvider
									.get())).getUserDao();
			userDao.add(action.getUser());
			return new UserRetrieverResult(userDao.getAll(), true);
		} catch (DataAccessException e) {
			logger.error("Error adding user: " + e.toString());
			throw new ActionException(e);
		}
	}

	@Override
	public void rollback(UserAddAction action, UserRetrieverResult result,
			ExecutionContext context) throws DispatchException {
	}
}