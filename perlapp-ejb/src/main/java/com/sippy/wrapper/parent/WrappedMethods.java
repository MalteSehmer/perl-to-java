package com.sippy.wrapper.parent;

import com.sippy.wrapper.parent.database.DatabaseConnection;
import com.sippy.wrapper.parent.database.dao.TnbDao;
import com.sippy.wrapper.parent.request.JavaTestRequest;
import com.sippy.wrapper.parent.request.JavaTestRequest2;
import com.sippy.wrapper.parent.response.JavaTestResponse;
import java.util.*;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class WrappedMethods {

  private static final Logger LOGGER = LoggerFactory.getLogger(WrappedMethods.class);

  @EJB DatabaseConnection databaseConnection;

  @RpcMethod(name = "javaTest", description = "Check if everything works :)")
  public Map<String, Object> javaTest(JavaTestRequest request) {
    JavaTestResponse response = new JavaTestResponse();

    int count = databaseConnection.getAllTnbs().size();

    LOGGER.info("the count is: " + count);

    response.setId(request.getId());
    String tempFeeling = request.isTemperatureOver20Degree() ? "warm" : "cold";
    response.setOutput(
        String.format(
            "%s has a rather %s day. And he has %d tnbs", request.getName(), tempFeeling, count));

    Map<String, Object> jsonResponse = new HashMap<>();
    jsonResponse.put("faultCode", "200");

    return jsonResponse;
  }

  @RpcMethod(name = "getTnbList", description = "my getTnbList function")
  public Map<String, Object> getTnbList(JavaTestRequest2 req) {
    LOGGER.info("CALL getTnbList, Malte" + req.getNumber());

    var tnbs_from_db = databaseConnection.getAllTnbs();
    Optional<TnbDao> tnb = Optional.empty();

    if (req.getNumber() != null) {
      LOGGER.info("getting number form db");
      var db_data =
          tnbs_from_db.stream()
              // .filter(t -> t.getTnb() == req.getNumber())
              .collect(Collectors.toList());
      if (!db_data.isEmpty()) {
        tnb = Optional.of(db_data.getFirst());

        LOGGER.info("Got TNB for id " + req.getNumber() + " val: " + tnb.get().getName());
      }
    }

    var ignoreIds = List.of("D146", "D218", "D248");

    List<TnbDao> my_tnbs = List.of();
    databaseConnection.createTnb("D001", "Deutsche Telekom");
    var d = databaseConnection.getTnb("D001");
    if (d.isPresent()) my_tnbs.add(d.get());

    for (TnbDao tnbDao : tnbs_from_db) {
      if (ignoreIds.contains(tnbDao.getTnb())) {
        LOGGER.info("Skipping: " + tnbDao.getTnb());
      }

      databaseConnection.createTnb(tnbDao.getTnb(), tnbDao.getName());
      d = databaseConnection.getTnb(tnbDao.getTnb());
      if (d.isPresent()) my_tnbs.add(d.get());
    }

    Map<String, Object> jsonResponse = new HashMap<>();
    jsonResponse.put("faultCode", "200");
    jsonResponse.put("faultString", "Method success");
    jsonResponse.put("tnbs", tnbs_from_db.toString());
    return jsonResponse;
  }
}
