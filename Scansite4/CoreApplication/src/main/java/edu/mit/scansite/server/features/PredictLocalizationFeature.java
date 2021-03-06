package edu.mit.scansite.server.features;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.scansite.server.ServiceLocator;
import edu.mit.scansite.server.dataaccess.DaoFactory;
import edu.mit.scansite.server.dataaccess.LocalizationDao;
import edu.mit.scansite.server.dataaccess.MotifDao;
import edu.mit.scansite.shared.DataAccessException;
import edu.mit.scansite.shared.dispatch.features.PredictLocalizationResult;
import edu.mit.scansite.shared.dispatch.features.PredictMotifsLocalizationResult;
import edu.mit.scansite.shared.dispatch.features.PredictProteinsLocalizationResult;
import edu.mit.scansite.shared.transferobjects.DataSource;
import edu.mit.scansite.shared.transferobjects.LightWeightLocalization;
import edu.mit.scansite.shared.transferobjects.LightWeightProtein;
import edu.mit.scansite.shared.transferobjects.Localization;
import edu.mit.scansite.shared.transferobjects.Motif;
import edu.mit.scansite.shared.transferobjects.MotifClass;
import edu.mit.scansite.shared.transferobjects.User;

/**
 * @author Konstantin Krismer
 */
public class PredictLocalizationFeature {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public PredictLocalizationFeature() {
	}

	public PredictLocalizationResult doPredictProteinLocalization(DataSource localizationDataSource,
			LightWeightProtein protein) throws DataAccessException {
		LocalizationDao dao = ServiceLocator.getDaoFactory().getLocalizationDao();

		PredictProteinsLocalizationResult result = new PredictProteinsLocalizationResult();
		result.setLocalizationDataSource(localizationDataSource);
		result.setProtein(protein);
		try {
			result.setTotalProteinLocalizations(dao.getLocalizationCount(localizationDataSource));
			Localization localization = dao.retrieveLocalization(localizationDataSource, protein);
			if (localization == null) {
				result.setSuccess(false);
				result.setErrorMessage("No localization information found for specified protein");
				logger.warn("No localization information found for specified protein");
			} else {
				result.setLocalization(localization);
			}
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);
			result.setSuccess(false);
			result.setErrorMessage("Server-side error");
		}
		return result;
	}

	public PredictLocalizationResult doPredictMotifsLocalization(DataSource localizationDataSource,
			MotifClass motifClass, User user) throws DataAccessException {
		DaoFactory factory = ServiceLocator.getDaoFactory();
		LocalizationDao dao = factory.getLocalizationDao();
		MotifDao motifDao = factory.getMotifDao();

		PredictMotifsLocalizationResult result = new PredictMotifsLocalizationResult();
		result.setLocalizationDataSource(localizationDataSource);
		result.setMotifClass(motifClass);
		try {
			result.setTotalProteinLocalizations(dao.getLocalizationCount(localizationDataSource));
			List<Motif> motifs = motifDao.getAll(motifClass, user);
			Map<Motif, LightWeightLocalization> localizations = dao
					.retrieveLocalizationsForMotifs(localizationDataSource, motifs);
			if (localizations == null) {
				result.setSuccess(false);
				result.setErrorMessage("No localization information found for specified motif class");
				logger.warn("No localization information found for specified motif class");
			} else {
				result.setLocalizations(localizations);
			}
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);
			result.setSuccess(false);
			result.setErrorMessage("Server-side error");
		}
		return result;
	}

	public Localization doPredictMotifLocalization(DataSource localizationDataSource, Motif motif)
			throws DataAccessException {
		LocalizationDao dao = ServiceLocator.getDaoFactory().getLocalizationDao();
		try {
			return dao.retrieveLocalizationForMotif(localizationDataSource, motif);
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
}
