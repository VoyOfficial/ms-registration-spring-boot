package src.application.validation.uniqueValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Component
public class EntityManagerAdapter {
    @Autowired
    private EntityManager entityManager;

    public List selectUniqueValue(String modelClassName, String field, String fieldNameNoted) {

        Query query = entityManager.createQuery("select 1 from " + modelClassName + " where " + field + " = :value");
        query.setParameter("value", fieldNameNoted);

        List results = query.getResultList();

//        Assert.state(results.size() <= 1, "Finded more 1 results of " + modelClassName+ " with attribute " + field);

        return results;

    }


}
