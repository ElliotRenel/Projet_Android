package com.example.editionimage.DefaultPackage.hugo.Commons;

public class inputTreatment {//traite les entrée afin des les trnasformer en données plus simple à manier

    static public MatriceType textInput(String input){
        switch(input){
            case "contour1" :
                return MatriceType.CONTOUR1;
            case "contour2" :
                return MatriceType.CONTOUR2;
            case "contour3" :
                return MatriceType.CONTOUR3;
            case "net" :
                return MatriceType.NET;
            case "gauss3" :
                return MatriceType.GAUSS3X3;
            case "gauss5" :
                return MatriceType.GAUSS5X5;
            case "identite" :
                return MatriceType.IDENTITE;
        }
        return MatriceType.MOYENNE;
    }
}
