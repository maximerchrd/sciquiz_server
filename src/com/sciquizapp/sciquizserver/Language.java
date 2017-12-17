package com.sciquizapp.sciquizserver;

/**
 * Created by maximerichard on 17.12.17.
 * Multi Language Strings Class
 */
public class Language {
    static private String LANGUAGE = "english";


    static public String ADDNEWQUESTION = "addnewquestion";
    static public String ADDSUBJECTBUTTON = "addnewsubject";
    static public String ADDOBJECTIVEBUTTON = "addnewobjective";
    static private String[] NOLANGUAGE = {
            ADDNEWQUESTION,
            ADDSUBJECTBUTTON,
            ADDOBJECTIVEBUTTON
    };

    static private String ADDNEWQUESTION_ENGLISH = "Add a new question";
    static private String ADDSUBJECTBUTTON_ENGLISH = "Add a new subject";
    static private String ADDOBJECTIVEBUTTON_ENGLISH = "Add a new learning objective";
    static private String[] ENGLISH = {
            ADDNEWQUESTION_ENGLISH,
            ADDSUBJECTBUTTON_ENGLISH,
            ADDOBJECTIVEBUTTON_ENGLISH
    };

    static private String ADDNEWQUESTION_FRENCH = "Ajouter une nouvelle question";
    static private String ADDSUBJECTBUTTON_FRENCH = "Ajouter un nouveau sujet";
    static private String ADDOBJECTIVEBUTTON_FRENCH = "Ajouter un nouvel objectif d'apprentissage";
    static private String[] FRENCH = {
            ADDNEWQUESTION_FRENCH,
            ADDSUBJECTBUTTON_FRENCH,
            ADDOBJECTIVEBUTTON_FRENCH
    };

    /**
     *  Returns String in current language
     * @param stringToTranslate
     * @return string in current language
     */
    static public String translate(String stringToTranslate) {
        Boolean found = false;
        int i = 0;
        for (; i < NOLANGUAGE.length && !found; i++) {
            if(NOLANGUAGE[i].contains(stringToTranslate)) {
                found = true;
            }
        }
        i--;
        if (LANGUAGE.contains("english")) {
            if (i < ENGLISH.length) {
                return ENGLISH[i];
            } else {
                return "text not yet translated";
            }
        } else if (LANGUAGE.contains("french")) {
            if (i < FRENCH.length) {
                return FRENCH[i];
            } else {
                return "texte pas encore traduit";
            }
        }
        return "text not found";
    }
}
