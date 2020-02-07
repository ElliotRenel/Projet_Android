package com.example.editionimage.DefaultPackage.hugo.Main;

import com.example.projet_tech.R;

public class ImageBank {
    public int index; //index actuel
    public int[] bank; //tableau des emplacement des images
    public int nbImages = 10; //taille du tableau

    public ImageBank(){
        bank = new int[nbImages];
        index = 0;
        bank[0] = R.drawable.image1;
        bank[1] = R.drawable.image2;
        bank[2] = R.drawable.image3;
        bank[3] = R.drawable.image4;
        bank[4] = R.drawable.image5;
        bank[5] = R.drawable.image6;
        bank[6] = R.drawable.image7;
        bank[7] = R.drawable.image8;
        bank[8] = R.drawable.image9;
        bank[9] = R.drawable.image10;
    }
}
