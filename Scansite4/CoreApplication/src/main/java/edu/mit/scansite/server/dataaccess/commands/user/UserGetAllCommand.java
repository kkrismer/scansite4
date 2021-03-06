package edu.mit.scansite.server.dataaccess.commands.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import edu.mit.scansite.server.dataaccess.commands.DbQueryCommand;
import edu.mit.scansite.shared.DataAccessException;
import edu.mit.scansite.shared.transferobjects.User;
import edu.mit.scansite.shared.transferobjects.User.UserGroup;

/**
 * @author Tobieh
 * @author Konstantin Krismer
 */
public class UserGetAllCommand extends DbQueryCommand<ArrayList<User>> {

	private String tUsers;
	private String cEmail;
	private String cFirstName;
	private String cLastName;
	private String cUserGroup;

	public UserGetAllCommand(Properties dbAccessConfig, Properties dbConstantsConfig) {
		super(dbAccessConfig, dbConstantsConfig);
		tUsers = c.gettUsers();
		cEmail = c.getcUsersEmail();
		cFirstName = c.getcUsersFirstName();
		cLastName = c.getcUsersLastName();
		cUserGroup = c.getcUsersUserGroup();
	}

	@Override
	protected ArrayList<User> doProcessResults(ResultSet result) throws DataAccessException {
		ArrayList<User> users = new ArrayList<User>();
		try {
			while (result.next()) {
				users.add(new User(result.getString(cEmail), result.getString(cFirstName), result.getString(cLastName),
						"", UserGroup.valueOf(result.getString(cUserGroup))));
			}
		} catch (SQLException e) {
			throw new DataAccessException(e.getMessage(), e);
		}
		return users;
	}

	@SuppressWarnings("static-access")
	@Override
	protected String doGetSqlStatement() throws DataAccessException {
		StringBuilder sql = new StringBuilder();
		sql.append(c.SELECT).append(cEmail).append(c.COMMA).append(cFirstName).append(c.COMMA).append(cLastName)
				.append(c.COMMA).append(cUserGroup).append(c.FROM).append(tUsers)
				.append(c.ORDERBY).append(cFirstName).append(c.COMMA).append(cLastName);
		return sql.toString();
	}
}
