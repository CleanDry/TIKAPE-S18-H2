package H2.util;

import H2.domain.Kysymys;
import H2.domain.Vastaus;
import java.util.ArrayList;
import java.util.List;


public class Utilities {
    public static String siistitty(String syote) {
        syote = syote.trim().toLowerCase();
        
        if (syote.isEmpty()) {
            return syote;
        }
        
        syote = syote.substring(0, 1).toUpperCase() + syote.substring(1);
        
        return syote;
    }
    
    public static List<Vastaus> etsiPoistettavatVastaukset(int id, List<Vastaus> vastaukset) {
        List<Vastaus> poistettavat = new ArrayList<>();
        for (Vastaus vastaus : vastaukset) {
            if (vastaus.getId() == id) {
                poistettavat.add(vastaus);
            }
        }
        return poistettavat;
    }
    
    public static List<Kysymys> etsiPoistettavatKysymykset(int id, List<Kysymys> kysymykset) {
        List<Kysymys> poistettavat = new ArrayList<>();
        for (Kysymys kysymys : kysymykset) {
            if (kysymys.getId() == id) {
                poistettavat.add(kysymys);
            }
        }
        return poistettavat;
    }
}
