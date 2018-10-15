package H2.App;

import H2.dao.KysymysDao;
import H2.dao.VastausDao;
import H2.database.Database;
import H2.domain.Kysymys;
import H2.domain.Vastaus;
import static H2.util.Utilities.poistaKysymysListalta;
import static H2.util.Utilities.poistaVastausListalta;
import static H2.util.Utilities.siistitty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class Kysymyspankki {

    public static void main(String[] args) throws Exception {
        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        if (System.getenv("PORT") != null) {
            Spark.port(Integer.valueOf(System.getenv("PORT")));
        }
        
        Database database = new Database("jdbc:sqlite:Kysymyspankki.db");
        KysymysDao kysymysDao = new KysymysDao(database);
        VastausDao vastausDao = new VastausDao(database);
        
        List<Kysymys> kysymykset = kysymysDao.findAll();
        List<Vastaus> vastaukset = vastausDao.findAll();
        
        Spark.get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            
            map.put("kysymykset", kysymykset);
            map.put("kurssit", kysymysDao.findKurssit());
            map.put("aiheet", kysymysDao.findAiheet());
            map.put("kurssitJaAiheet", kysymysDao.findByKurssi());
            
            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        Spark.post("/uusiKysymys", (req, res) -> {
            String kurssi = siistitty(req.queryParams("kurssi"));
            String aihe = siistitty(req.queryParams("aihe"));
            String kysymysteksti = siistitty(req.queryParams("kysymysteksti"));
            
            Kysymys kysymys = new Kysymys(kysymykset.size() +1, kurssi, aihe, kysymysteksti);
            System.out.println(kysymys);
            
            if (!kysymykset.contains(kysymys)) {
                System.out.println("Ei ole vielä listalla");
                
                if (!kysymysDao.validoiJaLuoUusi(kysymys)) {
                    System.out.println("Uudelleenohjataan virhesyötteeseen");
                    res.redirect("/virhesyote");
                    return "";
                }
                
                System.out.println("Lisätään kysymys listalle");
                kysymykset.add(kysymys);
            }
            
            System.out.println("Uudelleenohjataan takaisin");
            res.redirect("/");
            return "";
        });
        
        Spark.get("/kysymys/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            
            Integer kysymysId = Integer.parseInt(req.params(":id"));
            map.put("kysymys", kysymysDao.findOne(kysymysId));
            map.put("vastaukset", vastausDao.findByKysymys(kysymysId));
            
            return new ModelAndView(map, "kysymys");
        }, new ThymeleafTemplateEngine());
        
        Spark.post("/kysymys/poistaKysymys/:id", (req, res) -> {
            // kerätään parametri
            int kysymysId = Integer.parseInt(req.params(":id"));
            System.out.println("kysymysId: " + kysymysId);
            
            // haetaan parametrin mukainen kysymys
            Kysymys kysymys = kysymysDao.findOne(kysymysId);
            System.out.println("Kysymys: " + kysymys);
            
            // etsitään kysymykseen liittyvät vastaukset
            for (Vastaus vastaus :vastausDao.findByKysymys(kysymysId)) {
                
                // poistetaan kysymykseen liittyvät vastaukset
                System.out.println("Poistetaan vastaus: " + vastaus);
                vastausDao.delete(vastaus.getId());
                poistaVastausListalta(vastaus.getId(), vastaukset);
            }
            
            // poistetaan varsinainen kysymys
            System.out.println("Poistetaan kysymys: " + kysymys);
            kysymysDao.delete(kysymysId);
            poistaKysymysListalta(kysymysId, kysymykset);
            
            res.redirect("/");
            return "";
        });
        
        Spark.post("/kysymys/:id/uusiVastaus", (req, res) -> {            
            // kerätään parametrit
            int kysymysId = Integer.parseInt(req.params(":id"));
            String vastausteksti = siistitty(req.queryParams("vastausteksti"));
                // tarkastetaan boolean-syötteen oikeellisuus ennen keräystä
            if (!(siistitty(req.queryParams("oikein")).equals("True") || siistitty(req.queryParams("oikein")).equals("False"))) {
                System.out.println("uusiVastaus: Boolean-syöte oli virheellinen");
                res.redirect("/virhesyote");
                return "";
            }
            boolean oikein = Boolean.parseBoolean(siistitty(req.queryParams("oikein")));
            
            // luodaan vastausolio
            Vastaus vastaus = new Vastaus(vastaukset.size() +1, kysymysId, vastausteksti, oikein);
            System.out.println("uusiVastaus: Vastaus: " + vastaus);
            
            // vertaillaan vastauksen olemassaoloa vastaukset-listaan
            if (!vastaukset.contains(vastaus)) {
                System.out.println("uusiVastaus: Ei ole vielä listalla");
                
                // jos ei ole listalla validoidaan tekstikentät
                // jos kentät ok, lisätään tietokantaan
                if (!vastausDao.validoiJaLuoUusi(vastaus)) {
                    System.out.println("uusiVastaus: Uudelleenohjataan virhesyötteeseen");
                    res.redirect("/virhesyote");
                    return "";
                }
                
                // lisätään vastaus myös listaan
                System.out.println("uusiVastaus: Lisätään kysymys listalle");
                vastaukset.add(vastaus);
            }
            
            System.out.println("uusiVastaus: Uudelleenohjataan takaisin");
            res.redirect("/kysymys/" + kysymysId);
            return "";
        });
        
        Spark.post("/kysymys/:kysymysId/poistaVastaus/:vastausId", (req, res) -> {
            int kysymysId = Integer.parseInt(req.params(":kysymysId"));
            int id = Integer.parseInt(req.params(":vastausId"));
            System.out.println("Id: " + id);
            
            Vastaus vastaus = vastausDao.findOne(id);
            
            System.out.println("Poistetaan vastaus: " + vastaus);
            vastausDao.delete(id);
            poistaVastausListalta(id, vastaukset);
            
            res.redirect("/kysymys/" + kysymysId);
            return "";
        });
        
        
        
        Spark.get("/virhesyote", (req, res) -> {
            HashMap map = new HashMap<>();
            
            return new ModelAndView(map, "virhesyote");
        }, new ThymeleafTemplateEngine());
        
        
        // TESTIT
        
//        Kysymys k1 = new Kysymys(kysymykset.size()+1, "TIKAPE", "Harjoitustyö 2", "Paljonko aikaa menee websovellukseen?");
//        if (!kysymykset.contains(k1)) {
//            kysymysDao.saveOrUpdate(k1);
//            kysymykset.add(k1);
//        }
//        
//        System.out.println("Kysymykset");
//        for (Kysymys kysymys : kysymykset) {
//            System.out.println(kysymys);
//        }
//        
//        Vastaus k1v1 = new Vastaus(vastaukset.size()+1, kysymykset.get(0).getId(), "3 tuntia", false);
//               
//        if (!vastaukset.contains(k1v1)) {
//            vastausDao.saveOrUpdate(k1v1);
//            vastaukset.add(k1v1);
//        }
//        
//        
//        Vastaus k1v2 = new Vastaus(vastaukset.size(), kysymykset.get(0).getId(), "9 tuntia", false);
//        
//        if (!vastaukset.contains(k1v2)) {
//            vastausDao.saveOrUpdate(k1v2);
//            vastaukset.add(k1v2);
//        }
//        
//        Vastaus k1v3 = new Vastaus(vastaukset.size(), kysymykset.get(0).getId(), "15 tuntia", true);
//        
//        if (!vastaukset.contains(k1v3)) {
//            vastausDao.saveOrUpdate(k1v3);
//            vastaukset.add(k1v3);
//        }
//        
//        for (Vastaus vastaus : vastaukset) {
//            System.out.println(vastaus);
//        }
        
//        System.out.println(kysymysDao.findOne(1));
//        System.out.println(vastausDao.findOne(3));
        
//        for (Vastaus vastaus : vastausDao.findByKysymys(1)) {
//            System.out.println(vastaus);
//        }
        
//        vastausDao.delete(1);
//        vastausDao.delete(2);
//        vastausDao.delete(3);
//        kysymysDao.delete(1);
//        
//        for (Vastaus vastaus : vastaukset) {
//            System.out.println(vastaus);
//        }
    }
}
