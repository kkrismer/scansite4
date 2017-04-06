package mit.edu.scansite.examples.proteinscan;

import mit.edu.scansite.examples.ExampleUtils;

import java.util.List;

import static mit.edu.scansite.examples.ExampleUtils.baseURL;

/**
 * @author Thomas Bernwinkler
 * Last edited 4/4/2017
 * Provided by Yaffe Lab, Koch Institute, MIT
 */

public class ProteinScanExample4 {
    private static final String param1 = "/identifier=";
    private static final String param2 = "/dsshortname=";
    private static final String param3 = "/motifclass=";
    private static final String param4 = "/motifshortnames=";
    private static final String param5 = "/stringency=";
    private static final String param6 = "/referenceproteome=";

    private static final String service = "proteinscan";
    private static final String outputFileName = "ProteinScanExample4Output.txt";

    //../proteinscan/identifier=vav_human/dsshortname=swissprot/motifclass=MAMMALIAN/motifshortnames=Lck_Kin~Shc_SH2/stringency=High/referenceproteome=Vertebrata
    public static void main(String[] args) {
        String identifier = "vav_human";
        String dsshortname = "swissprot";
        String motifclass = "MAMMALIAN";
        String motifshortnames = "Lck_Kin~Shc_SH2";
        String stringency = "High";
        String referenceproteome = "Vertebrata";

        String urlString = baseURL + service
                + param1 + identifier
                + param2 + dsshortname
                + param3 + motifclass
                + param4 + motifshortnames
                + param5 + stringency
                + param6 + referenceproteome;

        List<String> results = ExampleUtils.runRequest(urlString);
        String resultContent = "";
        for (String result : results) {
            resultContent += result + "\n";
        }
        ExampleUtils.writeToFile(resultContent, outputFileName);
        System.out.println(resultContent);
    }
}