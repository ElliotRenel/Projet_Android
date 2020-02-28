#pragma version (1)
#pragma rs java_package_name(com.example.editionimage))

static float bound (float val) {
    float m = fmax(0.0f, val);
    return fmin(1.0f, m);
}

float4 RS_KERNEL rgbtohsv(uchar4 in) {

    //Convert input uchar4 to float4
    float4 pixel = rsUnpackColor8888(in);

    //Get min and max color for the pixel
    float r = pixel.r/255;
    float g = pixel.g/255;
    float b = pixel.b/255;

    float Cmax = max(r,max(g,b));
    float Cmin = min(r,min(g,b));
    float delta = Cmax - Cmin;


    float4 hsv;

    //calculating H
    if(delta == 0)
    {
        hsv.x=0;
    }
    if(Cmax == r)
    {
        hsv.x=60*fmod(((g-b)/delta),6);
    }
    if(Cmax == g)
    {
        hsv.x=60*(((b-r)/delta)+2);
    }
    if(Cmax == b)
    {
        hsv.x=60*(((r-g)/delta)+4);
    }

    //calculating S
    if(Cmax == 0)
    {
        hsv.y=0;
    }
    else
    {
        hsv.y=delta/Cmax;
    }

    //calculating S
    hsv.y=Cmax;

    hsv.w=pixel.a;

    return hsv;
}

uchar4 RS_KERNEL hsvtorgb(float4 in) {
    uchar4 rgb;
    float4 temprgb;

    float C = in.y*in.z;
    float X = C * (1 -     fabs(   (fmod((in.x/60),2)-1))      );//j'en suis là
    float m = in.z - C;

    if (0 <= in.x < 60 ) {
        temprgb.r = C;
        temprgb.g = X;
        temprgb.b = 0.0f;
    }

    if (60 <= in.x < 120 ) {
        temprgb.r = X;
        temprgb.g = C;
        temprgb.b = 0.0f;
    }

    if (120 <= in.x < 180 ) {
        temprgb.r = 0.0f;
        temprgb.g = C;
        temprgb.b = X;
    }

    if (180 <= in.x < 240 ) {
        temprgb.r = 0.0f;
        temprgb.g = X;
        temprgb.b = C;
    }

    if (240 <= in.x < 300 ) {
        temprgb.r = X;
        temprgb.g = 0.0f;
        temprgb.b = C;
    }

    if (300 <= in.x < 360 ) {
        temprgb.r = C;
        temprgb.g = 0.0f;
        temprgb.b = X;
    }

    rgb.r =bound((temprgb.r+m))*255;
    rgb.g =bound((temprgb.g+m))*255;
    rgb.b =bound((temprgb.b+m))*255;

    return rgb;
}

