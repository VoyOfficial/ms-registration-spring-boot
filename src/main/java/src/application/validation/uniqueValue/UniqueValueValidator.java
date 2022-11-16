package src.application.validation.uniqueValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class UniqueValueValidator implements ConstraintValidator<UniqueValue, String> {

    private Class<?> modelClass;
    private String field;

    @Autowired
    private EntityManager entityManager;

    @Override
    public void initialize(UniqueValue uniqueValueAnnotation) {

        ConstraintValidator.super.initialize(uniqueValueAnnotation);

        modelClass = uniqueValueAnnotation.modelClass();
        field = uniqueValueAnnotation.field();

    }

    @Override
    public boolean isValid(String fieldNameNoted, ConstraintValidatorContext constraintValidatorContext) {

        Query query = entityManager.createQuery("select 1 from " + modelClass.getName() + " where " + field + " = :value");
        query.setParameter("value", fieldNameNoted);

        List<?> results = query.getResultList();

        Assert.state(results.size() <= 1, "Finded more 1 results of " + modelClass.getName() + " with attribute " + field);

        return results.isEmpty();

    }

}
