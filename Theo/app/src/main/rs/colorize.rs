#pragma version(1)
#pragma rs java_package_name ( com.example.effectsapp)

uchar4 RS_KERNEL colorize ( uchar4 in ) {

    float r = in.r / 255.0;
    float g = in.g / 255.0;
    float b = in.b / 255.0;

    float minRGB = min( r, min( g, b ) );
    float maxRGB = max( r, max( g, b ) );
    float deltaRGB = maxRGB - minRGB;

    float h = rsRand(255);
    float s = maxRGB == 0 ? 0 : (maxRGB - minRGB) / maxRGB;
    float v = maxRGB;




}