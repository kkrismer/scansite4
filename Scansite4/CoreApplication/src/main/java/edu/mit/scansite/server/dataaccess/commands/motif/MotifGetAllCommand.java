package edu.mit.scansite.server.dataaccess.commands.motif;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import edu.mit.scansite.server.dataaccess.commands.CommandConstants;
import edu.mit.scansite.server.dataaccess.commands.DbQueryCommand;
import edu.mit.scansite.shared.DataAccessException;
import edu.mit.scansite.shared.transferobjects.AminoAcid;
import edu.mit.scansite.shared.transferobjects.LightWeightMotifGroup;
import edu.mit.scansite.shared.transferobjects.Motif;
import edu.mit.scansite.shared.transferobjects.MotifClass;
import edu.mit.scansite.shared.transferobjects.User;
import edu.mit.scansite.shared.transferobjects.User.UserGroup;

/**
 * @author Tobieh
 * @author Konstantin Krismer
 */
public class MotifGetAllCommand extends DbQueryCommand<List<Motif>> {

	private AminoAcid aas[] = AminoAcid.values();
	private Integer groupId = null;
	private Set<String> motifNicks;
	private MotifClass motifClass = MotifClass.MAMMALIAN;
	private User user = null;
	private boolean onlyUserMotifs = false;

	public MotifGetAllCommand(Properties dbAccessConfig, Properties dbConstantsConfig, Set<String> motifNicks,
			MotifClass motifClass, User user, boolean onlyUserMotifs) {
		super(dbAccessConfig, dbConstantsConfig);
		this.motifNicks = motifNicks;
		this.user = user;
		this.motifClass = motifClass;
		this.onlyUserMotifs = onlyUserMotifs;
	}

	public MotifGetAllCommand(Properties dbAccessConfig, Properties dbConstantsConfig, int groupId,
			MotifClass motifClass, User user, boolean onlyUserMotifs) {
		super(dbAccessConfig, dbConstantsConfig);
		this.groupId = groupId;
		this.user = user;
		this.motifClass = motifClass;
		this.onlyUserMotifs = onlyUserMotifs;
	}

	@Override
	protected List<Motif> doProcessResults(ResultSet result) throws DataAccessException {
		List<Motif> ms = new LinkedList<Motif>();
		Set<Integer> ids = new HashSet<Integer>();
		try {
			Motif m = null;
			while (result.next()) {
				int motifId = result.getInt(c.getcMotifsId());
				if (!ids.contains(motifId)) {
					ids.add(motifId);
					if (m != null) {
						ms.add(m);
					}
					m = new Motif();
					m.setId(motifId);
					m.setDisplayName(result.getString(c.getcMotifsDisplayName()));
					m.setShortName(result.getString(c.getcMotifsShortName()));
					LightWeightMotifGroup group = new LightWeightMotifGroup();
					group.setId(result.getInt(c.getcMotifGroupsId()));
					m.setGroup(group);
					m.setSubmitter(result.getString(c.getcUsersEmail()));
					m.setOptimalScore(result.getDouble(c.getcMotifsOptScore()));
					m.setPublic(result.getBoolean(c.getcMotifsIsPublic()));
					m.setMotifClass(MotifClass.getDbValue(result.getString(c.getcMotifsMotifClass())));
				}
				int position = result.getInt(c.getcMatrixDataPosition());
				m.setValue(AminoAcid._N, position, result.getDouble(c.getcMatrixDataScoreStart()));
				m.setValue(AminoAcid._C, position, result.getDouble(c.getcMatrixDataScoreEnd()));
				for (AminoAcid aa : aas) {
					if (!aa.equals(AminoAcid._C) && !aa.equals(AminoAcid._N)) {
						m.setValue(aa, position, result.getDouble(c.getcMatrixDataScorePrefix()
								+ MotifDataAddCommand.evaluateOneLetterCode(aa.getOneLetterCode())));
					}
				}
			}
			if (ids.size() > ms.size() && m != null) {
				ms.add(m);
			}
		} catch (Exception e) {
			throw new DataAccessException(e.getMessage());
		}
		return ms;
	}

