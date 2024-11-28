package com.sippy.wrapper.parent.database;

import com.sippy.wrapper.parent.database.dao.TnbDao;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class DatabaseConnection {

  @PersistenceContext(unitName = "CustomDB")
  private EntityManager entityManager;

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnection.class);

  public int countTheEntries() {
    return ((Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM tnbs").getSingleResult())
        .intValue();
  }

  @SuppressWarnings("unchecked")
  public List<TnbDao> getAllTnbs() {
    Query query = entityManager.createNativeQuery("SELECT * FROM tnbs", TnbDao.class);
    return query.getResultList();
  }

  public void createTnb(String tnb, String name) {
    Query query =
        entityManager.createNativeQuery("INSERT INTO tnbs (tnb, name) " + tnb + " " + name);
    query.executeUpdate();
  }

  public Optional<TnbDao> getTnb(String tnb) {
    Query query =
        entityManager.createNativeQuery(
            "SELECT * FROM tnbs WHERE tnb = '" + tnb + "'", TnbDao.class);
    List<TnbDao> res = query.getResultList();
    if (res.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(res.get(0));
  }
}
