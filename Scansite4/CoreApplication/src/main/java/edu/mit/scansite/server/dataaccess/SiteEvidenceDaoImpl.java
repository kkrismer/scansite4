package edu.mit.scansite.server.dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import edu.mit.scansite.server.dataaccess.commands.evidence.EvidenceResourceAddCommand;
import edu.mit.scansite.server.dataaccess.commands.evidence.EvidenceResourceGetAllCommand;
import edu.mit.scansite.server.dataaccess.commands.evidence.SiteEvidenceAddCommand;
import edu.mit.scansite.server.dataaccess.commands.evidence.SiteEvidenceGetAllCommand;
import edu.mit.scansite.server.dataaccess.commands.evidence.SiteEvidenceGetCommand;
import edu.mit.scansite.shared.DataAccessException;
import edu.mit.scansite.shared.transferobjects.EvidenceResource;

/**
 * @author Tobieh
 * @author Konstantin Krismer
 */
public class SiteEvidenceDaoImpl extends DaoImpl implements SiteEvidenceDao {

	public SiteEvidenceDaoImpl(Properties dbAccessConfig, Properties dbConstantsConfig) {
		super(dbAccessConfig, dbConstantsConfig);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see edu.mit.scansite.server.dataaccess.SiteEvidenceDao#getSiteEvidence(java
	 * .util.Set, java.lang.String)
	 */
	@Override
	public List<EvidenceResource> getSiteEvidence(Set<String> accessions, String site) throws DataAccessException {
		try {
			SiteEvidenceGetCommand cmd = new SiteEvidenceGetCommand(dbAccessConfig, dbConstantsConfig, accessions,
					site);
			List<EvidenceResource> siteResources = cmd.execute();
			if (siteResources != null && !siteResources.isEmpty()) {
				List<EvidenceResource> allResources = getAllResources();
				for (EvidenceResource rs : siteResources) {
					for (EvidenceResource r : allResources) {
						if (rs.getResourceName().equals(r.getResourceName())) {
							rs.setUri(r.getUri());
						}
					}
				}
			}
			return siteResources;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DataAccessException(e.getMessage(), e);
		}
	}

	private List<EvidenceResource> getAllResources() throws DataAccessException {
		try {
			EvidenceResourceGetAllCommand cmd = new EvidenceResourceGetAllCommand(dbAccessConfig, dbConstantsConfig);
			return cmd.execute();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DataAccessException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see edu.mit.scansite.server.dataaccess.SiteEvidenceDao#addSite(java.lang.
	 * String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addSite(String site, String accession, String resource) throws DataAccessException {
		try {
			SiteEvidenceAddCommand cmd = new SiteEvidenceAddCommand(dbAccessConfig, dbConstantsConfig, accession, site,
					resource);
			cmd.execute();
		} catch (Exception e) {
			throw new DataAccessException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * edu.mit.scansite.server.dataaccess.SiteEvidenceDao#addResource(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public void addResource(String resource, String resourceLink) throws DataAccessException {
		try {
			EvidenceResourceAddCommand cmd = new EvidenceResourceAddCommand(dbAccessConfig, dbConstantsConfig, resource,
					resourceLink);
			cmd.execute();
		} catch (Exception e) {
			throw new DataAccessException(e.getMessage(), e);
		}
	}

	@Override
	public HashMap<String, List<String>> getEvidenceMap() throws DataAccessException {
		List<EvidenceResource> evidenceResources = getAllSiteEvidence();
		HashMap<String, List<String>> evidenceMap = new HashMap<>();
		for (EvidenceResource evidenceResource : evidenceResources) {
			String accession = evidenceResource.getProteinAccession();
			if (!evidenceMap.containsKey(accession)) {
				evidenceMap.put(evidenceResource.getProteinAccession(), new ArrayList<>());
			}
			evidenceMap.get(accession).add(evidenceResource.getSite());
		}
		return evidenceMap;
	}

	private List<EvidenceResource> getAllSiteEvidence() throws DataAccessException {
		try {
			SiteEvidenceGetAllCommand cmd = new SiteEvidenceGetAllCommand(dbAccessConfig, dbConstantsConfig);
			List<EvidenceResource> siteResources = cmd.execute();
			// if (siteResources != null && !siteResources.isEmpty()) {
			// List<EvidenceResource> allResources = getAllResources();
			// for (EvidenceResource rs : siteResources) {
			// for (EvidenceResource r : allResources) {
			// if (rs.getResourceName().equals(r.getResourceName())) {
			// rs.setUri(r.getUri());
			// }
			// }
			// }
			// }
			return siteResources;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DataAccessException(e.getMessage(), e);
		}
	}
}