	@Override
	protected String doGetSqlStatement() throws DataAccessException {
		StringBuilder sql = new StringBuilder();

		sql.append(CommandConstants.SELECT).append(c.getcMotifsId()).append(CommandConstants.COMMA)
				.append(c.getcMotifsDisplayName()).append(CommandConstants.COMMA).append(c.getcMotifsShortName())
				.append(CommandConstants.COMMA).append(c.getcMotifGroupsId()).append(CommandConstants.COMMA)
				.append(c.getcUsersEmail()).append(CommandConstants.COMMA).append(c.getcMotifsOptScore())
				.append(CommandConstants.COMMA).append(c.getcMotifsIsPublic()).append(CommandConstants.COMMA)
				.append(c.getcMatrixDataPosition()).append(CommandConstants.COMMA).append(c.getcMotifsMotifClass())
				.append(CommandConstants.COMMA).append(c.getcMatrixDataScoreEnd()).append(CommandConstants.COMMA)
				.append(c.getcMatrixDataScoreStart());
		for (AminoAcid aa : aas) {
			if (!aa.equals(AminoAcid._C) && !aa.equals(AminoAcid._N)) {
				sql.append(CommandConstants.COMMA).append(c.getcMatrixDataScorePrefix())
						.append(MotifDataAddCommand.evaluateOneLetterCode(aa.getOneLetterCode()));
			}
		}
		sql.append(CommandConstants.FROM).append(c.gettMotifs()).append(CommandConstants.INNERJOIN)
				.append(c.gettMotifMatrixData()).append(CommandConstants.USING).append(CommandConstants.LPAR)
				.append(c.getcMotifsId()).append(CommandConstants.RPAR).append(CommandConstants.WHERE)
				.append(c.getcMotifsMotifClass()).append(CommandConstants.EQ)
				.append(CommandConstants.enquote(motifClass.getDatabaseEntry()));
		if (user == null) {
			sql.append(CommandConstants.AND).append(c.getcMotifsIsPublic()).append(CommandConstants.EQ).append(1);
		} else if (user.getUserGroup() == UserGroup.ADMIN) {
			// admins see all motifs
		} else {
			// collaborators and advanced users only see public motifs and their own motifs
			sql.append(CommandConstants.AND).append(" ( ");
			sql.append(c.getcMotifsIsPublic()).append(CommandConstants.EQ).append(1).append(CommandConstants.OR);
			sql.append(c.getcUsersEmail()).append(CommandConstants.EQ)
					.append(CommandConstants.enquote(user.getEmail()));
			sql.append(" ) ");
		}
		if (onlyUserMotifs) {
			String email = user == null ? "" : user.getEmail();
			sql.append(CommandConstants.AND).append(c.getcUsersEmail()).append(CommandConstants.EQ)
					.append(CommandConstants.enquote(email));
		}
		if (groupId != null) {
			sql.append(CommandConstants.AND).append(c.getcMotifGroupsId()).append(CommandConstants.EQ).append(groupId);
		}
		if (motifNicks != null && !motifNicks.isEmpty()) {
			sql.append(CommandConstants.AND).append(" ( ");
			boolean first = true;
			for (String nick : motifNicks) {
				if (first) {
					first = false;
				} else {
					sql.append(CommandConstants.OR);
				}
				sql.append(c.getcMotifsShortName()).append(CommandConstants.EQ).append(CommandConstants.enquote(nick));
			}
			sql.append(" ) ");
		}
		sql.append(CommandConstants.ORDERBY).append(c.getcMotifsDisplayName()).append(CommandConstants.COMMA)
				.append(c.getcMotifsId()).append(CommandConstants.COMMA).append(c.getcMatrixDataPosition());
		return sql.toString();
	}

}
