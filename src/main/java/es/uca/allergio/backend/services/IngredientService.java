package es.uca.allergio.backend.services;

import es.uca.allergio.backend.entities.Allergy;
import es.uca.allergio.backend.entities.Ingredient;
import es.uca.allergio.backend.entities.IngredientRowData;
import es.uca.allergio.backend.repositories.AllergyRepository;
import es.uca.allergio.backend.repositories.IngredientRepository;
import es.uca.allergio.backend.repositories.IngredientRowDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
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

    @Autowired
    private AllergyRepository allergyRepository;

    private FilteredClassifier classifier;
    private IBk knn;
    private Map<String, Integer> abecedary = new HashMap<>();
    private Instances data;

    public void initialize(){
        createABCMap(abecedary);
        buildClassifier();
    }

    public Set<String> convertIngredients(String OCRIngredients) {
        double classifiedClass = 0;
        Set<String> correctIngredients = new HashSet<>();
        List<Ingredient> allIngredients = ingredientRepository.findAllByOrderByIdAsc();

        OCRIngredients = OCRIngredients.toLowerCase();
        String[] ingredients = OCRIngredients.split(",");

        for (String ingredient : ingredients) {
            Instance instance = createInstanceOfIngredients(ingredient);

            try {
                classifiedClass = classifier.classifyInstance(instance);
            } catch (Exception ex) {
                Logger.getLogger(IngredientService.class.getName()).log(Level.SEVERE, null, ex);
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
            buildClassifier();
            return true;
        }
         return false;
    }

    public Boolean addAllergyToIngredient(String ingredientName, String allergyName) {
        Optional<Ingredient> ingredient = ingredientRepository.findByName(ingredientName);
        Optional<Allergy> allergy = allergyRepository.findByName(allergyName);

        if(ingredient.isPresent() && allergy.isPresent()) {
            Ingredient foundIngredient = ingredient.get();
            Allergy foundAllergy = allergy.get();

            List<Ingredient> relatedIngredients = foundAllergy.getRelatedIngredients();

            if (!relatedIngredients.contains(foundIngredient)) {
                relatedIngredients.add(foundIngredient);
                foundAllergy.setRelatedIngredients(relatedIngredients);
                allergyRepository.save(foundAllergy);
                return true;
            }
        }
        return false;
    }

    public Boolean deleteAllergyFromIngredient(String ingredientName, String allergyName) {
        Optional<Ingredient> ingredient = ingredientRepository.findByName(ingredientName);
        Optional<Allergy> allergy = allergyRepository.findByName(allergyName);

        if(ingredient.isPresent() && allergy.isPresent()) {
            Ingredient foundIngredient = ingredient.get();
            Allergy foundAllergy = allergy.get();

            List<Ingredient> relatedIngredients = foundAllergy.getRelatedIngredients();

            if (relatedIngredients.contains(foundIngredient)) {
                relatedIngredients.remove(foundIngredient);
                foundAllergy.setRelatedIngredients(relatedIngredients);
                allergyRepository.save(foundAllergy);
                return true;
            }
        }
        return false;
    }

    private IngredientRowData createRowData(String originalName, String randomizedIngredient) {

        Integer[] instance = new Integer[27];
        Integer index = 0;

        Set<String> keys;
        keys = abecedary.keySet();
        for (String key : keys) {
            instance[index] = (int)randomizedIngredient.chars().mapToObj(i -> (char)i).filter(letter -> letter == key.charAt(0)).count();
            index++;
        }
        
        return new IngredientRowData(originalName, instance);
    }


    private Map createABCMap(Map<String,Integer> abc) {
        Integer index = 0;
        for (char ch = 'a'; ch <= 'z'; ++ch)
            abc.put(String.valueOf(ch),index++);

        //Añadimos la 'ñ'
        abc.put(String.valueOf('ñ'),index+1);

        return abc;
    }

    private Instance createInstanceOfIngredients(String ingredientName) {
        Instance inst = new DenseInstance(data.numAttributes());
        inst.setDataset(data);

        Set<String> keys;
        keys = abecedary.keySet();
        for (String key : keys) {
            inst.setValue(data.attribute(key), (int)ingredientName.chars().mapToObj(i -> (char)i).filter(letter -> letter == key.charAt(0)).count());
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
        } catch (Exception ex) {
            Logger.getLogger(IngredientService.class.getName()).log(Level.SEVERE, ex.getMessage(),ex);
        }

        data.setClassIndex(data.numAttributes() - 1);
        knn = new IBk();
        knn.setCrossValidate(true);
        classifier = new FilteredClassifier();
        classifier.setClassifier(knn);

        try {
            classifier.buildClassifier(data);
        } catch (Exception ex) {
            Logger.getLogger(IngredientService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Optional<Ingredient> findByName(String ingredientName) {
        return ingredientRepository.findByName(ingredientName);
    }

    public List<Ingredient> findAll() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredientRepository.findAll().forEach(ingredients::add);
        return ingredients;
    }

    public Boolean existsIngredient(String ingredientName) {
        return ingredientRepository.findByName(ingredientName).isPresent();
    }

    public Ingredient getIngredientByName(String ingredientName) {
        Optional<Ingredient> ingredient = ingredientRepository.findByName(ingredientName);

        return ingredient.orElse(null);
    }
}
