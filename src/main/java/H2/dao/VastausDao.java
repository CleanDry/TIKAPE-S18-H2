
package H2.dao;

import H2.database.Database;
import H2.domain.Kysymys;
import H2.domain.Vastaus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VastausDao implements Dao<Vastaus, Integer> {
    private Database db;

    public VastausDao(Database db) {
        this.db = db;
    }
    
    @Override
    public Vastaus findOne(Integer id) throws SQLException {
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Vastaus WHERE vastausId = ?");
            stmt.setInt(1, id);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                return new Vastaus(rs.getInt("vastausId"), rs.getInt("kysymysId"), rs.getString("vastausteksti"), rs.getBoolean("oikein"));
            }
            
            return null;
        }
    }

    @Override
    public List<Vastaus> findAll() throws SQLException {
        List<Vastaus> vastaukset = new ArrayList<>();
        
        try (Connection conn = db.getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT * FROM Vastaus").executeQuery();
                
            KysymysDao kd = new KysymysDao(db);

            while (rs.next()) {
                vastaukset.add(new Vastaus(rs.getInt("vastausId"), rs.getInt("kysymysId"), rs.getString("vastausteksti"), rs.getBoolean("oikein")));
            }
        }

        return vastaukset;
    }
    
    public List<Vastaus> findByKysymys(int kysymysId) throws SQLException {
        List<Vastaus> vastaukset = new ArrayList<>();
        
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Vastaus, Kysymys WHERE Vastaus.kysymysId = Kysymys.kysymysId AND Kysymys.kysymysId = ?");
            stmt.setInt(1, kysymysId);

            ResultSet rs = stmt.executeQuery();
            KysymysDao kd = new KysymysDao(db);

            while (rs.next()) {
                vastaukset.add(new Vastaus(rs.getInt("vastausId"), rs.getInt("kysymysId"), rs.getString("vastausteksti"), rs.getBoolean("oikein")));
            }
        }

        return vastaukset;
    }

    @Override
    public void saveOrUpdate(Vastaus object) throws SQLException {
        try (Connection conn = db.getConnection()) {
            if (this.findOne(object.getId()) != null) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE Vastaus \n" +
                    "	SET vastausteksti = ?, \n" +
                    "	oikein = ? \n" +
                    "	WHERE vastausId = ?\n" +
                    ";");
                stmt.setString(1, object.getVastausteksti());
                stmt.setBoolean(2, object.isOikein());
                stmt.setInt(3, object.getId());
                
                stmt.executeUpdate();
            } else {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO Vastaus (vastausId, kysymysId, vastausteksti, oikein) VALUES (?,?,?,?);");
                stmt.setInt(1, object.getId());
                stmt.setInt(2, object.getKysymysId());
                stmt.setString(3, object.getVastausteksti());
                stmt.setBoolean(4, object.isOikein());
                
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Vastaus WHERE Vastaus.vastausId = ?");
            stmt.setInt(1, id);

            stmt.executeUpdate();
        }
    }
    
    public boolean validoiJaLuoUusi(Vastaus vastaus) throws SQLException {
        
        System.out.println("validoiJaLuoUusi: " + vastaus.getVastausteksti());
        
        if (vastaus.getVastausteksti().isEmpty()) {
            System.out.println("validoiJaLuoUusi: tekstikenttä oli tyhjä");
            return false;
        }
        
        if (vastaus.getVastausteksti().length() > 1000) {
            System.out.println("validoiJaLuoUusi: vastausteksti oli liian pitkä");
            return false;
        }
        
        System.out.println("validoiJaLuoUusi: tallennetaan tietokantaan");
        this.saveOrUpdate(vastaus);
        return true;
    }
}
