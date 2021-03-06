package edu.mit.scansite.server.dataaccess.commands.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import edu.mit.scansite.server.dataaccess.commands.DbQueryCommand;
import edu.mit.scansite.shared.DataAccessException;
import edu.mit.scansite.shared.transferobjects.User;
import edu.mit.scansite.shared.transferobjects.User.UserGroup;

/**
 * @author Tobieh
 * @author Konstantin Krismer
 */
public class UserGetCommand extends DbQueryCommand<User> {
	private boolean withPassword = true;

	private String email;
	private String password;

	private String tUsers;
	private String cEmail;
	private String cFirstName;
	private String cLastName;
	private String cPassword;
	private String cUserGroup;

	public UserGetCommand(Properties dbAccessConfig, Properties dbConstantsConfig, String email, String password) {
		super(dbAccessConfig, dbConstantsConfig);
		this.email = email;
		this.password = password;

		tUsers = c.gettUsers();
		cEmail = c.getcUsersEmail();
		cFirstName = c.getcUsersFirstName();
		cLastName = c.getcUsersLastName();
		cPassword = c.getcUsersPassword();
		cUserGroup = c.getcUsersUserGroup();
	}

	public UserGetCommand(Properties dbAccessConfig, Properties dbConstantsConfig, String email) {
		this(dbAccessConfig, dbConstantsConfig, email, "");
		withPassword = false;
	}

	@Override
	protected User doProcessResults(ResultSet result) throws DataAccessException {
		User user = null;
		try {
			if (result.next()) {
				user = new User(result.getString(cEmail), result.getString(cFirstName), result.getString(cLastName), "",
						UserGroup.valueOf(result.getString(cUserGroup)));
			}
		} catch (SQLException e) {
			throw new DataAccessException(e.getMessage(), e);
		}
		return user;
	}

	@SuppressWarnings("static-access")
	@Override
	protected String doGetSqlStatement() throws DataAccessException {
		StringBuilder sql = new StringBuilder();
		sql.append(c.SELECT).append(cEmail).append(c.COMMA).append(cFirstName).append(c.COMMA).append(cLastName)
				.append(c.COMMA).append(cUserGroup).append(c.FROM).append(tUsers).append(c.WHERE).append(cEmail)
				.append(" LIKE \"").append(email).append('\"');
		if (withPassword) {
			sql.append(c.AND).append(" PASSWORD(\"").append(password).append("\")").append(c.LIKE).append(cPassword);
		}
		return sql.toString();
	}
}