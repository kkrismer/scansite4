package edu.mit.scansite.server.dataaccess;

import edu.mit.scansite.server.dataaccess.commands.taxon.*;
import edu.mit.scansite.shared.DataAccessException;
import edu.mit.scansite.shared.transferobjects.DataSource;
import edu.mit.scansite.shared.transferobjects.Taxon;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Tobieh
 * @author Konstantin Krismer
 */
public class TaxonDaoImpl extends DaoImpl implements TaxonDao {

	public TaxonDaoImpl(Properties dbAccessConfig, Properties dbConstantsConfig) {
		super(dbAccessConfig, dbConstantsConfig);
	}

	/* (non-Javadoc)
	 * @see edu.mit.scansite.server.dataaccess.TaxonDao#setUseTempTablesForUpdate(boolean)
	 */
	@Override
	public void setUseTempTablesForUpdate(boolean useTempTablesForUpdate) {
		this.useTempTablesForUpdate = useTempTablesForUpdate;
	}

	/* (non-Javadoc)
	 * @see edu.mit.scansite.server.dataaccess.TaxonDao#add(edu.mit.scansite.shared.transferobjects.Taxon, edu.mit.scansite.shared.transferobjects.DataSource)
	 */
	@Override
	public int add(Taxon t, DataSource dataSource) throws DataAccessException {
		int id = t.getId();
		try {
			if (id < 0) {
				Taxon temp = getByName(t.getName(), dataSource);
				if (temp == null) {
					TaxonAddCommand cmd = new TaxonAddCommand(dbAccessConfig,
							dbConstantsConfig, t,
							useTempTablesForUpdate, dataSource);
					id = cmd.execute();
				} else {
					id = temp.getId();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DataAccessException("Adding Taxon failed.", e);
		}
		return id;
	}

	/* (non-Javadoc)
	 * @see edu.mit.scansite.server.dataaccess.TaxonDao#getByName(java.lang.String, edu.mit.scansite.shared.transferobjects.DataSource)
	 */
	@Override
	public Taxon getByName(String name, DataSource dataSource)
			throws DataAccessException {
		TaxonGetCommand cmd = new TaxonGetCommand(dbAccessConfig,
				dbConstantsConfig, name, useTempTablesForUpdate,
				dataSource);
		Taxon t;
		try {
			t = cmd.execute();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DataAccessException("Retrieving Taxon from DB failed.", e);
		}
		return t;
	}

	/* (non-Javadoc)
	 * @see edu.mit.scansite.server.dataaccess.TaxonDao#getById(int, edu.mit.scansite.shared.transferobjects.DataSource)
	 */
	@Override
	public Taxon getById(int id, DataSource dataSource)
			throws DataAccessException {
		Taxon t = null;
		if (id > 0) {
			TaxonGetCommand cmd = new TaxonGetCommand(dbAccessConfig,
					dbConstantsConfig, id, useTempTablesForUpdate,
					dataSource);
			try {
				t = cmd.execute();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new DataAccessException(
						"Retrieving Taxon from DB failed.", e);
			}
		}
		return t;
	}

	/* (non-Javadoc)
	 * @see edu.mit.scansite.server.dataaccess.TaxonDao#delete(java.lang.String, edu.mit.scansite.shared.transferobjects.DataSource)
	 */
	@Override
	public void delete(String name, DataSource dataSource)
			throws DataAccessException {
		TaxonDeleteCommand cmd = new TaxonDeleteCommand(dbAccessConfig,
				dbConstantsConfig, name, useTempTablesForUpdate,
				dataSource);
		try {
			cmd.execute();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DataAccessException("Deleting Taxon from DB failed.", e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.mit.scansite.server.dataaccess.TaxonDao#delete(int, edu.mit.scansite.shared.transferobjects.DataSource)
	 */
	@Override
	public void delete(int id, DataSource dataSource)
			throws DataAccessException {
		TaxonDeleteCommand cmd = new TaxonDeleteCommand(dbAccessConfig,
				dbConstantsConfig, id, useTempTablesForUpdate,
				dataSource);
		try {
			cmd.execute();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DataAccessException("Deleting Taxon from DB failed.", e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.mit.scansite.server.dataaccess.TaxonDao#getAllTaxa(edu.mit.scansite.shared.transferobjects.DataSource)
	 */
	@Override
	public ArrayList<Taxon> getAllTaxa(DataSource dataSource)
			throws DataAccessException {
		TaxonGetAllCommand cmd = new TaxonGetAllCommand(dbAccessConfig,
				dbConstantsConfig, useTempTablesForUpdate,
				dataSource);
		ArrayList<Taxon> ts;
		try {
			ts = cmd.execute();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DataAccessException("Retrieving Taxon from DB failed.", e);
		}
		return ts;
	}

	/* (non-Javadoc)
	 * @see edu.mit.scansite.server.dataaccess.TaxonDao#getSubTaxaIds(edu.mit.scansite.shared.transferobjects.Taxon, edu.mit.scansite.shared.transferobjects.DataSource)
	 */
	@Override
	public Set<Integer> getSubTaxaIds(Taxon taxon, DataSource dataSource)
			throws DataAccessException {
		TaxonIdGetSubTaxaIdCommand command = new TaxonIdGetSubTaxaIdCommand(
				dbAccessConfig, dbConstantsConfig, taxon.getParentTaxonList() + taxon.getId(),
				useTempTablesForUpdate, dataSource);
		try {
			Set<Integer> ids = command.execute();
			ids.add(taxon.getId());
			return ids;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DataAccessException("Retrieving Taxon from DB failed.", e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.mit.scansite.server.dataaccess.TaxonDao#getAllTaxonIds(edu.mit.scansite.shared.transferobjects.DataSource)
	 */
	@Override
	public Set<Integer> getAllTaxonIds(DataSource dataSource)
			throws DataAccessException {
		TaxonIdGetByDataSourceCommand command = new TaxonIdGetByDataSourceCommand(
				dbAccessConfig, dbConstantsConfig, useTempTablesForUpdate, dataSource);
		try {
			return command.execute();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DataAccessException(
					"Retrieving Taxa-IDs from DB failed.", e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.mit.scansite.server.dataaccess.TaxonDao#getAllTaxonIds(edu.mit.scansite.shared.transferobjects.DataSource, java.lang.String)
	 */
	@Override
	public Set<Integer> getAllTaxonIds(DataSource dataSource,
			String speciesRegex) throws DataAccessException {
		TaxonIdGetByDataSourceCommand command = new TaxonIdGetByDataSourceCommand(
				dbAccessConfig, dbConstantsConfig, useTempTablesForUpdate, dataSource, speciesRegex);
		command.setSpeciesOnly(true);
		try {
			return command.execute();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DataAccessException(
					"Retrieving Taxa-IDs from DB failed.", e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.mit.scansite.server.dataaccess.TaxonDao#getSpeciesMap(edu.mit.scansite.shared.transferobjects.DataSource, boolean)
	 */
	@Override
	public Map<Integer, Taxon> getSpeciesMap(DataSource dataSource, boolean b)
			throws DataAccessException {
		TaxonGetSpeciesMapCommand cmd = new TaxonGetSpeciesMapCommand(
				dbAccessConfig, dbConstantsConfig, useTempTablesForUpdate, dataSource);
		try {
			return cmd.execute();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DataAccessException("Retrieving Taxon from DB failed.", e);
		}
	}
}
