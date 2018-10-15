
package H2.domain;

import static H2.util.Utilities.siistitty;
import java.util.Objects;

public class Kysymys {
    private int id;
    private String kurssi;
    private String aihe;
    private String kysymysteksti;

    public Kysymys(int id, String kurssi, String aihe, String kysymysteksti) {
        this.id = id;
        this.kurssi = kurssi;
        this.aihe = aihe;
        this.kysymysteksti = kysymysteksti;
    }
    
    public Kysymys(String kurssi, String aihe, String kysymysteksti) {
        this.kurssi = kurssi;
        this.aihe = aihe;
        this.kysymysteksti = kysymysteksti;
    }

    public int getId() {
        return id;
    }
    
    public String getAihe() {
        return aihe;
    }

    public String getKurssi() {
        return kurssi;
    }

    public String getKysymysteksti() {
        return kysymysteksti;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public void setAihe(String aihe) {
        this.aihe = aihe;
    }

    public void setKurssi(String kurssi) {
        this.kurssi = kurssi;
    }

    public void setKysymysteksti(String kysymysteksti) {
        this.kysymysteksti = kysymysteksti;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (obj.getClass() != this.getClass() ) {
            return false;
        }
        
        Kysymys kysymys = (Kysymys) obj;
                
        return this.kurssi.equals(siistitty(kysymys.kurssi)) 
                && this.aihe.equals(siistitty(kysymys.aihe)) 
                && this.kysymysteksti.equals(siistitty(kysymys.kysymysteksti));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.id;
        hash = 89 * hash + Objects.hashCode(this.kurssi);
        hash = 89 * hash + Objects.hashCode(this.aihe);
        hash = 89 * hash + Objects.hashCode(this.kysymysteksti);
        return hash;
    }

    @Override
    public String toString() {
        return this.id + " | " + this.kurssi + " | " + this.aihe + " | " + this.kysymysteksti;
    }
    
    public boolean samaKurssi(String kurssi) {
        
        return siistitty(this.kurssi).equals(siistitty(kurssi));
    }
    
    public boolean samaKurssiJaAihe(String kurssi, String aihe) {
        
        return siistitty(this.kurssi).equals(siistitty(kurssi)) 
                && siistitty(this.aihe).equals(siistitty(aihe));
    }
    
    
}
