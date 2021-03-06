package edu.mit.scansite.server.dataaccess.commands.histogram;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.scansite.server.dataaccess.commands.CommandConstants;
import edu.mit.scansite.server.dataaccess.databaseconnector.DbConnector;
import edu.mit.scansite.server.images.histograms.ServerHistogram;
import edu.mit.scansite.shared.DataAccessException;

/**
 * @author Tobieh
 * @author Konstantin Krismer
 */
public class HistogramUpdateCommand {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private ServerHistogram hist;
	private CommandConstants c = CommandConstants.instance();

	public HistogramUpdateCommand(Properties dbConstantsConfig, ServerHistogram hist) {
		if (c == null) {
			c = CommandConstants.instance(dbConstantsConfig);
		}
		this.hist = hist;
	}

	public void execute() throws DataAccessException {
		String query = "";
		DbConnector dbConnector = null;
		Connection connection = null;
		PreparedStatement statement = null;
		File f = new File(hist.getImageFilePath());
		try {
			dbConnector = DbConnector.getInstance();
			connection = dbConnector.getConnection();
			query = getSqlStatement();
			statement = connection.prepareStatement(query);
			statement.setBlob(1, new FileInputStream(f), f.length());
			statement.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DataAccessException(e.getMessage(), e);
		} finally {
			try {
				if (dbConnector != null) {
					dbConnector.close(statement);
					dbConnector.close(connection);
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				throw new DataAccessException(e.getMessage(), e);
			}
		}
	}

	protected String getSqlStatement() throws DataAccessException {
		StringBuilder sql = new StringBuilder();
		sql.append(CommandConstants.UPDATE).append(c.gettHistograms())
				.append(CommandConstants.SET);
		sql.append(c.getcHistogramsThreshHigh()).append(CommandConstants.EQ)
				.append(hist.getThresholdHigh()).append(CommandConstants.COMMA);
		sql.append(c.getcHistogramsThreshMed()).append(CommandConstants.EQ)
				.append(hist.getThresholdMedium())
				.append(CommandConstants.COMMA);
		sql.append(c.getcHistogramsThreshLow()).append(CommandConstants.EQ)
				.append(hist.getThresholdLow()).append(CommandConstants.COMMA);
		sql.append(c.getcHistogramsMedian()).append(CommandConstants.EQ)
				.append(hist.getMedian()).append(CommandConstants.COMMA);
		sql.append(c.getcHistogramsMedianAbsDev()).append(CommandConstants.EQ)
				.append(hist.getMedianAbsDev()).append(CommandConstants.COMMA);
		sql.append(c.getcHistogramsSitesScored()).append(CommandConstants.EQ)
				.append(hist.getSitesScored()).append(CommandConstants.COMMA);
		sql.append(c.getcHistogramsProteinsScored())
				.append(CommandConstants.EQ).append(hist.getProteinsScored())
				.append(CommandConstants.COMMA);
		sql.append(c.getcHistogramsPlotImage()).append(CommandConstants.EQ)
				.append('?');
		sql.append(CommandConstants.WHERE);
		sql.append(c.getcMotifsId()).append(CommandConstants.EQ)
				.append(hist.getMotif().getId()).append(CommandConstants.AND);
		sql.append(c.getcDataSourcesId()).append(CommandConstants.EQ)
				.append(hist.getDataSource().getId())
				.append(CommandConstants.AND);
		sql.append(c.getcHistogramsTaxonId()).append(CommandConstants.EQ)
				.append(hist.getTaxon().getId());
		return sql.toString();
	}
}
