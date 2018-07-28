package es.uca.allergio.backend.services;

import es.uca.allergio.backend.entities.Ingredient;
import es.uca.allergio.backend.entities.IngredientRowData;
import es.uca.allergio.backend.repositories.IngredientRepository;
import es.uca.allergio.backend.repositories.IngredientRowDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.experiment.InstanceQuery;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private IngredientRowDataRepository ingredientRowDataRepository;

    private Classifier knn;
    private Map<String, Integer> abecedary = new HashMap<>();
    private Instances data;

    public void initialize(){
        createABCMap(abecedary);
        buildClassifier();
    }

    public Set<String> convertIngredients(String OCRingredients) throws Exception{
        double classifiedClass = 0;
        Set<String> correctIngredients = new HashSet<>();
        List<Ingredient> allIngredients = ingredientRepository.findAllByOrderByIdAsc();

        OCRingredients.toLowerCase();
        String[] ingredients = OCRingredients.split(",");

        for (String ingredient : ingredients) {
            Instance inst = createInstanceOfIngredients(ingredient);

            try {
                classifiedClass = knn.classifyInstance(inst);
            } catch (Exception ex) {
                Logger.getLogger(IngredientService.class.getName()).log(Level.SEVERE, null, ex);
                throw ex;
            }

            Ingredient resultIngredient = (Ingredient) allIngredients.toArray()[(int)classifiedClass];
            correctIngredients.add(resultIngredient.getName());
        }

        return correctIngredients;
    }

    public Boolean addIngredient(Ingredient ingredient) {

        if(!existsIngredient(ingredient.getName())) {

            List<IngredientRowData> ingredientRowsData = new ArrayList<>();
            String[] letters = ingredient.getName().split("");
            String[] abcKeys = abecedary.keySet().toArray(new String[0]);

            for (int i = 0; i < letters.length; i++) {
                for (int j = 0; j < abcKeys.length; j++) {
                    String suchIngredient = String.join("", String.join("", Arrays.copyOfRange(letters, 0, i)),
                            abcKeys[j], String.join("", Arrays.copyOfRange(letters, i + 1, letters.length)));
                    IngredientRowData ingredientRowData = createRowData(ingredient.getName(), suchIngredient);
                    ingredientRowData.originalIngredient = ingredient;
                    ingredientRowsData.add(ingredientRowData);
                    ingredientRowDataRepository.save(ingredientRowData);
                }
            }

            ingredient.setIngredientRowsData(ingredientRowsData);
            ingredientRepository.save(ingredient);
            buildClassifier();

            return true;
        }
        return false;
    }

    public Boolean deleteIngredient(String ingredientName) {
        Optional<Ingredient> ingredient = findByName(ingredientName);

        if(ingredient.isPresent()) {
            Ingredient foundIngredient = ingredient.get();

            ingredientRepository.delete(foundIngredient);
            return true;
        }
         return false;
    }

    private IngredientRowData createRowData(String originalName, String randomizedIngredient) {

        Integer[] instance = new Integer[27];
        Integer index = 0;

        Set<String> keys;
        keys = abecedary.keySet();
        for (String key : keys) {
            if(randomizedIngredient.contains(key))
                instance[index] = 1;
            else
                instance[index] = 0;

            index++;
        }

        return new IngredientRowData(originalName,instance);
    }


    private Map createABCMap(Map<String,Integer> abc) {
        Integer index = 0;
        for (char ch = 'a'; ch <= 'z'; ++ch)
            abc.put(String.valueOf(ch),index++);

        //Añadimos la 'ñ'
        abc.put(String.valueOf('ñ'),index++);

        return abc;
    }

    private Instance createInstanceOfIngredients(String ingredientName) {
        Instance inst = new DenseInstance(data.numAttributes());
        inst.setDataset(data);

        Set<String> keys;
        keys = abecedary.keySet();
        for (String key : keys) {
            if(ingredientName.contains(key))
                inst.setValue(data.attribute(key), 1.0);
            else
                inst.setValue(data.attribute(key), 0.0);
        }
        return inst;
    }

    private void buildClassifier() {

        InstanceQuery query;
        File customProps = new File("src/main/resources/DatabaseUtils.props");

        try {
            query = new InstanceQuery();
            query.setCustomPropsFile(customProps);
            query.setQuery("SELECT a,b,c,d,e,f,g,h,i,j,k,l,m,n,ñ,o,p,q,r,s,t,u,v,w,x,y,z,ingredient FROM ingredient_row_data");
            data = query.retrieveInstances();
        } catch (Exception e) {
            e.printStackTrace();
        }

        data.setClassIndex(data.numAttributes() - 1);
        knn = new IBk(27);

        try {
            knn.buildClassifier(data);
        } catch (Exception ex) {
            Logger.getLogger(IngredientService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public Optional<Ingredient> findById(Integer pk) {
        return ingredientRepository.findById(pk);
    }

    public Optional<Ingredient> findByName(String ingredientName) {
        return ingredientRepository.findByName(ingredientName);
    }

    public Boolean existsIngredient(String ingredientName) {
        return ingredientRepository.findByName(ingredientName).isPresent();
    }
}
