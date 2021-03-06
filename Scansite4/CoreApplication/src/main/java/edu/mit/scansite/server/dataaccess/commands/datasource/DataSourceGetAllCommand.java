package edu.mit.scansite.server.dataaccess.commands.datasource;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.mit.scansite.server.dataaccess.commands.CommandConstants;
import edu.mit.scansite.server.dataaccess.commands.DbQueryCommand;
import edu.mit.scansite.shared.DataAccessException;
import edu.mit.scansite.shared.transferobjects.DataSource;
import edu.mit.scansite.shared.transferobjects.DataSourceType;
import edu.mit.scansite.shared.transferobjects.IdentifierType;

/**
 * @author tobieh
 * @author Konstantin Krismer
 */
public class DataSourceGetAllCommand extends DbQueryCommand<List<DataSource>> {
	private DataSourceType type = null;

	public DataSourceGetAllCommand(Properties dbAccessConfig, Properties dbConstantsConfig) {
		super(dbAccessConfig, dbConstantsConfig);
	}

	public DataSourceGetAllCommand(Properties dbAccessConfig, Properties dbConstantsConfig, DataSourceType type) {
		super(dbAccessConfig, dbConstantsConfig);
		this.type = type;
	}

	@Override
	protected List<DataSource> doProcessResults(ResultSet result) throws DataAccessException {
		List<DataSource> dataSources = new LinkedList<DataSource>();
		try {
			while (result.next()) {
				DataSourceType type = new DataSourceType(result.getInt(c.getcDataSourceTypesId()),
						result.getString(c.getcDataSourceTypesShortName()),
						result.getString(c.getcDataSourceTypesDisplayName()));
				IdentifierType identifierType = new IdentifierType(result.getInt(c.getcIdentifierTypesId()),
						result.getString(c.getcIdentifierTypesName()));
				dataSources.add(new DataSource(result.getInt(c.getcDataSourcesId()), type, identifierType,
						result.getString(c.getcDataSourcesShortName()),
						result.getString(c.getcDataSourcesDisplayName()),
						result.getString(c.getcDataSourcesDescription()), result.getString(c.getcDataSourcesVersion()),
						result.getDate(c.getcDataSourcesLastUpdate()),
						result.getBoolean(c.getcDataSourcesIsPrimary())));
			}
		} catch (Exception e) {
			throw new DataAccessException(e.getMessage(), e);
		}
		return dataSources;
	}

	@Override
	protected String doGetSqlStatement() throws DataAccessException {
		// SELECT * FROM `datasources` INNER JOIN datasourcetypes USING
		// (dataSourceTypesId) INNER JOIN identifiertypes USING (identifierTypesId)
		StringBuilder sql = new StringBuilder();
		sql.append(CommandConstants.SELECT).append('*').append(CommandConstants.FROM).append(c.gettDataSources())
				.append(CommandConstants.INNERJOIN).append(c.gettDataSourceTypes()).append(CommandConstants.USING)
				.append(CommandConstants.LPAR).append(c.getcDataSourceTypesId()).append(CommandConstants.RPAR)
				.append(CommandConstants.INNERJOIN).append(c.gettIdentifierTypes()).append(CommandConstants.USING)
				.append(CommandConstants.LPAR).append(c.getcIdentifierTypesId()).append(CommandConstants.RPAR);
		if (type != null) {
			sql.append(CommandConstants.WHERE).append(c.getcDataSourceTypesId()).append(" = ").append(type.getId());
		}
		sql.append(CommandConstants.ORDERBY).append(c.getcDataSourcesDisplayName());
		return sql.toString();
	}
}
