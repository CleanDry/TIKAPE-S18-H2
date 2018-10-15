
package H2.domain;

import static H2.util.Utilities.siistitty;
import java.util.Objects;

public class Vastaus {
    private int id;
    private int kysymysId;
    private String vastausteksti;
    private boolean oikein;

    public Vastaus(int id, int kysymysId, String vastausteksti, boolean oikein) {
        this.id = id;
        this.kysymysId = kysymysId;
        this.vastausteksti = vastausteksti;
        this.oikein = oikein;
    }
    
    public Vastaus(int kysymysId, String vastausteksti, boolean oikein) {
        this.kysymysId = kysymysId;
        this.vastausteksti = vastausteksti;
        this.oikein = oikein;
    }

    public int getId() {
        return id;
    }

    public int getKysymysId() {
        return kysymysId;
    }
    
    public String getVastausteksti() {
        return vastausteksti;
    }

    public boolean isOikein() {
        return oikein;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setKysymysId(int kysymysId) {
        this.kysymysId = kysymysId;
    }
    
    public void setOikein(boolean oikein) {
        this.oikein = oikein;
    }

    public void setVastausteksti(String vastausteksti) {
        this.vastausteksti = vastausteksti;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (obj.getClass() != this.getClass() ) {
            return false;
        }
        
        Vastaus vastaus = (Vastaus) obj;
        
        return vastaus.kysymysId == this.kysymysId && siistitty(vastaus.vastausteksti).equals(siistitty(this.vastausteksti)) && vastaus.oikein == this.oikein;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.id;
        hash = 97 * hash + this.kysymysId;
        hash = 97 * hash + Objects.hashCode(this.vastausteksti);
        hash = 97 * hash + (this.oikein ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.id + " | " + this.kysymysId + " | " + this.vastausteksti + " | " + this.oikein;
    }
    
    
}
