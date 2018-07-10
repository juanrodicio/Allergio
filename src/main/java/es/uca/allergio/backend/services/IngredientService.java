package es.uca.allergio.backend.services;

import es.uca.allergio.backend.entities.Ingredient;
import es.uca.allergio.backend.repositories.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository repo;

    private Classifier knn;
    private Map<String, Integer> abecedary = new HashMap<>();
    private Instances data;

    @PostConstruct
    public void initialize(){
        createABCMap(abecedary);
        knn = buildClassifier();
    }

    private Map createABCMap(Map<String,Integer> abc) {
        Integer index = 0;
        for (char ch = 'a'; ch <= 'z'; ++ch)
            abc.put(String.valueOf(ch),index++);

        return abc;
    }

    public List<String> convertIngredients(String OCRingredients) {
        double classifiedClass = 0;
        List<String> correctIngredients = new ArrayList<>();
        List<Ingredient> allIngredients = repo.findAllByOrderByIdAsc();

        OCRingredients.toLowerCase();
        String[] ingredients = OCRingredients.split(",");

        for (String ingredient : ingredients) {
            Instance inst = createInstanceOfIngredients(ingredient);

            try {
                classifiedClass = knn.classifyInstance(inst);
            } catch (Exception ex) {
                Logger.getLogger(IngredientService.class.getName()).log(Level.SEVERE, null, ex);
            }

            Ingredient resultIngredient = (Ingredient) allIngredients.toArray()[(int)classifiedClass];
            correctIngredients.add(resultIngredient.getName());
        }

        return correctIngredients;
    }

    private Instance createInstanceOfIngredients(String ingredient) {
        Instance inst = new DenseInstance(data.numAttributes());
        inst.setDataset(data);

        Set<String> keys;
        keys = abecedary.keySet();
        for (String key : keys) {
            if(ingredient.contains(key))
                inst.setValue(data.attribute(key), 1.0);
            else
                inst.setValue(data.attribute(key), 0.0);
        }
        return inst;
    }

    private Classifier buildClassifier() {
        data = readDataFile("src/main/resources/ingredients.csv");
        data.setClassIndex(data.numAttributes() - 1);
        knn = new IBk(27);

        try {
            knn.buildClassifier(data);
        } catch (Exception ex) {
            Logger.getLogger(IngredientService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return knn;
    }

    private Instances readDataFile(String pathToFilename) {
        Instances inputReader = null;

         try {
             inputReader = ConverterUtils.DataSource.read(pathToFilename);
         } catch (Exception ex) {
             Logger.getLogger(IngredientService.class.getName()).log(Level.SEVERE, null, ex);
             System.err.println("File not found: " + pathToFilename);
         }

         return inputReader;
    }

    public Optional<Ingredient> findById(Integer pk) {
        return repo.findById(pk);
    }
}
