
package H2.dao;

import H2.database.Database;
import H2.domain.Kysymys;
import static H2.util.Utilities.siistitty;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KysymysDao implements Dao<Kysymys, Integer> {
    private Database db;

    public KysymysDao(Database db) {
        this.db = db;
    }
    
    @Override
    public Kysymys findOne(Integer id) throws SQLException {
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Kysymys WHERE kysymysId = ?");
            stmt.setInt(1, id);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                return new Kysymys(rs.getInt("kysymysId"), rs.getString("kurssi"), rs.getString("aihe"), rs.getString("kysymysteksti"));
            }
            
            return null;
        }
    }

    @Override
    public List<Kysymys> findAll() throws SQLException {
        List<Kysymys> kysymykset = new ArrayList<>();
        
        try (Connection conn = db.getConnection()) {
                ResultSet rs = conn.prepareStatement("SELECT * FROM Kysymys").executeQuery();

            while (rs.next()) {
                kysymykset.add(new Kysymys(rs.getInt("kysymysId"), rs.getString("kurssi"), rs.getString("aihe"), rs.getString("kysymysteksti")));
            }
        }

        return kysymykset;
        
    }
    
    @Override
    public void saveOrUpdate(Kysymys object) throws SQLException {
        try (Connection conn = db.getConnection()) {
            if (this.findOne(object.getId()) != null) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE Kysymys \n" +
                    "	SET kurssi = ?, \n" +
                    "	aihe = ?, \n" +
                    "	kysymysteksti = ?\n" +
                    "	WHERE kysymysId = ?\n" +
                    "; ");
                stmt.setString(1, object.getKurssi());
                stmt.setString(2, object.getAihe());
                stmt.setString(3, object.getKysymysteksti());
                stmt.setInt(4, object.getId());
                
                stmt.executeUpdate();
            } else {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO Kysymys (kurssi, aihe, kysymysteksti) VALUES (?,?,?);");
                stmt.setString(1, object.getKurssi());
                stmt.setString(2, object.getAihe());
                stmt.setString(3, object.getKysymysteksti());
                
                stmt.executeUpdate();
            }
        }
        
    }

    @Override
    public void delete(Integer id) throws SQLException {
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Kysymys WHERE Kysymys.kysymysId = ?");
            stmt.setInt(1, id);

            stmt.executeUpdate();
        }
    }
    
    public Map<String, List<String>> findByKurssi() throws SQLException {
        Map<String, List<String>> palautettava = new HashMap<>();
        List<String> uniikitKurssit = new ArrayList<>();
        
        try (Connection conn = db.getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT DISTINCT kurssi FROM kysymys;").executeQuery();
            
            while (rs.next()) {
                uniikitKurssit.add(rs.getString("kurssi"));
            }
            
            for (String kurssi : uniikitKurssit) {
                PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT aihe FROM Kysymys WHERE kurssi = ?");
                stmt.setString(1, kurssi);
                ResultSet result = stmt.executeQuery();
                
                List<String> kurssinAiheet = new ArrayList<>();
                
                while (result.next()) {
                    kurssinAiheet.add(result.getString("aihe"));
                }
                
                palautettava.put(kurssi, kurssinAiheet);
            }
        }
        
        return palautettava;
    }
    
    public List<String> findAiheet() throws SQLException {
        List<String> uniikitAiheet = new ArrayList<>();
        
        try (Connection conn = db.getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT DISTINCT aihe FROM kysymys;").executeQuery();
            
            while (rs.next()) {
                uniikitAiheet.add(rs.getString("aihe"));
            }
        }
        
        return uniikitAiheet;
    }
    
    public List<String> findKurssit() throws SQLException {
        List<String> uniikitKurssit = new ArrayList<>();
        
        try (Connection conn = db.getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT DISTINCT kurssi FROM kysymys;").executeQuery();
            
            while (rs.next()) {
                uniikitKurssit.add(rs.getString("kurssi"));
            }
        }
        
        return uniikitKurssit;
    }
    
    public boolean validoiJaLuoUusi(Kysymys kysymys) throws SQLException {
        
        System.out.println("validoiJaLuoUusi: " + kysymys.getKurssi());
        System.out.println("validoiJaLuoUusi: " + kysymys.getAihe());
        System.out.println("validoiJaLuoUusi: " + kysymys.getKysymysteksti());
        
        if (kysymys.getKurssi().isEmpty() || kysymys.getAihe().isEmpty() || kysymys.getKysymysteksti().isEmpty()) {
            System.out.println("validoiJaLuoUusi: jokin teksteistä oli tyhjä");
            return false;
        }
        
        if (kysymys.getKysymysteksti().length() > 1000) {
            System.out.println("validoiJaLuoUusi: kysymysteksti oli liian pitkä");
            return false;
        }
        
        if (kysymys.getAihe().length() > 60 || kysymys.getKurssi().length() > 60) {
            System.out.println("validoiJaLuoUusi: aiheen tai kurssin nimi oli liian pitkä");
            return false;
        }
        
        System.out.println("validoiJaLuoUusi: tallennetaan tietokantaan");
        this.saveOrUpdate(kysymys);
        return true;
    }

}
